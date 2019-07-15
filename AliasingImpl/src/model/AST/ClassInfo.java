package model.AST;

import org.eclipse.jdt.core.dom.CompilationUnit;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 * Holds a class static information. 
 * 
 * @author Victor Rivera (victor.rivera@anu.edu.au)
 *
 */

public class ClassInfo {

	/**
	 *  Class name
	 */
	String className;
	
	/**
	 * Compilation Unit
	 */
	private CompilationUnit cu;

	/**
	 * methods' formal arguments
	 */
	private HashMap<String, ArrayList<String>> methods;
	
	/**
	 * methods' function information.
	 *  	True if methods.get (x) is a function, False otherwise
	 */
	private HashMap<String, Boolean> funcs;
	
	public ClassInfo(String className, CompilationUnit cu) {
		this.className = className;
		this.cu = cu;
		methods = new HashMap<String, ArrayList<String>>();
		funcs = new HashMap<String, Boolean>();
	}
	
	/**
	 * @return s the compilation unit (AST) of the class
	 */
	public CompilationUnit cu () {
		return cu;
	}
	
	/**
	 * 
	 * @param m is the method name
	 * @return s the list of formal arguments of 'm'
	 */
	public ArrayList<String> formalArguments (String m){
		return methods.get(m);
	}
	
	//is 'm' a function?
	/**
	 * @param m is the name of the method
	 * @return whether 'm' is a function or not
	 */
	public boolean isFunction (String m) {
		return funcs.get(m);
	}
	
	
	/**
	 * adds a method's information to the class
	 * @param m is the name of the method to be added
	 * @param formals is the list of formal arguments' names
	 * @param func is True if the added method is a function, False if
	 * 			it is a procedure
	 */
	public void addMethod (String m, ArrayList<String> formals, boolean func) {
		//for (String )
		
		assert funcs.size() == methods.size();
	}
	

}