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
	
	public Set<Edge> set;
	
	public SetEdges () {
		set = new HashSet<Edge>();
	}
	
	/**
	 * from 's' to SetEdges
	 * 
	 * @param s should be in the form: [(1, w, 3), (1, z, 2), (1, v, 2)]
	 * 		this particular example has 3 nodes (1, 2, 3) and nodes 1 and 3
	 * 		are connected with label 'w', nodes 1 and 2 are connected with label
	 * 		'z' and so on.
	 */
	public SetEdges (String s) {
		set = new HashSet<Edge>();
		//e.g. [(1, w, 3), (1, z, 2), (1, v, 2)]
		String[] d = s.replace("[", "").replaceAll("]", "").replaceAll("\\(", "").replaceAll("\\)","").replaceAll(" ", "").split(",");
		if (d.length%3!=0) return;
		
		for (int i=0;i<d.length;i=i+3) {
			addEdge (new Edge (Integer.parseInt(d[i]), d[i+1], Integer.parseInt(d[i+2])));
		}
		
	}
	
	/**
	 * Adds edge 'e' to the current set
	 * @param e Edge to be added
	 */
	public void addEdge (Edge e) {
		set.add(e);
	}
	
	/**
	 * @param e Edge
	 * @return is edge 'e' in current Set of Edges?
	 */
	public boolean in (Edge e) {
		for (Edge ee: set) {
			if (ee.equals(e)) return true;
		}
		return false;
	}
	
	/**
	 * current set Union 'other' 
	 * @param other is a set to be union
	 */
	public void union (SetEdges other) {
		set.addAll(other.set);
	}
	
	@Override
	public String toString() {
		return set.toString();
	}
	
	@Override
	public boolean equals (Object other) {
		if (other == null) return false;
	    if (other == this) return true;
	    if (!(other instanceof SetEdges))return false;
		SetEdges tmp = (SetEdges)other;
		if (tmp.set.size() != set.size()) return false;
	    for (Edge e: set) {
	    	if (!tmp.in (e)) return false;
	    }
		return true;
	}

	public static void main (String[] args) {
		
		Edge e1 = new Edge(1, "a", 2);
		Edge e2 = new Edge(1, "b", 3);
		
		SetEdges set = new SetEdges();
		set.addEdge(e1);
		set.addEdge(e2);

		
		SetEdges set_o = new SetEdges("[(1, b, 3),  (1, a, 2)]");
		

		System.out.println(set);
		System.out.println(set_o);
		System.out.println(set.equals(set_o));
		System.out.println(set == set_o);
		
	}
	
}
