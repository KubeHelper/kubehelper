package com.kubehelper.domain.models;

import com.kubehelper.common.Global;

/**
 * @author JDev
 */
public class DashboardModel implements PageModel {

    private String templateUrl = "~./zul/pages/dashboard.zul";
    public static String NAME = Global.DASHBOARD_MODEL;
    private int desktopWidth;
    private int desktopHeight;

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

    @Override
    public void setDesktopWithAndHeight(int width, int height) {
        this.desktopWidth = width;
        this.desktopHeight = height;
    }

    @Override
    public String getName() {
        return NAME;
    }
}
