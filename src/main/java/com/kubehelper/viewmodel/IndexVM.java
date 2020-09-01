package com.kubehelper.viewmodel;

import com.kubehelper.model.DashboardModel;
import com.kubehelper.model.PageModel;
import com.kubehelper.model.SearchModel;
import com.kubehelper.service.CommonService;
import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zkplus.spring.DelegatingVariableResolver;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Window;

import java.util.HashMap;
import java.util.Map;

/**
 * @author JDev
 */
@VariableResolver(DelegatingVariableResolver.class)
public class IndexVM {

    private PageModel pageModel;
    private String currentModelName;

    @WireVariable
    private CommonService commonService;

    private Window wizard;

    private Map<String, PageModel> models = new HashMap<>() {
        {
            put("dashboard", new DashboardModel());
            put("search", new SearchModel());
            put("security", new SearchModel());
            put("auditing", new SearchModel());
            put("customResources", new SearchModel());
            put("deprecated", new SearchModel());
            put("events", new SearchModel());
            put("features", new SearchModel());
            put("featureGates", new SearchModel());
            put("ipsAndPorts", new SearchModel());
            put("volumes", new SearchModel());
        }
    };


    @Init
    public void init() {
//        pageModel = new DashboardModel();
        pageModel = new SearchModel();
        currentModelName = pageModel.getName();
    }

    public PageModel getPageModel() {
        return pageModel;
    }

    @Command()
    public void switchView(@BindingParam("modelName") String modelName) {
        pageModel = models.get(modelName);
        enableDisableMenuItem(modelName);

        BindUtils.postNotifyChange(null, null, this, ".");
    }

    private void enableDisableMenuItem(String modelName) {
        Toolbarbutton clickedMenuBtn = (Toolbarbutton) Path.getComponent("//indexPage/" + modelName + "MenuBtn");
        Toolbarbutton currentMenuBtn = (Toolbarbutton) Path.getComponent("//indexPage/" + currentModelName + "MenuBtn");
        clickedMenuBtn.setDisabled(true);
        currentMenuBtn.setDisabled(false);
        currentModelName = modelName;
    }
}
