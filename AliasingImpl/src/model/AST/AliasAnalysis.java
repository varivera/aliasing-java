package model.AST;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import exceptions.ASTException;
import exceptions.AliasException;
import exceptions.Log;
import model.AliasObject;
import model.Routine;
import model.nodeInfo;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.*;
import structures.AliasDiagram;
import structures.Conditional;
import structures.Edge;
import structures.Variable;
import structures.graphRep.SetEdges;
import structures.helpers.Const;
import structures.helpers.GlobalCond;
import structures.helpers.Helpers;
import structures.helpers.Id;

/**
 * 
 * Implements a Visitor for Java code AST. It attaches information
 * to the Alias Diagram after visiting each instruction in the code
 * 
 * @author Victor Rivera (victor.rivera@anu.edu.au)
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
	private Deque<Routine> stackCall;
	
	/**
	 * Stores information from conditionals, loops, recursion.
	 */
	private Deque<Conditional> stackControlStructures;

	// Compilation Unit
	private CompilationUnit cu;

	// CompilationUnit of all classes involved
	private HashMap<String, CompilationUnit> cus;

	// Method to be analysed
	String methodName;

	// Class to be analysed
	String className;

	// Actual Remote Arguments sent from source
	nodeInfo[] actualremoteArgs;
	// Actual Remote Arguments types sent from source
	String[] actualremoteTypes;
	
	/**
	 * Keep track of the conditionals. This is important to determine
	 * the computational path
	 */
	GlobalCond globalCond;

	/**
	 * Id generator: to give a unique identifier to Alias Objects
	 */
	public final Id idGen;

	/**
	 * Method to be read line by line. It constructs the 
	 * 	Alias Graph accordingly
	 */
	public boolean visit (MethodDeclaration method) {
		System.out.println ("MethodDeclaration: " + method.getName());




		return false;
	}

	//TODO unitName: should be only the sourcePath. 
	//TODO: 	(i) retrieve all .java files from sourcePath
	public AliasAnalysis(String sourcePath, String[] unitName, String[] classpath,
			String rootName) throws FileNotFoundException, IOException {
		cus = new HashMap<String, CompilationUnit>();
		for (String javaSource: unitName) {

			//System.out.println(t.getFileContent(source));
			ASTParser parser = ASTParser.newParser(AST.JLS11);
			char[] fileContent = Helpers.getFileContent(sourcePath+javaSource).toCharArray();


			parser.setKind(ASTParser.K_COMPILATION_UNIT);

			Map options = JavaCore.getOptions();
			parser.setCompilerOptions(options);

			parser.setResolveBindings(true);
			parser.setBindingsRecovery(true);

			parser.setUnitName(javaSource);

			String[] sources = {sourcePath}; 


			parser.setEnvironment(classpath, sources, new String[] { "UTF-8"}, true);
			parser.setSource(fileContent);
			cus.put(javaSource.replace(".java", ""), (CompilationUnit) parser.createAST(null));


		}
		stackCall = new ArrayDeque <Routine>();
		stackControlStructures = new ArrayDeque<Conditional>();
		idGen = new Id();
		aliasGraph = new AliasDiagram (idGen);
		globalCond = new GlobalCond();
	}

	/**
	 * @param method to be added the Stack Call
	 * @return the routine and its signature to be added to the Stack Call
	 */
	public Routine routineSignature (MethodDeclaration method) {
		System.out.println(method == null);
		Routine res = new Routine (method.getName().toString(), idGen);
		res.setReturnType();

		for (int i=0;i<method.parameters().size();i++) {
			res.addArgument(
					((SingleVariableDeclaration) method.parameters().get(i)).getName().toString());

		}
		return res;
	}

	/**
	 * Starts the analysis of method 'methodName' in class
	 * 'className' stopping at point 'point'.
	 * Initial implementation will consider the analysis of 
	 * all method (i.e. point is equal to method exit)
	 */
	public void start (String className, String methodName, int point, AliasAnalysis current, nodeInfo[] actualremoteArgs, String[] actualremoteTypes) {
		this.methodName = methodName;
		this.className = className;
		if (current != null) { //no root
			aliasGraph = current.aliasGraph;
			stackCall = current.stackCall;
			this.actualremoteArgs = actualremoteArgs;
			this.actualremoteTypes = actualremoteTypes;
			globalCond = current.globalCond;
			Helpers.printStackAll(stackCall);
		}
		cu = cus.get(className);
		cu.accept(this);
	}

	/**
	 * TypeDeclaration is visited initially. It initialises 
	 * 	the aliasGraph as it contains the name of the
	 * 	class being analysed.
	 * 
	 * 	It also finds the source code of the method being analysed.
	 * 
	 * TODO: is it visited in (un)qualified calls?
	 */
	public boolean visit (TypeDeclaration node) {
		//it is called only once
		System.out.println ("TypeDeclaration: " + node.getName().toString());


		MethodDeclaration m = null;

		//TODO: it can be done similarly to:
		//MethodDeclaration method = (MethodDeclaration) cu.findDeclaringNode(call.getName().resolveBinding());

		if (this.actualremoteArgs == null && this.actualremoteTypes == null) { // Initial call
			//TODO: find another way to do this: no need to iterate over all methods
			for (int j=0;j<node.getMethods().length;j++) {
				if (node.getMethods()[j].getName().toString().equals(this.methodName)) {
					m = node.getMethods()[j];
					stackCall.push(routineSignature (m));
					Helpers.printStackAll(stackCall);
					m.getBody().accept(this);
					break;
				}
			}
		}else if (this.actualremoteArgs != null && this.actualremoteTypes != null) { // Call from an object
			//TODO: find another way to do this: no need to iterate over all methods
			for (int j=0;j<node.getMethods().length;j++) {
				if (node.getMethods()[j].getName().toString().equals(this.methodName)
						&& compare (node.getMethods()[j].parameters())) {
					m = node.getMethods()[j];
					stackCall.push(routineSignature (m));
					Helpers.printStackAll(stackCall);
					
					//check if the method is called from another object
					if (this.actualremoteArgs != null) {
						assert actualremoteArgs.length == m.parameters().size();
						/**
						 * Handling Arguments
						 */
						// Get method's signature (used for the routineStack)
						//link formal to actual arguments (if any)
						for (int i=0;i< m.parameters().size();i++) {
							System.out.println("Formal Argument " + (i+1) + " : " + m.parameters().get(i));
							System.out.println("Actual Argument " + (i+1) + " : " + actualremoteArgs [i]);

							nodeInfo formal = new nodeInfo(
									((SingleVariableDeclaration)m.parameters().get(i)).getName().toString());
							stackCall.peek().aliasObjectsArgument(formal);

							if (formal != null && actualremoteArgs[i] != null) {
								aliasing (formal, actualremoteArgs[i]);
							}else {
								System.out.println ("Either left or right was null");

							}
						}

						this.actualremoteArgs = null;
						this.actualremoteTypes = null;
					}

					m.getBody().accept(this);
					break;
				}
			}
		}


		if (m == null) {
			try {
				throw new ASTException ("Method " + methodName + " was not found in class " + className);
			} catch (ASTException e) {
				e.printStackTrace();
				Log.log.push(e);
			}
		}

		return false;
	}

	private boolean compare(List parameters) {
		if (parameters.size() != this.actualremoteArgs.length) {
			return false;
		}
		
		for (int i=0; i < parameters.size(); i++) {
			if (!this.actualremoteTypes[i].equals("null") && !parameters.get(i).toString().split(" ")[0].equals(this.actualremoteTypes[i])) {
				return false;
			}
			//System.out.println("formal: " + parameters.get(i).toString().split(" ")[0]);
			//System.out.println("actual: " + );
		}
		
		return true;
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

	/**
	 * Only deals with local variable declaration: the analysis is done
	 * over methods, methods do not define class fields.
	 */
	public boolean visit (VariableDeclarationStatement node) {
		System.out.println("VariableDeclarationStatement: " + node);
		//value of the local name variable is not available. The link 
		//is created and updated down the AST tree.
		currentRoutine().addLocalVariable("?NoInfo?");
		// Addition of local variable is done down the AST Tree where
		// all information (name and type) is available
		System.out.println("\t\tParent Type: " + node.getParent().getClass());
		return true;
	}

	public void endVisit (VariableDeclarationStatement node) {
		System.out.println("END VariableDeclarationStatement: " + node);

	}

	/**
	 * Expression
	 */
	public boolean visit (ExpressionStatement node) {
		System.out.println("ExpressionStatement " + node);

		// back
		//getNodeInfo (null, node.getExpression());
		// back
		return true;
	}

	/**
	 * Return statement
	 */
	public boolean visit (ReturnStatement node) {
		System.out.println("ReturnStatement " + node);
		System.out.println(">> " + node.getExpression());
		System.out.println(">> " + node.getExpression().getClass());

		/**
		 * "return exp;" is treated as "return = exp;"
		 */
		nodeInfo left = new nodeInfo (Const.RETURN);
		currentRoutine().aliasObjectsReturn(left);

		nodeInfo right = null;

		if (node.getExpression() instanceof SimpleName) {
			//variable: either an argument, class field or local 

			SimpleName n = (SimpleName) node.getExpression();

			right = new nodeInfo (n.toString());

			if (currentRoutine().isLocal(right.tag)) {
				//Local
				currentRoutine().aliasObjectsLocal(right);
			}else if (currentRoutine().isArgument(right.tag)) {
				//Argument
				currentRoutine().aliasObjectsArgument(right);   
			}else { // it is indeed a variable
				// adding information to the alias graph in case it has not been added
				ITypeBinding typeBinding = n.resolveTypeBinding();

				aliasGraph.initEdge (n.toString(), null);

				aliasGraph.aliasObjects(right);
			}
			aliasing (left, right);
		}else if(node.getExpression() instanceof MethodInvocation) {
			visit ((MethodInvocation)node.getExpression());
			right = nodeInfoLastRoutine;
			assert right != null;
			aliasing (left, right);
		}else {
			//TODO: check others
		}
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
	 */
	public boolean visit (Assignment node) {
		System.out.println("Assignment: " + node);
		/**
		 * v = w; where 'v' is a variable (could be a path)
		 * and 'w' is an expression 
		 */

		nodeInfo left = null;
		nodeInfo right = null;

		System.out.println("left");
		left = getNodeInfo(false, node.getLeftHandSide());
		System.out.println("right");
		if (left != null) {
			right = getNodeInfo(false, node.getRightHandSide());
		}else {
			//ignore this case: no reference type
			System.out.println("\tright no visited: left was null");
		}


		if (left != null && right != null) {
			aliasing (left, right);
		}else {
			System.out.println("\taliasing was not performed: either left or right was null");
		}

		return false;
	}

	/**
	 * Basic operation of Aliasing
	 * @param left
	 * @param right
	 */
	public void aliasing (nodeInfo left, nodeInfo right) {
		//to delete
		System.out.println("left: " + left);
		System.out.println("right: " + right);
		//to delete
		assert left != null && right != null;
		assert left.nRoots() == right.nRoots();
		assert left.nRoots() >= 1;
		/** a = b
		 * a [[o1, o2]]
		 * b [[o3]]
		 */
		
		//to delete
		System.out.println("left: " + left);
		System.out.println("right: " + right);
		
		//to delete
		
		//check if the aliasing happens inside a control structure. If so,
		//store the edge that will be removed
		if (stackControlStructures.size()>0) {
			for (ArrayList<Edge> es: left.getEdges()) {
				for (Edge e: es) {
					//to delete
					System.out.println("removed: " + e);
					//to delete
					stackControlStructures.peek().add(e);
				}
			}
		}else {//It is in the base computation, then remove left from the Alias Diagram
			for (ArrayList<Edge> es: left.getEdges()) {
				for (Edge e: es) {
					e.source().succ.get(e.tag()).remove(e.target());
					e.target().pred.get(e.tag()).remove(e.source());
				}
			}
		}
		
		//to delete
		//System.out.println("size: " + stackControlStructures.peek()..size());
		//to delete
		
		
		for (int i=0;i<left.nRoots(); i++) {
			
			int[] path = null;
			if (stackControlStructures.size()>0) {
				path = new int[stackControlStructures.size()+1];
				path[0] = globalCond.getNumber();
				globalCond.getNumber();
				int c = stackControlStructures.size();
				for (Conditional cond: stackControlStructures) {
					path[c--] = cond.getDeletionCount();
				}
			}else {
				path = new int[] {0};
			}
			
			/*Variable newCP = new Variable(left.tag, (stackControlStructures.size()>0) ?
					stackControlStructures.peek().computationalPath(globalCond.getNumber())
					: new int[] {0});*/
			Variable newCP = new Variable(left.tag, path);
			for (Edge edgeLeft: left.getEdges().get(i)) {
				for (Edge edgeRight: right.getEdges().get(i)) {
					if (!edgeLeft.source().succ.containsKey(newCP)) {
						edgeLeft.source().succ.put(newCP, new ArrayList<AliasObject>());
					}
					edgeLeft.source().succ.get(newCP).add(edgeRight.target());
					//update predecessors
					//edgeLeft.target().pred.get(edgeLeft.tag()).remove(edgeLeft.source());
					
					if (!edgeRight.target().pred.containsKey(newCP)) {
						edgeRight.target().pred.put(newCP, new ArrayList<AliasObject>());
					}
					edgeRight.target().pred.get(newCP).add(edgeLeft.source());
				}	
			}
		}
		
		
		System.out.println("Aliased: " + left.tag + ":" + right.tag);
	}

	/**
	 * Taking SimpleName as class variables 
	 * arguments.
	 */
	public boolean visit (SimpleName node) {
		System.out.println("SimpleName: " + node);

		return true;

	}

	/**
	 * It is visited for each argument a routine contains
	 */
	public boolean visit (SingleVariableDeclaration node) {
		System.out.println("SingleVariableDeclaration: " + node.getName());
		return false;
	}

	/**
	 * AST for Class Fields
	 */
	public boolean visit (VariableDeclarationFragment node) {
		System.out.println("VariableDeclarationFragment: " + node);
		if (node.getParent() instanceof VariableDeclarationStatement &&
				currentRoutine().isUpdateNeeded("?NoInfo?")
				){
			// the analysis comes from a local declaration
			System.out.println("\t\tcoming from local declaration");
			try {
				currentRoutine().updateKeyLocalVariable(new Variable("?NoInfo?"), new Variable(node.getName().toString()));

			} catch (AliasException e) {
				e.printStackTrace();
				Log.log.add(e);
			}
		}

		// check whether there is a call function, a initialisation or 
		//a variable
		if (node.getInitializer() != null) {
			if (node.getInitializer() instanceof SimpleName) {
				//variable: either an argument, class field or local 
				SimpleName n = (SimpleName) node.getInitializer();

				nodeInfo left = new nodeInfo (node.getName().toString());
				currentRoutine().aliasObjectsLocal(left);

				nodeInfo right = getNodeInfo (false, n);
				aliasing (left, right);
			}else {
				System.out.println("No var/local/arg " + node.getInitializer());
				node.getInitializer().accept(this);
			}
		}


		return false;
	}

	public boolean visit (TypeDeclarationStatement node) {
		System.out.println("TypeDeclarationStatement: " + node);
		return true;
	}

	/**
	 * 
	 * @param qualified is getting the information from a qualified calls?
	 * @param node the ASTNode being analysed
	 * @return the information about the node. For instance, if the
	 * 		node is a variable, the nodeInfo will contain the alias
	 * 		object that variable is pointing at.
	 */
	public nodeInfo getNodeInfo (boolean qualified, ASTNode node) {
		nodeInfo res = null;

		if (node instanceof NumberLiteral) {
			//Ignore
		}else if (node instanceof SimpleName) {
			System.out.println("\tgetNodeInfo>SimpleName");

			// SimpleName can represent class variables, local variables, arguments
			SimpleName n = (SimpleName) node;

			// If the target is not null, the node is an attribute of the
			// target (it cannot be a local variable or an argument)
			if (qualified) {
				// adding information to the alias graph in case it has not been added
				aliasGraph.initEdge (n.toString(), null);
				res = new nodeInfo (n.toString());
				aliasGraph.aliasObjects(res);
			}else {

				ITypeBinding typeBinding = n.resolveTypeBinding();
				//System.out.println("typeBinding: " + typeBinding.getName());

				// Ignore native types (they are not references e.g. int, float ...)
				if (typeBinding != null && typeBinding.isPrimitive()) {
					//doNothing
				}else {
					Integer type = getTypeNode (n);
					if (type.equals(Const.LOCAL)){
						// adding information to the alias graph in case it has not been added
						//currentRoutine().addLocalVariable(n.toString(), typeBinding.getName());
						//Note: local variables can only be added through variable declaration

						res = new nodeInfo (n.toString());
						currentRoutine().aliasObjectsLocal(res);
					}else if (type.equals(Const.ARGUMENT)){
						// adding information to the alias graph in case it has not been added

						res = new nodeInfo (n.toString());
						currentRoutine().aliasObjectsArgument(res);
					}else if (type.equals(Const.ATTRIBUTE)) {
						// adding information to the alias graph in case it has not been added
						aliasGraph.initEdge (n.toString(), null);

						res = new nodeInfo (n.toString());
						aliasGraph.aliasObjects(res);
					} 
				}
			}
		}else if (node instanceof MethodInvocation) {
			System.out.println("\tgetNodeInfo>MethodInvocation");

			visit ((MethodInvocation)node);
			res = nodeInfoLastRoutine;
		}else if (node instanceof ClassInstanceCreation) {
			System.out.println("\tgetNodeInfo>ClassInstanceCreation");
			node.accept(this);
			assert nodeInfoLastRoutine != null;
			res = nodeInfoLastRoutine;
		}else if(node instanceof SingleVariableDeclaration){
			System.out.println("\tgetNodeInfo>SingleVariableDeclaration");
			// Formal argument
			res = new nodeInfo (((SingleVariableDeclaration)node).getName().toString());
			currentRoutine().aliasObjectsArgument(res);
		}else if (node instanceof QualifiedName) {
			System.out.println("\tgetNodeInfo>QualifiedName");
			node.accept(this);
			assert nodeInfoLastRoutine != null;
			res = nodeInfoLastRoutine;
		}else if (node instanceof FieldAccess) {
			System.out.println("\tgetNodeInfo>FieldAccess");
			node.accept(this);
			assert nodeInfoLastRoutine != null;
			res = nodeInfoLastRoutine;
		}else if (node instanceof ThisExpression) {
			System.out.println("\tgetNodeInfo>ThisExpression");
			res = new nodeInfo ("0");
			for (AliasObject r: aliasGraph.getRoots()) {
				res.newRoot();
				res.addEdge(r, new Variable("0"), r);
			}
		}else {
			System.out.println("AST node no supported yet: " + node.getClass() + " (" + node + ")" );

		}

		return res;
	}

	public boolean visit(ClassInstanceCreation node) {
		System.out.println ("ClassInstanceCreation");
		System.out.println(node.getExpression());
		System.out.println(node.getType());

		/**
		 * new T();
		 * 
		 *   (i) create a new Alias Object (node in the graph)
		 *  (ii) get T's information (i.e. actual arguments)
		 * (iii) reroot to (i)
		 *  (iv) Analyse T()
		 * (vii) reroot back
		 *  (vi) create nodeInfo 
		 */
		AliasObject n = new AliasObject(idGen.getId()); // (i) 

		nodeInfo[] actual = new nodeInfo[node.arguments().size()];
		String[] actualTypes = new String[actual.length];
		//Get actual arguments
		for (int i=0;i<node.arguments().size();i++) {
			System.out.println("Actual Argument " + (i+1) + " : " + node.arguments().get(i));
			// TODO: get rid of this workaround https://github.com/varivera/aliasing-java/issues/23
			actual[i] = getNodeInfo (false, (ASTNode)node.arguments().get(i));
			if (node.arguments().get(i) instanceof QualifiedName) {
				actualTypes[i] = ((QualifiedName)node.arguments().get(i)).getQualifier().resolveBinding().toString()
						.split(" ")[0];
				
			}else {
				actualTypes[i] = ((Expression)node.arguments().get(i)).resolveTypeBinding().getName();
			}
		}

		ArrayList <ArrayList<AliasObject>> tmpRoot = new ArrayList <ArrayList<AliasObject>>();
		tmpRoot.add(new ArrayList<AliasObject>());
		tmpRoot.get(0).add(n);
		// Change roots in the Alias Diagram
		aliasGraph.changeRoot(tmpRoot);

		//String[] r = n.resolveBinding().getKey().split("#");


		start (node.getType().toString(), node.getType().toString(), 0, this, actual, actualTypes);

		nodeInfoLastRoutine = new nodeInfo("");
		for (ArrayList<AliasObject> es: tmpRoot) {
			nodeInfoLastRoutine.newRoot();
			for (AliasObject o: es) {
				nodeInfoLastRoutine.addEdge(o, new Variable(""), o);
			}
		}

		stackCall.pop();
		// Change roots back in the Alias Diagram
		aliasGraph.changeBackRoot();
		Helpers.printStackAll(stackCall);

		return false;
	}

	public boolean visit(SimpleType node) {
		System.out.println ("SimpleType");
		System.out.println (node.getName());
		return true;
	}

	public boolean visit(QualifiedName node) {
		System.out.println ("QualifiedName: " + node.getFullyQualifiedName());
		System.out.println (">> " + node.getName());
		System.out.println (">> " + node.getQualifier());

		/**
		 * Assuming t.a
		 *   (i) retrieve t's pointing at (add 't' in case it is not already in)
		 *  (ii) reroot to 't'
		 * (iii) retrieve a's pointing at (add 'a' in case it is not already in)
		 *  (iv) reroot back
		 */

		nodeInfo left = getNodeInfo(false, node.getQualifier()); //(i)
		aliasGraph.changeRoot(left.getTargets()); // (ii)
		nodeInfo right = getNodeInfo(true, node.getName()); //(iii)
		aliasGraph.changeBackRoot();
		nodeInfoLastRoutine = right;
		return false;
	}

	public boolean visit(FieldAccess node) {
		System.out.println ("FieldAccess: " + node.getExpression() + "." + node.getName());

		System.out.println(">> " + node.getExpression());
		System.out.println(">> " + node.getName());
		/**
		 * Assuming m().a
		 *   (i) analyse 'm()'
		 *  (ii) reroot to 'm'
		 * (iii) analyse 'a'
		 *  (iv) reroot back
		 */
		nodeInfo left = getNodeInfo(false, node.getExpression()); //(i)
		aliasGraph.changeRoot(left.getTargets()); // (ii)
		nodeInfo right = getNodeInfo(true, node.getName()); //(iii)
		aliasGraph.changeBackRoot();
		nodeInfoLastRoutine = right;
		return false;
	}

	//nodeInfo before the last Routine is popped out of the Routine Stack
	nodeInfo nodeInfoLastRoutine;
	public boolean visit(MethodInvocation call) {
		System.out.println ("MethodInvocation");



		if (call.getExpression() == null) {//Unqualified call
			System.out.println("\tUnqualified Call");
			// Get the Declaring Method

			MethodDeclaration method = (MethodDeclaration) cu.findDeclaringNode(call.getName().resolveBinding());


			/**
			 * Handling Arguments
			 */
			// Get method's signature (used for the routineStack)
			// method signature
			Routine methodCall = routineSignature (method);


			//link formal to actual arguments (if any)
			for (int i=0;i<method.parameters().size();i++) {
				System.out.println("Formal Argument " + (i+1) + " : " + method.parameters().get(i));
				System.out.println("Actual Argument " + (i+1) + " : " + call.arguments().get(i));

				nodeInfo formal = new nodeInfo(((SingleVariableDeclaration)method.parameters().get(i)).getName().toString());
				methodCall.aliasObjectsArgument(formal);
				nodeInfo actual = getNodeInfo (false, (ASTNode)call.arguments().get(i));

				if (formal != null && actual != null) {
					aliasing (formal, actual);
				}else {
					System.out.println ("Either left or right was null");

				}
			}

			stackCall.push(methodCall);

			Helpers.printStackAll (stackCall);
			method.getBody().accept(this);


			assert stackCall.size() > 1;

			nodeInfoLastRoutine = null;
			if (currentRoutine().isFunction()) {
				nodeInfoLastRoutine = new nodeInfo(Const.RETURN);
				currentRoutine().aliasObjectsReturn (nodeInfoLastRoutine);
			}

			stackCall.pop();
		}else {//Qualified call
			System.out.println("\tQualified Call");


			/**
			 * According to the Rule: when analysing 
			 * void t(){
			 * 		v.w (a)
			 * } ...
			 * class T2{
			 * 	T b;
			 * 	void w (T v){
			 * 		b = v;
			 * 	}
			 * }
			 * 
			 *   (i) make sure 'v' is in the alias diagram
			 *  (ii) retrieve the objects 'v' is pointing at
			 * (iii) get w's information (i.e. formal arguments)
			 *  (iv) link formal to actual arguments
			 *  		(iii and iv are being performed later on)
			 *   (v) reroot the diagram to 'v'
			 *  (vi) Analyse w
			 * (vii) reroot back 
			 */

			// 'getNodeInfo' adds call.getExpression() to the Alias Diagram in case it does not
			//		exist. It also gets the 'pointingAt needed to change roots
			nodeInfo ni = getNodeInfo(false, call.getExpression()); // (i) and (ii)
			System.out.println(ni);
			nodeInfo[] actual = new nodeInfo[call.arguments().size()];
			String[] actualTypes = new String[actual.length];
			//Get actual arguments
			for (int i=0;i<call.arguments().size();i++) {
				System.out.println("Actual Argument " + (i+1) + " : " + call.arguments().get(i));
				// TODO: get rid of this workaround https://github.com/varivera/aliasing-java/issues/23
				actual[i] = getNodeInfo (false, (ASTNode)call.arguments().get(i));
				System.out.println(call.arguments().get(i).getClass());
				if (call.arguments().get(i) instanceof QualifiedName) {
					actualTypes[i] = ((QualifiedName)call.arguments().get(i)).getQualifier().resolveBinding().toString()
							.split(" ")[0];
					
				}else {
					actualTypes[i] = ((Expression)call.arguments().get(i)).resolveTypeBinding().getName();
				}
				
			}

			// Change roots in the Alias Diagram
			aliasGraph.changeRoot(ni.getTargets());

			System.out.println("|getName>> " + call.getName());
			System.out.println("|getExpression>> " + call.getExpression());



			//String[] r = n.resolveBinding().getKey().split("#");

			System.out.println("||>> " );

			start (call.getExpression().resolveTypeBinding().getName(), call.getName().toString(), 0, this, actual, actualTypes);

			nodeInfoLastRoutine = null;
			if (currentRoutine().isFunction()) {
				nodeInfoLastRoutine = new nodeInfo(Const.RETURN);
				currentRoutine().aliasObjectsReturn (nodeInfoLastRoutine);
			}

			stackCall.pop();
			// Change roots back in the Alias Diagram
			aliasGraph.changeBackRoot();
			Helpers.printStackAll(stackCall);
		}
		return false;
	}

	/**
	 * 
	 * @param n Node
	 * @return an identifier: a class variable, local variable or argument.
	 * TODO: to make use of the AST to get the information. 
	 */
	public Integer getTypeNode (SimpleName n) {

		String name = n.getIdentifier();

		System.out.println("name: " + name);

		// check first with the current information
		if (stackCall.peek().isArgument(name)) {
			System.out.println("Argument");
			return Const.ARGUMENT;
		}else if (stackCall.peek().isLocal(name)) {
			System.out.println("Local");
			return Const.LOCAL;
		}else if(n.resolveBinding() == null) { // call to another object, thus, the type is ATTRIBUTE
			return Const.ATTRIBUTE;
		}else {
			System.out.println("> " + n.resolveBinding());
			System.out.println("Other: " + n.resolveBinding().getKey());
			//FIXME: workaround
			//-> local variable only one #
			//-> variable zero #
			String[] r = n.resolveBinding().getKey().split("#");
			if (r.length == 2) {
				return Const.LOCAL;
			}else if (r.length == 1) {
				return Const.ATTRIBUTE;
			}
		}
		return null;
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
	
	public boolean visit(IfStatement node) {
		System.out.println ("IfStatement");
		globalCond.count();
		// the conditional is ignore
		node.getExpression().accept(this);
		
		// (i) start a new Conditional Control Structure in the Stack
		// (ii) Execute 'then' statement (storing deletions in the peek of the Stack)
		// 	(->) make sure that changes in the Alias Diagram has the corresponding computational Path 
		// (iii) Execute 'else' statement (storing deletions in the peek of the Stack)
		// (iv) remove from Alias Diagram the intersection of the peek of the Stack
		// (v) remove the Conditional ControlStructure from the Stack (transfer information if needed)
		
		stackControlStructures.add(new Conditional());
		stackControlStructures.peek().step();//(i)
		
		node.getThenStatement().accept(this); // (ii)
		
		//to delete
		System.out.println(stackControlStructures.peek());
		//to delete
		
		//(iii)
		stackControlStructures.peek().step();
		
		if (node.getElseStatement()!=null) {
			node.getElseStatement().accept(this); // (iv)
		}
		
		//to delete
		System.out.println(stackControlStructures.peek());
		//to delete
		
		//here
		
		//TODO: transfer if needed
		stackControlStructures.peek().stop();
		stackControlStructures.remove(); // (vii)
		return false;
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




	/*** START ****/




	public boolean visit(AnnotationTypeDeclaration node) {
		System.out.println ("AnnotationTypeDeclaration");
		return true;
	}

	public boolean visit(AnnotationTypeMemberDeclaration node) {
		System.out.println ("AnnotationTypeMemberDeclaration");
		return true;
	}

	public boolean visit(AnonymousClassDeclaration node) {
		System.out.println ("AnonymousClassDeclaration");
		return true;
	}

	public boolean visit(ArrayAccess node) {
		System.out.println ("ArrayAccess");
		return true;
	}

	public boolean visit(ArrayCreation node) {
		System.out.println ("ArrayCreation");
		return true;
	}

	public boolean visit(ArrayInitializer node) {
		System.out.println ("ArrayInitializer");
		return true;
	}

	public boolean visit(ArrayType node) {
		System.out.println ("ArrayType");
		return true;
	}

	public boolean visit(AssertStatement node) {
		System.out.println ("AssertStatement");
		return true;
	}

	public boolean visit(BlockComment node) {
		System.out.println ("BlockComment");
		return true;
	}

	public boolean visit(BooleanLiteral node) {
		System.out.println ("BooleanLiteral");
		return true;
	}

	public boolean visit(BreakStatement node) {
		System.out.println ("BreakStatement");
		return true;
	}

	public boolean visit(CastExpression node) {
		System.out.println ("CastExpression");
		return true;
	}

	public boolean visit(CatchClause node) {
		System.out.println ("CatchClause");
		return true;
	}

	public boolean visit(CharacterLiteral node) {
		System.out.println ("CharacterLiteral");
		return true;
	}

	public boolean visit(CompilationUnit node) {
		System.out.println ("CompilationUnit");
		return true;
	}

	public boolean visit(ConditionalExpression node) {
		System.out.println ("ConditionalExpression");
		return true;
	}

	public boolean visit(ConstructorInvocation node) {
		System.out.println ("ConstructorInvocation");
		return true;
	}

	public boolean visit(ContinueStatement node) {
		System.out.println ("ContinueStatement");
		return true;
	}

	public boolean visit(CreationReference node) {
		System.out.println ("CreationReference");
		return true;
	}

	public boolean visit(Dimension node) {
		System.out.println ("Dimension");
		return true;
	}

	public boolean visit(DoStatement node) {
		System.out.println ("DoStatement");
		return true;
	}

	public boolean visit(EmptyStatement node) {
		System.out.println ("EmptyStatement");
		return true;
	}

	public boolean visit(EnhancedForStatement node) {
		System.out.println ("EnhancedForStatement");
		return true;
	}

	public boolean visit(EnumConstantDeclaration node) {
		System.out.println ("EnumConstantDeclaration");
		return true;
	}

	public boolean visit(EnumDeclaration node) {
		System.out.println ("EnumDeclaration");
		return true;
	}

	public boolean visit(ExportsDirective node) {
		System.out.println ("ExportsDirective");
		return true;
	}

	public boolean visit(ExpressionMethodReference node) {
		System.out.println ("ExpressionMethodReference");
		return true;
	}

	public boolean visit(FieldDeclaration node) {
		System.out.println ("FieldDeclaration");
		return true;
	}

	public boolean visit(ForStatement node) {
		System.out.println ("ForStatement");
		return true;
	}

	public boolean visit(InfixExpression node) {
		System.out.println ("InfixExpression");
		return true;
	}

	public boolean visit(Initializer node) {
		System.out.println ("Initializer");
		return true;
	}

	public boolean visit(InstanceofExpression node) {
		System.out.println ("InstanceofExpression");
		return true;
	}

	public boolean visit(IntersectionType node) {
		System.out.println ("IntersectionType");
		return true;
	}

	public boolean visit(LabeledStatement node) {
		System.out.println ("LabeledStatement");
		return true;
	}


	public boolean visit(LambdaExpression node) {
		System.out.println ("LambdaExpression");
		return true;
	}

	public boolean visit(LineComment node) {
		System.out.println ("LineComment");
		return true;
	}


	public boolean visit(MarkerAnnotation node) {
		System.out.println ("MarkerAnnotation");
		return true;
	}


	public boolean visit(MemberRef node) {
		System.out.println ("MemberRef");
		return true;
	}


	public boolean visit(MemberValuePair node) {
		System.out.println ("MemberValuePair");
		return true;
	}


	public boolean visit(MethodRef node) {
		System.out.println ("MethodRef");
		return true;
	}


	public boolean visit(MethodRefParameter node) {
		System.out.println ("MethodRefParameter");
		return true;
	}

	public boolean visit(Modifier node) {
		System.out.println ("Modifier");
		return true;
	}

	public boolean visit(ModuleDeclaration node) {
		System.out.println ("ModuleDeclaration");
		return true;
	}

	public boolean visit(ModuleModifier node) {
		System.out.println ("ModuleModifier");
		return true;
	}

	public boolean visit(NameQualifiedType node) {
		System.out.println ("NameQualifiedType");
		return true;
	}

	public boolean visit(NormalAnnotation node) {
		System.out.println ("NormalAnnotation");
		return true;
	}

	public boolean visit(NullLiteral node) {
		System.out.println ("NullLiteral");
		return true;
	}

	public boolean visit(NumberLiteral node) {
		System.out.println ("NumberLiteral");
		return true;
	}

	public boolean visit(OpensDirective node) {
		System.out.println ("OpensDirective");
		return true;
	}

	public boolean visit(ParameterizedType node) {
		System.out.println ("ParameterizedType");
		return true;
	}

	public boolean visit(ParenthesizedExpression node) {
		System.out.println ("ParenthesizedExpression");
		return true;
	}

	public boolean visit(PostfixExpression node) {
		System.out.println ("PostfixExpression");
		return true;
	}

	public boolean visit(PrefixExpression node) {
		System.out.println ("PrefixExpression");
		return true;
	}

	public boolean visit(ProvidesDirective node) {
		System.out.println ("ProvidesDirective");
		return true;
	}

	public boolean visit(PrimitiveType node) {
		System.out.println ("PrimitiveType");
		return true;
	}

	public boolean visit(QualifiedType node) {
		System.out.println ("QualifiedType");
		return true;
	}

	public boolean visit(RequiresDirective node) {
		System.out.println ("RequiresDirective");
		return true;
	}


	public boolean visit(SingleMemberAnnotation node) {
		System.out.println ("SingleMemberAnnotation");
		return true;
	}


	public boolean visit(StringLiteral node) {
		System.out.println ("StringLiteral");
		return true;
	}

	public boolean visit(SuperConstructorInvocation node) {
		System.out.println ("SuperConstructorInvocation");
		return true;
	}

	public boolean visit(SuperFieldAccess node) {
		System.out.println ("SuperFieldAccess");
		return true;
	}

	public boolean visit(SuperMethodInvocation node) {
		System.out.println ("SuperMethodInvocation");
		return true;
	}

	public boolean visit(SuperMethodReference node) {
		System.out.println ("SuperMethodReference");
		return true;
	}

	public boolean visit(SwitchCase node) {
		System.out.println ("SwitchCase");
		return true;
	}

	public boolean visit(SwitchStatement node) {
		System.out.println ("SwitchStatement");
		return true;
	}

	public boolean visit(SynchronizedStatement node) {
		System.out.println ("SynchronizedStatement");
		return true;
	}


	public boolean visit(TagElement node) {
		System.out.println ("TagElement");
		return true;
	}


	public boolean visit(TextElement node) {
		System.out.println ("TextElement");
		return true;
	}


	public boolean visit(ThisExpression node) {
		System.out.println ("ThisExpression");
		return false;
	}

	public boolean visit(ThrowStatement node) {
		System.out.println ("ThrowStatement");
		return true;
	}

	public boolean visit(TryStatement node) {
		System.out.println ("TryStatement");
		return true;
	}

	public boolean visit(TypeLiteral node) {
		System.out.println ("TypeLiteral");
		return true;
	}

	public boolean visit(TypeMethodReference node) {
		System.out.println ("TypeMethodReference");
		return true;
	}

	public boolean visit(TypeParameter node) {
		System.out.println ("TypeParameter");
		return true;
	}

	public boolean visit(UnionType node) {
		System.out.println ("UnionType");
		return true;
	}

	public boolean visit(UsesDirective node) {
		System.out.println ("UsesDirective");
		return true;
	}

	public boolean visit(VariableDeclarationExpression node) {
		System.out.println ("VariableDeclarationExpression");
		return true;
	}

	public boolean visit(WhileStatement node) {
		System.out.println ("WhileStatement");
		return true;
	}

	public boolean visit(WildcardType node) {
		System.out.println ("WildcardType");
		return true;
	}




	/*** END ****/



	/**
	 * 
	 * @return the graph to be used by GraphViz including
	 * 		signature of methods in stackCall 
	 */
	public String toGraphVizAll () {
		return Helpers.toGraphAll(aliasGraph.getRoots(), stackCall);
	}
	
	
	/**
	 * SanityCheck on Alias Diagram predecessors
	 */
	public boolean predCheck() {
		return aliasGraph.predecesorsOK();
	}
	
	/**
	 * Aliasing query 
	*/
	public boolean aliased (String p1, String p2) {
		return aliasGraph.aliased(
				p1.contains(".")?p1.split("."):new String[] {p1}, 
				p2.contains(".")?p2.split("."):new String[] {p2});
	}


	public static void main(String[] args) throws FileNotFoundException, IOException {

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

		String classAnalyse = "ControlStruc";
		String methodAnalyse = "cond5";

		long start1 = System.currentTimeMillis();
		//Init
		AliasAnalysis v = new AliasAnalysis (sourcePath, unitName, classpath, classAnalyse);
		long start2 = System.currentTimeMillis();
		v.start(classAnalyse, methodAnalyse, 0, null, null, null);
		//End
		long end = System.currentTimeMillis();
		
		//test pred
		System.out.println("pred Check: " + v.predCheck());
		//end test pred
		//String g = v.aliasGraph.toGraphViz();
		String g = v.toGraphVizAll();
		Helpers.createDot (g, "test", "source");
		System.out.println("\nGraphViz: ");
		System.out.println(g);
		System.out.println("==========");
		System.out.println(v.aliasGraph.toSetEdges());
		float time1 = (end - start1) / 100F;
		float time2 = (end - start2) / 100F;
		System.out.println("Time Analysis (with AST generation): "+time1 + " seconds");
		System.out.println("Time Analysis (AST as input): "+time2+ " seconds");
		
	}

}