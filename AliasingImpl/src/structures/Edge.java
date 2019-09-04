package structures;

import model.AliasObject;

/**
 * Represents an edge (source, tag, target) in the Alias Diagram: source---tag--->target
 *  
 *  @author Victor Rivera (victor.rivera@anu.edu.au)
 */

public class Edge {
	
	private AliasObject source;
	private AliasObject target;
	private String tag;
	
	public Edge(AliasObject source, String tag, AliasObject target) {
		this.source = source;
		this.target = target;
		this.tag = tag;
	}
	
	public AliasObject source() {
		return source;
	}
	
	public AliasObject target() {
		return target;
	}
	public String tag() {
		return tag;
	}
	
	
	public String toString() {
		StringBuilder res = new StringBuilder();
		res.append("<");
		res.append(source.idNode());
		res.append(",");
		res.append(tag);
		res.append(",");
		res.append(target.idNode());
		res.append(">");
		return res.toString();
	}
	
	/**
	 * For testing purposes
	 */
}