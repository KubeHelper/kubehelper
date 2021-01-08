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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zkplus.spring.DelegatingVariableResolver;
import org.zkoss.zul.Footer;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Window;

import java.time.LocalDate;

/**
 * @author JDev
 */
@VariableResolver(DelegatingVariableResolver.class)
public class IndexVM {

    private PageModel pageModel;
    private String currentModelName;

    @Wire
    private Footer indexGlobalFooter;

    @WireVariable
    private CommonService commonService;
    private static Logger logger = LoggerFactory.getLogger(IndexVM.class);

    @Init
    public void init() {
//        pageModel = new DashboardModel();
//        pageModel = new SearchModel();
//        pageModel = Global.ACTIVE_MODELS.computeIfAbsent(Global.DASHBOARD_MODEL, (k) -> Global.NEW_MODELS.get(Global.DASHBOARD_MODEL));
//        pageModel = Global.ACTIVE_MODELS.computeIfAbsent(Global.EVENTS_MODEL, (k) -> Global.NEW_MODELS.get(Global.EVENTS_MODEL));
//        pageModel = Global.ACTIVE_MODELS.computeIfAbsent(Global.IPS_AND_PORTS_MODEL, (k) -> Global.NEW_MODELS.get(Global.IPS_AND_PORTS_MODEL));
//        pageModel = Global.ACTIVE_MODELS.computeIfAbsent(Global.SEARCH_MODEL, (k) -> Global.NEW_MODELS.get(Global.SEARCH_MODEL));
//        pageModel = Global.ACTIVE_MODELS.computeIfAbsent(Global.SECURITY_MODEL, (k) -> Global.NEW_MODELS.get(Global.SECURITY_MODEL));
        pageModel = Global.ACTIVE_MODELS.computeIfAbsent(Global.COMMANDS_MODEL, (k) -> Global.NEW_MODELS.get(Global.COMMANDS_MODEL));
//        pageModel = Global.ACTIVE_MODELS.computeIfAbsent(Global.CONFIGS_MODEL, (k) -> Global.NEW_MODELS.get(Global.CONFIGS_MODEL));
//        pageModel = Global.ACTIVE_MODELS.computeIfAbsent(Global.LABELS_MODEL, (k) -> Global.NEW_MODELS.get(Global.LABELS_MODEL));
        currentModelName = pageModel.getName();
    }

    @AfterCompose
    public void afterCompose(@ContextParam(ContextType.VIEW) Component view) {
        Selectors.wireEventListeners(view, this);
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
        Window window = (Window) Executions.createComponents("~./zul/kubehelper/components/contact.zul", null, null);
        window.doModal();
    }

    public String getFooterCopyrightText() {
        int year = LocalDate.now().getYear();
        String currentYear = year > 2020 ? "2020-" + year : String.valueOf(year);
        return "Copyright © " + currentYear + ", Kube Helper";
    }

    @Command()
    public void donate() {
        Window window = (Window) Executions.createComponents("~./zul/kubehelper/components/donations.zul", null, null);
        window.doModal();
    }

    private void enableDisableMenuItem(String modelName) {
        Toolbarbutton clickedMenuBtn = (Toolbarbutton) Path.getComponent("//indexPage/" + modelName + "MenuBtn");
        Toolbarbutton currentMenuBtn = (Toolbarbutton) Path.getComponent("//indexPage/" + currentModelName + "MenuBtn");
        currentMenuBtn.setDisabled(false);
        clickedMenuBtn.setDisabled(true);
        currentMenuBtn.setStyle("text-align: left; padding-left: 2px;font-weight: normal;");
        clickedMenuBtn.setStyle("text-align: left; padding-left: 2px;font-weight: bold;");
        currentModelName = modelName;
    }
}
