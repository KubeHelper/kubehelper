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
 * @author JDev
 */
@VariableResolver(DelegatingVariableResolver.class)
public class IpsAndPortsVM {

    private static Logger logger = LoggerFactory.getLogger(IpsAndPortsVM.class);

    private boolean isGetButtonPressed;

    private int centerLayoutHeight = 700;

    private String detailsLabel = "";

    private IpsAndPortsModel ipsAndPortsModel;
    private ListModelList<IpsAndPortsResult> ipsAndPortsResults = new ListModelList<>();

    @Wire
    private Footer ipsAndPortsGridFooter;

    @WireVariable
    private CommonService commonService;

    @WireVariable
    private IpsAndPortsService ipsAndPortsService;

    @Init
    public void init() {
        ipsAndPortsModel = (IpsAndPortsModel) Global.ACTIVE_MODELS.computeIfAbsent(Global.IPS_AND_PORTS_MODEL, (k) -> Global.NEW_MODELS.get(Global.IPS_AND_PORTS_MODEL));
        onInitPreparations();
    }

    /**
     * We need Selectors.wireComponents() in order to be able to @Wire GUI components.
     */
    @AfterCompose
    public void afterCompose(@ContextParam(ContextType.VIEW) Component view) {
        Selectors.wireComponents(view, this, false);
        Selectors.wireEventListeners(view, this);
    }

    @Listen("onAfterSize=#centerLayoutIpsAndPortsID")
    public void onAfterSizeCenter(AfterSizeEvent event) {
        centerLayoutHeight = event.getHeight() - 3;
        BindUtils.postNotifyChange(null, null, this, ".");
    }

    @Command
    @NotifyChange({"totalItems", "ipsAndPortsResults", "filter"})
    public void search() {
        ipsAndPortsModel.setFilter(new IpsAndPortsFilter());
        ipsAndPortsService.get(ipsAndPortsModel);
        isGetButtonPressed = true;
        onInitPreparations();
    }

    private void onInitPreparations() {
        ipsAndPortsModel.setNamespaces(ipsAndPortsModel.getNamespaces().isEmpty() ? commonService.getAllNamespaces() : ipsAndPortsModel.getNamespaces());
        if (ipsAndPortsModel.getFilter().isFilterActive() && !ipsAndPortsModel.getIpsAndPortsResults().isEmpty()) {
            filterIps();
        } else {
            ipsAndPortsResults = new ListModelList<>(ipsAndPortsModel.getIpsAndPortsResults());
        }
        sortResultsByNamespace();
        logger.info("Found {} namespaces.", ipsAndPortsModel.getNamespaces());
    }

    @Command
    @NotifyChange({"totalItems", "ipsAndPortsResults"})
    public void filterIps() {
        ipsAndPortsResults.clear();
        for (IpsAndPortsResult ipsAndPortsResult : ipsAndPortsModel.getIpsAndPortsResults()) {
            if (StringUtils.containsIgnoreCase(ipsAndPortsResult.getIp(), getFilter().getIp()) &&
                    StringUtils.containsIgnoreCase(ipsAndPortsResult.getPorts(), getFilter().getPorts()) &&
                    StringUtils.containsIgnoreCase(ipsAndPortsResult.getHostInfo(), getFilter().getHostInfo()) &&
                    StringUtils.containsIgnoreCase(ipsAndPortsResult.getCreationTime(), getFilter().getCreationTime()) &&
                    StringUtils.containsIgnoreCase(ipsAndPortsResult.getResourceName(), getFilter().getResourceName()) &&
                    commonService.checkEqualsFilter(ipsAndPortsResult.getResourceType(), getFilter().getSelectedResourceTypeFilter()) &&
                    commonService.checkEqualsFilter(ipsAndPortsResult.getNamespace(), getFilter().getSelectedNamespaceFilter())) {
                ipsAndPortsResults.add(ipsAndPortsResult);
            }
        }
        sortResultsByNamespace();
    }

    @Command
    @NotifyChange("detailsLabel")
    public void getDetails(@BindingParam("clickedItem") IpsAndPortsResult item) {
        detailsLabel = detailsLabel.equals(item.getDetails()) ? "" : item.getDetails();
    }

    @Command
    @NotifyChange({"totalItems", "ipsAndPortsResults", "filter", "selectedNamespace"})
    public void clearAll() {
        ipsAndPortsModel.setIpsAndPortsResults(new ListModelList<>())
                .setFilter(new IpsAndPortsFilter())
                .setNamespaces(commonService.getAllNamespaces())
                .setSelectedNamespace("all")
                .setSearchExceptions(new ArrayList<>());
        ipsAndPortsResults = new ListModelList<>();
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

    private void sortResultsByNamespace() {
        ipsAndPortsResults.sort(Comparator.comparing(IpsAndPortsResult::getNamespace));
    }

    public IpsAndPortsModel getModel() {
        return ipsAndPortsModel;
    }

    public String getSelectedNamespace() {
        return this.ipsAndPortsModel.getSelectedNamespace();
    }

    public IpsAndPortsVM setSelectedNamespace(String selectedNamespace) {
        this.ipsAndPortsModel.setSelectedNamespace(selectedNamespace);
        return this;
    }

    public String getTotalItems() {
        return String.format("Total Items: %d", ipsAndPortsResults.size());
    }

    public List<String> getNamespaces() {
        return ipsAndPortsModel.getNamespaces();
    }

    public ListModelList<IpsAndPortsResult> getIpsAndPortsResults() {
        if (isGetButtonPressed && ipsAndPortsResults.isEmpty()) {
            Notification.show("Nothing found.", "info", ipsAndPortsGridFooter, "before_end", 2000);
        }
        if (isGetButtonPressed && !ipsAndPortsResults.isEmpty()) {
            Notification.show("Found: " + ipsAndPortsResults.size() + " items", "info", ipsAndPortsGridFooter, "before_end", 2000);
        }
        if (isGetButtonPressed && ipsAndPortsModel.hasSearchErrors()) {
            Window window = (Window) Executions.createComponents("~./zul/kubehelper/components/errors.zul", null, Map.of("errors", ipsAndPortsModel.getSearchExceptions()));
            window.doModal();
            ipsAndPortsModel.setSearchExceptions(new ArrayList<>());
        }
        isGetButtonPressed = false;
        return ipsAndPortsResults;
    }

    public IpsAndPortsFilter getFilter() {
        return ipsAndPortsModel.getFilter();
    }

    public String getDetailsLabel() {
        return detailsLabel;
    }

    public String getMainGridHeight() {
        return centerLayoutHeight + "px";
    }

}
