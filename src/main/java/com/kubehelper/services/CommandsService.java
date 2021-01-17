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

import com.google.common.collect.ImmutableSortedSet;
import com.google.common.io.Files;
import com.kubehelper.common.Global;
import com.kubehelper.common.Resource;
import com.kubehelper.domain.models.CommandsModel;
import com.kubehelper.domain.results.CommandsResult;
import com.kubehelper.domain.results.FileSourceResult;
import com.moandjiezana.toml.Toml;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Search service.
 *
 * @author JDev
 */
@Service
public class CommandsService {

    private static Logger logger = LoggerFactory.getLogger(CommandsService.class);
    private String historyEntryTemplate;
    private String newCommandsTemplate;

    private KubernetesClient fabric8Client = new DefaultKubernetesClient();

    @Value("${kubehelper.predefined.commanmds.path}")
    private String predefinedCommandsPath;

    @Value("${kubehelper.user.commands.location.search.path}")
    private String userCommandsLocationSearchPath;

    @Value("${kubehelper.commanmds.history.path}")
    private String commandsHistoryPath;

    @Value("${kubehelper.history.entry.template.src.path}")
    private String historyEntryTemplateSrcPath;

    @Value("${kubehelper.new.commands.template.src.path}")
    private String newCommandsTemplateSrcPath;

    @Autowired
    private CommonService commonService;

    @PostConstruct
    private void postConstruct() {
        historyEntryTemplate = commonService.getClasspathResourceAsStringByPath(historyEntryTemplateSrcPath);
        newCommandsTemplate = commonService.getClasspathResourceAsStringByPath(newCommandsTemplateSrcPath);
    }


    //  COMMANDS ================


    /**
     * Executes command and writes output to history file.
     *
     * @param commandsModel - commands model.
     */
    public void run(CommandsModel commandsModel) {
        String commandOutput = commonService.executeCommand(commandsModel.getSelectedShell(), commandsModel.getCommandToExecute());
        commandsModel.setExecutedCommandOutput(commandOutput);
        writeCommandExecutionToHistory(commandsModel);
    }

    /**
     * Reads toml commands resources from init/commands and parse commands.
     *
     * @param commandsModel - commands model.
     */
    public void parsePredefinedCommands(CommandsModel commandsModel) {
        HashMap<String, Toml> commands = new HashMap<>();
        try {
            org.springframework.core.io.Resource[] resources = commonService.getFilesPathsFromClasspathByDirAndExtension(predefinedCommandsPath, ".toml");
            for (org.springframework.core.io.Resource resource : resources) {
                commands.put(Files.getNameWithoutExtension(resource.getFilename()), new Toml().read(resource.getInputStream()));
            }
        } catch (IOException e) {
            commandsModel.addParseException(e);
            logger.error(e.getMessage(), e);
        }
        parseCommands(commandsModel, commands);
    }

    /**
     * Reads toml commands resources from git folder and parse commands.
     *
     * @param commandsModel- commands model.
     */
    public void parseUserCommands(CommandsModel commandsModel) {
        HashMap<String, Toml> commands = new HashMap<>();
        try {
            Set<String> userCommandFiles = commonService.getFilesPathsByDirAndExtension(userCommandsLocationSearchPath, 10, ".toml");
            for (String filePath : userCommandFiles) {
                if (filePath.endsWith(Global.CONFIG_FILENAME)) {
                    continue;
                }
                commands.put(Files.getNameWithoutExtension(filePath), new Toml().read(commonService.getResourceAsStringByPath(filePath)));
            }
        } catch (IOException | IllegalStateException e) {
            commandsModel.addParseException(e);
            logger.error(e.getMessage(), e);
        }
        parseCommands(commandsModel, commands);
    }


    /**
     * Writes execution result output to file.
     *
     * @param commandsModel - commands model.
     */
    private void writeCommandExecutionToHistory(CommandsModel commandsModel) {
        String today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
        File file = new File(commandsHistoryPath + today + ".txt");
        try {
            file.createNewFile();
            String composedHistoryEntry = new StringSubstitutor(buildHistoryEntry(commandsModel)).replace(historyEntryTemplate);
            String fileContent = commonService.getResourceAsStringByPath(file.getPath());
            fileContent = composedHistoryEntry + fileContent;
            FileUtils.writeStringToFile(file, fileContent, StandardCharsets.UTF_8.toString());
        } catch (IOException e) {
            commandsModel.addNotificationException("Cannot write command to execution: Error." + e.getMessage());
            logger.debug(e.getMessage(), e);
        }
    }

    /**
     * Builds history entry from commands model for replacement in template.
     *
     * @param model - commands model.
     * @return - map with history entry
     */
    private Map<String, String> buildHistoryEntry(CommandsModel model) {
        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss yyyy-MM-dd"));
        return Map.of("time", time, "command", model.getCommandToExecute(), "output", model.getExecutedCommandOutput());
    }


