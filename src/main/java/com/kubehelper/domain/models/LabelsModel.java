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
import com.kubehelper.common.Resource;
import com.kubehelper.common.ResourceProperty;
import com.kubehelper.domain.filters.LabelsFilter;
import com.kubehelper.domain.filters.LabelsGroupedColumnsFilter;
import com.kubehelper.domain.filters.LabelsGroupedFilter;
import com.kubehelper.domain.results.LabelResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author JDev
 */
public class LabelsModel implements PageModel {

    private final String templateUrl = "~./zul/kubehelper/pages/labels.zul";
    public static String NAME = Global.LABELS_MODEL;
    private String selectedNamespace = "all";
    private List<String> namespaces = new ArrayList<>();
    private List<LabelResult> searchResults = new ArrayList<>();
    private LabelsFilter filter = new LabelsFilter();
    private LabelsGroupedColumnsFilter groupedColumnsFilter = new LabelsGroupedColumnsFilter();
    private LabelsGroupedFilter groupedFilter = new LabelsGroupedFilter();
    private List<KubeHelperException> searchExceptions = new ArrayList<>();
    private boolean skipKubeNamespaces = true;
    private boolean skipHashLabels = true;
    private Map<String, List<LabelResult>> groupedSearchResults = new HashMap<>();
    private List<GroupedLabel> groupedLabels = new ArrayList<>();
    private List<GroupedLabelColumn> groupedLabelsColumns = new ArrayList<>();
    private String clickedLabelsGroup = "";

    public LabelsModel() {
    }

