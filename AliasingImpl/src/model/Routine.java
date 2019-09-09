package model;

import exceptions.AliasException;
import structures.Variable;
import structures.helpers.Const;
import structures.helpers.Id;

/**
 * 
 * This class contains all information about
 * the routine being analysed. Name, signature,
 * Local variables, changes on the graph
 * 
 * @author Victor Rivera (victor.rivera@anu.edu.au)
 *
 */

public class Routine {
	
	/**
	 * Routine name
	 */
	public String name;
	
	/**
	 * return type
	 */
	private AliasObject returnType;
	
	/**
	 * Routine's arguments
	 */
	private AliasObject formalArguments;
	
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
		formalArguments = new AliasObject(this.id.getId());
		locals = new AliasObject(this.id.getId());
		returnType = new AliasObject(this.id.getId());
	}
	
	/**
	 * adds an argument of the routine
	 * @param name of the argument
	 */
	public void addArgument (String name) {
		formalArguments.initValMap(name, null, id.getId());
	}
	
	/**
	 * adds a local variable of the routine
	 * @param name of the argument
	 */
	public void addLocalVariable (String name) {
		//TODO check if there is a need to use 'addMap'
		locals.initValMap(name, null, id.getId());
	}
	
	/**
	 * updates the name of the tag, from oldKey to newKey
	 * @param oldKey to be updated from
	 * @param newKey to be updated to
	 * @throws AliasException 
	 */
	public void updateKeyLocalVariable (Variable oldKey, Variable newKey) throws AliasException {
		locals.updateInHold(oldKey, newKey);
	}
	
	/**
	 * Is the Alias Object from localVariables waiting for info to be updated?
	 * @param oldKey
	 * @return
	 */
	public boolean isUpdateNeeded (String oldKey){
		return locals.isUpdateNeeded(new Variable(oldKey, null));
	}
	
	/**
	 * adds the return type of the routine
	 * @param type of the return value
	 */
	public void setReturnType () {
		returnType.addMap(Const.RETURN, null, id.getId());
	}
	
	/**
	 * 
	 */
	public AliasObject[] getSignatureObjects () {
		return new AliasObject[] {returnType, formalArguments, locals};
		
	}
	
	/**
	 * 
	 * @param a
	 * @return true if 'a' is an argument of current routine.
	 * 		False otherwise 
	 */
	public boolean isArgument (String a) {
		//arguments exist only temporarily 
		return formalArguments.isIn(new Variable(a, null));
	}
	
	/**
	 * 
	 * @param a
	 * @return true if 'a' has been defined as a local variable in
	 * 		the current routine. False otherwise.
	 */
	public boolean isLocal (String a) {
		//locals exist only temporarily 
		return locals.isIn(new Variable(a, null));
	}
	
	/**
	 * 
	 * @return True if the current routine is a function.
	 * 		False otherwise
	 */
	public boolean isFunction () {
		return !returnType.succ.get(new Variable(Const.RETURN, null)).equals(Const.VOID);
	}
	
	/**
	 * 
	 * @param ref reference to update the nodeInfo
	 * update: the list of objects associated to 'ref.tag', from the
	 * 			arguments of the current routine
	 */
	public void aliasObjectsArgument (nodeInfo ref)  {
		ref.addObjects(formalArguments.succ.get(ref.tag));
	}
	
	/**
	 * 
	 * @param ref reference to update the nodeInfo
	 * update: the list of objects associated to 'ref.tag', from the
	 * 			locals of the current routine
	 */
	public void aliasObjectsLocal (nodeInfo ref)  {
		ref.addObjects(locals.succ.get(ref.tag));
	}
	
	/**
	 * 
	 * @param ref reference to update the nodeInfo
	 * update: the list of objects associated to 'ref.tag', from the
	 * 			return value of the current routine
	 */
	public void aliasObjectsReturn (nodeInfo ref)  {
		ref.addObjects(returnType.succ.get(new Variable(Const.RETURN, null)));
	}
	
	/**
	 * printable version of the Routine
	 */
	public String toString() {
		StringBuilder res = new StringBuilder();
		res.append("T " + name + " (");
		for (Variable arg: formalArguments.succ.keySet()) {
			if (formalArguments.succ.get(arg).size() > 0) {
				res.append(" " + arg + ", ");
			}
		}
		if (formalArguments.succ.size() > 0 && res.lastIndexOf(", ") >= 0) {
			res.replace(res.lastIndexOf(", "), res.lastIndexOf(", ")+2, "");
		}
		
		res.append(")");
		return res.toString();
	}
}
