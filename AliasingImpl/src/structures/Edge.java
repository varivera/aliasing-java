package structures;

import java.util.ArrayList;

import model.AliasObject;

/**
 * Represents an edge (source, tag, target) in the Alias Diagram: source---tag--->target
 *  
 *  @author Victor Rivera (victor.rivera@anu.edu.au)
 */

public class Edge {
	
	private AliasObject source;
	private AliasObject target;
	private Variable tag;
	
	public Edge(AliasObject source, Variable tag, AliasObject target) {
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
	public Variable tag() {
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
	
	@Override
    public boolean equals(Object o) {
		if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Edge e = (Edge) o;
        if (e.source.idNode() != source.idNode()) return false;
        if (e.target.idNode() != target.idNode()) return false;
        if (!e.tag.equals(tag)) return false;
        return true;
    }
    @Override
    public int hashCode() {
        int result = 1;
        result = 31 * result + source.idNode();
        result = 31 * result + tag.hashCode();
        result = 31 * result + target.idNode();
        return result;
    }
	
	/**
	 * For testing purposes
	 */
	
	public static void main (String[] arg) {
		ArrayList<Edge> l = new ArrayList<Edge>();
		l.add(new Edge(new AliasObject(0), new Variable("b", null), new AliasObject(1)));
		l.add(new Edge(new AliasObject(1), new Variable("c", null), new AliasObject(2)));
		l.add(new Edge(new AliasObject(0), new Variable("b", null), new AliasObject(2)));
		System.out.println(l);
		System.out.println(l.contains(new Edge(new AliasObject(0), new Variable("b", null), new AliasObject(0))));
		
	}
	
}