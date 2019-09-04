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

class AliasDiagramTests {
	AliasAnalysis v;
	
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
			
			String classAnalyse = "Basic";

			v = new AliasAnalysis (sourcePath, unitName, classpath, classAnalyse);
			
			
		} catch (FileNotFoundException e) {
			fail (e.getMessage());
		} catch (IOException e) {
			fail (e.getMessage());
		}
	}
	
	@AfterEach
	public void sanityCehckPredecessor() {
		//assertTrue(v.predCheck());
	}
	
	@Test
	void test1() {
		assertNotNull (v);
		v.start("Basic", "assg1", 0, null, null, null);
		SetEdges expectedValue = new SetEdges ("[(0, w, 6), (0, v, 6)]");
		assertTrue (expectedValue.equals(v.toSetEdges()));
	}

	@Test
	void test2() {
		assertNotNull (v);
		v.start("Basic", "assg2", 0, null, null, null);
		SetEdges expectedValue = new SetEdges ("[(0, w, 6), (0, v, 8), (0, z, 8)]");
		assertTrue (expectedValue.equals(v.toSetEdges()));
	}
	
	@Test
	void test3() {
		assertNotNull (v);
		v.start("Basic", "assg3", 0, null, null, null);
		SetEdges expectedValue = new SetEdges ("[(0, w, 6), (0, v, 6), (0, z, 6)]");
		assertTrue (expectedValue.equals(v.toSetEdges()));
	}
	
	@Test
	void test4() {
		assertNotNull (v);
		v.start("Basic", "localArg1", 0, null, null, null);
		SetEdges expectedValue = new SetEdges ("[(0, v, 15), (0, w, 15)]");
		assertTrue (expectedValue.equals(v.toSetEdges()));
	}
	
	@Test
	void test5() {
		assertNotNull (v);
		v.start("Basic", "creation", 0, null, null, null);
		SetEdges expectedValue = new SetEdges ("[(0, v, 30), (30, a, 36), (30, b, 36)]");
		assertTrue (expectedValue.equals(v.toSetEdges()));
	}
	
	@Test
	void test6() {
		assertNotNull (v);
		v.start("Basic", "creation2", 0, null, null, null);
		SetEdges expectedValue = new SetEdges ("[(0, w, 6)]");
		assertTrue (expectedValue.equals(v.toSetEdges()));
	}

	@Test
	void test7() {
		assertNotNull (v);
		v.start("Basic", "unq_call", 0, null, null, null);
		SetEdges expectedValue = new SetEdges ("[(0, v, 10), (0, w, 10)]");
		assertTrue (expectedValue.equals(v.toSetEdges()));
	}
	
	@Test
	void test8() {
		assertNotNull (v);
		v.start("Basic", "unq_call_arg", 0, null, null, null);
		SetEdges expectedValue = new SetEdges ("[(0, v, 11), (0, w, 11)]");
		assertTrue (expectedValue.equals(v.toSetEdges()));
	}

	@Test
	void test9() {
		assertNotNull (v);
		v.start("Basic", "nestedCall", 0, null, null, null);
		SetEdges expectedValue = new SetEdges ("[(0, z, 10), (0, v, 10)]");
		assertTrue (expectedValue.equals(v.toSetEdges()));
	}	
	
	@Test
	void test10() {
		assertNotNull (v);
		v.start("Basic", "localArg2", 0, null, null, null);
		SetEdges expectedValue = new SetEdges ("[(0, w, 15), (0, v, 15)]");
		assertTrue (expectedValue.equals(v.toSetEdges()));
	}
	
	@Test
	void test11() {
		assertNotNull (v);
		v.start("Basic", "args", 0, null, null, null);
		SetEdges expectedValue = new SetEdges ("[(0, v, 5)]");
		assertTrue (expectedValue.equals(v.toSetEdges()));
	}
	
	@Test
	void test12() {
		assertNotNull (v);
		v.start("Basic", "t3", 0, null, null, null);
		SetEdges expectedValue = new SetEdges ("[(0, v, 5)]");
		assertTrue (expectedValue.equals(v.toSetEdges()));
	}
	
	@Test
	void test13() {
		assertNotNull (v);
		v.start("Basic", "t2", 0, null, null, null);
		SetEdges expectedValue = new SetEdges ("[(0, v, 5)]");
		assertTrue (expectedValue.equals(v.toSetEdges()));
	}
	
	@Test
	void test14() {
		assertNotNull (v);
		v.start("Basic", "return1", 0, null, null, null);
		SetEdges expectedValue = new SetEdges ("[]");
		assertTrue (expectedValue.equals(v.toSetEdges()));
	}
	
	@Test
	void test15() {
		assertNotNull (v);
		v.start("Basic", "return2", 0, null, null, null);
		SetEdges expectedValue = new SetEdges ("[]");
		assertTrue (expectedValue.equals(v.toSetEdges()));
	}
	
	@Test
	void test16() {
		assertNotNull (v);
		v.start("Basic", "return3", 0, null, null, null);
		SetEdges expectedValue = new SetEdges ("[]");
		assertTrue (expectedValue.equals(v.toSetEdges()));
	}
	
	@Test
	void test17() {
		assertNotNull (v);
		v.start("Basic", "return4", 0, null, null, null);
		SetEdges expectedValue = new SetEdges ("[]");
		assertTrue (expectedValue.equals(v.toSetEdges()));
	}
	
	@Test
	void test18() {
		assertNotNull (v);
		v.start("Basic", "return5", 0, null, null, null);
		SetEdges expectedValue = new SetEdges ("[(0, v, 5)]");
		assertTrue (expectedValue.equals(v.toSetEdges()));
	}
	
	@Test
	void test19() {
		assertNotNull (v);
		v.start("Basic", "return6", 0, null, null, null);
		SetEdges expectedValue = new SetEdges ("[(0, v, 9)]");
		assertTrue (expectedValue.equals(v.toSetEdges()));
	}
	
	@Test
	void test20() {
		assertNotNull (v);
		v.start("Basic", "methodInv", 0, null, null, null);
		SetEdges expectedValue = new SetEdges ("[(0, w, 10), (0, v, 10)]");
		assertTrue (expectedValue.equals(v.toSetEdges()));
	}
	
	@Test
	void test21() {
		assertNotNull (v);
		v.start("Basic", "methodInv2", 0, null, null, null);
		SetEdges expectedValue = new SetEdges ("[(0, w, 14), (0, v, 14)]");
		assertTrue (expectedValue.equals(v.toSetEdges()));
	}
	
	@Test
	void test22() {
		assertNotNull (v);
		v.start("Basic", "get_v", 0, null, null, null);
		SetEdges expectedValue = new SetEdges ("[(0, v, 5)]");
		assertTrue (expectedValue.equals(v.toSetEdges()));
	}
	
	@Test
	void test23() {
		assertNotNull (v);
		v.start("Basic", "func", 0, null, null, null);
		SetEdges expectedValue = new SetEdges ("[(0, w, 10), (0, v, 10)]");
		assertTrue (expectedValue.equals(v.toSetEdges()));
	}
	
	@Test
	void test24() {
		assertNotNull (v);
		v.start("Basic", "creationAndCall2", 0, null, null, null);
		SetEdges expectedValue = new SetEdges ("[(8, a, 14), (0, v, 8), (8, b, 14), (0, z, 6)]");
		assertTrue (expectedValue.equals(v.toSetEdges()));
	}
	
	@Test
	void test25() {
		assertNotNull (v);
		v.start("Basic", "creationAndCall1", 0, null, null, null);
		SetEdges expectedValue = new SetEdges ("[(14, b, 20), (0, w, 10), (14, a, 20), (0, z, 12), (0, v, 14)]");
		assertTrue (expectedValue.equals(v.toSetEdges()));
	}
	
	@Test
	void test26() {
		assertNotNull (v);
		v.start("Basic", "whichmethod", 0, null, null, null);
		SetEdges expectedValue = new SetEdges ("[(5, a, 15), (5, c, 15), (0, v, 5)]");
		assertTrue (expectedValue.equals(v.toSetEdges()));
	}
	
	@Test
	void test27() {
		assertNotNull (v);
		v.start("Basic", "whichmethod2", 0, null, null, null);
		SetEdges expectedValue = new SetEdges ("[(5, b, 16), (0, z, 6), (5, a, 16), (0, v, 5)]");
		assertTrue (expectedValue.equals(v.toSetEdges()));
	}
	
	@Test
	void test28() {
		assertNotNull (v);
		v.start("Basic", "creation3", 0, null, null, null);
		SetEdges expectedValue = new SetEdges ("[(0, w, 9), (8, a, 9), (0, v, 8), (0, z, 6)]");
		assertTrue (expectedValue.equals(v.toSetEdges()));
	}
	
	@Test
	void test29() {
		assertNotNull (v);
		v.start("Basic", "Basic", 0, null, null, null);
		SetEdges expectedValue = new SetEdges ("[(0, w, 14), (14, a, 20), (22, b, 28), (6, a, 12), (22, a, 28), (0, v, 6), (6, b, 12), (14, b, 20), (0, z, 22)]");
		assertTrue (expectedValue.equals(v.toSetEdges()));
	}
}
