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
import org.zkoss.zul.Tabpanels;
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
 * Class for displaying/management and view history for commands.
 * ViewModel initializes ..kubehelper/pages/commands.zul
 *
 * @author JDev
 */
@VariableResolver(DelegatingVariableResolver.class)
public class CommandsVM implements EventListener<Event> {

    private static Logger logger = LoggerFactory.getLogger(CommandsVM.class);

    private String activeTab = "commands";
    private boolean isOnInit = true;

    private int commandsOutputFontSize = 14;

    private int commandsHistoryFontSize = 14;

    private int commandsManagementFontSize = 14;

    private boolean wordWrapCommandsInHistory;

    private int centerLayoutHeight = 700;

    private ListModelList<CommandsResult> commandsResults = new ListModelList<>();

    private CommandsModel model;

    @WireVariable
    private CommonService commonService;

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

    @Wire("#commandsTabPanels")
    private Tabpanels notificationContainer;

    @Init
    @NotifyChange("*")
    public void init() {
        model = new CommandsModel();
        onInitPreparations();
    }

    /**
     * Calls after UI render.
     * <p>
     * Explanation:
     * Selectors.wireComponents() in order to be able to @Wire GUI components.
     * Selectors.wireEventListeners() in order to be able to work with listeners and events.
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
        commandsService.parsePredefinedCommands(model);
        commandsService.parseUserCommands(model);
        commandsResults = new ListModelList<>(model.getCommandsResults());
        model.setNamespaces(model.getNamespaces().isEmpty() ? Set.copyOf(commonService.getAllNamespacesWithoutAll()) : model.getNamespaces());
        logger.debug("Found {} namespaces.", model.getNamespaces());
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
        if ("commandsHistory".equals(activeTab) && StringUtils.isBlank(model.getSelectedCommandsHistoryRaw())) {
            commandsService.prepareCommandsHistory(model);
            redrawCommandsToolbarbuttons("commandsHistoriesToolbarID", model.getCommandsHistoriesSortedList(), getCommandToolbarButtonId(model.getSelectedCommandsHistoryLabel()));
            refreshHistoryOutput();
        } else if ("commandsManagement".equals(activeTab) && StringUtils.isBlank(model.getSelectedCommandsSourceRaw())) {
            commandsService.prepareCommandsManagement(model);
            redrawCommandsToolbarbuttons("commandsSourcesToolbarID", model.getCommandsSourcesNamesSortedList(), getCommandToolbarButtonId(model.getSelectedCommandsSourceLabel()));
            refreshCommandsManagementOutput(model.getSelectedCommandsSourceLabel());
            disableEnableMainControlButtons();
        }
        checkRuntimeNotificationExceptions();
    }

    /**
     * Checks if in commands model exists runtime exceptions and shows notification.
     */
    private void checkRuntimeNotificationExceptions() {
        if (StringUtils.isNotBlank(model.getRuntimeNotificationExceptions())) {
            Notification.show(model.getRuntimeNotificationExceptions(), "error", notificationContainer, "bottom_right", 5000);
        }
    }

    /**
     * Recreates and prepares toolbarbuttons after refresh or first start.
     *
     * @param toolbarId             - toolbar id.
     * @param entries               - set with button labels.
     * @param activeToolbarButtonId - sets active toolbarbutton if history exists.
     */
    private void redrawCommandsToolbarbuttons(String toolbarId, List<String> entries, String activeToolbarButtonId) {
        createCommandsToolbarButtons(toolbarId, entries);
        if (StringUtils.isNotBlank(model.getSelectedCommandsHistoryLabel()) || StringUtils.isNotBlank(model.getSelectedCommandsSourceLabel())) {
            enableDisableMenuItem(activeToolbarButtonId, true, "bold;");
        }
    }


    //  COMMANDS METHODS ================


