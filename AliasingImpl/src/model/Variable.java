package model;

/**
 * Information about variables: Name and Type.
 * Types are stored as Strings (this implementation
 * relies on the Java Type System
 * 
 * @author Victor Rivera (victor.rivera@anu.edu.au)
 *
 */

public class Variable {

	public String name;
	public String type;
	
	public Variable (String name, String type) {
		this.name = name;
		this.type = type;
	}
	
	public String toString () {
		return type + " " + name;
	}
}
