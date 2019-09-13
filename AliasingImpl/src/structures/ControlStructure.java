package structures;

import java.util.ArrayList;

/**
 * Keeps track of the edges in the control structures.
 * 
 *  
 *  @author Victor Rivera (victor.rivera@anu.edu.au)
 */

public abstract class ControlStructure {
	
	/**
	 * Keeps track of edges of the Alias Diagram modified in Control Structures.
	 */
	protected ArrayList<ArrayList<Edge>> edges;
	
	
	/**
	 * creates a new place holder of Edges
	 */
	public void step() {
		edges.add(new ArrayList<Edge>());
	}
	
	/**
	 * @param e is the edge: (source, tag, target) 
	 * updates edges
	 */
	public void add(Edge e) {
		edges.get(edges.size()-1).add(e);
	}
	
	/**
	 * implements the corresponding actions according to the different 
	 * control stuctures
	 */
	public abstract ArrayList<Edge> stop();
	
	
	/**
	 * @param globalCP
	 * @return the computational path. Information needed to update the Alias Diagram
	 */
	public int[] computationalPath (int globalCP) {
		int[] res = new int[edges.size()+1];
		res[0] = globalCP;
		
		for (int i=0;i<edges.size();i++) {
			res[i+1] = edges.get(i).size();
		}
		
		return res;
	}
	
	public int getCount() {
		return edges.size();
	}
	
	public boolean isIn (Edge e) {
		return edges.get(edges.size()-1).contains(e);
	}
	
	public String toString() {
		StringBuilder res = new StringBuilder();
		
		res.append("\n*****************\n");
		
		for (int i=0;i<edges.size();i++) {
			res.append((i+1) + ". <");
			for (int j=0;j<edges.get(i).size();j++) {
				res.append(edges.get(i).get(j));
				if (j<edges.get(i).size()-1) {
					res.append(", ");
				}
			}
			if (i<edges.size()-1) {
				res.append(">\n");
			}
		}
		res.append(">\n*****************\n");
		return res.toString();
	}
}