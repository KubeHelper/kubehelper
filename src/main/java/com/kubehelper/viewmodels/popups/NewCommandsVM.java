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
package com.kubehelper.viewmodels.popups;

import com.google.common.io.Files;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Notification;
import org.zkoss.zkplus.spring.DelegatingVariableResolver;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Window;

import java.io.File;
import java.util.Map;

/**
 * Class for displaying modal window for create a new commands file.
 * ViewModel initializes ..kubehelper/components/new-commands-modal.zul
 *
 * @author JDev
 */
@VariableResolver(DelegatingVariableResolver.class)
public class NewCommandsVM {

    private static Logger logger = LoggerFactory.getLogger(NewCommandsVM.class);

    private String newCommandsFileName = "";

    private String commandsLocationPath = "";

    @Wire
    private Window newCommandsWindow;

    /**
     * Initializes popup window.
     */
    @Init
    public void init() {
        Environment env = SpringUtil.getApplicationContext().getEnvironment();
        commandsLocationPath = env.getProperty("kubehelper.user.commands.local.location.search.path");
    }

    /**
     * @param view - {@link org.zkoss.zul.Window} component.
     */
    @AfterCompose
    public void afterCompose(@ContextParam(ContextType.VIEW) Component view) {
        Selectors.wireComponents(view, this, false);
        Selectors.wireEventListeners(view, this);
    }

    @Command
    public void createNewCommandsFile() {
        if (StringUtils.isBlank(newCommandsFileName)) {
            Notification.show("Unable to create file with empty name.", "warning", null, "top_right", 3000);
            return;
        }
        String filePath = newCommandsFileName.contains("/") ? newCommandsFileName.substring(0, newCommandsFileName.lastIndexOf("/") + 1) : "";
        String optimizedFilePath = filePath.startsWith("/") ? filePath.substring(1) : filePath;
        File newCommandsFile = new File(commandsLocationPath + optimizedFilePath + Files.getNameWithoutExtension(newCommandsFileName) + ".toml");
        if (newCommandsFile.exists()) {
            Notification.show(String.format("Unable to create file. File with name %s already exists.", newCommandsFile.getAbsolutePath()), "warning", null, "top_right", 3000);
            return;
        }
        BindUtils.postGlobalCommand(null, null, "addNewCommandsFile", Map.of("newCommandsFilePath", newCommandsFile.getAbsolutePath()));
        newCommandsWindow.detach();
    }

    public String getNewCommandsFileName() {
        return newCommandsFileName;
    }

    public NewCommandsVM setNewCommandsFileName(String newCommandsFileName) {
        this.newCommandsFileName = newCommandsFileName;
        return this;
    }
}