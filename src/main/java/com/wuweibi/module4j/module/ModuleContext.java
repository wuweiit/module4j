package com.wuweibi.module4j.module;


import com.alibaba.fastjson.JSON;
import com.wuweibi.module4j.ModuleActivator;
import com.wuweibi.module4j.ModuleFramework;
import com.wuweibi.module4j.config.Configuration;
import com.wuweibi.module4j.exception.GroovyActivatorLoadException;
import com.wuweibi.module4j.exception.PackageJsonNotFoundException;
import com.wuweibi.module4j.exception.StopModuleActivatorException;
import com.wuweibi.module4j.groovy.GroovyScriptUtil;
import com.wuweibi.module4j.groovy.ScriptClassLoader;
import com.wuweibi.utils.FileTools;
import groovy.lang.GroovyObject;
import groovy.util.GroovyScriptEngine;
import javassist.*;
import org.codehaus.groovy.tools.GroovyClass;
import org.objectweb.asm.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;



/**
 * 模块容器，用于存取模块
 *
 *
 *
 *
 * @author marker
 * @version 1.0
 */
public class ModuleContext {
	/** 日志记录 */
	private final Logger logger = LoggerFactory.getLogger(ModuleContext.class);

	// 线程安全集合
	private Map<String,Module> modules = new ConcurrentHashMap<String,Module>();


	private Map<String, String> config;

	private Configuration configuration;


	/**
	 * 构造
	 * @param config
	 */
	public ModuleContext(Map<String, String> config) {
		this.configuration = new Configuration(config);



	}


	/**
	 * 安装模块
	 * @param moduleFilePath 模块路径
	 * @return
	 * @throws PackageJsonNotFoundException
	 * @throws GroovyActivatorLoadException
	 */
	public Module install(String moduleFilePath) throws PackageJsonNotFoundException, GroovyActivatorLoadException {
		File moduleFile = new File(moduleFilePath);
		if(!moduleFile.exists()){
			logger.warn("modulefilepath[{}] not exists!", moduleFilePath);
			return null;
		}
		return install(moduleFile);
	}

	/**
	 * 安装模块
	 * @param moduleFile
	 * @return
	 * @throws PackageJsonNotFoundException
	 * @throws GroovyActivatorLoadException
	 */
	public Module install(File moduleFile) throws GroovyActivatorLoadException, PackageJsonNotFoundException {
		// 校验模块完整性




        // 读取 packageJson
        String packageJson = moduleFile.getAbsolutePath() + File.separator + "package.json";
        logger.info("loading {}",packageJson);

        String json = "{\"error\":\"invalid config info\"}";
        try {
            json = FileTools.getFileContet(new File(packageJson), FileTools.FILE_CHARACTER_UTF8);
        } catch (IOException e) {
            throw new PackageJsonNotFoundException();
        }

        @SuppressWarnings("unchecked")
        Map<String,Object> config = (Map<String, Object>) JSON.parse(json);


	    // 判断上级路径是否是模块目录
		File parentFile = moduleFile.getParentFile();

        File modulesFile = new File(configuration.getModulesDeployDir());
        String a = modulesFile.getAbsolutePath();
        String b = parentFile.getAbsolutePath();
		if(a.equals(b)){// 若路径相同

		}else{// 路径不同
            // 拷贝文件夹到模块目录
            String moduleId  = (String)config.get("id");

            File toFile = new File(modulesFile.getAbsolutePath() +File.separator);

            try {
                logger.info("file copy....");
                FileTools.copyFiles(moduleFile, toFile);
                String toFile2 = toFile.getPath()+File.separator+moduleFile.getName();
                File file2 = new File(toFile2);
                File file3 = new File(toFile.getPath()+File.separator+ moduleId);
                file2.renameTo(file3);
                moduleFile =file3;
            } catch (IOException e) {
                logger.error("",e);
            }

        }

		if(moduleFile.isDirectory()){

            String activatorFile = configuration.getActivatorFile().replaceFirst(".groovy","");
			
			String src = moduleFile.getAbsolutePath() + File.separator;
			 
			ScriptClassLoader loader = new ScriptClassLoader(src);
			
			Object obj;
			try {

				logger.info("start groovy engine...");
//				GroovyScriptEngine gse = new GroovyScriptEngine(src, this.getClass().getClassLoader());

				logger.info("start load groovy script...");
				Class clzz =  loader.parseClass(activatorFile);

                ClassPool cpool = ClassPool.getDefault();
                CtClass className = cpool.get(clzz.getName());

                // 添加字段
                CtField field = new CtField(cpool.get(GroovyScriptUtil.class.getName()), "util", className);
                field.setModifiers(Modifier.PUBLIC);
                className.addField(field);

                // 添加方法
                CtMethod method = new CtMethod(cpool.get(Class.class.getName()), "require",
                        new CtClass[] {cpool.get(String.class.getName())} , className);
                method.setModifiers(Modifier.PUBLIC);
                method.setBody("{ return util.loadGroovy($1);}");
                className.addMethod(method);


                Class clazz =  className.toClass();

                obj = clazz.newInstance();

                Field feild = clazz.getDeclaredField("util");
                feild.setAccessible(true);
                feild.set(obj, new GroovyScriptUtil(config,loader));


//				obj.invokeMethod("setUtil", new GroovyScriptUtil(config,loader));// 注入脚本加载工具
			} catch (Exception e) {
			    logger.error("",e);
				throw new GroovyActivatorLoadException();
			}


			logger.info("build module complete...");
			ModuleActivator activator = (ModuleActivator) obj;
			Module module = new Module(activator, config, this);

			// 持久化
			
			
			
			
			modules.put(moduleFile.getName(), module);
			return module;  
		}
		return null;
	}
	
	
	
	/**
	 * 卸载
	 */
	public void uninstall(String uuid){
		if(modules.containsKey(uuid)){
			try {
				Module m = this.modules.get(uuid);
				if(m.getStatus() == Module.STATUS_RUNING){
					m.stop();
				}
				this.modules.remove(uuid);

				
				// 本地文件删除
				String moduleDir =  "modules"+File.separator+uuid;
				 
				FileTools.deleteDirectory(new  File(moduleDir));  
			} catch (StopModuleActivatorException e) {
				e.printStackTrace();
			}
		}
		
	
	}

    public Map<String, Module> getModules() {
        return modules;
    }

    public List<Map<String, Object>>  getList() {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>(); 
		Set<String> sets = modules.keySet(); 
		for(String uuid : sets){
			Module m = modules.get(uuid); 
			list.add(m.getConfig()); 
		} 
		return list;
	}


	
	
	/**
	 * 获取模块对象
	 * @param uuid
	 * @return
	 */
	public Module getModule(String uuid) {
		return this.modules.get(uuid);
	}
	
}
