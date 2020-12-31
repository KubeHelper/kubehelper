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

import com.google.common.collect.Iterables;
import com.kubehelper.common.Global;
import com.kubehelper.common.Resource;
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
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zk.ui.util.Notification;
import org.zkoss.zkplus.spring.DelegatingVariableResolver;
import org.zkoss.zul.Auxhead;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Footer;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Html;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Slider;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Vbox;
import org.zkoss.zul.Window;

import javax.tools.Tool;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.StreamSupport;

/**
 * @author JDev
 */
@VariableResolver(DelegatingVariableResolver.class)
public class CommandsVM implements EventListener<Event> {

    private static Logger logger = LoggerFactory.getLogger(CommandsVM.class);

    private boolean isRunButtonPressed;
    private boolean isOnInit = true;

    private final String fontSizeCss = "font-size: %spx;";
    private int fontSize = 14;

    private int centerLayoutHeight = 700;

    private ListModelList<CommandsResult> commandsResults = new ListModelList<>();
    private ListModelList<CommandsResult> commandOutputResults = new ListModelList<>();
    private String fullCommand = "";

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
    private Footer commandOutputGridFooter;

    @Wire
    private Slider fontSizeSlider;

    @Wire
    private Combobox commandsNamespacesCBox;

    @Init
    @NotifyChange("*")
    public void init() {
        commandsModel = (CommandsModel) Global.ACTIVE_MODELS.computeIfAbsent(Global.COMMANDS_MODEL, (k) -> Global.NEW_MODELS.get(Global.COMMANDS_MODEL));
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
        createCommandsToolbarButtons();
//        TODO call method after refresh, components should be in DOM
//        enableDisableMenuItem(commandsModel.getSelectedCommandsSource(), true, "bold;");
    }

    @Listen("onAfterSize=#centerLayoutCommandsID")
    public void onAfterSizeCenter(AfterSizeEvent event) {
        centerLayoutHeight = event.getHeight() - 3;
        BindUtils.postNotifyChange(null, null, this, ".");
    }

    @Command
    @NotifyChange({"commandOutputTotalItems", "commandOutputResults", "executedCommandOutput"})
    public void run() {
        commandsModel.setCommandToExecute(fullCommand);
        commandsService.run(commandsModel);

        highlightBlock();
//        commandsModel.setFilter(new CommandsFilter());
//        clearAllFilterComboboxes();
//        isRunButtonPressed = true;
//        onInitPreparations();
    }

