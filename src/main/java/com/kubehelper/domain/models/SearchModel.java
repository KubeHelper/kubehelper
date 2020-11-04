package com.kubehelper.domain.models;

import com.kubehelper.common.Global;
import com.kubehelper.domain.filters.SearchFilter;
import com.kubehelper.domain.results.SearchResult;
import org.zkoss.zul.ListModelList;

import java.util.ArrayList;
import java.util.List;

/**
 * @author JDev
 */
public class SearchModel implements PageModel {

    private String templateUrl = "~./zul/pages/search.zul";
    public static String NAME = Global.SEARCH_MODEL;
    private int desktopWidth;
    private int desktopHeight;
    private String selectedNamespace = "all";
    private List<String> namespaces = new ArrayList<>();
    private ListModelList<SearchResult> searchResults = new ListModelList<>();
    private SearchFilter filter = new SearchFilter();

    public SearchModel() {
    }

    public SearchModel addSearchResult(SearchResult searchResult) {
        searchResults.add(searchResult);
        filter.addResourceTypesFilter(searchResult.getResourceType());
        filter.addNamespacesFilter(searchResult.getNamespace());
        return this;
    }

    public SearchModel addResourceNameFilter(String resourceName) {
        filter.addResourceNamesFilter(resourceName);
        return this;
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

    public SearchModel setNamespaces(List<String> namespaces) {
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

    public SearchModel setSelectedNamespace(String selectedNamespace) {
        this.selectedNamespace = selectedNamespace;
        return this;
    }

    public SearchModel setSearchResults(ListModelList<SearchResult> searchResults) {
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
}
