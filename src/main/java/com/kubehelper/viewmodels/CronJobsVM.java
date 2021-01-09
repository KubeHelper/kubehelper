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
package com.kubehelper.viewmodels;

import com.kubehelper.common.Global;
import com.kubehelper.configs.Config;
import com.kubehelper.domain.filters.CommandsFilter;
import com.kubehelper.domain.models.CronJobsModel;
import com.kubehelper.domain.results.CommandsResult;
import com.kubehelper.domain.results.CronJobResult;
import com.kubehelper.services.CommonService;
import com.kubehelper.services.CronJobsService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.event.AfterSizeEvent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.Notification;
import org.zkoss.zkplus.spring.DelegatingVariableResolver;
import org.zkoss.zul.Div;
import org.zkoss.zul.Footer;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Html;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Vbox;
import org.zkoss.zul.Window;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Class for displaying Kube Helper dashboard Cluster and nodes metrics.
 * ViewModel initializes ..kubehelper/pages/dashboard.zul
 *
 * @author JDev
 */
@VariableResolver(DelegatingVariableResolver.class)
public class CronJobsVM implements EventListener<Event> {

    private static Logger logger = LoggerFactory.getLogger(CronJobsVM.class);

    private int centerLayoutHeight = 700;
    private boolean isOnInit = true;

    private String activeTab = "cronJobs";

    private CronJobsModel cronsModel;

    @WireVariable
    private CronJobsService cronJobsService;

    @WireVariable
    private CommonService commonService;

    @WireVariable
    private Config config;

    private final String cronJobsReportsCss = "font-size: %spx;";
    private int cronJobsReportsFontSize = 14;

    private final String commandsManagementCss = "font-size: %spx;";
    private int commandsManagementFontSize = 14;

    private boolean wordWrapCommandsInReport;

    private CronJobsModel cronJobsModel;

    private ListModelList<CommandsResult> commandsResults = new ListModelList<>();
    private ListModelList<CronJobResult> activeCronJobs = new ListModelList<>();

    @Wire
    private Footer commandsGridFooter;

    @Wire
    private Groupbox commandOutputGrBox;


    @Init
    public void init() {
        cronJobsModel = new CronJobsModel();
        onInitPreparations();
    }

    @AfterCompose
    public void afterCompose(@ContextParam(ContextType.VIEW) Component view) {
        Selectors.wireComponents(view, this, false);
        Selectors.wireEventListeners(view, this);
    }

    @Listen("onAfterSize=#centerLayoutIpsAndPortsID")
    public void onAfterSizeCenter(AfterSizeEvent event) {
        centerLayoutHeight = event.getHeight() - 3;
        BindUtils.postNotifyChange(null, null, this, ".");
    }

    @Command
    public void startCronJob() {
        cronJobsService.startCronJob(cronJobsModel);
    }

    /**
     * Prepare commands view.
     * Parse predefined and user commands an pull namespaces.
     */
    private void onInitPreparations() {
        cronJobsService.parsePredefinedCommands(cronJobsModel);
        cronJobsService.parseUserCommands(cronJobsModel);
        commandsResults = new ListModelList<>(cronJobsModel.getCommandsResults());
        activeCronJobs = new ListModelList<>(cronJobsModel.getCronJobsResults());
        cronJobsModel.setNamespaces(cronJobsModel.getNamespaces().isEmpty() ? Set.copyOf(commonService.getAllNamespacesWithoutAll()) : cronJobsModel.getNamespaces());
        logger.info("Found {} namespaces.", cronJobsModel.getNamespaces());
        checkRuntimeNotificationExceptions();
    }


    /**
     * Processing of various events
     *
     * @param event - event.
     */
    @Override
    public void onEvent(Event event) {
        if (Events.ON_CLICK.equals(event.getName())) { //Changes raw source in management,history and toolbarbutton state.
            selectAndChangeSourceToolbarbuttonLabel(event);
        }
    }

    /**
     * Loads last command history file into view and prepare links to other history files.
     */
    @Command
    public void onSelectMainCronJobsTabs(@ContextParam(ContextType.COMPONENT) Tabbox tabbox) {
        activeTab = tabbox.getSelectedTab().getId();
        if ("cronJobsReports".equals(tabbox.getSelectedTab().getId()) && StringUtils.isBlank(cronJobsModel.getSelectedReportRaw())) {
            cronJobsService.prepareCronJobsReports(cronJobsModel);
            redrawCommandsToolbarbuttons("cronJobsReportsToolbarID", cronJobsModel.getCronJobsReports().keySet(), getCommandToolbarButtonId(cronJobsModel.getSelectedReportLabel()));
            refreshReportsOutput();
        }
        checkRuntimeNotificationExceptions();
    }

