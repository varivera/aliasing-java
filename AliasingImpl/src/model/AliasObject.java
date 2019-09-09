package model;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import exceptions.AliasException;
import structures.Variable;

/**
 * 
 * AliasObject represents a possible object computation
 * at run-time. AliasObjects are Nodes of the 
 * Alias Diagram.
 * 
 * @author Victor Rivera  (victor.rivera@anu.edu.au)
 *
 */

public class AliasObject {
	
	/**
	 * Successors of the AliasObject
	 * mapping of the AliasObject. For example,
	 * AliasObject could be the variables of a class, or 
	 * arguments of a routine
	 */
	public Map<Variable, ArrayList<AliasObject>> succ;
	
	/**
	 * Predecessor of the AliasObject
	 */
	public Map<Variable, ArrayList<AliasObject>> pred;
	
	public AliasObject(int id) {
		succ = new HashMap<Variable, ArrayList<AliasObject>>();
		pred = new HashMap<Variable, ArrayList<AliasObject>>();
		setVisited(false);
		this.id = id;
	}
	
	/**
	 * Add 'tag' to the map of the current object
	 * @param mapName
	 * @param compP: computational path
	 * @param id 
	 */
	public void addMap (String mapName, int[] compP, int id)  {
		Variable key = new Variable (mapName, compP);
		if (!succ.containsKey(key)) {
			succ.put (key, new ArrayList<AliasObject>());
		}
		AliasObject newO = new AliasObject (id);
		succ.get(key).add(newO);
		//update predecessor
		newO.pred.put(key, new ArrayList<AliasObject>());
		newO.pred.get(key).add(this);
	}
	
	/**
	 * Add 'tag' to the mapping of the current
	 * object only if it was not there already
	 * @param mapName
	 * @param compP: computational path
	 * @param id 
	 */
	public void initValMap (String mapName, int[] compP, int id)  {
		Variable key = new Variable (mapName, compP);
		if (!succ.containsKey(key)) {
			succ.put (key, new ArrayList<AliasObject>());
			AliasObject newO = new AliasObject (id);
			succ.get(key).add(newO);
			//update predecessor
			newO.pred.put(key, new ArrayList<AliasObject>());
			newO.pred.get(key).add(this);
		} 
	}
	
	/**
	 * Add object 'o' to the map[mapName] 
	 * @param o Object
	 * @param mapName tag
	 * @param compP: computational path
	 */
	public void addObjectAtt (AliasObject o, String mapName, int[] compP)  {
		Variable key = new Variable (mapName, compP);
		if (!succ.containsKey(key)) {
			succ.put (key, new ArrayList<AliasObject>());
		}
		succ.get(key).add(o); 
		//update predecessor
		if (!o.pred.containsKey(key)) {
			o.pred.put(key, new ArrayList<AliasObject>());
		}
		o.pred.get(key).add(this);
	}
	
	/**
	 * 
	 * @param mapName
	 * @return the list of objects associated to mapName, from the
	 * 			current context
	 */
	public ArrayList<AliasObject> getObjects (Variable mapName)  {
		// TODO (9919): the list of associated objects should be according to the computational
		//					path (to fix)
		return succ.get (mapName);
	}
	
	/**
	 * Updates the name of the key
	 * @param key
	 * @throws AliasException 
	 */
	public void updateInHold (Variable oldKey, Variable newKey) throws AliasException {
		if (!succ.containsKey(oldKey)) {
			throw new AliasException("Alias Object is not holding any key: " + oldKey);
		}
		ArrayList<AliasObject> val = succ.get(oldKey);
		succ.remove(oldKey);
		succ.put(newKey, val);
		// update the oldKey in the predecessors
		for (AliasObject p: val) {
			ArrayList<AliasObject> val2 = p.pred.get(oldKey);
			p.pred.remove(oldKey);
			p.pred.put(newKey, val2);
		}
	}
	
	/**
	 * Is the Alias Object waiting for info to be updated?
	 * @param oldKey
	 * @return
	 */
	public boolean isUpdateNeeded (Variable oldKey) {
		return succ.containsKey(oldKey);
	}
	
	
	public String toString() {
		return "#" + this.hashCode();
	}
	
	
	/**
	 * Node of a graph
	 */
	
	private boolean visited;
	
	public boolean isVisited() {
		return visited;
	}

	public void setVisited(boolean visited) {
		this.visited = visited;
	}
	
	public boolean isIn (Variable tag) {
		return succ.containsKey(tag);
	}
	
	//idNode
	private int id;
	//Avoiding changes from outside the class
	public int idNode() {
		return id;
	}
		
	
	/**
	 * For Debugging and Testing phases
	 */
	
	public static void main (String[] arg) throws NoSuchFieldException, SecurityException {
		/*Field[] fields = t.type.getDeclaredFields();
		System.out.println (fields.length);
		for (int i=0;i<fields.length;i++) {
			System.out.println (fields[i].getName());
		}*/
		
	}

}
