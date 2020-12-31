package com.kubehelper.configs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.zkoss.lang.Library;
import org.zkoss.zk.ui.WebApps;

import javax.annotation.PostConstruct;

/**
 * @author JDev
 */

//@Profile("dev")
@Configuration
public class KubeHelperDevProfile {

    private static Logger logger = LoggerFactory.getLogger(CustomConfig.class);

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
        Library.setProperty("org.zkoss.theme.preferred", "iceblue_c");
//        Library.setProperty("org.zkoss.theme.preferred", "breeze");
//        Library.setProperty("org.zkoss.theme.preferred", "flatly");
        Library.setProperty("org.zkoss.zul.progressbox.position", "center");

        // enable non minified js
        WebApps.getCurrent().getConfiguration().setDebugJS(true);

        // enable for debugging MVVM commands and binding (very verbose)
        Library.setProperty("org.zkoss.bind.DebuggerFactory.enable", "true");
    }
}
