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
import com.kubehelper.configs.KubeHelperCache;
import com.kubehelper.domain.models.ConfigsModel;
import com.kubehelper.services.CommonService;
import com.kubehelper.services.ConfigsService;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.AfterSizeEvent;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zk.ui.util.Notification;
import org.zkoss.zkplus.spring.DelegatingVariableResolver;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Window;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

/**
 * View Model for displaying Kube Helper config.
 * ViewModel initializes ..kubehelper/pages/config.zul
 *
 * @author JDev
 */
@VariableResolver(DelegatingVariableResolver.class)
public class ConfigsVM {

    private static Logger logger = LoggerFactory.getLogger(ConfigsVM.class);

    private int centerLayoutHeight = 700;

    private ConfigsModel model;

    //    TODO REMOVE
    @WireVariable
    private ConfigsService configsService;

    @WireVariable
    private CommonService commonService;

    @WireVariable("kubeHelperCache")
    private KubeHelperCache cache;

    private String gitRepoLocationPath = "/Volumes/MAC_WORK/tmp/kubehelper/git";
//    private boolean autoSyncEnabled;


    @Init
    public void init() {
        model = new ConfigsModel();
        model.setConfig(commonService.getResourceAsStringByPath(Global.PATH_TO_CONFIG_FILE));
        Environment env = SpringUtil.getApplicationContext().getEnvironment();
        gitRepoLocationPath = env.getProperty("kubehelper.git.repo.location.path");
    }

    /**
     * Calls after UI render.
     * <p>
     * Explanation:
     * Selectors.wireComponents() in order to be able to @Wire GUI components.
     * Selectors.wireEventListeners() in order to be able to work with listeners and events.
     * Clients.evalJavaScript - call client highlightConfig method.
     */
    @AfterCompose
    public void afterCompose(@ContextParam(ContextType.VIEW) Component view) {
        Selectors.wireComponents(view, this, false);
        Selectors.wireEventListeners(view, this);
        Clients.evalJavaScript("highlightConfig();");
    }

    @Listen("onAfterSize=#centerLayoutIpsAndPortsID")
    public void onAfterSizeCenter(AfterSizeEvent event) {
        centerLayoutHeight = event.getHeight() - 3;
        BindUtils.postNotifyChange(this, ".");
    }

    /**
     * Saves updated config into Global.config, into file and starts/stops cron jobs.
     *
     * @param config - config string from client.
     */
    @Command
    public void saveConfig(@BindingParam("config") String config) {
        model.setConfig(config);
        commonService.checkAndStartJobsFromConfig(model, config);
        if (checkExceptions()) {
            commonService.updateConfigFile(model);
            Notification.show("The configurations was successfully saved.", "info", null, "before_end", 4000);
        }
        BindUtils.postNotifyChange(this, ".");
        Clients.evalJavaScript("highlightConfig();");
    }


    @Command
    public void cloneGitRepo() {
        if (StringUtils.isBlank(getGitUrl())) {
            Notification.show("Please enter a valid git url in order to clone the repository.", "error", null, "before_end", 5000);
            return;
        }
        try {
            if (StringUtils.isAllEmpty(cache.getGitUsername(), cache.getGitPassword(), cache.getGitBranch())) {
                Git.cloneRepository().setURI(cache.getGitUrl())
                        .setDirectory(new File(gitRepoLocationPath))
                        .call();
                return;
            } else if (StringUtils.isAllEmpty(cache.getGitUsername(), cache.getGitPassword())) {
                Git.cloneRepository().setURI(cache.getGitUrl())
                        .setDirectory(new File(gitRepoLocationPath))
                        .setBranchesToClone(Arrays.asList("refs/heads/" + cache.getGitBranch()))
                        .setBranch(cache.getGitBranch())
                        .call();
                return;
            }

            CloneCommand cloneCommand = Git.cloneRepository().setURI(cache.getGitUrl());
            if (!StringUtils.isBlank(getGitBranch())) {
                cloneCommand.setBranchesToClone(Arrays.asList("refs/heads/" + cache.getGitBranch())).setBranch(cache.getGitBranch());
            }
            cloneCommand.setCredentialsProvider(new UsernamePasswordCredentialsProvider(cache.getGitUsername(), cache.getGitPassword())).call();
        } catch (GitAPIException e) {
            model.addException("Git clone Error. Error:" + e.getMessage(), e);
            logger.error(e.getMessage(), e);
        }
        if (checkExceptions()) {
            Notification.show(String.format("The repository %s was successfully cloned.", cache.getGitUrl()), "info", null, "before_end", 4000);
        }
    }

    @Command
    public void pullGitRepo() {
        commonService.pullGitRepo();
    }

    @Command
    public void pushGitRepo() {
        commonService.pushGitRepo();
    }


    public String getConfig() {
        checkExceptions();
        return model.getConfig();
    }

    private boolean checkExceptions() {
        if (model.hasValidationErrors()) {
            Window window = (Window) Executions.createComponents(Global.PATH_TO_ERROR_RESOURCE_ZUL, null, Map.of("errors", model.getValidationExceptions()));
            window.doModal();
            model.setValidationExceptions(new ArrayList<>());
            return false;
        }
        return true;
    }


    public void setGitUrl(String gitUrl) {
        cache.setGitUrl(gitUrl);
    }

    public String getGitUrl() {
        return cache.getGitUrl();
    }

    public void setGitBranch(String gitBranch) {
        cache.setGitBranch(gitBranch);
    }

    public String getGitBranch() {
        return cache.getGitBranch();
    }

    public void setGitUsername(String gitUsername) {
        cache.setGitUsername(gitUsername);
    }

    public String getGitUsername() {
        return cache.getGitUsername();
    }

    public void setGitPassword(String gitPassword) {
        cache.setGitPassword(gitPassword);
    }

    public String getGitPassword() {
        return cache.getGitPassword();
    }

    public void setGitEmail(String gitEmail) {
        cache.setGitEmail(gitEmail);
    }

    public String getGitEmail() {
        return cache.getGitEmail();
    }

}