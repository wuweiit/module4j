package com.wuweibi.module4j.module;


import com.alibaba.fastjson.JSONObject;
import com.wuweibi.module4j.ModuleActivator;
import com.wuweibi.module4j.exception.StartModuleActivatorException;
import com.wuweibi.module4j.exception.StopModuleActivatorException;

import java.io.Serializable;
import java.util.Map;


/**
 * 模块对象
 * @author marker
 * @version 1.0
 */
public class Module implements Serializable {
	private static final long serialVersionUID = 2740932792002272914L;

	
	public static final String CONFIG_UUID = "uuid";
	public static final String CONFIG_STATUS = "status";
	
	
	/** 准备状态 */
	public static final int STATUS_READY = 0;
	
	/** 运行状态 */
	public static final int STATUS_RUNING = 1;
	
	/** 停止状态 */
	public static final int STATUS_STOP = 2;
	
	/** 错误状态 */
	public static final int STATUS_ERROR = 3;





    /**  */
	private ModuleActivator activator;

	/**  */
	private JSONObject config;

    /**  */
	private ModuleContext context;

    /**  */
	private int status = STATUS_READY;
	
	public Module(ModuleContext context){
		this.context = context;
	}
	
	
	
	
	public Module(ModuleActivator activator, JSONObject config,
			ModuleContext context) {
		super();
		this.activator = activator;
		this.config = config;
		this.context = context;
	}


	/**
	 * 启动
	 * @throws StartModuleActivatorException yidsa
	 */
	public void start() throws StartModuleActivatorException {
		synchronized (Module.class) { 
			if (status == STATUS_READY || status == STATUS_STOP) {

				activator.start(context, this);
				status = STATUS_RUNING;
				config.put("status", status);
			}
		}
	}


    /**
     *
     * @throws StopModuleActivatorException
     */
	public void stop() throws StopModuleActivatorException {
		synchronized (Module.class) { 
			if(status == STATUS_RUNING){
				activator.stop(context);	
				status = STATUS_STOP;
				config.put("status", status);
			}
		} 
	}


    /**
     *
     * @return
     */
	public JSONObject getConfig() {
		config.put(Module.CONFIG_STATUS, this.status); 
		return config;
	}
	
	
	/**
	 * 获取模块状态
	 * @return
	 */
	public int getStatus() {
		return status;
	}


    /**
     * 获取模块唯一标记
     * @return
     */
	public String getId() {
	    return this.config.getString("id");
    }
}
