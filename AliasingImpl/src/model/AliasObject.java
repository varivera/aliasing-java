package model;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * AliasObject represents a possible object computation
 * at run-time. AliasObjects are Nodes of the Alias Diagram.
 * 
 * @author Victor Rivera
 *
 */

public class AliasObject {
	
	/**
	 * class attributes of the current AliasObject.
	 * It could be empty if not variables are used in the
	 * source code
	 */
	public Map<String, ArrayList<AliasObject>> attributes;
	
	/**
	 * type of the AliasObject.
	 */
	private String type;
	
	public AliasObject(String t) {
		type = t;
		attributes = new HashMap<String, ArrayList<AliasObject>>();
		setVisited(false);
		//lastObjectVisited = null;
	}
	
	/**
	 * class attributes of the current AliasObject.
	 * It could be empty if not variables are used in the
	 * source code
	 */
	//public ArrayList<AliasObject> lastObjectVisited;
	
	/**
	 * Add 'tag' to the list of class attributes of the current
	 * object
	 * @param attributeName
	 * @throws SecurityException 
	 * @throws NoSuchFieldException 
	 */
	public void addAttribute (String attributeName, String type)  {
		if (!attributes.containsKey(attributeName)) {
			attributes.put (attributeName, new ArrayList<AliasObject>());
		}
		attributes.get(attributeName).add(new AliasObject (type));
		//lastObjectVisited = attributes.get(attributeName); 
	}
	
	/**
	 * Add 'tag' to the list of class attributes of the current
	 * object only if it was not there already
	 * @param attributeName
	 * @throws NoSuchFieldException 
	 */
	public void initAttribute (String attributeName, String type)  {
		if (!attributes.containsKey(attributeName)) {
			attributes.put (attributeName, new ArrayList<AliasObject>());
			attributes.get(attributeName).add(new AliasObject (type));
		} 
	}
	
	/**
	 * Add object 'o' to the map[attributeName] 
	 * @param o Object
	 * @param attributeName tag
	 */
	public void addObjectAtt (AliasObject o, String attributeName)  {
		if (!attributes.containsKey(attributeName)) {
			attributes.put (attributeName, new ArrayList<AliasObject>());
		}
		attributes.get(attributeName).add(o);
		//lastObjectVisited = attributes.get(attributeName); 
	}
	
	/**
	 * 
	 * @param attributeName
	 * @return the list of objects associated to attributeName, from the
	 * 			current context
	 */
	public ArrayList<AliasObject> getObjects (String attributeName)  {
		return attributes.get (attributeName);
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
	public String idNode;
		
	
	/**
	 * For Debugging and Testing phases
	 */
	
	@SuppressWarnings("unused")
	public static void main (String[] arg) throws NoSuchFieldException, SecurityException {
		AliasObject t = new AliasObject("sourceClass");
		
		/*Field[] fields = t.type.getDeclaredFields();
		System.out.println (fields.length);
		for (int i=0;i<fields.length;i++) {
			System.out.println (fields[i].getName());
		}*/
		
	}

}
