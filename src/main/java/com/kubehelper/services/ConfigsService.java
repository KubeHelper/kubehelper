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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 *The config service contains method to validate and store configuration.
 *
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

    @PostConstruct
    private void postConstruct() {
//        reportEntryTemplate = commonService.getClasspathResourceAsStringByPath(historyEntryTemplateSrcPath);
    }

}
