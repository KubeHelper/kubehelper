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
package com.kubehelper.domain.results;

/**
 * @author JDev
 */
public class CommandsResult {
    private int id;
    private String group = "";
    private String command = "";
    private String name = "";
    private String description = "";
    private String file = "";

    public CommandsResult(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getGroup() {
        return group;
    }

    public CommandsResult setGroup(String group) {
        this.group = group;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public CommandsResult setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getCommand() {
        return command;
    }

    public CommandsResult setCommand(String command) {
        this.command = command;
        return this;
    }

    public String getFile() {
        return file;
    }

    public CommandsResult setFile(String file) {
        this.file = file;
        return this;
    }

    public String getName() {
        return name;
    }

    public CommandsResult setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("CommandsResult{");
        sb.append("id=").append(id);
        sb.append(", group='").append(group).append('\'');
        sb.append(", command='").append(command).append('\'');
        sb.append(", name='").append(name).append('\'');
        sb.append(", description='").append(description).append('\'');
        sb.append(", file='").append(file).append('\'');
        sb.append('}');
        return sb.toString();
    }
}