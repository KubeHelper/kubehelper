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
import com.kubehelper.domain.results.UtilResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author JDev
 */
public class VersionsModel implements PageModel {

    private final String templateUrl = "~./zul/kubehelper/pages/versions.zul";

    private final String KUBECTL_UTIL_DESCR = "The kubectl command line tool lets you control Kubernetes clusters.";
    private final List<String> KUBECTL_UTIL_LINKS = Arrays.asList("https://kubernetes.io/docs/reference/kubectl/overview/", "https://kubernetes.io/docs/reference/kubectl/cheatsheet/");

    private final String KUBECTL_KREW_DESCR = "Krew is the package manager for kubectl plugins. Krew is a tool that makes it easy to use kubectl plugins. Krew helps you discover plugins, install and manage them on your machine. It is similar to tools like apt, dnf or brew. Today, over 100 kubectl plugins are available on Krew.";
    private final List<String> KUBECTL_KREW_LINKS = Arrays.asList("https://github.com/kubernetes-sigs/krew", "https://krew.sigs.k8s.io", "https://kubernetes.io/docs/tasks/extend-kubectl/kubectl-plugins/");

    private final String KREW_A_MATRIX_DESCR = "Review Access - kubectl plugin to show an access matrix for server resources.";
    private final List<String> KREW_A_MATRIX_LINKS = Collections.singletonList("https://github.com/corneliusweig/rakkess");
    private final String KREW_A_MATRIX_CHECK_VER_COMM = "kubectl krew info access-matrix | grep 'VERSION' | awk '{print $2}'";

    private final String KREW_DEPR_DESCR = "Checks for deprecated objects in a cluster.";
    private final List<String> KREW_DEPR_LINKS = Collections.singletonList("https://github.com/rikatz/kubepug");
    private final String KREW_DEPR_CHECK_VER_COMM = "kubectl krew info deprecations | grep 'VERSION' | awk '{print $2}'";

    private final String KREW_DF_PV_DESCR = "A kubectl plugin to see df for persistent volumes.";
    private final List<String> KREW_DF_PV_LINKS = Collections.singletonList("https://github.com/yashbhutwala/kubectl-df-pv");
    private final String KREW_DF_PV_CHECK_VER_COMM = "kubectl krew info df-pv | grep 'VERSION' | awk '{print $2}'";

    private final String KREW_GET_ALL_DESCR = "Like `kubectl get all` but _really_ everything";
    private final List<String> KREW_GET_ALL_LINKS = Collections.singletonList("https://github.com/corneliusweig/ketall");
    private final String KREW_GET_ALL_CHECK_VER_COMM = "kubectl krew info get-all | grep 'VERSION' | awk '{print $2}'";

    private final String KREW_IMAGES_DESCR = "Show container images used in the cluster.";
    private final List<String> KREW_IMAGES_LINKS = Collections.singletonList("https://github.com/chenjiandongx/kubectl-images");
    private final String KREW_IMAGES_CHECK_VER_COMM = "kubectl krew info images | grep 'VERSION' | awk '{print $2}'";

    private final String KREW_INGR_NGINX_DESCR = "Interact with ingress-nginx.";
    private final List<String> KREW_INGR_NGINX_LINKS = Collections.singletonList("https://kubernetes.github.io/ingress-nginx/kubectl-plugin/");
    private final String KREW_INGR_NGINX_CHECK_VER_COMM = "kubectl krew info ingress-nginx | grep 'VERSION' | awk '{print $2}'";

    private final String KREW_NP_VIEWER_DESCR = "Network Policies rules viewer.";
    private final List<String> KREW_NP_VIEWER_LINKS = Collections.singletonList("https://github.com/runoncloud/kubectl-np-viewer");
    private final String KREW_NP_VIEWER_CHECK_VER_COMM = "kubectl krew info np-viewer | grep 'VERSION' | awk '{print $2}'";

    private final String KREW_OUTDATED_DESCR = "Finds outdated container images running in a cluster.";
    private final List<String> KREW_OUTDATED_LINKS = Collections.singletonList("https://github.com/replicatedhq/outdated");
    private final String KREW_OUTDATED_CHECK_VER_COMM = "kubectl krew info outdated | grep 'VERSION' | awk '{print $2}'";

    private final String KREW_POPEYE_DESCR = "Scans your clusters for potential resource issues.";
    private final List<String> KREW_POPEYE_LINKS = Arrays.asList("https://popeyecli.io", "https://github.com/derailed/popeye");
    private final String KREW_POPEYE_CHECK_VER_COMM = "kubectl krew info popeye | grep 'VERSION' | awk '{print $2}'";

