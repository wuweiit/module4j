package com.wuweibi.module4j.module;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wuweibi.module4j.ModuleActivator;
import com.wuweibi.module4j.SupperModule;
import com.wuweibi.module4j.config.Configuration;
import com.wuweibi.module4j.exception.*;
import com.wuweibi.module4j.groovy.GroovyScriptUtil;
import com.wuweibi.module4j.groovy.ScriptClassLoader;
import com.wuweibi.module4j.listener.InstallListenter;
import com.wuweibi.utils.FileTools;
import javassist.*;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


/**
 * 模块容器，用于存取模块
 *
 * @author marker
 * @version 1.0
 */
public class ModuleContext {
    /**
     * 日志记录
     */
    private final Logger logger = LoggerFactory.getLogger(ModuleContext.class);

    /** 文本文件UTF-8编码 */
    public static final String CHARACTER = "UTF-8";



    /** 线程安全集合 */
    private Map<String, Module> modules = new ConcurrentHashMap<>();


    /**  */
    private Map<String, String> config;

    /**  */
    private Configuration configuration;

    /** 安装监听器 */
    private List<InstallListenter> listenters = new ArrayList<>(3);


    /** 绑定当前的模块 */
    public static final ThreadLocal<Module> moduleThreadLocal = new ThreadLocal<>();

    /**
     * 构造
     *
     * @param config
     */
    public ModuleContext(Map<String, String> config) {
        this.configuration = new Configuration(config);


    }


    /**
     * 安装模块
     *
     * @param moduleFilePath 模块路径
     * @return
     * @throws PackageJsonNotFoundException
     * @throws GroovyActivatorLoadException
     */
    public Module install(String moduleFilePath) {
        File moduleFile = new File(moduleFilePath);
        if (!moduleFile.exists()) {
            logger.warn("modulefilepath[{}] not exists!", moduleFilePath);
            return null;
        }
        try {
            return install(moduleFile);
        } catch (Exception e) {
            logger.error("", e);
            return null;
        }
    }

    /**
     * 安装模块
     *
     * @param moduleFile 模块文件
     *
     * @return Module
     *
     * @throws IOException 异常
     */
    public Module install(File moduleFile) throws IOException {
        // 校验模块完整性
        if (!moduleFile.isDirectory()) {
            throw new ModuleErrorException();
        }

        // 读取 packageJson
        String packageJson = moduleFile.getAbsolutePath() + File.separator + "package.json";
        logger.info("loading {}", packageJson);

        String json = "{\"error\":\"invalid config info\"}";
        try {
            File filePackageJson = new File(packageJson);
            json = FileUtils.readFileToString(filePackageJson, CHARACTER);
        } catch (IOException e) {
            throw new PackageJsonNotFoundException();
        }

        JSONObject config = JSON.parseObject(json);
        if (config.containsKey("error")){
            throw new PackageJsonErrorException();
        }


        // 判断上级路径是否是模块目录
        File parentFile  = moduleFile.getParentFile();
        File modulesFile = new File(configuration.getAutoDeployDir());

        // 设置模块的目录名称
        config.put(Configuration.CONFIG_DIRECTORY, modulesFile.getName());

        if (modulesFile.equals(parentFile)) { // 若路径相同
            logger.debug("install path is modules path!");
        } else { // 路径不同

            // 拷贝文件夹到模块目录
            String moduleId = (String) config.get("id");

            File toFile = modulesFile;
            try {
                logger.info("file copy....");
                FileTools.copyFiles(moduleFile, toFile);
                String toFile2 = toFile.getPath() + File.separator + moduleFile.getName();
                File file2 = new File(toFile2);
                File file3 = new File(toFile.getPath() + File.separator + moduleId);
                file2.renameTo(file3);
                moduleFile = file3;
            } catch (IOException e) {
                throw new ModuleMoveException(e);
            }
        }


        // 加载 ActivatorFile
        String activatorFile = configuration.getActivatorFile().replaceFirst(".groovy", "");

        String modulePath = moduleFile.getAbsolutePath() + File.separator;



//        ClassPool cpool = ClassPool.getDefault();


        ScriptClassLoader loader = new ScriptClassLoader(modulePath);


        SupperModule activator;
        try {

            logger.debug("start groovy engine...");
//				GroovyScriptEngine gse = new GroovyScriptEngine(src, this.getClass().getClassLoader());

            logger.debug("start load groovy[{}] script...", activatorFile);
            Class clzz = loader.parseClass(activatorFile);
            logger.debug("{}", clzz.getName());

            activator = (SupperModule) clzz.newInstance();


            activator.setUtil(new GroovyScriptUtil(config, loader));
            activator.setPath(modulePath);

        } catch (Exception e) {
            throw new GroovyActivatorLoadException(e);
        }

        logger.info("build module complete...");

        Module module = new Module(activator, config, this);



        // 持久化
        modules.put(moduleFile.getName(), module);

        // 监听器调用
        Iterator<InstallListenter> it = listenters.iterator();
        while (it.hasNext()) {
            InstallListenter lis = it.next();
            lis.install(module);
        }
        return module;

    }


    /**
     *  卸载
     * @param id id
     */
    public void uninstall(String id) {
        if (modules.containsKey(id)) {
            try {
                Module module = this.modules.get(id);
                if (module.getStatus() == Module.STATUS_RUNING) {
                    module.stop();
                }
                this.modules.remove(id);
                String directoryName = module.getDirectory();



                // 本地文件删除
                String moduleDir = configuration.getAutoDeployDir() + File.separator + directoryName;

                FileTools.deleteDirectory(new File(moduleDir));
                Iterator<InstallListenter> it = listenters.iterator();
                while (it.hasNext()) {
                    InstallListenter lis = it.next();
                    lis.uninstall(module);
                }


            } catch (StopModuleActivatorException e) {
                e.printStackTrace();
            }

        }


    }

    /**
     *
     * @return
     */
    public Map<String, Module> getModules() {
        return modules;
    }


    /**
     *
     * @return
     */
    public List<Map<String, Object>> getList() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Set<String> sets = modules.keySet();
        for (String uuid : sets) {
            Module m = modules.get(uuid);
            list.add(m.getConfig());
        }
        return list;
    }


    /**
     * 获取当前的模块
     * @return
     */
    public Module getModule() {
        return moduleThreadLocal.get();
    }


    /**
     * 添加安装监听器
     * @param listener 监听器
     */
    public void addInstallListener(InstallListenter listener){
        this.listenters.add(listener);
    }
}
