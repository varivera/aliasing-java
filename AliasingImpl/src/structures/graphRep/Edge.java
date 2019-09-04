package structures.graphRep;

/**
 * 
 * Implements an edge from the Alias Diagram. It is used by
 * SetEdges only.
 * 
 * @author Victor Rivera (victor.rivera@anu.edu.au)
 *
 */

public class Edge {

	public int target, source;
	public String tag;
	
	public Edge(int target, String tag, int source) {
		this.target = target;
		this.source = source;
		this.tag = tag;
	}
	
	
	@Override
	public String toString() {
		return "(" + target + ", " + tag + ", " + source +")";  
	}
	
	@Override
	public boolean equals (Object other) {
		if (other == null) return false;
	    if (other == this) return true;
	    if (!(other instanceof Edge))return false;
	    Edge tmp = (Edge)other;
	    return tmp.target == target &&
	    		tmp.source == source && 
	    		tmp.tag.equals(tag);
	}
	
	public static void main (String[] a) {
		Edge e1 = new Edge (1,"a",2);
		Edge e2 = new Edge (1,"a",2);
		System.out.println(e1);
		System.out.println(e2);
		System.out.println(e1.equals(e2));
		System.out.println(e1 == e2);
	}
	
}
