package model;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import structures.helpers.Id;

/**
 * 
 * AliasObject represents a possible object computation
 * at run-time. AliasObjects are possible Nodes of the 
 * Alias Diagram.
 * 
 * @author Victor Rivera
 *
 */

public class AliasObject {
	
	/**
	 * mapping of the AliasObject. For example,
	 * AliasObject could be the variables of a class, or 
	 * arguments of a routine
	 */
	public Map<String, ArrayList<AliasObject>> mapping;
	
	/**
	 * type of the entity that holds the AliasObject.
	 * e.g. in case of variables, the type is 'this'
	 */
	private String type;
	
	public AliasObject(String type, int id) {
		this.type = type;
		mapping = new HashMap<String, ArrayList<AliasObject>>();
		setVisited(false);
		this.id = id;
	}
	
	/**
	 * Add 'tag' to the map of the current object
	 * @param mapName
	 * @param type  
	 * @param id 
	 */
	public void addMap (String mapName, String type, int id)  {
		if (!mapping.containsKey(mapName)) {
			mapping.put (mapName, new ArrayList<AliasObject>());
		}
		mapping.get(mapName).add(new AliasObject (type, id));
	}
	
	/**
	 * Add 'tag' to the mapping of the current
	 * object only if it was not there already
	 * @param mapName
	 * @param type
	 * @param id 
	 */
	public void initValMap (String mapName, String type, int id)  {
		if (!mapping.containsKey(mapName)) {
			mapping.put (mapName, new ArrayList<AliasObject>());
			mapping.get(mapName).add(new AliasObject (type, id));
		} 
	}
	
	/**
	 * Add object 'o' to the map[mapName] 
	 * @param o Object
	 * @param mapName tag
	 */
	public void addObjectAtt (AliasObject o, String mapName)  {
		if (!mapping.containsKey(mapName)) {
			mapping.put (mapName, new ArrayList<AliasObject>());
		}
		mapping.get(mapName).add(o); 
	}
	
	/**
	 * 
	 * @param mapName
	 * @return the list of objects associated to mapName, from the
	 * 			current context
	 */
	public ArrayList<AliasObject> getObjects (String mapName)  {
		return mapping.get (mapName);
	}
	
	/**
	 * 
	 * @return string representing the type
	 */
	public String typeName () {
		return type;
	}
	
	/**
	 * 
	 * @return printable type name
	 */
	public String printableTypeName () {
		return "<"+typeName()+">";
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
