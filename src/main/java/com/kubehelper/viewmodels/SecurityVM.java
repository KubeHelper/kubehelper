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
import com.kubehelper.common.Resource;
import com.kubehelper.domain.filters.RBACFilter;
import com.kubehelper.domain.filters.RolesSecurityFilter;
import com.kubehelper.domain.models.SecurityModel;
import com.kubehelper.domain.results.ContainerSecurityResult;
import com.kubehelper.domain.results.PodSecurityContextResult;
import com.kubehelper.domain.results.PodSecurityPoliciesResult;
import com.kubehelper.domain.results.RBACResult;
import com.kubehelper.domain.results.RoleResult;
import com.kubehelper.domain.results.RoleRuleResult;
import com.kubehelper.domain.results.ServiceAccountResult;
import com.kubehelper.services.CommonService;
import com.kubehelper.services.SecurityService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.event.AfterSizeEvent;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Listen;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.kubehelper.common.Resource.KUBE_HELPER_CONTAINER_SECURITY_CONTEXT;
import static com.kubehelper.common.Resource.KUBE_HELPER_CUSTOM;
import static com.kubehelper.common.Resource.KUBE_HELPER_POD_SECURITY_CONTEXT;

/**
 * View Model for search for security components and visualize results in table.
 * ViewModel initializes ..kubehelper/pages/security.zul
 *
 * @author JDev
 */
@VariableResolver(DelegatingVariableResolver.class)
public class SecurityVM {

    private static Logger logger = LoggerFactory.getLogger(SecurityVM.class);

    private boolean isGetRolesButtonPressed;
    private boolean isGetPodsSecurityContextsButtonPressed;
    private boolean isGetRBACsButtonPressed;
    private boolean isGetContainersSecurityContextsButtonPressed;
    private boolean isGetServiceAccountsButtonPressed;
    private boolean isGetPodSecurityPoliciesButtonPressed;

    private SecurityModel model;

    private ListModelList<RoleResult> rolesResults = new ListModelList<>();
    private ListModelList<RoleResult.RoleBindingSubject> roleSubjectsResults = new ListModelList<>();
    private ListModelList<RoleRuleResult> roleRulesResults = new ListModelList<>();
    private ListModelList<RBACResult> rbacsResults = new ListModelList<>();
    private ListModelList<PodSecurityContextResult> podsSecurityContextsResults = new ListModelList<>();
    private ListModelList<ContainerSecurityResult> containersSecurityResults = new ListModelList<>();
    private ListModelList<ServiceAccountResult> serviceAccountsResults = new ListModelList<>();
    private ListModelList<PodSecurityPoliciesResult> podsSecurityPoliciesResults = new ListModelList<>();

    private String clickedRoleBindingSubjectsLabel = "";
    private String clickedRoleRulesLabel = "";
    private int centerLayoutHeight = 700;

    @WireVariable
    private CommonService commonService;

    @WireVariable
    private SecurityService securityService;

    @Wire
    private Footer rolesGridFooter;

    @Wire
    private Footer roleRulesGridFooter;

    @Wire
    private Footer podsSecurityContextsGridFooter;

    @Wire
    private Footer rbacsGridFooter;

    @Wire
    private Footer containersSecurityGridFooter;

    @Wire
    private Footer serviceAccountsGridFooter;

    @Wire
    private Footer podSecurityPoliciesGridFooter;


    @Init
    public void init() {
        model = (SecurityModel) Global.ACTIVE_MODELS.computeIfAbsent(Global.SECURITY_MODEL, (k) -> Global.NEW_MODELS.get(Global.SECURITY_MODEL));
        setAllNamespacesToModel();
    }

    /**
     * Calls after UI render.
     * <p>
     * Explanation:
     * Selectors.wireComponents() in order to be able to @Wire GUI components.
     * Selectors.wireEventListeners() in order to be able to work with listeners and events.
     */
    @AfterCompose
    public void afterCompose(@ContextParam(ContextType.VIEW) Component view) {
        Selectors.wireComponents(view, this, false);
        Selectors.wireEventListeners(view, this);
    }

