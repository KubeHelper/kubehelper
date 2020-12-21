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
import com.kubehelper.domain.filters.IpsAndPortsFilter;
import com.kubehelper.domain.results.IpsAndPortsResult;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

/**
 * @author JDev
 */
public class IpsAndPortsModel implements PageModel {

    private int mainGridHeight = 600;
    private PropertyChangeSupport grid = new PropertyChangeSupport(this);

    private String templateUrl = "~./zul/pages/ipsandports.zul";

    public static String NAME = Global.IPS_AND_PORTS_MODEL;
    private String selectedNamespace = "all";
    private List<String> namespaces = new ArrayList<>();
    private List<IpsAndPortsResult> ipsAndPortsResults = new ArrayList<>();
    private IpsAndPortsFilter filter = new IpsAndPortsFilter();
    private List<KubeHelperException> searchExceptions = new ArrayList<>();

    @Override
    public void setPageMainContentHeight(int newHeight) {
        int oldMainGridHeight = this.mainGridHeight;
        this.mainGridHeight = newHeight;
        grid.firePropertyChange(null, oldMainGridHeight, newHeight);
    }

    public int getMainGridHeight() {
        return mainGridHeight;
    }

    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        grid.addPropertyChangeListener(pcl);
    }

    public void addIpsAndPortsResult(IpsAndPortsResult ipsAndPortsResult) {
        ipsAndPortsResults.add(ipsAndPortsResult);
        filter.addNamespacesFilter(ipsAndPortsResult.getNamespace());
        filter.addResourceTypesFilter(ipsAndPortsResult.getResourceType());
    }

    public IpsAndPortsModel setNamespaces(List<String> namespaces) {
        this.namespaces = namespaces;
        return this;
    }

    public void addSearchException(Exception exception) {
        this.searchExceptions.add(new KubeHelperException(exception));
    }

    @Override
    public void addException(String message, Exception exception) {
        this.searchExceptions.add(new KubeHelperException(message, exception));
    }

    @Override
    public String getName() {
        return NAME;
    }

    public List<String> getNamespaces() {
        return namespaces;
    }

    @Override
    public String getTemplateUrl() {
        return templateUrl;
    }

    public List<IpsAndPortsResult> getIpsAndPortsResults() {
        return ipsAndPortsResults;
    }

    public IpsAndPortsModel setIpsAndPortsResults(List<IpsAndPortsResult> ipsAndPortsResults) {
        this.ipsAndPortsResults = ipsAndPortsResults;
        return this;
    }

    public IpsAndPortsFilter getFilter() {
        return filter;
    }

    public IpsAndPortsModel setFilter(IpsAndPortsFilter filter) {
        this.filter = filter;
        return this;
    }

    public String getSelectedNamespace() {
        return selectedNamespace;
    }

    public IpsAndPortsModel setSelectedNamespace(String selectedNamespace) {
        this.selectedNamespace = selectedNamespace;
        return this;
    }

    public List<KubeHelperException> getSearchExceptions() {
        return searchExceptions;
    }

    public IpsAndPortsModel setSearchExceptions(List<KubeHelperException> searchExceptions) {
        this.searchExceptions = searchExceptions;
        return this;
    }

    public boolean hasSearchErrors() {
        return !searchExceptions.isEmpty();
    }
}
