package structures;

import java.util.ArrayList;

import model.AliasObject;
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
	 * @return true if the path all path are in the Alias Diagram. False otherwise
	 */
	public boolean exists() {
		for(ArrayList<AliasObject> p: nodeTags) {
			assert p.size() == tags.size()+1;
			for (int i=0; i<tags.size();i++) {
				if (!p.get(i).succ.containsKey(tags.get(i)) || !p.get(i).succ.get(tags.get(i)).contains(p.get(i+1))) {
					return false;
				}
			}
		}
		return true;
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
		System.out.println("start");
		Id id = new Id();
		AliasDiagram g = new AliasDiagram(id);
		
		AliasObject o1 = new AliasObject (id.getId());
		AliasObject o2 = new AliasObject (id.getId());
		AliasObject o3 = new AliasObject (id.getId());
		
		g.addEdge(o1, "a", null);
		g.addEdge(o1, "d", null);
		g.addEdge(o2, "e", null);
		
		ArrayList<AliasObject> l1 = new ArrayList<AliasObject>();
		l1.add(o1);
		ArrayList<ArrayList<AliasObject>> l2 = new ArrayList<ArrayList<AliasObject>>();
		l2.add(l1);
		g.changeRoot(l2);
		g.addEdge(o2, "b", null);
		g.addEdge(o3, "f", null);
		
		l1 = new ArrayList<AliasObject>();
		l1.add(o2);
		l2 = new ArrayList<ArrayList<AliasObject>>();
		l2.add(l1);
		g.changeRoot(l2);
		g.addEdge(o3, "c", null);
		
		g.changeBackRoot();
		g.changeBackRoot();
		
		System.out.println("well-defined predecessor: " + g.predecesorsOK ());
		
		
		
		// Is path ok?
		Path p = new Path(
				new ArrayList<String>() { 
		            { 
		                add("a"); 
		                add("b"); 
		                add("c"); 
		            }}, 
				new ArrayList <ArrayList<AliasObject>>() { 
			            { 
			            	add(new  ArrayList<AliasObject>() {
			                	{
			                		add (g.getRoots().get(0));
			                		add (o1);
			                		add (o2);
			                		add (o3);
			                }});
			            }});
		
		
		
		System.out.println(p);
		System.out.println("is path p ok?: " + p.exists());
		String s = Helpers.toGraphAll (g.getRoots());
		Helpers.createDot (s, "testingAliasDiagram", "source");
		System.out.println("done");
	}
	
}