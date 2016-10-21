package com.wuweibi.module4j.groovy;

import com.wuweibi.utils.FileTools;
import groovy.lang.GroovyClassLoader;
import org.codehaus.groovy.control.CompilationFailedException;

import java.io.File;
import java.io.IOException;

public class ScriptClassLoader {

	public GroovyClassLoader loader ;
	
	private String src;
	
	public ScriptClassLoader(String src) {
		this.src = src;
		 ClassLoader parent = ScriptClassLoader.class.getClassLoader(); 
		 loader = new GroovyClassLoader(parent);
		 loader.addClasspath(src); 
	}
	
	
	/**
	 * Groovy脚本转换Class
	 * @param scriptName
	 * @return
	 * @throws CompilationFailedException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public Class<?> parseClass(String scriptName) throws CompilationFailedException, IOException, ClassNotFoundException{
		File filePath = new File(src + File.separator + scriptName + ".groovy");
		String text = FileTools.getFileContet(filePath, FileTools.FILE_CHARACTER_UTF8);
		return loader.parseClass(text); 
	}
	
	
}
