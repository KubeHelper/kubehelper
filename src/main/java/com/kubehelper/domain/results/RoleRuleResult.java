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
package com.kubehelper.domain.results;

import com.kubehelper.common.Resource;

import java.util.ArrayList;
import java.util.List;

/**
 * @author JDev
 */
public class RoleRuleResult {
    private int id;
    private List<String> apiGroups = new ArrayList<>();
    private List<String> nonResourceURLs = new ArrayList<>();
    private List<String> resourceNames = new ArrayList<>();
    private List<String> resources = new ArrayList<>();
    private List<String> verbs = new ArrayList<>();
    private String fullDefinition = "";

    //Generate a simple panel with 4 columns and 2 rows in one row create, read, update and delete and below ist verbs which belongs to this CRUD will be shown if rule is selected
    private boolean create;
    private boolean read;
    private boolean update;
    private boolean delete;

    public RoleRuleResult() {
    }

    public RoleRuleResult(int id) {
        this.id = id;
    }

}