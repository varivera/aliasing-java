package structures;

import java.util.ArrayList;

import model.AliasObject;

/**
 * Represents all possible edges in the Alias Diagram with a specific tag
 *  
 *  @author Victor Rivera (victor.rivera@anu.edu.au)
 */

public class Edge {
	
	private ArrayList<ArrayList<AliasObject>> source;
	private ArrayList<ArrayList<AliasObject>> target;
	private String tag;
	
	public Edge(ArrayList<ArrayList<AliasObject>> source, ArrayList<ArrayList<AliasObject>> target,
			String tag) {
		this.source = source;
		this.target = target;
		this.tag = tag;
	}
	
	
	public String toString() {
		StringBuilder res = new StringBuilder();
		res.append("(<");
		for (int i=0;i<source.size();i++) {
			res.append("<");
			for (int j=0;j<source.get(i).size();j++) {
				res.append(source.get(i).get(i).idNode());
				if (j < source.get(i).size()-1) {
					res.append(", ");
				}
			}
			res.append(">");
			if (i < source.size()-1) {
				res.append(", ");
			}
		}
		res.append(">, ");
		res.append(tag);
		res.append(", <");
		for (int i=0;i<target.size();i++) {
			res.append("<");
			for (int j=0;j<target.get(i).size();j++) {
				res.append(target.get(i).get(i).idNode());
				if (j < target.get(i).size()-1) {
					res.append(", ");
				}
			}
			res.append(">");
			if (i < target.size()-1) {
				res.append(", ");
			}
		}
		res.append(">)");
		
		return res.toString();
	}
	
	/**
	 * For testing purposes
	 */
}