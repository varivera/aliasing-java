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
		//TODO: not sure if there is a need to have different roots. The Alias Diagram has changed
		//				Issue: https://github.com/varivera/aliasing-java/issues/25
		ref.newRoot();
		for (AliasObject ao: getRoots()) {
			//TODO: check if it exists
			ao.getObjects(ref);
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
			if (ao.isIn(new Variable(s))){
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
	 * @param p path in the Alias Diagram
	 * @return the set of Paths that are aliased to p. Empty if none
	 */
	public ArrayList<Path> alias (Path p) {
		// TODO 3
		return null;
	}
	
	/**
	 * aliasing queries
	 */
	
	/**
	 * @param path OO path (e.g.: a.b.c) represented as a String array (e.g.: [a,b,c])
	 * @return true is path 'path; is in Alias Diagram
	 */
	public boolean doesPathExist(String[] path) {
		assert path!=null && path.length>0;
		Deque<Pair<Integer, AliasObject>> S = new ArrayDeque <Pair<Integer, AliasObject>>();
		S.add(new Pair<Integer, AliasObject> (0, getRoots().get(0)));
		while (!S.isEmpty() ) {
			Pair<Integer, AliasObject> v = S.pop();
			//TODO: assuming no loops for now
			if (v.prj2.containsSucc(path[v.prj1])) {
				if (v.prj1 == path.length-1) {
					Helpers.notVisited(getRoots());
					return true;
				}
				for (AliasObject n: v.prj2.getSucc(path[v.prj1])) {
					S.add(new Pair<Integer, AliasObject> (v.prj1+1, n));
				}
			}
		}
		return false;
	}
	
	/**
	 * @param path OO path (e.g.: a.b.c) represented as a String array (e.g.: [a,b,c])
	 * @return the set of objects that 'path' is pointing at in the Alias Diagram
	 */
	private ArrayList<Pair<Variable, AliasObject>> pathObjects(String[] path) {
		assert path!=null && path.length>0 && doesPathExist(path);
		ArrayList<Pair<Variable, AliasObject>> res = new ArrayList<Pair<Variable, AliasObject>>();
		Deque<Pair<Integer, AliasObject>> S = new ArrayDeque <Pair<Integer, AliasObject>>();
		S.add(new Pair<Integer, AliasObject> (0, getRoots().get(0)));
		while (!S.isEmpty() ) {
			Pair<Integer, AliasObject> v = S.pop();
			// TODO: assuming no loops for now
			if (v.prj2.containsSucc(path[v.prj1])) {
				if (v.prj1 == path.length-1) {
					for (Pair<Variable, AliasObject> n: v.prj2.getSucc2(path[v.prj1])) {
						res.add(n);
					}
				}else {
					for (AliasObject n: v.prj2.getSucc(path[v.prj1])) {
						S.add(new Pair<Integer, AliasObject> (v.prj1+1, n));
					}
				}
			}
		}
		return res;
	}
	
	/**
	 * returns true if there is a path from root to any node in 'endObjects' following 'path'
	 * 	Note: the implementation evaluates the path backwards and determine whether
	 * 			it reaches the root of the Alias Diagram.
	 * It returns false, otherwise 
	 */
	private boolean isRootReach(ArrayList<Pair<Variable, AliasObject>> endObjects, String[] path) {
		assert path!=null && path.length>0;
		for (Pair<Variable, AliasObject> endO: endObjects) {
			Deque<Pair<Integer, AliasObject>> S = new ArrayDeque <Pair<Integer, AliasObject>>();
			S.add(new Pair<Integer, AliasObject> (path.length-1, endO.prj2));
			while (!S.isEmpty() ) {
				Pair<Integer, AliasObject> v = S.pop();
				if (!v.prj2.isVisited()) {
					if (!getRoots().contains(v.prj2)) {
						v.prj2.setVisited(true);
					}
					if (v.prj1 == path.length-1) {////only the last part of the paths need to be in the same computational path
						ArrayList<AliasObject> pred = endO.prj2.getPredSimilarCP (path[v.prj1], endO.prj1.getCompP());
						if (pred.size()==0) {
							Helpers.notVisited(getRoots());
						}else {
							for (AliasObject n: pred) {
								S.add(new Pair<Integer, AliasObject> (v.prj1-1, n));
							}
						}
					}else if (v.prj1 == -1) {
						Helpers.notVisited(getRoots());
						return getRoots().contains(v.prj2);
					}else {
						if (v.prj2.containsPred(path[v.prj1])) {
							for (AliasObject n: v.prj2.getPred(path[v.prj1])) {
								S.add(new Pair<Integer, AliasObject> (v.prj1-1, n));
							}
						}
					}
				}
			}
			Helpers.notVisited(getRoots());
		}
		Helpers.notVisited(getRoots());
		return false;
	}
	
	/**
	 * return true if there is at least one computational path in the Alias Diagram
	 * 	that makes p1 and p2 aliased.
	 */
	public boolean aliased (String[] p1, String[] p2) {
		return isRootReach(pathObjects(p1), p2);
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
		AliasObject o4 = new AliasObject (id.getId());
		AliasObject o5 = new AliasObject (id.getId());
		AliasObject o6 = new AliasObject (id.getId());
		AliasObject o7 = new AliasObject (id.getId());
		AliasObject o8 = new AliasObject (id.getId());
		AliasObject o9 = new AliasObject (id.getId());
		AliasObject o10 = new AliasObject (id.getId());
		AliasObject o11 = new AliasObject (id.getId());
		
		
		
		g.addEdge(o1, "a", new int[] {0});
		g.addEdge(o2, "a", new int[] {0});
		g.addEdge(o5, "c", new int[] {0});
		g.addEdge(o11, "c", new int[] {0});
		
		
		ArrayList<AliasObject> l1 = new ArrayList<AliasObject>();
		l1.add(o1);
		ArrayList<ArrayList<AliasObject>> l2 = new ArrayList<ArrayList<AliasObject>>();
		l2.add(l1);
		g.changeRoot(l2);
		g.addEdge(o3, "b", new int[] {0});
		g.addEdge(o4, "b", new int[] {1,1});
		
		l1 = new ArrayList<AliasObject>();
		l1.add(o2);
		l2 = new ArrayList<ArrayList<AliasObject>>();
		l2.add(l1);
		g.changeRoot(l2);
		g.addEdge(o5, "b", new int[] {1,1});
		
		l1 = new ArrayList<AliasObject>();
		l1.add(o5);
		l2 = new ArrayList<ArrayList<AliasObject>>();
		l2.add(l1);
		g.changeRoot(l2);
		g.addEdge(o9, "e", new int[] {0});
		g.addEdge(o10, "c", new int[] {0});
		
		l1 = new ArrayList<AliasObject>();
		l1.add(o4);
		l2 = new ArrayList<ArrayList<AliasObject>>();
		l2.add(l1);
		g.changeRoot(l2);
		g.addEdge(o7, "d", new int[] {0});
		g.addEdge(o8, "cc", new int[] {0});
		
		l1 = new ArrayList<AliasObject>();
		l1.add(o3);
		l2 = new ArrayList<ArrayList<AliasObject>>();
		l2.add(l1);
		g.changeRoot(l2);
		g.addEdge(o6, "d", new int[] {0});

		l1 = new ArrayList<AliasObject>();
		l1.add(o11);
		l2 = new ArrayList<ArrayList<AliasObject>>();
		l2.add(l1);
		g.changeRoot(l2);
		g.addEdge(o5, "c", new int[] {1,2});
		
		l1 = new ArrayList<AliasObject>();
		l1.add(o10);
		l2 = new ArrayList<ArrayList<AliasObject>>();
		l2.add(l1);
		g.changeRoot(l2);
		g.addEdge(o8, "c", new int[] {1,2});
		
		g.changeBackRoot();
		g.changeBackRoot();
		g.changeBackRoot();
		g.changeBackRoot();
		g.changeBackRoot();
		g.changeBackRoot();
		g.changeBackRoot();
		
		
		String s = Helpers.toGraphAll (g.getRoots());
		Helpers.createDot (s, "testingAliasDiagram", "source");
		Path p = new Path();
		
		assert g.doesPathExist(new String[] {"a", "b"});
		assert g.doesPathExist(new String[] {"a", "b","e"});
		assert !g.doesPathExist(new String[] {"b", "a"});
		assert !g.doesPathExist(new String[] {"a", "b","e","s"});
		assert g.doesPathExist(new String[] {"a", "b","cc"});
		assert g.doesPathExist(new String[] {"a", "b","d"});
		assert g.aliased (new String[] {"a", "b"}, new String[] {"c"});
		assert g.aliased (new String[] {"c"}, new String[] {"c", "c"});
		assert !g.aliased (new String[] {"c", "c"}, new String[] {"a","b"});
		assert g.aliased (new String[] {"c", "c", "c", "c"}, new String[] {"a","b", "cc"});
		//System.out.println(p.pathObjects(new String[] {"c", "c", "c", "c"}, g));
		
		System.out.println(">> " + g.aliased (new String[] {"c", "c"}, new String[] {"a","b"}));
		
		System.out.println("done");
	}
}