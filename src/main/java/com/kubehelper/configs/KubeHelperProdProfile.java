package com.kubehelper.configs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.zkoss.lang.Library;

import javax.annotation.PostConstruct;

/**
 * @author JDev
 */

@Profile("prod")
@Configuration
public class KubeHelperProdProfile {

    private static Logger logger = LoggerFactory.getLogger(CustomConfig.class);

    @PostConstruct
    public void initDevelopmentProperties() throws Exception {
        logger.info("**************************************************************");
        logger.info("**** Kube Helper: Prod configuration active ****");
        logger.info("**************************************************************");

        //disable various caches to avoid server restarts
//        Library.setProperty("org.zkoss.zk.ZUML.cache", "false");
//        Library.setProperty("org.zkoss.zk.WPD.cache", "false");
//        Library.setProperty("org.zkoss.zk.WCS.cache", "false");
//        Library.setProperty("org.zkoss.web.classWebResource.cache", "false");
//        Library.setProperty("org.zkoss.util.label.cache", "false");
//        Library.setProperty("org.zkoss.theme.preferred", "atlantic");
//        Library.setProperty("org.zkoss.theme.preferred", "silvertail");
//        Library.setProperty("org.zkoss.theme.preferred", "sapphire");
        Library.setProperty("org.zkoss.theme.preferred", "iceblue_c");
//        Library.setProperty("org.zkoss.theme.preferred", "breeze");
//        Library.setProperty("org.zkoss.theme.preferred", "flatly");
        Library.setProperty("org.zkoss.zul.progressbox.position", "center");
    }
}
