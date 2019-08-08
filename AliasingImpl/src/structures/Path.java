package structures;

import java.util.ArrayList;

import model.AliasObject;

/**
 * Path represents a path in an AliasDiagram. It contains a lot more
 * information than just 'a.b.c.d', it also stores the corresponding nodes
 * of the Alias Diagram. This means, some operations can be easily implemented
 * in this class, e.g whether a path is in the Alias Diagram.
 *  
 *  @author Victor Rivera (victor.rivera@anu.edu.au)
 */

public class Path {
	private	ArrayList <String> tags;
	private ArrayList <ArrayList<AliasObject>> nodeTags;
	
	public Path(ArrayList <String> tags, ArrayList <ArrayList<AliasObject>> nodeTags) {
		assert tags.size() == nodeTags.size()-1;
		this.tags = tags;
		this.nodeTags = nodeTags;
	}
	
	/**
	 * @return true is the path is in the Alias Diagram. False otherwise
	 */
	public boolean exists() {
		assert tags.size() == nodeTags.size()-1;
		for (int i=0; i<tags.size();i++) {
			for (AliasObject ao: nodeTags.get(i)) {
				String tag = tags.get(i);
				if (!ao.succ.containsKey(tag)) {
					return false;
				}
//				ao.succ.get(tag)
			}
		}
		return false;
	}
	
	
	
	public String toString() {
		StringBuilder res = new StringBuilder();
		for (String t: tags) {
			res.append(t);
			res.append(".");
		}
		return res.substring(0, res.length()-1);
	}
	
}