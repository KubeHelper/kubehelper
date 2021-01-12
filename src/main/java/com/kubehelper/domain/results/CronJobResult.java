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
public class CronJobResult {

    private int id;
    private String name = "";
    private String expression = "";
    private String command = "";
    private String shell = "";
    private int runs;
    private String email = "";
    private String description = "";
    private String reportsFolderPath = "";
    private boolean isDone;

    public CronJobResult(int id) {
        this.id = id;
    }

    public void buildReportsFolderPath(String root) {
        reportsFolderPath = root + name;
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

    public String getExpression() {
        return expression;
    }

    public CronJobResult setExpression(String expression) {
        this.expression = expression;
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

    public String getReportsFolderPath() {
        return reportsFolderPath;
    }

    public CronJobResult setReportsFolderPath(String reportsFolderPath) {
        this.reportsFolderPath = reportsFolderPath;
        return this;
    }

    public boolean isDone() {
        return isDone;
    }

    public CronJobResult setDone(boolean done) {
        isDone = done;
        return this;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("CronJobResult{");
        sb.append("id=").append(id);
        sb.append(", name='").append(name).append('\'');
        sb.append(", expression='").append(expression).append('\'');
        sb.append(", command='").append(command).append('\'');
        sb.append(", shell='").append(shell).append('\'');
        sb.append(", runs=").append(runs);
        sb.append(", email='").append(email).append('\'');
        sb.append(", description='").append(description).append('\'');
        sb.append(", reportsFolderPath='").append(reportsFolderPath).append('\'');
        sb.append(", isDone=").append(isDone);
        sb.append('}');
        return sb.toString();
    }
}