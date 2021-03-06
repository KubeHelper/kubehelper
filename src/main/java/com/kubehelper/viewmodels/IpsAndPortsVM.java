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
import com.kubehelper.domain.filters.IpsAndPortsFilter;
import com.kubehelper.domain.models.IpsAndPortsModel;
import com.kubehelper.domain.results.IpsAndPortsResult;
import com.kubehelper.services.CommonService;
import com.kubehelper.services.IpsAndPortsService;
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
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * View Model for displaying ips, ports and container details for kubernetes pods.
 * ViewModel initializes ..kubehelper/pages/ipsandports.zul
 *
 * @author JDev
 */
@VariableResolver(DelegatingVariableResolver.class)
public class IpsAndPortsVM {

    private static Logger logger = LoggerFactory.getLogger(IpsAndPortsVM.class);

    private boolean isGetButtonPressed;

    private int centerLayoutHeight = 700;

    private String detailsLabel = "";

    private IpsAndPortsModel model;
    private ListModelList<IpsAndPortsResult> ipsAndPortsResults = new ListModelList<>();

    @WireVariable
    private CommonService commonService;

    @WireVariable
    private IpsAndPortsService ipsAndPortsService;

    @Wire
    private Footer ipsAndPortsGridFooter;

    @Init
    public void init() {
        model = (IpsAndPortsModel) Global.ACTIVE_MODELS.computeIfAbsent(Global.IPS_AND_PORTS_MODEL, (k) -> Global.NEW_MODELS.get(Global.IPS_AND_PORTS_MODEL));
        onInitPreparations();
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


    @Listen("onAfterSize=#centerLayoutIpsAndPortsGrBox")
    public void onAfterSizeCenter(AfterSizeEvent event) {
        centerLayoutHeight = event.getHeight() - 10;
        BindUtils.postNotifyChange(this, ".");
    }

    /**
     * Search for ips and ports in cluster.
     */
    @Command
    @NotifyChange({"totalItems", "ipsAndPortsResults", "filter"})
    public void search() {
        model.setFilter(new IpsAndPortsFilter());
        ipsAndPortsService.get(model);
        isGetButtonPressed = true;
        onInitPreparations();
    }

    /**
     * Get namespaces if not set by model.
     * Filter results if filter active and results is not empty.
     * Sort results by namespace.
     */
    private void onInitPreparations() {
        model.setNamespaces(model.getNamespaces().isEmpty() ? commonService.getAllNamespaces() : model.getNamespaces());
        if (model.getFilter().isFilterActive() && !model.getIpsAndPortsResults().isEmpty()) {
            filterIps();
        } else {
            ipsAndPortsResults = new ListModelList<>(model.getIpsAndPortsResults());
        }
        sortResultsByNamespace();
        logger.debug("Found {} namespaces.", model.getNamespaces());
    }

    /**
     * Filter IPs.
     */
    @Command
    @NotifyChange({"totalItems", "ipsAndPortsResults"})
    public void filterIps() {
        ipsAndPortsResults.clear();
        for (IpsAndPortsResult ipsAndPortsResult : model.getIpsAndPortsResults()) {
            if (StringUtils.containsIgnoreCase(ipsAndPortsResult.getIp(), getFilter().getIp()) &&
                    StringUtils.containsIgnoreCase(ipsAndPortsResult.getPorts(), getFilter().getPorts()) &&
                    StringUtils.containsIgnoreCase(ipsAndPortsResult.getHostInfo(), getFilter().getHostInfo()) &&
                    StringUtils.containsIgnoreCase(ipsAndPortsResult.getCreationTime(), getFilter().getCreationTime()) &&
                    StringUtils.containsIgnoreCase(ipsAndPortsResult.getResourceName(), getFilter().getResourceName()) &&
                    StringUtils.containsIgnoreCase(ipsAndPortsResult.getAdditionalInfo(), getFilter().getAdditionalInfo()) &&
                    commonService.checkEqualsFilter(ipsAndPortsResult.getResourceType(), getFilter().getSelectedResourceTypeFilter()) &&
                    commonService.checkEqualsFilter(ipsAndPortsResult.getNamespace(), getFilter().getSelectedNamespaceFilter())) {
                ipsAndPortsResults.add(ipsAndPortsResult);
            }
        }
        sortResultsByNamespace();
    }

    /**
     * Show pod details label.
     *
     * @param item - {@link IpsAndPortsResult} item
     */
    @Command
    @NotifyChange("detailsLabel")
    public void getDetails(@BindingParam("clickedItem") IpsAndPortsResult item) {
        detailsLabel = detailsLabel.equals(item.getDetails()) ? "" : item.getDetails();
    }

    /**
     * Clear model. Clear filters, results, namespaces.
     */
    @Command
    @NotifyChange({"totalItems", "ipsAndPortsResults", "filter", "selectedNamespace"})
    public void clearAll() {
        model = new IpsAndPortsModel();
        Global.ACTIVE_MODELS.replace(Global.IPS_AND_PORTS_MODEL,model);
        ipsAndPortsResults = new ListModelList<>();
        clearAllFilterComboboxes();
    }

    /**
     * Removes last selected value from all filter comboboxes.
     */
    private void clearAllFilterComboboxes() {
        Auxhead searchGridAuxHead = (Auxhead) Path.getComponent("//indexPage/templateInclude/ipsAndPortsGridAuxHead");
        for (Component child : searchGridAuxHead.getFellows()) {
            if (Arrays.asList("filterNamespacesCBox", "filterResourceTypesCBox").contains(child.getId())) {
                Combobox cBox = (Combobox) child;
                cBox.setValue("");
            }
        }
    }

    /**
     * Shows different Notifications messages and if exceptions then Windows with errors list.
     *
     * @return List with @{@link IpsAndPortsResult}
     */
    public ListModelList<IpsAndPortsResult> getIpsAndPortsResults() {
        if (isGetButtonPressed && ipsAndPortsResults.isEmpty()) {
            Notification.show("Nothing found.", "info", ipsAndPortsGridFooter, "before_end", 2000);
        }
        if (isGetButtonPressed && !ipsAndPortsResults.isEmpty()) {
            Notification.show(String.format("Found: %s items", ipsAndPortsResults.size()), "info", ipsAndPortsGridFooter, "before_end", 2000);
        }
        if (isGetButtonPressed && model.hasSearchErrors()) {
            Window window = (Window) Executions.createComponents(Global.PATH_TO_ERROR_RESOURCE_ZUL, null, Map.of("errors", model.getSearchExceptions()));
            window.doModal();
            model.setSearchExceptions(new ArrayList<>());
        }
        isGetButtonPressed = false;
        return ipsAndPortsResults;
    }

    private void sortResultsByNamespace() {
        ipsAndPortsResults.sort(Comparator.comparing(IpsAndPortsResult::getNamespace));
    }

    public IpsAndPortsModel getModel() {
        return model;
    }

    public String getSelectedNamespace() {
        return this.model.getSelectedNamespace();
    }

    public void setSelectedNamespace(String selectedNamespace) {
        this.model.setSelectedNamespace(selectedNamespace);
    }

    public String getTotalItems() {
        return String.format("Total Items: %d", ipsAndPortsResults.size());
    }

    public List<String> getNamespaces() {
        return model.getNamespaces();
    }


    public IpsAndPortsFilter getFilter() {
        return model.getFilter();
    }

    public String getDetailsLabel() {
        return detailsLabel;
    }

    public String getMainGridHeight() {
        return centerLayoutHeight + "px";
    }

}
