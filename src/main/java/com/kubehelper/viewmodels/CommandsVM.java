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
import com.kubehelper.domain.models.CommandsModel;
import com.kubehelper.domain.results.CommandsResult;
import com.kubehelper.services.CommandsService;
import com.kubehelper.services.CommonService;
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
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.Notification;
import org.zkoss.zkplus.spring.DelegatingVariableResolver;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Footer;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Html;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Slider;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Vbox;
import org.zkoss.zul.Window;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author JDev
 */
@VariableResolver(DelegatingVariableResolver.class)
public class CommandsVM implements EventListener<Event> {

    private static Logger logger = LoggerFactory.getLogger(CommandsVM.class);

    private String activeTab = "commands";
    private boolean isOnInit = true;

    private final String fontSizeCss = "font-size: %spx;";
    private int fontSize = 14;

    private int centerLayoutHeight = 700;

    private ListModelList<CommandsResult> commandsResults = new ListModelList<>();

    private CommandsModel commandsModel;

    @WireVariable
    private CommonService commonService;

    @WireVariable
    private Config config;

    @WireVariable
    private CommandsService commandsService;

    @Wire
    private Footer commandsGridFooter;

    @Wire
    private Groupbox commandOutputGrBox;

    @Wire
    private Slider fontSizeSlider;

    @Wire
    private Combobox commandsNamespacesCBox;

    @Wire
    private Textbox commandToExecuteTBox;

    @Init
    @NotifyChange("*")
    public void init() {
        commandsModel = new CommandsModel();
        onInitPreparations();
    }

    /**
     * Creates CheckBox components Dynamically after UI render.
     * <p>
     * Explanation:
     * We need Selectors.wireComponents() in order to be able to @Wire GUI components.
     */
    @AfterCompose
    public void afterCompose(@ContextParam(ContextType.VIEW) Component view) {
        Selectors.wireComponents(view, this, false);
        Selectors.wireEventListeners(view, this);
        fontSizeSlider.addEventListener("onScroll", this);
        commandsNamespacesCBox.addEventListener("onSelect", this);
        commandToExecuteTBox.addEventListener("onChange", this);
        createCommandsToolbarButtons("commandsSourcesToolbarID", commandsModel.getCommandsSources().keySet());
        createCommandsToolbarButtons("commandsHistoriesToolbarID", commandsModel.getCommandsHistories().keySet());
//        TODO call method after refresh, components should be in DOM
//        enableDisableMenuItem(commandsModel.getSelectedCommandsSource(), true, "bold;");
    }

    @Listen("onAfterSize=#centerLayoutCommandsID")
    public void onAfterSizeCenter(AfterSizeEvent event) {
        centerLayoutHeight = event.getHeight() - 3;
        BindUtils.postNotifyChange(null, null, this, ".");
    }

    @Command
    @NotifyChange({"executedCommandOutput"})
    public void run() {
        if (StringUtils.isBlank(commandsModel.getCommandToExecuteEditable())) {
            Notification.show("Please select or put the command for execute.", "info", commandOutputGrBox, "bottom_right", 3000);
            return;
        }
        commandsService.run(commandsModel);
        highlightBlock();
    }


    /**
     * Prepare view for result depends on filters or new searches
     */
    private void onInitPreparations() {
        commandsService.parsePredefinedCommands(commandsModel);
        commandsService.prepareCommandsHistory(commandsModel);
//        commandsService.parseUserCommands(commandsModel);
//        TODO change after
//        commandsModel.setSelectedCommandsSourceLabel("commands2");
//        commandsModel.setSelectedCommandsSourceRaw(commandsModel.getCommandsSources().get("commands2").getRawSource());
        commandsResults = new ListModelList<>(commandsModel.getCommandsResults());
        commandsModel.setNamespaces(commandsModel.getNamespaces().isEmpty() ? Set.copyOf(commonService.getAllNamespacesWithoutAll()) : commandsModel.getNamespaces());
        logger.info("Found {} namespaces.", commandsModel.getNamespaces());
    }

