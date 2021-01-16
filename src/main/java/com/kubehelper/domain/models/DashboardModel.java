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
import com.kubehelper.domain.results.ClusterResult;
import com.kubehelper.domain.results.NodeResult;

import java.util.ArrayList;
import java.util.List;

/**
 * @author JDev
 */
public class DashboardModel implements PageModel {

    private String templateUrl = "~./zul/kubehelper/pages/dashboard.zul";
    public static String NAME = Global.DASHBOARD_MODEL;

    private ClusterResult clusterResult = new ClusterResult();
    private List<NodeResult> nodesResults = new ArrayList<>();

    private List<KubeHelperException> exceptions = new ArrayList<>();

    public void addNodeResult(NodeResult nodeResult) {
        this.nodesResults.add(nodeResult);
    }

    @Override
    public String getTemplateUrl() {
        return templateUrl;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void addException(String message, Exception exception) {
        this.exceptions.add(new KubeHelperException(message, exception));
    }

    public ClusterResult getClusterResult() {
        return clusterResult;
    }

    public DashboardModel setClusterResult(ClusterResult clusterResult) {
        this.clusterResult = clusterResult;
        return this;
    }

    public List<NodeResult> getNodesResults() {
        return nodesResults;
    }

    public List<KubeHelperException> getExceptions() {
        return exceptions;
    }

    public DashboardModel setExceptions(List<KubeHelperException> exceptions) {
        this.exceptions = exceptions;
        return this;
    }

    public boolean hasExceptions() {
        return !exceptions.isEmpty();
    }
}
