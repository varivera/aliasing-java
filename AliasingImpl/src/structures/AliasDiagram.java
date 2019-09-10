package structures;

import model.AliasObject;
import model.nodeInfo;
import structures.graphRep.SetEdges;
import structures.helpers.Helpers;
import structures.helpers.Id;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

/**
 * AliasDiagram is the implementation of a graph where
 * 		nodes represent possible object computations and
 * 		edges represent class variables and their relation
 * 		to other object computation. This graph will be mainly
 * 		used to the determine when two (or more) path computations
 * 		are pointing to the same object, a.k.a. as aliasing
 * 
 *  This is a rooted-directed graph	
 *  
 *  
 *  @author Victor Rivera (victor.rivera@anu.edu.au)
 */

public class AliasDiagram {
	 
	/**
	 * the graph is rooted.
	 * 'root' represents the graph's root. It cannot be
	 * externally modified. The AliasDiagram will change 
	 * the root accordingly (moving the context of different
	 * computation) (hence the Stack).
	 * 
	 * It can be more than one root, all representing the same
	 * computation in different scenarios (hence the List).
	 * 
	 */
	private Deque<ArrayList<AliasObject>> root;
	
	/**
	 * edges of the graph are represented by the variables of the
	 * root node. No need to be represented here
	 */
	
	/**
	 * for id generation
	 */
	private Id id;
	
	/**
	 * the graph's root is the current (this) object.
	 * Its class attributes will not be present, they will
	 * be added when they are present in the source code
	 */
	public AliasDiagram (Id id) {
		
		root = new ArrayDeque<ArrayList <AliasObject>>();
		// There is an initial root (representing the root of the computation e.g. 'this')
		root.push(new ArrayList <AliasObject>());
		
		this.id = id;
		getRoots().add(new AliasObject (this.id.getId()));
	}
	

	/**
	 * Adds an edge to the graph with tag 'tag'. It checks
	 * whether the class of the root object has a class
	 * attribute 'tag'. If not, it throws an exception.
	 * If so, it creates the edge.  
	 * @param tag is the name of the class attribute
	 * @param compP is the computational path
	 */
	public void addEdge (String tag, int[] compP) {
		for (AliasObject ao: getRoots()) {
			ao.addMap(tag, compP, id.getId());
		}
	}
	
	/**
	 * checks whether 'tag' is already in the graph
	 * if not, it adds it
	 * @param tag is the name of the class attribute
	 * @param compP is the computational path
	 */
	public void initEdge (String tag, int[] compP) {
		for (AliasObject ao: getRoots()) {
			ao.initValMap(tag, compP, id.getId());
		}
	}
	

	/**
	 * Adds an edge to the graph with tag 'tag'. It checks
	 * whether the class of the root object has a class
	 * attribute 'tag'. If not, it throws an exception.
	 * If so, it creates the edge.  
	 * @param tag is the name of the class attribute
	 * @param compP is the computational path
	 */
	public void addEdge (AliasObject o, String tag, int[] compP) {
		for (AliasObject ao: getRoots()) {
			ao.addObjectAtt (o, tag, compP);
		}
	}
	
	/**
	 * 
	 * @param ref reference to update the nodeInfo
	 * update the list of objects associated to 'ref.tag', from the
	 * 			current context
	 */
	public void aliasObjects (nodeInfo ref)  {
		for (AliasObject ao: getRoots()) {
			//TODO: check if it exists
			ref.newRoot();
			
			ao.getObjects(ref.tag)
			
			ref.addObjects(ao.getObjects(ref.tag));
		}
	}
	
	public ArrayList<AliasObject> getRoots (){
		return root.peek();
	}
	
	/**
	 * @param o is a node in the Alias Diagram
	 * @return o's successors 
	 */
	public Map<String, ArrayList<AliasObject>> succ(AliasObject o) {
		
		
		return null;
	}
	
	/**
	 * @param e edges
	 * @return printable edges
	 */
	public String printEdges (Map<String, ArrayList<AliasObject>> e) {
		
		
		return null;
	}
	
	
	
	/**
	 * 
	 * @return the graph to be used by GraphViz
	 */
	public String toGraphViz () {
		assert root.size() == 1;
		return Helpers.toGraph(getRoots ());
	}
	
	/**
	 * 
	 * @return the graph as a set of edges
	 */
	public SetEdges toSetEdges () {
		assert root.size() == 1;
		return Helpers.toSetEdges(getRoots ());
	}
	
