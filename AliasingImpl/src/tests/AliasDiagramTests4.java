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
	
}
