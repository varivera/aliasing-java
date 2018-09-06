package model;


import structures.helpers.Id;

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
	private AliasObject returnType;
	
	/**
	 * Routine's arguments
	 */
	private AliasObject arguments;
	
	/**
	 * for id generation
	 */
	private Id id;
	
	/**
	 * Routine's local variables
	 */
	private AliasObject locals;
	
	public Routine(String name, Id id) {
		this.name = name;
		this.id = id;
		arguments = new AliasObject(name+" args", this.id.getId());
		locals = new AliasObject(name+" loc", this.id.getId());
		returnType = new AliasObject(name+" r", this.id.getId());
	}
	
	/**
	 * adds an argument of the routine
	 * @param name of the argument
	 * @param type of the argument
	 */
	public void addArgument (String name, String type) {
		arguments.initValMap(name, type, id.getId());
	}
	
	/**
	 * adds a local variable of the routine
	 * @param name of the argument
	 * @param type of the argument
	 */
	public void addLocalVariable (String name, String type) {
		//TODO check if there is a need to use 'addMap'
		locals.initValMap(name, type, id.getId());
	}
	
	/**
	 * adds the return type of the routine
	 * @param type of the return value
	 */
	public void setReturnType (String type) {
		returnType.addMap("return", type, id.getId());
	}
	
	/**
	 * 
	 */
	public AliasObject[] getSignatureObjects () {
		return new AliasObject[] {returnType, arguments, locals};
		
	}
	
	/**
	 * 
	 * @param a
	 * @return true if 'a' is an argument of current routine.
	 * 		False otherwise 
	 */
	public boolean isArgument (String a) {
		return arguments.mapping.containsKey(a);
	}
	
	/**
	 * 
	 * @param a
	 * @return true if 'a' has been defined as a local variable in
	 * 		the current routine. False otherwise.
	 */
	public boolean isLocal (String a) {
		return locals.mapping.containsKey(a);
	}
	
	/**
	 * 
	 * @param ref reference to update the nodeInfo
	 * update: the list of objects associated to 'ref.tag', from the
	 * 			arguments of the current routine
	 */
	public void aliasObjectsArgument (nodeInfo ref)  {
		ref.addObjects(arguments.mapping.get(ref.tag));
	}
	
	/**
	 * 
	 * @param ref reference to update the nodeInfo
	 * update: the list of objects associated to 'ref.tag', from the
	 * 			locals of the current routine
	 */
	public void aliasObjectsLocal (nodeInfo ref)  {
		ref.addObjects(locals.mapping.get(ref.tag));
	}
	
	/**
	 * printable version of the Routine
	 */
	public String toString() {
		StringBuilder res = new StringBuilder();
		res.append(returnType.mapping.get("return").get(0).typeName() + " " + name + " (");
		for (String arg: arguments.mapping.keySet()) {
			res.append(arguments.mapping.get(arg).get(0).typeName() + " " + arg + ", ");
		}
		if (arguments.mapping.size() > 0) {
			res.replace(res.lastIndexOf(", "), res.lastIndexOf(", ")+2, "");
		}
		
		res.append(")");
		return res.toString();
	}
}
