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
package com.kubehelper.domain.core;

import com.kubehelper.common.Global;
import com.kubehelper.domain.results.CronJobResult;

import java.util.concurrent.ScheduledFuture;

/**
 * The class for store scheduledFuture cron Job.
 *
 * @author JDev
 */
public class KubeHelperScheduledFuture {

    private int id;
    private String name = "";
    private String expression = "";
    private String command = "";
    private String shell = "";
    private int runs;
    private String email = "";
    private String description = "";
    private String reportsFolderPath = "";
    private ScheduledFuture<?> scheduledFuture;

    public KubeHelperScheduledFuture(CronJobResult jobResult, ScheduledFuture<?> scheduledFuture) {
        this.id = Global.CRON_JOBS.size() + 1;
        this.name = jobResult.getName();
        this.expression = jobResult.getExpression();
        this.command = jobResult.getCommand();
        this.shell = jobResult.getShell();
        this.email = jobResult.getEmail();
        this.description = jobResult.getDescription();
        this.reportsFolderPath = jobResult.getReportsFolderPath();
        this.scheduledFuture = scheduledFuture;
    }

    public boolean isDone() {
        return scheduledFuture.isDone();
    }

    public boolean shutdownCronJob() {
        return scheduledFuture.cancel(true);
    }

    public void addRun() {
        runs++;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getExpression() {
        return expression;
    }

    public String getCommand() {
        return command;
    }

    public String getShell() {
        return shell;
    }

    public int getRuns() {
        return runs;
    }

    public String getEmail() {
        return email;
    }

    public String getDescription() {
        return description;
    }

    public String getReportsFolderPath() {
        return reportsFolderPath;
    }


    public ScheduledFuture<?> getScheduledFuture() {
        return scheduledFuture;
    }

    public KubeHelperScheduledFuture setScheduledFuture(ScheduledFuture<?> scheduledFuture) {
        this.scheduledFuture = scheduledFuture;
        return this;
    }
}
