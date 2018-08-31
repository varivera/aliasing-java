package model.old;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import demo.sourceClass;
import exceptions.Log;

/**
 * 
 * AliasObject represents a possible object computation
 * at run-time. AliasObjects are Nodes of the Alias Diagram.
 * 
 * @author Victor Rivera
 *
 */

public class AliasObjectOld <T> {
	
	/**
	 * class attributes of the current AliasObject.
	 * It could be empty if not variables are used in the
	 * source code
	 */
	public Map<String, ArrayList<AliasObjectOld <?>>> attributes;
	
	/**
	 * type of the AliasObject.
	 */
	private T type;
	
	public AliasObjectOld(T t) {
		type = t;
		attributes = new HashMap<String, ArrayList<AliasObjectOld <?>>>(type.getClass().getDeclaredFields().length);
		setVisited(false);
	}
	
	/**
	 * Add 'tag' to the list of class attributes of the current
	 * object
	 * @param attributeName
	 * @throws SecurityException 
	 * @throws NoSuchFieldException 
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void addAttribute (String attributeName)  {
		if (!attributes.containsKey(attributeName)) {
			attributes.put (attributeName, new ArrayList<AliasObjectOld <?>>());
		}
		try {
			attributes.get(attributeName).add(new AliasObjectOld (type.getClass().getDeclaredField(attributeName).getType()));
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
			Log.log.push(e);
		} catch (SecurityException e) {
			e.printStackTrace();
			Log.log.push(e);
		}
		
	}
	
	/**
	 * Add object 'o' to the map[attributeName] 
	 * @param o Object
	 * @param attributeName tag
	 */
	public void addObjectAtt (AliasObjectOld <?> o, String attributeName)  {
		if (!attributes.containsKey(attributeName)) {
			attributes.put (attributeName, new ArrayList<AliasObjectOld <?>>());
		}
		attributes.get(attributeName).add(o);
	}
	
	/**
	 * 
	 * @param attributeName
	 * @return the list of objects associated to attributeName, from the
	 * 			current context
	 */
	public ArrayList<AliasObjectOld <?>> getObjects (String attributeName)  {
		return attributes.get (attributeName);
	}
	
	/**
	 * 
	 * @return string representing the type
	 */
	public String typeName () {
		String[] t = type.toString().split("\\.");
		if (t.length>1) {
			String[] t2 = t[1].split("@");
			if (t2.length > 1) {
				return t2[0];
			}else {
				return t[1];
			}
			
		}
		return type.toString();
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
	
	public static void main (String[] arg) throws NoSuchFieldException, SecurityException {
		AliasObjectOld<sourceClass> t = new AliasObjectOld<sourceClass>(new sourceClass());
		
		/*Field[] fields = t.type.getDeclaredFields();
		System.out.println (fields.length);
		for (int i=0;i<fields.length;i++) {
			System.out.println (fields[i].getName());
		}*/
		String f = t.type.getClass().getDeclaredField("a").getType().toString();
		System.out.println (f);
		String[] ff = "classdemo.T2".split("\\.");
		System.out.println (ff.length);
	}

}