    @Listen("onAfterSize=#centerLayoutSecurityID")
    public void onAfterSizeCenter(AfterSizeEvent event) {
        centerLayoutHeight = event.getHeight();
        BindUtils.postNotifyChange(this, ".");
    }


    //  GETTING ================


    /**
     * Gets/Searches for all Roles depends on namespace.
     */
    @Command
    @NotifyChange({"rolesTotalItems", "rolesResults", "rolesFilter"})
    public void getRoles() {
        model.setRolesFilter(new RolesSecurityFilter());
        securityService.getRoles(model);
        isGetRolesButtonPressed = true;
        setAllNamespacesToModel();
        if (model.getRolesFilter().isFilterActive() && !model.getRolesResults().isEmpty()) {
            filterSecurityRoles();
        } else {
            rolesResults = new ListModelList<>(model.getRolesResultsList());
        }
    }

    /**
     * Gets/Searches for all Rbacs depends on namespace.
     */
    @Command
    @NotifyChange({"rbacsTotalItems", "rbacsResults", "rbacsFilter"})
    public void getRbacs() {
        model.setRbacsFilter(new RBACFilter());
        securityService.getRBACs(model);
        isGetRBACsButtonPressed = true;
        setAllNamespacesToModel();
        if (model.getRbacsFilter().isFilterActive() && !model.getRbacsResults().isEmpty()) {
            filterRbacs();
        } else {
            rbacsResults = new ListModelList<>(model.getRbacsResults());
        }
    }

    /**
     * Gets/Searches for all PodsSecurityContexts depends on namespace.
     */
    @Command
    @NotifyChange({"podsSecurityContextsTotalItems", "podsSecurityContextsResults"})
    public void getPodsSecurityContexts() {
        securityService.getPodsSecurityContexts(model);
        isGetPodsSecurityContextsButtonPressed = true;
        setAllNamespacesToModel();
        podsSecurityContextsResults = new ListModelList<>(model.getPodsSecurityContextsResults());
    }

    /**
     * Gets/Searches for all ContainersSecurityContexts depends on namespace.
     */
    @Command
    @NotifyChange({"containersSecurityTotalItems", "containersSecurityResults", "containersSecurityFilter"})
    public void getContainersSecurityContexts() {
        securityService.getContainersSecurityContexts(model);
        isGetContainersSecurityContextsButtonPressed = true;
        setAllNamespacesToModel();
        containersSecurityResults = new ListModelList<>(model.getContainersSecurityResults());
    }

    /**
     * Gets/Searches for ServiceAccounts roles depends on namespace.
     */
    @Command
    @NotifyChange({"serviceAccountsTotalItems", "serviceAccountsResults", "serviceAccountsFilter"})
    public void getServiceAccounts() {
        securityService.getServiceAccounts(model);
        isGetServiceAccountsButtonPressed = true;
        setAllNamespacesToModel();
        serviceAccountsResults = new ListModelList<>(model.getServiceAccountsResults());
    }

    /**
     * Gets/Searches for all PodsSecurityPolicies depends on namespace.
     */
    @Command
    @NotifyChange({"podsSecurityPoliciesTotalItems", "podsSecurityPoliciesResults", "podSecurityPoliciesFilter"})
    public void getPodsSecurityPolicies() {
        securityService.getPodsSecurityPolicies(model);
        isGetPodSecurityPoliciesButtonPressed = true;
        podsSecurityPoliciesResults = new ListModelList<>(model.getPodSecurityPoliciesResults());
        setAllNamespacesToModel();
    }

    private void setAllNamespacesToModel() {
        model.setNamespaces(model.getNamespaces().isEmpty() ? commonService.getAllNamespaces() : model.getNamespaces());
        logger.debug("Found {} namespaces.", model.getNamespaces());
    }


