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

import com.kubehelper.domain.models.ConfigsModel;
import com.moandjiezana.toml.Toml;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * @author JDev
 */
@Service
public class ConfigsService {

    private static Logger logger = LoggerFactory.getLogger(ConfigsService.class);

    @Value("${kubehelper.predefined.config.path}")
    private String predefinedConfigPath;

    @Value("${kubehelper.default.config.file.path}")
    private String defaultConfigFilePath;

    @Value("${kubehelper.custom.config.location.search.path}")
    private String customConfigLocationSearchPath;

    @Autowired
    private CommonService commonService;

    /**
     * Checks, creates and search for config.
     * 1. Searches for custom kubehelper config. If not found then.
     * 2. Searches for default kubehelper config. If not found then.
     * 3. Creates new default config from predefined config.
     *
     * @param configsModel - configs model.
     */
    public void checkConfigLocation(ConfigsModel configsModel) {

        //search for custom kubehelper config
        Set<String> customConfigPath = checkCustomKubeHelperConfig(configsModel);
        if (!customConfigPath.isEmpty()) {
            configsModel.setConfig(commonService.getResourceAsStringByPath(customConfigPath.stream().findFirst().get()));
            return;
        }

        //look for default kubehelper config
        if (new File(defaultConfigFilePath).exists()) {
            configsModel.setConfig(commonService.getResourceAsStringByPath(defaultConfigFilePath));
            return;
        }

        //create new default config from predefined config
        try {
            String predefinedConfig = commonService.getClasspathResourceAsStringByPath(predefinedConfigPath);
            FileUtils.writeStringToFile(new File(defaultConfigFilePath), predefinedConfig);
            configsModel.setConfig(predefinedConfig);
        } catch (IOException e) {
            configsModel.addException("An error occurred while creating of default config file. Error" + e.getMessage(), e);
            logger.error("An error occurred while creating of default config file. Error" + e.getMessage());
        }
    }

    private Set<String> checkCustomKubeHelperConfig(ConfigsModel configsModel) {
        Set<String> customConfig = new HashSet<>();
        try {
            customConfig = commonService.getFilesPathsByDirAndExtension(customConfigLocationSearchPath, 10, "kubehelper-config.toml");
        } catch (IOException e) {
            configsModel.addException("An error occurred while searching for custom configuration. Error" + e.getMessage(), e);
            logger.error("An error occurred while searching for custom configuration. Error" + e.getMessage());
        }
        return customConfig;
    }

    public void updateConfig(ConfigsModel configsModel) {

        try {
            new Toml().read("a=1");
        } catch (RuntimeException e) {
            configsModel.addException("Configuration file is not valid. Error" + e.getMessage(), e);
            logger.error("Configuration file is not valid. Error" + e.getMessage());
        }

        Toml toml = new Toml().read("a=1");

        Set<String> customConfigPath = checkCustomKubeHelperConfig(configsModel);
        if (!customConfigPath.isEmpty()) {
            try {
                FileUtils.writeStringToFile(new File(customConfigPath.stream().findFirst().get()), configsModel.getConfig());
                return;
            } catch (IOException e) {
                configsModel.addException("An error occurred while updating config file. Error" + e.getMessage(), e);
                logger.error("An error occurred while updating config file. Error" + e.getMessage());
            }
        }

        try {
            FileUtils.writeStringToFile(new File(defaultConfigFilePath), configsModel.getConfig());
        } catch (IOException e) {
            configsModel.addException("An error occurred while updating default config file. Error" + e.getMessage(), e);
            logger.error("An error occurred while updating default config file. Error" + e.getMessage());
        }
    }
}
