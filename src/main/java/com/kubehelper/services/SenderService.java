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
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * The scheduler service.
 *
 * @author JDev
 */
@Service
public class SenderService {

    private static Logger logger = LoggerFactory.getLogger(SenderService.class);

    @Autowired
    private CommonService commonService;

    //
    @PostConstruct
    private void postConstruct() {
//        TODO check config from config file
//        reportEntryTemplate = commonService.getClasspathResourceAsStringByPath(historyEntryTemplateSrcPath);

    }

    public void sendEmail(String email, String content) {

    }

}
