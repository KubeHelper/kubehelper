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

import com.kubehelper.common.Global;
import com.kubehelper.domain.core.KubeHelperScheduledFuture;
import com.kubehelper.domain.results.CronJobResult;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.SequenceInputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

/**
 * The scheduler service.
 *
 * @author JDev
 */
@Service
public class SchedulerService {

    private static Logger logger = LoggerFactory.getLogger(SchedulerService.class);

    @Autowired
    private ThreadPoolTaskScheduler threadPoolTaskScheduler;

    @Autowired
    private CommonService commonService;

    @Autowired
    private SenderService senderService;


    @Value("${kubehelper.report.template.src.path}")
    private String reportTemplateSrcPath;

    private String reportTemplate = "";

    //
    @PostConstruct
    private void postConstruct() {
        reportTemplate = commonService.getClasspathResourceAsStringByPath(reportTemplateSrcPath);
//        TODO check config from config file
//        reportEntryTemplate = commonService.getClasspathResourceAsStringByPath(historyEntryTemplateSrcPath);

    }

    /**
     * Starts cron job and adds to global cron jobs map.
     *
     * @param job - @{@link CronJobResult} object.
     */
    public void startCronJob(CronJobResult job) {
        ScheduledFuture<?> schedule = threadPoolTaskScheduler.schedule(getRunnableTask(job), new CronTrigger(job.getExpression()));
        Global.CRON_JOBS.put(job.getName(), new KubeHelperScheduledFuture(job, schedule));
//        TODO write to config toml
        //TODO add to toml boolean config isStart by default
    }

    /**
     * Creates runnable Task from @{@link CronJobResult}
     *
     * @param job - @{@link CronJobResult} object.
     * @return - runnable.
     */
    private Runnable getRunnableTask(CronJobResult job) {
        return () -> {
            ProcessBuilder processBuilder = new ProcessBuilder();
            String cronResultOutput = "", exception = "", reportContent = "";

            //Start process
            Process process;
            processBuilder.command(job.getShell(), "-c", job.getCommand());
            try {
                process = processBuilder.start();

                //read process output to string
                try (BufferedReader output = new BufferedReader(new InputStreamReader(new SequenceInputStream(process.getInputStream(), process.getErrorStream())))) {
                    cronResultOutput = output.lines().map(line -> line = line + "\n").reduce("", String::concat);
                }
            } catch (IOException e) {
                if (logger.isDebugEnabled()) {
                    exception = String.format("executeCommand: Command=%s", job.getCommand());
                    logger.error(exception + e.getMessage(), e);
                }
            }

            //check exception
            cronResultOutput = StringUtils.isNotBlank(exception) ? exception : cronResultOutput;

            //build report
            reportContent = buildReport(job.getName(), job.getCommand(), cronResultOutput);

            //create folder for report if they does not exists
            new File(job.getReportsFolderPath()).mkdirs();

            //combine path to report file
            String today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
            String reportFilePath = job.getReportsFolderPath() + File.separator + today + ".txt";
            try {
//                create/open report file
                File file = new File(reportFilePath);
                file.createNewFile();

                //reads report content and append to the start new content
                String fileContent = commonService.getResourceAsStringByPath(file.getPath());
                fileContent = reportContent + fileContent;
                FileUtils.writeStringToFile(file, fileContent, StandardCharsets.UTF_8.toString());
            } catch (IOException e) {
                if (logger.isDebugEnabled()) {
                    logger.error(e.getMessage(), e);
                }
            }

            //send email with report
            if (StringUtils.isNotBlank(job.getEmail())) {
                senderService.sendEmail(job.getEmail(), reportContent);
            }

            //add cron run count
            Global.CRON_JOBS.get(job.getName()).addRun();
        };
    }

    /**
     * Builds cron report. Report from template and parameters.
     *
     * @param name - cron job name.
     * @param command - cron job command.
     * @param output - report output.
     * @return - build string report for this run.
     */
    private String buildReport(String name, String command, String output) {
        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss yyyy-MM-dd"));
        Map<String, String> replaceMap = Map.of("name", name, "time", time, "command", command, "output", output);
        return new StringSubstitutor(replaceMap).replace(reportTemplate);
    }
}
