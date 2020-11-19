package com.kubehelper.domain.models;

import com.kubehelper.common.Global;
import com.kubehelper.common.KubeHelperException;
import com.kubehelper.domain.filters.SearchFilter;
import com.kubehelper.domain.results.SearchResult;
import org.apache.commons.lang3.StringUtils;
import org.zkoss.zul.ListModelList;

import java.util.ArrayList;
import java.util.List;

/**
 * @author JDev
 */
public class SecurityModel implements PageModel {

    private String templateUrl = "~./zul/pages/security.zul";
    public static String NAME = Global.SEARCH_MODEL;
    private int desktopWidth;
    private int desktopHeight;
    private String selectedNamespace = "all";
    private List<String> namespaces = new ArrayList<>();
    private ListModelList<SearchResult> searchResults = new ListModelList<>();
    private SearchFilter filter = new SearchFilter();
    private List<KubeHelperException> searchExceptions = new ArrayList<>();
    private String searchString = "";
    private boolean skipKubeNamespaces = true;
    private boolean skipNativeEnvVars = true;
//    private boolean caseSensitiveSearch = false;

    public SecurityModel() {
    }

    public SecurityModel addSearchResult(SearchResult searchResult) {
        searchResults.add(searchResult);
        filter.addResourceTypesFilter(searchResult.getResourceType());
        filter.addNamespacesFilter(searchResult.getNamespace());
        return this;
    }

    public SecurityModel addResourceNameFilter(String resourceName) {
        if (StringUtils.isNotBlank(resourceName)) {
            filter.addResourceNamesFilter(resourceName);
        }
        return this;
    }

    public void addSearchException(Exception exception) {
        this.searchExceptions.add(new KubeHelperException(exception));
    }

    @Override
    public void setDesktopWithAndHeight(int width, int height) {
        this.desktopWidth = width;
        this.desktopHeight = height;
    }

    @Override
    public String getName() {
        return NAME;
    }

    public SecurityModel setNamespaces(List<String> namespaces) {
        this.namespaces = namespaces;
        return this;
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

    public List<String> getNamespaces() {
        return namespaces;
    }

    public ListModelList<SearchResult> getSearchResults() {
        return searchResults;
    }

    public String getSelectedNamespace() {
        return selectedNamespace;
    }

    public SecurityModel setSelectedNamespace(String selectedNamespace) {
        this.selectedNamespace = selectedNamespace;
        return this;
    }

    public SecurityModel setSearchResults(ListModelList<SearchResult> searchResults) {
        this.searchResults = searchResults;
        return this;
    }

    public SearchFilter getFilter() {
        return filter;
    }

    public SecurityModel setFilter(SearchFilter filter) {
        this.filter = filter;
        return this;
    }

    public List<KubeHelperException> getSearchExceptions() {
        return searchExceptions;
    }

    public SecurityModel setSearchExceptions(List<KubeHelperException> searchExceptions) {
        this.searchExceptions = searchExceptions;
        return this;
    }

    public boolean hasSearchErrors() {
        return !searchExceptions.isEmpty();
    }

    public String getSearchString() {
        return searchString;
    }

    public SecurityModel setSearchString(String searchString) {
        this.searchString = searchString;
        return this;
    }

    public boolean isSkipKubeNamespaces() {
        return skipKubeNamespaces;
    }

    public SecurityModel setSkipKubeNamespaces(boolean skipKubeNamespaces) {
        this.skipKubeNamespaces = skipKubeNamespaces;
        return this;
    }

    public boolean isSkipNativeEnvVars() {
        return skipNativeEnvVars;
    }

    public SecurityModel setSkipNativeEnvVars(boolean skipNativeEnvVars) {
        this.skipNativeEnvVars = skipNativeEnvVars;
        return this;
    }
}
