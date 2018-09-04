package structures.graphRep;

import model.AliasObject;

/**
 * 
 * Implements an edge from the Alias Diagram. It is used by
 * SetEdges only.
 * 
 * @author Victor Rivera
 *
 */

public class Edge {

	public AliasObject target, source;
	public String tag;
	
	public Edge(AliasObject target, String tag, AliasObject source) {
		this.target = target;
		this.source = source;
		this.tag = tag;
	}
	
	@Override
	public boolean equals (Object other) {
		/*if (other == null) return false;
	    if (other == this) return true;
	    if (!(other instanceof Edge))return false;
		return target.equals(((Edge)other).target) &&
				source.equals(((Edge)other).source) &&
				tag.equals(((Edge)other).tag);*/
		return true;
	}

	public String toString() {
		return "(" + target + ", " + tag + ", " + source +")";  
	}
	
}
