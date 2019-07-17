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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import model.AST.AliasAnalysis;
import structures.graphRep.SetEdges;

class AliasDiagramTests2 {
	AliasAnalysis v;
	String classAnalyse;
	
	@BeforeEach
	void InitClass() {
		System.out.println("Init Alias Analysis");
		try {
			String sourcePath = "";
			String[] unitName = new String[] {};
			String[] classpath = new String[] {};
			if (System.getProperty("os.name").contains("Windows")) {
				sourcePath = "D:\\OneDrive\\Documents\\work\\aliasingJava\\aliasing-java\\AliasTestProject\\src\\Basics\\";
				unitName = new String[]{"QualifiedCall.java", "T.java", "Basic.java"};
				classpath = new String[]{"C:\\Program Files\\Java\\jre1.8.0_181\\lib\\rt.jar"};
			}else if (System.getProperty("os.name").contains("Mac")) {
				sourcePath = "/Users/victor/git/aliasing-java/AliasTestProject/src/Basics/";
				unitName = new String[]{"QualifiedCall.java", "T.java", "Basic.java"};
				classpath = new String[]{"/Library/Java/JavaVirtualMachines/jdk1.8.0_151.jdk/Contents/Home/jre/librt.jar"};
			}else if (System.getProperty("os.name").contains("Linux")) {
				sourcePath = "/home/varivera/Desktop/VR/work/research/aliasing-java/AliasTestProject/src/Basics/";
				unitName = new String[]{"QualifiedCall.java", "T.java", "Basic.java"};
				classpath = new String[]{"/Library/Java/JavaVirtualMachines/jdk1.8.0_151.jdk/Contents/Home/jre/librt.jar"};
			}
			
			classAnalyse = "QualifiedCall";

			v = new AliasAnalysis (sourcePath, unitName, classpath, classAnalyse);
			
			
		} catch (FileNotFoundException e) {
			fail (e.getMessage());
		} catch (IOException e) {
			fail (e.getMessage());
		}
	}
	
	@Test
	void test1() {
		assertNotNull (v);
		v.start(classAnalyse, "q1", 0, null, null, null);
		SetEdges expectedValue = new SetEdges ("[(10, b, 17), (0, t, 10), (10, a, 17), (0, v, 10)]");
		assertTrue (expectedValue.equals(v.toSetEdges()));
		
		System.out.println("1>> "+ v.toSetEdges());
		System.out.println("2>> "+ expectedValue);
	}
	
	@Test
	void test2() {
		assertNotNull (v);
		v.start(classAnalyse, "q2", 0, null, null, null);
		SetEdges expectedValue = new SetEdges ("[(0, v, 6), (0, t, 5), (5, a, 6)]");
		assertTrue (expectedValue.equals(v.toSetEdges()));
	}
	
	@Test
	void test3() {
		assertNotNull (v);
		v.start(classAnalyse, "q3", 0, null, null, null);
		SetEdges expectedValue = new SetEdges ("[(0, v, 11), (0, t, 6), (6, a, 11)]");
		assertTrue (expectedValue.equals(v.toSetEdges()));
	}
	
	@Test
	void test4() {
		assertNotNull (v);
		v.start(classAnalyse, "q4", 0, null, null, null);
		SetEdges expectedValue = new SetEdges ("[(0, v2, 8), (0, t, 8), (0, v, 11), (8, a, 11)]");
		assertTrue (expectedValue.equals(v.toSetEdges()));
	}
	
	@Test
	void test5() {
		assertNotNull (v);
		v.start(classAnalyse, "q5", 0, null, null, null);
		SetEdges expectedValue = new SetEdges ("[(8, a, 6), (0, v2, 8), (0, t, 8), (0, v, 6)]");
		assertTrue (expectedValue.equals(v.toSetEdges()));
	}
	
	@Test
	void test6() {
		assertNotNull (v);
		v.start(classAnalyse, "q6", 0, null, null, null);
		SetEdges expectedValue = new SetEdges ("[(7, b, 8), (6, a, 7), (0, t, 8), (0, v, 6)]");
		assertTrue (expectedValue.equals(v.toSetEdges()));
	}
	
