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
import java.util.List;

/**
 * @author JDev
 */
public class VersionsModel implements PageModel {

    private String templateUrl = "~./zul/kubehelper/pages/versions.zul";

    private final String KUBECTL_UTIL_DESCR = "";
    private final List<String> KUBECTL_UTIL_LINKS = Arrays.asList("");

    private final String KUBECTL_KREW_DESCR = "";
    private final List<String> KUBECTL_KREW_LINKS = Arrays.asList("");

    private final String KREW_A_MATRIX_DESCR = "";
    private final List<String> KREW_A_MATRIX_LINKS = Arrays.asList("");

    private final String KREW_A_PSP_DESCR = "";
    private final List<String> KREW_A_PSP_LINKS = Arrays.asList("");

    private final String KREW_CAPTURE_DESCR = "";
    private final List<String> KREW_CAPTURE_LINKS = Arrays.asList("");

    private final String KREW_DESCR_DESCR = "";
    private final List<String> KREW_DESCR_LINKS = Arrays.asList("");

    private final String KREW_DF_PV_DESCR = "";
    private final List<String> KREW_DF_PV_LINKS = Arrays.asList("");

    private final String KREW_DOCTOR_DESCR = "";
    private final List<String> KREW_DOCTOR_LINKS = Arrays.asList("");

    private final String KREW_FLAME_DESCR = "";
    private final List<String> KREW_FLAME_LINKS = Arrays.asList("");

    private final String KREW_GET_ALL_DESCR = "";
    private final List<String> KREW_GET_ALL_LINKS = Arrays.asList("");

    private final String KREW_IMAGES_DESCR = "";
    private final List<String> KREW_IMAGES_LINKS = Arrays.asList("");

    private final String KREW_INGR_NGINX_DESCR = "";
    private final List<String> KREW_INGR_NGINX_LINKS = Arrays.asList("");

    private final String KREW_KUBESEC_DESCR = "";
    private final List<String> KREW_KUBESEC_LINKS = Arrays.asList("");

    private final String KREW_NP_VIEWER_DESCR = "";
    private final List<String> KREW_NP_VIEWER_LINKS = Arrays.asList("");

    private final String KREW_OUTDATED_DESCR = "";
    private final List<String> KREW_OUTDATED_LINKS = Arrays.asList("");

    private final String KREW_POPEYE_DESCR = "";
    private final List<String> KREW_POPEYE_LINKS = Arrays.asList("");

    private final String KREW_PREFLIGHT_DESCR = "";
    private final List<String> KREW_PREFLIGHT_LINKS = Arrays.asList("");

    private final String KREW_RBAC_DESCR = "";
    private final List<String> KREW_RBAC_LINKS = Arrays.asList("");

    private final String KREW_RES_CAP_DESCR = "";
    private final List<String> KREW_RES_CAP_LINKS = Arrays.asList("");

    private final String KREW_ROLESUM_DESCR = "";
    private final List<String> KREW_ROLESUM_LINKS = Arrays.asList("");

    private final String KREW_SCORE_DESCR = "";
    private final List<String> KREW_SCORE_LINKS = Arrays.asList("");

    private final String KREW_SNIFF_DESCR = "";
    private final List<String> KREW_SNIFF_LINKS = Arrays.asList("");

    private final String KREW_STARBOARD_DESCR = "";
    private final List<String> KREW_STARBOARD_LINKS = Arrays.asList("");

    private final String KREW_TRACE_DESCR = "";
    private final List<String> KREW_TRACE_LINKS = Arrays.asList("");

    private final String KREW_TREE_DESCR = "";
    private final List<String> KREW_TREE_LINKS = Arrays.asList("");

    private final String KREW_VIEW_ALLOC_DESCR = "";
    private final List<String> KREW_VIEW_ALLOC_LINKS = Arrays.asList("");

    private final String KREW_VIEW_UTIL_DESCR = "";
    private final List<String> KREW_VIEW_UTIL_LINKS = Arrays.asList("");

    private final String KREW_VIEW_WEBH_DESCR = "";
    private final List<String> KREW_VIEW_WEBH_LINKS = Arrays.asList("");

    private final String KREW_WHO_CAN_DESCR = "";
    private final List<String> KREW_WHO_CAN_LINKS = Arrays.asList("");

    private final String UTIL_JQ_DESCR = "";
    private final List<String> UTIL_JQ_LINKS = Arrays.asList("");

    private final String UTIL_GIT_DESCR = "";
    private final List<String> UTIL_GIT_LINKS = Arrays.asList("");

