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

class AliasDiagramTests3 {
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
			
			classAnalyse = "AAPaper";

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
		v.start(classAnalyse, "assignment", 0, null, null, null);
		SetEdges expectedValue = new SetEdges ("[(0, b<0>, 6), (0, a<0>, 6)]");
		System.out.println(expectedValue + " : Expected value");
		System.out.println(v.toSetEdges() + " : Obtained value");
		assertTrue (expectedValue.equals(v.toSetEdges()));
	}

	@Test
	void test2() {
		assertNotNull (v);
		v.start(classAnalyse, "composition", 0, null, null, null);
		SetEdges expectedValue = new SetEdges ("[(0, b<0>, 6), (0, a<0>, 6), (0, x<0>, 6)]");
		System.out.println(expectedValue + " : Expected value");
		System.out.println(v.toSetEdges() + " : Obtained value");
		assertTrue (expectedValue.equals(v.toSetEdges()));
	}
	
	@Test
	void test3() {
		assertNotNull (v);
		v.start(classAnalyse, "creation", 0, null, null, null);
		SetEdges expectedValue = new SetEdges ("[(0, x<0>, 6)]");
		System.out.println(expectedValue + " : Expected value");
		System.out.println(v.toSetEdges() + " : Obtained value");
		assertTrue (expectedValue.equals(v.toSetEdges()));
	}
	
	@Test
	void test4() {
		assertNotNull (v);
		v.start(classAnalyse, "conditional", 0, null, null, null);
		SetEdges expectedValue = new SetEdges ("[(0, v<0>, 15), (0, w<0>, 15)]");
		System.out.println(expectedValue + " : Expected value");
		System.out.println(v.toSetEdges() + " : Obtained value");
		assertTrue (expectedValue.equals(v.toSetEdges()));
	}
	
	@Test
	void test5() {
		assertNotNull (v);
		v.start(classAnalyse, "loop", 0, null, null, null);
		SetEdges expectedValue = new SetEdges ("[(0, v<0>, 30), (30, a<0>, 36), (30, b<0>, 36)]");
		System.out.println(expectedValue + " : Expected value");
		System.out.println(v.toSetEdges() + " : Obtained value");
		assertTrue (expectedValue.equals(v.toSetEdges()));
	}
	
	@Test
	void test6() {
		assertNotNull (v);
		v.start(classAnalyse, "unqualifiedCall", 0, null, null, null);
		SetEdges expectedValue = new SetEdges ("[(0, x<0>, 10), (0, a<0>, 10)]");
		System.out.println(expectedValue + " : Expected value");
		System.out.println(v.toSetEdges() + " : Obtained value");
		assertTrue (expectedValue.equals(v.toSetEdges()));
	}

	@Test
	void test7() {
		assertNotNull (v);
		v.start(classAnalyse, "callSiteSensitivity", 0, null, null, null);
		SetEdges expectedValue = new SetEdges ("[(0, b<0>, 17), (0, a<0>, 10), (0, x<0>, 17)]");
		System.out.println(expectedValue + " : Expected value");
		System.out.println(v.toSetEdges() + " : Obtained value");
		assertTrue (expectedValue.equals(v.toSetEdges()));
	}
	
	@Test
	void test8() {
		assertNotNull (v);
		v.start(classAnalyse, "qualifiedCall", 0, null, null, null);
		SetEdges expectedValue = new SetEdges ("[(0, b<0>, 6), (0, a2<0>, 5), (5, x<0>, 6)]");
		System.out.println(expectedValue + " : Expected value");
		System.out.println(v.toSetEdges() + " : Obtained value");
		assertTrue (expectedValue.equals(v.toSetEdges()));
	}
}
