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
package com.kubehelper.services;

import com.google.common.io.Files;
import com.kubehelper.common.Global;
import com.kubehelper.domain.models.CronJobsModel;
import com.kubehelper.domain.results.CommandsResult;
import com.kubehelper.domain.results.CronJobResult;
import com.kubehelper.domain.results.FileSourceResult;
import com.moandjiezana.toml.Toml;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author JDev
 */
@Service
public class CronJobsService {

    private static Logger logger = LoggerFactory.getLogger(CronJobsService.class);

    @Value("${kubehelper.predefined.commanmds.path}")
    private String predefinedCommandsPath;

    @Value("${kubehelper.user.commands.location.search.path}")
    private String userCommandsLocationSearchPath;

    @Value("${kubehelper.cron.jobs.reports.path}")
    private String cronJobsReportsPath;

    @Autowired
    private CommonService commonService;

    @Autowired
    private SchedulerService schedulerService;


    //  CRON JOBS ================


    /**
     * Creates cron job and checks if folder with cron job name exists.
     *
     * @param model - @{@link CronJobsModel}
     * @param job   - @{@link CronJobResult}
     */
    public void startCronJob(CronJobsModel model, CronJobResult job) {
        job.buildReportsFolderPath(cronJobsReportsPath);

        File reportsFolder = new File(job.getReportsFolderPath());
        if (reportsFolder.exists() && reportsFolder.isDirectory() || Global.CRON_JOBS.containsKey(job.getName())) {
            model.addException(new RuntimeException("Cron job with this name already exists or existed. Please choose another name."));
            return;
        }
        schedulerService.startCronJob(job, model);
    }

    /**
     * Runs stopped job again.
     *
     * @param job - {@link CronJobResult}.
     */
    public void rerunCronJob(CronJobResult job, CronJobsModel model) {
        schedulerService.rerunCronJob(job);
        Global.config.markCronjobAsActive(job.getName());
        commonService.updateConfigFile(model);
    }

    public void stopCronJob(CronJobResult job, CronJobsModel model) {
        Global.config.markCronjobAsInactive(job.getName());
        commonService.updateConfigFile(model);
    }

    public void removeCronJob(CronJobResult job, CronJobsModel model) {
        Global.config.removeCronjob(job.getName());
        commonService.updateConfigFile(model);
    }

    /**
     * returns list with active(running and stopped) cron jobs.
     *
     * @return - list with @{@link CronJobResult}.
     */
    public List<CronJobResult> getActiveCronJobs() {
        List<CronJobResult> activeCronJobs = new ArrayList<>();
        Global.CRON_JOBS.forEach((jobName, scheduler) -> {
            CronJobResult jobResult = new CronJobResult(scheduler.getId())
                    .setName(jobName)
                    .setCommand(scheduler.getCommand())
                    .setExpression(scheduler.getExpression())
                    .setDescription(scheduler.getDescription())
                    .setShell(scheduler.getShell())
                    .setRuns(scheduler.getRuns())
                    .setDone(scheduler.isDone())
                    .buildReportsFolderPath(cronJobsReportsPath);
            activeCronJobs.add(jobResult);
        });
        return activeCronJobs;
    }

    /**
     * Reads toml commands resources from init/commands and parse commands.
     *
     * @param model - cron jobs model @{@link CronJobsModel}.
     */
    public void parsePredefinedCommands(CronJobsModel model) {
        HashMap<String, Toml> commands = new HashMap<>();
        try {
            org.springframework.core.io.Resource[] resources = commonService.getFilesPathsFromClasspathByDirAndExtension(predefinedCommandsPath, ".toml");
            for (org.springframework.core.io.Resource resource : resources) {
                commands.put(Files.getNameWithoutExtension(resource.getFilename()), new Toml().read(resource.getInputStream()));
            }
        } catch (IOException e) {
            model.addException("Error at parse predefined commands. Error: " + e.getMessage(), e);
            logger.error(e.getMessage(), e);
        }
        parseCommands(model, commands);
    }

    /**
     * Reads toml commands resources from git folder and parse commands.
     *
     * @param model- cron jobs model @{@link CronJobsModel}.
     */
    public void parseUserCommands(CronJobsModel model) {
        HashMap<String, Toml> commands = new HashMap<>();
        try {
            Set<String> userCommandFiles = commonService.getFilesPathsByDirAndExtension(userCommandsLocationSearchPath, 10, ".toml");
            for (String filePath : userCommandFiles) {
                commands.put(Files.getNameWithoutExtension(filePath), new Toml().read(commonService.getResourceAsStringByPath(filePath)));
            }
        } catch (IOException e) {
            model.addException("Error at parse user commands. Error: " + e.getMessage(), e);
            logger.error(e.getMessage(), e);
        }
        parseCommands(model, commands);
    }