    private final String UTIL_CURL_DESCR = "";
    private final List<String> UTIL_CURL_LINKS = Arrays.asList("");

    private final String UTIL_WGET_DESCR = "";
    private final List<String> UTIL_WGET_LINKS = Arrays.asList("");

    private final String SHELL_BASH_DESCR = "";
    private final List<String> SHELL_BASH_LINKS = Arrays.asList("");

    private final String SHELL_FISH_DESCR = "";
    private final List<String> SHELL_FISH_LINKS = Arrays.asList("");

    private final String SHELL_ZSH_DESCR = "";
    private final List<String> SHELL_ZSH_LINKS = Arrays.asList("");

    private final String SHELL_CSH_DESCR = "";
    private final List<String> SHELL_CSH_LINKS = Arrays.asList("");

    private final String SHELL_KSH_DESCR = "";
    private final List<String> SHELL_KSH_LINKS = Arrays.asList("");

    public static String NAME = Global.VERSIONS_MODEL;
    private List<UtilResult> utilsResults = new ArrayList<>();
    private List<KubeHelperException> searchExceptions = new ArrayList<>();

    private final String KCTL_PLUGIN_CATEGORY = "Kubectl";
    private final String SHELL_CATEGORY = "Shell";
    private final String UTIL_PLUGIN_CATEGORY = "Util";