    private final String KREW_RBAC_DESCR = "Reverse lookup for RBAC.";
    private final List<String> KREW_RBAC_LINKS = Collections.singletonList("https://github.com/FairwindsOps/rbac-lookup");
    private final String KREW_RBAC_CHECK_VER_COMM = "kubectl krew info rbac-lookup | grep 'VERSION' | awk '{print $2}'";

    private final String KREW_RES_CAP_DESCR = "Provides an overview of resource requests, limits, and utilization.";
    private final List<String> KREW_RES_CAP_LINKS = Collections.singletonList("https://github.com/robscott/kube-capacity");
    private final String KREW_RES_CAP_CHECK_VER_COMM = "kubectl krew info resource-capacity | grep 'VERSION' | awk '{print $2}'";

    private final String KREW_ROLESUM_DESCR = "Summarize RBAC roles for subjects.";
    private final List<String> KREW_ROLESUM_LINKS = Collections.singletonList("https://github.com/Ladicle/kubectl-rolesum");
    private final String KREW_ROLESUM_CHECK_VER_COMM = "kubectl krew info rolesum | grep 'VERSION' | awk '{print $2}'";

    private final String KREW_SCORE_DESCR = "Kubernetes static code analysis.";
    private final List<String> KREW_SCORE_LINKS = Collections.singletonList("https://github.com/zegl/kube-score");
    private final String KREW_SCORE_CHECK_VER_COMM = "kubectl krew info score | grep 'VERSION' | awk '{print $2}'";

    private final String KREW_TREE_DESCR = "Show a tree of object hierarchies through ownerReferences.";
    private final List<String> KREW_TREE_LINKS = Collections.singletonList("https://github.com/ahmetb/kubectl-tree");
    private final String KREW_TREE_CHECK_VER_COMM = "kubectl krew info tree | grep 'VERSION' | awk '{print $2}'";

    private final String KREW_VIEW_ALLOC_DESCR = "List allocations per resources, nodes, pods.";
    private final List<String> KREW_VIEW_ALLOC_LINKS = Collections.singletonList("https://github.com/davidB/kubectl-view-allocations");
    private final String KREW_VIEW_ALLOC_CHECK_VER_COMM = "kubectl krew info view-allocations | grep 'VERSION' | awk '{print $2}'";

    private final String KREW_VIEW_UTIL_DESCR = "Shows cluster cpu and memory utilization.";
    private final List<String> KREW_VIEW_UTIL_LINKS = Collections.singletonList("https://github.com/etopeter/kubectl-view-utilization");
    private final String KREW_VIEW_UTIL_CHECK_VER_COMM = "kubectl krew info view-utilization | grep 'VERSION' | awk '{print $2}'";

    private final String KREW_WHO_CAN_DESCR = "Shows who has RBAC permissions to access Kubernetes resources.";
    private final List<String> KREW_WHO_CAN_LINKS = Collections.singletonList("https://github.com/aquasecurity/kubectl-who-can");
    private final String KREW_WHO_CAN_CHECK_VER_COMM = "kubectl krew info who-can | grep 'VERSION' | awk '{print $2}'";

    private final String UTIL_JQ_DESCR = "jq is a lightweight and flexible command-line JSON processor.";
    private final List<String> UTIL_JQ_LINKS = Arrays.asList("https://stedolan.github.io/jq/", "https://github.com/stedolan/jq");

    private final String UTIL_GIT_DESCR = "Git is a free and open source distributed version control system designed to handle everything from small to very large projects with speed and efficiency.";
    private final List<String> UTIL_GIT_LINKS = Collections.singletonList("https://git-scm.com");

    private final String UTIL_CURL_DESCR = "curl is a tool to transfer data from or to a server, using one of the supported protocols (DICT, FILE, FTP, FTPS, GOPHER, HTTP, HTTPS, IMAP, IMAPS, LDAP, LDAPS, MQTT, POP3, POP3S, RTMP, RTMPS, RTSP, SCP, SFTP, SMB, SMBS, SMTP, SMTPS, TELNET and TFTP). The command is designed to work without user interaction.";
    private final List<String> UTIL_CURL_LINKS = Collections.singletonList("https://curl.se");

