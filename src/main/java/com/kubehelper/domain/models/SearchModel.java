package com.kubehelper.domain.models;

import com.kubehelper.common.Global;
import com.kubehelper.common.Resource;
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
    private List<String> namespaces = new ArrayList<>();
    private ListModelList<SearchResult> searchResults = new ListModelList<>();


    public void addSearchResult(String namespace, Resource resourceType, String resourceName, String additionalInfo, String foundString, String creationTime) {
        SearchResult searchResult = new SearchResult();
        searchResult.setNamespace(namespace);
        searchResult.setResourceType(resourceType);
        searchResult.setResourceName(resourceName);
        searchResult.setAdditionalInfo(additionalInfo);
        searchResult.setFoundString(foundString);
        searchResult.setCreationTime(creationTime);
        searchResults.add(searchResult);
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
}
