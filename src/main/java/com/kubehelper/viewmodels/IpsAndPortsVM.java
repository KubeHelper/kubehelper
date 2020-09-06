package com.kubehelper.viewmodels;

import com.kubehelper.common.Global;
import com.kubehelper.domain.filters.IpsAndPortsFilter;
import com.kubehelper.domain.models.IpsAndPortsModel;
import com.kubehelper.domain.results.IpsAndPortsResult;
import com.kubehelper.services.CommonService;
import com.kubehelper.services.IpsAndPortsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.GlobalCommand;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.event.OpenEvent;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zkplus.spring.DelegatingVariableResolver;
import org.zkoss.zul.ListModelList;

import java.util.Comparator;
import java.util.List;

/**
 * @author JDev
 */
@VariableResolver(DelegatingVariableResolver.class)
public class IpsAndPortsVM {

    private static Logger logger = LoggerFactory.getLogger(IpsAndPortsVM.class);

    private String detailsLabel = "";
    private String ipsAndPortsGridHeight = "100px";

    private IpsAndPortsModel ipsAndPortsModel = new IpsAndPortsModel();
    private ListModelList<IpsAndPortsResult> ipsAndPortsResults = new ListModelList<>();

    @WireVariable
    private CommonService commonService;

    @WireVariable
    private IpsAndPortsService ipsAndPortsService;

    @Init
    public void init() {
        ipsAndPortsModel = (IpsAndPortsModel) Global.ACTIVE_MODELS.computeIfAbsent(Global.IPS_AND_PORTS_MODEL, (k) -> Global.NEW_MODELS.get(Global.IPS_AND_PORTS_MODEL));
        onInitPreparations();
    }

    @Command
    @NotifyChange({"totalItems", "ipsAndPortsResults", "filter"})
    public void search() {
        ipsAndPortsModel.setFilter(new IpsAndPortsFilter());
        ipsAndPortsService.get(ipsAndPortsModel.getSelectedNamespace(), ipsAndPortsModel);
        ipsAndPortsModel.setNamespaces(commonService.getAllNamespaces());
        logger.info("Found {} namespaces.", ipsAndPortsModel.getNamespaces());
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
        updateHeightsAndRerenderVM();
    }

    @Command
    @NotifyChange({"totalItems", "ipsAndPortsResults"})
    public void filterIps() {
        ipsAndPortsResults.clear();
        for (IpsAndPortsResult ipsAndPortsResult : ipsAndPortsModel.getIpsAndPortsResults()) {
            if (ipsAndPortsResult.getIp().toLowerCase().contains(getFilter().getIp()) &&
                    ipsAndPortsResult.getPorts().toLowerCase().contains(getFilter().getPorts()) &&
                    ipsAndPortsResult.getHostInfo().toLowerCase().contains(getFilter().getHostInfo()) &&
                    ipsAndPortsResult.getCreationTime().toLowerCase().contains(getFilter().getCreationTime()) &&
                    ipsAndPortsResult.getResourceName().toLowerCase().contains(getFilter().getResourceName()) &&
                    ipsAndPortsResult.getResourceType().toLowerCase().contains(getFilter().getResourceType()) &&
                    ipsAndPortsResult.getNamespace().toLowerCase().contains(getFilter().getNamespace())) {
                ipsAndPortsResults.add(ipsAndPortsResult);
            }
        }
        sortResultsByNamespace();
    }

    @GlobalCommand
    @NotifyChange({"*"})
    public void updateHeightsAndRerenderVM() {
        ipsAndPortsGridHeight = ((float) ipsAndPortsModel.getDesktopHeight() / 100) * 68 + "px";
    }

    @Command
    @NotifyChange({"*"})
    public void updateGridHeightOnSouthPanelChange(@ContextParam(ContextType.TRIGGER_EVENT) OpenEvent event) {
        int heightPercentage = event.isOpen() ? 68 : 78;
        ipsAndPortsGridHeight = ((float) ipsAndPortsModel.getDesktopHeight() / 100) * heightPercentage + "px";
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
                .setIpsAndPortsResults(new ListModelList<>());
        ipsAndPortsResults = new ListModelList<>();
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
        return ipsAndPortsResults;
    }

    public IpsAndPortsFilter getFilter() {
        return ipsAndPortsModel.getFilter();
    }

    public String getDetailsLabel() {
        return detailsLabel;
    }

    //    The approximate heights of the components on the page as a percentage. Header: 15%, Grid: 68, Footer: 17%
    public String getIpsAndPortsGridHeight() {
        return ipsAndPortsGridHeight;
    }

}