    public VersionsModel() {
//        TODO GET correct names, descriptions and links
        addUtil("Kubectl", "kubectl", "kubectl version", KCTL_PLUGIN_CATEGORY, KUBECTL_UTIL_DESCR, KUBECTL_UTIL_LINKS);
        addUtil("Krew", "kubectl krew", "kubectl version | grep 'GitTag\\|GitCommit'", KCTL_PLUGIN_CATEGORY, KUBECTL_KREW_DESCR, KUBECTL_KREW_LINKS);
        addUtil("Krew", "kubectl access-matrix", "kubectl access-matrix version", KCTL_PLUGIN_CATEGORY, KREW_A_MATRIX_DESCR, KREW_A_MATRIX_LINKS);
        addUtil("Krew", "kubectl advise-psp", "kubectl advise-psp version", KCTL_PLUGIN_CATEGORY, KREW_A_PSP_DESCR, KREW_A_PSP_LINKS);
        addUtil("Krew", "kubectl capture", "kubectl capture version", KCTL_PLUGIN_CATEGORY, KREW_CAPTURE_DESCR, KREW_CAPTURE_LINKS);
        addUtil("Krew", "kubectl deprecations", "kubectl deprecations version", KCTL_PLUGIN_CATEGORY, KREW_DESCR_DESCR, KREW_DESCR_LINKS);
        addUtil("Krew", "kubectl df-pv", "kubectl df-pv version", KCTL_PLUGIN_CATEGORY, KREW_DF_PV_DESCR, KREW_DF_PV_LINKS);
        addUtil("Krew", "kubectl doctor", "kubectl doctor version", KCTL_PLUGIN_CATEGORY, KREW_DOCTOR_DESCR, KREW_DOCTOR_LINKS);
        addUtil("Krew", "kubectl flame", "kubectl flame version", KCTL_PLUGIN_CATEGORY, KREW_FLAME_DESCR, KREW_FLAME_LINKS);
        addUtil("Krew", "kubectl get-all", "kubectl get-all version", KCTL_PLUGIN_CATEGORY, KREW_GET_ALL_DESCR, KREW_GET_ALL_LINKS);
        addUtil("Krew", "kubectl images", "kubectl images version", KCTL_PLUGIN_CATEGORY, KREW_IMAGES_DESCR, KREW_IMAGES_LINKS);
        addUtil("Krew", "kubectl ingress-nginx", "kubectl ingress-nginx version", KCTL_PLUGIN_CATEGORY, KREW_INGR_NGINX_DESCR, KREW_INGR_NGINX_LINKS);
        addUtil("Krew", "kubectl kubesec-scan", "kubectl kubesec-scan version", KCTL_PLUGIN_CATEGORY, KREW_KUBESEC_DESCR, KREW_KUBESEC_LINKS);
        addUtil("Krew", "kubectl np-viewer", "kubectl np-viewer version", KCTL_PLUGIN_CATEGORY, KREW_NP_VIEWER_DESCR, KREW_NP_VIEWER_LINKS);
        addUtil("Krew", "kubectl outdated", "kubectl outdated version", KCTL_PLUGIN_CATEGORY, KREW_OUTDATED_DESCR, KREW_OUTDATED_LINKS);
        addUtil("Krew", "kubectl popeye", "kubectl popeye version", KCTL_PLUGIN_CATEGORY, KREW_POPEYE_DESCR, KREW_POPEYE_LINKS);
        addUtil("Krew", "kubectl preflight", "kubectl preflight version", KCTL_PLUGIN_CATEGORY, KREW_PREFLIGHT_DESCR, KREW_PREFLIGHT_LINKS);
        addUtil("Krew", "kubectl rbac-lookup", "kubectl rbac-lookup version", KCTL_PLUGIN_CATEGORY, KREW_RBAC_DESCR, KREW_RBAC_LINKS);
        addUtil("Krew", "kubectl resource-capacity", "kubectl resource-capacity version", KCTL_PLUGIN_CATEGORY, KREW_RES_CAP_DESCR, KREW_RES_CAP_LINKS);
        addUtil("Krew", "kubectl rolesum", "kubectl rolesum version", KCTL_PLUGIN_CATEGORY, KREW_ROLESUM_DESCR, KREW_ROLESUM_LINKS);
        addUtil("Krew", "kubectl score", "kubectl score version", KCTL_PLUGIN_CATEGORY, KREW_SCORE_DESCR, KREW_SCORE_LINKS);
        addUtil("Krew", "kubectl sniff", "kubectl sniff version", KCTL_PLUGIN_CATEGORY, KREW_SNIFF_DESCR, KREW_SNIFF_LINKS);
        addUtil("Krew", "kubectl starboard", "kubectl starboard version", KCTL_PLUGIN_CATEGORY, KREW_STARBOARD_DESCR, KREW_STARBOARD_LINKS);
        addUtil("Krew", "kubectl trace", "kubectl trace version", KCTL_PLUGIN_CATEGORY, KREW_TRACE_DESCR, KREW_TRACE_LINKS);
        addUtil("Krew", "kubectl tree", "kubectl tree version", KCTL_PLUGIN_CATEGORY, KREW_TREE_DESCR, KREW_TREE_LINKS);
        addUtil("Krew", "kubectl view-allocations", "kubectl view-allocations version", KCTL_PLUGIN_CATEGORY, KREW_VIEW_ALLOC_DESCR, KREW_VIEW_ALLOC_LINKS);
        addUtil("Krew", "kubectl view-utilization", "kubectl view-utilization version", KCTL_PLUGIN_CATEGORY, KREW_VIEW_UTIL_DESCR, KREW_VIEW_UTIL_LINKS);
        addUtil("Krew", "kubectl view-webhook", "kubectl view-webhook version", KCTL_PLUGIN_CATEGORY, KREW_VIEW_WEBH_DESCR, KREW_VIEW_WEBH_LINKS);
        addUtil("Krew", "kubectl who-can", "kubectl who-can version", KCTL_PLUGIN_CATEGORY, KREW_WHO_CAN_DESCR, KREW_WHO_CAN_LINKS);
        addUtil("Krew", "jq", "jq --version", UTIL_PLUGIN_CATEGORY, UTIL_JQ_DESCR, UTIL_JQ_LINKS);
        addUtil("Krew", "git", "git --version", UTIL_PLUGIN_CATEGORY, UTIL_GIT_DESCR, UTIL_GIT_LINKS);
        addUtil("Krew", "curl", "curl --version", UTIL_PLUGIN_CATEGORY, UTIL_CURL_DESCR, UTIL_CURL_LINKS);
        addUtil("Krew", "wget", "wget --version", UTIL_PLUGIN_CATEGORY, UTIL_WGET_DESCR, UTIL_WGET_LINKS);
        addUtil("Krew", "bash", "bash --version", SHELL_CATEGORY, SHELL_BASH_DESCR, SHELL_BASH_LINKS);
        addUtil("Krew", "fish", "fish --version", SHELL_CATEGORY, SHELL_FISH_DESCR, SHELL_FISH_LINKS);
        addUtil("Krew", "zsh", "zsh --version", SHELL_CATEGORY, SHELL_ZSH_DESCR, SHELL_ZSH_LINKS);
        addUtil("Krew", "csh", "csh --version", SHELL_CATEGORY, SHELL_CSH_DESCR, SHELL_CSH_LINKS);
        addUtil("Krew", "ksh", "ksh --version", SHELL_CATEGORY, SHELL_KSH_DESCR, SHELL_KSH_LINKS);
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
