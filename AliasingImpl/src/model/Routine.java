package model;

import java.util.ArrayList;

/**
 * 
 * This class contains all information about
 * the routine being analysed. Name, signature,
 * Local variables, changes on the graph
 * 
 * @author Victor Rivera
 *
 */

public class Routine {
	
	/**
	 * Routine name
	 */
	private String name;
	
	/**
	 * return type
	 */
	private Variable returnType;
	
	/**
	 * Routine's arguments
	 */
	private ArrayList<Variable> arguments;
	
	/**
	 * Routine's local variables
	 */
	private ArrayList<Variable> local;
	
	public Routine(String name) {
		this.name = name;
		arguments = new ArrayList <Variable>();
		local = new ArrayList<Variable>();
	}
	
	/**
	 * adds an argument of the routine
	 * @param name of the argument
	 * @param type of the argument
	 */
	public void addArgument (String name, String type) {
		arguments.add(new Variable (name, type));
	}
	
	/**
	 * adds a local variable of the routine
	 * @param name of the argument
	 * @param type of the argument
	 */
	public void addLocalVariable (String name, String type) {
		local.add(new Variable (name, type));
	}
	
	/**
	 * adds the return type of the routine
	 * @param type of the return value
	 */
	public void setReturnType (String type) {
		returnType = new Variable ("return", type);
	}
	
	/**
	 * printable version of the Routine
	 */
	public String toString() {
		StringBuilder res = new StringBuilder();
		res.append(returnType.type + " " + name + " (");
		for (int i=0;i<arguments.size();i++) {
			res.append(arguments.get(i));
			if (i+1<arguments.size()) {
				res.append(", ");
			}
		}
		res.append(")");
		return res.toString();
	}
}
