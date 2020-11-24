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
package com.kubehelper.viewmodels;

import com.kubehelper.common.Global;
import com.kubehelper.domain.filters.ContainersSecurityFilter;
import com.kubehelper.domain.filters.PodsSecurityFilter;
import com.kubehelper.domain.filters.PodsSecurityPoliciesSecurityFilter;
import com.kubehelper.domain.filters.RoleRulesSecurityFilter;
import com.kubehelper.domain.filters.RolesSecurityFilter;
import com.kubehelper.domain.filters.ServiceAccountsSecurityFilter;
import com.kubehelper.domain.models.SecurityModel;
import com.kubehelper.domain.results.ContainerSecurityResult;
import com.kubehelper.domain.results.PodSecurityPoliciesResult;
import com.kubehelper.domain.results.PodSecurityResult;
import com.kubehelper.domain.results.RoleResult;
import com.kubehelper.domain.results.ServiceAccountResult;
import com.kubehelper.services.CommonService;
import com.kubehelper.services.SecurityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.Notification;
import org.zkoss.zkplus.spring.DelegatingVariableResolver;
import org.zkoss.zul.Auxhead;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Footer;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Window;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

/**
 * @author JDev
 */
@VariableResolver(DelegatingVariableResolver.class)
public class SecurityVM {

    private static Logger logger = LoggerFactory.getLogger(SecurityVM.class);

    private boolean isGetRolesButtonPressed;
    private boolean isGetPodsButtonPressed;
    private boolean isGetContainersButtonPressed;
    private boolean isGetServiceAccountsButtonPressed;
    private boolean isGetPodSecurityPoliciesButtonPressed;

    private SecurityModel securityModel;

    private ListModelList<RoleResult> rolesResults = new ListModelList<>();
    private ListModelList<RoleResult> roleRulesResults = new ListModelList<>();
    private ListModelList<PodSecurityResult> podsResults = new ListModelList<>();
    private ListModelList<ContainerSecurityResult> containersResults = new ListModelList<>();
    private ListModelList<ServiceAccountResult> serviceAccountsResults = new ListModelList<>();
    private ListModelList<PodSecurityPoliciesResult> podSecurityPoliciesResults = new ListModelList<>();

    @Wire
    private Footer rolesGridFooter;

    @Wire
    private Footer roleRulesGridFooter;

    @Wire
    private Footer podsGridFooter;

    @Wire
    private Footer containersGridFooter;

    @Wire
    private Footer serviceAccountsGridFooter;

    @Wire
    private Footer podSecurityPoliciesGridFooter;

    @WireVariable
    private CommonService commonService;

    @WireVariable
    private SecurityService securityService;

    @Init
    public void init() {
        securityModel = (SecurityModel) Global.ACTIVE_MODELS.computeIfAbsent(Global.SECURITY_MODEL, (k) -> Global.NEW_MODELS.get(Global.SECURITY_MODEL));
//        TODO
//        onInitPreparations();
    }

    /**
     * We need Selectors.wireComponents() in order to be able to @Wire GUI components.
     */
    @AfterCompose
    public void afterCompose(@ContextParam(ContextType.VIEW) Component view) {
        Selectors.wireComponents(view, this, false);
    }

    @Command
    @NotifyChange({"rolesTotalItems", "rolesResults", "rolesFilter"})
    public void getRoles() {
        securityModel.setRolesFilter(new RolesSecurityFilter());
//        securityService.getRoles(securityModel);
        securityModel.setSearchExceptions(new ArrayList<>());
        isGetRolesButtonPressed = true;
//        onInitPreparations();
    }

    @Command
    @NotifyChange({"podsTotalItems", "podsResults", "podsFilter"})
    public void getPods() {
        securityModel.setPodsFilter(new PodsSecurityFilter());
//        securityService.getPods(securityModel);
        securityModel.setSearchExceptions(new ArrayList<>());
        isGetPodsButtonPressed = true;
//        onInitPreparations();
    }

