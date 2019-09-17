package structures;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;

import model.AliasObject;
import model.Routine;
import structures.helpers.Helpers;
import structures.helpers.Id;

/**
 * Path represents a path in an AliasDiagram. It contains a lot more
 * information than just 'a.b.c.d', it also stores the corresponding nodes
 * of the Alias Diagram. This means, some operations can be easily implemented
 * in this class, e.g whether a path is in the Alias Diagram.
 *  
 *  @author Victor Rivera (victor.rivera@anu.edu.au)
 */

public class Path {
	/**
	 * representation of the path. Path 'a.b.c' is
	 * represented by list [a, b, c]
	 */
	private	ArrayList <String> tags;
	
	/**
	 * nodeTags stores possible path of nodes that takes
	 * from the source to the target
	 * Constraint: there exist at least one path
	 * Constraint: for all paths p in nodeTags, p.size() == tags.size+1
	 */
	private ArrayList <ArrayList<AliasObject>> nodeTags;
	
	public Path(ArrayList <String> tags, ArrayList <ArrayList<AliasObject>> nodeTags) {
		assert nodeTags != null && tags != null;
		this.tags = tags;
		this.nodeTags = nodeTags;
		
		assert correctNumberOfNodes();
	}
	
	public Path(AliasDiagram g, ArrayList<String> path) {
		// TODO
		assert this.tags != null;
		assert this.nodeTags != null;
		assert correctNumberOfNodes();
	}
	
	
	//delete
	public Path() {
		
	}
	
	
	/**
	 * @return the possible source nodes of the path
	 */
	public ArrayList<AliasObject> getSource(){
		assert nodeTags.size() > 0;
		ArrayList<AliasObject> res = new ArrayList<AliasObject>(nodeTags.size());
		for (ArrayList<AliasObject> ao: nodeTags) {
			res.add(ao.get(0));
		}
		return res;
	}
	
	/**
	 * @return all possible target nodes of the path
	 */
	public ArrayList<AliasObject> getTarget(){
		assert nodeTags.size() > 0;
		ArrayList<AliasObject> res = new ArrayList<AliasObject>(nodeTags.size());
		for (ArrayList<AliasObject> ao: nodeTags) {
			res.add(ao.get(ao.size()-1));
		}
		return res;
	}
	
	/**
	 * @param s1
	 * @param s2
	 * @return is s1.intersection(s2) /= {}
	 */
	public static boolean intersect (ArrayList<AliasObject> s1, ArrayList<AliasObject> s2) {
		for (AliasObject oa: s1) {
			if (s2.contains(oa)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * @param node starting point in the diagram
	 * @param path to be considered
	 * @return the corresponding path of nodes in g, following path 'path'
	 */
	private ArrayList <ArrayList<AliasObject>> getPath(AliasObject node, ArrayList<String> path) {
		
		if (path.size() == 0) {// no more elements in the path to look up
			//return 
		}
		
		return null;
	}
	
	
	/**
	 * Returns the path in the form
	 * a.b...
	 * <<0>,<1>,<2>>
	 */
	public String toString() {
		StringBuilder res = new StringBuilder();
		for (String t: tags) {
			res.append(t);
			res.append(".");
		}
		res.deleteCharAt(res.length()-1);
		res.append("\n<");
		for (ArrayList<AliasObject> paths: nodeTags) {
			res.append("<");
			for (AliasObject ao: paths) {
				res.append(ao.idNode()+",");
			}
			res.append(">");
		}
		res.append(">");
		
		
		return res.toString();
	}
	
	/**
	 * For testing purposes
	 */
	private boolean correctNumberOfNodes() {
		int n = tags.size() + 1;
		for (ArrayList<AliasObject> paths: nodeTags) {
			if (paths.size()!=n) return false;
		}
		return true;
	}
	
	public static void main(String[] args) {
		
	}
	
}