	@Test
	void test7() {
		assertNotNull (v);
		v.start(classAnalyse, "q7", 0, null, null, null);
		SetEdges expectedValue = new SetEdges ("[(6, b, 8), (5, a, 6), (0, t, 8), (0, v, 5)]");
		assertTrue (expectedValue.equals(v.toSetEdges()));
	}
	
	@Test
	void test8() {
		assertNotNull (v);
		v.start(classAnalyse, "q8", 0, null, null, null);
		SetEdges expectedValue = new SetEdges ("[(0, v, 5), (5, a, 11), (0, t, 11)]");
		assertTrue (expectedValue.equals(v.toSetEdges()));
	}
	
	@Test
	void test9() {
		assertNotNull (v);
		v.start(classAnalyse, "q9", 0, null, null, null);
		SetEdges expectedValue = new SetEdges ("[(0, v2, 11), (10, a, 11), (0, t, 10)]");
		assertTrue (expectedValue.equals(v.toSetEdges()));
	}
	
	@Test
	void test10() {
		assertNotNull (v);
		v.start(classAnalyse, "q10", 0, null, null, null);
		SetEdges expectedValue = new SetEdges ("[(0, v2, 11), (9, a, 11), (0, t, 9)]");
		assertTrue (expectedValue.equals(v.toSetEdges()));
	}

	@Test
	void test11() {
		assertNotNull (v);
		v.start(classAnalyse, "q11", 0, null, null, null);
		SetEdges expectedValue = new SetEdges ("[(0, v2, 16), (14, a, 16), (9, a, 14), (0, t, 9)]");
		assertTrue (expectedValue.equals(v.toSetEdges()));
	}
	
	@Test
	void test12() {
		assertNotNull (v);
		v.start(classAnalyse, "q12", 0, null, null, null);
		SetEdges expectedValue = new SetEdges ("[(0, v, 12), (11, a, 12), (6, a, 11), (0, t, 6)]");
		assertTrue (expectedValue.equals(v.toSetEdges()));
	}
	
	@Test
	void test13() {
		assertNotNull (v);
		v.start(classAnalyse, "q13", 0, null, null, null);
		SetEdges expectedValue = new SetEdges ("[(0, t, 6), (0, v, 5), (5, a, 7), (6, a, 7)]");
		assertTrue (expectedValue.equals(v.toSetEdges()));
	}
	

	@Test
	void test14() {
		assertNotNull (v);
		v.start(classAnalyse, "q14", 0, null, null, null);
		SetEdges expectedValue = new SetEdges ("[(0, v, 5)]");
		assertTrue (expectedValue.equals(v.toSetEdges()));
	}
	
	@Test
	void test15() {
		assertNotNull (v);
		v.start(classAnalyse, "q15", 0, null, null, null);
		SetEdges expectedValue = new SetEdges ("[(0, t, 11), (11, a, 12), (0, v, 12)]");
		assertTrue (expectedValue.equals(v.toSetEdges()));
	}
	
	@Test
	void test16() {
		assertNotNull (v);
		v.start(classAnalyse, "qThis16", 0, null, null, null);
		SetEdges expectedValue = new SetEdges ("[(0, v, 6), (0, t, 6)]");
		assertTrue (expectedValue.equals(v.toSetEdges()));
	}
	
	@Test
	void test17() {
		assertNotNull (v);
		v.start(classAnalyse, "qThis17", 0, null, null, null);
		SetEdges expectedValue = new SetEdges ("[(0, v, 6), (0, t, 6)]");
		assertTrue (expectedValue.equals(v.toSetEdges()));
	}
	
	@Test
	void test18() {
		assertNotNull (v);
		v.start(classAnalyse, "qThis18", 0, null, null, null);
		SetEdges expectedValue = new SetEdges ("[(0, v, 6), (0, v2, 6)]");
		assertTrue (expectedValue.equals(v.toSetEdges()));
	}
	
}