    private final String UTIL_WGET_DESCR = "Wget is a free software package for retrieving files using HTTP, HTTPS, FTP and FTPS, the most widely used Internet protocols.";
    private final List<String> UTIL_WGET_LINKS = Collections.singletonList("https://www.gnu.org/software/wget/");

    private final String UTIL_JAVA_DESCR = "Java. Open JDK.";
    private final List<String> UTIL_JAVA_LINKS = Collections.singletonList("https://hub.docker.com/_/openjdk");

    private final String SHELL_BASH_DESCR = "Bash is the GNU Project's shell—the Bourne Again SHell. This is an sh-compatible shell that incorporates useful features from the Korn shell (ksh) and the C shell (csh).";
    private final List<String> SHELL_BASH_LINKS = Collections.singletonList("https://www.gnu.org/software/bash/");

    private final String SHELL_FISH_DESCR = "fish is a smart and user-friendly command line shell for Linux, macOS, and the rest of the family.";
    private final List<String> SHELL_FISH_LINKS = Collections.singletonList("https://fishshell.com");

    private final String SHELL_ZSH_DESCR = "The Z shell (Zsh) is a Unix shell that can be used as an interactive login shell and as a command interpreter for shell scripting. Zsh is an extended Bourne shell with many improvements, including some features of Bash, ksh, and tcsh.";
    private final List<String> SHELL_ZSH_LINKS = Collections.singletonList("https://sourceforge.net/p/zsh/code/ci/master/tree/");

    private final String SHELL_KSH_DESCR = "KornShell (ksh) is a Unix shell which was developed by David Korn at Bell Labs in the early 1980s and announced at USENIX on July 14, 1983.";
    private final List<String> SHELL_KSH_LINKS = Collections.singletonList("https://www.well.ox.ac.uk/~johnb/comp/unix/ksh.html");

    public static String NAME = Global.VERSIONS_MODEL;
    private List<UtilResult> utilsResults = new ArrayList<>();
    private List<KubeHelperException> searchExceptions = new ArrayList<>();

    private final String KCTL_PLUGIN_CATEGORY = "Kubectl";
    private final String SHELL_CATEGORY = "Shell";
    private final String UTIL_PLUGIN_CATEGORY = "Util";