    //  FILTERING ================


    /**
     * Filter for SecurityRoles.
     */
    @Command
    @NotifyChange({"rolesTotalItems", "rolesResults"})
    public void filterSecurityRoles() {
        rolesResults.clear();
        for (RoleResult roleResult : model.getRolesResultsList()) {
            if (StringUtils.containsIgnoreCase(roleResult.getCreationTime(), getRolesFilter().getCreationTime()) &&
                    commonService.checkEqualsFilter(roleResult.getResourceName(), getRolesFilter().getSelectedResourceNameFilter()) &&
                    commonService.checkEqualsFilter(roleResult.getResourceType(), getRolesFilter().getSelectedResourceTypeFilter()) &&
                    commonService.checkEqualsFilter(roleResult.getNamespace(), getRolesFilter().getSelectedNamespaceFilter())) {
                rolesResults.add(roleResult);
            }
        }
    }

    /**
     * Filter for Rbacs.
     */
    @Command
    @NotifyChange({"rbacsTotalItems", "rbacsResults"})
    public void filterRbacs() {
        rbacsResults.clear();
        for (RBACResult rbacResult : model.getRbacsResults()) {
            if (StringUtils.containsIgnoreCase(rbacResult.getApiGroup(), getRbacFilter().getApiGroup()) &&
                    commonService.checkEqualsFilter(rbacResult.getResourceName(), getRbacFilter().getSelectedResourceNameFilter()) &&
                    commonService.checkEqualsFilter(rbacResult.getResourceType(), getRbacFilter().getSelectedResourceTypeFilter()) &&
                    commonService.checkEqualsFilter(rbacResult.getNamespace(), getRbacFilter().getSelectedNamespaceFilter()) &&
                    commonService.checkEqualsFilter(rbacResult.getSubjectKind(), getRbacFilter().getSelectedSubjectKindFilter()) &&
                    commonService.checkEqualsFilter(rbacResult.getSubjectName(), getRbacFilter().getSelectedSubjectNameFilter()) &&
                    commonService.checkEqualsFilter(rbacResult.getRoleName(), getRbacFilter().getSelectedRoleNameFilter()) &&
                    getRbacFilter().getVerbAll().equals(checkRbacRoleFilter(getRbacFilter().getVerbAll(), rbacResult.isAll())) &&
                    getRbacFilter().getVerbGet().equals(checkRbacRoleFilter(getRbacFilter().getVerbGet(), rbacResult.isGet())) &&
                    getRbacFilter().getVerbList().equals(checkRbacRoleFilter(getRbacFilter().getVerbList(), rbacResult.isList())) &&
                    getRbacFilter().getVerbCreate().equals(checkRbacRoleFilter(getRbacFilter().getVerbCreate(), rbacResult.isCreate())) &&
                    getRbacFilter().getVerbUpdate().equals(checkRbacRoleFilter(getRbacFilter().getVerbUpdate(), rbacResult.isUpdate())) &&
                    getRbacFilter().getVerbPatch().equals(checkRbacRoleFilter(getRbacFilter().getVerbPatch(), rbacResult.isPatch())) &&
                    getRbacFilter().getVerbWatch().equals(checkRbacRoleFilter(getRbacFilter().getVerbWatch(), rbacResult.isWatch())) &&
                    getRbacFilter().getVerbDelete().equals(checkRbacRoleFilter(getRbacFilter().getVerbDelete(), rbacResult.isDelete())) &&
                    getRbacFilter().getVerbDeleteCollection().equals(checkRbacRoleFilter(getRbacFilter().getVerbDeleteCollection(), rbacResult.isDeletecollection())) &&
                    StringUtils.containsIgnoreCase(rbacResult.getOthers(), getRbacFilter().getOthers())) {
                rbacsResults.add(rbacResult);
            }
        }
    }

