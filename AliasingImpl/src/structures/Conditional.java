package structures;

import java.util.ArrayList;

import model.AliasObject;

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

public class Conditional {
	
	/**
	 * Keeps track of each deletion to the Alias Diagram.
	 */
	private ArrayList<ArrayList<Edge>> deletions;
	
	public Conditional() {
		deletions = new ArrayList<ArrayList<Edge>>();
	}
	
	/**
	 * creates a new place holder of Edges
	 */
	public void step() {
		deletions.add(new ArrayList<Edge>());
	}
	
	/**
	 * @param e is the edge: (source, tag, target) 
	 * updates deletions
	 */
	public void add(Edge e) {
		deletions.get(deletions.size()-1).add(e);
	}
	
	/**
	 * @return the intersection of edges in deletion
	 */
	public ArrayList<Edge> stop() {
		ArrayList<Edge> res = new ArrayList<Edge>();
		if (deletions.size() <= 1) {
			return res;
		}
		int min = deletions.get(0).size();
		int j = 0;
		
		
		for (int i=1; i<deletions.size();i++) {
			if (deletions.get(i).size() < deletions.get(j).size()) {
				min = deletions.get(i).size();
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
	
	/**
	 * @param globalCond
	 * @return the computational path. Information needed to update the Alias Diagram
	 */
	public int[] computationalPath (int globalCond) {
		int[] res = new int[deletions.size()+1];
		res[0] = globalCond;
		
		for (int i=0;i<deletions.size();i++) {
			res[i+1] = deletions.get(i).size();
		}
		
		return res;
	}
	
	public int getDeletionCount() {
		return deletions.size();
	}
	
	public String toString() {
		StringBuilder res = new StringBuilder();
		
		res.append("\n*****************\n");
		
		for (int i=0;i<deletions.size();i++) {
			res.append((i+1) + ". <");
			for (int j=0;j<deletions.get(i).size();j++) {
				res.append(deletions.get(i).get(j));
				if (j<deletions.get(i).size()-1) {
					res.append(", ");
				}
			}
			if (i<deletions.size()-1) {
				res.append(">\n");
			}
		}
		res.append(">\n*****************\n");
		return res.toString();
	}
}