package model.AST;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

public class AliasAnalyzer {

	public static void main(String[] args) throws FileNotFoundException, IOException {
		/*String source = "source/sourceClass.java";
		AliasAnalyzer t = new AliasAnalyzer();
		System.out.println(t.getFileContent(source));
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		char[] fileContent = t.getFileContent(source).toCharArray();

		parser.setSource(fileContent);
		
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		
		AliasAnalysis v = new AliasAnalysis (parser);
		v.start("sourceClass","test",0);
		
		
		/*CompilationUnit cu = (CompilationUnit) parser.createAST(null);
		
		
		cu.accept(new ASTVisitor () {
			public boolean visit (VariableDeclarationFragment node) {
				SimpleName name = node.getName();
				int lineNumber = cu.getLineNumber(name.getStartPosition());
				System.out.println("Name: " + name.toString());
				System.out.println("Line: " + lineNumber);
				System.out.println("----------------------------");
				return false;
			}
			
		});*/
		

	}

}