    /**
     * Methot helps filter RBAC verbs correct.
     *
     * @param verb     - verb.
     * @param rbacVerb - rbac verb.
     * @return - Yes/No/Empty
     */
    private String checkRbacRoleFilter(String verb, boolean rbacVerb) {
        if (rbacVerb && verb.equals("Yes")) {
            return "Yes";
        }
        if (!rbacVerb && verb.equals("No")) {
            return "No";
        }
        return "";
    }


    //  CLEARING ================


    /**
     * Clears all model items that belongs to Roles.
     */
    @Command
    @NotifyChange({"rolesTotalItems", "rolesResults", "roleRulesResults", "roleSubjectsResults", "rolesFilter", "selectedRolesNamespace", "clickedRoleBindingSubjectsLabel", "clickedRoleRulesLabel"})
    public void clearAllRoles() {
        model.setRolesResults(new HashMap<>())
                .setRolesFilter(new RolesSecurityFilter())
                .setNamespaces(commonService.getAllNamespaces())
                .setSelectedRolesNamespace("all")
                .setSearchExceptions(new ArrayList<>());
        rolesResults = new ListModelList<>();
        roleRulesResults = new ListModelList<>();
        roleSubjectsResults = new ListModelList<>();
        clickedRoleBindingSubjectsLabel = "";
        clickedRoleRulesLabel = "";
        clearAllRolesFilterComboboxes();
    }

    /**
     * Clears all model items that belongs to Rbacs.
     */
    @Command
    @NotifyChange({"rbacsTotalItems", "rbacsResults", "rbacFilter", "selectedRBACsNamespace"})
    public void clearAllRbacs() {
        model.setRbacsResults(new ArrayList<>())
                .setRbacsFilter(new RBACFilter())
                .setNamespaces(commonService.getAllNamespaces())
                .setSelectedRBACsNamespace("all")
                .setSearchExceptions(new ArrayList<>());
        rbacsResults = new ListModelList<>();
        clearAllRbacFilterComboboxes();
    }

    /**
     * Clears all model items that belongs to PodsSecurityContexts.
     */
    @Command
    @NotifyChange({"podsSecurityContextsTotalItems", "podsSecurityContextsResults", "selectedPodsSecurityContextsNamespace"})
    public void clearAllPodsSecurityContexts() {
        model.setPodsSecurityContextResults(new ListModelList<>())
                .setNamespaces(commonService.getAllNamespaces())
                .setSelectedPodsSecurityContextsNamespace("all")
                .setSearchExceptions(new ArrayList<>());
        podsSecurityContextsResults = new ListModelList<>();
    }

    /**
     * Clears all model items that belongs to ContainersSecurityContexts.
     */
    @Command
    @NotifyChange({"containersSecurityTotalItems", "containersSecurityResults", "selectedContainersSecurityNamespace"})
    public void clearAllContainersSecurityContexts() {
        model.setContainersSecurityResults(new ListModelList<>())
                .setNamespaces(commonService.getAllNamespaces())
                .setSelectedContainersSecurityNamespace("all")
                .setSearchExceptions(new ArrayList<>());
        containersSecurityResults = new ListModelList<>();
    }

    /**
     * Clears all model items that belongs to ServiceAccounts.
     */
    @Command
    @NotifyChange({"serviceAccountsTotalItems", "serviceAccountsResults", "selectedServiceAccountsNamespace"})
    public void clearAllServiceAccounts() {
        model.setServiceAccountsResults(new ListModelList<>())
                .setNamespaces(commonService.getAllNamespaces())
                .setSelectedServiceAccountsNamespace("all")
                .setSearchExceptions(new ArrayList<>());
        serviceAccountsResults = new ListModelList<>();
    }