    /**
     * Refreshes commands history output div.
     */
    public void refreshReportsOutput() {
        Div cronJobsReportBlock = (Div) Path.getComponent("//indexPage/templateInclude/cronJobsReportOutputId");
        cronJobsReportBlock.getChildren().clear();
        String style = wordWrapCommandsInReport ? "style=\"white-space: pre-wrap; word-break: keep-all;\"" : "";
        cronJobsReportBlock.appendChild(new Html("<pre " + style + "><code>" + cronJobsModel.getSelectedReportRaw() + "</code></pre>"));
        BindUtils.postNotifyChange(this, ".");
    }

    /**
     * Checks if in commands model exists runtime exceptions and shows notification.
     */
    private void checkRuntimeNotificationExceptions() {
        if (StringUtils.isNotBlank(cronJobsModel.getRuntimeNotificationExceptions())) {
            Notification.show(cronJobsModel.getRuntimeNotificationExceptions(), "error", null, "bottom_right", 5000);
        }
    }

    /**
     * Recreates and prepares toolbarbuttons after refresh or first start.
     *
     * @param toolbarId             - toolbar id.
     * @param entries               - set with button labels.
     * @param activeToolbarButtonId - sets active toolbarbutton if history exists.
     */
    private void redrawCommandsToolbarbuttons(String toolbarId, Set<String> entries, String activeToolbarButtonId) {
        createReportsToolbarButtons(toolbarId, entries);
        if (StringUtils.isNotBlank(cronJobsModel.getSelectedReportLabel())) {
            enableDisableMenuItem(activeToolbarButtonId, true, "bold;");
        }
    }

    /**
     * Creates toolbarbuttons on the commands management and commands history tabs for view sources/files.
     */
    private void createReportsToolbarButtons(String toolbarId, Set<String> sources) {
        Vbox commandsSourcesToolbar = (Vbox) Path.getComponent("//indexPage/templateInclude/" + toolbarId);
        commandsSourcesToolbar.getChildren().clear();
        sources.forEach(key -> {
            Toolbarbutton toolbarbutton = new Toolbarbutton(key);
            toolbarbutton.setId(getCommandToolbarButtonId(key));
            toolbarbutton.setIconSclass("z-icon-file");
            toolbarbutton.setStyle("text-align: left;");
            toolbarbutton.addEventListener("onClick", this);
            toolbarbutton.setHflex("1");
            commandsSourcesToolbar.appendChild(toolbarbutton);
        });
    }

    /**
     * Make active or inactive menu item for commands management and history.
     *
     * @param toolbarbuttonId - toolbarbutton id.
     * @param disabled        - disable or enable button.
     * @param fontWeight      - font weight bold/normal.
     */
    private void enableDisableMenuItem(String toolbarbuttonId, boolean disabled, String fontWeight) {
        Toolbarbutton toolbarbutton = (Toolbarbutton) Path.getComponent("//indexPage/templateInclude/" + toolbarbuttonId);
        toolbarbutton.setDisabled(disabled);
        toolbarbutton.setStyle("text-align: left;font-weight: " + fontWeight);
    }

    private String getCommandToolbarButtonId(String label) {
        return label.replaceAll("[^a-zA-Z0-9]", "") + "Id";
    }


    /**
     * Filters searches and refresh total items label and search results view.
     */
    @Command
    @NotifyChange({"commandsTotalItems", "commandsResults"})
    public void filterCommands() {
        commandsResults.clear();
        for (CommandsResult commandeResult : cronJobsModel.getCommandsResults()) {
            if (commonService.checkEqualsFilter(commandeResult.getGroup(), getFilter().getSelectedGroupFilter()) &&
                    commonService.checkEqualsFilter(commandeResult.getFile(), getFilter().getSelectedFileFilter()) &&
                    StringUtils.containsIgnoreCase(commandeResult.getCommand(), getFilter().getCommand()) &&
                    StringUtils.containsIgnoreCase(commandeResult.getDescription(), getFilter().getDescription())) {
                commandsResults.add(commandeResult);
            }
        }
    }


