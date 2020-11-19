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
import com.kubehelper.domain.models.PageModel;
import com.kubehelper.services.CommonService;
import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zkplus.spring.DelegatingVariableResolver;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Window;

/**
 * @author JDev
 */
@VariableResolver(DelegatingVariableResolver.class)
public class IndexVM {

    private PageModel pageModel;
    private String currentModelName;

    @WireVariable
    private CommonService commonService;

//    @Command
//    public void onClientInfoEvent(ClientInfoEvent evt) {
//        pageModel.setDesktopWithAndHeight(evt.getDesktopWidth(), evt.getDesktopHeight());
//        BindUtils.postGlobalCommand(null, null, "updateHeightsAndRerenderVM", Map.of("eventType", "onClientInfo"));
//    }

    @Command
    public void refreshMainGridSize() {
        BindUtils.postNotifyChange(null, null, this, "mainGridId");
    }

    @Init
    public void init() {
//        pageModel = new DashboardModel();
//        pageModel = new SearchModel();
//        pageModel = Global.ACTIVE_MODELS.computeIfAbsent(Global.LABELS_MODEL, (k) -> Global.NEW_MODELS.get(Global.LABELS_MODEL));
//        pageModel = Global.ACTIVE_MODELS.computeIfAbsent(Global.EVENTS_MODEL, (k) -> Global.NEW_MODELS.get(Global.EVENTS_MODEL));
//        pageModel = Global.ACTIVE_MODELS.computeIfAbsent(Global.IPS_AND_PORTS_MODEL, (k) -> Global.NEW_MODELS.get(Global.IPS_AND_PORTS_MODEL));
//        pageModel = Global.ACTIVE_MODELS.computeIfAbsent(Global.SEARCH_MODEL, (k) -> Global.NEW_MODELS.get(Global.SEARCH_MODEL));
        pageModel = Global.ACTIVE_MODELS.computeIfAbsent(Global.LABELS_MODEL, (k) -> Global.NEW_MODELS.get(Global.LABELS_MODEL));
        currentModelName = pageModel.getName();
    }

    @AfterCompose
    public void afterCompose(@ContextParam(ContextType.VIEW) Component view) {
//        BindUtils.postNotifyChange(null, null, this, "mainGridId");
        enableDisableMenuItem(pageModel.getName());
    }

    public PageModel getPageModel() {
        return pageModel;
    }

    @Command()
    public void switchView(@BindingParam("modelName") String modelName) {
        pageModel = Global.ACTIVE_MODELS.computeIfAbsent(modelName, (k) -> Global.NEW_MODELS.get(modelName));
        enableDisableMenuItem(modelName);
        BindUtils.postNotifyChange(null, null, this, ".");
    }

    @Command()
    public void contactDeveloper() {
        Window window = (Window) Executions.createComponents("~./zul/components/contact.zul", null, null);
        window.doModal();
    }

    private void enableDisableMenuItem(String modelName) {
        Toolbarbutton clickedMenuBtn = (Toolbarbutton) Path.getComponent("//indexPage/" + modelName + "MenuBtn");
        Toolbarbutton currentMenuBtn = (Toolbarbutton) Path.getComponent("//indexPage/" + currentModelName + "MenuBtn");
        currentMenuBtn.setDisabled(false);
        clickedMenuBtn.setDisabled(true);
        currentMenuBtn.setStyle("font-weight: normal;");
        clickedMenuBtn.setStyle("font-weight: bold;");
        currentModelName = modelName;
    }
}
