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
		
		ArrayList<ArrayList<AliasObject>> oldSource = new ArrayList<ArrayList<AliasObject>>(oldT.size());
		
		for (ArrayList<AliasObject> r: oldT) {
			oldSource.add(new ArrayList<AliasObject>());
			for (AliasObject ao: r) {
				for (AliasObject pred: ao.pred.get(tag)) {
					oldSource.get(oldSource.size()-1).add(pred);
					Edge e = new Edge (pred, tag, ao);
					deletions.get(deletions.size()-1).add(e);
					//to delete
					System.out.println("deletions e: " + e);
					//to delete
				}
			}
		}
		assert newT.size() == oldSource.size();
		for (int i=0;i<newT.size();i++) {
			for (AliasObject ao: newT.get(i)) {
				for (AliasObject pred: oldSource.get(i)) {
					Edge e = new Edge (pred, tag, ao);
					additions.get(additions.size()-1).add(e);
					//to delete
					System.out.println("additions e: " + e);
					//to delete
				}
			}
		}
	}
	
	public ArrayList<Edge> getLastAdded(){
		return additions.get(additions.size()-1);
	}
	
	public ArrayList<Edge> getLastRemoved(){
		return deletions.get(deletions.size()-1);
	}
	
	public String toString() {
		StringBuilder res = new StringBuilder();
		
		res.append("*****************\nAdditions:\n");
		
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
		
		res.append(">\n\nDeletions:\n");
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