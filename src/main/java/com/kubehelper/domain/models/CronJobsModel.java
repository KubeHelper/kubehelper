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
package com.kubehelper.domain.models;

import com.google.common.collect.ImmutableSortedMap;
import com.kubehelper.common.Global;
import com.kubehelper.common.KubeHelperException;
import com.kubehelper.domain.filters.CommandsFilter;
import com.kubehelper.domain.results.CommandsResult;
import com.kubehelper.domain.results.CronJobResult;
import com.kubehelper.domain.results.FileSourceResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Cron Jobs Model.
 *
 * @author JDev
 */
public class CronJobsModel implements PageModel {

    private String templateUrl = "~./zul/kubehelper/pages/cron.zul";
    public static String NAME = Global.CRON_JOBS_MODEL;

    private List<KubeHelperException> exceptions = new ArrayList<>();
    private String runtimeNotificationExceptions = "";

    //  COMMANDS AND CRON JOBS ================
    private List<CommandsResult> commandsResults = new ArrayList<>();
    private List<CronJobResult> cronJobsResults = new ArrayList<>();
    private CommandsFilter filter = new CommandsFilter();

    private String selectedShell = "bash";
    private List<String> shells = Arrays.asList("bash", "sh", "fish", "zsh", "ksh");

    private String commandToExecute = "";
    private String commandToExecuteEditable = "";
    private String executedCommandOutput = "";

    //  REPORTS ================
    private Map<String, FileSourceResult> cronJobsReports = new HashMap<>();

    private String selectedReportLabel = "";
    private String selectedReportRaw = "";
    private String commandsRawReportBuffer = "";
    private String selectedReportsFolder = "";

    @Override
    public String getTemplateUrl() {
        return templateUrl;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void addException(String message, Exception exception) {
        this.exceptions.add(new KubeHelperException(message, exception));
    }

    public void addException(Exception exception) {
        this.exceptions.add(new KubeHelperException(exception));
    }

    public void addCommandResult(CommandsResult commandResult) {
        commandsResults.add(commandResult);
        filter.addGroupFilter(commandResult.getGroup());
        filter.addOperationFilter(commandResult.getFile());
    }

    public void addReportSource(String label, String filePath) {
        FileSourceResult reportSource = new FileSourceResult();
        reportSource.setLabel(label);
        reportSource.setFilePath(filePath);
        cronJobsReports.put(label, reportSource);
    }

    public void sortCronJobsReportsAlphabeticallyAsc() {
        cronJobsReports = ImmutableSortedMap.copyOf(cronJobsReports, Comparator.comparing(file -> file));
    }

//    public List<FileSourceResult> getCronJobsReportsObjectsForJob() {
//        return cronJobsReports.values().stream().collect(Collectors.toList());
//    }

    public List<String> getCronJobsReportsForJob() {
        return cronJobsReports.keySet().stream().sorted().collect(Collectors.toList());
    }

    public boolean hasErrors() {
        return !exceptions.isEmpty();
    }

    public void addNotificationException(String ruuntimeEx) {
        runtimeNotificationExceptions += ruuntimeEx + "\n";
    }

    public List<KubeHelperException> getExceptions() {
        return exceptions;
    }

    public CronJobsModel setExceptions(List<KubeHelperException> exceptions) {
        this.exceptions = exceptions;
        return this;
    }

    public List<CommandsResult> getCommandsResults() {
        return commandsResults;
    }

    public CronJobsModel setCommandsResults(List<CommandsResult> commandsResults) {
        this.commandsResults = commandsResults;
        return this;
    }

    public List<CronJobResult> getCronJobsResults() {
        return cronJobsResults;
    }

    public CronJobsModel setCronJobsResults(List<CronJobResult> cronJobsResults) {
        this.cronJobsResults = cronJobsResults;
        return this;
    }

    public CommandsFilter getFilter() {
        return filter;
    }

    public CronJobsModel setFilter(CommandsFilter filter) {
        this.filter = filter;
        return this;
    }

    public String getSelectedShell() {
        return selectedShell;
    }

    public CronJobsModel setSelectedShell(String selectedShell) {
        this.selectedShell = selectedShell;
        return this;
    }

    public List<String> getShells() {
        return shells;
    }

    public CronJobsModel setShells(List<String> shells) {
        this.shells = shells;
        return this;
    }

    public String getCommandToExecute() {
        return commandToExecute;
    }

    public CronJobsModel setCommandToExecute(String commandToExecute) {
        this.commandToExecute = commandToExecute;
        return this;
    }

    public String getCommandToExecuteEditable() {
        return commandToExecuteEditable;
    }

    public CronJobsModel setCommandToExecuteEditable(String commandToExecuteEditable) {
        this.commandToExecuteEditable = commandToExecuteEditable;
        return this;
    }

    public String getExecutedCommandOutput() {
        return executedCommandOutput;
    }

    public CronJobsModel setExecutedCommandOutput(String executedCommandOutput) {
        this.executedCommandOutput = executedCommandOutput;
        return this;
    }

    public Map<String, FileSourceResult> getCronJobsReports() {
        return cronJobsReports;
    }


    public CronJobsModel setCronJobsReports(Map<String, FileSourceResult> cronJobsReports) {
        this.cronJobsReports = cronJobsReports;
        return this;
    }

    public String getSelectedReportLabel() {
        return selectedReportLabel;
    }

    public CronJobsModel setSelectedReportLabel(String selectedReportLabel) {
        this.selectedReportLabel = selectedReportLabel;
        return this;
    }

    public String getSelectedReportRaw() {
        return selectedReportRaw;
    }

    public CronJobsModel setSelectedReportRaw(String selectedReportRaw) {
        this.selectedReportRaw = selectedReportRaw;
        return this;
    }

    public String getCommandsRawReportBuffer() {
        return commandsRawReportBuffer;
    }

    public CronJobsModel setCommandsRawReportBuffer(String commandsRawReportBuffer) {
        this.commandsRawReportBuffer = commandsRawReportBuffer;
        return this;
    }

    public String getRuntimeNotificationExceptions() {
        return runtimeNotificationExceptions;
    }

    public String getSelectedReportsFolder() {
        return selectedReportsFolder;
    }

    public CronJobsModel setSelectedReportsFolder(String selectedReportsFolder) {
        this.selectedReportsFolder = selectedReportsFolder;
        return this;
    }
}
