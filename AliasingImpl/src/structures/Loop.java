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
	
	/**
	 * keeps track of the added edges in a Loop body.
	 * 	This is needed to subsume nodes (if any) 
	 */
	ArrayList<ArrayList<Edge>> added;
	
	public Loop() {
		edges = new ArrayList<ArrayList<Edge>>();
		added = new ArrayList<ArrayList<Edge>>();
	}
	
	/***
	 * Update 'added' with 'e'
	 */
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
				assert added.size() > 2;
				subsume (added.get(added.size()-2).get(i).target(), added.get(added.size()-1).get(i).target());
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
	private void subsume (AliasObject n1, AliasObject n2) {
		/**
		 * subsume n2 into n1
		 * 
		 *   (i) for all v such that (n2, v, n2) in G, then add (n1, v, n1) to G
		 *  (ii) for all v such that (n1, v, n2) in G, then add (n1, v, n1) to G
		 * (iii) for all v and n such that (n2, v, n) in G, then add (n1, v, n) to G
		 *  (iv) for all v and n such that (n, v, n2) in G, then add (n, v, n1) to G
		 * 
		 * -> update the corresponding predecessors.
		 * 		TODO: adding edges to the graph should be an operation of AliasDiagram
		 * 				so there is not need to make sure the predecessors are updated
		 */
		ArrayList<Edge> toRemove = new ArrayList<Edge>();
		for (Variable v: n2.pred.keySet()) {
			for (AliasObject o: n2.pred.get(v)) {
				if (o.equals(n2)) {// (i)
					
					AliasDiagram.addEdge(n1, v, n1);
					//n2.pred.get(v).remove(n2);
					//n2.succ.get(v).remove(n2);
					
				}if (o.equals(n1)) {// (ii)
					//n2.pred.get(v).remove(n1);
					//n1.succ.get(v).remove(n2);
					AliasDiagram.addEdge(n1, v, n1);
					toRemove.add(new Edge(n1, v, n2));
				}else { // (iv)
					
					AliasDiagram.addEdge(o, v, n1);
					toRemove.add(new Edge(o, v, n2));
					//o.succ.get(v).remove(n2);
					//n2.pred.get(v).remove(o);
				}
			}
		}
		
		for (Variable v: n2.succ.keySet()) {
			// (iii)
			for (AliasObject n: n2.succ.get(v)) {
				AliasDiagram.addEdge(n1, v, n);
				toRemove.add(new Edge(n2, v, n));
				//n2.succ.get(v).remove(n);
				//n.pred.get(v).remove(n2);
			}
		}
		
		for (Edge e: toRemove) {
			AliasDiagram.removeEdge(e.source(), e.tag(), e.target());
		}
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