    @Command
    @NotifyChange({"executedCommandOutput"})
    public void run() {
        if (StringUtils.isBlank(model.getCommandToExecuteEditable())) {
            Notification.show("Please select or put the command for execute.", "warning", notificationContainer, "top_right", 3000);
            return;
        }
        commandsService.run(model);
        highlightCommandOutputBlock();
    }


    /**
     * Filters searches and refresh total items label and search results view.
     */
    @Command
    @NotifyChange({"commandsTotalItems", "commandsResults"})
    public void filterCommands() {
        commandsResults.clear();
        for (CommandsResult commandeResult : model.getCommandsResults()) {
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
        model.setCommandToExecute(getCommandWithoutUnnecessaryWhitespaces(item.getCommand()));
        model.setCommandToExecuteEditable(item.getCommand());
    }


    /**
     * Synchronizes command to execute and hot replacement on full command textbox onChange event.
     */
    @Command
    public void synchronizeCommandToExecute() {
        String command = getCommandWithoutUnnecessaryWhitespaces(model.getCommandToExecuteEditable());
        if (StringUtils.isNotBlank(model.getSelectedNamespace())) {
            command = command.replace("$MY_NAMESPACE", model.getSelectedNamespace());
        }
        model.setCommandToExecute(command);

        BindUtils.postNotifyChange(this, "commandToExecute", "commandToExecuteEditable");
    }
    

    /**
     * Replaces \n with spaces in commad.
     *
     * @param commandToExecuteEditable - editable command to execute.
     * @return - replaced string without Unnecessary whitespaces.
     */
    private String getCommandWithoutUnnecessaryWhitespaces(String commandToExecuteEditable) {
        return commandToExecuteEditable.replaceAll("\\n", " ").replaceAll(" +", " ").trim();
    }


    /**
     * Shows popup window with executed command output.
     */
    @Command
    public void commandOutputFullSize() {
        if (StringUtils.isAnyEmpty(model.getCommandToExecute(), model.getExecutedCommandOutput())) {
            Notification.show("Cannot open full screen mode if command is not executed.", "warning", notificationContainer, "top_right", 3000);
            return;
        }
        Map<String, String> params = Map.of("title", "Command Output", "command", model.getCommandToExecute(), "commandOutput", model.getExecutedCommandOutput());
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
            Notification.show("Nothing found.", "info", commandsGridFooter, "before_end", 2000);
        }
        if (isOnInit && !commandsResults.isEmpty()) {
            Notification.show("Loaded: " + commandsResults.size() + " items", "info", commandsGridFooter, "before_end", 2000);
        }
        if (isOnInit && model.hasBuildErrors()) {
            Window window = (Window) Executions.createComponents(Global.PATH_TO_ERROR_RESOURCE_ZUL, null, Map.of("errors", model.getBuildExceptions()));
            window.doModal();
            model.setBuildExceptions(new ArrayList<>());
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

        if (StringUtils.isBlank(model.getExecutedCommandOutput())) {
            Notification.show("Command execution output is empty.", "info", commandOutputGrBox, "before_end", 3000);
        }

        highlightBlock.appendChild(new Html("<pre><code>" + model.getExecutedCommandOutput() + "</code></pre>"));
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
    public void saveCommands(@BindingParam("commands") String commands) {
        commandsService.updateCommands(model, commands);
        if (checkExceptions()) {
            Notification.show(String.format("The Commands %s was successfully saved.", model.getSelectedCommandsSourceLabel()), "info", notificationContainer, "bottom_right", 4000);
        }
        BindUtils.postNotifyChange(this, ".");
    }

    /**
     * Refreshes commands history output div.
     */
    public void refreshCommandsManagementOutput(String label) {
        Div historyOutputBlock = (Div) Path.getComponent("//indexPage/templateInclude/commandsManagementOutputId");
        historyOutputBlock.getChildren().clear();
        boolean isReadonly = !model.getCommandsSources().get(label).isReadonly();
        String preDiv = "<div id = \"editableCommandsManagementOutputId\" width=\"100%\" height=\"100%\" class=\"input\" contenteditable=\"" + isReadonly + "\">" +
                "<pre><code class=\"toml\">" + model.getSelectedCommandsSourceRaw() + "</code></pre></div>";
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
        commandsService.addNewCommandsFile(model, newCommandsFilePath);
        if (checkExceptions()) {
            Notification.show(String.format("The Commands %s was successfully created.", newCommandsFilePath), "info", notificationContainer, "bottom_right", 5000);
            refreshCommandsManagement();
        }
    }

    @Command
    public void deleteCommandsFile() {
        Messagebox.show(String.format("Are you sure you want to delete the file %s with commands?", model.getSelectedSourceFileResult().getFilePath()),
                "Confirmation", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION,
                (EventListener) e -> {
                    if (Messagebox.ON_OK.equals(e.getName())) {
                        if (Files.deleteIfExists(Paths.get(model.getSelectedSourceFileResult().getFilePath()))) {
                            Notification.show(String.format("The Commands file %s was successfully deleted.",
                                    model.getSelectedSourceFileResult().getFilePath()), "info", notificationContainer, "bottom_right", 3000);
                            refreshCommandsManagement();
                        } else {
                            Notification.show(String.format("An error occurred while deleting the file %s.",
                                    model.getSelectedSourceFileResult().getFilePath()), "warning", notificationContainer, "bottom_right", 3000);
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
        commandsService.showOnlyCommandsInHistory(model, checkbox.isChecked());
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
        historyOutputBlock.appendChild(new Html("<pre " + style + "><code>" + model.getSelectedCommandsHistoryRaw() + "</code></pre>"));
        BindUtils.postNotifyChange(this, ".");
    }


    /**
     * Changes commands history raw depends on selected Range in commandsModel.
     */
    @Command
    public void changeHistoryRaw() {
        commandsService.changeHistoryRaw(model);
        refreshHistoryOutput();
    }


    /**
     * Refreshes commands history files and content.
     */
    @Command
    public void refreshHistory() {
        model.setCommandsHistories(new HashMap<>());
        commandsService.prepareCommandsHistory(model);
        redrawCommandsToolbarbuttons("commandsHistoriesToolbarID", model.getCommandsHistoriesSortedList(), getCommandToolbarButtonId(model.getSelectedCommandsHistoryLabel()));
        refreshHistoryOutput();
        BindUtils.postNotifyChange(this, ".");
    }

    /**
     * Refreshes commands sources files and content.
     */
    @Command
    public void refreshCommandsManagement() {
        model.setCommandsSources(new HashMap<>());
        commandsService.prepareCommandsManagement(model);
        redrawCommandsToolbarbuttons("commandsSourcesToolbarID", model.getCommandsSourcesNamesSortedList(), getCommandToolbarButtonId(model.getSelectedCommandsSourceLabel()));
//        Notification.show("Commands management state has been updated.", "info", notificationContainer, "top_right", 3000);
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
            oldToolbarbuttonId = getCommandToolbarButtonId(model.getSelectedCommandsHistoryLabel());
            model.setSelectedCommandsHistoryLabel(label);
            refreshHistoryRangeCombobox();
            commandsService.changeHistoryRaw(model);
            refreshHistoryOutput();
        } else if ("commandsManagement".equals(activeTab)) {
            oldToolbarbuttonId = getCommandToolbarButtonId(model.getSelectedCommandsSourceLabel());
            model.setSelectedCommandsSourceLabel(label);
            commandsService.changeCommandsManagementRaw(model);
            refreshCommandsManagementOutput(label);
            disableEnableMainControlButtons();
        }
        String newToolbarbuttonId = getCommandToolbarButtonId(label);
        enableDisableMenuItem(oldToolbarbuttonId, false, "normal;");
        enableDisableMenuItem(newToolbarbuttonId, true, "bold;");
    }

    public void disableEnableMainControlButtons() {
        boolean isDisable = model.getCommandsSources().get(model.getSelectedCommandsSourceLabel()).isReadonly();
        saveBtn.setDisabled(isDisable);
        minusBtn.setDisabled(isDisable);
    }

    private void refreshHistoryRangeCombobox() {
        commandsHistoryRangesCbox.setValue("");
        model.setSelectedCommandsHistoryRange("");
    }


    /**
     * Creates toolbarbuttons on the commands management and commands history tabs for view sources/files.
     */
    private void createCommandsToolbarButtons(String toolbarId, List<String> sources) {
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
        if (model.hasBuildErrors()) {
            Window window = (Window) Executions.createComponents(Global.PATH_TO_ERROR_RESOURCE_ZUL, null, Map.of("errors", model.getBuildExceptions()));
            window.doModal();
            model.setBuildExceptions(new ArrayList<>());
            return false;
        }
        return true;
    }


    //  COMMANDS GETTERS AND SETTERS ================


    public CommandsFilter getFilter() {
        return model.getFilter();
    }

    public String getCommandsTotalItems() {
        return String.format("Total Items: %d", commandsResults.size());
    }

    public String getCommandToExecute() {
        return model.getCommandToExecute();
    }

    public void setCommandToExecute(String commandToExecute) {
        model.setCommandToExecute(commandToExecute);
    }

    public int getCommandsOutputFontSize() {
        return commandsOutputFontSize;
    }

    public String getCommandsOutputFontSizeCss() {
        return String.format("font-size: %spx;", commandsOutputFontSize);
    }

    public String getCommandsGridHeight() {
        return centerLayoutHeight * 0.35 + "px";
    }

    public String getFullCommandBoxHeight() {
        return centerLayoutHeight * 0.05 + "px";
    }

    public String getCommandOutputHeight() {
        return centerLayoutHeight > 1200 ? centerLayoutHeight * 0.49 + "px" : centerLayoutHeight * 0.46 + "px";
    }

    public String getExecutedCommandOutput() {
        return model.getExecutedCommandOutput();
    }

    public String getCommandToExecuteEditable() {
        return model.getCommandToExecuteEditable();
    }

    public void setCommandToExecuteEditable(String commandToExecuteEditable) {
        model.setCommandToExecuteEditable(commandToExecuteEditable);
    }

    public Set<String> getNamespaces() {
        return model.getNamespaces();
    }

    public Set<String> getNamespacedPods() {
        return model.getNamespacedPods();
    }

    public Set<String> getNamespacedDeployments() {
        return model.getNamespacedDeployments();
    }

    public Set<String> getNamespacedStatefulSets() {
        return model.getNamespacedStatefulSets();
    }

    public Set<String> getNamespacedReplicaSets() {
        return model.getNamespacedReplicaSets();
    }

    public Set<String> getNamespacedDaemonSets() {
        return model.getNamespacedDaemonSets();
    }

    public Set<String> getNamespacedConfigMaps() {
        return model.getNamespacedConfigMaps();
    }

    public Set<String> getNamespacedServices() {
        return model.getNamespacedServices();
    }

    public Set<String> getNamespacedJobs() {
        return model.getNamespacedJobs();
    }

    public String getSelectedNamespace() {
        return model.getSelectedNamespace();
    }

    public void setSelectedNamespace(String selectedNamespace) {
        model.setSelectedNamespace(selectedNamespace);
    }

    public Set<String> getSelectedDeployments() {
        return model.getSelectedDeployments();
    }

    public Set<String> getSelectedStatefulSets() {
        return model.getSelectedStatefulSets();
    }

    public Set<String> getSelectedReplicaSets() {
        return model.getSelectedReplicaSets();
    }

    public Set<String> getSelectedDaemonSets() {
        return model.getSelectedDaemonSets();
    }

    public Set<String> getSelectedConfigMaps() {
        return model.getSelectedConfigMaps();
    }

    public Set<String> getSelectedServices() {
        return model.getSelectedServices();
    }

    public Set<String> getSelectedJobs() {
        return model.getSelectedJobs();
    }

    public Set<String> getSelectedPods() {
        return model.getSelectedPods();
    }

    public void setSelectedPods(Set<String> selectedPods) {
        model.setSelectedPods(selectedPods);
    }

    public void setSelectedDeployments(Set<String> selectedDeployments) {
        model.setSelectedDeployments(selectedDeployments);
    }

    public void setSelectedStatefulSets(Set<String> selectedStatefulSets) {
        model.setSelectedStatefulSets(selectedStatefulSets);
    }

    public void setSelectedReplicaSets(Set<String> selectedReplicaSets) {
        model.setSelectedReplicaSets(selectedReplicaSets);
    }

    public void setSelectedDaemonSets(Set<String> selectedDaemonSets) {
        model.setSelectedDaemonSets(selectedDaemonSets);
    }

    public void setSelectedConfigMaps(Set<String> selectedConfigMaps) {
        model.setSelectedConfigMaps(selectedConfigMaps);
    }

    public void setSelectedServices(Set<String> selectedServices) {
        model.setSelectedServices(selectedServices);
    }

    public void setSelectedJobs(Set<String> selectedJobs) {
        model.setSelectedJobs(selectedJobs);
    }

    public String getSelectedShell() {
        return model.getSelectedShell();
    }

    public void setSelectedShell(String selectedShell) {
        model.setSelectedShell(selectedShell);
    }

    public List<String> getShells() {
        return model.getShells();
    }


    //  COMMANDS MANAGEMENT GETTERS AND SETTERS ================


    public String getSelectedCommandsSourceRaw() {
        return model.getSelectedCommandsSourceRaw();
    }

    public String getSelectedCommandsSourceLabel() {
        return model.getSelectedCommandsSourceLabel();
    }

    public void setSelectedCommandsSourceLabel(String selectedCommandsSource) {
        model.setSelectedCommandsSourceLabel(selectedCommandsSource);
    }

    public String getCommandsManagementHeight() {
        return centerLayoutHeight - 50 + "px";
    }

    public String getCommandsManagementSrcPanelHeight() {
        return centerLayoutHeight - 75 + "px";
    }

    public String getCommandsManagementCss() {
        return String.format("font-size: %spx;", commandsManagementFontSize);
    }


    //  COMMANDS HISTORY GETTERS AND SETTERS ================


    public String getSelectedCommandsHistoryRaw() {
        return model.getSelectedCommandsSourceRaw();
    }

    public String getSelectedCommandsHistoryLabel() {
        return model.getSelectedCommandsHistoryLabel();
    }

    public void setSelectedCommandsHistoryLabel(String selectedCommandsHistory) {
        model.setSelectedCommandsHistoryLabel(selectedCommandsHistory);
    }

    public String getCommandsHistorySrcPanelHeight() {
        return centerLayoutHeight - 75 + "px";
    }

    public String getCommandsHistoryHeight() {
        return centerLayoutHeight - 50 + "px";
    }

    public String getSelectedCommandsHistoryRange() {
        return model.getSelectedCommandsHistoryRange();
    }

    public void setSelectedCommandsHistoryRange(String selectedCommandsHistoryRange) {
        model.setSelectedCommandsHistoryRange(selectedCommandsHistoryRange);
    }

    public List<String> getCommandsHistoryRanges() {
        return model.getCommandsHistoryRanges();
    }

    public boolean isShowOnlyCommandsInHistory() {
        return model.isShowOnlyCommandsInHistory();
    }

    public void setShowOnlyCommandsInHistory(boolean showOnlyCommandsInHistory) {
        model.setShowOnlyCommandsInHistory(showOnlyCommandsInHistory);
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
        return String.format("font-size: %spx;", commandsHistoryFontSize);
    }

}