    /**
     * Clears all model items that belongs to PodSecurityPolicies.
     */
    @Command
    @NotifyChange({"podsSecurityPoliciesTotalItems", "podsSecurityPoliciesResults", "selectedPodSecurityPoliciesNamespace"})
    public void clearAllPodSecurityPolicies() {
        model.setPodSecurityPoliciesResults(new ListModelList<>())
                .setNamespaces(commonService.getAllNamespaces())
                .setSelectedPodSecurityPoliciesNamespace("all")
                .setSearchExceptions(new ArrayList<>());
        podsSecurityPoliciesResults = new ListModelList<>();
    }

    /**
     * Removes last selected value from all RolesFilter comboboxes.
     */
    private void clearAllRolesFilterComboboxes() {
        Auxhead searchGridAuxHead = (Auxhead) Path.getComponent("//indexPage/templateInclude/rolesGridAuxHead");
        for (Component child : searchGridAuxHead.getFellows()) {
            if (Arrays.asList("filterRolesNamespacesCBox", "filterRolesResourceTypesCBox", "filterRolesResourceNamesCBox").contains(child.getId())) {
                Combobox cBox = (Combobox) child;
                cBox.setValue("");
            }
        }
    }

    /**
     * Removes last selected value from all RbacFilter comboboxes.
     */
    private void clearAllRbacFilterComboboxes() {
        Auxhead searchGridAuxHead = (Auxhead) Path.getComponent("//indexPage/templateInclude/rbacGridAuxHead");
        for (Component child : searchGridAuxHead.getFellows()) {
            if (Arrays.asList("rbacResourceNamesFilterCBox", "rbacSubjectKindsFilterCBox", "rbacSubjectNamesFilterCBox", "rbacResourceTypesFilterCBox", "rbacNamespacesFilterCBox", "rbacVerbAllCBox", "rbacVerbGetCBox", "rbacVerbListCBox", "rbacVerbCreateCBox", "rbacVerbUpdateCBox", "rbacVerbPatchCBox", "rbacWerbWatchCBox", "rbacVerbDeleteCBox", "rbacVerbDeleteCollectionCBox", "rbacVerbPatchCBox").contains(child.getId())) {
                Combobox cBox = (Combobox) child;
                cBox.setValue("");
            }
        }
    }

    //  FULL DEFINITION / LOGIC ================

    /**
     * Shows full definitions window for Role.
     *
     * @param item - @{@link RoleResult} item
     */
    @Command
    public void showRoleFullDefinition(@BindingParam("item") RoleResult item) {
        Map<String, Object> parameters = getParametersMap(item.getRawResourceType(), item.getResourceName(), item.getResourceName(), item.getNamespace(), item.getFullDefinition());
        Window window = (Window) Executions.createComponents(Global.PATH_TO_RAW_RESOURCE_ZUL, null, parameters);
        window.doModal();
    }

    /**
     * Shows full definitions window for RoleRule.
     *
     * @param item - @{@link RoleRuleResult} item
     */
    @Command
    public void showRoleRuleFullDefinition(@BindingParam("item") RoleRuleResult item) {
        Map<String, Object> parameters = getParametersMap(KUBE_HELPER_CUSTOM, String.valueOf(item.getId()), String.valueOf(item.getId()), "N/A", item.getFullDefinition());
        Window window = (Window) Executions.createComponents(Global.PATH_TO_RAW_RESOURCE_ZUL, null, parameters);
        window.doModal();
    }

    /**
     * Shows full definitions window for PodSecurityContext.
     *
     * @param item - @{@link PodSecurityContextResult} item
     */
    @Command
    public void showPodSecurityContextFullDefinition(@BindingParam("item") PodSecurityContextResult item) {
        Map<String, Object> parameters = getParametersMap(KUBE_HELPER_POD_SECURITY_CONTEXT, item.getResourceName(), item.getResourceName(), item.getNamespace(), item.getFullDefinition());
        Window window = (Window) Executions.createComponents(Global.PATH_TO_RAW_RESOURCE_ZUL, null, parameters);
        window.doModal();
    }