    /**
     * Parse predefined commands from commands map.
     *
     * @param model - cron jobs model @{@link CronJobsModel}.
     */
    public void parseCommands(CronJobsModel model, HashMap<String, Toml> commands) {
        for (Map.Entry<String, Toml> commandsMap : commands.entrySet()) {
            for (Map.Entry<String, Object> commandEntry : commandsMap.getValue().entrySet()) {
                CommandsResult cr = new CommandsResult(model.getCommandsResults().size() + 1);
                try {
                    Toml command = (Toml) commandEntry.getValue();
                    cr.setFile(commandsMap.getKey())
                            .setName(commandEntry.getKey())
                            .setGroup(command.getString("group"))
                            .setDescription(command.getString("description"))
                            .setCommand(command.getString("command"));
                    model.addCommandResult(cr);
                } catch (RuntimeException e) {
                    model.addException(new RuntimeException("Command parse Error. Name, Group, Description and Command itself are mandatory fields. Object: " + cr.toString()));
                    logger.error("Command parse Error. Group, Operation Description and command itself are mandatory fields. Object: " + cr.toString());
                }
            }
        }
    }


    //  CRON JOBS REPORTS ================


    /**
     * Prepares cron jobs reports view. Finds all cron jobs reports groups files and set active newest.
     *
     * @param model - cron jobs model @{@link CronJobsModel}.
     */
    public void prepareCronJobsReports(CronJobsModel model) {
        try {
            List<File> reportsGroups = Arrays.stream(new File(cronJobsReportsPath).listFiles()).filter(File::isDirectory).sorted(Comparator.comparing(File::getName)).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(reportsGroups)) {
                //set cron jobs reports groups
                model.setCronJobsReportsGroups(reportsGroups.stream().map(File::getName).collect(Collectors.toList()));

                //set selected folder
                String selectedReportsFolder = reportsGroups.get(0).getName();
                model.setSelectedReportsFolder(selectedReportsFolder);

                //get all reports feom group
                Set<String> filesPathsByDirAndExtension = commonService.getFilesPathsByDirAndExtension(reportsGroups.get(0).getAbsolutePath(), 2, ".txt");
                addNewReportsFromSelectedFolder(model, filesPathsByDirAndExtension);
            }
        } catch (IOException e) {
            model.addNotificationException("Cannot Prepare Reports: Error: " + e.getMessage());
            logger.debug(e.getMessage(), e);
        }
    }

    /**
     * Change Reports Folder. Finds all cron job files from selected folder and set active newest.
     *
     * @param model - cron jobs model @{@link CronJobsModel}.
     */
    public void changeReportsFolder(CronJobsModel model) {
        try {
            Set<String> filesPathsByDirAndExtension = commonService.getFilesPathsByDirAndExtension(cronJobsReportsPath + model.getSelectedReportsFolder(), 2, ".txt");
            addNewReportsFromSelectedFolder(model, filesPathsByDirAndExtension);
        } catch (IOException e) {
            model.addNotificationException("Cannot Prepare Reports: Error: " + e.getMessage());
            logger.debug(e.getMessage(), e);
        }
    }

    /**
     * Adds to model new reports from selected group.
     *
     * @param model                       - cron jobs model @{@link CronJobsModel}.
     * @param filesPathsByDirAndExtension - a set with files from selected group.
     */
    private void addNewReportsFromSelectedFolder(CronJobsModel model, Set<String> filesPathsByDirAndExtension) {

        //clear and get meta from files
        model.setCronJobsReports(new HashMap<>());
        filesPathsByDirAndExtension.forEach(file -> model.addReportSource(Files.getNameWithoutExtension(file), file));
        model.sortCronJobsReportsAlphabeticallyAsc();

        //set first active
        Optional<Map.Entry<String, FileSourceResult>> first = model.getCronJobsReports().entrySet().stream().findFirst();
        if (first.isPresent()) {
            model.setSelectedReportRaw(commonService.getResourceAsStringByPath(first.get().getValue().getFilePath()));
            model.setSelectedReportLabel(first.get().getKey());
        }
    }

    /**
     * Change report raw at click on report button.
     *
     * @param model - @{@link CronJobsModel}
     */
    public void changeReportRaw(CronJobsModel model) {
        model.setSelectedReportRaw(commonService.getResourceAsStringByPath(model.getCronJobsReports().get(model.getSelectedReportLabel()).getFilePath()));
    }
}
