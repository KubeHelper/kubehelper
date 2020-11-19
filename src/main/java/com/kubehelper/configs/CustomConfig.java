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
package com.kubehelper.configs;

import io.kubernetes.client.Exec;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.apis.BatchV1Api;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.apis.ExtensionsV1beta1Api;
import io.kubernetes.client.openapi.apis.NetworkingV1Api;
import io.kubernetes.client.openapi.apis.PolicyV1beta1Api;
import io.kubernetes.client.openapi.apis.RbacAuthorizationV1beta1Api;
import io.kubernetes.client.util.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.zkoss.lang.Library;
import org.zkoss.zk.ui.WebApps;

import javax.annotation.PostConstruct;
import java.io.IOException;

/**
 * @author JDev
 */
@Configuration
public class CustomConfig {

    private static Logger logger = LoggerFactory.getLogger(CustomConfig.class);

    @Bean
    public CoreV1Api getCoreV1Api() throws IOException {
        return new CoreV1Api(Config.defaultClient());
    }

    @Bean
    public AppsV1Api getAppsV1Api() throws IOException {
        return new AppsV1Api(Config.defaultClient());
    }

    @Bean
    public BatchV1Api getBatchV1Api() throws IOException {
        return new BatchV1Api(Config.defaultClient());
    }

    @Bean
    public ExtensionsV1beta1Api getExtensionsV1beta1Api() throws IOException {
        return new ExtensionsV1beta1Api(Config.defaultClient());
    }

    @Bean
    public RbacAuthorizationV1beta1Api getRbacAuthorizationV1beta1Api() throws IOException {
        return new RbacAuthorizationV1beta1Api(Config.defaultClient());
    }

    @Bean
    public NetworkingV1Api getNetworkingV1Api() throws IOException {
        return new NetworkingV1Api(Config.defaultClient());
    }

    @Bean
    public PolicyV1beta1Api getPolicyV1beta1Api() throws IOException {
        return new PolicyV1beta1Api(Config.defaultClient());
    }


    @Bean
    public Exec getExec() throws IOException {
        return new Exec(Config.defaultClient());
    }


    @PostConstruct
    public void initDevelopmentProperties() throws Exception {
        logger.info("**************************************************************");
        logger.info("**** Kube Helper: development configuration active ****");
        logger.info("**************************************************************");

        //disable various caches to avoid server restarts
        Library.setProperty("org.zkoss.zk.ZUML.cache", "false");
        Library.setProperty("org.zkoss.zk.WPD.cache", "false");
        Library.setProperty("org.zkoss.zk.WCS.cache", "false");
        Library.setProperty("org.zkoss.web.classWebResource.cache", "false");
        Library.setProperty("org.zkoss.util.label.cache", "false");
//        Library.setProperty("org.zkoss.theme.preferred", "atlantic");
//        Library.setProperty("org.zkoss.theme.preferred", "silvertail");
//        Library.setProperty("org.zkoss.theme.preferred", "sapphire");
//        Library.setProperty("org.zkoss.theme.preferred", "iceblue_c");
        Library.setProperty("org.zkoss.theme.preferred", "breeze");
        Library.setProperty("org.zkoss.zul.progressbox.position", "center");

        // enable non minified js
        WebApps.getCurrent().getConfiguration().setDebugJS(true);

        // enable for debugging MVVM commands and binding (very verbose)
        Library.setProperty("org.zkoss.bind.DebuggerFactory.enable", "true");
    }
}
