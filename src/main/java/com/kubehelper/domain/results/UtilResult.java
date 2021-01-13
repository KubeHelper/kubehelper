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

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

/**
 * @author JDev
 */
public class UtilResult {

    private int id;
    private String name = "";
    private String version = "";
    private String shellCommand = "";
    private String versionCheckCommand = "";
    private String category = "";
    private String description = "";
    private List<String> links = new ArrayList<>();


    public UtilResult(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public UtilResult setName(String name) {
        this.name = name;
        return this;
    }

    public String getVersion() {
        return version;
    }

    public UtilResult setVersion(String version) {
        this.version = version;
        return this;
    }

    public String getShellCommand() {
        return shellCommand;
    }

    public UtilResult setShellCommand(String shellCommand) {
        this.shellCommand = shellCommand;
        return this;
    }

    public String getVersionCheckCommand() {
        return versionCheckCommand;
    }

    public UtilResult setVersionCheckCommand(String versionCheckCommand) {
        this.versionCheckCommand = versionCheckCommand;
        return this;
    }

    public String getCategory() {
        return category;
    }

    public UtilResult setCategory(String category) {
        this.category = category;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public UtilResult setDescription(String description) {
        this.description = description;
        return this;
    }

    public List<String> getLinks() {
        return links;
    }

    public UtilResult setLinks(List<String> links) {
        this.links = links;
        return this;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", UtilResult.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("name='" + name + "'")
                .add("version='" + version + "'")
                .add("shellCommand='" + shellCommand + "'")
                .add("versionCheckCommand='" + versionCheckCommand + "'")
                .add("category='" + category + "'")
                .add("description='" + description + "'")
                .add("links=" + links)
                .toString();
    }
}