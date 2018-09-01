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
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
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
	
	
	// Compilation Unit
	private CompilationUnit cu;
	
	// Method to be analysed
	String method;
	
	public AliasAnalysis(ASTParser parser) {
		cu = (CompilationUnit) parser.createAST(null);
	}
	
	/**
	 * Starts the analysis of method 'methodName' in class
	 * 'className' stopping at point 'point'.
	 * Initial implementation will consider the analysis of 
	 * all method (i.e. point is equal to method exit)
	 */
	public void start (String className, String methodName, int point) {
		assert (cu == null);
		aliasGraph = new AliasDiagram (className);
		stackCall = new LinkedList <Routine>();
		method = methodName;
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
		//it is called only once
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
		
		// method signature
		stackCall.add(new Routine (node.getName().toString()));
		currentRoutine().setReturnType(node.getReturnType2().toString());
		
		
		for (int i=0;i<node.parameters().size();i++) {
			((SingleVariableDeclaration) node.parameters().get(i)).accept(this);
		}
		
		System.out.println (currentRoutine());
		node.getBody().accept(this);
		
		return false;
	}
	
	/**
	 * AST for the body of the routine.
	 * It goes down in the AST for each expression
	 */
	public boolean visit (Block node) {
		System.out.println("Block: " + node.getNodeType());
		for (int i=0;i<node.statements().size();i++) {
			((ExpressionStatement)node.statements().get(i)).accept(this);
		}
		return false;
	}
	
	/**
	 * AST for assignments
	 * This is the most crucial part of the implementation,
	 * aliasing happens in assignments 
	 */
	public boolean visit (Assignment node) {
		System.out.println("Assignment: " + node);
		
		return false;
	}
	
	/**
	 * It is visited for each argument a routine contains
	 */
	public boolean visit (SingleVariableDeclaration node) {
		System.out.println("SingleVariableDeclaration: " + node.getName());
		currentRoutine().addArgument(node.getName().toString(), node.getType().toString());
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
	
	/**
	 * 
	 * @return the current routine being analysed
	 */
	public Routine currentRoutine () {
		return stackCall.peek();
	}
	
	
	
	
	public static void main(String[] args) throws FileNotFoundException, IOException {
		String source = "source/sourceClass.java";
		AliasAnalyzer t = new AliasAnalyzer();
		//System.out.println(t.getFileContent(source));
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		char[] fileContent = t.getFileContent(source).toCharArray();

		parser.setSource(fileContent);
		
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setResolveBindings(true); //TODO this operation is expensive: do we really need it?
		
		AliasAnalysis v = new AliasAnalysis (parser);
		v.start("sourceClass", "test", 0);
		
		String g = v.aliasGraph.toGraphViz();
		Helpers.createDot (g, "test", "source");
		System.out.println("\nGraphViz: ");
		System.out.println(g);
		
		int[] i = new int[] {10};
		int[] j = i;
		i[0] = 1000;
		System.out.println(i[0]);
		System.out.println(j[0]);
		System.out.println(i == j);
		
		

	}

}
