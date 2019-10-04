package structures;

import java.util.ArrayList;

import model.AliasObject;

/**
 * Keeps track of the added edges in the body of the loops.
 * This is important as it determines whether to delete added edges or subsume others.
 *
 *  @author Victor Rivera (victor.rivera@anu.edu.au)
 */

public class Loop extends ControlStructure {
	
	public Loop() {
		deletions = new ArrayList<ArrayList<Edge>>();
		additions = new ArrayList<ArrayList<Edge>>();
	}
	
	/**
	 * @return the intersection of edges in deletion
	 * 
	 * Note: there's no need to go through each list as it does not matter how many time the loop is executed,
	 * 				the number of added edges is the same in the same order.
	 */
	public Pair<ArrayList<Edge>, ArrayList<Edge>> stop() {
		assert deletions.size() > 1;
		assert deletions.size() == additions.size();
		//res.prj1 -> additions
		//res.prj2 -> deletions
		Pair<ArrayList<Edge>, ArrayList<Edge>> res = new Pair<ArrayList<Edge>, ArrayList<Edge>>(
			new ArrayList<Edge>(),
			new ArrayList<Edge>()
			);
		// check only the last 2 elements
		
		for (int i=0;i<deletions.get(deletions.size()-1).size();i++) {
			if (deletions.get(deletions.size()-1).get(i).target().equals(deletions.get(deletions.size()-2).get(i).target())) { //same
				Edge e = deletions.get(0).get(i);
				e.source().addEdge(e.tag(), e.target());
				res.prj1.add(new Edge (e.source(), e.tag(), e.target()));
			}else { 
				for (int j=0;j<deletions.size();j++) {
					Edge e = deletions.get(j).get(i);
					e.source().addEdge(e.tag(), e.target());
					res.prj1.add(new Edge (e.source(), e.tag(), e.target()));
				}
				assert additions.size() > 2;
				subsume (additions.get(additions.size()-2).get(i).target(), additions.get(additions.size()-1).get(i).target(), res);
			}
		}
		
		
		return res;
	}
	
	/**
	 * Subsume is an over-approximation in the analysis needed as the stop
	 * condition of the loop is not considered. An example of the need to 
	 * subsume nodes:
	 * 		loop
	 * 			l = l.right;
	 * 		end
	 * 
	 *  subsume (AliasObject n1, AliasObject n2) -> Subsume n2 into n1
	 */
	private void subsume (AliasObject n1, AliasObject n2, Pair<ArrayList<Edge>, ArrayList<Edge>> ad) {
		/**
		 * subsume n2 into n1
		 * 
		 *   (i) for all v such that (n2, v, n2) in G, then add (n1, v, n1) to G
		 *  (ii) for all v such that (n1, v, n2) in G, then add (n1, v, n1) to G
		 * (iii) for all v and n such that (n2, v, n) in G, then add (n1, v, n) to G
		 *  (iv) for all v and n such that (n, v, n2) in G, then add (n, v, n1) to G
		 * 
		 */
		n1.subsume(n2, ad);
	}
	
}