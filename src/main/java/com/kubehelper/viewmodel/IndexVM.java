package com.kubehelper.viewmodel;

import com.kubehelper.model.PageModel;
import com.kubehelper.service.TestService;
import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zkplus.spring.DelegatingVariableResolver;
import org.zkoss.zul.Include;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author JDev
 */
@VariableResolver(DelegatingVariableResolver.class)
public class IndexVM {

    private String url;
    private static final String URL_PREFIX = "~./zul/webpages/templates/";
    private String template = "~./zul/pages/dashboard.zul";
    private boolean isInitial = true;

    @WireVariable
    private TestService testService;

//    @Init
//    public void init(@QueryParam("page") String queryParam) {
//        url = (String) Executions.getCurrent().getAttribute("page") + ".zul";
//    }

    @Init
    public void init() {
        
    }

    @Command
    @NotifyChange("currentTime")
    public void updateTime() {
        //NOOP just for the notify change
    }

    public Date getCurrentTime() {
        return testService.getTime();
    }

    public String getTemplate() {
        return template;
    }

    public String getUrl() {
        if (url == null) {
            return null;
        }
        return URL_PREFIX + url;

    }

    public void onButtonClick() {
        if (isInitial) {
            template = "~./zul/pages/dashboard.zul";
            isInitial = false;
        } else {
            template = "~./zul/pages/search.zul";
            isInitial = true;
        }
        BindUtils.postNotifyChange(null,null,this,".");
    }

    //TODO look for page model for all views
//    Map<String, PageModel<String>> pages = new HashMap<>();
//    private PageModel<String> currentPage;
//
//    @Init
//    public void init() {
//        pages.put("page1", new PageModel<>("~./zul/mvvm-page1.zul", "some data for page 1 (could be a more complex object)"));
//        pages.put("page2", new PageModel<>("~./zul/mvvm-page2.zul", "different data for page 2"));
//    }


//    @Command
//    @NotifyChange("currentTime")
//    public void updateTime() {
//        //NOOP just for the notify change
//    }
}
