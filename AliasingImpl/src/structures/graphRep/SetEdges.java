package structures.graphRep;

import java.util.HashSet;
import java.util.Set;

import model.AliasObject;

/**
 * 
 * Implements another view of the alias diagram: a set of edges.
 * The class implements a mechanism to get the SetEdges from
 * an AliasDiagram
 * 
 * @author Victor Rivera
 *
 */

public class SetEdges {
	
	private Set<Edge> set;
	
	public SetEdges () {
		set = new HashSet<Edge>();
	}
	
	public void addEdge (Edge e) {
		set.add(e);
	}
	
	public boolean equals (SetEdges other) {
		return true;
	}

	public static void main (String[] args) {
		
		AliasObject r = new AliasObject("T");
		AliasObject n1 = new AliasObject("T");
		AliasObject n2 = new AliasObject("T");
		Edge e1 = new Edge(r, "a", n1);
		Edge e2 = new Edge(r, "b", n2);
		
		Set<Edge> set = new HashSet<Edge>();
		set.add(e1);
		set.add(e2);
		

		AliasObject r_o = new AliasObject("T");
		AliasObject n1_o = new AliasObject("T");
		AliasObject n2_o = new AliasObject("T");
		Edge e1_o = new Edge(r_o, "a", n1_o);
		Edge e2_o = new Edge(r_o, "b", n2_o);
		
		Set<Edge> set_o = new HashSet<Edge>();
		set_o.add(e2_o);
		set_o.add(e1_o);

		System.out.println(set);
		System.out.println(set_o);
		System.out.println(set.equals(set_o));
	}
	
}