	public boolean isVariable (String s) {
		
		for (AliasObject ao: getRoots ()) {
			if (ao.isIn(new Variable(s, null))){
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * In OO computations, it is common to use the dot notation 
	 * to express that a feature will be apply to an object. e.g.
	 * 'x.getVal()' means, apply feature 'getVal()' to object 'x'.
	 * When this happens, the context of the computation is changed.
	 * 'changeRoot' implements such a change of computation 
	 * @param newRoots the new roots of the graph
	 */
	public void changeRoot (ArrayList <ArrayList<AliasObject>> newRoots){ 
		//TODO: to consider: newRoots is sent (indirectly) from nodeInfo. Maybe, there is not need
		//					for using an array of arrays. Issue: https://github.com/varivera/aliasing-java/issues/18
		// forall r: Roots
		//		i) check if source exists in r. If not, add it (UPDATE: being done before)
		//		ii) retrieve r.get (source) <- list of new roots
		//	iii) add the new roots to 'root' stack
		
		root.push(new ArrayList<AliasObject>());
		for (ArrayList<AliasObject> r: newRoots) {	
			root.peek().addAll(r);
		}
		
	}
	
	/**
	 * Once a qualified call is done, the Alias Diagram is root back
	 */
	public void changeBackRoot() {
		root.pop();
	}
	
	/**
	 * (9919): this action is not longer needed
	 * 
	 * Restore the Alias Diagram: add the 'removed' edges and remove the 'added' edges. 
	 * @param added edges in the Alias Diagram
	 * @param removed edges in the Alias Diagram
	 * 
	 * This operation is needed when analysing control structures: e.g. in a conditional,
	 * before analysing the else statement, the Alias Diagram is restored.
	 */
	/*public void restoreDiagram(ArrayList<Edge> added, ArrayList<Edge> removed) {
		// remove added
		for (Edge e: added) {
			//to delete
			System.out.println("restoreDiagram (remove added): " + e);
			//to delete
			assert e.source().succ.containsKey(e.tag());
			assert e.target().pred.containsKey(e.tag());
			e.source().succ.get(e.tag()).remove(e.target());
			e.target().pred.get(e.tag()).remove(e.source());
		}
		
		//added removed
		for (Edge e: removed) {
			System.out.println("restoreDiagram (add removed): " + e);
			if (!e.source().succ.containsKey(e.tag())) {
				e.source().succ.put(e.tag(), new ArrayList<AliasObject>());
			}
			e.source().succ.get(e.tag()).add(e.target());
			if (!e.target().pred.containsKey(e.tag())) {
				e.target().pred.put(e.tag(), new ArrayList<AliasObject>());
			}
			e.target().pred.get(e.tag()).add(e.source());
		}
	}*/
	
	
	/**
	 * @param p path in the Alias Diagram
	 * @return the set of Paths that are aliased to p. Empty if none
	 */
	public ArrayList<Path> alias (Path p) {
		// TODO 3
		return null;
	}
	
	/**
	 * @param p1 path in the Alias Diagram
	 * @param p2 path in the Alias Diagram
	 * @return true if p1 is aliased to p2. False otherwise
	 */
	public boolean areAliased (Path p1, Path p2) {
		// if both path exists and point to the same node, they are aliased 
		return p1.exists() && p2.exists() && Path.intersect(p1.getTarget(), p2.getTarget());
	}
	
	/**
	 * For Debugging and Testing phases
	 */
	
	private void printRoots() {
		
		for (ArrayList <AliasObject> a: root) {
			System.out.println (">>");
			for (AliasObject b: a) {
				System.out.println (b.idNode());
			}
		}
	}
	
	/**
	 * @param p path possibly in the Alias Diagram
	 * @return true if p is a path on the Alias Diagram. False otherwise
	 */
	public boolean isInGraph (Path p) {
		return p.exists();
	}
	
	
	/**
	 * 	Sanity check for predecessor
	 */
	
	/**
	 * 
	 * @return true if the alias diagram's predecessors are well-defined.
	 * 			false otherwise
	 */
	public boolean predecesorsOK () {
		Helpers.notVisited (getRoots());
		for (AliasObject a: getRoots()) {
			if (!predOk (a)) {
				return false;
			}
		}
		Helpers.notVisited (getRoots());
		return true;
	}
	
	/**
	 * 
	 * @param ao root node of the graph
	 * @return true is the predecessors of root 'ao' are well-defined 
	 */
	private boolean predOk (AliasObject ao) {
		Queue <AliasObject> objects = new LinkedList <AliasObject>();
		objects.add(ao);
		
		while (!objects.isEmpty()) {
			AliasObject currentObject = objects.remove();
			if (!currentObject.isVisited()) {
				currentObject.setVisited(true);
				for (Variable suc: currentObject.succ.keySet()){
					for (AliasObject obj: currentObject.succ.get(suc)) {
						//System.out.println("does pred in " + obj.idNode() + " contains " + suc + "?: " +obj.pred.containsKey(suc));
						if (!obj.pred.get(suc).contains(currentObject)) {
							System.out.println("predecessor no found. Node: " + obj.idNode() + " name: " + suc);
							return false;
						}else {
							/*System.out.println("predecessor found. Node: " + obj.idNode());
							for (AliasObject p: obj.pred.get(suc)) {
								System.out.println(p.idNode());
							}*/
						}
						objects.add(obj);
					}
				}
			}
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
		
		
		
		Path p1 = new Path(
				new ArrayList<String>() { 
		            { 
		                add("a"); 
		                add("b"); 
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
			            }});
		
		Path p2 = new Path(
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
		
		System.out.println(p1);
		System.out.println(p2);
		System.out.println("p1:p2?: " + g.areAliased(p1, p2));
		String s = Helpers.toGraphAll (g.getRoots());
		Helpers.createDot (s, "testingAliasDiagram", "source");
		System.out.println("done");
	}
}