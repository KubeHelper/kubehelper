package com.kubehelper.model;

/**
 * @author JDev
 */
public class DashboardModel implements PageModel {

    private String templateUrl = "~./zul/pages/dashboard.zul";
    private String name = "dashboard";

    @Override
    public String getTemplateUrl() {
        return templateUrl;
    }

    @Override
    public String getName() {
        return name;
    }
}