    /**
     * Parse predefined commands from commands map..
     *
     * @param commandsModel - commands Model.
     */
    public void parseCommands(CommandsModel commandsModel, HashMap<String, Toml> commands) {
        for (Map.Entry<String, Toml> commandsMap : commands.entrySet()) {
            for (Map.Entry<String, Object> commandEntry : commandsMap.getValue().entrySet()) {
                CommandsResult cr = new CommandsResult(commandsModel.getCommandsResults().size() + 1);
                try {
                    Toml command = (Toml) commandEntry.getValue();
                    cr.setFile(commandsMap.getKey())
                            .setName(commandEntry.getKey())
                            .setGroup(command.getString("group"))
                            .setDescription(command.getString("description"))
                            .setCommand(command.getString("command"));
                    commandsModel.addCommandResult(cr);
                } catch (RuntimeException e) {
                    commandsModel.addParseException(new RuntimeException("Command parse Error. Name, Group, Description and Command itself are mandatory fields. Object: " + cr.toString()));
                    logger.error("Command parse Error. Group, Operation Description and command itself are mandatory fields. Object: " + cr.toString());
                }
            }
        }
    }


    /**
     * Fehches resources(resource names) depends on namespace for commands hot replacement comboboxes.
     *
     * @param model - commands model
     */
    public void fetchResourcesDependsOnNamespace(CommandsModel model) {
        model.setNamespacedPods(fabric8Client.pods().inNamespace(model.getSelectedNamespace())
                .list().getItems().stream().map(item -> item.getMetadata().getName()).collect(Collectors.toSet()));
        model.setNamespacedDeployments(fabric8Client.apps().deployments().inNamespace(model.getSelectedNamespace())
                .list().getItems().stream().map(item -> item.getMetadata().getName()).collect(Collectors.toSet()));
        model.setNamespacedStatefulSets(fabric8Client.apps().statefulSets().inNamespace(model.getSelectedNamespace())
                .list().getItems().stream().map(item -> item.getMetadata().getName()).collect(Collectors.toSet()));
        model.setNamespacedReplicaSets(fabric8Client.apps().replicaSets().inNamespace(model.getSelectedNamespace())
                .list().getItems().stream().map(item -> item.getMetadata().getName()).collect(Collectors.toSet()));
        model.setNamespacedDaemonSets(fabric8Client.apps().daemonSets().inNamespace(model.getSelectedNamespace())
                .list().getItems().stream().map(item -> item.getMetadata().getName()).collect(Collectors.toSet()));
        model.setNamespacedConfigMaps(fabric8Client.configMaps().inNamespace(model.getSelectedNamespace())
                .list().getItems().stream().map(item -> item.getMetadata().getName()).collect(Collectors.toSet()));
        model.setNamespacedServices(fabric8Client.services().inNamespace(model.getSelectedNamespace())
                .list().getItems().stream().map(item -> item.getMetadata().getName()).collect(Collectors.toSet()));
        model.setNamespacedJobs(fabric8Client.batch().jobs().inNamespace(model.getSelectedNamespace())
                .list().getItems().stream().map(item -> item.getMetadata().getName()).collect(Collectors.toSet()));
    }


    public void commandHotReplacement(CommandsModel commandsModel) {
        String command = commandsModel.getCommandToExecute().trim().toLowerCase(Locale.ROOT);

        String join = String.join(" ", commandsModel.getSelectedPods());
        String fixedInput1 = command.replaceAll(" (pods|pod|po) .* ", " get pods " + join);

        if (StringUtils.startsWithAny(command, "kubectl get", "kubectl exec")) {
            Resource resource = detectResourceFromCommand(command);

        }
    }

    public Resource detectResourceFromCommand(String command) {
        Resource resource = null;

        int i = StringUtils.lastIndexOfAny(command, "get", "exec");
        String resourceAsString = command.substring(i, command.indexOf(" ", i));

        if (StringUtils.isNotBlank(resourceAsString)) {
            switch (resourceAsString) {
                case "po", "pod", "pods" -> resource = Resource.POD; //commandsModel.getselectedPods()
                case "deploy", "deployment", "deployments" -> resource = Resource.DEPLOYMENT;
                case "sts", "statefulset", "statefulsets" -> resource = Resource.STATEFUL_SET;
                case "rs", "replicaset", "replicasets" -> resource = Resource.REPLICA_SET;
                case "ds", "daemonset", "daemonsets" -> resource = Resource.DAEMON_SET;
                case "cm", "configmap", "configmaps" -> resource = Resource.CONFIG_MAP;
                case "svc", "service", "services" -> resource = Resource.SERVICE;
                case "job", "jobs" -> resource = Resource.JOB;
            }
        }
        return resource;
    }

