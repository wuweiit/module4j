package com.wuweibi.module4j;

import com.wuweibi.module4j.exception.StartModuleActivatorException;
import com.wuweibi.module4j.exception.StopModuleActivatorException;
import com.wuweibi.module4j.module.ModuleContext;


/**
 * 激活器 模块接口
 * 用于激活一个模块和停止一个模块功能。
 *
 *
 * @author marker
 * @version 1.0
 */
public interface ModuleActivator {

 
	/**
	 * 启用
	 * @param context
	 * @throws Exception
	 */
	public void start(ModuleContext context) throws StartModuleActivatorException;
	
	
	/**
	 * 停止
	 * @param context
	 * @throws Exception
	 */
	public void stop(ModuleContext context) throws StopModuleActivatorException;
}
