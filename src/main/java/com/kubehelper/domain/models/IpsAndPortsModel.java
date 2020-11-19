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
import org.zkoss.zul.ListModelList;

import java.util.ArrayList;
import java.util.List;

/**
 * @author JDev
 */
public class IpsAndPortsModel implements PageModel {

//    TODO - change name
//    private String templateUrl = "~./zul/pages/feature-gates.zul";
    private String templateUrl = "~./zul/pages/ipsandports.zul";

    public static String NAME = Global.IPS_AND_PORTS_MODEL;
    private int desktopWidth;
    private int desktopHeight;
    private String selectedNamespace = "all";
    private List<String> namespaces = new ArrayList<>();
    private ListModelList<IpsAndPortsResult> ipsAndPortsResults = new ListModelList<>();
    private IpsAndPortsFilter filter = new IpsAndPortsFilter();
    private List<KubeHelperException> searchExceptions = new ArrayList<>();


    public void addIpsAndPortsResult(IpsAndPortsResult ipsAndPortsResult) {
        ipsAndPortsResults.add(ipsAndPortsResult);
    }

    public IpsAndPortsModel setNamespaces(List<String> namespaces) {
        this.namespaces = namespaces;
        return this;
    }

    @Override
    public void setDesktopWithAndHeight(int width, int height) {
        this.desktopWidth = width;
        this.desktopHeight = height;
    }

    public void addSearchException(Exception exception) {
        this.searchExceptions.add(new KubeHelperException(exception));
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

    @Override
    public int getDesktopWidth() {
        return desktopWidth;
    }

    @Override
    public int getDesktopHeight() {
        return desktopHeight;
    }

    public ListModelList<IpsAndPortsResult> getIpsAndPortsResults() {
        return ipsAndPortsResults;
    }

    public IpsAndPortsModel setIpsAndPortsResults(ListModelList<IpsAndPortsResult> ipsAndPortsResults) {
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
