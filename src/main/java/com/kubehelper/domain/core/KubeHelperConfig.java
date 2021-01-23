/*
Kube Helper
Copyright (C) 2021 JDev

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package com.kubehelper.domain.core;

import com.kubehelper.common.Global;
import com.kubehelper.domain.models.PageModel;
import com.kubehelper.domain.results.CronJobResult;
import com.moandjiezana.toml.Toml;
import com.moandjiezana.toml.TomlWriter;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * The class for store/parse/validate config.
 *
 * @author JDev
 */
public class KubeHelperConfig {
    private Git git;
    private List<CronJob> cron_job = new ArrayList<>();

    public KubeHelperConfig(String configString, PageModel model) {
        Toml configToml = new Toml().read(configString);
        List<String> notUniqueJobsCheck = new ArrayList<>();

        //get git config
        getGitConfig(configToml);

        //get unique cron jobs, non unique jobs were ignored
        try {
            for (Toml job : configToml.getTables("cron_job")) {
                CronJob newJob = job.to(CronJob.class);

                //Validate Cron job
                if (isCronJobNotValid(newJob, model)) {
                    continue;
                }

                if (cron_job.contains(newJob)) {
                    notUniqueJobsCheck.add(newJob.name);
                    continue;
                }
                cron_job.add(job.to(CronJob.class));
            }
        } catch (RuntimeException ex) {
            throw new RuntimeException("reading the cron jobs configuration: " + ex.getMessage());
        }

        if (!notUniqueJobsCheck.isEmpty()) {
            model.addException(String.format("Some cron jobs has the same name, duplicates were ignored and removed from the config. Duplicated cron jobs: %s", notUniqueJobsCheck.toString()),
                    new RuntimeException("Non Unique Cron jobs detected."));
        }

        //convert parsed config Object to visualize in config panel.
        convertParsedConfigToString(model);
    }

    private void convertParsedConfigToString(PageModel model) {
        Writer writer = new StringWriter();
        try {
            TomlWriter tomlWriter = new TomlWriter.Builder().indentValuesBy(2).indentTablesBy(4).build();
            tomlWriter.write(this, writer);
            Global.configString = writer.toString();
        } catch (IOException e) {
            model.addException(" parsing to the configurations object to configuration string. Error: " + e.getMessage(), e);
            throw new RuntimeException(" parsing to the configurations object to configuration string: " + e.getMessage());
        }
    }

    private void getGitConfig(Toml configToml) {
        try {
            git = configToml.getTable("git").to(Git.class);
        } catch (RuntimeException ex) {
            throw new RuntimeException("reading the git configuration: " + ex.getMessage());
        }
    }

    private boolean isCronJobNotValid(CronJob newJob, PageModel model) {
        if (StringUtils.isAnyBlank(newJob.name, newJob.expression, newJob.command)) {
            model.addException("Cron job %s is not valid. Name, expression and command are required fields.", new RuntimeException("Cron Job validation Exception"));
            return true;
        }

        if (!Pattern.matches("^[a-zA-Z0-9_-]*$", newJob.name)) {
            model.addException(String.format("Cron job %s is not valid. The job name should contain only: letters, numbers, '-' and '_'", newJob.name), new RuntimeException("Cron Job validation Exception"));
            return true;
        }

        return false;
    }

    public Git getGit() {
        return git;
    }

    /**
     * Adds only new Cron job to the list.
     *
     * @param newJob - new cron job.
     */
    public void addNewCronJob(CronJobResult newJob) {
        CronJob job = new CronJob();
        job.name = newJob.getName();
        job.expression = newJob.getExpression();
        job.command = newJob.getCommand();
        job.shell = newJob.getShell();
        job.description = newJob.getDescription();
        job.isActive = newJob.isActive();
        if (!cron_job.contains(job)) {
            cron_job.add(job);
        }
    }

    /**
     * Gets and parse config jobs pojos to @{@link CronJobResult} list.
     *
     * @param cronJobsReportsPath - root reports path.
     * @return - list with converted jobs.
     */
    public List<CronJobResult> getCronJobsResults(String cronJobsReportsPath) {
        return cron_job.stream().map(job -> new CronJobResult()
                .setName(job.name)
                .setExpression(job.expression)
                .setCommand(job.command)
                .setShell(job.shell)
                .setDescription(job.description)
                .setActive(job.isActive)
                .buildReportsFolderPath(cronJobsReportsPath))
                .collect(Collectors.toList());
    }

    public void markCronjobAsInactive(String name) {
        cron_job.stream().filter(job -> job.name.equals(name)).findFirst().ifPresent(cronJob -> cronJob.isActive = false);
    }

    public void markCronjobAsActive(String name) {
        cron_job.stream().filter(job -> job.name.equals(name)).findFirst().ifPresent(cronJob -> cronJob.isActive = true);
    }

    /**
     * Deletes cron job. Object ist sync with Global.CRON_JOBS object.
     *
     * @param name - job name.
     */
    public void removeCronjob(String name) {
        Iterator<CronJob> iterator = cron_job.iterator();
        while (iterator.hasNext()) {
            CronJob next = iterator.next();
            if (next.name.equals(name)) {
                cron_job.remove(next);
                break;
            }
        }
    }

    private class CronJob {
        private String name = "";
        private boolean isActive = true;
        private String expression = "";
        private String command = "";
        private String shell = "";
        private String description = "";

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CronJob cronJob = (CronJob) o;
            return Objects.equals(name, cronJob.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name);
        }
    }

    public class Git {
        private String url = "";
        private String branch = "";
        private String user = "";
        private String password = "";
        private String email = "";

        public String getUrl() {
            return url;
        }

        public String getBranch() {
            return branch;
        }

        public String getUser() {
            return user;
        }

        public String getPassword() {
            return password;
        }

        public String getEmail() {
            return email;
        }
    }
}
