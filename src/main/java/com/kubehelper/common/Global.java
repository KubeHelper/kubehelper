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
package com.kubehelper.common;

import com.kubehelper.domain.core.KubeHelperConfig;
import com.kubehelper.domain.core.KubeHelperScheduledFuture;
import com.kubehelper.domain.models.CommandsModel;
import com.kubehelper.domain.models.ConfigsModel;
import com.kubehelper.domain.models.CronJobsModel;
import com.kubehelper.domain.models.DashboardModel;
import com.kubehelper.domain.models.IpsAndPortsModel;
import com.kubehelper.domain.models.LabelsModel;
import com.kubehelper.domain.models.PageModel;
import com.kubehelper.domain.models.SearchModel;
import com.kubehelper.domain.models.SecurityModel;
import com.kubehelper.domain.models.VersionsModel;

import java.util.HashMap;
import java.util.Map;

/**
 * @author JDev
 */
public class Global {

    public static final String DASHBOARD_MODEL = "dashboard";
    public static final String SEARCH_MODEL = "search";
    public static final String SECURITY_MODEL = "security";
    public static final String LABELS_MODEL = "labels";
    public static final String COMMANDS_MODEL = "commands";
    public static final String IPS_AND_PORTS_MODEL = "ipsAndPorts";
    public static final String CRON_JOBS_MODEL = "cronJobs";
    public static final String CONFIGS_MODEL = "configs";
    public static final String VERSIONS_MODEL = "versions";

    public static final String CONFIGS_CACHE = "configs";
    public static final String CONFIG_FILENAME = "kubehelper-config.toml";

    public static String AUTH_EXCEPTION_MESSAGE = "";
    public static String PATH_TO_RAW_RESOURCE_ZUL = "~./zul/kubehelper/components/raw-resource.zul";
    public static String PATH_TO_ERROR_RESOURCE_ZUL = "~./zul/kubehelper/components/errors.zul";
    public static String PATH_TO_CONFIG_FILE = "";

    public static HashMap<String, KubeHelperScheduledFuture> CRON_JOBS = new HashMap<>();
    public static KubeHelperConfig config;
    public static String configString;

    public static Map<String, PageModel> ACTIVE_MODELS = new HashMap<>();
    public static Map<String, PageModel> NEW_MODELS = new HashMap<>();

    static {
        initModels();
    }

    public static void initModels() {
        NEW_MODELS.put(DASHBOARD_MODEL, new DashboardModel());
        NEW_MODELS.put(SEARCH_MODEL, new SearchModel());
        NEW_MODELS.put(SECURITY_MODEL, new SecurityModel());
        NEW_MODELS.put(LABELS_MODEL, new LabelsModel());
        NEW_MODELS.put(COMMANDS_MODEL, new CommandsModel());
        NEW_MODELS.put(IPS_AND_PORTS_MODEL, new IpsAndPortsModel());
        NEW_MODELS.put(CRON_JOBS_MODEL, new CronJobsModel());
        NEW_MODELS.put(CONFIGS_MODEL, new ConfigsModel());
        NEW_MODELS.put(VERSIONS_MODEL, new VersionsModel());
    }
}
