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
package com.kubehelper.domain.filters;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author JDev
 */
public class CommandsFilter {
    private String description = "", group = "", command = "";

    private String selectedGroupFilter = "";
    private String selectedOperationFilter = "";

    private List<String> groupsFilter = new ArrayList<>();
    private List<String> operationsFilter = new ArrayList<>();

    public CommandsFilter() {
    }

    public void addGroupFilter(String groupFilter) {
        if (!groupsFilter.contains(groupFilter)) {
            groupsFilter.add(groupFilter);
        }
    }

    public void addOperationFilter(String operationFilter) {
        if (!operationsFilter.contains(operationFilter)) {
            operationsFilter.add(operationFilter);
        }
    }

    public boolean isFilterActive() {
        return StringUtils.isNoneBlank(description, group, command, selectedGroupFilter, selectedOperationFilter);
    }

    public String getDescription() {
        return description;
    }

    public CommandsFilter setDescription(String description) {
        this.description = description == null ? "" : description;
        return this;
    }

    public String getCommand() {
        return command;
    }

    public CommandsFilter setCommand(String command) {
        this.command = command == null ? "" : command;
        return this;
    }



    public String getSelectedGroupFilter() {
        return selectedGroupFilter;
    }

    public CommandsFilter setSelectedGroupFilter(String selectedGroupFilter) {
        this.selectedGroupFilter = selectedGroupFilter == null ? "" : selectedGroupFilter;
        return this;
    }

    public String getSelectedOperationFilter() {
        return selectedOperationFilter;
    }

    public CommandsFilter setSelectedOperationFilter(String selectedOperationFilter) {
        this.selectedOperationFilter = selectedOperationFilter == null ? "" : selectedOperationFilter;
        return this;
    }

    public List<String> getGroupsFilter() {
        return groupsFilter;
    }

    public List<String> getOperationsFilter() {
        return operationsFilter;
    }
}
