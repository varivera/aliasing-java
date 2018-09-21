package model.AST;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
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
import structures.graphRep.SetEdges;
import structures.helpers.Const;
import structures.helpers.Helpers;
import structures.helpers.Id;

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
	private Deque<Routine> stackCall;
	
	
	// Compilation Unit
	private CompilationUnit cu;
	
	// Method to be analysed
	String method;
	
	/**
	 * For each class used in the analysis, methods declaration
	 * will be stored. This is in case of a (un)qualified call.
	 * JDT might have some mechanisms to manage this differently
	 */
	HashMap<String, 
		HashMap<String, MethodDeclaration>> methodsClass;
	
	/**
	 * Id generator: to give a unique identifier to Alias Objects
	 */
	public final Id idGen;
	
	public AliasAnalysis(ASTParser parser) {
		cu = (CompilationUnit) parser.createAST(null);
		idGen = new Id();
	}
	
	/**
	 * Starts the analysis of method 'methodName' in class
	 * 'className' stopping at point 'point'.
	 * Initial implementation will consider the analysis of 
	 * all method (i.e. point is equal to method exit)
	 */
	public void start (String className, String methodName, int point) {
		assert (cu != null);
		aliasGraph = new AliasDiagram (className, idGen);
		stackCall = new ArrayDeque <Routine>();
		method = methodName;
		methodsClass = new HashMap<String,HashMap<String, MethodDeclaration>>();
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
		String className = node.getName().toString();
		if (!methodsClass.containsKey(className)) {
			methodsClass.put(node.getName().toString(), new HashMap<String, MethodDeclaration>());
			for (int i=0;i<node.getMethods().length;i++) {
				methodsClass.get(className).put(node.getMethods()[i].getName().toString(), node.getMethods()[i]);
			}
		}
		
		
		// finding the right method
		MethodDeclaration m = methodsClass.get(className).get(method);
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
		
		ArrayList<nodeInfo> actualArguments = null;
		//actual arguments, in case this is not the first call of the Method
		if (!stackCall.isEmpty()) {
			actualArguments = currentRoutine().actualArguments;
		}
		
		
		// method signature
		stackCall.push(new Routine (node.getName().toString(), idGen));
		
		Helpers.printStack (stackCall);
		
		currentRoutine().setReturnType(node.getReturnType2().toString());
		
		for (int i=0;i<node.parameters().size();i++) {
			((SingleVariableDeclaration) node.parameters().get(i)).accept(this);
		}
		
		// aliased actual to formal arguments
		if (actualArguments != null) {
			assert (actualArguments.size() == node.parameters().size());
			for (int i=0;i<node.parameters().size();i++) {
				if (actualArguments.get(i) != null) {
					//aliased
					
 					nodeInfo left = new nodeInfo (((SingleVariableDeclaration) node.parameters().get(i)).getName().toString());
					currentRoutine().aliasObjectsArgument(left);
					
					aliasing (left, actualArguments.get(i));
				}
			}
		}
		
		System.out.println (currentRoutine());
		node.getBody().accept(this);
		
		/**
		 * For testing/debugging purposes, the first call is not delete
		 */
		if (stackCall.size() > 1) {
			stackCall.pop();
		}
		
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
	
	/**
	 * Only deals with local variable declaration: the analysis is done
	 * over methods, methods do not define class fields.
	 */
	public boolean visit (VariableDeclarationStatement node) {
		System.out.println("VariableDeclarationStatement: " + node);
		//value of the local name variable is not available. The link 
		//is created and updated down the AST tree.
		currentRoutine().addLocalVariable("?NoInfo?", node.getType().toString());
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
		left = getNodeInfo(null, node.getLeftHandSide());
		System.out.println("right");
		if (left != null) {
			right = getNodeInfo(null, node.getRightHandSide());
		}else {
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
		assert (left != null && right != null);
		assert (left.pointingAt.size() == right.pointingAt.size());
		assert (left.pointingAt.size() >= 1);
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
		currentRoutine().addArgument(node.getName().toString(), node.getType().toString());
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
				currentRoutine().updateKeyLocalVariable("?NoInfo?", node.getName().toString());
				
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
				
				nodeInfo right = new nodeInfo (n.toString());
				
				if (currentRoutine().isArgument(right.tag)) {
					//Argument
					currentRoutine().aliasObjectsArgument(right);   
				}else if (currentRoutine().isLocal(right.tag)) {
					//Local
					currentRoutine().aliasObjectsLocal(right);
				}else { // it is indeed a variable
					// adding information to the alias graph in case it has not been added
					ITypeBinding typeBinding = n.resolveTypeBinding();
					
					aliasGraph.initEdge (n.toString(), typeBinding.getName());
					
					aliasGraph.aliasObjects(right);
				}
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
	 * @param target represents qualified calls (null otherwise)
	 * @param node the ASTNode being analysed
	 * @return the information about the node. For instance, if the
	 * 		node is a variable, the nodeInfo will contain the alias
	 * 		object that variable is pointing at.
	 */
	public nodeInfo getNodeInfo (AliasObject target, ASTNode node) {
		nodeInfo res = null;
		
		if (node instanceof SimpleName) {
				// SimpleName can represent class variables, local variables, arguments
			SimpleName n = (SimpleName) node;
			ITypeBinding typeBinding = n.resolveTypeBinding();
			System.out.println("typeBinding: " + typeBinding.getName());
			
			// Ignore native types (they are not references e.g. int, float ...)
			if (typeBinding.isPrimitive()) {
				
			}else {
				
				Integer type = getTypeNode (n);
				
				if (type.equals(Const.ATTRIBUTE)) {
					// adding information to the alias graph in case it has not been added
					aliasGraph.initEdge (n.toString(), typeBinding.getName());
					
					res = new nodeInfo (n.toString());
					aliasGraph.aliasObjects(res);
				}else if (type.equals(Const.LOCAL)){
					// adding information to the alias graph in case it has not been added
					//currentRoutine().addLocalVariable(n.toString(), typeBinding.getName());
					//Note: local variables can only be added through variable declaration
					
					res = new nodeInfo (n.toString());
					currentRoutine().aliasObjectsLocal(res);
				}else if (type.equals(Const.ARGUMENT)){
					// adding information to the alias graph in case it has not been added
					
					res = new nodeInfo (n.toString());
					currentRoutine().aliasObjectsArgument(res);
				}
			}
		}
		
		return res;
	}
	
	public boolean visit(ClassInstanceCreation node) {
		System.out.println ("ClassInstanceCreation");
		return true;
	}
	
	public boolean visit(SimpleType node) {
		System.out.println ("SimpleType");
		System.out.println (node.getName());
		
		return true;
	}
	
	public boolean visit(MethodInvocation node) {
		System.out.println ("MethodInvocation");
		// Unqualified call
		MethodDeclaration method = methodsClass.get(node.resolveMethodBinding().getDeclaringClass().getName().toString()).get(node.resolveMethodBinding().getName());
		
		//store the value of actual arguments to be aliased in 
		//MethodDeclaration
		for (int i=0;i<node.arguments().size();i++){
			System.out.println(node.arguments().get(i).toString());
			System.out.println(node.arguments().get(i).getClass());
			
			if (node.arguments().get(i) instanceof SimpleName) {
				//it is either a local variable, an argument or a class attribute
				String actualArg = node.arguments().get(i).toString();
				nodeInfo nodeInfo = new nodeInfo(actualArg);
				if (currentRoutine().isLocal(actualArg)) {
					//Local
					currentRoutine().aliasObjectsLocal(nodeInfo);
				}if (currentRoutine().isArgument(actualArg)) {
					//Argument
					currentRoutine().aliasObjectsArgument(nodeInfo);   
				} else { // it is a variable
					// adding information to the alias graph in case it has not been added
					System.out.println ("c: " + node.arguments().get(i).getClass());
					
					ITypeBinding typeBinding = ((SimpleName)node.arguments().get(i)).resolveTypeBinding();
					
					aliasGraph.initEdge (actualArg, typeBinding.getName());
					
					aliasGraph.aliasObjects(nodeInfo);
				}
				
				currentRoutine().addActualArgument(nodeInfo);
			}else if (node.arguments().get(i) instanceof InfixExpression) {//it cannot be aliased
				currentRoutine().addNullActualArgument();
			}else {
				//TODO: implement the case where the argument is a function
				currentRoutine().addNullActualArgument();
			}
		}
		
		method.accept(this);
		// delete the actual arguments
		currentRoutine().restoreActualArgument();
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
		// check first with the current information
		if (stackCall.peek().isArgument(name)) {
			return Const.ARGUMENT;
		}else if (stackCall.peek().isLocal(name)) {
			return Const.LOCAL;
		}else {
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

	public boolean visit(FieldAccess node) {
		System.out.println ("FieldAccess");
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

	public boolean visit(IfStatement node) {
		System.out.println ("IfStatement");
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

	public boolean visit(QualifiedName node) {
		System.out.println ("QualifiedName");
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
		return true;
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
		ASTParser parser = ASTParser.newParser(AST.JLS10);
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
		parser.setSource(fileContent);
		
		AliasAnalysis v = new AliasAnalysis (parser);
		//v.start("Basic", "localArg1", 0);
		v.start("Basic", "unq_call_arg", 0);
		
		//String g = v.aliasGraph.toGraphViz();
		String g = v.toGraphVizAll();
		Helpers.createDot (g, "test", "source");
		System.out.println("\nGraphViz: ");
		System.out.println(g);
		System.out.println("==========");
		System.out.println(v.aliasGraph.toSetEdges());
		
	}

}