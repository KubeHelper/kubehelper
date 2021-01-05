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

import com.kubehelper.domain.models.CommandsModel;
import com.kubehelper.domain.models.ConfigsModel;
import com.kubehelper.domain.models.CronsModel;
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

    public static String DASHBOARD_MODEL = "dashboard";
    public static String SEARCH_MODEL = "search";
    public static String SECURITY_MODEL = "security";
    public static String LABELS_MODEL = "labels";
    public static String COMMANDS_MODEL = "commands";
    public static String IPS_AND_PORTS_MODEL = "ipsAndPorts";
    public static String CRONS_MODEL = "crons";
    public static String CONFIGS_MODEL = "configs";
    public static String VERSIONS_MODEL = "versions";

    public static String AUTH_EXCEPTION_MESSAGE = "";
    public static String PATH_TO_RAW_RESOURCE_ZUL = "~./zul/kubehelper/components/raw-resource.zul";
    public static String PATH_TO_ERROR_RESOURCE_ZUL = "~./zul/kubehelper/components/errors.zul";


    public static Map<String, PageModel> ACTIVE_MODELS = new HashMap<>() {
    };

    public static Map<String, PageModel> NEW_MODELS = new HashMap<>() {
        {
            put(DASHBOARD_MODEL, new DashboardModel());
            put(SEARCH_MODEL, new SearchModel());
            put(SECURITY_MODEL, new SecurityModel());
            put(LABELS_MODEL, new LabelsModel());
            put(COMMANDS_MODEL, new CommandsModel());
            put(IPS_AND_PORTS_MODEL, new IpsAndPortsModel());
            put(CRONS_MODEL, new CronsModel());
            put(CONFIGS_MODEL, new ConfigsModel());
            put(VERSIONS_MODEL, new VersionsModel());
        }
    };
}
