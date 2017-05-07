import com.wuweibi.module4j.ModuleFramework;
import com.wuweibi.module4j.config.Configuration;
import com.wuweibi.module4j.module.ModuleContext;
import org.junit.Test;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * 一项简单的测试而已/
 * @author marker
 *
 * Created by ROOT on 2016/10/21.
 */
public class ModuleFrameworkTest {



    public static void main(String[] args){

        String moduleDir =  "/Volumes/DATA/works/idea15/module4j/modules";// 模块目录

        // 缓存目录
        String cacheDir = moduleDir + File.separator + "cache";// 模块目录

        Map<String,String> configMap = new HashMap<String,String>();
        // 自动部署目录配置
        configMap.put(Configuration.DIR_MODULES, moduleDir);
        // 缓存目录
        configMap.put(Configuration.DIR_CACHE, cacheDir);
        // 日志级别
        configMap.put(Configuration.LOG_LEVEL, "1");

        try {
            ModuleFramework moduleFramework = new ModuleFramework(configMap);
            ModuleContext context = moduleFramework.getModuleContext();
            moduleFramework.start();

            // 做操作
           // context.install("D:\\modules\\chanel\\");



            // 停止服务
            moduleFramework.stop();

        } catch (Exception e) {
           e.printStackTrace();
        }


    }




}
