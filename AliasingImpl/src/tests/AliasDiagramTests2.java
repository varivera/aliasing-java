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

import org.junit.jupiter.api.AfterEach;
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
			String[] unitName = new String[]{"QualifiedCall.java", "T.java", "Basic.java", "AAPaper.java"};
			String[] classpath = new String[] {};
			if (System.getProperty("os.name").contains("Windows")) {
				sourcePath = "D:\\OneDrive\\Documents\\work\\aliasingJava\\aliasing-java\\AliasTestProject\\src\\Basics\\";
				classpath = new String[]{"C:\\Program Files\\Java\\jre1.8.0_181\\lib\\rt.jar"};
			}else if (System.getProperty("os.name").contains("Mac")) {
				sourcePath = "/Users/victor/git/aliasing-java2/AliasTestProject/src/Basics/";
				classpath = new String[]{"/Library/Java/JavaVirtualMachines/jdk1.8.0_151.jdk/Contents/Home/jre/librt.jar"};
			}else if (System.getProperty("os.name").contains("Linux")) {
				sourcePath = "/home/varivera/Desktop/VR/work/research/aliasing-java/AliasTestProject/src/Basics/";
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
	
	@AfterEach
	public void sanityCehckPredecessor() {
		assertTrue(v.predCheck());
	}
	
	@AfterEach
	public void done() {
		System.out.println("\n====================DONE===============================");
	}
	
	@Test
	void test1() {
		assertNotNull (v);
		v.start(classAnalyse, "q1", 0, null, null, null);
		SetEdges expectedValue = new SetEdges ("[(10, b<0>, 17), (0, t<0>, 10), (10, a<0>, 17), (0, v<0>, 10)]");
		System.out.println(expectedValue + " : Expected value");
		System.out.println(v.toSetEdges() + " : Obtained value");
		assertTrue (expectedValue.equals(v.toSetEdges()));
	}
	
	@Test
	void test2() {
		assertNotNull (v);
		v.start(classAnalyse, "q2", 0, null, null, null);
		SetEdges expectedValue = new SetEdges ("[(0, v<0>, 6), (0, t<0>, 5), (5, a<0>, 6)]");
		System.out.println(expectedValue + " : Expected value");
		System.out.println(v.toSetEdges() + " : Obtained value");
		assertTrue (expectedValue.equals(v.toSetEdges()));
	}
	
	@Test
	void test3() {
		assertNotNull (v);
		v.start(classAnalyse, "q3", 0, null, null, null);
		SetEdges expectedValue = new SetEdges ("[(0, v<0>, 11), (0, t<0>, 6), (6, a<0>, 11)]");
		System.out.println(expectedValue + " : Expected value");
		System.out.println(v.toSetEdges() + " : Obtained value");
		assertTrue (expectedValue.equals(v.toSetEdges()));
	}
	
	@Test
	void test4() {
		assertNotNull (v);
		v.start(classAnalyse, "q4", 0, null, null, null);
		SetEdges expectedValue = new SetEdges ("[(0, v2<0>, 8), (0, t<0>, 8), (0, v<0>, 11), (8, a<0>, 11)]");
		System.out.println(expectedValue + " : Expected value");
		System.out.println(v.toSetEdges() + " : Obtained value");
		assertTrue (expectedValue.equals(v.toSetEdges()));
	}
	
	@Test
	void test5() {
		assertNotNull (v);
		v.start(classAnalyse, "q5", 0, null, null, null);
		SetEdges expectedValue = new SetEdges ("[(8, a<0>, 6), (0, v2<0>, 8), (0, t<0>, 8), (0, v<0>, 6)]");
		System.out.println(expectedValue + " : Expected value");
		System.out.println(v.toSetEdges() + " : Obtained value");
		assertTrue (expectedValue.equals(v.toSetEdges()));
	}
	
	@Test
	void test6() {
		assertNotNull (v);
		v.start(classAnalyse, "q6", 0, null, null, null);
		SetEdges expectedValue = new SetEdges ("[(7, b<0>, 8), (6, a<0>, 7), (0, t<0>, 8), (0, v<0>, 6)]");
		System.out.println(expectedValue + " : Expected value");
		System.out.println(v.toSetEdges() + " : Obtained value");
		assertTrue (expectedValue.equals(v.toSetEdges()));
	}
	
	@Test
	void test7() {
		assertNotNull (v);
		v.start(classAnalyse, "q7", 0, null, null, null);
		SetEdges expectedValue = new SetEdges ("[(6, b<0>, 8), (5, a<0>, 6), (0, t<0>, 8), (0, v<0>, 5)]");
		System.out.println(expectedValue + " : Expected value");
		System.out.println(v.toSetEdges() + " : Obtained value");
		assertTrue (expectedValue.equals(v.toSetEdges()));
	}
	
	@Test
	void test8() {
		assertNotNull (v);
		v.start(classAnalyse, "q8", 0, null, null, null);
		SetEdges expectedValue = new SetEdges ("[(0, v<0>, 5), (5, a<0>, 11), (0, t<0>, 11)]");
		System.out.println(expectedValue + " : Expected value");
		System.out.println(v.toSetEdges() + " : Obtained value");
		assertTrue (expectedValue.equals(v.toSetEdges()));
	}
	
	@Test
	void test9() {
		assertNotNull (v);
		v.start(classAnalyse, "q9", 0, null, null, null);
		SetEdges expectedValue = new SetEdges ("[(0, v2<0>, 11), (10, a<0>, 11), (0, t<0>, 10)]");
		System.out.println(expectedValue + " : Expected value");
		System.out.println(v.toSetEdges() + " : Obtained value");
		assertTrue (expectedValue.equals(v.toSetEdges()));
	}
	
	@Test
	void test10() {
		assertNotNull (v);
		v.start(classAnalyse, "q10", 0, null, null, null);
		SetEdges expectedValue = new SetEdges ("[(0, v2<0>, 11), (9, a<0>, 11), (0, t<0>, 9)]");
		System.out.println(expectedValue + " : Expected value");
		System.out.println(v.toSetEdges() + " : Obtained value");
		assertTrue (expectedValue.equals(v.toSetEdges()));
	}

	@Test
	void test11() {
		assertNotNull (v);
		v.start(classAnalyse, "q11", 0, null, null, null);
		SetEdges expectedValue = new SetEdges ("[(0, v2<0>, 16), (14, a<0>, 16), (9, a<0>, 14), (0, t<0>, 9)]");
		System.out.println(expectedValue + " : Expected value");
		System.out.println(v.toSetEdges() + " : Obtained value");
		assertTrue (expectedValue.equals(v.toSetEdges()));
	}
	
	@Test
	void test12() {
		assertNotNull (v);
		v.start(classAnalyse, "q12", 0, null, null, null);
		SetEdges expectedValue = new SetEdges ("[(0, v<0>, 12), (11, a<0>, 12), (6, a<0>, 11), (0, t<0>, 6)]");
		System.out.println(expectedValue + " : Expected value");
		System.out.println(v.toSetEdges() + " : Obtained value");
		assertTrue (expectedValue.equals(v.toSetEdges()));
	}
	
	@Test
	void test13() {
		assertNotNull (v);
		v.start(classAnalyse, "q13", 0, null, null, null);
		SetEdges expectedValue = new SetEdges ("[(0, t<0>, 6), (0, v<0>, 5), (5, a<0>, 7), (6, a<0>, 7)]");
		System.out.println(expectedValue + " : Expected value");
		System.out.println(v.toSetEdges() + " : Obtained value");
		assertTrue (expectedValue.equals(v.toSetEdges()));
	}
	

	@Test
	void test14() {
		assertNotNull (v);
		v.start(classAnalyse, "q14", 0, null, null, null);
		SetEdges expectedValue = new SetEdges ("[(0, v<0>, 5)]");
		System.out.println(expectedValue + " : Expected value");
		System.out.println(v.toSetEdges() + " : Obtained value");
		assertTrue (expectedValue.equals(v.toSetEdges()));
	}
	
	@Test
	void test15() {
		assertNotNull (v);
		v.start(classAnalyse, "q15", 0, null, null, null);
		SetEdges expectedValue = new SetEdges ("[(0, t<0>, 11), (11, a<0>, 12), (0, v<0>, 12)]");
		System.out.println(expectedValue + " : Expected value");
		System.out.println(v.toSetEdges() + " : Obtained value");
		assertTrue (expectedValue.equals(v.toSetEdges()));
	}
	
	@Test
	void test16() {
		assertNotNull (v);
		v.start(classAnalyse, "qThis16", 0, null, null, null);
		SetEdges expectedValue = new SetEdges ("[(0, v<0>, 6), (0, t<0>, 6)]");
		System.out.println(expectedValue + " : Expected value");
		System.out.println(v.toSetEdges() + " : Obtained value");
		assertTrue (expectedValue.equals(v.toSetEdges()));
	}
	
	@Test
	void test17() {
		assertNotNull (v);
		v.start(classAnalyse, "qThis17", 0, null, null, null);
		SetEdges expectedValue = new SetEdges ("[(0, v<0>, 6), (0, t<0>, 6)]");
		System.out.println(expectedValue + " : Expected value");
		System.out.println(v.toSetEdges() + " : Obtained value");
		assertTrue (expectedValue.equals(v.toSetEdges()));
	}
	
	@Test
	void test18() {
		assertNotNull (v);
		v.start(classAnalyse, "qThis18", 0, null, null, null);
		SetEdges expectedValue = new SetEdges ("[(0, v<0>, 6), (0, v2<0>, 6)]");
		System.out.println(expectedValue + " : Expected value");
		System.out.println(v.toSetEdges() + " : Obtained value");
		assertTrue (expectedValue.equals(v.toSetEdges()));
	}
	
}
