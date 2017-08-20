package com.wuweibi.module4j;/**
 * Created by marker on 2017/8/19.
 */

import com.wuweibi.module4j.groovy.GroovyScriptUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author marker
 *  2017-08-19 下午11:47
 **/
public abstract class SupperModule  implements ModuleActivator {

    /**  */
    private final Logger logger = LoggerFactory.getLogger(ModuleUtils.class);

    /**  */
    private GroovyScriptUtil util;

    /**  */
    private String path;


    /**
     *
     * @param path adsa
     * @return
     */
    public Class require(String path) throws Exception {
        return util.loadGroovy("/src/" + path);
    }


    public GroovyScriptUtil getUtil() {
        return util;
    }

    public void setUtil(GroovyScriptUtil util) {
        this.util = util;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }


}
