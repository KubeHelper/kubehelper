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
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author JDev
 */
@Configuration
@EnableCaching
public class Config {

    private final String CONFIGS_CACHE = "configs";

    @Autowired
    private CacheManager cacheManager;

    private List<String> hljsLanguages = Arrays.asList("livescript", "bash", "dockerfile", "apache", "xml", "markdown", "latex", "gradle", "go", "cpp",
            "http", "json", "puppet", "awk", "ruby", "haml", "julia", "lua", "processing", "c", "typescript", "groovy", "scss", "less", "nginx", "diff",
            "dos", "perl", "sql", "moonscript", "css", "vim", "dart", "scala", "java", "livecodeserver", "makefile", "profile", "plaintext", "rust",
            "powershell", "python", "python-repl", "shell", "ini", "javascript", "yaml", "properties", "xquery", "kotlin");

    public static Boolean COMMANDS_HOT_REPLACEMENT = false;

    public static String GIT_URL = "gitUrl";
    public static String GIT_BRANCH = "gitBranch";
    public static String GIT_USERNAME = "gitUsername";
    public static String GIT_PASSWORD = "gitPassword";
    public static String GIT_EMAIL = "gitEmail";

    @Bean
    public CacheManager cacheManager() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        if (Objects.nonNull(cacheManager.getCache(CONFIGS_CACHE))) {
            cacheManager.setCaches(Arrays.asList(cacheManager.getCache(CONFIGS_CACHE)));
        } else {
            cacheManager.setCaches(Arrays.asList(new ConcurrentMapCache(CONFIGS_CACHE)));
        }
        return cacheManager;
    }

//    @PostConstruct
//    public void initCache() {
//    }

    public void setGitUrl(String gitUrl) {
        cacheManager.getCache(CONFIGS_CACHE).put(GIT_URL, gitUrl);
    }

    public String getGitUrl() {
        Cache.ValueWrapper value = cacheManager.getCache(CONFIGS_CACHE).get(GIT_URL);
        return Objects.isNull(value) ? "" : (String) value.get();
    }

    public void setGitUsername(String gitUsername) {
        cacheManager.getCache(CONFIGS_CACHE).put(GIT_USERNAME, gitUsername);
    }

    public String getGitUsername() {
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
