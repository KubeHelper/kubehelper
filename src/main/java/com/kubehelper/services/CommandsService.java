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

import com.google.common.io.Files;
import com.kubehelper.common.Resource;
import com.kubehelper.domain.models.CommandsModel;
import com.kubehelper.domain.results.CommandsResult;
import com.moandjiezana.toml.Toml;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.Arrays;
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
    private String predefinedCommandsPath = "/init/commands/";
    private List<String> predefinedCommands = Arrays.asList("commands.kh", "commands2.kh");

    //    private String commandsHistoryPath = "/Volumes/MAC_WORK/tmp/history";
    private String commandsHistoryPath = "C:\\temp\\history";
    private String historyEntryTemplate;

    private KubernetesClient fabric8Client = new DefaultKubernetesClient();

    @Autowired
    private Environment env;

//    @Value("zul.raw.resource.path")
//    private String zulRawResourcePath;

    @Autowired
    private CommonService commonService;

    @PostConstruct
    private void postConstruct() {
        historyEntryTemplate = commonService.getClasspathResourceAsStringByPath("/templates/history/history-entry.template");
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


    public void parsePredefinedCommands(CommandsModel commandsModel) {
        parsePredefinedCommandsFromInit(commandsModel);
//        predefinedCommands.forEach(f -> {
//            List<String> lines = commonService.getLinesFromResourceByPath(predefinedCommandsPath + f);
//            parsePredefinedCommandsFromInit(lines, commandsModel);
//            commandsModel.addCommandSource(Files.getNameWithoutExtension(f), predefinedCommandsPath, true);
//        });
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
            FileUtils.writeStringToFile(file, composedHistoryEntry, StandardCharsets.UTF_8.toString(), true);
        } catch (IOException e) {
//            TODO to think about notification
//            commandsModel.addParseException(e);
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


    public void parseUserCommands(CommandsModel commandsModel) {
        try {
            Set<String> userCommandFiles = commonService.getFilesPathsByDirAndExtension(commandsModel.getUserCommandsPath(), 5, ".kh");
            for (String filePath : userCommandFiles) {
                List<String> lines = Files.readLines(new File(filePath), Charset.forName("UTF-8"));
//                TODO
//                commandsModel.addCommandSource("Predefined Commands", predefinedCommandsPath, commandsRaw);
                parsePredefinedCommandsFromInit(commandsModel);
            }
        } catch (IOException e) {
            commandsModel.addParseException(e);
            logger.error(e.getMessage(), e);
        }
    }

    public void parsePredefinedCommandsFromInit(CommandsModel commandsModel) {

//        Set<String> commands = getCommandsFilesPathsSet(commandsModel, "/init/commands", 1, ".toml");
//        TODO dynamically read from classpath
//        https://www.logicbig.com/how-to/java/find-classpath-files-under-folder-and-sub-folder.html

//        for (String file : commands) {
//        Toml commandsToml = new Toml().read(commonService.getClasspathResourceAsStringByPath(file));
        String tomlString = commonService.getClasspathResourceAsStringByPath("init/commands/commands.toml");
        Toml commandsToml = new Toml().read(tomlString);
        String file = "commands.toml";
        for (Map.Entry<String, Object> entry : commandsToml.entrySet()) {
            CommandsResult cr = new CommandsResult(commandsModel.getCommandsResults().size() + 1);
            try {
                Toml command = (Toml) entry.getValue();
                cr.setFile(Files.getNameWithoutExtension(file))
                        .setName(entry.getKey())
                        .setGroup(command.getString("group"))
                        .setDescription(command.getString("description"))
                        .setCommand(command.getString("command"));
                commandsModel.addCommandResult(cr);
            } catch (RuntimeException e) {
                commandsModel.addParseException(new RuntimeException("Command parse Error. Name, Group, Description and Command itself are mandatory fields. Object: " + cr.toString()));
                logger.error("Command parse Error. Group, Operation Description and command itself are mandatory fields. Object: " + cr.toString());
            }
        }

//        }

    }

    private Set<String> getCommandsFilesPathsSet(CommandsModel commandsModel, String filesPath, int depth, String extension) {
        Set<String> commands = new HashSet<>();
        try {
            commands = commonService.getFilesPathsByDirAndExtension(filesPath, depth, extension);
        } catch (IOException e) {
            commandsModel.addParseException(e);
            logger.error(e.getMessage(), e);
        }
        return commands;
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
        CommandLineParser parser = new DefaultParser();
        Options options = new Options();
        Option option = new Option("pods", "test");
        Option option1 = new Option("n", "test1");
        options.addOption(option);
        options.addOption(option1);
//        commandsModel.setCommandToExecute(commandsModel.getEditableCommandToExecute());
        String command = commandsModel.getCommandToExecute().trim().toLowerCase(Locale.ROOT);
        CommandLine parse = null;
        try {
            parse = parser.parse(options, command.split(" "));
        } catch (ParseException e) {
            e.printStackTrace();
        }
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


    //  COMMANDS HISTORY ================


    public void prepareCommandsHistory(CommandsModel commandsModel) {
        try {
            Set<String> filesPathsByDirAndExtension = commonService.getFilesPathsByDirAndExtension(commandsHistoryPath, 2, ".txt");
            commandsModel.setCommandsHistories(new HashMap<>());
            filesPathsByDirAndExtension.forEach(file -> {
                commandsModel.addHistorySource(Files.getNameWithoutExtension(file), file);
            });
            commandsModel.sortMapByDateDesc();
            Optional<Map.Entry<String, CommandsModel.FileSource>> first = commandsModel.getCommandsHistories().entrySet().stream().findFirst();
            if (first.isPresent()) {
                commandsModel.setSelectedCommandsHistoryRaw(commonService.getResourceAsStringByPath(first.get().getValue().getFilePath()));
                commandsModel.setSelectedCommandsHistoryLabel(first.get().getKey());
            }
        } catch (IOException e) {
//            TODO to think about notification
//            commandsModel.addParseException(e);
            logger.debug(e.getMessage(), e);
        }
    }


    public void changeHistoryRaw(CommandsModel cm) {
//        String today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
        LocalDate today = LocalDate.now();

//        TODO
//          get history files/strings from folder -> sort DESC -> set last elem to raw histroy
        WeekFields.of(Locale.getDefault());
        switch (cm.getSelectedCommandsHistoryRange()) {
            case "Week" -> showHistoryFor(today, today);
            default -> cm.setSelectedCommandsHistoryRaw(commonService.getResourceAsStringByPath(cm.getCommandsHistories().get(cm.getSelectedCommandsHistoryLabel()).getFilePath()));
        }

    }


    private void showHistoryFor(LocalDate from, LocalDate to) {

        if (from.equals(to)) {
            to = from;
        }
        try {
            Set<String> filesPathsByDirAndExtension = commonService.getFilesPathsByDirAndExtension(commandsHistoryPath, 2, ".txt");
            for (String filePath : filesPathsByDirAndExtension) {
//                File file = new File(filePath);
//                BasicFileAttributes attr = java.nio.file.Files.readAttributes(Paths.get(new URI(new File(filePath).getAbsolutePath())), BasicFileAttributes.class, LinkOption.NOFOLLOW_LINKS);
                FileTime creationTime = (FileTime) java.nio.file.Files.getAttribute(Paths.get(new URI(new File(filePath).getAbsolutePath())), "creationTime");
                LocalDate localDate = LocalDate.ofInstant(creationTime.toInstant(), ZoneId.systemDefault());
                if (!localDate.isBefore(from) && !localDate.isAfter(to)) {
//                   TODO
//                   read files to commands content
//                   copy actual content to buffer
//                   disable toolbarbuttons
                }
            }
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }

//        Calendar c = Calendar.getInstance();
//        c.setTime(yourdate); // yourdate is an object of type Date
//
//        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
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
            List<String> lines = Arrays.asList(commandsModel.getSelectedCommandsHistoryRaw().split("\n"));
            Iterator<String> iterator = lines.iterator();
            boolean lineToRemove = false;
            while (iterator.hasNext()) {
                String next = iterator.next();
                if (next.contains("===========")) {
                    lineToRemove = false;
                }
                if (next.equals("************************************************************************************************************") || lineToRemove) {
                    iterator.remove();
                    lineToRemove = true;
                }
            }
            commandsModel.setSelectedCommandsHistoryRaw(lines.stream().collect(Collectors.joining("\n")));
        } else {
            commandsModel.setSelectedCommandsHistoryRaw(commandsModel.getCommandsRawHistoryBuffer());
        }
    }

}