    /**
     * Shows full definitions window for ContainerSecurityContext.
     *
     * @param item - @{@link ContainerSecurityResult} item
     */
    @Command
    public void showContainerSecurityContextFullDefinition(@BindingParam("item") ContainerSecurityResult item) {
        String title = item.getPodName() + " [ " + item.getResourceName() + " ]";
        Map<String, Object> parameters = getParametersMap(KUBE_HELPER_CONTAINER_SECURITY_CONTEXT, item.getResourceName(), title, item.getNamespace(), item.getFullDefinition());
        Window window = (Window) Executions.createComponents(Global.PATH_TO_RAW_RESOURCE_ZUL, null, parameters);
        window.doModal();
    }

    /**
     * Shows full definitions window for ServiceAccount.
     *
     * @param item - @{@link ServiceAccountResult} item
     */
    @Command
    public void showServiceAccountFullDefinition(@BindingParam("item") ServiceAccountResult item) {
        Map<String, Object> parameters = getParametersMap(item.getRawResourceType(), item.getResourceName(), item.getResourceName(), item.getNamespace(), item.getFullDefinition());
        Window window = (Window) Executions.createComponents(Global.PATH_TO_RAW_RESOURCE_ZUL, null, parameters);
        window.doModal();
    }

    /**
     * Shows full definitions window for PodSecurityPolicy.
     *
     * @param item - @{@link PodSecurityPoliciesResult} item
     */
    @Command
    public void showPodSecurityPolicyFullDefinition(@BindingParam("item") PodSecurityPoliciesResult item) {
        Map<String, Object> parameters = getParametersMap(item.getRawResourceType(), item.getResourceName(), item.getResourceName(), item.getNamespace(), item.getFullDefinition());
        Window window = (Window) Executions.createComponents(Global.PATH_TO_RAW_RESOURCE_ZUL, null, parameters);
        window.doModal();
    }

    /**
     * Shows(by click) rules that belongs to role.
     *
     * @param item - @{@link RoleResult} item.
     */
    @Command
    @NotifyChange({"roleSubjectsResults", "roleRulesResults", "clickedRoleBindingSubjectsLabel", "clickedRoleRulesLabel", "roleRulesTotalItems"})
    public void showRoleRules(@BindingParam("clickedItem") RoleResult item) {
        roleSubjectsResults = new ListModelList<>(item.getSubjects());
        roleRulesResults = new ListModelList<>(item.getRoleRules());
        clickedRoleRulesLabel = item.getResourceName();
        clickedRoleBindingSubjectsLabel = item.getResourceName();
    }

    /**
     * Builds parameters map for WIndow parameters.
     *
     * @param resource  - Resource
     * @param name      -  name
     * @param title     - title
     * @param namespace -  namespace
     * @param content   - content
     * @return - parameters map
     */
    private Map<String, Object> getParametersMap(Resource resource, String name, String title, String namespace, String content) {
        return Map.of("resource", resource, "name", name, "title", title, "namespace", namespace, "content", content);
    }


    public void showNotificationAndExceptions(boolean pressedButton, ListModelList results, Footer footer) {
        if (pressedButton && results.isEmpty()) {
            Notification.show("Nothing found.", "info", footer, "before_end", 2000);
        }
        if (pressedButton && !results.isEmpty()) {
            Notification.show(String.format("Found %s items", results.size()), "info", footer, "before_end", 2000);
        }
        if (pressedButton && model.hasSearchErrors()) {
            Window window = (Window) Executions.createComponents(Global.PATH_TO_ERROR_RESOURCE_ZUL, null, Map.of("errors", model.getSearchExceptions()));
            window.doModal();
            model.setSearchExceptions(new ArrayList<>());
        }
    }

    public SecurityModel getModel() {
        return model;
    }


    //  RESULTS ================


    public ListModelList<RoleResult> getRolesResults() {
        showNotificationAndExceptions(isGetRolesButtonPressed, rolesResults, rolesGridFooter);
        isGetRolesButtonPressed = false;
        return rolesResults;
    }

