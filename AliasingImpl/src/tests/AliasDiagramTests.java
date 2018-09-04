package tests;

import static org.junit.Assert.assertTrue;

/**
 * 
 * Testing the Alias Diagrams
 * 
 * @author Victor Rivera
 *
 */

import static org.junit.jupiter.api.Assertions.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import model.AST.AliasAnalysis;
import structures.helpers.Helpers;

class AliasDiagramTests {
	AliasAnalysis v;
	
	@BeforeEach
	void InitClass() {
		System.out.println("Init Alias Analysis");
		try {
			String sourcePath = "";
			String unitName = "";
			String[] classpath = null;
			if (System.getProperty("os.name").contains("Windows")) {
				sourcePath = "D:\\OneDrive\\Documents\\work\\aliasingJava\\aliasing-java\\AliasTestProject\\src\\Basics\\";
				unitName = "Basic.java";
				classpath = new String[]{"C:\\Program Files\\Java\\jre1.8.0_181\\lib\\rt.jar"};
			}else if (System.getProperty("os.name").contains("Mac")) {
				sourcePath = "/Users/victor/git/aliasing-java/AliasTestProject/src/Basics/";
				unitName = "Basic.java";
				classpath = new String[]{"/Library/Java/JavaVirtualMachines/jdk1.8.0_151.jdk/Contents/Home/jre/librt.jar"};
			}
			

			//System.out.println(t.getFileContent(source));
			ASTParser parser = ASTParser.newParser(AST.JLS4);
			char[] fileContent;
			fileContent = Helpers.getFileContent(sourcePath+unitName).toCharArray();
			
			parser.setKind(ASTParser.K_COMPILATION_UNIT);
			parser.setResolveBindings(true);
			
			Map options = JavaCore.getOptions();
			parser.setCompilerOptions(options);
			
			parser.setBindingsRecovery(true);
			
			
			parser.setUnitName(unitName);
			
			///Users/victor/git/aliasing-java/AliasTestProject/src/Basics/Basic.java
			String[] sources = {sourcePath}; 
			
	 
			parser.setEnvironment(classpath, sources, new String[] { "UTF-8"}, true);
			/*parser.setEnvironment( // apply classpath
		                new String[] { "D:\\OneDrive\\Documents\\work\\aliasingJava\\aliasing-java\\AliasingImpl" }, //
		                null, null, true);*/
			parser.setSource(fileContent);
			
			v = new AliasAnalysis (parser);
			
		} catch (FileNotFoundException e) {
			fail (e.getMessage());
		} catch (IOException e) {
			fail (e.getMessage());
		}
	}
	
	@Test
	void test1() {
		System.out.println("test1");
		assertNotNull (v);
		v.start("Basic", "test1", 0);
		//assertTrue ("d", true);
	}

	@Test
	void test3() {
		//fail("Not yet implemented");
	}
}
