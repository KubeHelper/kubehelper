package com.kubehelper.viewmodel;

import com.kubehelper.model.DashboardModel;
import com.kubehelper.service.CommonService;
import com.kubehelper.service.DashboardService;
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
        dashboardModel = new DashboardModel();
    }

}