    public ListModelList<RoleRuleResult> getRoleRulesResults() {
        return roleRulesResults;
    }

    public ListModelList<RBACResult> getRbacsResults() {
        showNotificationAndExceptions(isGetRBACsButtonPressed, rbacsResults, rbacsGridFooter);
        isGetRBACsButtonPressed = false;
        return rbacsResults;
    }

    public ListModelList<RoleResult.RoleBindingSubject> getRoleSubjectsResults() {
        return roleSubjectsResults;
    }

    public ListModelList<PodSecurityContextResult> getPodsSecurityContextsResults() {
        showNotificationAndExceptions(isGetPodsSecurityContextsButtonPressed, podsSecurityContextsResults, podsSecurityContextsGridFooter);
        isGetPodsSecurityContextsButtonPressed = false;
        return podsSecurityContextsResults;
    }

    public ListModelList<ContainerSecurityResult> getContainersSecurityResults() {
        showNotificationAndExceptions(isGetContainersSecurityContextsButtonPressed, containersSecurityResults, containersSecurityGridFooter);
        isGetContainersSecurityContextsButtonPressed = false;
        return containersSecurityResults;
    }

    public ListModelList<ServiceAccountResult> getServiceAccountsResults() {
        showNotificationAndExceptions(isGetServiceAccountsButtonPressed, serviceAccountsResults, serviceAccountsGridFooter);
        isGetServiceAccountsButtonPressed = false;
        return serviceAccountsResults;
    }

    public ListModelList<PodSecurityPoliciesResult> getPodsSecurityPoliciesResults() {
        showNotificationAndExceptions(isGetPodSecurityPoliciesButtonPressed, podsSecurityPoliciesResults, podSecurityPoliciesGridFooter);
        isGetPodSecurityPoliciesButtonPressed = false;
        return podsSecurityPoliciesResults;
    }


    //  SELECTED NAMESPACES ================

    public String getSelectedRolesNamespace() {
        return model.getSelectedRolesNamespace();
    }

    public String getSelectedRBACsNamespace() {
        return model.getSelectedRBACsNamespace();
    }

    public void setSelectedRolesNamespace(String selectedRolesNamespace) {
        this.model.setSelectedRolesNamespace(selectedRolesNamespace);
    }

    public void setSelectedRBACsNamespace(String selectedRBACsNamespace) {
        this.model.setSelectedRBACsNamespace(selectedRBACsNamespace);
    }

    public String getSelectedPodsSecurityContextsNamespace() {
        return model.getSelectedPodsSecurityContextsNamespace();
    }

    public void setSelectedPodsSecurityContextsNamespace(String selectedPodsNamespace) {
        this.model.setSelectedPodsSecurityContextsNamespace(selectedPodsNamespace);
    }

    public String getSelectedContainersSecurityNamespace() {
        return model.getSelectedContainersSecurityNamespace();
    }

    public void setSelectedContainersSecurityNamespace(String selectedContainersNamespace) {
        this.model.setSelectedContainersSecurityNamespace(selectedContainersNamespace);
    }

    public String getSelectedServiceAccountsNamespace() {
        return model.getSelectedServiceAccountsNamespace();
    }

    public void setSelectedServiceAccountsNamespace(String selectedServiceAccountsNamespace) {
        this.model.setSelectedServiceAccountsNamespace(selectedServiceAccountsNamespace);
    }

    public String getSelectedPodSecurityPoliciesNamespace() {
        return model.getSelectedPodSecurityPoliciesNamespace();
    }

    public void setSelectedPodSecurityPoliciesNamespace(String selectedPodSecurityPoliciesNamespace) {
        this.model.setSelectedPodSecurityPoliciesNamespace(selectedPodSecurityPoliciesNamespace);
    }


    //  TOTAL ITEMS ================

    public String getRolesTotalItems() {
        return String.format("Total Items: %d", rolesResults.size());
    }

    public String getRoleRulesTotalItems() {
        return String.format("Total Items: %d", roleRulesResults.size());
    }

