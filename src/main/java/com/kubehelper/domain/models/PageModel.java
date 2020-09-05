package com.kubehelper.domain.models;

/**
 * @author JDev
 */
public interface PageModel {

    String getTemplateUrl();
    int getDesktopWidth();
    int getDesktopHeight();
    
    void setDesktopWithAndHeight(int width, int height);
    String getName();

}
