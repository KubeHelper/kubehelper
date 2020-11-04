package com.kubehelper.configs;

import io.kubernetes.client.Exec;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.apis.ExtensionsV1beta1Api;
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
    public ExtensionsV1beta1Api getExtensionsV1beta1Api() throws IOException {
        return new ExtensionsV1beta1Api(Config.defaultClient());
    }

//    @Bean
//    public AppsV1beta1Api getAppsV1beta1Api() throws IOException {
//        return new AppsV1beta1Api(Config.defaultClient());
//    }
//
//    @Bean
//    public AppsV1beta2Api getAppsV1beta2Api() throws IOException {
//        return new AppsV1beta2Api(Config.defaultClient());
//    }

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
