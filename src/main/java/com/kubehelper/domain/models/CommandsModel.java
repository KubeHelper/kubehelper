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
package com.kubehelper.domain.models;

import com.kubehelper.common.Global;
import com.kubehelper.common.KubeHelperException;
import com.kubehelper.domain.filters.CommandsFilter;
import com.kubehelper.domain.results.CommandsResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author JDev
 */
public class CommandsModel implements PageModel {

    private String templateUrl = "~./zul/pages/commands.zul";

//    private String userCommandsPath = "C:\\temp\\kubehelper";
        private String userCommandsPath = "/tmp/kubehelper";
    public static String NAME = Global.COMMANDS_MODEL;
    private List<String> namespaces = new ArrayList<>();
    private List<CommandsResult> commandsResults = new ArrayList<>();
    private CommandsFilter filter = new CommandsFilter();
    private List<KubeHelperException> buildExceptions = new ArrayList<>();
    private String selectedNamespace = "all";
    private String selectedCommandsSourceLabel = "";
    private String selectedCommandsSourceRaw = "";

    private Map<String, CommandSource> commandsSources = new HashMap<>() {
    };


    public CommandsModel() {
    }

    public void addCommandResult(CommandsResult commandResult) {
        commandsResults.add(commandResult);
        filter.addGroupFilter(commandResult.getGroup());
        filter.addOperationFilter(commandResult.getOperation());
    }

    public void addCommandSource(String label, String filePath, String rawSource) {
        CommandSource commandSource = new CommandSource();
        commandSource.setLabel(label);
        commandSource.setFilePath(filePath);
        commandSource.setRawSource(rawSource);
        commandsSources.put(label, commandSource);
    }


    public void addParseException(Exception exception) {
        this.buildExceptions.add(new KubeHelperException(exception));
    }

    @Override
    public void addException(String message, Exception exception) {
        this.buildExceptions.add(new KubeHelperException(message, exception));
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getTemplateUrl() {
        return templateUrl;
    }

    public List<String> getNamespaces() {
        return namespaces;
    }

    public CommandsModel setNamespaces(List<String> namespaces) {
        this.namespaces = namespaces;
        return this;
    }

    public List<CommandsResult> getCommandsResults() {
        return commandsResults;
    }

    public CommandsModel setCommandsResults(List<CommandsResult> commandsResults) {
        this.commandsResults = commandsResults;
        return this;
    }

    public List<KubeHelperException> getBuildExceptions() {
        return buildExceptions;
    }

    public CommandsModel setBuildExceptions(List<KubeHelperException> buildExceptions) {
        this.buildExceptions = buildExceptions;
        return this;
    }

    public boolean hasBuildErrors() {
        return !buildExceptions.isEmpty();
    }

    public CommandsFilter getFilter() {
        return filter;
    }

    public CommandsModel setFilter(CommandsFilter filter) {
        this.filter = filter;
        return this;
    }

    public String getSelectedNamespace() {
        return selectedNamespace;
    }

    public CommandsModel setSelectedNamespace(String selectedNamespace) {
        this.selectedNamespace = selectedNamespace;
        return this;
    }

    public String getSelectedCommandsSourceRaw() {
        return selectedCommandsSourceRaw;
    }

    public CommandsModel setSelectedCommandsSourceRaw(String selectedCommandsSourceRaw) {
        this.selectedCommandsSourceRaw = selectedCommandsSourceRaw;
        return this;
    }

    public String getUserCommandsPath() {
        return userCommandsPath;
    }

    public CommandsModel setUserCommandsPath(String userCommandsPath) {
        this.userCommandsPath = userCommandsPath;
        return this;
    }

    public CommandsModel setCommandsSources(Map<String, CommandSource> commandsSources) {
        this.commandsSources = commandsSources;
        return this;
    }

    public Map<String, CommandSource> getCommandsSources() {
        return commandsSources;
    }

    public String getSelectedCommandsSourceLabel() {
        return selectedCommandsSourceLabel;
    }

    public CommandsModel setSelectedCommandsSourceLabel(String selectedCommandsSourceLabel) {
        this.selectedCommandsSourceLabel = selectedCommandsSourceLabel;
        return this;
    }

    public class CommandSource {
        private String label;
        private String filePath;
        private String rawSource;

        public String getLabel() {
            return label;
        }

        public CommandSource setLabel(String label) {
            this.label = label;
            return this;
        }

        public String getFilePath() {
            return filePath;
        }

        public CommandSource setFilePath(String filePath) {
            this.filePath = filePath;
            return this;
        }

        public String getRawSource() {
            return rawSource;
        }

        public CommandSource setRawSource(String rawSource) {
            this.rawSource = rawSource;
            return this;
        }
    }
}
