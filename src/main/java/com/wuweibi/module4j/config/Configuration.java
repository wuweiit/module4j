package com.wuweibi.module4j.config;

import java.util.Map;

/**
 * 配置对象
 *
 *
 * Created by ROOT on 2016/10/27.
 */
public class Configuration {
    // 模块上下文名称
    public final static String MODULE_CONTEXT = "mrcms_moduleContext";

    // 自动部署目录
    public final static String DIR_MODULES  = "MSEI.auto.deploy.dir";

    // 缓存目录
    public final static String DIR_CACHE = "MSEI.cache.rootdir";

    // 日志级别
    public final static String LOG_LEVEL = "MSEI.log.level";

    // 日志级别
    public final static String CONFIG_ACTIVATOR = "activator";



    private Map<String, String> config;

    public Configuration(Map<String, String> config) {
        this.config = config;
    }


    /**
     * 获取模板部署目录
     * @return
     */
    public String getModulesDeployDir(){
        return config.getOrDefault(DIR_MODULES,"./modules");
    }

    public String getActivatorFile() {
        return config.getOrDefault(CONFIG_ACTIVATOR,"./src/activator.groovy");
    }
}