    /**
     * Prepare view for result depends on filters or new searches
     */
    private void onInitPreparations() {
        commandsService.parsePredefinedCommands(commandsModel);
//        commandsService.parseUserCommands(commandsModel);
//        TODO change after
        commandsModel.setSelectedCommandsSourceLabel("Predefined Commands");
        commandsModel.setSelectedCommandsSourceRaw(commandsModel.getCommandsSources().get("Predefined Commands").getRawSource());
        commandsResults = new ListModelList<>(commandsModel.getCommandsResults());
        if (commandsModel.getFilter().isFilterActive() && !commandsModel.getCommandsResults().isEmpty()) {
            filterCommands();
        } else {
            commandsResults = new ListModelList<>(commandsModel.getCommandsResults());
        }
        commandsModel.setNamespaces(commandsModel.getNamespaces().isEmpty() ? Set.copyOf(commonService.getAllNamespaces()) : commandsModel.getNamespaces());
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
                    StringUtils.containsIgnoreCase(commandeResult.getViewCommand(), getFilter().getCommand()) &&
                    StringUtils.containsIgnoreCase(commandeResult.getDescription(), getFilter().getDescription())) {
                commandsResults.add(commandeResult);
            }
        }
    }

    private void createCommandsToolbarButtons() {
        Vbox commandsSourcesToolbarID = (Vbox) Path.getComponent("//indexPage/templateInclude/commandsSourcesToolbarID");
        commandsModel.getCommandsSources().keySet().forEach(key -> {
            Toolbarbutton toolbarbutton = new Toolbarbutton(key);
            toolbarbutton.setId(getCommandToolbarButtonId(key));
            toolbarbutton.setIconSclass("z-icon-file");
            toolbarbutton.addEventListener("onClick", this);
            toolbarbutton.setHflex("1");
            commandsSourcesToolbarID.appendChild(toolbarbutton);
        });
    }


    /**
     * Removes last selected value from all filter comboboxes.
     */
    private void clearAllFilterComboboxes() {
        Auxhead searchGridAuxHead = (Auxhead) Path.getComponent("//indexPage/templateInclude/commandsGridAuxHead");
        for (Component child : searchGridAuxHead.getFellows()) {
            if (Arrays.asList("operationsGroupCBox", "groupsGroupCBox").contains(child.getId())) {
                Combobox cBox = (Combobox) child;
                cBox.setValue("");
            }
        }
    }

    @Command
    @NotifyChange({"fullCommand"})
    public void showFullCommand(@BindingParam("clickedItem") CommandsResult item) {
        this.fullCommand = item.getViewCommand();
    }

    @Override
    @NotifyChange({"namespacedPods", "namespacedDeployments", "namespacedStatefulSets", "namespacedReplicaSets", "namespacedDaemonSets", "namespacedConfigMaps", "namespacedServices", "namespacedJobs"})
    public void onEvent(Event event) {
        if ("onScroll".equals(event.getName())) {
            Slider fontSlider = (Slider) event.getTarget();
            fontSize = fontSlider.getCurpos();
            BindUtils.postNotifyChange(null, null, this, "fontSizeCss");
        } else if ("onClick".equals(event.getName())) {
            String label = ((Toolbarbutton) event.getTarget()).getLabel();
            String oldToolbarbuttonId = getCommandToolbarButtonId(commandsModel.getSelectedCommandsSourceLabel());
            String newToolbarbuttonId = getCommandToolbarButtonId(label);
            enableDisableMenuItem(oldToolbarbuttonId, false, "normal;");
            enableDisableMenuItem(newToolbarbuttonId, true, "bold;");
            changePredefinedCommandsRawSource(label);
        } else if ("onSelect".equals(event.getName())) {
            commandsService.fetchResourcesDependsOnNamespace(commandsModel);
            BindUtils.postNotifyChange(null, null, this, "namespacedPods", "namespacedDeployments", "namespacedStatefulSets", "namespacedReplicaSets", "namespacedDaemonSets", "namespacedConfigMaps", "namespacedServices", "namespacedJobs");
            Notification.show(String.format("New resources for %s namespace, successfully fetched.", commandsModel.getSelectedNamespace()), "info", commandOutputGridFooter, "overlap_after", 2000);
        }
    }

    @Command
    public void commandOutputFullSize() {
        if (StringUtils.isAnyEmpty(fullCommand, commandsModel.getExecutedCommandOutput())) {
            Notification.show("Cannot open full screen mode if command is not executed.", "warning", commandOutputGridFooter, "overlap_after", 3000);
            return;
        }
        Map<String, String> params = Map.of("title", "Command Output", "command", fullCommand, "commandOutput", commandsModel.getExecutedCommandOutput());
        Window window = (Window) Executions.createComponents("~./zul/kubehelper/components/command-output-window.zul", null, params);
        window.doModal();
    }

    //    @NotifyChange({"selectedCommandsSource","selectedCommandsRaw"})
    private void changePredefinedCommandsRawSource(String label) {
//        Html selectedCommandsRaw = (Html) Path.getComponent("//indexPage/templateInclude/selectedCommandsRawId");
        commandsModel.setSelectedCommandsSourceLabel(label);
        commandsModel.setSelectedCommandsSourceRaw("<![CDATA[<pre><code>" + commandsModel.getCommandsSources().get(label).getRawSource() + "</code></pre>]]>");
//        selectedCommandsRaw.setContent(commandsModel.getSelectedCommandsSourceRaw());
        BindUtils.postNotifyChange(null, null, this, ".");
    }

    private String getCommandToolbarButtonId(String label) {
        return label.replaceAll("[^a-zA-Z0-9]", "") + "Id";
    }


    private void enableDisableMenuItem(String toolbarbuttonId, boolean disabled, String fontWeight) {
        Toolbarbutton toolbarbutton = (Toolbarbutton) Path.getComponent("//indexPage/templateInclude/" + toolbarbuttonId);
        toolbarbutton.setDisabled(disabled);
        toolbarbutton.setStyle("font-weight: " + fontWeight);
    }

    /**
     * Returns search results for grid and shows Notification if nothing was found or/and error window if some errors has occurred while parsing the results.
     *
     * @return - search results
     */
    public ListModelList<CommandsResult> getCommandsResults() {
        if (isOnInit && commandsResults.isEmpty()) {
            Notification.show("Nothing found.", "info", commandsGridFooter, "before_end", 2000);
        }
        if (isOnInit && !commandsResults.isEmpty()) {
            Notification.show("Found: " + commandsResults.size() + " items", "info", commandsGridFooter, "before_end", 2000);
        }
        if (isOnInit && commandsModel.hasBuildErrors()) {
            Window window = (Window) Executions.createComponents("~./zul/kubehelper/components/errors.zul", null, Map.of("errors", commandsModel.getBuildExceptions()));
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
        highlightBlock.appendChild(new Html("<pre><code>" + commandsModel.getExecutedCommandOutput() + "</code></pre>"));
        BindUtils.postNotifyChange(null, null, this, ".");
//        Clients.evalJavaScript("highlightCommandOutput();");
    }

    public ListModelList<CommandsResult> getCommandOutputResults() {
//        if (isRunButtonPressed && commandOutputResults.isEmpty()) {
//            Notification.show("Nothing found.", "info", commandOutputGridFooter, "before_end", 2000);
//        }
//        if (isRunButtonPressed && !commandOutputResults.isEmpty()) {
//            Notification.show("Found: " + commandsResults.size() + " items", "info", commandOutputGridFooter, "before_end", 2000);
//        }
//        if (isRunButtonPressed && commandsModel.hasBuildErrors()) {
//            Window window = (Window) Executions.createComponents("~./zul/kubehelper/components/errors.zul", null, Map.of("errors", commandsModel.getBuildExceptions()));
//            window.doModal();
//        }
        isRunButtonPressed = false;
        return commandOutputResults;
    }

    public CommandsFilter getFilter() {
        return commandsModel.getFilter();
    }

    public String getCommandOutputTotalItems() {
        return String.format("Total Items: %d", commandsResults.size());
    }

    public String getCommandsTotalItems() {
        return String.format("Total Items: %d", commandsResults.size());
    }


    public CommandsFilter getCommandsFilter() {
        return commandsModel.getFilter();
    }

    public String getFullCommand() {
        return fullCommand;
    }

    public void setFullCommand(String fullCommand) {
        this.fullCommand = fullCommand;
    }

    public String getSelectedCommandsSourceRaw() {
        return commandsModel.getSelectedCommandsSourceRaw();
    }

    public String getSelectedCommandsSourceLabel() {
        return commandsModel.getSelectedCommandsSourceLabel();
    }

    public void setSelectedCommandsSourceLabel(String selectedCommandsSource) {
        commandsModel.setSelectedCommandsSourceLabel(selectedCommandsSource);
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

    public String getCommandsSrcViewHeight() {
        return centerLayoutHeight - 50 + "px";
    }

    public String getCommandsManagementHeight() {
        return centerLayoutHeight - 50 + "px";
    }

    public String getCommandsManagementSrcPanelHeight() {
        return centerLayoutHeight - 95 + "px";
    }

    public String getCommandsHistoryHeight() {
        return centerLayoutHeight - 50 + "px";
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

    public void setMarkCredentials(@ContextParam(ContextType.COMPONENT) Checkbox markCredentials) {
        config.setMarkCredentials(markCredentials.isChecked());
    }

    public String getExecutedCommandOutput() {
        return commandsModel.getExecutedCommandOutput();
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

    public String getSelectedPod() {
        return commandsModel.getSelectedPod();
    }

    public String getSelectedDeployment() {
        return commandsModel.getSelectedDeployment();
    }

    public String getSelectedStatefulSet() {
        return commandsModel.getSelectedStatefulSet();
    }

    public String getSelectedReplicaSet() {
        return commandsModel.getSelectedReplicaSet();
    }

    public String getSelectedDaemonSet() {
        return commandsModel.getSelectedDaemonSet();
    }

    public String getSelectedConfigMap() {
        return commandsModel.getSelectedConfigMap();
    }

    public String getSelectedService() {
        return commandsModel.getSelectedService();
    }

    public String getSelectedJob() {
        return commandsModel.getSelectedJob();
    }

    public void setSelectedNamespace(String selectedNamespace) {
        commandsModel.setSelectedNamespace(selectedNamespace);
    }

    public void setSelectedPod(String selectedPod) {
        commandsModel.setSelectedPod(selectedPod);
    }

    public void setSelectedDeployment(String selectedDeployment) {
        commandsModel.setSelectedDeployment(selectedDeployment);
    }

    public void setSelectedStatefulSet(String selectedStatefulSet) {
        commandsModel.setSelectedStatefulSet(selectedStatefulSet);
    }

    public void setSelectedReplicaSet(String selectedReplicaSet) {
        commandsModel.setSelectedReplicaSet(selectedReplicaSet);
    }

    public void setSelectedDaemonSet(String selectedDaemonSet) {
        commandsModel.setSelectedDaemonSet(selectedDaemonSet);
    }

    public void setSelectedConfigMap(String selectedConfigMap) {
        commandsModel.setSelectedConfigMap(selectedConfigMap);
    }

    public void setSelectedService(String selectedService) {
        commandsModel.setSelectedService(selectedService);
    }

    public void setSelectedJob(String selectedJob) {
        commandsModel.setSelectedJob(selectedJob);
    }

}
