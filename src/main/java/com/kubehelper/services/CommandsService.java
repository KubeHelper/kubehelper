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
import com.kubehelper.common.Operation;
import com.kubehelper.common.Resource;
import com.kubehelper.domain.models.CommandsModel;
import com.kubehelper.domain.results.CommandsResult;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Search service.
 *
 * @author JDev
 */
@Service
public class CommandsService {

    private static Logger logger = LoggerFactory.getLogger(CommandsService.class);
    private String predefinedCommandsPath = "/templates/features/commands.kh";

    private KubernetesClient fabric8Client = new DefaultKubernetesClient();

    @Autowired
    private CommonService commonService;

    public void parsePredefinedCommands(CommandsModel commandsModel) {
        List<String> lines = commonService.getLinesFromResourceByPath(predefinedCommandsPath);
        String predefinedCommands = commonService.getResourcesAsStringByPath(predefinedCommandsPath);
        commandsModel.addCommandSource("Predefined Commands", predefinedCommandsPath, predefinedCommands);
        parsePredefinedCommandsFromLines(lines, commandsModel);
    }

    public void run(CommandsModel commandsModel) {
//        try {
        String commandOutput = commonService.executeCommand(commandsModel.getSelectedShell(), commandsModel.getCommandToExecute());
        commandsModel.setExecutedCommandOutput(commandOutput);
//        fetchResourcesDependsOnNamespace(commandsModel);
//            KubectlGet pods = KubectlHelper.getKubectlGet("pods");
//            List name = pods.namespace("spark").execute();
//            name.size();
//        } catch (IOException | KubectlException e) {
//            commandsModel.addException("Error" + e.getMessage(), e);
//            logger.error(e.getMessage(), e);
//        }
    }

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


    public void parseUserCommands(CommandsModel commandsModel) {
        try {
            Set<String> userCommandFiles = getUserCommandFilesPaths(commandsModel.getUserCommandsPath(), 5);
            for (String filePath : userCommandFiles) {
                List<String> lines = Files.readLines(new File(filePath), Charset.forName("UTF-8"));
//                TODO
//                commandsModel.addCommandSource("Predefined Commands", predefinedCommandsPath, commandsRaw);
                parsePredefinedCommandsFromLines(lines, commandsModel);
            }
        } catch (IOException e) {
            commandsModel.addParseException(e);
            logger.error(e.getMessage(), e);
        }
    }

    public Set<String> getUserCommandFilesPaths(String dir, int depth) throws IOException {
        try (Stream<Path> stream = java.nio.file.Files.walk(Paths.get(dir), depth)) {
            return stream
                    .map(path -> path.toString()).filter(f -> f.endsWith(".kh"))
                    .collect(Collectors.toSet());
        }
    }


    public void parsePredefinedCommandsFromLines(List<String> lines, CommandsModel commandsModel) {
        ListIterator<String> iterator = lines.listIterator();
        while (iterator.hasNext()) {
            try {
                buildCommandResult(iterator, commandsModel);
            } catch (RuntimeException e) {
                commandsModel.addParseException(e);
                logger.error(e.getMessage(), e);
            }
        }
    }

    private void buildCommandResult(ListIterator<String> iterator, CommandsModel commandsModel) {
        CommandsResult commandResult = new CommandsResult(commandsModel.getCommandsResults().size() + 1);
        String line = iterator.next();
        if (line.startsWith("Group:")) {
            commandResult.setGroup(getLineContent(line));
            line = iterator.next();
        }

        if (line.startsWith("Operation:")) {
            commandResult.setOperation(getLineContent(line));
            line = iterator.next();
        }

        if (line.startsWith("Description:")) {
            commandResult.setDescription(getLineContent(line));
            line = iterator.next();
        }

        if (line.startsWith("Rows:")) {
            String[] split = getLineContent(line).split("\\|");
            commandResult.setRows(Arrays.asList(split));
            line = iterator.next();
        }

        if (StringUtils.startsWithAny(line, "kubectl") && StringUtils.isNotBlank(line)) {
            StringBuilder builder = new StringBuilder();
            while (line.trim().endsWith("\\")) {
                String commandLine = line.substring(0, line.lastIndexOf("\\")).trim();
                builder.append(commandLine).append(" \n");
                line = iterator.next();
            }
            if (StringUtils.isNotBlank(line)) {
                builder.append(line.trim());
            }
            commandResult.setCommand(builder.toString());
        }
        if (StringUtils.isNotBlank(line)) {
            validateAndAddCommandResult(commandResult, commandsModel);
        }
    }

    private void validateAndAddCommandResult(CommandsResult cr, CommandsModel commandsModel) {
        if (StringUtils.isAnyBlank(cr.getGroup(), cr.getDescription(), cr.getCommand()) || Operation.isOperationInvalid(cr.getOperation())) {
            commandsModel.addParseException(new RuntimeException("Command parse Error. Group, Operation, Description and command itself are mandatory fields. Object: " + cr.toString()));
            logger.error("Command parse Error. Group, Operation Description and command itself are mandatory fields. Object: " + cr.toString());
        } else {
            commandsModel.addCommandResult(cr);
        }
    }

    private String getLineContent(String line) {
        return line.substring(line.indexOf(":") + 1).trim();
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
}
