import com.wuweibi.module4j.ModuleActivator;import com.wuweibi.module4j.SupperModuleimport com.wuweibi.module4j.module.Module;import com.wuweibi.module4j.module.ModuleContext/** * 栏目模块  *  * @author marker */public class ModuleActivatorImpl extends SupperModule {		 	/**	 * 启用 模块	 */	public void start(ModuleContext context, Module module) {		Class clzz = require("a");        GroovyObject obj = clzz.newInstance();        Object str = obj.invokeMethod("getInfo",null);        println(str);        println(path);        println "module start"	} 			/**	 * 停用	 */	public void stop(ModuleContext context)  {        println "module stop"	}}