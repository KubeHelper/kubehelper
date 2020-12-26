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
package com.kubehelper.services;

import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1NamespaceList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.zkoss.zul.Messagebox;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author JDev
 */
@Service
public class CommonService {

    @Autowired
    private CoreV1Api api;

    private static Logger logger = LoggerFactory.getLogger(CommonService.class);

    public List<String> getAllNamespaces() {
        List<String> namespaces = new ArrayList<String>(Arrays.asList("all"));
        V1NamespaceList v1NamespacesList = null;
        try {
            v1NamespacesList = api.listNamespace(null, false, null, null, null, 0, null, 30, false);
            v1NamespacesList.getItems().forEach(v1Namespace -> namespaces.add(v1Namespace.getMetadata().getName()));
        } catch (ApiException e) {
            Messagebox.show(e.getMessage(), "Fetch Namespaces Error", Messagebox.OK, Messagebox.ERROR);
            e.printStackTrace();
        }
        return namespaces;
    }


    public boolean checkEqualsFilter(String resource, String filter) {
        if (filter.equals("")) {
            return true;
        }
        if (resource.equals(filter)) {
            return true;
        }
        return false;
    }

    public String getResourcesAsStringByPath(String path) {
        String data = "";
        ClassPathResource cpr = new ClassPathResource(path);
        try {
            byte[] bdata = FileCopyUtils.copyToByteArray(cpr.getInputStream());
            data = new String(bdata, StandardCharsets.UTF_8);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return data;
    }

    public File getResourcesAsFileByPath(String path) {
        File file = null;
        try {
            file = new ClassPathResource(path).getFile();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return file;
    }
}
