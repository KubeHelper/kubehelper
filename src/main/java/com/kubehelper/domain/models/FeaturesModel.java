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
import com.kubehelper.domain.filters.FeaturesFilter;
import com.kubehelper.domain.results.FeatureResult;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

/**
 * @author JDev
 */
public class FeaturesModel implements PageModel {

    private int mainGridHeight = 600;
    private PropertyChangeSupport grid = new PropertyChangeSupport(this);

    private String templateUrl = "~./zul/pages/features.zul";
    private String predefinedCommandsPath = "/templates/features/commands.kh";

    private String userCommandsPath = "C:\\temp\\kubehelper";
    //    private String userCommandsPath = "/tmp/kubehelper";
    public static String NAME = Global.FEATURES_MODEL;
    private List<String> namespaces = new ArrayList<>();
    private List<String> pods = new ArrayList<>();
    private List<FeatureResult> featuresResults = new ArrayList<>();
    private FeaturesFilter filter = new FeaturesFilter();
    private List<KubeHelperException> buildExceptions = new ArrayList<>();
    private String selectedNamespace = "all";

    public FeaturesModel() {
    }

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

    public void addFeatureResult(FeatureResult featureResult) {
        featuresResults.add(featureResult);
//        filter.addResourceTypesFilter(searchResult.getResourceType());
//        filter.addNamespacesFilter(searchResult.getNamespace());
    }

    public FeaturesModel addGroupFilter(String resourceName) {
//        if (StringUtils.isNotBlank(resourceName)) {
//            filter.addResourceNamesFilter(resourceName);
//        }
        return this;
    }

    public void addParseException(Exception exception) {
        this.buildExceptions.add(new KubeHelperException(exception));
    }

    @Override
    public void addException(String message, Exception exception) {
//        this.searchExceptions.add(new KubeHelperException(message, exception));
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getTemplateUrl() {
        return templateUrl;
    }

    public List<String> getNamespaces() {
        return namespaces;
    }

    public FeaturesModel setNamespaces(List<String> namespaces) {
        this.namespaces = namespaces;
        return this;
    }

    public List<String> getPods() {
        return pods;
    }

    public FeaturesModel setPods(List<String> pods) {
        this.pods = pods;
        return this;
    }

    public List<FeatureResult> getFeaturesResults() {
        return featuresResults;
    }

    public FeaturesModel setFeaturesResults(List<FeatureResult> featuresResults) {
        this.featuresResults = featuresResults;
        return this;
    }

    public List<KubeHelperException> getBuildExceptions() {
        return buildExceptions;
    }

    public FeaturesModel setBuildExceptions(List<KubeHelperException> buildExceptions) {
        this.buildExceptions = buildExceptions;
        return this;
    }

    public boolean hasBuildErrors() {
        return !buildExceptions.isEmpty();
    }

    public FeaturesFilter getFilter() {
        return filter;
    }

    public FeaturesModel setFilter(FeaturesFilter filter) {
        this.filter = filter;
        return this;
    }

    public String getSelectedNamespace() {
        return selectedNamespace;
    }

    public FeaturesModel setSelectedNamespace(String selectedNamespace) {
        this.selectedNamespace = selectedNamespace;
        return this;
    }

    public String getPredefinedCommandsPath() {
        return predefinedCommandsPath;
    }

    public FeaturesModel setPredefinedCommandsPath(String predefinedCommandsPath) {
        this.predefinedCommandsPath = predefinedCommandsPath;
        return this;
    }

    public String getUserCommandsPath() {
        return userCommandsPath;
    }

    public FeaturesModel setUserCommandsPath(String userCommandsPath) {
        this.userCommandsPath = userCommandsPath;
        return this;
    }
}
