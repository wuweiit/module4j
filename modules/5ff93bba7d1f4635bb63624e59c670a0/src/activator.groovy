import com.wuweibi.module4j.ModuleUtilsimport com.wuweibi.module4j.ModuleActivator;import com.wuweibi.module4j.module.ModuleContext;/** * 栏目模块  *  * @author marker */public class ModuleActivatorImpl extends ModuleUtils implements ModuleActivator {		 	/**	 * 启用 	 */	public void start(ModuleContext context) throws Exception {        println "module start"	} 			/**	 * 停用	 */	public void stop(ModuleContext context) throws Exception {        println "module stop"	}}