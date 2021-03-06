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
package com.kubehelper.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.kubehelper.common.Global.CONFIGS_CACHE;

/**
 * @author JDev
 */
@Configuration
@EnableCaching
public class KubeHelperCache {

    @Autowired
    private CacheManager cacheManager;

    public static String GIT_URL = "gitUrl";
    public static String GIT_BRANCH = "gitBranch";
    public static String GIT_USERNAME = "gitUser";
    public static String GIT_PASSWORD = "gitPassword";
    public static String GIT_EMAIL = "gitEmail";


    public void setGitUrl(String gitUrl) {
        cacheManager.getCache(CONFIGS_CACHE).put(GIT_URL, gitUrl);
    }

    public String getGitUrl() {
        Cache.ValueWrapper value = cacheManager.getCache(CONFIGS_CACHE).get(GIT_URL);
        return Objects.isNull(value) ? "" : (String) value.get();
    }

    public void setGitUser(String gitUser) {
        cacheManager.getCache(CONFIGS_CACHE).put(GIT_USERNAME, gitUser);
    }

    public String getGitUser() {
        Cache.ValueWrapper value = cacheManager.getCache(CONFIGS_CACHE).get(GIT_USERNAME);
        return Objects.isNull(value) ? "" : (String) value.get();
    }

    public void setGitPassword(String gitPassword) {
        cacheManager.getCache(CONFIGS_CACHE).put(GIT_PASSWORD, gitPassword);
    }

    public String getGitPassword() {
        Cache.ValueWrapper value = cacheManager.getCache(CONFIGS_CACHE).get(GIT_PASSWORD);
        return Objects.isNull(value) ? "" : (String) value.get();
    }

    public void setGitEmail(String gitEmail) {
        cacheManager.getCache(CONFIGS_CACHE).put(GIT_EMAIL, gitEmail);
    }

    public String getGitEmail() {
        Cache.ValueWrapper value = cacheManager.getCache(CONFIGS_CACHE).get(GIT_EMAIL);
        return Objects.isNull(value) ? "" : (String) value.get();
    }

    public String getGitBranch() {
        Cache.ValueWrapper value = cacheManager.getCache(CONFIGS_CACHE).get(GIT_BRANCH);
        return Objects.isNull(value) ? "" : (String) value.get();
    }

    public void setGitBranch(String gitBranch) {
        cacheManager.getCache(CONFIGS_CACHE).put(GIT_BRANCH, gitBranch);
    }

}
