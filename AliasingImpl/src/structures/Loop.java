package structures;

import java.util.ArrayList;

/**
 * Keeps track of the added edges in the body of the loops.
 * This is important as it determines whether to delete added edges or subsume others.
 *
 *  @author Victor Rivera (victor.rivera@anu.edu.au)
 */

public class Loop extends ControlStructure {
	
	public Loop() {
		edges = new ArrayList<ArrayList<Edge>>();
	}
	
	/**
	 * @return the intersection of edges in deletion
	 * 
	 * Note: there's no need to go through each list as it does not matter how many time the loop is executed,
	 * 				the number of added edges is the same in the same order.
	 */
	public ArrayList<Edge> stop() {
		assert edges.size() > 1;
		ArrayList<Edge> res = new ArrayList<Edge>();
		// check only the last 2 elements
		
		/*for (int i=0;i<edges.get(edges.size()-1).size();i++) {
			if (edges.get(edges.size()-1).get(i).target().equals(edges.get(edges.size()-2).get(i).target())) { //same
				for (int j=1;j<edges.size();j++) {
					Edge e = edges.get(j).get(i);
					e.source().succ.get(e.tag()).remove(e.target());
					e.target().pred.get(e.tag()).remove(e.source());
				}
			}else { //subsume
				//TODO
			}
		*/
		
		
		return res;
	}
	
	
}