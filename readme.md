### Java 动态模块化框架

OSGI相信大家比较熟悉，早有耳闻。但是OSGI是一个复杂的工程，学习曲线很高。本人曾经也被坑过最后放弃了OSGI。
目前我所知道的Eclipse 开发工具就是基于OSGI的，更新zip包。那么我为什么要开发这套模块化框架。

是面对动态更新的时候，Java本身不像PHP那样的灵活动态性。


### 更新日志

2016-11-02 使用字节码技术在主要脚本中可以使用require(String)函数加载其他groovy中的类。


### 实现思路

Java是编译语言，但在一些技术大牛的努力之下实现了与Java无缝集成的脚本语言，例如：Scala、Groovy。并且这些脚本语言应用非常广。Groovy被应用于Gradle构建工具，还衍生出Grails(groovy on rails) 框架，Scala也是被应用于大数据处理框架Spark等项目。

为什么要使用脚本语言？ 因为简单，函数式编程。


在OSGI规范的影响下，Java模块化编程变得”简单“，但是OSGI比较复杂，很多配置让我痛苦（在开发MRCMS插件的时候）。


那么Module4j就这样诞生了，我们要使用OSGI的思路结合Groovy 脚本的动态性，再加上ASM字节码技术，nodejs的模块规范，能实现一个功能强大的插件平台。


### 目标

module4j只做模块化框架，不会涉及复杂业务。


groovy 模块实现案例
```
import com.wuweibi.module4j.ModuleUtils
import com.wuweibi.module4j.ModuleActivator;
import com.wuweibi.module4j.module.ModuleContext


/**
 * 栏目模块
 *
 * @author marker
 */
public class ModuleActivatorImpl implements ModuleActivator {



	/**
	 * 启用
	 */
	public void start(ModuleContext context) throws Exception {
        println "module start"
	}



	/**
	 * 停用
	 */
	public void stop(ModuleContext context) throws Exception {
        println "module stop"
	}
}
```



### 长远规划

实现一个模块生态圈，能需要什么模块能像nodejs一样从云端下载模块。



### 成功案例

MRCMS，该项目基于Java开发，采用不使用JSP渲染视图思路设计，在动态插件动态模块方面使用了module4j。