    /**
     * Filters searches and refresh total items label and search results view.
     */
    @Command
    @NotifyChange({"commandsTotalItems", "commandsResults"})
    public void filterCommands() {
        commandsResults.clear();
        for (CommandsResult commandeResult : commandsModel.getCommandsResults()) {
            if (commonService.checkEqualsFilter(commandeResult.getGroup(), getFilter().getSelectedGroupFilter()) &&
                    commonService.checkEqualsFilter(commandeResult.getOperation(), getFilter().getSelectedOperationFilter()) &&
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
        commandsModel.setCommandToExecute(getCommandWithoutBreaks(item.getCommand()));
        commandsModel.setCommandToExecuteEditable(item.getCommand());
    }


    /**
     * Processing of various events
     *
     * @param event - event.
     */
    @Override
    @NotifyChange({"namespacedPods", "namespacedDeployments", "namespacedStatefulSets", "namespacedReplicaSets", "namespacedDaemonSets", "namespacedConfigMaps", "namespacedServices", "namespacedJobs"})
    public void onEvent(Event event) {
        if ("onScroll".equals(event.getName())) { //font size slider event
            changeFontSize(event);
        } else if ("onClick".equals(event.getName())) { //Changes raw source in management,history and toolbarbutton state.
            selectAndChangeCommandsSource(event);
        } else if ("onSelect".equals(event.getName()) && event.getTarget() instanceof Toolbarbutton) {  //change resources in comboxex depend on namespace in commands window
            changeResourcesInComboxexDependOnNamespace();
        } else if ("onChange".equals(event.getName())) {  // synchronize command to execute and hot replacement on full command textbox onChange Event
            synchronizeCommandToExecuteAndHotReplacement();
        }
    }

    /**
     * loads last command history file into view.
     */
    @Command
    public void onSelectMainCommandsTabs(@ContextParam(ContextType.COMPONENT) Tabbox tabbox) {
        activeTab = tabbox.getSelectedTab().getId();
        if ("commandsHistory".equals(tabbox.getSelectedTab().getId()) && StringUtils.isBlank(commandsModel.getSelectedCommandsHistoryRaw())) {
            commandsService.setStartHistoryRaw(commandsModel);
        } else if ("commandsManagement".equals(tabbox.getSelectedTab().getId()) && StringUtils.isBlank(commandsModel.getSelectedCommandsSourceRaw())) {
            commandsService.setStartCommandsRaw(commandsModel);
        }
    }

    @Command
    public void showOnlyCommandsInHistory(@ContextParam(ContextType.COMPONENT) Checkbox checkbox) {
        commandsService.showOnlyCommandsInHistory(commandsModel, checkbox.isChecked());

        Div historyOutputBlock = (Div) Path.getComponent("//indexPage/templateInclude/historyOutputId");
        historyOutputBlock.getChildren().clear();
        historyOutputBlock.appendChild(new Html("<pre><code>" + commandsModel.getSelectedCommandsHistoryRaw() + "</code></pre>"));
        BindUtils.postNotifyChange(this, ".");
    }

    /**
     * Synchronizes command to execute and hot replacement on full command textbox onChange event.
     */
    private void synchronizeCommandToExecuteAndHotReplacement() {
        commandsModel.setCommandToExecute(getCommandWithoutBreaks(commandsModel.getCommandToExecuteEditable()));
        if (isHotReplacementEnabled()) {
            //TODO
//            commandsService.commandHotReplacement(commandsModel);
        }
        BindUtils.postNotifyChange(this, "commandToExecute", "commandToExecuteEditable");
    }

    /**
     * Changes resources in comboxex depend on namespace in commands window.
     */
    private void changeResourcesInComboxexDependOnNamespace() {
        commandsService.fetchResourcesDependsOnNamespace(commandsModel);
        BindUtils.postNotifyChange(this, "namespacedPods", "namespacedDeployments", "namespacedStatefulSets", "namespacedReplicaSets", "namespacedDaemonSets", "namespacedConfigMaps", "namespacedServices", "namespacedJobs");
        Notification.show(String.format("New resources for %s namespace, successfully fetched.", commandsModel.getSelectedNamespace()), "info", commandOutputGrBox, "bottom_right", 2000);
    }

    /**
     * Changes raw source and toolbarbutton state.
     *
     * @param event - event.
     */
    private void selectAndChangeCommandsSource(Event event) {
        String label = ((Toolbarbutton) event.getTarget()).getLabel();
        String oldToolbarbuttonId = getCommandToolbarButtonId(commandsModel.getSelectedCommandsSourceLabel());
        String newToolbarbuttonId = getCommandToolbarButtonId(label);
        enableDisableMenuItem(oldToolbarbuttonId, false, "normal;");
        enableDisableMenuItem(newToolbarbuttonId, true, "bold;");
        changeRawSource(label);
    }

    /**
     * Changes history raw depends on selected Range in commandsModel.
     */
    @Command
    public void changeHistoryRaw() {
        commandsModel.setSelectedCommandsHistoryLabel(commandsModel.getSelectedCommandsHistoryRange());
        commandsService.setStartHistoryRaw(commandsModel);
        BindUtils.postNotifyChange(this, ".");
    }

    /**
     * Refresh history files and content.
     */
    @Command
    public void refreshHistory() {
        commandsModel.getCommandsHistories().clear();
        commandsService.prepareCommandsHistory(commandsModel);
        BindUtils.postNotifyChange(this, ".");
    }

    /**
     * Changes font size in command output panel.
     *
     * @param event - event.
     */
    private void changeFontSize(Event event) {
        Slider fontSlider = (Slider) event.getTarget();
        fontSize = fontSlider.getCurpos();
        BindUtils.postNotifyChange(this, "fontSizeCss");
    }


    /**
     * Shows popup window with executed command output.
     */
    @Command
    public void commandOutputFullSize() {
        if (StringUtils.isAnyEmpty(commandsModel.getCommandToExecute(), commandsModel.getExecutedCommandOutput())) {
            Notification.show("Cannot open full screen mode if command is not executed.", "warning", commandOutputGrBox, "bottom_right", 3000);
            return;
        }
        Map<String, String> params = Map.of("title", "Command Output", "command", commandsModel.getCommandToExecute(), "commandOutput", commandsModel.getExecutedCommandOutput());
        Window window = (Window) Executions.createComponents("~./zul/kubehelper/components/command-output-window.zul", null, params);
        window.doModal();
    }

    /**
     * Returns search results for grid and shows Notification if nothing was found or/and error window if some errors has occurred while parsing the results.
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
        if (isOnInit && commandsModel.hasBuildErrors()) {
            Window window = (Window) Executions.createComponents(Global.PATH_TO_ERROR_RESOURCE_ZUL, null, Map.of("errors", commandsModel.getBuildExceptions()));
            window.doModal();
            commandsModel.setBuildExceptions(new ArrayList<>());
        }
        isOnInit = false;
        return commandsResults;
    }

    /**
     * Highlights div block with command output content.
     */
    private void highlightBlock() {
        Div highlightBlock = (Div) Path.getComponent("//indexPage/templateInclude/commandOutputId");
        highlightBlock.getChildren().clear();

        if (StringUtils.isBlank(commandsModel.getExecutedCommandOutput())) {
            Notification.show("Command execution output is empty.", "info", commandOutputGrBox, "bottom_right", 3000);
        }

        highlightBlock.appendChild(new Html("<pre><code>" + commandsModel.getExecutedCommandOutput() + "</code></pre>"));
        BindUtils.postNotifyChange(this, ".");
    }


    //  COMMANDS MANAGEMENT METHODS ================


    /**
     * Creates toolbarbuttons on the commands management and commands history tabs for view sources/files.
     */
    private void createCommandsToolbarButtons(String toolbarId, Set<String> sources) {
        Vbox commandsSourcesToolbarID = (Vbox) Path.getComponent("//indexPage/templateInclude/" + toolbarId);
        sources.forEach(key -> {
            Toolbarbutton toolbarbutton = new Toolbarbutton(key);
            toolbarbutton.setId(getCommandToolbarButtonId(key));
            toolbarbutton.setIconSclass("z-icon-file");
            toolbarbutton.addEventListener("onClick", this);
            toolbarbutton.setHflex("1");
            commandsSourcesToolbarID.appendChild(toolbarbutton);
        });
    }

    @Listen("onSelect = toolbarbutton")
    public void onOK() {
        BindUtils.postNotifyChange(null, null, this, ".");
    }

//    /**
//     * Highlights div block with command output content.
//     */
//    private void highlightBlock() {
//
//
//        rawSourceBlock.appendChild(new Html("<pre><code>" + commandsModel.getExecutedCommandOutput() + "</code></pre>"));
//        BindUtils.postNotifyChange(null, null, this, ".");
//    }
//
//    private void changeRawSource(String htmlBlockId, String label, String source) {
//        Div rawSourceBlock = (Div) Path.getComponent("//indexPage/templateInclude/"+htmlBlockId);
//        rawSourceBlock.getChildren().clear();
//        commandsModel.setSelectedCommandsSourceLabel(label);
//        commandsModel.setSelectedCommandsSourceRaw("<pre><code>" + commandsModel.getCommandsSources().get(label).getRawSource() + "</code></pre>");
//        BindUtils.postNotifyChange(null, null, this, "selectedCommandsSource", "selectedCommandsRaw");
//    }

    private void changeRawSource(String label) {
        if ("commandsHistory".equals(activeTab)) {

        }
        commandsModel.setSelectedCommandsSourceLabel(label);
        commandsModel.setSelectedCommandsSourceRaw("<pre><code>" + commandsModel.getCommandsSources().get(label).getRawSource() + "</code></pre>");
        BindUtils.postNotifyChange(this, "selectedCommandsSource", "selectedCommandsRaw");
    }


    private String getCommandToolbarButtonId(String label) {
        return label.replaceAll("[^a-zA-Z0-9]", "") + "Id";
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
        toolbarbutton.setStyle("font-weight: " + fontWeight);
    }


    //  COMMON METHODS ================


    private String getCommandWithoutBreaks(String commandToExecuteEditable) {
        return commandToExecuteEditable.replaceAll("\\n", " ");
    }


    //  COMMANDS GETTERS AND SETTERS ================


    public CommandsFilter getFilter() {
        return commandsModel.getFilter();
    }

    public String getCommandOutputTotalItems() {
        return String.format("Total Items: %d", commandsResults.size());
    }

    public String getCommandsTotalItems() {
        return String.format("Total Items: %d", commandsResults.size());
    }

    public String getCommandToExecute() {
        return commandsModel.getCommandToExecute();
    }

    public void setCommandToExecute(String commandToExecute) {
        commandsModel.setCommandToExecute(commandToExecute);
    }

    public int getFontSize() {
        return fontSize;
    }

    public String getFontSizeCss() {
        return String.format(fontSizeCss, fontSize);
    }

    public String getCommandsGridHeight() {
        return centerLayoutHeight * 0.38 + "px";
    }

    public String getFullCommandBoxHeight() {
        return centerLayoutHeight * 0.07 + "px";
    }

    public String getCommandOutputHeight() {
        return centerLayoutHeight * 0.43 + "px";
    }

    public String getExecutedCommandOutput() {
        return commandsModel.getExecutedCommandOutput();
    }

    public String getCommandToExecuteEditable() {
        return commandsModel.getCommandToExecuteEditable();
    }

    public void setCommandToExecuteEditable(String commandToExecuteEditable) {
        commandsModel.setCommandToExecuteEditable(commandToExecuteEditable);
    }

    public Set<String> getNamespaces() {
        return commandsModel.getNamespaces();
    }

    public Set<String> getNamespacedPods() {
        return commandsModel.getNamespacedPods();
    }

    public Set<String> getNamespacedDeployments() {
        return commandsModel.getNamespacedDeployments();
    }

    public Set<String> getNamespacedStatefulSets() {
        return commandsModel.getNamespacedStatefulSets();
    }

    public Set<String> getNamespacedReplicaSets() {
        return commandsModel.getNamespacedReplicaSets();
    }

    public Set<String> getNamespacedDaemonSets() {
        return commandsModel.getNamespacedDaemonSets();
    }

    public Set<String> getNamespacedConfigMaps() {
        return commandsModel.getNamespacedConfigMaps();
    }

    public Set<String> getNamespacedServices() {
        return commandsModel.getNamespacedServices();
    }

    public Set<String> getNamespacedJobs() {
        return commandsModel.getNamespacedJobs();
    }

    public String getSelectedNamespace() {
        return commandsModel.getSelectedNamespace();
    }

    public void setSelectedNamespace(String selectedNamespace) {
        commandsModel.setSelectedNamespace(selectedNamespace);
    }

    public Set<String> getSelectedDeployments() {
        return commandsModel.getSelectedDeployments();
    }

    public Set<String> getSelectedStatefulSets() {
        return commandsModel.getSelectedStatefulSets();
    }

    public Set<String> getSelectedReplicaSets() {
        return commandsModel.getSelectedReplicaSets();
    }

    public Set<String> getSelectedDaemonSets() {
        return commandsModel.getSelectedDaemonSets();
    }

    public Set<String> getSelectedConfigMaps() {
        return commandsModel.getSelectedConfigMaps();
    }

    public Set<String> getSelectedServices() {
        return commandsModel.getSelectedServices();
    }

    public Set<String> getSelectedJobs() {
        return commandsModel.getSelectedJobs();
    }

    public Set<String> getSelectedPods() {
        return commandsModel.getSelectedPods();
    }

    public void setSelectedPods(Set<String> selectedPods) {
        commandsModel.setSelectedPods(selectedPods);
    }

    public void setSelectedDeployments(Set<String> selectedDeployments) {
        commandsModel.setSelectedDeployments(selectedDeployments);
    }

    public void setSelectedStatefulSets(Set<String> selectedStatefulSets) {
        commandsModel.setSelectedStatefulSets(selectedStatefulSets);
    }

    public void setSelectedReplicaSets(Set<String> selectedReplicaSets) {
        commandsModel.setSelectedReplicaSets(selectedReplicaSets);
    }

    public void setSelectedDaemonSets(Set<String> selectedDaemonSets) {
        commandsModel.setSelectedDaemonSets(selectedDaemonSets);
    }

    public void setSelectedConfigMaps(Set<String> selectedConfigMaps) {
        commandsModel.setSelectedConfigMaps(selectedConfigMaps);
    }

    public void setSelectedServices(Set<String> selectedServices) {
        commandsModel.setSelectedServices(selectedServices);
    }

    public void setSelectedJobs(Set<String> selectedJobs) {
        commandsModel.setSelectedJobs(selectedJobs);
    }

    public String getSelectedShell() {
        return commandsModel.getSelectedShell();
    }

    public void setSelectedShell(String selectedShell) {
        commandsModel.setSelectedShell(selectedShell);
    }

    public List<String> getShells() {
        return commandsModel.getShells();
    }

    public boolean isHotReplacementEnabled() {
        return config.getCommandsHotReplacement();
    }

    public void setHotReplacementEnabled(boolean hotReplacement) {
        config.setCommandsHotReplacement(hotReplacement);
    }


    //  COMMANDS MANAGEMENT GETTERS AND SETTERS ================


    public String getSelectedCommandsSourceRaw() {
        return commandsModel.getSelectedCommandsSourceRaw();
    }

    public String getSelectedCommandsSourceLabel() {
        return commandsModel.getSelectedCommandsSourceLabel();
    }

    public void setSelectedCommandsSourceLabel(String selectedCommandsSource) {
        commandsModel.setSelectedCommandsSourceLabel(selectedCommandsSource);
    }

    public String getCommandsManagementHeight() {
        return centerLayoutHeight - 50 + "px";
    }

    public String getCommandsManagementSrcPanelHeight() {
        return centerLayoutHeight - 95 + "px";
    }

    public void setGitUrl(String gitUrl) {
        config.setGitUrl(gitUrl);
    }

    public String getGitUrl() {
        return config.getGitUrl();
    }

    public void setGitUsername(String gitUsername) {
        config.setGitUsername(gitUsername);
    }

    public String getGitUsername() {
        return config.getGitUsername();
    }

    public void setGitPassword(String gitPassword) {
        config.setGitPassword(gitPassword);
    }

    public String getGitPassword() {
        return config.getGitPassword();
    }

    public boolean isMarkCredentials() {
        return config.getMarkCredentials();
    }

    public void setMarkCredentials(boolean markCredentials) {
        config.setMarkCredentials(markCredentials);
    }


    //  COMMANDS HISTORY GETTERS AND SETTERS ================

    public String getSelectedCommandsHistoryRaw() {
        return commandsModel.getSelectedCommandsSourceRaw();
    }

    public String getSelectedCommandsHistoryLabel() {
        return commandsModel.getSelectedCommandsSourceLabel();
    }

    public void setSelectedCommandsHistoryLabel(String selectedCommandsHistory) {
        commandsModel.setSelectedCommandsHistoryLabel(selectedCommandsHistory);
    }

    public String getCommandsHistorySrcPanelHeight() {
        return centerLayoutHeight - 95 + "px";
    }

    public String getCommandsHistoryHeight() {
        return centerLayoutHeight - 50 + "px";
    }

    public String getSelectedCommandsHistoryRange() {
        return commandsModel.getSelectedCommandsHistoryRange();
    }

    public void setSelectedCommandsHistoryRange(String selectedCommandsHistoryRange) {
        commandsModel.setSelectedCommandsHistoryRange(selectedCommandsHistoryRange);
    }

    public List<String> getCommandsHistoryRanges() {
        return commandsModel.getCommandsHistoryRanges();
    }

    public boolean isShowOnlyCommandsInHistory() {
        return commandsModel.isShowOnlyCommandsInHistory();
    }

    public void setShowOnlyCommandsInHistory(boolean showOnlyCommandsInHistory) {
        commandsModel.setShowOnlyCommandsInHistory(showOnlyCommandsInHistory);
    }

}
