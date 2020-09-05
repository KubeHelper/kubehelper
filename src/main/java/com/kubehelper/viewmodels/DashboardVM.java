package com.kubehelper.viewmodels;

import com.kubehelper.common.Global;
import com.kubehelper.domain.models.DashboardModel;
import com.kubehelper.domain.models.IpsAndPortsModel;
import com.kubehelper.services.CommonService;
import com.kubehelper.services.DashboardService;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zkplus.spring.DelegatingVariableResolver;

/**
 * @author JDev
 */
@VariableResolver(DelegatingVariableResolver.class)
public class DashboardVM {

    private DashboardModel dashboardModel;

    @WireVariable
    private DashboardService dashboardService;

    @WireVariable
    private CommonService commonService;


    @Init
    public void init() {
        dashboardModel = (DashboardModel) Global.ACTIVE_MODELS.computeIfAbsent(Global.DASHBOARD_MODEL, (k) -> Global.NEW_MODELS.get(Global.DASHBOARD_MODEL));
    }

}