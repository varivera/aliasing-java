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
	private	ArrayList <String> tags;
	private ArrayList <ArrayList<AliasObject>> nodeTags;
	
	public Path(ArrayList <String> tags, ArrayList <ArrayList<AliasObject>> nodeTags) {
		assert tags.size() == nodeTags.size()-1;
		this.tags = tags;
		this.nodeTags = nodeTags;
	}
	
	public Path(AliasDiagram g, ArrayList<String> path) {
		//TODO
	}
	
	/**
	 * @return the source of the path
	 */
	public ArrayList<AliasObject> getSource(){
		assert nodeTags.size() > 0;
		return nodeTags.get(0);
	}
	
	/**
	 * @return the source of the path
	 */
	public ArrayList<AliasObject> getTarget(){
		assert nodeTags.size() > 0;
		return nodeTags.get(nodeTags.size()-1);
	}
	
	/**
	 * @return true is the path is in the Alias Diagram. False otherwise
	 */
	public boolean exists() {
		assert tags.size() == nodeTags.size()-1;
		for (int i=0; i<tags.size();i++) {
			for (AliasObject ao: nodeTags.get(i)) {
				String tag = tags.get(i);
				if (!ao.succ.containsKey(tag) || !issubset(nodeTags.get(i+1), ao.succ.get(tag))) {
					return false;
				}
			}
		}
		return true;
	}
	
	/**
	 * @param s1
	 * @param s2
	 * @return s1.subset(s2)?
	 */
	private boolean issubset (ArrayList<AliasObject> s1, ArrayList<AliasObject> s2) {
		for (AliasObject oa: s1) {
			if (!s2.contains(oa)) {
				return false;
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
		for (ArrayList<AliasObject> nt1: nodeTags) {
			res.append("<");
			for (AliasObject ao: nt1) {
				res.append(ao.idNode());
			}
			res.append(">");
		}
		res.append(">");
		
		
		return res.toString();
	}
	
	public static void main(String[] args) {
		System.out.println("start");
		Id id = new Id();
		AliasDiagram g = new AliasDiagram(id);
		
		AliasObject o1 = new AliasObject (id.getId());
		AliasObject o2 = new AliasObject (id.getId());
		AliasObject o3 = new AliasObject (id.getId());
		
		g.addEdge(o1, "a");
		g.addEdge(o1, "d");
		g.addEdge(o2, "e");
		
		ArrayList<AliasObject> l1 = new ArrayList<AliasObject>();
		l1.add(o1);
		ArrayList<ArrayList<AliasObject>> l2 = new ArrayList<ArrayList<AliasObject>>();
		l2.add(l1);
		g.changeRoot(l2);
		g.addEdge(o2, "b");
		g.addEdge(o3, "f");
		
		l1 = new ArrayList<AliasObject>();
		l1.add(o2);
		l2 = new ArrayList<ArrayList<AliasObject>>();
		l2.add(l1);
		g.changeRoot(l2);
		g.addEdge(o3, "c");
		
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
			            	add (g.getRoots());
			                add(new  ArrayList<AliasObject>() {
			                	{
			                	add (o1);
			                }});
			                add(new  ArrayList<AliasObject>() {
			                	{
			                	add (o2);
			                }});
			                add(new  ArrayList<AliasObject>() {
			                	{
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