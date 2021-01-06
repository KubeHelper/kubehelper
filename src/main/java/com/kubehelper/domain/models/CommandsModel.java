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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author JDev
 */
public class CommandsModel implements PageModel {

    private String templateUrl = "~./zul/kubehelper/pages/commands.zul";

    //    private String userCommandsPath = "C:\\temp\\kubehelper";
    private String userCommandsPath = "/tmp/kubehelper";
    public static String NAME = Global.COMMANDS_MODEL;
    private List<KubeHelperException> buildExceptions = new ArrayList<>();

    //  COMMANDS ================
    private List<CommandsResult> commandsResults = new ArrayList<>();
    private CommandsFilter filter = new CommandsFilter();

    private String selectedNamespace = "";
    private Set<String> namespaces = new HashSet<>();
    private Set<String> namespacedPods = new HashSet<>();
    private Set<String> namespacedDeployments = new HashSet<>();
    private Set<String> namespacedStatefulSets = new HashSet<>();
    private Set<String> namespacedReplicaSets = new HashSet<>();
    private Set<String> namespacedDaemonSets = new HashSet<>();
    private Set<String> namespacedConfigMaps = new HashSet<>();
    private Set<String> namespacedServices = new HashSet<>();
    private Set<String> namespacedJobs = new HashSet<>();
    private Set<String> selectedPods = new HashSet<>();
    private Set<String> selectedDeployments = new HashSet<>();
    private Set<String> selectedStatefulSets = new HashSet<>();
    private Set<String> selectedReplicaSets = new HashSet<>();
    private Set<String> selectedDaemonSets = new HashSet<>();
    private Set<String> selectedConfigMaps = new HashSet<>();
    private Set<String> selectedServices = new HashSet<>();
    private Set<String> selectedJobs = new HashSet<>();

    private String selectedShell = "bash";
    private List<String> shells = Arrays.asList("bash", "sh", "fish", "zsh", "csh", "ksh");

    private String commandToExecute = "";
    private String commandToExecuteEditable = "";
    private String executedCommandOutput = "";

    //  COMMANDS MANAGEMENT ================
    private Map<String, FileSource> commandsSources = new HashMap<>();
    private String selectedCommandsSourceLabel = "";
    private String selectedCommandsSourceRaw = "";

    //  COMMANDS HISTORY ================
    private Map<String, FileSource> commandsHistories = new HashMap<>();

    private List<String> commandsHistoryRanges = Arrays.asList("Week", "Last Week", "Last Month", "Year", "All");

    private String selectedCommandsHistoryLabel = "";
    private String selectedCommandsHistoryRaw = "";
    private String commandsRawHistoryBuffer = "";
    private String selectedCommandsHistoryRange = "";
    private boolean showOnlyCommandsInHistory;


    public CommandsModel() {
    }

    public void addCommandResult(CommandsResult commandResult) {
        commandsResults.add(commandResult);
        filter.addGroupFilter(commandResult.getGroup());
        filter.addOperationFilter(commandResult.getFile());
    }

    public void addCommandSource(String label, String filePath, boolean readonly) {
        FileSource commandSource = new FileSource();
        commandSource.setLabel(label);
        commandSource.setFilePath(filePath);
        commandSource.setReadonly(readonly);
        commandsSources.put(label, commandSource);
    }

    public void addHistorySource(String label, String filePath) {
        FileSource historySource = new FileSource();
        historySource.setLabel(label);
        historySource.setFilePath(filePath);
        commandsHistories.put(label, historySource);
    }

