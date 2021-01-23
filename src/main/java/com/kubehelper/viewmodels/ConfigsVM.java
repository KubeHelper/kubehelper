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
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jgit.api.CheckoutCommand;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.CreateBranchCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.api.PullCommand;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.ConfigInvalidException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.StoredConfig;
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
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zk.ui.util.Notification;
import org.zkoss.zkplus.spring.DelegatingVariableResolver;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Div;
import org.zkoss.zul.Window;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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

    @WireVariable
    private CommonService commonService;

    @WireVariable("kubeHelperCache")
    private KubeHelperCache cache;

    private String gitRepoLocationPath;

    @Wire("#configBlockId")
    private Div notificationContainer;

    @Init
    public void init() {
        model = new ConfigsModel();
        model.setConfig(Global.configString);
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

        commonService.readAndValidateConfig(model, config);
        if (checkExceptions()) return;

        commonService.checkAndStartJobsFromConfig(model);
        if (checkExceptions()) return;

        commonService.updateConfigFile(model);
        Notification.show("The configurations was successfully saved.", "info", notificationContainer, "top_right", 4000);

        BindUtils.postNotifyChange(this, ".");
        Clients.evalJavaScript("highlightConfig();");
    }


    /**
     * Clones git repo with commands and configs.
     */
    @Command
    public void cloneGitRepo() {
        if (StringUtils.isBlank(getGitUrl())) {
            Notification.show("Please enter a valid git url in order to clone the repository.", "error", notificationContainer, "top_right", 5000);
            return;
        }

        CloneCommand cloneCommand = Git.cloneRepository().setURI(cache.getGitUrl()).setDirectory(new File(gitRepoLocationPath));

        try {
            //checkout public repo with default branch
            if (StringUtils.isAllEmpty(cache.getGitUser(), cache.getGitPassword(), cache.getGitBranch())) {
                cloneCommand.call();
                showSuccessfullyClonedNotification();
                return;
                //checkout public repo with defined branch
            } else if (StringUtils.isAllEmpty(cache.getGitUser(), cache.getGitPassword())) {
                cloneCommand.setBranchesToClone(Collections.singletonList("refs/heads/" + cache.getGitBranch()))
                        .setBranch(cache.getGitBranch())
                        .call();
                showSuccessfullyClonedNotification();
                return;
            }

            //clone private repo with branch and credentials
            if (!StringUtils.isBlank(getGitBranch())) {
                cloneCommand.setBranchesToClone(Collections.singletonList("refs/heads/" + cache.getGitBranch())).setBranch(cache.getGitBranch());
            }

            cloneCommand.setCredentialsProvider(new UsernamePasswordCredentialsProvider(cache.getGitUser(), cache.getGitPassword())).call();
            showSuccessfullyClonedNotification();
        } catch (GitAPIException e) {
            Notification.show(String.format("Git clone Error. Error: %s", e.getMessage()), "error", notificationContainer, "top_right", 5000);
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * Pull changes from remote.
     */
    @Command
    public void pullGitRepo() {
        PullResult result;
        Git git;

        try {
            git = Git.open(new File(gitRepoLocationPath));
        } catch (IOException e) {
            Notification.show(String.format("Cannot build git object in folder %s . The folder may not be empty. Error: %s", gitRepoLocationPath, e.getMessage()), "error", notificationContainer,
                    "top_right", 5000);
            logger.error(e.getMessage(), e);
            return;
        }

        try {
            PullCommand pullCommand = git.pull();
            //set credentials if not empty
            if (StringUtils.isNoneBlank(getGitUser(), getGitPassword())) {
                pullCommand.setCredentialsProvider(new UsernamePasswordCredentialsProvider(getGitUser(), getGitPassword()));
            }

            // if pull repo is other than default
            if (StringUtils.isNotBlank(getGitBranch()) && !getGitBranch().equals(git.getRepository().getBranch())) {
                CheckoutCommand checkout = git.checkout();

                //check if branch exists locally. If not then create new branch to pull from remote.
                if (isLocalBranchNotExists(git, getGitBranch())) {
                    checkout.setCreateBranch(true);
                }
                //pull changes from remote
                checkout.setName(cache.getGitBranch())
                        .setUpstreamMode(CreateBranchCommand.SetupUpstreamMode.SET_UPSTREAM)
                        .setStartPoint("origin/" + cache.getGitBranch()).call();
            }

            //pull
            result = pullCommand.call();
        } catch (GitAPIException | IOException e) {
            Notification.show(String.format("Cannot pull from repository %s in folder %s. Error: %s", cache.getGitUrl(), gitRepoLocationPath, e.getMessage()), "error", notificationContainer,
                    "top_right", 5000);
            logger.error(e.getMessage(), e);
            return;
        }
        if (result.isSuccessful()) {
            Notification.show(String.format("Pull from repository %s was successful. Pull result: %s", cache.getGitUrl(), result.toString()), "info", notificationContainer,
                    "top_right",
                    5000);
        } else {
            Notification.show(String.format("Pull from repository %s was unsuccessful. Pull result: %s", cache.getGitUrl(), result.toString()), "error", notificationContainer,
                    "top_right", 5000);
        }
    }

    /**
     * Push to git repo.
     */
    @Command
    public void pushGitRepo() {
        PushCommand pushCommand;
        Git git;

        try {
            git = Git.open(new File(gitRepoLocationPath));
        } catch (IOException e) {
            Notification.show(String.format("Cannot build git object in folder %s . The folder may not be empty. Error: %s", gitRepoLocationPath, e.getMessage()), "error", notificationContainer,
                    "top_right", 5000);
            logger.error(e.getMessage(), e);
            return;
        }
        try {
            //set user and email if they exists
            setGitUserAndEmail(git);
            //add akk and commit
            git.add().addFilepattern(".").call();
            git.commit().setMessage("Kube Helper commit").call();
            pushCommand = git.push();

            //push with credentials
            pushCommand.setRemote("origin").setCredentialsProvider(new UsernamePasswordCredentialsProvider(cache.getGitUser(), cache.getGitPassword())).call();
        } catch (GitAPIException | IOException | ConfigInvalidException e) {
            Notification.show(String.format("Cannot push to repository %s in folder %s. Error: %s", cache.getGitUrl(), gitRepoLocationPath, e.getMessage()), "error", notificationContainer,
                    "top_right", 5000);
            logger.error(e.getMessage(), e);
            return;
        }
        //success notification
        Notification.show(String.format("Push to repository %s was successful. Pull pushCommand: %s", cache.getGitUrl(), pushCommand.toString()), "info", notificationContainer, "top_right",
                5000);
    }

    /**
     * Set user and email for push if exists.
     *
     * @param git - git object.
     * @throws IOException            - IOException
     * @throws ConfigInvalidException - ConfigInvalidException
     */
    private void setGitUserAndEmail(Git git) throws IOException, ConfigInvalidException {
        StoredConfig config = git.getRepository().getConfig();
        if (StringUtils.isNotBlank(getGitUser()) || StringUtils.isNotBlank(getGitEmail())) {
            config.load();
            config.setString("user", null, "name", getGitUser());
            config.setString("user", null, "email", getGitEmail());
            config.save();
        }
    }


    public String getConfig() {
        return model.getConfig();
    }

    /**
     * If the git needs to pull from a branch that is different from the default branch, then you need to create a local branch. This function checks if a local branch exists.
     *
     * @param git    - git object.
     * @param branch - new or existed branch.
     * @return - true if local branch doe not exists.
     */
    private boolean isLocalBranchNotExists(Git git, String branch) {
        List<Ref> branches = null;
        try {
            branches = git.branchList().setListMode(ListBranchCommand.ListMode.ALL).call();
        } catch (GitAPIException e) {
            logger.error(e.getMessage(), e);
        }

        return CollectionUtils.isNotEmpty(branches) ? !branches.stream().filter(ref -> ref.getName().endsWith("refs/heads/" + branch)).findFirst().isPresent() : true;
    }

    /**
     * Check exceptions at model and shows in popup.
     *
     * @return - true if model has no exceptions.
     */
    private boolean checkExceptions() {
        if (model.hasValidationErrors()) {
            Window window = (Window) Executions.createComponents(Global.PATH_TO_ERROR_RESOURCE_ZUL, null, Map.of("errors", model.getValidationExceptions()));
            window.doModal();
            model.setValidationExceptions(new ArrayList<>());
            return true;
        }
        return false;
    }

    /**
     * Shows success clone message.
     */
    private void showSuccessfullyClonedNotification() {
        Notification.show(String.format("The repository %s was successfully cloned into %s. Default branch %s", getGitUrl(), gitRepoLocationPath, getGitBranch()), "info",
                notificationContainer, "top_right", 5000);
    }


    public void setGitUrl(String gitUrl) {
        cache.setGitUrl(gitUrl);
    }

    public String getGitUrl() {
        return Objects.nonNull(Global.config) && StringUtils.isNotBlank(Global.config.getGit().getUrl()) ? Global.config.getGit().getUrl() : cache.getGitUrl();
    }

    public void setGitBranch(String gitBranch) {
        cache.setGitBranch(gitBranch);
    }

    public String getGitBranch() {
        return Objects.nonNull(Global.config) && StringUtils.isNotBlank(Global.config.getGit().getBranch()) ? Global.config.getGit().getBranch() : cache.getGitBranch();
    }

    public void setGitUser(String gitUser) {
        cache.setGitUser(gitUser);
    }

    public String getGitUser() {
        return Objects.nonNull(Global.config) && StringUtils.isNotBlank(Global.config.getGit().getUser()) ? Global.config.getGit().getUser() : cache.getGitUser();
    }

    public void setGitPassword(String gitPassword) {
        cache.setGitPassword(gitPassword);
    }

    public String getGitPassword() {
        return Objects.nonNull(Global.config) && StringUtils.isNotBlank(Global.config.getGit().getPassword()) ? Global.config.getGit().getPassword() : cache.getGitPassword();
    }

    public void setGitEmail(String gitEmail) {
        cache.setGitEmail(gitEmail);
    }

    public String getGitEmail() {
        return Objects.nonNull(Global.config) && StringUtils.isNotBlank(Global.config.getGit().getEmail()) ? Global.config.getGit().getEmail() : cache.getGitEmail();
    }

}