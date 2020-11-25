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
import com.kubehelper.domain.filters.SearchFilter;
import com.kubehelper.domain.results.SearchResult;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author JDev
 */
public class SearchModel implements PageModel {

    private String templateUrl = "~./zul/pages/search.zul";
    public static String NAME = Global.SEARCH_MODEL;
    private String selectedNamespace = "all";
    private List<String> namespaces = new ArrayList<>();
    private List<SearchResult> searchResults = new ArrayList<>();
    private SearchFilter filter = new SearchFilter();
    private List<KubeHelperException> searchExceptions = new ArrayList<>();
    private String searchString = "";
    private boolean skipKubeNamespaces = true;
    private boolean skipNativeEnvVars = true;
//    private boolean caseSensitiveSearch = false;

    public SearchModel() {
    }

    public SearchModel addSearchResult(SearchResult searchResult) {
        searchResults.add(searchResult);
        filter.addResourceTypesFilter(searchResult.getResourceType());
        filter.addNamespacesFilter(searchResult.getNamespace());
        return this;
    }

    public SearchModel addResourceNameFilter(String resourceName) {
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

    public SearchModel setNamespaces(List<String> namespaces) {
        this.namespaces = namespaces;
        return this;
    }

    @Override
    public String getTemplateUrl() {
        return templateUrl;
    }

    public List<String> getNamespaces() {
        return namespaces;
    }

    public List<SearchResult> getSearchResults() {
        return searchResults;
    }

    public String getSelectedNamespace() {
        return selectedNamespace;
    }

    public SearchModel setSelectedNamespace(String selectedNamespace) {
        this.selectedNamespace = selectedNamespace;
        return this;
    }

    public SearchModel setSearchResults(List<SearchResult> searchResults) {
        this.searchResults = searchResults;
        return this;
    }

    public SearchFilter getFilter() {
        return filter;
    }

    public SearchModel setFilter(SearchFilter filter) {
        this.filter = filter;
        return this;
    }

    public List<KubeHelperException> getSearchExceptions() {
        return searchExceptions;
    }

    public SearchModel setSearchExceptions(List<KubeHelperException> searchExceptions) {
        this.searchExceptions = searchExceptions;
        return this;
    }

    public boolean hasSearchErrors() {
        return !searchExceptions.isEmpty();
    }

    public String getSearchString() {
        return searchString;
    }

    public SearchModel setSearchString(String searchString) {
        this.searchString = searchString;
        return this;
    }

    public boolean isSkipKubeNamespaces() {
        return skipKubeNamespaces;
    }

    public SearchModel setSkipKubeNamespaces(boolean skipKubeNamespaces) {
        this.skipKubeNamespaces = skipKubeNamespaces;
        return this;
    }

    public boolean isSkipNativeEnvVars() {
        return skipNativeEnvVars;
    }

    public SearchModel setSkipNativeEnvVars(boolean skipNativeEnvVars) {
        this.skipNativeEnvVars = skipNativeEnvVars;
        return this;
    }
}