    public void sortMapByDateDesc() {
        commandsHistories = ImmutableSortedMap.copyOf(commandsHistories, Comparator.comparing(date -> LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE), Comparator.reverseOrder()));
    }

    public void addParseException(Exception exception) {
        this.buildExceptions.add(new KubeHelperException(exception));
    }

    @Override
    public void addException(String message, Exception exception) {
        this.buildExceptions.add(new KubeHelperException(message, exception));
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getTemplateUrl() {
        return templateUrl;
    }

    public Set<String> getNamespaces() {
        return namespaces;
    }

    public CommandsModel setNamespaces(Set<String> namespaces) {
        this.namespaces = namespaces;
        return this;
    }

    public Set<String> getNamespacedPods() {
        return namespacedPods;
    }

    public CommandsModel setNamespacedPods(Set<String> namespacedPods) {
        this.namespacedPods = namespacedPods;
        return this;
    }

    public Set<String> getNamespacedDeployments() {
        return namespacedDeployments;
    }

    public CommandsModel setNamespacedDeployments(Set<String> namespacedDeployments) {
        this.namespacedDeployments = namespacedDeployments;
        return this;
    }

    public Set<String> getNamespacedStatefulSets() {
        return namespacedStatefulSets;
    }

    public CommandsModel setNamespacedStatefulSets(Set<String> namespacedStatefulSets) {
        this.namespacedStatefulSets = namespacedStatefulSets;
        return this;
    }

    public Set<String> getNamespacedReplicaSets() {
        return namespacedReplicaSets;
    }

    public CommandsModel setNamespacedReplicaSets(Set<String> namespacedReplicaSets) {
        this.namespacedReplicaSets = namespacedReplicaSets;
        return this;
    }

    public Set<String> getNamespacedDaemonSets() {
        return namespacedDaemonSets;
    }

    public CommandsModel setNamespacedDaemonSets(Set<String> namespacedDaemonSets) {
        this.namespacedDaemonSets = namespacedDaemonSets;
        return this;
    }

    public Set<String> getNamespacedConfigMaps() {
        return namespacedConfigMaps;
    }

    public CommandsModel setNamespacedConfigMaps(Set<String> namespacedConfigMaps) {
        this.namespacedConfigMaps = namespacedConfigMaps;
        return this;
    }

    public Set<String> getNamespacedServices() {
        return namespacedServices;
    }

    public CommandsModel setNamespacedServices(Set<String> namespacedServices) {
        this.namespacedServices = namespacedServices;
        return this;
    }

    public Set<String> getNamespacedJobs() {
        return namespacedJobs;
    }

    public CommandsModel setNamespacedJobs(Set<String> namespacedJobs) {
        this.namespacedJobs = namespacedJobs;
        return this;
    }

    public List<CommandsResult> getCommandsResults() {
        return commandsResults;
    }

    public CommandsModel setCommandsResults(List<CommandsResult> commandsResults) {
        this.commandsResults = commandsResults;
        return this;
    }

    public List<KubeHelperException> getBuildExceptions() {
        return buildExceptions;
    }

    public CommandsModel setBuildExceptions(List<KubeHelperException> buildExceptions) {
        this.buildExceptions = buildExceptions;
        return this;
    }

    public boolean hasBuildErrors() {
        return !buildExceptions.isEmpty();
    }

    public CommandsFilter getFilter() {
        return filter;
    }

    public CommandsModel setFilter(CommandsFilter filter) {
        this.filter = filter;
        return this;
    }

    public String getSelectedNamespace() {
        return selectedNamespace;
    }

    public CommandsModel setSelectedNamespace(String selectedNamespace) {
        this.selectedNamespace = selectedNamespace;
        return this;
    }

    public Set<String> getSelectedDeployments() {
        return selectedDeployments;
    }

    public CommandsModel setSelectedDeployments(Set<String> selectedDeployments) {
        this.selectedDeployments = selectedDeployments;
        return this;
    }

    public Set<String> getSelectedStatefulSets() {
        return selectedStatefulSets;
    }

    public CommandsModel setSelectedStatefulSets(Set<String> selectedStatefulSets) {
        this.selectedStatefulSets = selectedStatefulSets;
        return this;
    }

    public Set<String> getSelectedReplicaSets() {
        return selectedReplicaSets;
    }

    public CommandsModel setSelectedReplicaSets(Set<String> selectedReplicaSets) {
        this.selectedReplicaSets = selectedReplicaSets;
        return this;
    }

    public Set<String> getSelectedDaemonSets() {
        return selectedDaemonSets;
    }

    public CommandsModel setSelectedDaemonSets(Set<String> selectedDaemonSets) {
        this.selectedDaemonSets = selectedDaemonSets;
        return this;
    }

    public Set<String> getSelectedConfigMaps() {
        return selectedConfigMaps;
    }

    public CommandsModel setSelectedConfigMaps(Set<String> selectedConfigMaps) {
        this.selectedConfigMaps = selectedConfigMaps;
        return this;
    }

    public Set<String> getSelectedServices() {
        return selectedServices;
    }

    public CommandsModel setSelectedServices(Set<String> selectedServices) {
        this.selectedServices = selectedServices;
        return this;
    }

    public Set<String> getSelectedJobs() {
        return selectedJobs;
    }

    public CommandsModel setSelectedJobs(Set<String> selectedJobs) {
        this.selectedJobs = selectedJobs;
        return this;
    }

    public String getSelectedCommandsSourceRaw() {
        return selectedCommandsSourceRaw;
    }

    public CommandsModel setSelectedCommandsSourceRaw(String selectedCommandsSourceRaw) {
        this.selectedCommandsSourceRaw = selectedCommandsSourceRaw;
        return this;
    }

    public String getUserCommandsPath() {
        return userCommandsPath;
    }

    public CommandsModel setUserCommandsPath(String userCommandsPath) {
        this.userCommandsPath = userCommandsPath;
        return this;
    }

    public CommandsModel setCommandsSources(Map<String, FileSource> commandsSources) {
        this.commandsSources = commandsSources;
        return this;
    }

    public Map<String, FileSource> getCommandsSources() {
        return commandsSources;
    }

    public String getSelectedCommandsSourceLabel() {
        return selectedCommandsSourceLabel;
    }

    public CommandsModel setSelectedCommandsSourceLabel(String selectedCommandsSourceLabel) {
        this.selectedCommandsSourceLabel = selectedCommandsSourceLabel;
        return this;
    }

    public String getExecutedCommandOutput() {
        return executedCommandOutput;
    }

    public CommandsModel setExecutedCommandOutput(String executedCommandOutput) {
        this.executedCommandOutput = executedCommandOutput;
        return this;
    }

    public String getCommandToExecuteEditable() {
        return commandToExecuteEditable;
    }

    public CommandsModel setCommandToExecuteEditable(String commandToExecuteEditable) {
        this.commandToExecuteEditable = commandToExecuteEditable;
        return this;
    }

    public String getCommandToExecute() {
        return commandToExecute;
    }

    public CommandsModel setCommandToExecute(String commandToExecute) {
        this.commandToExecute = commandToExecute;
        return this;
    }

    public Set<String> getSelectedPods() {
        return selectedPods;
    }

    public CommandsModel setSelectedPods(Set<String> selectedPods) {
        this.selectedPods = selectedPods;
        return this;
    }

    public String getSelectedShell() {
        return selectedShell;
    }

    public CommandsModel setSelectedShell(String selectedShell) {
        this.selectedShell = selectedShell;
        return this;
    }

    public String getSelectedCommandsHistoryLabel() {
        return selectedCommandsHistoryLabel;
    }

    public CommandsModel setSelectedCommandsHistoryLabel(String selectedCommandsHistoryLabel) {
        this.selectedCommandsHistoryLabel = selectedCommandsHistoryLabel;
        return this;
    }

    public String getSelectedCommandsHistoryRaw() {
        return selectedCommandsHistoryRaw;
    }

    public CommandsModel setSelectedCommandsHistoryRaw(String selectedCommandsHistoryRaw) {
        this.selectedCommandsHistoryRaw = selectedCommandsHistoryRaw;
        return this;
    }

    public Map<String, FileSource> getCommandsHistories() {
        return commandsHistories;
    }

    public CommandsModel setCommandsHistories(Map<String, FileSource> commandsHistories) {
        this.commandsHistories = commandsHistories;
        return this;
    }

    public List<String> getShells() {
        return shells;
    }

    public String getSelectedCommandsHistoryRange() {
        return selectedCommandsHistoryRange;
    }

    public CommandsModel setSelectedCommandsHistoryRange(String selectedCommandsHistoryRange) {
        this.selectedCommandsHistoryRange = selectedCommandsHistoryRange;
        return this;
    }

    public List<String> getCommandsHistoryRanges() {
        return commandsHistoryRanges;
    }

    public boolean isShowOnlyCommandsInHistory() {
        return showOnlyCommandsInHistory;
    }

    public CommandsModel setShowOnlyCommandsInHistory(boolean showOnlyCommandsInHistory) {
        this.showOnlyCommandsInHistory = showOnlyCommandsInHistory;
        return this;
    }

    public String getCommandsRawHistoryBuffer() {
        return commandsRawHistoryBuffer;
    }

    public CommandsModel setCommandsRawHistoryBuffer(String commandsRawHistoryBuffer) {
        this.commandsRawHistoryBuffer = commandsRawHistoryBuffer;
        return this;
    }

    public class FileSource {
        private String label;
        private String filePath;
        private boolean readonly = true;

        public String getLabel() {
            return label;
        }

        public FileSource setLabel(String label) {
            this.label = label;
            return this;
        }

        public String getFilePath() {
            return filePath;
        }

        public FileSource setFilePath(String filePath) {
            this.filePath = filePath;
            return this;
        }

        public boolean isReadonly() {
            return readonly;
        }

        public FileSource setReadonly(boolean readonly) {
            this.readonly = readonly;
            return this;
        }
    }
}