    /**
     * Shows full command by clicking on grid item.
     *
     * @param item - commands grid item.
     */
    @Command
    @NotifyChange({"commandToExecute", "commandToExecuteEditable"})
    public void showFullCommand(@BindingParam("clickedItem") CommandsResult item) {
//        TODO
//        cronJobsModel.setCommandToExecute(getCommandWithoutUnnecessaryWhitespaces(item.getCommand()));
        cronJobsModel.setCommandToExecuteEditable(item.getCommand());
    }

    /**
     * Changes resources in comboxex depend on namespace in commands window.
     */
    @Command
    public void changeResourcesInComboxexDependOnNamespace() {
        cronJobsService.fetchResourcesDependsOnNamespace(cronJobsModel);
        BindUtils.postNotifyChange(this, "namespacedPods", "namespacedDeployments", "namespacedStatefulSets", "namespacedReplicaSets", "namespacedDaemonSets", "namespacedConfigMaps", "namespacedServices", "namespacedJobs");
        Notification.show(String.format("New resources for %s namespace, successfully fetched.", cronJobsModel.getSelectedNamespace()), "info", commandOutputGrBox, "bottom_right", 2000);
    }

    /**
     * Returns commands results for grid and shows Notification if nothing was found or/and error window if some errors has occurred while parsing the results.
     *
     * @return - search results
     */
    public ListModelList<CommandsResult> getCommandsResults() {
        if (isOnInit && commandsResults.isEmpty()) {
            Notification.show("Nothing found.", "info", commandsGridFooter, "bottom_right", 2000);
        }
        if (isOnInit && !commandsResults.isEmpty()) {
            Notification.show("Loaded: " + commandsResults.size() + " items", "info", commandsGridFooter, "before_end", 2000);
        }
        if (isOnInit && cronJobsModel.hasErrors()) {
            Window window = (Window) Executions.createComponents(Global.PATH_TO_ERROR_RESOURCE_ZUL, null, Map.of("errors", cronJobsModel.getExceptions()));
            window.doModal();
            cronJobsModel.setExceptions(new ArrayList<>());
        }
        isOnInit = false;
        return commandsResults;
    }

    /**
     * Changes raw source and toolbarbutton state by onClick event.
     *
     * @param event - onClick event.
     */
    private void selectAndChangeSourceToolbarbuttonLabel(Event event) {
        String label = ((Toolbarbutton) event.getTarget()).getLabel();
        String oldToolbarbuttonId = null;
        if ("commandsHistory".equals(activeTab)) {
            oldToolbarbuttonId = getCommandToolbarButtonId(cronJobsModel.getSelectedReportLabel());
            cronJobsModel.setSelectedReportLabel(label);
            cronJobsService.changeHistoryRaw(cronJobsModel);
            refreshReportsOutput();
        }
        String newToolbarbuttonId = getCommandToolbarButtonId(label);
        enableDisableMenuItem(oldToolbarbuttonId, false, "normal;");
        enableDisableMenuItem(newToolbarbuttonId, true, "bold;");
    }

    //  COMMANDS GETTERS AND SETTERS ================


    public CommandsFilter getFilter() {
        return cronJobsModel.getFilter();
    }

    public String getCommandsTotalItems() {
        return String.format("Total Items: %d", commandsResults.size());
    }

    public String getCommandToExecute() {
        return cronJobsModel.getCommandToExecute();
    }

    public void setCommandToExecute(String commandToExecute) {
        cronJobsModel.setCommandToExecute(commandToExecute);
    }

    public String getCommandsGridHeight() {
        return centerLayoutHeight * 0.38 + "px";
    }

    public String getFullCommandBoxHeight() {
        return centerLayoutHeight * 0.07 + "px";
    }

    public String getCronJobsListHeight() {
        return centerLayoutHeight * 0.43 + "px";
    }

    public String getCommandToExecuteEditable() {
        return cronJobsModel.getCommandToExecuteEditable();
    }

    public void setCommandToExecuteEditable(String commandToExecuteEditable) {
        cronJobsModel.setCommandToExecuteEditable(commandToExecuteEditable);
    }

    public Set<String> getNamespaces() {
        return cronJobsModel.getNamespaces();
    }

    public Set<String> getNamespacedPods() {
        return cronJobsModel.getNamespacedPods();
    }

    public Set<String> getNamespacedDeployments() {
        return cronJobsModel.getNamespacedDeployments();
    }

    public Set<String> getNamespacedStatefulSets() {
        return cronJobsModel.getNamespacedStatefulSets();
    }

    public Set<String> getNamespacedReplicaSets() {
        return cronJobsModel.getNamespacedReplicaSets();
    }

    public Set<String> getNamespacedDaemonSets() {
        return cronJobsModel.getNamespacedDaemonSets();
    }

