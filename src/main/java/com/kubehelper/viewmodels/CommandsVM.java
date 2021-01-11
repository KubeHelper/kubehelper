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
import org.zkoss.bind.BindContext;
import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.GlobalCommand;
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
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zk.ui.util.Notification;
import org.zkoss.zkplus.spring.DelegatingVariableResolver;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Footer;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Html;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Slider;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Vbox;
import org.zkoss.zul.Window;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
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

    private final String commandsOutputFontSizeCss = "font-size: %spx;";
    private int commandsOutputFontSize = 14;

    private final String commandsHistoryCss = "font-size: %spx;";
    private int commandsHistoryFontSize = 14;

    private final String commandsManagementCss = "font-size: %spx;";
    private int commandsManagementFontSize = 14;

    private boolean wordWrapCommandsInHistory;

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
    private Combobox commandsHistoryRangesCbox;

    @Wire
    private Button saveBtn;

    @Wire
    private Button minusBtn;

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
    }

    @Listen("onAfterSize=#centerLayoutCommandsID")
    public void onAfterSizeCenter(AfterSizeEvent event) {
        centerLayoutHeight = event.getHeight() - 3;
        BindUtils.postNotifyChange(this, ".");
    }

    /**
     * Prepare commands view.
     * Parse predefined and user commands an pull namespaces.
     */
    private void onInitPreparations() {
        commandsService.parsePredefinedCommands(commandsModel);
        commandsService.parseUserCommands(commandsModel);
        commandsResults = new ListModelList<>(commandsModel.getCommandsResults());
        commandsModel.setNamespaces(commandsModel.getNamespaces().isEmpty() ? Set.copyOf(commonService.getAllNamespacesWithoutAll()) : commandsModel.getNamespaces());
        logger.info("Found {} namespaces.", commandsModel.getNamespaces());
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
    public void onSelectMainCommandsTabs(@ContextParam(ContextType.COMPONENT) Tabbox tabbox) {
        activeTab = tabbox.getSelectedTab().getId();
        if ("commandsHistory".equals(activeTab) && StringUtils.isBlank(commandsModel.getSelectedCommandsHistoryRaw())) {
            commandsService.prepareCommandsHistory(commandsModel);
            redrawCommandsToolbarbuttons("commandsHistoriesToolbarID", commandsModel.getCommandsHistories().keySet(), getCommandToolbarButtonId(commandsModel.getSelectedCommandsHistoryLabel()));
            refreshHistoryOutput();
        } else if ("commandsManagement".equals(activeTab) && StringUtils.isBlank(commandsModel.getSelectedCommandsSourceRaw())) {
            commandsService.prepareCommandsManagement(commandsModel);
            redrawCommandsToolbarbuttons("commandsSourcesToolbarID", commandsModel.getCommandsSources().keySet(), getCommandToolbarButtonId(commandsModel.getSelectedCommandsSourceLabel()));
            refreshCommandsManagementOutput(commandsModel.getSelectedCommandsSourceLabel());
            disableEnableMainControlButtons();
        }
        checkRuntimeNotificationExceptions();
    }

    /**
     * Checks if in commands model exists runtime exceptions and shows notification.
     */
    private void checkRuntimeNotificationExceptions() {
        if (StringUtils.isNotBlank(commandsModel.getRuntimeNotificationExceptions())) {
            Notification.show(commandsModel.getRuntimeNotificationExceptions(), "error", null, "bottom_right", 5000);
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
        createCommandsToolbarButtons(toolbarId, entries);
        if (StringUtils.isNotBlank(commandsModel.getSelectedCommandsHistoryLabel()) || StringUtils.isNotBlank(commandsModel.getSelectedCommandsSourceLabel())) {
            enableDisableMenuItem(activeToolbarButtonId, true, "bold;");
        }
    }


    //  COMMANDS METHODS ================


    @Command
    @NotifyChange({"executedCommandOutput"})
    public void run() {
        if (StringUtils.isBlank(commandsModel.getCommandToExecuteEditable())) {
            Notification.show("Please select or put the command for execute.", "info", commandOutputGrBox, "bottom_right", 3000);
            return;
        }
        commandsService.run(commandsModel);
        highlightCommandOutputBlock();
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
                    commonService.checkEqualsFilter(commandeResult.getFile(), getFilter().getSelectedFileFilter()) &&
                    StringUtils.containsIgnoreCase(commandeResult.getName(), getFilter().getName()) &&
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
        commandsModel.setCommandToExecute(getCommandWithoutUnnecessaryWhitespaces(item.getCommand()));
        commandsModel.setCommandToExecuteEditable(item.getCommand());
    }


    /**
     * Synchronizes command to execute and hot replacement on full command textbox onChange event.
     */
    @Command
    public void synchronizeCommandToExecute() {
        commandsModel.setCommandToExecute(getCommandWithoutUnnecessaryWhitespaces(commandsModel.getCommandToExecuteEditable()));
//        if (isHotReplacementEnabled()) {
//            commandsService.commandHotReplacement(commandsModel);
//        }
        BindUtils.postNotifyChange(this, "commandToExecute", "commandToExecuteEditable");
    }

    /**
     * Replaces \n with spaces in commad.
     *
     * @param commandToExecuteEditable - editable command to execute.
     * @return - replaced string without Unnecessary whitespaces.
     */
    private String getCommandWithoutUnnecessaryWhitespaces(String commandToExecuteEditable) {
//        return commandToExecuteEditable.replaceAll("\\n", " ");
//        TODO correct replacement
        return commandToExecuteEditable.replaceAll("\\n", " ").replaceAll(" +", " ");
    }


    /**
     * Changes resources in comboxex depend on namespace in commands window.
     */
//    @Command
//    public void changeResourcesInComboxexDependOnNamespace() {
//        commandsService.fetchResourcesDependsOnNamespace(commandsModel);
//        BindUtils.postNotifyChange(this, "namespacedPods", "namespacedDeployments", "namespacedStatefulSets", "namespacedReplicaSets", "namespacedDaemonSets", "namespacedConfigMaps", "namespacedServices", "namespacedJobs");
//        Notification.show(String.format("New resources for %s namespace, successfully fetched.", commandsModel.getSelectedNamespace()), "info", commandOutputGrBox, "bottom_right", 2000);
//    }

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
    private void highlightCommandOutputBlock() {
        Div highlightBlock = (Div) Path.getComponent("//indexPage/templateInclude/commandOutputId");
        highlightBlock.getChildren().clear();

        if (StringUtils.isBlank(commandsModel.getExecutedCommandOutput())) {
            Notification.show("Command execution output is empty.", "info", commandOutputGrBox, "bottom_right", 3000);
        }

        highlightBlock.appendChild(new Html("<pre><code>" + commandsModel.getExecutedCommandOutput() + "</code></pre>"));
        BindUtils.postNotifyChange(this, ".");
    }

    /**
     * Changes font size in command output panel.
     *
     * @param ctxEvent - onScroll event.
     */
    @Command
    public void commandsOutputChangeFontSize(BindContext ctxEvent) {
        Slider fontSlider = (Slider) ctxEvent.getComponent();
        commandsOutputFontSize = fontSlider.getCurpos();
        BindUtils.postNotifyChange(this, "commandsOutputFontSizeCss", "commandsHistoryCss");
    }


    //  COMMANDS MANAGEMENT METHODS ================

    @Command
    public void pullGitRepo() {
        commonService.pullGitRepo();
    }

    @Command
    public void pushGitRepo() {
        commonService.pushGitRepo();
    }

    @Command
    public void saveCommands(@BindingParam("commands") String commands) {
        commandsService.updateCommands(commandsModel, commands);
        if (checkExceptions()) {
            Notification.show(String.format("The Commands %s was successfully saved.", commandsModel.getSelectedCommandsSourceLabel()), "info", null, "before_end", 4000);
        }
        BindUtils.postNotifyChange(this, ".");
    }

    /**
     * Refreshes commands history output div.
     */
    public void refreshCommandsManagementOutput(String label) {
        Div historyOutputBlock = (Div) Path.getComponent("//indexPage/templateInclude/commandsManagementOutputId");
        historyOutputBlock.getChildren().clear();
        boolean isReadonly = !commandsModel.getCommandsSources().get(label).isReadonly();
        String preDiv = "<div id = \"editableCommandsManagementOutputId\" width=\"100%\" height=\"100%\" class=\"input\" contenteditable=\"" + isReadonly + "\">" +
                "<pre><code class=\"toml\">" + commandsModel.getSelectedCommandsSourceRaw() + "</code></pre></div>";
        historyOutputBlock.appendChild(new Html(preDiv));
        Clients.evalJavaScript("highlightCommandManagement();");
        BindUtils.postNotifyChange(this, ".");
    }

    @Command
    public void addNewCommandsFileWindow() {
        Window window = (Window) Executions.createComponents("~./zul/kubehelper/components/new-commands-modal.zul", null, null);
        window.doModal();
    }

    @GlobalCommand
    public void addNewCommandsFile(@BindingParam("newCommandsFilePath") String newCommandsFilePath) {
        commandsService.addNewCommandsFile(commandsModel, newCommandsFilePath);
        if (checkExceptions()) {
            Notification.show(String.format("The Commands %s was successfully created.", newCommandsFilePath), "info", null, "bottom_left", 5000);
            refreshCommandsManagement();
        }
    }

    @Command
    public void deleteCommandsFile() {
        Messagebox.show(String.format("Are you sure you want to delete the file %s with commands?", commandsModel.getSelectedSourceFileResult().getFilePath()),
                "Question", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION,
                (EventListener) e -> {
                    if (Messagebox.ON_OK.equals(e.getName())) {
                        if (Files.deleteIfExists(Paths.get(commandsModel.getSelectedSourceFileResult().getFilePath()))) {
                            Notification.show(String.format("The Commands file %s was successfully deleted.",
                                    commandsModel.getSelectedSourceFileResult().getFilePath()), "info", null, "bottom_left", 3000);
                            refreshCommandsManagement();
                        } else {
                            Notification.show(String.format("An error occurred while deleting the file %s.",
                                    commandsModel.getSelectedSourceFileResult().getFilePath()), "warning", null, "bottom_left", 3000);
                        }
                    }
                }
        );
    }


    //  COMMANDS HISTORY METHODS ================


    /**
     * Commands history output filter. Shows only commands without outputs.
     *
     * @param checkbox - checkbox flag to activate/deactivate.
     */
    @Command
    public void showOnlyCommandsInHistory(@ContextParam(ContextType.COMPONENT) Checkbox checkbox) {
        commandsService.showOnlyCommandsInHistory(commandsModel, checkbox.isChecked());
        refreshHistoryOutput();
    }

    /**
     * Commands history output filter. Wraps long lines with css.
     *
     * @param checkbox - checkbox flag to activate/deactivate.
     */
    @Command
    public void wordWrapCommandsInHistory(@ContextParam(ContextType.COMPONENT) Checkbox checkbox) {
        refreshHistoryOutput();
    }


    /**
     * Refreshes commands history output div.
     */
    public void refreshHistoryOutput() {
        Div historyOutputBlock = (Div) Path.getComponent("//indexPage/templateInclude/historyOutputId");
        historyOutputBlock.getChildren().clear();
        String style = wordWrapCommandsInHistory ? "style=\"white-space: pre-wrap; word-break: keep-all;\"" : "";
        historyOutputBlock.appendChild(new Html("<pre " + style + "><code>" + commandsModel.getSelectedCommandsHistoryRaw() + "</code></pre>"));
        BindUtils.postNotifyChange(this, ".");
    }


    /**
     * Changes commands history raw depends on selected Range in commandsModel.
     */
    @Command
    public void changeHistoryRaw() {
        commandsService.changeHistoryRaw(commandsModel);
        refreshHistoryOutput();
    }


    /**
     * Refreshes commands history files and content.
     */
    @Command
    public void refreshHistory() {
        commandsModel.setCommandsHistories(new HashMap<>());
        commandsService.prepareCommandsHistory(commandsModel);
        redrawCommandsToolbarbuttons("commandsHistoriesToolbarID", commandsModel.getCommandsHistories().keySet(), getCommandToolbarButtonId(commandsModel.getSelectedCommandsHistoryLabel()));
        refreshHistoryOutput();
        BindUtils.postNotifyChange(this, ".");
    }

    /**
     * Refreshes commands sources files and content.
     */
    @Command
    public void refreshCommandsManagement() {
        commandsModel.setCommandsSources(new HashMap<>());
        commandsService.prepareCommandsManagement(commandsModel);
        redrawCommandsToolbarbuttons("commandsSourcesToolbarID", commandsModel.getCommandsSources().keySet(), getCommandToolbarButtonId(commandsModel.getSelectedCommandsSourceLabel()));
        Notification.show("Commands management state has been updated.", "info", null, "bottom_right", 3000);
        BindUtils.postNotifyChange(this, ".");
    }


    /**
     * Changes font size in commands history output panel.
     *
     * @param ctxEvent - onScroll event.
     */
    @Command
    public void commandsHistoryChangeFontSize(BindContext ctxEvent) {
        Slider fontSlider = (Slider) ctxEvent.getComponent();
        commandsHistoryFontSize = fontSlider.getCurpos();
        BindUtils.postNotifyChange(this, "commandsHistoryCss");
    }


    //  COMMON METHODS ================

    /**
     * Changes raw source and toolbarbutton state by onClick event.
     *
     * @param event - onClick event.
     */
    private void selectAndChangeSourceToolbarbuttonLabel(Event event) {
        String label = ((Toolbarbutton) event.getTarget()).getLabel();
        String oldToolbarbuttonId = null;
        if ("commandsHistory".equals(activeTab)) {
            oldToolbarbuttonId = getCommandToolbarButtonId(commandsModel.getSelectedCommandsHistoryLabel());
            commandsModel.setSelectedCommandsHistoryLabel(label);
            refreshHistoryRangeCombobox();
            commandsService.changeHistoryRaw(commandsModel);
            refreshHistoryOutput();
        } else if ("commandsManagement".equals(activeTab)) {
            oldToolbarbuttonId = getCommandToolbarButtonId(commandsModel.getSelectedCommandsSourceLabel());
            commandsModel.setSelectedCommandsSourceLabel(label);
            commandsService.changeCommandsManagementRaw(commandsModel);
            refreshCommandsManagementOutput(label);
            disableEnableMainControlButtons();
        }
        String newToolbarbuttonId = getCommandToolbarButtonId(label);
        enableDisableMenuItem(oldToolbarbuttonId, false, "normal;");
        enableDisableMenuItem(newToolbarbuttonId, true, "bold;");
    }

    public void disableEnableMainControlButtons() {
        boolean isDisable = commandsModel.getCommandsSources().get(commandsModel.getSelectedCommandsSourceLabel()).isReadonly();
        saveBtn.setDisabled(isDisable);
        minusBtn.setDisabled(isDisable);
    }

    private void refreshHistoryRangeCombobox() {
        commandsHistoryRangesCbox.setValue("");
        commandsModel.setSelectedCommandsHistoryRange("");
    }


    /**
     * Creates toolbarbuttons on the commands management and commands history tabs for view sources/files.
     */
    private void createCommandsToolbarButtons(String toolbarId, Set<String> sources) {
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
        toolbarbutton.setStyle("text-align: left;font-weight: " + fontWeight);
    }

    private boolean checkExceptions() {
        if (commandsModel.hasBuildErrors()) {
            Window window = (Window) Executions.createComponents(Global.PATH_TO_ERROR_RESOURCE_ZUL, null, Map.of("errors", commandsModel.getBuildExceptions()));
            window.doModal();
            commandsModel.setBuildExceptions(new ArrayList<>());
            return false;
        }
        return true;
    }


    //  COMMANDS GETTERS AND SETTERS ================


    public CommandsFilter getFilter() {
        return commandsModel.getFilter();
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

    public int getCommandsOutputFontSize() {
        return commandsOutputFontSize;
    }

    public String getCommandsOutputFontSizeCss() {
        return String.format(commandsOutputFontSizeCss, commandsOutputFontSize);
    }

    public String getCommandsGridHeight() {
        return centerLayoutHeight * 0.35 + "px";
    }

    public String getFullCommandBoxHeight() {
        return centerLayoutHeight * 0.05 + "px";
    }

    public String getCommandOutputHeight() {
        return centerLayoutHeight * 0.515 + "px";
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


    //  COMMANDS HISTORY GETTERS AND SETTERS ================


    public String getSelectedCommandsHistoryRaw() {
        return commandsModel.getSelectedCommandsSourceRaw();
    }

    public String getSelectedCommandsHistoryLabel() {
        return commandsModel.getSelectedCommandsHistoryLabel();
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

    public boolean isWordWrapCommandsInHistory() {
        return wordWrapCommandsInHistory;
    }

    public void setWordWrapCommandsInHistory(boolean wordWrapCommandsInHistory) {
        this.wordWrapCommandsInHistory = wordWrapCommandsInHistory;
    }

    public int getCommandsHistoryFontSize() {
        return commandsHistoryFontSize;
    }

    public String getCommandsHistoryCss() {
        return String.format(commandsHistoryCss, commandsHistoryFontSize);
    }

    public String getCommandsManagementCss() {
        return String.format(commandsManagementCss, commandsManagementFontSize);
    }

}
