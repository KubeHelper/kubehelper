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

import com.kubehelper.domain.models.DashboardModel;
import com.kubehelper.domain.models.EventsModel;
import com.kubehelper.domain.models.FeaturesModel;
import com.kubehelper.domain.models.IpsAndPortsModel;
import com.kubehelper.domain.models.LabelsModel;
import com.kubehelper.domain.models.PageModel;
import com.kubehelper.domain.models.SearchModel;
import com.kubehelper.domain.models.SecurityModel;

import java.util.HashMap;
import java.util.Map;

/**
 * @author JDev
 */
public class Global {

    public static String DASHBOARD_MODEL = "dashboard";
    public static String SEARCH_MODEL = "search";
    public static String SECURITY_MODEL = "security";
    public static String AUDITING_MODEL = "auditing";
    public static String LABELS_MODEL = "labels";
    public static String CUSTOM_RESOURCES_MODEL = "customResources";
    public static String DEPRECATED_MODEL = "deprecated";
    public static String EVENTS_MODEL = "events";
    public static String FEATURES_MODEL = "features";
    public static String FEATURE_GATES_MODEL = "featureGates";
    public static String IPS_AND_PORTS_MODEL = "ipsAndPorts";
    public static String VOLUMES_MODEL = "volumes";

    public static Map<String, PageModel> ACTIVE_MODELS = new HashMap<>() {
    };

    public static Map<String, PageModel> NEW_MODELS = new HashMap<>() {
        {
            put(DASHBOARD_MODEL, new DashboardModel());
            put(SEARCH_MODEL, new SearchModel());
            put(SECURITY_MODEL, new SecurityModel());
            put(LABELS_MODEL, new LabelsModel());
            put(AUDITING_MODEL, new SearchModel());
            put(CUSTOM_RESOURCES_MODEL, new SearchModel());
            put(DEPRECATED_MODEL, new SearchModel());
            put(EVENTS_MODEL, new EventsModel());
            put(FEATURES_MODEL, new FeaturesModel());
            put(FEATURE_GATES_MODEL, new SearchModel());
            put(IPS_AND_PORTS_MODEL, new IpsAndPortsModel());
            put(VOLUMES_MODEL, new SearchModel());
        }
    };
}
