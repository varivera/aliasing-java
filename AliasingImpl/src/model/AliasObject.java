package model;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import exceptions.AliasException;

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
	public Map<String, ArrayList<AliasObject>> succ;
	
	/**
	 * Predecessor of the AliasObject
	 */
	public Map<String, ArrayList<AliasObject>> pred;
	
	public AliasObject(int id) {
		succ = new HashMap<String, ArrayList<AliasObject>>();
		pred = new HashMap<String, ArrayList<AliasObject>>();
		setVisited(false);
		this.id = id;
	}
	
	/**
	 * Add 'tag' to the map of the current object
	 * @param mapName
	 * @param id 
	 */
	public void addMap (String mapName, int id)  {
		if (!succ.containsKey(mapName)) {
			succ.put (mapName, new ArrayList<AliasObject>());
		}
		AliasObject newO = new AliasObject (id);
		succ.get(mapName).add(newO);
		//update predecessor
		newO.pred.put(mapName, new ArrayList<AliasObject>());
		newO.pred.get(mapName).add(this);
	}
	
	/**
	 * Add 'tag' to the mapping of the current
	 * object only if it was not there already
	 * @param mapName
	 * @param id 
	 */
	public void initValMap (String mapName, int id)  {
		if (!succ.containsKey(mapName)) {
			succ.put (mapName, new ArrayList<AliasObject>());
			AliasObject newO = new AliasObject (id);
			succ.get(mapName).add(newO);
			//update predecessor
			newO.pred.put(mapName, new ArrayList<AliasObject>());
			newO.pred.get(mapName).add(this);
		} 
	}
	
	/**
	 * Add object 'o' to the map[mapName] 
	 * @param o Object
	 * @param mapName tag
	 */
	public void addObjectAtt (AliasObject o, String mapName)  {
		if (!succ.containsKey(mapName)) {
			succ.put (mapName, new ArrayList<AliasObject>());
		}
		succ.get(mapName).add(o); 
		//update predecessor
		if (!o.pred.containsKey(mapName)) {
			o.pred.put(mapName, new ArrayList<AliasObject>());
		}
		o.pred.get(mapName).add(this);
	}
	
	/**
	 * 
	 * @param mapName
	 * @return the list of objects associated to mapName, from the
	 * 			current context
	 */
	public ArrayList<AliasObject> getObjects (String mapName)  {
		return succ.get (mapName);
	}
	
	/**
	 * Updates the name of the key
	 * @param key
	 * @throws AliasException 
	 */
	public void updateInHold (String oldKey, String newKey) throws AliasException {
		if (!succ.containsKey(oldKey)) {
			throw new AliasException("Alias Object is not holding any key: " + oldKey);
		}
		ArrayList<AliasObject> val = succ.get(oldKey);
		succ.remove(oldKey);
		succ.put(newKey, val);
		//No need to update predecessors as only the edge's name changes
	}
	
	/**
	 * Is the Alias Object waiting for info to be updated?
	 * @param oldKey
	 * @return
	 */
	public boolean isUpdateNeeded (String oldKey) {
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
	
	public boolean isIn (String tag) {
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