    @Command
    @NotifyChange({"containersTotalItems", "containersResults", "containersFilter"})
    public void getContainers() {
        securityModel.setContainersFilter(new ContainersSecurityFilter());
//        securityService.getContainers(securityModel);
        securityModel.setSearchExceptions(new ArrayList<>());
        isGetContainersButtonPressed = true;
//        onInitPreparations();
    }

    @Command
    @NotifyChange({"serviceAccountsTotalItems", "serviceAccountsResults", "serviceAccountsFilter"})
    public void getServiceAccounts() {
        securityModel.setServiceAccountsFilter(new ServiceAccountsSecurityFilter());
//        securityService.getServiceAccounts(securityModel);
        securityModel.setSearchExceptions(new ArrayList<>());
        isGetServiceAccountsButtonPressed = true;
//        onInitPreparations();
    }

    @Command
    @NotifyChange({"podSecurityPoliciesTotalItems", "podSecurityPoliciesResults", "podSecurityPoliciesFilter"})
    public void getPodSecurityPolicies() {
        securityModel.setPodSecurityPoliciesFilter(new PodsSecurityPoliciesSecurityFilter());
//        securityService.getPodSecurityPolicies(securityModel);
        securityModel.setSearchExceptions(new ArrayList<>());
        isGetPodSecurityPoliciesButtonPressed = true;
//        onInitPreparations();
    }

    //    TODO onInitPreparations for each tab
    private void onInitPreparations() {
        securityModel.setNamespaces(securityModel.getNamespaces().isEmpty() ? commonService.getAllNamespaces() : securityModel.getNamespaces());
        //        TODO remove call commonService.getAllNamespaces() and from other models and put logger in onInitPreparations
        logger.info("Found {} namespaces.", securityModel.getNamespaces());
//        if (securityModel.getFilter().isFilterActive() && !ipsAndPortsModel.getIpsAndPortsResults().isEmpty()) {
//            filterIps();
//        } else {
//            ipsAndPortsResults = new ListModelList<>(securityModel.getRolesResults());
//        }
//        sortResultsByNamespace();
    }

//    TODO Filters for all tabs

//    @Command
//    @NotifyChange({"totalItems", "ipsAndPortsResults"})
//    public void filterIps() {
//        ipsAndPortsResults.clear();
//        for (IpsAndPortsResult ipsAndPortsResult : ipsAndPortsModel.getIpsAndPortsResults()) {
//            if (ipsAndPortsResult.getIp().toLowerCase().contains(getFilter().getIp().toLowerCase()) &&
//                    ipsAndPortsResult.getPorts().toLowerCase().contains(getFilter().getPorts().toLowerCase()) &&
//                    ipsAndPortsResult.getHostInfo().toLowerCase().contains(getFilter().getHostInfo().toLowerCase()) &&
//                    ipsAndPortsResult.getCreationTime().toLowerCase().contains(getFilter().getCreationTime().toLowerCase()) &&
//                    ipsAndPortsResult.getResourceName().toLowerCase().contains(getFilter().getResourceName().toLowerCase()) &&
//                    ipsAndPortsResult.getResourceType().toLowerCase().contains(getFilter().getResourceType().toLowerCase()) &&
//                    ipsAndPortsResult.getNamespace().toLowerCase().contains(getFilter().getNamespace().toLowerCase())) {
//                ipsAndPortsResults.add(ipsAndPortsResult);
//            }
//        }
//        sortResultsByNamespace();
//    }


    @Command
    @NotifyChange({"rolesTotalItems", "rolesResults", "rolesFilter", "selectedRolesNamespace"})
    public void clearAllRoles() {
//        securityModel.setRolesResults(new ListModelList<>())
//                .setFilter(new IpsAndPortsFilter())
//                .setNamespaces(commonService.getAllNamespaces())
//                .setSelectedNamespace("all")
//                .setSearchExceptions(new ArrayList<>());
        rolesResults = new ListModelList<>();
        clearAllFilterComboboxes();
    }

