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
import com.kubehelper.domain.results.ContainerSecurityResult;
import com.kubehelper.domain.results.PodSecurityPoliciesResult;
import com.kubehelper.domain.results.PodSecurityResult;
import com.kubehelper.domain.results.RoleResult;
import com.kubehelper.domain.results.SearchResult;
import com.kubehelper.domain.results.ServiceAccountResult;
import org.apache.commons.lang3.StringUtils;
import org.zkoss.zul.ListModelList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author JDev
 */
public class SecurityModel implements PageModel {

    private String templateUrl = "~./zul/pages/security.zul";
    public static String NAME = Global.SECURITY_MODEL;
    private String selectedNamespace = "all";
    private List<String> namespaces = new ArrayList<>();
    private List<PodSecurityResult> podsSearchResults = new ArrayList<>();
    private List<ContainerSecurityResult> containersSearchResults = new ArrayList<>();
    private List<ServiceAccountResult> serviceAccountsSearchResults = new ArrayList<>();
    private List<PodSecurityPoliciesResult> podSecurityPoliciesSearchResults = new ArrayList<>();
    //key is RoleResult id
    private Map<Integer, RoleResult> rolesResults = new HashMap<>();
    private SearchFilter filter = new SearchFilter();
    private List<KubeHelperException> searchExceptions = new ArrayList<>();
    private int selectedRoleId;
    private int selectedRoleRuleId;
//    private boolean caseSensitiveSearch = false;

    public SecurityModel() {
    }

//    public SecurityModel addSearchResult(SearchResult searchResult) {
//        searchResults.add(searchResult);
//        filter.addResourceTypesFilter(searchResult.getResourceType());
//        filter.addNamespacesFilter(searchResult.getNamespace());
//        filter.addResourceNamesFilter(resourceName);
//        return this;
//    }

    public void addSearchException(Exception exception) {
        this.searchExceptions.add(new KubeHelperException(exception));
    }

    @Override
    public String getTemplateUrl() {
        return templateUrl;
    }

    @Override
    public String getName() {
        return NAME;
    }
}
