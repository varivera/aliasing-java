package structures;

import java.util.ArrayList;

/**
 * Keeps track of the added edges in the body of the loops.
 * This is important as it determines whether to delete added edges or subsume others.
 *
 *  @author Victor Rivera (victor.rivera@anu.edu.au)
 */

public class Loop extends ControlStructure {
	
	ArrayList<ArrayList<Edge>> added;
	
	public Loop() {
		edges = new ArrayList<ArrayList<Edge>>();
		added = new ArrayList<ArrayList<Edge>>();
	}
	
	
	public void added (Edge e) {
		added.get(added.size()-1).add(e);
	}
	
	@Override
	public void step() {
		super.step();
		added.add(new ArrayList<Edge>());
	}
	
	/**
	 * @return the intersection of edges in deletion
	 * 
	 * Note: there's no need to go through each list as it does not matter how many time the loop is executed,
	 * 				the number of added edges is the same in the same order.
	 */
	public ArrayList<Edge> stop() {
		assert edges.size() > 1;
		assert edges.size() == added.size();
		ArrayList<Edge> res = new ArrayList<Edge>();
		// check only the last 2 elements
		
		for (int i=0;i<edges.get(edges.size()-1).size();i++) {
			if (edges.get(edges.size()-1).get(i).target().equals(edges.get(edges.size()-2).get(i).target())) { //same
				Edge e = edges.get(0).get(i);
				e.source().succ.get(e.tag()).add(e.target());
				e.target().pred.get(e.tag()).add(e.source());
			}else { 
				for (int j=0;j<edges.size();j++) {
					Edge e = edges.get(j).get(i);
					e.source().succ.get(e.tag()).add(e.target());
					e.target().pred.get(e.tag()).add(e.source());
				}
				//TODO subsume (n2, n1)
			}
		}
		
		
		return res;
	}
	
	@Override
	public String toString() {
		StringBuilder res = new StringBuilder();
		res.append(super.toString());
		
		res.append("\nAdded*****************\n");
		
		for (int i=0;i<added.size();i++) {
			res.append((i+1) + ". <");
			for (int j=0;j<added.get(i).size();j++) {
				res.append(added.get(i).get(j));
				if (j<added.get(i).size()-1) {
					res.append(", ");
				}
			}
			if (i<added.size()-1) {
				res.append(">\n");
			}
		}
		res.append(">\n*****************\n");
		return res.toString();
	}
	
	
}