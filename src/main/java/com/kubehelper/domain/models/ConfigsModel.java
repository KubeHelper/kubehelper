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
package com.kubehelper.domain.models;

import com.kubehelper.common.Global;
import com.kubehelper.common.KubeHelperException;

import java.util.ArrayList;
import java.util.List;

/**
 * @author JDev
 */
public class ConfigsModel implements PageModel {

    private String templateUrl = "~./zul/kubehelper/pages/config.zul";
    public static String NAME = Global.CONFIGS_MODEL;
    private List<KubeHelperException> validationExceptions = new ArrayList<>();

    private String config = "";

    @Override
    public String getTemplateUrl() {
        return templateUrl;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void addException(String message, Exception exception) {
        this.validationExceptions.add(new KubeHelperException(message, exception));
    }

    public String getConfig() {
        return config;
    }

    public ConfigsModel setConfig(String config) {
        this.config = config;
        return this;
    }

    public boolean hasValidationErrors() {
        return !validationExceptions.isEmpty();
    }

    public List<KubeHelperException> getValidationExceptions() {
        return validationExceptions;
    }

    public ConfigsModel setValidationExceptions(List<KubeHelperException> validationExceptions) {
        this.validationExceptions = validationExceptions;
        return this;
    }
}