    //  COMMANDS MANAGEMENT ================


    public void prepareCommandsManagement(CommandsModel commandsModel) {
        try {
            //search for user commands
            Set<String> filesPathsByDirAndExtension = commonService.getFilesPathsByDirAndExtension(userCommandsLocationSearchPath, 10, ".toml");
            commandsModel.setCommandsSources(new HashMap<>());
            for (String filePath : filesPathsByDirAndExtension) {
                if (filePath.endsWith(Global.CONFIG_FILENAME)) {
                    continue;
                }
                commandsModel.addCommandSource(Files.getNameWithoutExtension(filePath), filePath);
            }

            //get predefined commands
            org.springframework.core.io.Resource[] resources = commonService.getFilesPathsFromClasspathByDirAndExtension(predefinedCommandsPath, ".toml");
            for (org.springframework.core.io.Resource resource : resources) {
                commandsModel.addReadonlyCommandSource(Files.getNameWithoutExtension(resource.getFilename()), resource.getURI());
            }

            //sort and set first
            commandsModel.sortCommandSourcesAlphabeticallyAsc();
            Optional<Map.Entry<String, FileSourceResult>> first = commandsModel.getCommandsSources().entrySet().stream().findFirst();
            if (first.isPresent()) {
                if (first.get().getValue().isReadonly()) {
                    commandsModel.setSelectedCommandsSourceRaw(IOUtils.toString(first.get().getValue().getUri()));
                } else {
                    commandsModel.setSelectedCommandsSourceRaw(commonService.getResourceAsStringByPath(first.get().getValue().getFilePath()));
                }
                commandsModel.setSelectedCommandsSourceLabel(first.get().getKey());
            }
        } catch (IOException e) {
            commandsModel.addNotificationException("Cannot Prepare Commands for Management: Error." + e.getMessage());
            logger.debug(e.getMessage(), e);
        }
    }

    public void changeCommandsManagementRaw(CommandsModel cm) {
        FileSourceResult fileSource = cm.getCommandsSources().get(cm.getSelectedCommandsSourceLabel());
        String rawSource = "";
        try {
            rawSource = fileSource.isReadonly() ? IOUtils.toString(fileSource.getUri()) : commonService.getResourceAsStringByPath(fileSource.getFilePath());
        } catch (IOException e) {
            cm.addNotificationException("Cannot Prepare Commands for Management: Error." + e.getMessage());
            logger.debug(e.getMessage(), e);
        }
        cm.setSelectedCommandsSourceRaw(rawSource);
    }

    public void updateCommands(CommandsModel cm, String commandsForUpdate) {

        //validate
        try {
            new Toml().read(commandsForUpdate);
            cm.setSelectedCommandsSourceRaw(commandsForUpdate);
        } catch (IllegalStateException e) {
            cm.addException(String.format("Commands file %s is not valid. Error ", cm.getSelectedCommandsSourceLabel()) + e.getMessage(), e);
            logger.error(String.format("Commands file %s is not valid. Error ", cm.getSelectedCommandsSourceLabel()) + e.getMessage());
            return;
        }

        String commandsFilePath = cm.getSelectedSourceFileResult().getFilePath();

        //overwrite commands
        try {
            FileUtils.writeStringToFile(new File(commandsFilePath), commandsForUpdate);
        } catch (IOException e) {
            cm.addException(String.format("Commands file %s is not valid. Error ", cm.getSelectedCommandsSourceLabel()) + e.getMessage(), e);
            logger.error(String.format("Commands file %s is not valid. Error ", cm.getSelectedCommandsSourceLabel()) + e.getMessage());
        }
    }

    public void addNewCommandsFile(CommandsModel model, String newCommandsFilePath) {
        try {
            FileUtils.writeStringToFile(new File(newCommandsFilePath), newCommandsTemplate);
        } catch (IOException e) {
            model.addException(String.format("Unable to create commands file %s. Error ", newCommandsFilePath) + e.getMessage(), e);
            logger.error(String.format("Unable to create commands file %s. Error ", newCommandsFilePath) + e.getMessage());
        }
    }


    //  COMMANDS HISTORY ================