    @Command
    @NotifyChange({"podsTotalItems", "podsResults", "podsFilter", "selectedPodsNamespace"})
    public void clearAllPods() {
//        securityModel.setRolesResults(new ListModelList<>())
//                .setFilter(new IpsAndPortsFilter())
//                .setNamespaces(commonService.getAllNamespaces())
//                .setSelectedNamespace("all")
//                .setSearchExceptions(new ArrayList<>());
        containersResults = new ListModelList<>();
        clearAllFilterComboboxes();
    }

    @Command
    @NotifyChange({"containersTotalItems", "containersResults", "containersFilter", "selectedContainersNamespace"})
    public void clearAllContainers() {
//        securityModel.setRolesResults(new ListModelList<>())
//                .setFilter(new IpsAndPortsFilter())
//                .setNamespaces(commonService.getAllNamespaces())
//                .setSelectedNamespace("all")
//                .setSearchExceptions(new ArrayList<>());
        containersResults = new ListModelList<>();
        clearAllFilterComboboxes();
    }

    @Command
    @NotifyChange({"serviceAccountsTotalItems", "serviceAccountsResults", "serviceAccountsFilter", "selectedServiceAccountsNamespace"})
    public void clearAllServiceAccounts() {
//        securityModel.setRolesResults(new ListModelList<>())
//                .setFilter(new IpsAndPortsFilter())
//                .setNamespaces(commonService.getAllNamespaces())
//                .setSelectedNamespace("all")
//                .setSearchExceptions(new ArrayList<>());
        serviceAccountsResults = new ListModelList<>();
        clearAllFilterComboboxes();
    }

    @Command
    @NotifyChange({"podSecurityPoliciesTotalItems", "podSecurityPoliciesResults", "podSecurityPoliciesFilter", "selectedPodSecurityPoliciesNamespace"})
    public void clearAllPodSecurityPolicies() {
//        securityModel.setRolesResults(new ListModelList<>())
//                .setFilter(new IpsAndPortsFilter())
//                .setNamespaces(commonService.getAllNamespaces())
//                .setSelectedNamespace("all")
//                .setSearchExceptions(new ArrayList<>());
        podSecurityPoliciesResults = new ListModelList<>();
        clearAllFilterComboboxes();
    }

    /**
     * Removes last selected value from all filter comboboxes.
     */
    private void clearAllFilterComboboxes() {
        Auxhead searchGridAuxHead = (Auxhead) Path.getComponent("//indexPage/templateInclude/ipsAndPortsGridAuxHead");
        for (Component child : searchGridAuxHead.getFellows()) {
            if (Arrays.asList("filterResourceNamesCBox", "filterNamespacesCBox", "filterResourceTypesCBox").contains(child.getId())) {
                Combobox cBox = (Combobox) child;
                cBox.setValue("");
            }
        }
    }

    //TODO get sort by namespace for each grid
//    private void sortResultsByNamespace() {
//        ipsAndPortsResults.sort(Comparator.comparing(IpsAndPortsResult::getNamespace));
//    }

    public SecurityModel getModel() {
        return securityModel;
    }


    public ListModelList<RoleResult> getRolesResults() {
        showNotificationAndExceptions(isGetRolesButtonPressed, rolesResults, rolesGridFooter);
        isGetRolesButtonPressed = false;
        return rolesResults;
    }

    public ListModelList<RoleResult> getRoleRulesResults() {
//TODO add Method for get RoleRules by ID
        return roleRulesResults;
    }

    public ListModelList<PodSecurityResult> getPodsResults() {
        showNotificationAndExceptions(isGetPodsButtonPressed, podsResults, podsGridFooter);
        isGetPodsButtonPressed = false;
        return podsResults;
    }

    public ListModelList<ContainerSecurityResult> getContainersResults() {
        showNotificationAndExceptions(isGetContainersButtonPressed, containersResults, containersGridFooter);
        isGetContainersButtonPressed = false;
        return containersResults;
    }

    public ListModelList<ServiceAccountResult> getServiceAccountsResults() {
        showNotificationAndExceptions(isGetServiceAccountsButtonPressed, serviceAccountsResults, serviceAccountsGridFooter);
        isGetServiceAccountsButtonPressed = false;
        return serviceAccountsResults;
    }

