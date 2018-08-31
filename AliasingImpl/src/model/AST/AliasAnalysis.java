package model.AST;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

import demo.sourceClass;
import exceptions.ASTException;
import exceptions.Log;
import model.Routine;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclarationStatement;
import org.eclipse.jdt.core.dom.TypeLiteral;
import org.eclipse.jdt.core.dom.TypeParameter;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

import structures.AliasDiagram;
import structures.helpers.Helpers;

/**
 * 
 * Implements a Visitor for Java code AST. It attaches information
 * to a Alias Diagram after visiting each instruction in the code
 * 
 * @author Victor Rivera
 *
 */

public class AliasAnalysis extends ASTVisitor {
	
	/**
	 * holds the alias diagram. It will be updated
	 * according to the Literature. refer to RiveraMeyer:18:JSS
	 */
	private AliasDiagram aliasGraph;
	
	/**
	 * Holds all routine calls.
	 */
	private Queue<Routine> stackCall;
	
	//TODO delete
	private String method;
	
	// Compilation Unit
	private CompilationUnit cu;
	
	public AliasAnalysis(ASTParser parser) {
		cu = (CompilationUnit) parser.createAST(null);
	}
	
	/**
	 * Starts the analysis
	 */
	public void start (String method) {
		
		//TODO aliasGraph = new AliasDiagram (node.getName().toString());
		stackCall = new LinkedList <Routine>();
		stackCall.add(new Routine ());
		cu.accept(this);
	}
	
	/**
	 * TypeDeclaration is visited initially. It initialises 
	 * 	the aliasGraph as it contains the name of the
	 * 	class being analysed.
	 * 
	 * 	It also finds the source code of the method being analysed.
	 */
	public boolean visit (TypeDeclaration node) {
		System.out.println ("TypeDeclaration: " + node.getName().toString());
		
		// finding the right method
		MethodDeclaration m = null;
		for (int i=0;i<node.getMethods().length;i++) {
			if (node.getMethods()[i].getName().toString().equals(method)) {
				m = node.getMethods()[i];
				break;
			}
		}
		
		if (m != null) {
			m.accept(this);
		}else {
			try {
				throw new ASTException ("Method " + method + " was not found in the class");
			} catch (ASTException e) {
				e.printStackTrace();
				Log.log.push(e);
			}
		}
		
		
		return false;
	}
	
	/**
	 * Method to be read line by line. It constructs the 
	 * 	Alias Graph accordingly
	 */
	public boolean visit (MethodDeclaration node) {
		System.out.println ("MethodDeclaration: " + node.getName());
		
		// TODO: method signature
		
		
		return false;
	}
	
	/**
	 * AST for Class Fields
	 */
	public boolean visit (VariableDeclarationFragment node) {
		SimpleName name = node.getName();
		int lineNumber = cu.getLineNumber(name.getStartPosition());
		System.out.println("Name: " + name.toString());
		System.out.println("Line: " + lineNumber);
		System.out.println("----------------------------");
		return false;
	}
	
	
	
	
	public static void main(String[] args) throws FileNotFoundException, IOException {
		String source = "source/sourceClass.java";
		AliasAnalyzer t = new AliasAnalyzer();
		//System.out.println(t.getFileContent(source));
		ASTParser parser = ASTParser.newParser(AST.JLS2);
		char[] fileContent = t.getFileContent(source).toCharArray();

		parser.setSource(fileContent);
		
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setResolveBindings(true); //TODO this operation is expensive: do we really need it?
		
		AliasAnalysis v = new AliasAnalysis (parser);
		v.start("test");
		
		String g = v.aliasGraph.toGraphViz();
		Helpers.createDot (g, "test", "source");
		System.out.println("\nGraphViz: ");
		System.out.println(g);
		
		

	}

}