    public String getRbacsTotalItems() {
        return String.format("Total Items: %d", rbacsResults.size());
    }

    public String getPodsSecurityContextsTotalItems() {
        return String.format("Total Items: %d", podsSecurityContextsResults.size());
    }

    public String getContainersSecurityTotalItems() {
        return String.format("Total Items: %d", containersSecurityResults.size());
    }

    public String getServiceAccountsTotalItems() {
        return String.format("Total Items: %d", serviceAccountsResults.size());
    }

    public String getPodsSecurityPoliciesTotalItems() {
        return String.format("Total Items: %d", podsSecurityPoliciesResults.size());
    }


    //  FILTERS ================

    public RolesSecurityFilter getRolesFilter() {
        return model.getRolesFilter();
    }

    public RBACFilter getRbacFilter() {
        return model.getRbacsFilter();
    }


    //  LABELS / HEIGHTS / OTHERS ================

    public String getClickedRoleBindingSubjectsLabel() {
        return "Role Binding subjects for the Role: " + clickedRoleBindingSubjectsLabel;
    }

    public String getClickedRoleRulesLabel() {
        return "Rules for the Role: " + clickedRoleRulesLabel;
    }

    public String getRBACIcon(@BindingParam("verb") boolean verb) {
        return verb ? "z-icon-check" : "z-icon-times";
    }

    public String getRBACIconColor(@BindingParam("verb") boolean verb) {
        return verb ? "color:green;" : "color:red;";
    }


    public List<String> getNamespaces() {
        return model.getNamespaces();
    }

    public String getRolesGridHeight() {
        return centerLayoutHeight * 0.3 + "px";
    }

    public String getRolesGridGrBoxHeight() {
        return centerLayoutHeight * 0.3 + 40 + "px";
    }

    public String getSubjectsGridHeight() {
        return centerLayoutHeight * 0.15 + "px";
    }

    public String getRoleRulesGridHeight() {

        return centerLayoutHeight > 1200 ? centerLayoutHeight * 0.34 + "px" : centerLayoutHeight * 0.32 + "px";
    }

    public String getSubjectsGridGrBoxHeight() {
        return centerLayoutHeight * 0.15 + 40 + "px";
    }

    public String getRoleRulesGridGrBoxHeight() {
        return centerLayoutHeight > 1200 ? centerLayoutHeight * 0.34 + 40 + "px" : centerLayoutHeight * 0.32 + 40 + "px";
    }

    public String getRbacsGridHeight() {
        return centerLayoutHeight - 155 + "px";
    }

    public String getPodsSecurityContextsGridHeight() {
        return centerLayoutHeight - 155 + "px";
    }

    public String getContainersSecurityContextsGridHeight() {
        return centerLayoutHeight - 155 + "px";
    }

    public String getServiceAccountsGridHeight() {
        return centerLayoutHeight - 155 + "px";
    }

    public String getPodsSecurityPoliciesGridHeight() {
        return centerLayoutHeight - 155 + "px";
    }

    public String getRbacsGridGrBoxHeight() {
        return centerLayoutHeight - 115 + "px";
    }

    public String getPodsSecurityContextsGridGrBoxHeight() {
        return centerLayoutHeight - 115 + "px";
    }

    public String getContainersSecurityContextsGridGrBoxHeight() {
        return centerLayoutHeight - 115 + "px";
    }

    public String getServiceAccountsGridGrBoxHeight() {
        return centerLayoutHeight - 115 + "px";
    }

    public String getPodsSecurityPoliciesGridGrBoxHeight() {
        return centerLayoutHeight - 115 + "px";
    }

    public boolean isSkipKubeNamespaces() {
        return model.isSkipKubeNamespaces();
    }

    public void setSkipKubeNamespaces(boolean skipKubeNamespaces) {
        model.setSkipKubeNamespaces(skipKubeNamespaces);
    }

}
