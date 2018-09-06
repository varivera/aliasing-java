package model.AST;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import demo.sourceClass;
import exceptions.ASTException;
import exceptions.AliasException;
import exceptions.Log;
import model.AliasObject;
import model.Routine;
import model.nodeInfo;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclarationStatement;
import org.eclipse.jdt.core.dom.TypeLiteral;
import org.eclipse.jdt.core.dom.TypeParameter;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;

import structures.AliasDiagram;
import structures.graphRep.SetEdges;
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
		assert (cu != null);
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
	 * 
	 * TODO: is it visited in qualified calls?
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
			System.out.println("-> " + node.statements().get(i).getClass());
			((ASTNode)node.statements().get(i)).accept(this);
		}
		return false;
	}
	
	public boolean visit (VariableDeclarationStatement node) {
		System.out.println("VariableDeclarationStatement: " + node);
		return false;
	}
	
	/**
	 * Expression
	 */
	public boolean visit (ExpressionStatement node) {
		System.out.println("ExpressionStatement " + node);
		node.getExpression().accept(this);
		return false;
	}
	
	/**
	 * Return statement
	 */
	public boolean visit (ReturnStatement node) {
		System.out.println("ReturnStatement " + node);
		return false;
	}
	
	/**
	 * AST for assignments
	 * This is the most crucial part of the implementation,
	 * aliasing happens in assignments 
	 * 
	 * The process is as follows. In 'a = b', where 'a' and 'b'
	 * 		could be anything:
	 * 	(i) collect all objects that 'a' is pointing at (from root)
	 *  (ii) collect all objects that 'b' is pointing at (from root)
	 *  (iii) remove objects in (i)
	 *  (iv) for each object in (ii), put it in (i)
	 * 
	 * TODO: check how to manage local variables.
	 */
	public boolean visit (Assignment node) {
		System.out.println("Assignment: " + node);
		/**
		 * v = w; where 'v' is a variable (could be a path)
		 * and 'w' is an expression 
		 */
		
		System.out.println("left");
		nodeInfo left = getNodeInfo(null, node.getLeftHandSide());
		System.out.println("right");
		nodeInfo right = getNodeInfo(null, node.getRightHandSide());
		
		aliasing (left, right);
		
		
		/*System.out.println("left");
		node.getLeftHandSide().accept(this);
		System.out.println("right");
		node.getRightHandSide().accept(this);*/
		
		return false;
	}
	
	/**
	 * Basic operation of Aliasing
	 * @param left
	 * @param right
	 */
	public void aliasing (nodeInfo left, nodeInfo right) {
		assert (left.pointingAt.size() == right.pointingAt.size());
		/** a = b
		 * a [[o1, o2]]
		 * b [[o3]]
		 */
		for (ArrayList<AliasObject> l: left.pointingAt) {
			l.clear();
		}
		
		for (int i=0;i<right.pointingAt.size();i++) {
			for (AliasObject ao: right.pointingAt.get(i)) {
				left.pointingAt.get(i).add(ao);
			}
		}
	}
	
	/**
	 * Taking SimpleName as class variables 
	 * TODO: check how to catch local variables and 
	 * arguments.
	 */
	public boolean visit (SimpleName node) {
		System.out.println("SimpleName: " + node);
		
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
	
	public boolean visit (TypeDeclarationStatement node) {
		System.out.println("TypeDeclarationStatement: " + node);
		return false;
	}
	
	/**
	 * 
	 * @param target represents qualified calls (null otherwise)
	 * @param node the ASTNode being analysed
	 * @return the information about the node. For instance, if the
	 * 		node is a variable, the nodeInfo will contain the alias
	 * 		object that variable is pointing at.
	 */
	public nodeInfo getNodeInfo (AliasObject target, ASTNode node) {
		nodeInfo res = null;
		
		if (node instanceof SimpleName) {
			SimpleName n = (SimpleName) node;
			ITypeBinding typeBinding = n.resolveTypeBinding();
			System.out.println("typeBinding: " + typeBinding.getName());
			// adding information to the alias graph in case it has not been added
			aliasGraph.initEdge (n.toString(), typeBinding.getName());
			
			res = new nodeInfo (n.toString());
			aliasGraph.aliasObjects(res);
		}
		
		return res;
	}
	
	/**
	 * 
	 * @return the current routine being analysed
	 */
	public Routine currentRoutine () {
		return stackCall.peek();
	}
	
	/**
	 * 
	 * @return the graph as a set of edges
	 */
	public SetEdges toSetEdges () {
		return aliasGraph.toSetEdges();
	}
	
	
	/***** no visit *****/
	/**
	 * AST for Package Declaration
	 */
	public boolean visit (PackageDeclaration node) {
		System.out.println("PackageDeclaration: " + node + " doNothing");
		return false;
	}
	
	/**
	 * AST for Package Declaration
	 */
	public boolean visit (ImportDeclaration node) {
		System.out.println("ImportDeclaration: " + node + " doNothing");
		return false;
	}
	
	
	
	public static void main(String[] args) throws FileNotFoundException, IOException {
		
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
		char[] fileContent = Helpers.getFileContent(sourcePath+unitName).toCharArray();

		
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
		
		AliasAnalysis v = new AliasAnalysis (parser);
		v.start("Basic", "test3", 0);
		
		String g = v.aliasGraph.toGraphViz();
		Helpers.createDot (g, "test", "source");
		System.out.println("\nGraphViz: ");
		System.out.println(g);
		System.out.println("==========");
		System.out.println(v.aliasGraph.toSetEdges());
	}

}