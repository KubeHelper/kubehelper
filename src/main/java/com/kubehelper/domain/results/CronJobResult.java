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

import java.util.StringJoiner;

/**
 * @author JDev
 */
public class CronJobResult {

    private int id;
    private String name = "";
    private String period = "";
    private String command = "";
    private String shell = "";
    private int runs;
    private String email = "";
    private String description = "";

    public CronJobResult(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public CronJobResult setName(String name) {
        this.name = name;
        return this;
    }

    public String getPeriod() {
        return period;
    }

    public CronJobResult setPeriod(String period) {
        this.period = period;
        return this;
    }

    public String getCommand() {
        return command;
    }

    public CronJobResult setCommand(String command) {
        this.command = command;
        return this;
    }

    public int getRuns() {
        return runs;
    }

    public CronJobResult setRuns(int runs) {
        this.runs = runs;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public CronJobResult setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public CronJobResult setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getShell() {
        return shell;
    }

    public CronJobResult setShell(String shell) {
        this.shell = shell;
        return this;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", CronJobResult.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("name='" + name + "'")
                .add("period='" + period + "'")
                .add("command='" + command + "'")
                .add("shell='" + shell + "'")
                .add("runs=" + runs)
                .add("email='" + email + "'")
                .add("description='" + description + "'")
                .toString();
    }
}