    /**
     * Prepares commands history view. Finds all history files and set active newest.
     *
     * @param commandsModel - commands model.
     */
    public void prepareCommandsHistory(CommandsModel commandsModel) {
        try {
            Set<String> filesPathsByDirAndExtension = commonService.getFilesPathsByDirAndExtension(commandsHistoryPath, 2, ".txt");
            commandsModel.setCommandsHistories(new HashMap<>());
            filesPathsByDirAndExtension.forEach(file -> commandsModel.addHistorySource(Files.getNameWithoutExtension(file), file));
            commandsModel.sortMapByDateDesc();
            Optional<Map.Entry<String, FileSourceResult>> first = commandsModel.getCommandsHistories().entrySet().stream().findFirst();
            if (first.isPresent()) {
                commandsModel.setSelectedCommandsHistoryRaw(commonService.getResourceAsStringByPath(first.get().getValue().getFilePath()));
                commandsModel.setSelectedCommandsHistoryLabel(first.get().getKey());
            }
        } catch (IOException e) {
            commandsModel.addNotificationException("Cannot Prepare Commands for History: Error." + e.getMessage());
            logger.debug(e.getMessage(), e);
        }
    }

    /**
     * Shows history for ranges. From Week/Month/Year to today. Default case for single day.
     *
     * @param cm - commands model.
     */
    public void changeHistoryRaw(CommandsModel cm) {
        LocalDate today = LocalDate.now();
        switch (cm.getSelectedCommandsHistoryRange()) {
            case "This Week" -> showHistoryFor(cm, today.with(TemporalAdjusters.previous(DayOfWeek.MONDAY)), today);
            case "This Month" -> showHistoryFor(cm, today.with(TemporalAdjusters.firstDayOfMonth()), today);
            case "This Year" -> showHistoryFor(cm, today.with(TemporalAdjusters.firstDayOfYear()), today);
            case "All" -> showHistoryFor(cm, LocalDate.now().minusMonths(200), today);
            default -> cm.setSelectedCommandsHistoryRaw(commonService.getResourceAsStringByPath(cm.getCommandsHistories().get(cm.getSelectedCommandsHistoryLabel()).getFilePath()));
        }
    }

    /**
     * Calculates, which history to show depending on the selected period.
     *
     * @param cm   - commands model.
     * @param from - from date.
     * @param to   - to date.
     */
    private void showHistoryFor(CommandsModel cm, LocalDate from, LocalDate to) {
        StringBuilder history = new StringBuilder();
        String historyDayHeader = "++++++++++++++++++++++++++++++++++++++++=== %s ===++++++++++++++++++++++++++++++++++++++++\n\n\n";
        Set<String> filesPathsByDirAndExtension = new HashSet<>();

        //get all history files
        try {
            filesPathsByDirAndExtension = commonService.getFilesPathsByDirAndExtension(commandsHistoryPath, 2, ".txt");
        } catch (IOException e) {
            logger.debug(e.getMessage(), e);
        }

        //get dates range and filter files paths depends of range
        List<LocalDate> datesRange = from.datesUntil(to.plusDays(1)).collect(Collectors.toList());
        Set<String> datesInRange = filesPathsByDirAndExtension.stream()
                .filter(filePath -> datesRange.contains(LocalDate.parse(Files.getNameWithoutExtension(filePath))))
                .collect(Collectors.toSet());

        //sort history DESC
        ImmutableSortedSet<String> sortedDatesInRangeFilePaths = ImmutableSortedSet.copyOf(
                Comparator.comparing(filePath -> LocalDate.parse(Files.getNameWithoutExtension(filePath), DateTimeFormatter.ISO_LOCAL_DATE), Comparator.reverseOrder()), datesInRange);

        //add filtered history to history string builder
        for (String filePath : sortedDatesInRangeFilePaths) {
            history.append(String.format(historyDayHeader, Files.getNameWithoutExtension(filePath))).append(commonService.getResourceAsStringByPath(filePath)).append("\n");
        }
        cm.setSelectedCommandsHistoryRaw(history.toString());
    }

    /**
     * Filters only commands from file for history output. Or vice versa.
     *
     * @param commandsModel - commands model.
     * @param show          - state. If true then only commands will be shown.
     */
    public void showOnlyCommandsInHistory(CommandsModel commandsModel, boolean show) {
        if (show) {
            commandsModel.setCommandsRawHistoryBuffer(commandsModel.getSelectedCommandsHistoryRaw());
            List<String> lines = new ArrayList<>(Arrays.asList(commandsModel.getSelectedCommandsHistoryRaw().split("\n")));
            Iterator<String> iterator = lines.iterator();
            boolean lineToRemove = false;
            while (iterator.hasNext()) {
                String next = iterator.next();
                if (next.contains("===========")) {
                    lineToRemove = false;
                }
                if (next.equals("************************************************************************************************************") || lineToRemove) {
                    if (next.contains("++++++++++++++++++++++++++++++++++++++++===")) {
                        continue;
                    }
                    iterator.remove();
                    lineToRemove = true;
                }
            }
            commandsModel.setSelectedCommandsHistoryRaw(String.join("\n", lines));
        } else {
            commandsModel.setSelectedCommandsHistoryRaw(commandsModel.getCommandsRawHistoryBuffer());
        }
    }
}
