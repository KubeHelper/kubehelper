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
import com.kubehelper.domain.results.FileSourceResult;
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
import org.zkoss.zul.Html;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Vbox;
import org.zkoss.zul.Window;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Class for displaying and creating of cron jobs. And View of reports from cron jobs.
 * ViewModel initializes ..kubehelper/pages/cron.zul
 *
 * @author JDev
 */
@VariableResolver(DelegatingVariableResolver.class)
public class CronJobsVM implements EventListener<Event> {

    private static Logger logger = LoggerFactory.getLogger(CronJobsVM.class);

    private int centerLayoutHeight = 700;
    private boolean isOnInit = true;

    private String activeTab = "cronJobs";

    private final String cronJobsReportsCss = "font-size: %spx;";
    private int cronJobsReportsFontSize = 14;

    private String cronJobName = "";
    private String cronJobExpression = "";
    private String cronJobEmail = "";
    private String cronJobDescription = "";

    private boolean wordWrapCommandsInReport;

    private CronJobsModel model;

    private ListModelList<CommandsResult> commandsResults = new ListModelList<>();

    @WireVariable
    private CronJobsService cronJobsService;

    @WireVariable
    private CommonService commonService;

    @WireVariable
    private Config config;

    @Wire("#cronJobsTabbox")
    private Tabbox notificationContainer;

    @Wire
    private Footer commandsGridFooter;


    @Init
    public void init() {
        model = new CronJobsModel();
        cronJobsService.parsePredefinedCommands(model);
        cronJobsService.parseUserCommands(model);
        commandsResults = new ListModelList<>(model.getCommandsResults());
    }

    @AfterCompose
    public void afterCompose(@ContextParam(ContextType.VIEW) Component view) {
        Selectors.wireComponents(view, this, false);
        Selectors.wireEventListeners(view, this);
        checkRuntimeNotificationExceptions();
    }

    @Listen("onAfterSize=#cronJobsTemplate")
    public void onAfterSizeCenter(AfterSizeEvent event) {
        centerLayoutHeight = event.getHeight() - 3;
        BindUtils.postNotifyChange(this, ".");
    }

    /**
     * Processing of various events. Reports panel. On click on report.
     *
     * @param event - event.
     */
    @Override
    public void onEvent(Event event) {
        if (Events.ON_CLICK.equals(event.getName())) {
            selectAndChangeSourceToolbarbuttonLabel(event);
        }
    }

    /**
     * Loads first report file into view and prepare links to other report files.
     * Creates toolbarbuttons for each report group(cron job).
     *
     * @param tabbox - main tabbox @{@link Tabbox}.
     */
    @Command
    public void onSelectMainCronJobsTabs(@ContextParam(ContextType.COMPONENT) Tabbox tabbox) {
        activeTab = tabbox.getSelectedTab().getId();
        if ("cronJobsReports".equals(tabbox.getSelectedTab().getId()) && StringUtils.isBlank(model.getSelectedReportRaw())) {
            cronJobsService.prepareCronJobsReports(model);
            redrawReportsToolbarbuttons("cronJobsReportsToolbarID", model.getCronJobsReportsForJob(), getCommandToolbarButtonId(model.getSelectedReportLabel()));
            refreshReportsOutput();
        }
        checkRuntimeNotificationExceptions();
    }


    //  CRON JOBS METHODS ================


    /**
     * Validates and starts new cron job.
     */
    @Command
    public void startCronJob() {
        if (isCronJobNotValid()) {
            return;
        }

        //TODO change after test
        CronJobResult cronJobResult = new CronJobResult(1)
                .setName(cronJobName)
                .setCommand(model.getCommandToExecute())
                .setExpression("*/20 * * * * *")
//                .setPeriod(cronJobExpression)
                .setDescription(cronJobDescription)
                .setEmail(cronJobEmail)
                .setShell("bash");
        cronJobsService.startCronJob(model, cronJobResult);
        checkExceptions();
        updateActiveCronJobsUI();
    }

    @Command
    public void editCronJob() {

    }