    public LabelsModel addSearchResult(LabelResult searchResult) {
        searchResults.add(searchResult);
        filter.addResourceTypesFilter(searchResult.getResourceType());
        filter.addResourcePropertiesFilter(searchResult.getResourceProperty());
        filter.addNamespacesFilter(searchResult.getNamespace());
        filter.addResourceNamesFilter(searchResult.getResourceName());
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
                groupedSearchResults.put(searchResult.getName(), new ArrayList<>(Collections.singletonList(searchResult)));
                groupedLabels.add(new GroupedLabel(groupedLabels.size() + 1).setName(searchResult.getName()).setAmount("1"));
            }
        }
    }

    public void reGroupSearchResultsAfterFilter(List<LabelResult> filteredSearchResults) {
        groupedSearchResults.clear();
        groupedLabels.clear();
        for (LabelResult searchResult : filteredSearchResults) {
            if (groupedSearchResults.containsKey(searchResult.getName())) {
                groupedSearchResults.get(searchResult.getName()).add(searchResult);
                incrementFoundSearchResult(searchResult.getName());
            } else {
                groupedSearchResults.put(searchResult.getName(), new ArrayList<>(Collections.singletonList(searchResult)));
                groupedLabels.add(new GroupedLabel(groupedLabels.size() + 1).setName(searchResult.getName()).setAmount("1"));
            }
        }
    }

    public LabelsModel setGroupedLabels(List<GroupedLabel> groupedLabels) {
        this.groupedLabels = groupedLabels;
        return this;
    }

    public List<GroupedLabelColumn> getGroupedLabelsColumns() {
        return groupedLabelsColumns;
    }

    public LabelsModel setGroupedLabelsColumns(List<GroupedLabelColumn> groupedLabelsColumns) {
        this.groupedLabelsColumns = groupedLabelsColumns;
        return this;
    }

    public void fillGroupedLabelsColumnsForGroup(LabelsModel.GroupedLabel item) {
        clickedLabelsGroup = item.getName().length() > 100 ? item.getName().substring(0, 100) + "..." : item.getName();
        this.groupedLabelsColumns.clear();
        this.groupedColumnsFilter = new LabelsGroupedColumnsFilter();
        for (LabelResult labelResult : groupedSearchResults.get(item.getName())) {
            this.groupedLabelsColumns.add(new GroupedLabelColumn(groupedLabelsColumns.size() + 1)
                    .setNamespace(labelResult.getNamespace())
                    .setResourceName(labelResult.getResourceName())
                    .setResourceType(labelResult.getEnumResourceType())
                    .setAdditionalInfo(labelResult.getAdditionalInfo())
                    .setResourceProperty(labelResult.getEnumResourceProperty()));

            this.groupedColumnsFilter.addNamespacesFilter(labelResult.getNamespace())
                    .addResourceNamesFilter(labelResult.getResourceName())
                    .addResourcePropertiesFilter(labelResult.getResourceProperty())
                    .addResourceTypesFilter(labelResult.getResourceType());
        }
    }

    private void incrementFoundSearchResult(String name) {
        for (GroupedLabel groupedLabel : groupedLabels) {
            if (groupedLabel.getName().equals(name)) {
                groupedLabel.setAmount(String.valueOf(Integer.parseInt(groupedLabel.getAmount()) + 1));
                break;
            }
        }
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

    public LabelsModel setNamespaces(List<String> namespaces) {
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

    public List<LabelResult> getSearchResults() {
        return searchResults;
    }

    public String getSelectedNamespace() {
        return selectedNamespace;
    }

    public LabelsModel setSelectedNamespace(String selectedNamespace) {
        this.selectedNamespace = selectedNamespace;
        return this;
    }

    public LabelsModel setSearchResults(List<LabelResult> searchResults) {
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

    public LabelsGroupedColumnsFilter getGroupedColumnsFilter() {
        return groupedColumnsFilter;
    }

    public LabelsModel setGroupedColumnsFilter(LabelsGroupedColumnsFilter groupedColumnsFilter) {
        this.groupedColumnsFilter = groupedColumnsFilter;
        return this;
    }

    public LabelsGroupedFilter getGroupedFilter() {
        return groupedFilter;
    }

    public LabelsModel setGroupedFilter(LabelsGroupedFilter groupedFilter) {
        this.groupedFilter = groupedFilter;
        return this;
    }

    public Map<String, List<LabelResult>> getGroupedSearchResults() {
        return groupedSearchResults;
    }

    public void setGroupedSearchResults(Map<String, List<LabelResult>> groupedSearchResults) {
        this.groupedSearchResults = groupedSearchResults;
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

    public String getClickedLabelsGroup() {
        return clickedLabelsGroup;
    }

    public LabelsModel setClickedLabelsGroup(String clickedLabelsGroup) {
        this.clickedLabelsGroup = clickedLabelsGroup;
        return this;
    }

    public class GroupedLabel {
        private int id;
        private String name = "";
        private String amount = "";

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

        public String getAmount() {
            return amount;
        }

        public GroupedLabel setAmount(String amount) {
            this.amount = amount;
            return this;
        }
    }

    public class GroupedLabelColumn {
        private int id;
        private ResourceProperty resourceProperty;
        private Resource resourceType;
        private String resourceName = "";
        private String namespace = "";
        private String additionalInfo = "";

        public GroupedLabelColumn(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public GroupedLabelColumn setId(int id) {
            this.id = id;
            return this;
        }

        public String getResourceProperty() {
            return ResourceProperty.getValueByKey(resourceProperty.name());
        }

        public GroupedLabelColumn setResourceProperty(ResourceProperty resourceProperty) {
            this.resourceProperty = resourceProperty;
            return this;
        }

        public String getResourceType() {
            return resourceType.getKind();
        }

        public GroupedLabelColumn setResourceType(Resource resourceType) {
            this.resourceType = resourceType;
            return this;
        }

        public Resource getRawResourceType() {
            return resourceType;
        }

        public String getResourceName() {
            return resourceName;
        }

        public GroupedLabelColumn setResourceName(String resourceName) {
            this.resourceName = resourceName;
            return this;
        }

        public String getNamespace() {
            return namespace;
        }

        public GroupedLabelColumn setNamespace(String namespace) {
            this.namespace = namespace;
            return this;
        }

        public String getAdditionalInfo() {
            return additionalInfo;
        }

        public GroupedLabelColumn setAdditionalInfo(String additionalInfo) {
            this.additionalInfo = additionalInfo;
            return this;
        }
    }
}


