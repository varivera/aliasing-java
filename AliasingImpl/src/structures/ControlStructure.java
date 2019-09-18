package structures;

import java.util.ArrayList;

/**
 * Keeps track of the added and deleted edges in control structures.
 * 
 *  
 *  @author Victor Rivera (victor.rivera@anu.edu.au)
 */

public abstract class ControlStructure {
	
	/**
	 * Keeps track of the added edges in the Alias Diagram.
	 */
	protected ArrayList<ArrayList<Edge>> additions;
	
	/**
	 * Keeps track of the deleted edges in the Alias Diagram.
	 */
	protected ArrayList<ArrayList<Edge>> deletions;
	
	
	/**
	 * creates a new place holder of Edges
	 */
	public void step() {
		additions.add(new ArrayList<Edge>());
		deletions.add(new ArrayList<Edge>());
	}
	
	/**
	 * @param e is the edge: (source, tag, target) 
	 * updates additions
	 */
	public void added(Edge e) {
		additions.get(additions.size()-1).add(e);
	}
	
	/**
	 * @param e is the edge: (source, tag, target) 
	 * updates deletions
	 */
	public void deleted(Edge e) {
		deletions.get(deletions.size()-1).add(e);
	}
	
	/**
	 * implements the corresponding actions according to the different 
	 * control structures
	 */
	public abstract ArrayList<Edge> stop();
	
	
	/**
	 * @param globalCP
	 * @return the computational path. Information needed to update the Alias Diagram
	 */
	public int[] computationalPath (int globalCP) {
		int[] res = new int[additions.size()+1];
		res[0] = globalCP;
		
		for (int i=0;i<additions.size();i++) {
			res[i+1] = additions.get(i).size();
		}
		
		return res;
	}
	
	public int getCount() {
		assert additions.size() == deletions.size();
		return additions.size();
	}
	
	public String toString() {
		StringBuilder res = new StringBuilder();
		
		res.append("\nAdded*****************\n");
		
		for (int i=0;i<additions.size();i++) {
			res.append((i+1) + ". <");
			for (int j=0;j<additions.get(i).size();j++) {
				res.append(additions.get(i).get(j));
				if (j<additions.get(i).size()-1) {
					res.append(", ");
				}
			}
			if (i<additions.size()-1) {
				res.append(">\n");
			}
		}
		res.append(">\n*****************\n");
		res.append("\nAdded*****************\n");
		
		for (int i=0;i<additions.size();i++) {
			res.append((i+1) + ". <");
			for (int j=0;j<additions.get(i).size();j++) {
				res.append(additions.get(i).get(j));
				if (j<additions.get(i).size()-1) {
					res.append(", ");
				}
			}
			if (i<additions.size()-1) {
				res.append(">\n");
			}
		}
		res.append(">\n*****************\n");
		return res.toString();
	}
}