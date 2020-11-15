package com.kubehelper.domain.models;

import com.kubehelper.common.Global;
import com.kubehelper.common.KubeHelperException;
import com.kubehelper.domain.filters.LabelsFilter;
import com.kubehelper.domain.results.LabelResult;
import org.apache.commons.lang3.StringUtils;
import org.zkoss.zul.ListModelList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author JDev
 */
public class LabelsModel implements PageModel {

    private String templateUrl = "~./zul/pages/labels.zul";
    public static String NAME = Global.LABELS_MODEL;
    private int desktopWidth;
    private int desktopHeight;
    private String selectedNamespace = "all";
    private List<String> namespaces = new ArrayList<>();
    private ListModelList<LabelResult> searchResults = new ListModelList<>();
    private LabelsFilter filter = new LabelsFilter();
    private List<KubeHelperException> searchExceptions = new ArrayList<>();
    private boolean skipKubeNamespaces = true;
    private boolean skipHashLabels = true;
    private Map<String, List<LabelResult>> groupedSearchResults = new HashMap<>();
    private List<GroupedLabel> groupedLabels = new ArrayList<>();

    public LabelsModel() {
    }

    public LabelsModel addSearchResult(LabelResult searchResult) {
        searchResults.add(searchResult);
        filter.addResourceTypesFilter(searchResult.getResourceType());
        filter.addResourcePropertiesFilter(searchResult.getResourceProperty());
        filter.addNamespacesFilter(searchResult.getNamespace());
        return this;
    }

    public LabelsModel addResourceNameFilter(String resourceName) {
        if (StringUtils.isNotBlank(resourceName)) {
            filter.addResourceNamesFilter(resourceName);
        }
        return this;
    }

    public void groupSearchResults() {
        groupedSearchResults.clear();
        groupedLabels.clear();
        for (LabelResult searchResult : searchResults) {
            if (groupedSearchResults.containsKey(searchResult.getName())) {
                groupedSearchResults.get(searchResult.getName()).add(searchResult);
                incrementFoundSearchResult(searchResult.getName());
            } else {
                groupedSearchResults.put(searchResult.getName(), new ArrayList<>(Arrays.asList(searchResult)));
                groupedLabels.add(new GroupedLabel(groupedLabels.size() + 1).setName(searchResult.getName()).setAmount(1));
            }
        }
    }

    private void incrementFoundSearchResult(String name) {
        for (GroupedLabel groupedLabel : groupedLabels) {
            if (groupedLabel.getName().equals(name)) {
                groupedLabel.setAmount(groupedLabel.getAmount() + 1);
                break;
            }
        }
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

    public LabelsModel setNamespaces(List<String> namespaces) {
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

    public ListModelList<LabelResult> getSearchResults() {
        return searchResults;
    }

    public String getSelectedNamespace() {
        return selectedNamespace;
    }

    public LabelsModel setSelectedNamespace(String selectedNamespace) {
        this.selectedNamespace = selectedNamespace;
        return this;
    }

    public LabelsModel setSearchResults(ListModelList<LabelResult> searchResults) {
        this.searchResults = searchResults;
        return this;
    }

    public LabelsFilter getFilter() {
        return filter;
    }

    public LabelsModel setFilter(LabelsFilter filter) {
        this.filter = filter;
        return this;
    }

    public List<KubeHelperException> getSearchExceptions() {
        return searchExceptions;
    }

    public LabelsModel setSearchExceptions(List<KubeHelperException> searchExceptions) {
        this.searchExceptions = searchExceptions;
        return this;
    }

    public boolean hasSearchErrors() {
        return !searchExceptions.isEmpty();
    }

    public boolean isSkipKubeNamespaces() {
        return skipKubeNamespaces;
    }

    public LabelsModel setSkipKubeNamespaces(boolean skipKubeNamespaces) {
        this.skipKubeNamespaces = skipKubeNamespaces;
        return this;
    }

    public boolean isSkipHashLabels() {
        return skipHashLabels;
    }

    public LabelsModel setSkipHashLabels(boolean skipHashLabels) {
        this.skipHashLabels = skipHashLabels;
        return this;
    }

    public List<GroupedLabel> getGroupedLabels() {
        return groupedLabels;
    }

    public List<LabelResult> getGroupedLabelDetail(String name) {
        return groupedSearchResults.get(name);
    }

    public class GroupedLabel {
        private int id;
        private String name = "";
        private int amount;

        public GroupedLabel(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public GroupedLabel setId(int id) {
            this.id = id;
            return this;
        }

        public String getName() {
            return name;
        }

        public GroupedLabel setName(String name) {
            this.name = name;
            return this;
        }

        public int getAmount() {
            return amount;
        }

        public GroupedLabel setAmount(int amount) {
            this.amount = amount;
            return this;
        }
    }
}


