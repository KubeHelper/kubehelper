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
package com.kubehelper.common;

import java.util.HashMap;
import java.util.Map;

/**
 * Resources for labels view.
 *
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