    public Set<String> getNamespacedConfigMaps() {
        return cronJobsModel.getNamespacedConfigMaps();
    }

    public Set<String> getNamespacedServices() {
        return cronJobsModel.getNamespacedServices();
    }

    public Set<String> getNamespacedJobs() {
        return cronJobsModel.getNamespacedJobs();
    }

    public String getSelectedNamespace() {
        return cronJobsModel.getSelectedNamespace();
    }

    public void setSelectedNamespace(String selectedNamespace) {
        cronJobsModel.setSelectedNamespace(selectedNamespace);
    }

    public Set<String> getSelectedDeployments() {
        return cronJobsModel.getSelectedDeployments();
    }

    public Set<String> getSelectedStatefulSets() {
        return cronJobsModel.getSelectedStatefulSets();
    }

    public Set<String> getSelectedReplicaSets() {
        return cronJobsModel.getSelectedReplicaSets();
    }

    public Set<String> getSelectedDaemonSets() {
        return cronJobsModel.getSelectedDaemonSets();
    }

    public Set<String> getSelectedConfigMaps() {
        return cronJobsModel.getSelectedConfigMaps();
    }

    public Set<String> getSelectedServices() {
        return cronJobsModel.getSelectedServices();
    }

    public Set<String> getSelectedJobs() {
        return cronJobsModel.getSelectedJobs();
    }

    public Set<String> getSelectedPods() {
        return cronJobsModel.getSelectedPods();
    }

    public void setSelectedPods(Set<String> selectedPods) {
        cronJobsModel.setSelectedPods(selectedPods);
    }

    public void setSelectedDeployments(Set<String> selectedDeployments) {
        cronJobsModel.setSelectedDeployments(selectedDeployments);
    }

    public void setSelectedStatefulSets(Set<String> selectedStatefulSets) {
        cronJobsModel.setSelectedStatefulSets(selectedStatefulSets);
    }

    public void setSelectedReplicaSets(Set<String> selectedReplicaSets) {
        cronJobsModel.setSelectedReplicaSets(selectedReplicaSets);
    }

    public void setSelectedDaemonSets(Set<String> selectedDaemonSets) {
        cronJobsModel.setSelectedDaemonSets(selectedDaemonSets);
    }

    public void setSelectedConfigMaps(Set<String> selectedConfigMaps) {
        cronJobsModel.setSelectedConfigMaps(selectedConfigMaps);
    }

    public void setSelectedServices(Set<String> selectedServices) {
        cronJobsModel.setSelectedServices(selectedServices);
    }

    public void setSelectedJobs(Set<String> selectedJobs) {
        cronJobsModel.setSelectedJobs(selectedJobs);
    }

    public String getSelectedShell() {
        return cronJobsModel.getSelectedShell();
    }

    public void setSelectedShell(String selectedShell) {
        cronJobsModel.setSelectedShell(selectedShell);
    }

    public List<String> getShells() {
        return cronJobsModel.getShells();
    }

    public boolean isHotReplacementEnabled() {
        return config.getCommandsHotReplacement();
    }

    public void setHotReplacementEnabled(boolean hotReplacement) {
        config.setCommandsHotReplacement(hotReplacement);
    }

    public ListModelList<CronJobResult> getActiveCronJobs() {
        return activeCronJobs;
    }

    //  CRON JOBS REPORTS GETTERS AND SETTERS ================


    public String getSelectedCronJobsReportRaw() {
        return cronJobsModel.getSelectedReportRaw();
    }

    public String getSelectedCronJobsReportLabel() {
        return cronJobsModel.getSelectedReportLabel();
    }

    public void setSelectedCronJobsReportLabel(String selectedCommandsHistory) {
        cronJobsModel.setSelectedReportLabel(selectedCommandsHistory);
    }

    public String getCronJobsReportsSrcPanelHeight() {
        return centerLayoutHeight - 95 + "px";
    }

    public String getCronJobsReportsHeight() {
        return centerLayoutHeight - 50 + "px";
    }

    public boolean isWordWrapCommandsInReport() {
        return wordWrapCommandsInReport;
    }

    public void setWordWrapCommandsInReport(boolean wordWrapCommandsInReport) {
        this.wordWrapCommandsInReport = wordWrapCommandsInReport;
    }

    public int getCronJobsReportsFontSize() {
        return cronJobsReportsFontSize;
    }

    public String getCronJobsReportsCss() {
        return String.format(cronJobsReportsCss, cronJobsReportsFontSize);
    }

}