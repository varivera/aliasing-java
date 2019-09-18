package structures;

import java.util.ArrayList;

/**
 * Keeps track of the deleted edges in the branches of the conditionals.
 * This is important as if the intersection of the deletions of the branches 
 * of a conditional is not empty, then that needs to be reflected in the Alias Diagram.
 * e.g.
 * a = b;
 * then a=c; 
 * else d=e;
 * end
 * 		-> in this case, there is a computation in which aliased(a,b), but consider a case 
 * 		where the intersection of the branches is not empty:
 * a = b;
 * then a=c; 
 * else a=e;
 * end
 *  	-> in this case, there is not computational path that makes aliased(a,b).
 *  @author Victor Rivera (victor.rivera@anu.edu.au)
 */

public class Conditional extends ControlStructure {
	
	public Conditional() {
		deletions = new ArrayList<ArrayList<Edge>>();
		additions = new ArrayList<ArrayList<Edge>>();
	}
	
	/**
	 * @return the intersection of edges in deletion
	 */
	public ArrayList<Edge> stop() {
		ArrayList<Edge> res = new ArrayList<Edge>();
		if (deletions.size() <= 1) {
			return res;
		}
		int j = 0;
		
		
		for (int i=1; i<deletions.size();i++) {
			if (deletions.get(i).size() < deletions.get(j).size()) {
				j = i;
			}
		}
		
		boolean inter;
		for (Edge e: deletions.get(j)) {
			inter = true;
			for (int i=0;i<deletions.size()&&inter;i++) {
				if (i!=j) {
					if (!deletions.get(i).contains(e)) {
						inter = false;
					}
				}
			}
			if (inter) {
				res.add(e);
			}
		}
		return res;
	}
	
	
}