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
import com.kubehelper.domain.filters.EventsFilter;
import com.kubehelper.domain.results.EventResult;
import org.apache.commons.lang3.StringUtils;
import org.zkoss.zul.ListModelList;

import java.util.ArrayList;
import java.util.List;

/**
 * @author JDev
 */
public class EventsModel implements PageModel {

    private String templateUrl = "~./zul/pages/events.zul";

    public static String NAME = Global.EVENTS_MODEL;
    private int desktopWidth;
    private int desktopHeight;
    private String selectedNamespace = "all";
    private List<String> namespaces = new ArrayList<>();
    private ListModelList<EventResult> searchResults = new ListModelList<>();
    private EventsFilter filter = new EventsFilter();
    private List<KubeHelperException> searchExceptions = new ArrayList<>();


    public EventsModel addSearchResult(EventResult eventResult) {
        searchResults.add(eventResult);
        filter.addResourceTypesFilter(eventResult.getResourceType());
        filter.addNamespacesFilter(eventResult.getNamespace());
        return this;
    }

    public EventsModel setNamespaces(List<String> namespaces) {
        this.namespaces = namespaces;
        return this;
    }

    @Override
    public void setDesktopWithAndHeight(int width, int height) {
        this.desktopWidth = width;
        this.desktopHeight = height;
    }


    public EventsModel addResourceNameFilter(String resourceName) {
        if (StringUtils.isNotBlank(resourceName)) {
            filter.addResourceNamesFilter(resourceName);
        }
        return this;
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

    public ListModelList<EventResult> getSearchResults() {
        return searchResults;
    }

    public EventsModel setSearchResults(ListModelList<EventResult> searchResults) {
        this.searchResults = searchResults;
        return this;
    }

    public EventsFilter getFilter() {
        return filter;
    }

    public EventsModel setFilter(EventsFilter filter) {
        this.filter = filter;
        return this;
    }

    public String getSelectedNamespace() {
        return selectedNamespace;
    }

    public EventsModel setSelectedNamespace(String selectedNamespace) {
        this.selectedNamespace = selectedNamespace;
        return this;
    }

    public List<KubeHelperException> getSearchExceptions() {
        return searchExceptions;
    }

    public EventsModel setSearchExceptions(List<KubeHelperException> searchExceptions) {
        this.searchExceptions = searchExceptions;
        return this;
    }

    public boolean hasSearchErrors() {
        return !searchExceptions.isEmpty();
    }
}
