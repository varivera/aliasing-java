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

import model.AliasObject;
import model.AST.AliasAnalysis;
import structures.AliasDiagram;
import structures.Variable;
import structures.graphRep.SetEdges;

class AliasDiagramTests4 {
	AliasAnalysis v;
	String classAnalyse;
	
	@BeforeEach
	void InitClass() {
		System.out.println("Init Alias Analysis");
		try {
			String sourcePath = "";
			String[] unitName = new String[]{"QualifiedCall.java", "T.java", "Basic.java", "AAPaper.java", "ControlStruc.java"};
			String[] classpath = new String[] {};
			if (System.getProperty("os.name").contains("Windows")) {
				sourcePath = "C:\\Users\\varz8_p87ir1f\\git\\aliasing-java2\\AliasTestProject\\src\\Basics\\";
				classpath = new String[]{"C:\\Program Files\\Java\\jre1.8.0_181\\lib\\rt.jar"};
			}else if (System.getProperty("os.name").contains("Mac")) {
				sourcePath = "/Users/victor/git/aliasing-java2/AliasTestProject/src/Basics/";
				classpath = new String[]{"/Library/Java/JavaVirtualMachines/jdk1.8.0_151.jdk/Contents/Home/jre/librt.jar"};
			}else if (System.getProperty("os.name").contains("Linux")) {
				sourcePath = "/home/varivera/Desktop/VR/work/research/aliasing-java/AliasTestProject/src/Basics/";
				classpath = new String[]{"/Library/Java/JavaVirtualMachines/jdk1.8.0_151.jdk/Contents/Home/jre/librt.jar"};
			}
			
			classAnalyse = "ControlStruc";

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
		v.start(classAnalyse, "cond1", 0, null, null, null);
		SetEdges expectedValue = new SetEdges ("[(0, b<0>, 7), (0, c<0>, 8), (0, c<1-1>, 9), (0, d<0>, 9), (0, a<0>, 7)]");
		System.out.println(expectedValue + " : Expected value");
		System.out.println(v.toSetEdges() + " : Obtained value");
		assertTrue (expectedValue.equals(v.toSetEdges()));
	}
	
	@Test
	void test2() {
		assertNotNull (v);
		v.start(classAnalyse, "cond2", 0, null, null, null);
		SetEdges expectedValue = new SetEdges ("[(0, b<0>, 7), (0, a<1-1>, 7), (0, a<0>, 6), (0, c<0>, 8), (0, c<1-2>, 9), (0, d<0>, 9)]");
		System.out.println(expectedValue + " : Expected value");
		System.out.println(v.toSetEdges() + " : Obtained value");
		assertTrue (expectedValue.equals(v.toSetEdges()));
	}
	
	@Test
	void test3() {
		assertNotNull (v);
		v.start(classAnalyse, "cond3", 0, null, null, null);
		SetEdges expectedValue = new SetEdges ("[(0, b<0>, 7), (0, a<1-1>, 7), (6, b<0>, 9), (6, b<0>, 8), (7, b<0>, 8), (7, b<0>, 9), (0, a<0>, 6), (0, c<0>, 8), (0, c<1-2>, 9), (0, d<0>, 9)]");
		System.out.println(expectedValue + " : Expected value");
		System.out.println(v.toSetEdges() + " : Obtained value");
		assertTrue (expectedValue.equals(v.toSetEdges()));
	}
	
	@Test
	void test4() {
		assertNotNull (v);
		v.start(classAnalyse, "cond4", 0, null, null, null);
		SetEdges expectedValue = new SetEdges ("[(0, a<1-1>, 7), (0, a<0>, 6), (0, b<0>, 8), (0, b<1-2>, 7), (0, c<0>, 7)]");
		System.out.println(expectedValue + " : Expected value");
		System.out.println(v.toSetEdges() + " : Obtained value");
		assertTrue (expectedValue.equals(v.toSetEdges()));
		assertTrue(!v.aliased("a", "b"));
	}
	
	@Test
	void test5() {
		assertNotNull (v);
		v.start(classAnalyse, "cond5", 0, null, null, null);
		SetEdges expectedValue = new SetEdges ("[(0, c<0>, 9), (0, d<0>, 11), (0, a<1-1>, 9), (0, b<0>, 7), (0, a<1-2>, 11)]");
		System.out.println(expectedValue + " : Expected value");
		System.out.println(v.toSetEdges() + " : Obtained value");
		assertTrue (expectedValue.equals(v.toSetEdges()));
		assertTrue(!v.aliased("a", "b"));
	}
	
	@Test
	void test6() {
		assertNotNull (v);
		v.start(classAnalyse, "cond6", 0, null, null, null);
		SetEdges expectedValue = new SetEdges ("[(0, a<1-1-1>, 13), (0, d<0>, 9), (0, c<0>, 9), (0, a<1-1-2>, 15), (0, c<1-1>, 7), (0, f<0>, 15), (0, e<0>, 13), (0, a<1-2>, 9), (0, b<0>, 7)]");
		System.out.println(expectedValue + " : Expected value");
		System.out.println(v.toSetEdges() + " : Obtained value");
		assertTrue (expectedValue.equals(v.toSetEdges()));
		assertTrue(v.aliased("a", "e"));
		assertTrue(v.aliased("b","c"));
		assertTrue(v.aliased("a","f"));
		assertTrue(v.aliased("d","c"));
		assertTrue(v.aliased("c","a"));
		assertTrue(v.aliased("a","d"));
		assertTrue(!v.aliased("f","c"));
		assertTrue(!v.aliased("b","a"));
		assertTrue(!v.aliased("c","e"));
	}
	
	@Test
	void test7() {
		assertNotNull (v);
		v.start(classAnalyse, "cond7", 0, null, null, null);
		SetEdges expectedValue = new SetEdges ("[(0, c<1-2>, 9), (6, c<0>, 17), (0, a<0>, 6), (0, c<0>, 10), (0, x<0>, 14), (0, x<2-1>, 17), (6, right<1-1>, 9), (0, b<0>, 14), (6, right<0>, 7)]");
		System.out.println(expectedValue + " : Expected value");
		System.out.println(v.toSetEdges() + " : Obtained value");
		assertTrue (expectedValue.equals(v.toSetEdges()));
		assertTrue(v.aliased("x", "b"));
		assertTrue(v.aliased("a.c","x"));
		assertTrue(!v.aliased("a.right","c"));
		assertTrue(!v.aliased("b","a.c"));
	}
	
	@Test
	void test8() {
		assertNotNull (v);
		v.start(classAnalyse, "cond8", 0, null, null, null);
		SetEdges expectedValue = new SetEdges ("[(0, b<0>, 7), (7, x<0>, 15), (0, v<0>, 9), (0, v<0>, 15), (7, x<1-1>, 9), (0, w<0>, 9), (0, a<0>, 7), (7, y<0>, 7), (0, t<0>, 31), (0, v<2-1>, 31)]");
		System.out.println(expectedValue + " : Expected value");
		System.out.println(v.toSetEdges() + " : Obtained value");
		assertTrue (expectedValue.equals(v.toSetEdges()));
		assertTrue(v.aliased("v", "w"));
		assertTrue(v.aliased("a", "b"));
		assertTrue(v.aliased("a.y", "b"));
		assertTrue(v.aliased("b.y", "b"));
		assertTrue(v.aliased("a.y.x", "w"));
		assertTrue(v.aliased("a.x", "v"));
		assertTrue(v.aliased("b.x", "w"));
		assertTrue(v.aliased("b.x", "v"));
		assertTrue(v.aliased("t", "v"));
		assertTrue(v.aliased("a.y.x", "v"));
		assertTrue(!v.aliased("b.x","t"));
		assertTrue(!v.aliased("a.x","t"));
	} 

	@Test
	void test9() {
		assertNotNull (v);
		v.start(classAnalyse, "loop1", 0, null, null, null);
		SetEdges expectedValue = new SetEdges ("[(0, b<0>, 6), (0, a<0>, 5), (0, a<1>, 6)]");
		System.out.println(expectedValue + " : Expected value");
		System.out.println(v.toSetEdges() + " : Obtained value");
		assertTrue (expectedValue.equals(v.toSetEdges()));
		assertTrue(v.aliased("a", "b"));
	}
	
	@Test
	void test10() {
		assertNotNull (v);
		v.start(classAnalyse, "loop2", 0, null, null, null);
		SetEdges expectedValue = new SetEdges ("[(0, a<1>, 7), (0, v<0>, 8), (12, right<0>, 12), (0, a<1>, 12), (0, t<0>, 9), (5, right<0>, 7), (7, right<0>, 12), (0, a<0>, 5), (0, v<1>, 9)]");
		System.out.println(expectedValue + " : Expected value");
		System.out.println(v.toSetEdges() + " : Obtained value");
		assertTrue (expectedValue.equals(v.toSetEdges()));
		assertTrue(v.aliased("t", "v"));
		assertTrue(v.aliased("a", "a.right"));
		assertTrue(v.aliased("a.right", "a.right.right"));
		assertTrue(v.aliased("a.right.right.right.right.right", "a"));
		assertTrue(v.aliased("a", "a.right.right.right.right.right"));
	}
	
	@Test
	void test11() {
		assertNotNull (v);
		
		v.start(classAnalyse, "loop3", 0, null, null, null);
		SetEdges expectedValue = new SetEdges ("[(10, right<0>, 10), (5, right<0>, 7), (10, right<0>, 27), (0, a<1>, 10), (0, a<0>, 5), (7, right<0>, 10), (0, b<0>, 27), (0, a<1>, 7)]");
		System.out.println(expectedValue + " : Expected value");
		System.out.println(v.toSetEdges() + " : Obtained value");
		assertTrue (expectedValue.equals(v.toSetEdges()));
		assertTrue(v.aliased("a", "a.right"));
		assertTrue(v.aliased("a.right", "a.right.right"));
		assertTrue(v.aliased("a.right.right.right.right.right", "a"));
		assertTrue(v.aliased("a", "a.right.right.right.right.right"));
	}
}
