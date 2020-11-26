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

import com.google.common.io.Files;
import com.kubehelper.common.KubeAPI;
import com.kubehelper.domain.models.FeaturesModel;
import com.kubehelper.domain.models.SearchModel;
import com.kubehelper.domain.results.FeatureResult;
import io.kubernetes.client.Exec;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.models.V1Pod;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Search service.
 *
 * @author JDev
 */
@Service
public class FeaturesService {

    private static Logger logger = LoggerFactory.getLogger(FeaturesService.class);

    @Autowired
    private KubeAPI kubeAPI;

    @Autowired
    private Exec exec;

    public void parsePredefinedCommands(FeaturesModel featuresModel) {
        try {
            List<String> lines = Files.readLines(new File(this.getClass().getResource(featuresModel.getPredefinedCommandsPath()).toURI()), Charset.forName("UTF-8"));
            parsePredefinedCommandsFromLines(lines, featuresModel);
        } catch (IOException | URISyntaxException e) {
            featuresModel.addParseException(e);
            logger.error(e.getMessage(), e);
        }
    }

    public void parseUserCommands(FeaturesModel featuresModel) {
        try {
            Set<String> userCommandFiles = getUserCommandFilesPaths(featuresModel.getUserCommandsPath(), 5);
            for (String filePath : userCommandFiles) {
                List<String> lines = Files.readLines(new File(filePath), Charset.forName("UTF-8"));
                parsePredefinedCommandsFromLines(lines, featuresModel);
            }
        } catch (IOException e) {
            featuresModel.addParseException(e);
            logger.error(e.getMessage(), e);
        }
    }

    public Set<String> getUserCommandFilesPaths(String dir, int depth) throws IOException {
        try (Stream<Path> stream = java.nio.file.Files.walk(Paths.get(dir), depth)) {
            return stream
                    .map(path -> path.toString()).filter(f -> f.endsWith(".kh"))
                    .collect(Collectors.toSet());
        }
    }


    public void parsePredefinedCommandsFromLines(List<String> lines, FeaturesModel featuresModel) {
        ListIterator<String> iterator = lines.listIterator();
        while (iterator.hasNext()) {
            try {
                buildFeatureResult(iterator, featuresModel);
            } catch (RuntimeException e) {
                featuresModel.addParseException(e);
                logger.error(e.getMessage(), e);
            }
        }
    }

    private void buildFeatureResult(ListIterator<String> iterator, FeaturesModel featuresModel) {
        FeatureResult featureResult = new FeatureResult(featuresModel.getFeaturesResults().size() + 1);
        String line = iterator.next();
        if (line.startsWith("Group:")) {
            featureResult.setGroup(getLineContent(line));
            line = iterator.next();
        }
        if (line.startsWith("Description:")) {
            featureResult.setDescription(getLineContent(line));
            line = iterator.next();
        }

        if (line.startsWith("Rows:")) {
            String[] split = getLineContent(line).split("\\|");
            featureResult.setRows(Arrays.asList(split));
            line = iterator.next();
        }

        if (!StringUtils.startsWithAny(line, "Group:", "Description:", "Rows:") && StringUtils.isNotBlank(line)) {
            StringBuilder builder = new StringBuilder();
            while (line.trim().endsWith("\\")) {
                builder.append(line.substring(0, line.lastIndexOf("\\")).trim()).append(" ");
                line = iterator.next();
            }
            if (StringUtils.isNotBlank(line)) {
                builder.append(line.trim());
            }
            featureResult.setCommand(builder.toString());
        }
        if (StringUtils.isNotBlank(line)) {
            validateAndAddFeatureResult(featureResult, featuresModel);
        }
    }

    private void validateAndAddFeatureResult(FeatureResult fr, FeaturesModel featuresModel) {
        if (StringUtils.isAnyBlank(fr.getGroup(), fr.getDescription(), fr.getCommand())) {
            featuresModel.addParseException(new RuntimeException("Command parse Error. Group, Description and command itself are mandatory fields. Object: " + fr.toString()));
            logger.error("Command parse Error. Group, Description and command itself are mandatory fields. Object: " + fr.toString());
        } else {
            featuresModel.addFeatureResult(fr);
        }

    }

    private String getLineContent(String line) {
        return line.substring(line.indexOf(":") + 1).trim();
    }


    /**
     * Executes env command on pod, and collects native environment variables.
     *
     * @param pod         - kubernetes pod
     * @param searchModel - search model
     * @return - properties object with key=value native environment variables map.
     */
    private Properties getPodEnvironmentVars(V1Pod pod, SearchModel searchModel) {
        Properties envVars = new Properties();
        String[] command = new String[]{"env"};
        try {
            Process process = exec.exec(pod, command, false);
            BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = br.readLine()) != null) {
                int idx = line.indexOf('=');
                if (idx != -1) {
                    String key = line.substring(0, idx);
                    String value = line.substring(idx + 1);
                    envVars.setProperty(key, value);
                }
            }
        } catch (ApiException | IOException | RuntimeException e) {
            searchModel.addSearchException(e);
            logger.error(e.getMessage(), e);
        }
        return envVars;
    }


    private String getParsedCreationTime(DateTime dateTime) {
        return dateTime.toString("dd.MM.yyyy HH:mm:ss");
    }

}