    /**
     * Updates active cron job view after create/rerun/remove action.
     * With small delay for register new cron job.
     */
    private void updateActiveCronJobsUI() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
        }
        BindUtils.postNotifyChange(this, "activeCronJobs");
    }

    /**
     * Reruns stopped cron job.
     *
     * @param job - @{@link CronJobResult}
     */
    @Command
    public void rerunCronJob(@BindingParam("job") CronJobResult job) {
        cronJobsService.rerunCronJob(job);
        Notification.show(String.format("Cron Job %s was Started again.", job.getName()), "info", notificationContainer, "top_right", 3000);
        updateActiveCronJobsUI();
    }

    /**
     * Stops running cron job and shows confirmation dialog before.
     *
     * @param job - @{@link CronJobResult}
     */
    @Command
    public void stopCronJob(@BindingParam("job") CronJobResult job) {
        Messagebox.show(String.format("Are you sure you want to stop %s job?", job.getName()),
                "Confirmation", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION,
                (EventListener) e -> {
                    if (Messagebox.ON_OK.equals(e.getName())) {
                        if (Global.CRON_JOBS.get(job.getName()).shutdownCronJob()) {
                            Notification.show(String.format("Cron Job %s was stopped.", job.getName()), "info", notificationContainer, "top_right", 3000);
                        } else {
                            Notification.show(String.format("An error occurred while stopping the cron job. Please look in the application log.", job.getName()), "error", notificationContainer, "top_right", 5000);
                        }
                        updateActiveCronJobsUI();
                    }
                }
        );
    }


    /**
     * Removes stopped cron job and shows confirmation dialog before.
     *
     * @param job - @{@link CronJobResult}
     */
    @Command
    public void removeCronJob(@BindingParam("job") CronJobResult job) {
        Messagebox.show(String.format("Are you sure you want to delete %s job?", job.getName()),
                "Confirmation", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION,
                (EventListener) e -> {
                    if (Messagebox.ON_OK.equals(e.getName())) {
                        //additional checks, if job is done
                        if (!Global.CRON_JOBS.get(job.getName()).isDone()) {
                            Global.CRON_JOBS.get(job.getName()).shutdownCronJob();
                        }
                        //remove job from jobs list
                        if (Objects.nonNull(Global.CRON_JOBS.remove(job.getName()))) {
                            Notification.show(String.format("Cron Job %s was deleted.", job.getName()), "info", notificationContainer, "top_right", 3000);
                        } else {
                            Notification.show(String.format("An error occurred while stopping the cron job. Please look in the application log.", job.getName()), "error", notificationContainer, "top_right", 5000);
                        }
                        updateActiveCronJobsUI();
                    }
                }
        );
    }

    /**
     * Filters searches and refresh total items label and commands results view.
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
     * Synchronizes command to execute and hot replacement on full command textbox onChange event.
     */
    @Command
    @NotifyChange({"commandToExecute", "commandToExecuteEditable"})
    public void synchronizeCommandToExecute() {
        model.setCommandToExecute(getCommandWithoutUnnecessaryWhitespaces(model.getCommandToExecuteEditable()));
    }


    /**
     * Shows full command by clicking on grid item.
     *
     * @param item - commands grid item @{@link CommandsResult}.
     */
    @Command
    @NotifyChange({"commandToExecute", "commandToExecuteEditable"})
    public void showFullCommand(@BindingParam("clickedItem") CommandsResult item) {
        model.setCommandToExecute(getCommandWithoutUnnecessaryWhitespaces(item.getCommand()));
        model.setCommandToExecuteEditable(item.getCommand().trim());
    }

    /**
     * Replaces \n with spaces in command.
     *
     * @param commandToExecuteEditable - editable command to execute.
     * @return - replaced string without Unnecessary whitespaces.
     */
    private String getCommandWithoutUnnecessaryWhitespaces(String commandToExecuteEditable) {
        return commandToExecuteEditable.replaceAll("\\n", " ").replaceAll(" +", " ").trim();
    }


    /**
     * Validate cron job parameters, email, name and expression.
     *
     * @return - true if cron job parameters are invalid.
     */
    private boolean isCronJobNotValid() {
        if (StringUtils.isAnyBlank(cronJobName, cronJobExpression, model.getCommandToExecute())) {
            Notification.show(String.format("Can't create cron job %s. Name, expression and command are required fields.", cronJobName), "error", notificationContainer, "top_right", 5000);
            return true;
        }

        if (!Pattern.matches("^[a-zA-Z0-9_-]*$", cronJobName)) {
            Notification.show(String.format("Can't create cron job %s. The job name should contain only: letters, numbers, '-' and '_'", cronJobName),
                    "error", notificationContainer, "top_right", 5000);
            return true;
        }

        if (StringUtils.isNotBlank(cronJobEmail) && !Pattern.matches("^(.+)@(\\S+)$", cronJobEmail)) {
            Notification.show(String.format("Can't create cron job %s. The cron job email is invalid.", cronJobName), "error", notificationContainer, "top_right", 5000);
            return true;
        }
        return false;
    }


    /**
     * Checks if model has errors and shows popup windows with errors.
     *
     * @return - true if model has no exceptions.
     */
    private boolean checkExceptions() {
        if (model.hasErrors()) {
            Window window = (Window) Executions.createComponents(Global.PATH_TO_ERROR_RESOURCE_ZUL, null, Map.of("errors", model.getExceptions()));
            window.doModal();
            model.setExceptions(new ArrayList<>());
            return false;
        }
        return true;
    }


    //  CRON JOBS REPORTS METHODS ================


    /**
     * Refreshes commands history output div.
     */
    public void refreshReportsOutput() {
        Div cronJobsReportBlock = (Div) Path.getComponent("//indexPage/templateInclude/cronJobsReportOutputId");
        cronJobsReportBlock.getChildren().clear();
        String style = wordWrapCommandsInReport ? "style=\"white-space: pre-wrap; word-break: keep-all;\"" : "";
        cronJobsReportBlock.appendChild(new Html("<pre " + style + "><code>" + model.getSelectedReportRaw() + "</code></pre>"));
        BindUtils.postNotifyChange(this, ".");
    }

    /**
     * Checks if in commands model exists runtime exceptions and shows notification.
     */
    private void checkRuntimeNotificationExceptions() {
        if (StringUtils.isNotBlank(model.getRuntimeNotificationExceptions())) {
            Notification.show(model.getRuntimeNotificationExceptions(), "error", notificationContainer, "top_right", 5000);
        }
    }

    /**
     * Recreates and prepares toolbarbuttons after refresh or first start.
     *
     * @param toolbarId             - toolbar id.
     * @param entries               - set with button labels.
     * @param activeToolbarButtonId - sets active toolbarbutton if report exists.
     */
    private void redrawReportsToolbarbuttons(String toolbarId, List<String> entries, String activeToolbarButtonId) {
        createReportsToolbarButtons(toolbarId, entries);
        if (StringUtils.isNotBlank(model.getSelectedReportLabel())) {
            enableDisableMenuItem(activeToolbarButtonId, true, "bold;");
        }
    }

    /**
     * Creates toolbarbuttons on the reports tabs for view reports.
     *
     * @param toolbarId - id of main container for toolbarbuttons.
     * @param sources   - Set with labels(keys) for toolbarbuttons.
     */
    private void createReportsToolbarButtons(String toolbarId, List<String> sources) {
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
     * Make active or inactive menu item for report.
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

    /**
     * Generates id for new toolbarbutton from label.
     *
     * @param label - toolbarbutton label.
     * @return - generated id.
     */
    private String getCommandToolbarButtonId(String label) {
        return label.replaceAll("[^a-zA-Z0-9]", "") + "Id";
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
        if (isOnInit && model.hasErrors()) {
            Window window = (Window) Executions.createComponents(Global.PATH_TO_ERROR_RESOURCE_ZUL, null, Map.of("errors", model.getExceptions()));
            window.doModal();
            model.setExceptions(new ArrayList<>());
        }
        isOnInit = false;
        return commandsResults;
    }

    /**
     * Changes raw source and toolbarbutton state by onClick event.
     *
     * @param event - @Events.ON_CLICK event.
     */
    private void selectAndChangeSourceToolbarbuttonLabel(Event event) {
        String label = ((Toolbarbutton) event.getTarget()).getLabel();
        String oldToolbarbuttonId = null;
        if ("cronJobsReports".equals(activeTab)) {
            oldToolbarbuttonId = getCommandToolbarButtonId(model.getSelectedReportLabel());
            model.setSelectedReportLabel(label);
//            TODO
//            cronJobsService.changeHistoryRaw(model);
            refreshReportsOutput();
        }
        String newToolbarbuttonId = getCommandToolbarButtonId(label);
        enableDisableMenuItem(oldToolbarbuttonId, false, "normal;");
        enableDisableMenuItem(newToolbarbuttonId, true, "bold;");
    }

    @Command
    public void changeReportsFolder() {
        List<FileSourceResult> first = model.getCronJobsReportsObjectsForJob();
        if (!first.isEmpty()) {
            model.setSelectedReportRaw(commonService.getResourceAsStringByPath(first.get(0).getFilePath()));
            model.setSelectedReportLabel(first.get(0).getLabel());
            redrawReportsToolbarbuttons("cronJobsReportsToolbarID", model.getCronJobsReportsForJob(), getCommandToolbarButtonId(model.getSelectedReportLabel()));
            refreshReportsOutput();
        } else {
            Notification.show("There are no reports for this job.", "info", null, "bottom_left", 3000);
        }
    }

    @Command
    public void refreshReports() {

    }

    @Command
    public void wordWrapCommandsInReports() {

    }

    @Command
    public void cronJobsReportsChangeFontSize() {

    }


    //  COMMANDS GETTERS AND SETTERS ================


    public String getCommandsGridHeight() {
        return centerLayoutHeight * 0.3 + "px";
    }

    public String getNewCronJobBoxHeight() {
        return centerLayoutHeight * 0.185 + "px";
    }
    
    public String getCronJobsListGroupBoxHeight() {
        return centerLayoutHeight > 1200 ? centerLayoutHeight * 0.46 + "px" : centerLayoutHeight * 0.41 + "px";
    }

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

    public String getCommandToExecuteEditable() {
        return model.getCommandToExecuteEditable();
    }

    public void setCommandToExecuteEditable(String commandToExecuteEditable) {
        model.setCommandToExecuteEditable(commandToExecuteEditable);
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

    public ListModelList<CronJobResult> getActiveCronJobs() {
        return new ListModelList<>(cronJobsService.getActiveCronJobs());
    }

    public String getCronJobName() {
        return cronJobName;
    }

    public CronJobsVM setCronJobName(String cronJobName) {
        this.cronJobName = cronJobName;
        return this;
    }

    public String getCronJobExpression() {
        return cronJobExpression;
    }

    public CronJobsVM setCronJobExpression(String cronJobExpression) {
        this.cronJobExpression = cronJobExpression;
        return this;
    }

    public String getCronJobEmail() {
        return cronJobEmail;
    }

    public CronJobsVM setCronJobEmail(String cronJobEmail) {
        this.cronJobEmail = cronJobEmail;
        return this;
    }

    public String getCronJobDescription() {
        return cronJobDescription;
    }

    public CronJobsVM setCronJobDescription(String cronJobDescription) {
        this.cronJobDescription = cronJobDescription;
        return this;
    }

    //  CRON JOBS REPORTS GETTERS AND SETTERS ================


    public String getCronJobsReportsSrcPanelHeight() {
        return centerLayoutHeight - 95 + "px";
    }

    public String getCronJobsReportsHeight() {
        return centerLayoutHeight - 50 + "px";
    }

    public String getCronJobsReportsCss() {
        return String.format(cronJobsReportsCss, cronJobsReportsFontSize);
    }

    public Set<String> getAllCronJobsReportsGroups() {
        return model.getCronJobsReports().keySet();
    }

    public String getSelectedCronJobsReportRaw() {
        return model.getSelectedReportRaw();
    }

    public String getSelectedCronJobsReportLabel() {
        return model.getSelectedReportLabel();
    }

    public void setSelectedCronJobsReportLabel(String selectedCommandsHistory) {
        model.setSelectedReportLabel(selectedCommandsHistory);
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

    public void setSelectedReportsFolder(String selectedReportsFolder) {
        model.setSelectedReportsFolder(selectedReportsFolder);
    }

    public String getSelectedReportsFolder() {
        return model.getSelectedReportsFolder();
    }

}