package com.wuweibi.module4j;



import com.wuweibi.module4j.config.Configuration;
import com.wuweibi.module4j.exception.GroovyActivatorLoadException;
import com.wuweibi.module4j.exception.PackageJsonNotFoundException;
import com.wuweibi.module4j.exception.StartModuleActivatorException;
import com.wuweibi.module4j.exception.StopModuleActivatorException;
import com.wuweibi.module4j.module.Module;
import com.wuweibi.module4j.module.ModuleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Collections;
import java.util.Map;
import java.util.Set;


/**
 * 模块化框架
 *
 * @author marker
 * @version 1.0
 */
public class ModuleFramework {
	
	private final Logger logger = LoggerFactory.getLogger(ModuleFramework.class);
	



    private Map<String, String> config;


    /***
     * 模块上下文对象
     */
    private ModuleContext context;


    private Configuration configuration;


    public ModuleFramework(Map<String, String> config) throws Exception {
		this.config = Collections.synchronizedMap(config);
        this.configuration = new Configuration(config);
        context = new ModuleContext(config);
	} 

	 
	/**
	 * 获取模块上下文对象
	 * @return
	 */
	public ModuleContext getModuleContext() {
		return context;
	}


    /**
     * 启动
     */
    public void start() throws Exception {
        String modulesDir =  configuration.getModulesDeployDir() ;
//		String modulesCache = config.get(DIR_CACHE);

        if(null == modulesDir){
            throw new Exception("配置信息缺失：" + configuration.getModulesDeployDir());
        }


        // 扫描自动部署模块
        File file = new File(modulesDir);
        logger.info("start scan [{}] modules... ",modulesDir);
        for(File f : file.listFiles()){
            String uuid = f.getName();//
            logger.info("loading {} ", uuid);
            File moduleFile = new File(modulesDir + File.separator + uuid);
            try {
                Module module = context.install(moduleFile);
                module.start();// 启动模块
            }catch(PackageJsonNotFoundException e){
                logger.error("module package.json file not found!", e);
            } catch (GroovyActivatorLoadException e) {
                logger.error("load module [ "+moduleFile.getName()+" ] Activator faild!",e);
            } catch (StartModuleActivatorException e) {
                logger.error("start module [ "+moduleFile.getName()+" ] faild!",e);
            } catch (Exception e) {
                logger.error("",e);
            }

        }
    }


    /**
     * 停止服务
     */
    public void stop() throws StopModuleActivatorException {
        logger.info("stop ModuleFramework...");
        Map<String, Module> modules = context.getModules();
        Set<String> sets = modules.keySet();
        for(String uuid : sets){
            logger.info("stop module ({})...", uuid);
            Module m = modules.get(uuid);
            m.stop();
        }
        logger.info("stop ModuleFramework complete");
    }

}