    public VersionsModel() {
        addUtil("kubectl", "kubectl", "kubectl version", KCTL_PLUGIN_CATEGORY, KUBECTL_UTIL_DESCR, KUBECTL_UTIL_LINKS);
        addUtil("krew", "kubectl krew", "kubectl version | grep 'GitTag\\|GitCommit'", KCTL_PLUGIN_CATEGORY, KUBECTL_KREW_DESCR, KUBECTL_KREW_LINKS);
        addUtil("access-matrix", "kubectl access-matrix", KREW_A_MATRIX_CHECK_VER_COMM, KCTL_PLUGIN_CATEGORY, KREW_A_MATRIX_DESCR, KREW_A_MATRIX_LINKS);
        addUtil("deprecations", "kubectl deprecations", KREW_DEPR_CHECK_VER_COMM, KCTL_PLUGIN_CATEGORY, KREW_DEPR_DESCR, KREW_DEPR_LINKS);
        addUtil("df-pv", "kubectl df-pv", KREW_DF_PV_CHECK_VER_COMM, KCTL_PLUGIN_CATEGORY, KREW_DF_PV_DESCR, KREW_DF_PV_LINKS);
        addUtil("get-all", "kubectl get-all", KREW_GET_ALL_CHECK_VER_COMM, KCTL_PLUGIN_CATEGORY, KREW_GET_ALL_DESCR, KREW_GET_ALL_LINKS);
        addUtil("images", "kubectl images", KREW_IMAGES_CHECK_VER_COMM, KCTL_PLUGIN_CATEGORY, KREW_IMAGES_DESCR, KREW_IMAGES_LINKS);
        addUtil("ingress-nginx", "kubectl ingress-nginx", KREW_INGR_NGINX_CHECK_VER_COMM, KCTL_PLUGIN_CATEGORY, KREW_INGR_NGINX_DESCR, KREW_INGR_NGINX_LINKS);
        addUtil("np-viewer", "kubectl np-viewer", KREW_NP_VIEWER_CHECK_VER_COMM, KCTL_PLUGIN_CATEGORY, KREW_NP_VIEWER_DESCR, KREW_NP_VIEWER_LINKS);
        addUtil("outdated", "kubectl outdated", KREW_OUTDATED_CHECK_VER_COMM, KCTL_PLUGIN_CATEGORY, KREW_OUTDATED_DESCR, KREW_OUTDATED_LINKS);
        addUtil("popeye", "kubectl popeye", KREW_POPEYE_CHECK_VER_COMM, KCTL_PLUGIN_CATEGORY, KREW_POPEYE_DESCR, KREW_POPEYE_LINKS);
        addUtil("rbac-lookup", "kubectl rbac-lookup", KREW_RBAC_CHECK_VER_COMM, KCTL_PLUGIN_CATEGORY, KREW_RBAC_DESCR, KREW_RBAC_LINKS);
        addUtil("resource-capacity", "kubectl resource-capacity", KREW_RES_CAP_CHECK_VER_COMM, KCTL_PLUGIN_CATEGORY, KREW_RES_CAP_DESCR, KREW_RES_CAP_LINKS);
        addUtil("rolesum", "kubectl rolesum", KREW_ROLESUM_CHECK_VER_COMM, KCTL_PLUGIN_CATEGORY, KREW_ROLESUM_DESCR, KREW_ROLESUM_LINKS);
        addUtil("score", "kubectl score", KREW_SCORE_CHECK_VER_COMM, KCTL_PLUGIN_CATEGORY, KREW_SCORE_DESCR, KREW_SCORE_LINKS);
        addUtil("tree", "kubectl tree", KREW_TREE_CHECK_VER_COMM, KCTL_PLUGIN_CATEGORY, KREW_TREE_DESCR, KREW_TREE_LINKS);
        addUtil("view-allocations", "kubectl view-allocations", KREW_VIEW_ALLOC_CHECK_VER_COMM, KCTL_PLUGIN_CATEGORY, KREW_VIEW_ALLOC_DESCR, KREW_VIEW_ALLOC_LINKS);
        addUtil("view-utilization", "kubectl view-utilization", KREW_VIEW_UTIL_CHECK_VER_COMM, KCTL_PLUGIN_CATEGORY, KREW_VIEW_UTIL_DESCR, KREW_VIEW_UTIL_LINKS);
        addUtil("who-can", "kubectl who-can", KREW_WHO_CAN_CHECK_VER_COMM, KCTL_PLUGIN_CATEGORY, KREW_WHO_CAN_DESCR, KREW_WHO_CAN_LINKS);
        addUtil("jq", "jq", "jq --version", UTIL_PLUGIN_CATEGORY, UTIL_JQ_DESCR, UTIL_JQ_LINKS);
        addUtil("git", "git", "git --version", UTIL_PLUGIN_CATEGORY, UTIL_GIT_DESCR, UTIL_GIT_LINKS);
        addUtil("curl", "curl", "curl --version | grep 'curl\\|Release-Date'", UTIL_PLUGIN_CATEGORY, UTIL_CURL_DESCR, UTIL_CURL_LINKS);
        addUtil("wget", "wget", "wget --version | grep 'GNU Wget'", UTIL_PLUGIN_CATEGORY, UTIL_WGET_DESCR, UTIL_WGET_LINKS);
        addUtil("java", "java", "java --version", UTIL_PLUGIN_CATEGORY, UTIL_JAVA_DESCR, UTIL_JAVA_LINKS);
        addUtil("bash", "bash", "bash --version | grep 'GNU bash'", SHELL_CATEGORY, SHELL_BASH_DESCR, SHELL_BASH_LINKS);
        addUtil("fish", "fish", "fish --version", SHELL_CATEGORY, SHELL_FISH_DESCR, SHELL_FISH_LINKS);
        addUtil("zsh", "zsh", "zsh --version", SHELL_CATEGORY, SHELL_ZSH_DESCR, SHELL_ZSH_LINKS);
        addUtil("ksh", "ksh", "ksh --version", SHELL_CATEGORY, SHELL_KSH_DESCR, SHELL_KSH_LINKS);
    }

    private void addUtil(String name, String shellCommand, String versionCheckCommand, String category, String description, List<String> links) {
        UtilResult result = new UtilResult(utilsResults.size() + 1)
                .setName(name)
                .setVersionCheckCommand(versionCheckCommand)
                .setShellCommand(shellCommand)
                .setCategory(category)
                .setDescription(description)
                .setLinks(links);
        utilsResults.add(result);
    }

    @Override
    public void addException(String message, Exception exception) {
        this.searchExceptions.add(new KubeHelperException(message, exception));
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getTemplateUrl() {
        return templateUrl;
    }

    public List<UtilResult> getUtilsResults() {
        return utilsResults;
    }
}
