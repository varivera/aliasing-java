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
 * to the Alias Diagram after visiting each instruction in the code
 * 
 * @author Victor Rivera (victor.rivera@anu.edu.au)
 *
 */

public class VisitorTest extends ASTVisitor {

	// Compilation Unit
	private CompilationUnit cu;
	private String method;

	public boolean visit (MethodDeclaration node) {
		System.out.println ("MethodDeclaration: " + node.getName());

		node.getBody().accept(this);
		return false;
	}


	public VisitorTest(ASTParser parser) {
		cu = (CompilationUnit) parser.createAST(null);
	}

	public void start (String className, String methodName, int point) {
		assert cu != null;
		method = methodName;
		cu.accept(this);
	}

	public boolean visit (TypeDeclaration node) {
		System.out.println ("TypeDeclaration: " + node.getName().toString());
		String className = node.getName().toString();
		HashMap<String, 
		HashMap<String, MethodDeclaration>> methodsClass = new HashMap<String, 
		HashMap<String, MethodDeclaration>>();
		if (!methodsClass.containsKey(className)) {
			methodsClass.put(node.getName().toString(), new HashMap<String, MethodDeclaration>());
			for (int i=0;i<node.getMethods().length;i++) {
				methodsClass.get(className).put(node.getMethods()[i].getName().toString(), node.getMethods()[i]);
			}
		}


		// finding the right method
		MethodDeclaration m = methodsClass.get(className).get(method);
		if (m != null) {
			m.getBody().accept(this);
		}else {

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

		return true;
	}

	/**
	 * Return statement
	 */
	public boolean visit (ReturnStatement node) {
		System.out.println("ReturnStatement " + node);
		System.out.println(">> " + node.getExpression());
		System.out.println(">> " + node.getExpression().getClass());


		return false;
	}

	public boolean visit (Assignment node) {
		System.out.println("Assignment: " + node);
		return false;
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


		
		return false;
	}

	public boolean visit (TypeDeclarationStatement node) {
		System.out.println("TypeDeclarationStatement: " + node);
		return true;
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
		System.out.println ("AST MethodInvocation");
		System.out.println (">> " + (node.getExpression()==null));
		if (node.getExpression() != null) {


			//ASTNode d = cu.findDeclaringNode(
			//		node.getExpression().resolveTypeBinding());
			System.out.println("|QC>>");
			System.out.println("|getName>> " + node.getName());
			System.out.println("|getExpression>> " + node.getExpression());

			
			String sourcePath = "";
			String unitName = "";
			String[] classpath = null;
			sourcePath = "/home/varivera/Desktop/VR/work/research/aliasing-java/AliasTestProject/src/Basics/";
			unitName = "T.java";
			classpath = new String[]{"/Library/Java/JavaVirtualMachines/jdk1.8.0_151.jdk/Contents/Home/jre/librt.jar"};
			ASTParser parser = ASTParser.newParser(AST.JLS11);
			char[] fileContent;
			try {
				fileContent = Helpers.getFileContent(sourcePath+unitName).toCharArray();
				parser.setKind(ASTParser.K_COMPILATION_UNIT);
				parser.setResolveBindings(true);
				Map options = JavaCore.getOptions();
				parser.setCompilerOptions(options);
				parser.setBindingsRecovery(true);
				parser.setUnitName(unitName);
				String[] sources = {sourcePath}; 
				parser.setEnvironment(classpath, sources, new String[] { "UTF-8"}, true);
				parser.setSource(fileContent);
				VisitorTest v = new VisitorTest (parser);
				v.start("T", "remoteCall", 0);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			

			//node.getExpression().accept(this);
			System.out.println("<<QC|");

		}else{
			System.out.println("|UC>>");
			cu.findDeclaringNode(node.getName().resolveBinding()).accept(this);
			System.out.println("<<UC|");
		}
		return false;
	}


	public boolean visit (SimpleName node) {
		System.out.println("SimpleName: " + node);
		ASTNode d = cu.findDeclaringNode(node.resolveBinding());
		d.accept(this);
		System.out.println(">>>> " + d);
		return false;

	}


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
		}else if (System.getProperty("os.name").contains("Linux")) {
			sourcePath = "/home/varivera/Desktop/VR/work/research/aliasing-java/AliasTestProject/src/Basics/";
			unitName = "QualifiedCall.java";
			classpath = new String[]{"/Library/Java/JavaVirtualMachines/jdk1.8.0_151.jdk/Contents/Home/jre/librt.jar"};
		}




		//System.out.println(t.getFileContent(source));
		ASTParser parser = ASTParser.newParser(AST.JLS11);
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

		//Init
		long start = System.currentTimeMillis();
		VisitorTest v = new VisitorTest (parser);
		v.start("QualifiedCall", "unq1", 0);
		//End
		long end = System.currentTimeMillis();
		//String g = v.aliasGraph.toGraphViz();

	}

}