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
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.Notification;

/**
 * Class for displaying login form and common help for Kube Helper.
 * ViewModel initializes ..zul/home.zul
 *
 * @author JDev
 */
public class HomeVM {

    private static Logger logger = LoggerFactory.getLogger(HomeVM.class);

    /**
     * Shows the error that occurred during authorization.
     *
     * @param view - login view.
     */
    @AfterCompose
    public void afterCompose(@ContextParam(ContextType.VIEW) Component view) {
        if (StringUtils.isNotBlank(Global.AUTH_EXCEPTION_MESSAGE)) {
            logger.error(String.format("Auth Error: %s", Global.AUTH_EXCEPTION_MESSAGE));
            Notification.show(Global.AUTH_EXCEPTION_MESSAGE, "error", view, "top_center", 5000);
            Global.AUTH_EXCEPTION_MESSAGE = "";
        }
    }
}