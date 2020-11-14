package com.kubehelper.common;

import java.util.HashMap;
import java.util.Map;

/**
 * @author JDev
 */
public enum ResourceProperty {

    LABEL("Label"),
    ANNOTATION("Annotation"),
    SELECTOR("Selector"),
    NODE_SELECTOR("Node Selector");

    private String value;

    private static final Map<ResourceProperty, String> resourcePropertiesMap = new HashMap<ResourceProperty, String>();

    static {
        for (ResourceProperty rp : values()) {
            resourcePropertiesMap.put(rp, rp.getValue());
        }
    }

    ResourceProperty(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static String getValueByKey(String key) {
        return resourcePropertiesMap.get(ResourceProperty.valueOf(key));
    }

}
