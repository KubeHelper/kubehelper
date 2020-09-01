package com.kubehelper.model;

import com.kubehelper.common.Resource;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;

import java.util.ArrayList;
import java.util.List;

/**
 * @author JDev
 */
public class SearchModel implements PageModel {

    private String templateUrl = "~./zul/pages/search.zul";

    private String name = "name";
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

    public SearchModel setNamespaces(List<String> namespaces) {
        this.namespaces = namespaces;
        return this;
    }

    @Override
    public String getTemplateUrl() {
        return templateUrl;
    }

    @Override
    public String getName() {
        return name;
    }

    public List<String> getNamespaces() {
        return namespaces;
    }

    public ListModelList<SearchResult> getSearchResults() {
        return searchResults;
    }
}
