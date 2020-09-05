package com.kubehelper.domain.models;

import com.kubehelper.common.Global;
import com.kubehelper.domain.filters.IpsAndPortsFilter;
import com.kubehelper.domain.results.IpsAndPortsResult;
import org.zkoss.bind.BindUtils;
import org.zkoss.zul.ListModelList;

import java.util.ArrayList;
import java.util.List;

/**
 * @author JDev
 */
public class IpsAndPortsModel implements PageModel {

    private String templateUrl = "~./zul/pages/ipsandports.zul";

    public static String NAME = Global.IPS_AND_PORTS_MODEL;
    private int desktopWidth;
    private int desktopHeight;
    private String selectedNamespace = "all";
    private List<String> namespaces = new ArrayList<>();
    private ListModelList<IpsAndPortsResult> ipsAndPortsResults = new ListModelList<>();
    private IpsAndPortsFilter filter = new IpsAndPortsFilter();


    public void addIpsAndPortsResult(IpsAndPortsResult ipsAndPortsResult) {
        ipsAndPortsResults.add(ipsAndPortsResult);
    }

    public IpsAndPortsModel setNamespaces(List<String> namespaces) {
        this.namespaces = namespaces;
        return this;
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

    public List<String> getNamespaces() {
        return namespaces;
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

    public ListModelList<IpsAndPortsResult> getIpsAndPortsResults() {
        return ipsAndPortsResults;
    }

    public IpsAndPortsFilter getFilter() {
        return filter;
    }

    public IpsAndPortsModel setFilter(IpsAndPortsFilter filter) {
        this.filter = filter;
        return this;
    }

    public String getSelectedNamespace() {
        return selectedNamespace;
    }

    public IpsAndPortsModel setSelectedNamespace(String selectedNamespace) {
        this.selectedNamespace = selectedNamespace;
        return this;
    }

    public IpsAndPortsModel setIpsAndPortsResults(ListModelList<IpsAndPortsResult> ipsAndPortsResults) {
        this.ipsAndPortsResults = ipsAndPortsResults;
        return this;
    }
}
