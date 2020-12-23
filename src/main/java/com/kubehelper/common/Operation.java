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
import java.util.Optional;

/**
 * @author JDev
 */
public enum Operation {

    API_VERSIONS("api-versions"),
    DESCRIBE("describe"),
    EXEC("exec"),
    EXPLAIN("explain"),
    GET("get");

    private String value;

    private static final Map<Operation, String> operationsMap = new HashMap<Operation, String>();

    static {
        for (Operation o : values()) {
            operationsMap.put(o, o.getValue());
        }
    }

    Operation(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static String getValueByKey(String key) {
        return operationsMap.get(Operation.valueOf(key));
    }

    public static boolean isOperationInvalid(String operation) {
        Optional<String> first = operationsMap.values().stream().filter(o -> o.equals(operation)).findFirst();
        return first.isPresent() ? false : true;
    }
}
