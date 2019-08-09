package structures;

import java.util.ArrayList;

import model.AliasObject;

/**
 * Implements the operations that handle Control Structures (e.g. conditionals)
 * in the Alias Diagram
 *  
 *  @author Victor Rivera (victor.rivera@anu.edu.au)
 */

public class ControlStructures {
	/**
	 * Keeps track of each addition to the Alias Diagram
	 */
	private	ArrayList<ArrayList<Edge>> additions;
	
	/**
	 * Keeps track of each deletion to the Alias Diagram
	 */
	private ArrayList<ArrayList<Edge>> deletions;
	
	public ControlStructures() {
		additions = new ArrayList<ArrayList<Edge>>();
		deletions = new ArrayList<ArrayList<Edge>>();
	}
	
	/**
	 * creates a new place holder of Edges
	 */
	public void step() {
		additions.add(new ArrayList<Edge>());
		deletions.add(new ArrayList<Edge>());
	}
	
	/**
	 * @param oldT old Target 
	 * @param newT new Target
	 * updates additions and deletions
	 */
	public void add(ArrayList <ArrayList<AliasObject>> oldT, ArrayList <ArrayList<AliasObject>> newT, String tag) {
		assert oldT.size() == newT.size();
		ArrayList<ArrayList<AliasObject>> source = new ArrayList<ArrayList<AliasObject>>();
		ArrayList<ArrayList<AliasObject>> target = new ArrayList<ArrayList<AliasObject>>();
		
		for (ArrayList<AliasObject> t: oldT) {
			source.add(new ArrayList<AliasObject>());
			target.add(new ArrayList<AliasObject>());
			
			for (AliasObject ao: t) {
				if (!target.get(target.size()-1).contains(ao)) {
					target.get(target.size()-1).add(ao);
				}
				for (AliasObject pred: ao.pred.get(tag)) {
					if (!source.get(target.size()-1).contains(pred)) {
						source.get(source.size()-1).add(pred);
					}
				}
			}
		}
		
		

		Edge e = new Edge(source, target, tag);
		System.out.println("e1: " + e);
		additions.get(additions.size()-1).add(e);
		
		e = new Edge(source, target, tag);
		System.out.println("e2: " + e);
		deletions.get(deletions.size()-1).add(e);
	}
	
	public String toString() {
		StringBuilder res = new StringBuilder();
		
		res.append("*****************\nAdditions:");
		
		for (int i=0;i<additions.size();i++) {
			res.append((i+1) + ". <");
			for (int j=0;j<additions.get(i).size()-1;j++) {
				res.append(additions.get(i).get(j));
				if (j<additions.get(i).size()-1) {
					res.append(", ");
				}
			}
			res.append(">\n");
		}
		
		res.append(">\n\nDeletions:");
		for (int i=0;i<deletions.size();i++) {
			res.append((i+1) + ". <");
			for (int j=0;j<deletions.get(i).size()-1;j++) {
				res.append(deletions.get(i).get(j));
				if (j<deletions.get(i).size()-1) {
					res.append(", ");
				}
			}
			res.append(">\n");
		}
		res.append(">\n*****************\n");
		return res.toString();
	}
}