    public ListModelList<PodSecurityPoliciesResult> getPodSecurityPoliciesResults() {
        showNotificationAndExceptions(isGetPodSecurityPoliciesButtonPressed, podSecurityPoliciesResults, podSecurityPoliciesGridFooter);
        isGetPodSecurityPoliciesButtonPressed = false;
        return podSecurityPoliciesResults;
    }

    public void showNotificationAndExceptions(boolean pressedButton, ListModelList results, Footer footer) {
        if (pressedButton && results.isEmpty()) {
            Notification.show("Nothing found.", "info", footer, "before_end", 2000);
        }
        if (pressedButton && !results.isEmpty()) {
            Notification.show("Found: " + results.size() + " items", "info", footer, "before_end", 2000);
        }
        if (pressedButton && securityModel.hasSearchErrors()) {
            Window window = (Window) Executions.createComponents("~./zul/components/errors.zul", null, Map.of("errors", securityModel.getSearchExceptions()));
            window.doModal();
        }
    }

    public String getSelectedRolesNamespace() {
        return securityModel.getSelectedRolesNamespace();
    }

    public void setSelectedRolesNamespace(String selectedRolesNamespace) {
        this.securityModel.setSelectedRolesNamespace(selectedRolesNamespace);
    }

    public String getSelectedPodsNamespace() {
        return securityModel.getSelectedPodsNamespace();
    }

    public void setSelectedPodsNamespace(String selectedPodsNamespace) {
        this.securityModel.setSelectedPodsNamespace(selectedPodsNamespace);
    }

    public String getSelectedContainersNamespace() {
        return securityModel.getSelectedContainersNamespace();
    }

    public void setSelectedContainersNamespace(String selectedContainersNamespace) {
        this.securityModel.setSelectedContainersNamespace(selectedContainersNamespace);
    }

    public String getSelectedServiceAccountsNamespace() {
        return securityModel.getSelectedServiceAccountsNamespace();
    }

    public void setSelectedServiceAccountsNamespace(String selectedServiceAccountsNamespace) {
        this.securityModel.setSelectedServiceAccountsNamespace(selectedServiceAccountsNamespace);
    }

    public String getSelectedPodSecurityPoliciesNamespace() {
        return securityModel.getSelectedPodSecurityPoliciesNamespace();
    }

    public void setSelectedPodSecurityPoliciesNamespace(String selectedPodSecurityPoliciesNamespace) {
        this.securityModel.setSelectedPodSecurityPoliciesNamespace(selectedPodSecurityPoliciesNamespace);
    }

    public String getRolesTotalItems() {
        return String.format("Total Items: %d", rolesResults.size());
    }

    public String getRoleRulesTotalItems() {
        return String.format("Total Items: %d", roleRulesResults.size());
    }

    public String getPodsTotalItems() {
        return String.format("Total Items: %d", podsResults.size());
    }

    public String getContainersTotalItems() {
        return String.format("Total Items: %d", containersResults.size());
    }

    public String getServiceAccountsTotalItems() {
        return String.format("Total Items: %d", serviceAccountsResults.size());
    }

    public String getPodSecurityPoliciesTotalItems() {
        return String.format("Total Items: %d", podSecurityPoliciesResults.size());
    }

    public RolesSecurityFilter getRolesFilter() {
        return securityModel.getRolesFilter();
    }

    public RoleRulesSecurityFilter getRoleRulesFilter() {
        return securityModel.getRoleRulesFilter();
    }

    public PodsSecurityFilter getPodsFilter() {
        return securityModel.getPodsFilter();
    }

    public ContainersSecurityFilter getContainersFilter() {
        return securityModel.getContainersFilter();
    }

    public ServiceAccountsSecurityFilter getServiceAccountsFilter() {
        return securityModel.getServiceAccountsFilter();
    }

    public PodsSecurityPoliciesSecurityFilter getPodSecurityPoliciesFilter() {
        return securityModel.getPodSecurityPoliciesFilter();
    }

}
