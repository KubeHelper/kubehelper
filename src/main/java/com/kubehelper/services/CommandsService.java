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
import com.kubehelper.common.KubeAPI;
import com.kubehelper.common.KubectlHelper;
import com.kubehelper.common.Operation;
import com.kubehelper.domain.models.CommandsModel;
import com.kubehelper.domain.results.CommandsResult;
import io.kubernetes.client.extended.kubectl.KubectlGet;
import io.kubernetes.client.extended.kubectl.exception.KubectlException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
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

    @Autowired
    private CommonService commonService;

    public void parsePredefinedCommands(CommandsModel commandsModel) {

        try {
//            TODO add read predefined commands to postConstruct, as in IpsAndModels
            File predefinedCommands = commonService.getResourcesAsFileByPath(predefinedCommandsPath);
            List<String> lines = Files.readLines(predefinedCommands, Charset.forName("UTF-8"));
            parsePredefinedCommandsFromLines(lines, commandsModel);
            initPredefinedCommands(commandsModel, predefinedCommands);
        } catch (IOException e) {
            commandsModel.addParseException(e);
            logger.error(e.getMessage(), e);
        }
    }

    public void run(CommandsModel commandsModel) {
        try {
            KubectlGet pods = KubectlHelper.getKubectlGet("pods");
            List name = pods.namespace("spark").execute();
            name.size();
        } catch (IOException | KubectlException e) {
            commandsModel.addException("Error" + e.getMessage(), e);
            logger.error(e.getMessage(), e);
        }
    }


    private void initPredefinedCommands(CommandsModel commandsModel, File file) throws IOException {
        String commandsRaw = FileUtils.readFileToString(file, "UTF-8");
        commandsModel.addCommandSource("Predefined Commands", predefinedCommandsPath, commandsRaw);
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
            StringBuilder viewBuilder = new StringBuilder(), runBuilder = new StringBuilder();
            while (line.trim().endsWith("\\")) {
                String commandLine = line.substring(0, line.lastIndexOf("\\")).trim();
                viewBuilder.append(commandLine).append("\n");
                runBuilder.append(commandLine).append(" ");
                line = iterator.next();
            }
            if (StringUtils.isNotBlank(line)) {
                viewBuilder.append(line.trim());
                runBuilder.append(line.trim());
            }
            commandResult.setViewCommand(viewBuilder.toString());
            commandResult.setRunCommand(runBuilder.toString());
        }
        if (StringUtils.isNotBlank(line)) {
            validateAndAddCommandResult(commandResult, commandsModel);
        }
    }

    private void validateAndAddCommandResult(CommandsResult cr, CommandsModel commandsModel) {
        if (StringUtils.isAnyBlank(cr.getGroup(), cr.getDescription(), cr.getRunCommand()) || Operation.isOperationInvalid(cr.getOperation())) {
            commandsModel.addParseException(new RuntimeException("Command parse Error. Group, Operation, Description and command itself are mandatory fields. Object: " + cr.toString()));
            logger.error("Command parse Error. Group, Operation Description and command itself are mandatory fields. Object: " + cr.toString());
        } else {
            commandsModel.addCommandResult(cr);
        }
    }

    private String getLineContent(String line) {
        return line.substring(line.indexOf(":") + 1).trim();
    }

}
