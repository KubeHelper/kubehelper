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
import com.kubehelper.domain.models.DashboardModel;
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