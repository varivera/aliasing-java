package tests;

import static org.junit.Assert.assertTrue;

/**
 * 
 * Testing the Alias Diagrams
 * 
 * @author Victor Rivera (victor.rivera@anu.edu.au)
 *
 */

import static org.junit.jupiter.api.Assertions.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import model.AST.AliasAnalysis;
import structures.graphRep.SetEdges;
import structures.helpers.Helpers;

class AliasDiagramTests {
	AliasAnalysis v;
	
	@SuppressWarnings({ "deprecation", "unchecked" })
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
			}else if (System.getProperty("os.name").contains("Linux")) {
				sourcePath = "/home/varivera/Desktop/VR/work/research/aliasing-java/AliasTestProject/src/Basics/";
				unitName = "Basic.java";
				classpath = new String[]{"/Library/Java/JavaVirtualMachines/jdk1.8.0_151.jdk/Contents/Home/jre/librt.jar"};
			}
			

			//System.out.println(t.getFileContent(source));
			ASTParser parser = ASTParser.newParser(AST.JLS4);
			char[] fileContent;
			fileContent = Helpers.getFileContent(sourcePath+unitName).toCharArray();
			
			parser.setKind(ASTParser.K_COMPILATION_UNIT);
			parser.setResolveBindings(true);
			
			@SuppressWarnings("rawtypes")
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
		assertNotNull (v);
		v.start("Basic", "assg1", 0);
		SetEdges expectedValue = new SetEdges ("[(0, w, 6), (0, v, 6)]");
		assertTrue (expectedValue.equals(v.toSetEdges()));
	}

	@Test
	void test2() {
		assertNotNull (v);
		v.start("Basic", "assg2", 0);
		SetEdges expectedValue = new SetEdges ("[(0, w, 6), (0, v, 8), (0, z, 8)]");
		assertTrue (expectedValue.equals(v.toSetEdges()));
	}
	
	@Test
	void test3() {
		assertNotNull (v);
		v.start("Basic", "assg3", 0);
		SetEdges expectedValue = new SetEdges ("[(0, w, 6), (0, v, 6), (0, z, 6)]");
		assertTrue (expectedValue.equals(v.toSetEdges()));
	}
	
	@Test
	void test4() {
		assertNotNull (v);
		v.start("Basic", "localArg1", 0);
		SetEdges expectedValue = new SetEdges ("[(0, w, 8), (0, v, 8)]");
		assertTrue (expectedValue.equals(v.toSetEdges()));
	}
	
	@Test
	void test5() {
		assertNotNull (v);
		v.start("Basic", "creation", 0);
		SetEdges expectedValue = new SetEdges ("[(0, v, 8)]");
		assertTrue (expectedValue.equals(v.toSetEdges()));
	}
	
	@Test
	void test6() {
		assertNotNull (v);
		v.start("Basic", "creation2", 0);
		SetEdges expectedValue = new SetEdges ("[(0, w, 6)]");
		assertTrue (expectedValue.equals(v.toSetEdges()));
	}

	@Test
	void test7() {
		assertNotNull (v);
		v.start("Basic", "unq_call", 0);
		SetEdges expectedValue = new SetEdges ("[(0, v, 10), (0, w, 10)]");
		assertTrue (expectedValue.equals(v.toSetEdges()));
	}
	
	@Test
	void test8() {
		assertNotNull (v);
		v.start("Basic", "unq_call_arg", 0);
		SetEdges expectedValue = new SetEdges ("[(0, v, 11), (0, w, 11)]");
		assertTrue (expectedValue.equals(v.toSetEdges()));
	}

	@Test
	void test9() {
		assertNotNull (v);
		v.start("Basic", "nestedCall", 0);
		SetEdges expectedValue = new SetEdges ("[(0, z, 10), (0, v, 10)]");
		assertTrue (expectedValue.equals(v.toSetEdges()));
	}	
	
	@Test
	void test10() {
		assertNotNull (v);
		v.start("Basic", "localArg2", 0);
		SetEdges expectedValue = new SetEdges ("[(0, v, 8), (0, w, 8)]");
		assertTrue (expectedValue.equals(v.toSetEdges()));
	}
	
	@Test
	void test11() {
		assertNotNull (v);
		v.start("Basic", "args", 0);
		SetEdges expectedValue = new SetEdges ("[(0, v, 5)]");
		assertTrue (expectedValue.equals(v.toSetEdges()));
	}
	
	@Test
	void test12() {
		assertNotNull (v);
		v.start("Basic", "t3", 0);
		SetEdges expectedValue = new SetEdges ("[(0, v, 5)]");
		assertTrue (expectedValue.equals(v.toSetEdges()));
	}
	
	@Test
	void test13() {
		assertNotNull (v);
		v.start("Basic", "t2", 0);
		SetEdges expectedValue = new SetEdges ("[(0, v, 5)]");
		assertTrue (expectedValue.equals(v.toSetEdges()));
	}
	
	@Test
	void test14() {
		assertNotNull (v);
		v.start("Basic", "return1", 0);
		SetEdges expectedValue = new SetEdges ("[]");
		assertTrue (expectedValue.equals(v.toSetEdges()));
	}
	
	@Test
	void test15() {
		assertNotNull (v);
		v.start("Basic", "return2", 0);
		SetEdges expectedValue = new SetEdges ("[]");
		assertTrue (expectedValue.equals(v.toSetEdges()));
	}
	
	@Test
	void test16() {
		assertNotNull (v);
		v.start("Basic", "return3", 0);
		SetEdges expectedValue = new SetEdges ("[]");
		assertTrue (expectedValue.equals(v.toSetEdges()));
	}
	
	@Test
	void test17() {
		assertNotNull (v);
		v.start("Basic", "return4", 0);
		SetEdges expectedValue = new SetEdges ("[]");
		assertTrue (expectedValue.equals(v.toSetEdges()));
	}
	
	@Test
	void test18() {
		assertNotNull (v);
		v.start("Basic", "return5", 0);
		SetEdges expectedValue = new SetEdges ("[(0, v, 5)]");
		assertTrue (expectedValue.equals(v.toSetEdges()));
	}
	
	@Test
	void test19() {
		assertNotNull (v);
		v.start("Basic", "return6", 0);
		SetEdges expectedValue = new SetEdges ("[(0, v, 9)]");
		assertTrue (expectedValue.equals(v.toSetEdges()));
	}
	
	@Test
	void test20() {
		assertNotNull (v);
		v.start("Basic", "methodInv", 0);
		SetEdges expectedValue = new SetEdges ("[(0, w, 10), (0, v, 10)]");
		assertTrue (expectedValue.equals(v.toSetEdges()));
	}
	
	@Test
	void test21() {
		assertNotNull (v);
		v.start("Basic", "methodInv2", 0);
		SetEdges expectedValue = new SetEdges ("[(0, w, 14), (0, v, 14)]");
		assertTrue (expectedValue.equals(v.toSetEdges()));
	}
	
	@Test
	void test22() {
		assertNotNull (v);
		v.start("Basic", "get_v", 0);
		SetEdges expectedValue = new SetEdges ("[(0, v, 5)]");
		assertTrue (expectedValue.equals(v.toSetEdges()));
	}
	
	@Test
	void test23() {
		assertNotNull (v);
		v.start("Basic", "func", 0);
		SetEdges expectedValue = new SetEdges ("[(0, w, 10), (0, v, 10)]");
		assertTrue (expectedValue.equals(v.toSetEdges()));
	}
}
