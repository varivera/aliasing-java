package model;

import java.util.ArrayList;
import structures.Edge;
import structures.Variable;

/**
 * It contains relevant information about the edges of the Alias Diagram to
 * be used in assignments.
 * 
 *  
 *  @author Victor Rivera (victor.rivera@anu.edu.au)
 */

public class nodeInfo {
	
	/**
	 * contains the edges associated to a tag.
	 * It is an array of arrays since there could be more than 
	 * 		one root in the alias graph.
	 */
	private ArrayList <ArrayList<Edge>> edges;
	
	// raw name (no computational path associated)
	public String tag;
	
	public nodeInfo (String tag) {
		this.tag = tag;
		edges = new ArrayList <ArrayList<Edge>>();
	}
	
	/**
	 * creates rooms for edges in another root
	 */
	public void newRoot() {
		edges.add(new ArrayList<Edge>());
	}
	
	/**
	 * 
	 * @param objs to be added (the reference) to the current nodeInfo
	 */
	public void addEdge (AliasObject source, Variable tag, AliasObject target) {
		assert edges.size()!=0;
		edges.get(edges.size()-1).add(new Edge(source, tag, target));
	}
	
	/**
	 * @return s the number of roots stored
	 */
	public int nRoots() {
		return edges.size();
	}
	
	public ArrayList <ArrayList<AliasObject>> getTargets(){
		ArrayList <ArrayList<AliasObject>> res = new ArrayList <ArrayList<AliasObject>>();
		for (ArrayList<Edge> es: edges) {
			res.add(new ArrayList<AliasObject>());
			for (Edge e: es) {
				res.get(res.size()-1).add(e.target());
			}
		}
		return res;
	}
	
	public ArrayList <ArrayList<Edge>> getEdges (){
		return edges;
	}
	
	@Override
	public String toString() {
		return edges.toString();
	}
}
