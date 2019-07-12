package structures;

import model.AliasObject;
import model.nodeInfo;
import structures.graphRep.SetEdges;
import structures.helpers.Helpers;
import structures.helpers.Id;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;

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
	 * It's class attributes will not be present, they will
	 * be added when they are present in the source code
	 * @param currentClass represents the current class
	 * 			being analysed. 
	 */
	public AliasDiagram (String currentClass, Id id) {
		
		root = new ArrayDeque<ArrayList <AliasObject>>();
		// There is an initial root (representing the root of the computation e.g. 'this')
		root.push(new ArrayList <AliasObject>());
		
		this.id = id;
		getRoots().add(new AliasObject (currentClass, this.id.getId()));
	}
	

	/**
	 * Adds an edge to the graph with tag 'tag'. It checks
	 * whether the class of the root object has a class
	 * attribute 'tag'. If not, it throws an exception.
	 * If so, it creates the edge.  
	 * @param tag is the name of the class attribute
	 * @param type type of the class attribute
	 */
	public void addEdge (String tag, String type) {
		for (AliasObject ao: getRoots()) {
			ao.addMap(tag, type, id.getId());
		}
	}
	
	/**
	 * checks whether 'tag' is already in the graph
	 * if not, it adds it
	 * @param tag is the name of the class attribute
	 * @param type type of the class attribute
	 */
	public void initEdge (String tag, String type) {
		for (AliasObject ao: getRoots()) {
			ao.initValMap(tag, type, id.getId());
		}
	}
	

	/**
	 * Adds an edge to the graph with tag 'tag'. It checks
	 * whether the class of the root object has a class
	 * attribute 'tag'. If not, it throws an exception.
	 * If so, it creates the edge.  
	 * @param tag is the name of the class attribute
	 */
	public void addEdge (AliasObject o, String tag) {
		for (AliasObject ao: getRoots()) {
			ao.addObjectAtt (o, tag);
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
			ref.addObjects(ao.getObjects(ref.tag));
		}
	}
	
	public ArrayList<AliasObject> getRoots (){
		return root.peek();
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
			if (ao.isIn(s)){
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
		
		root.add(new ArrayList<AliasObject>());
		for (ArrayList<AliasObject> r: newRoots) {	
			root.peek().addAll(r);
		}
	}
	
	//TODO
	public void changeBackRoot() {
		
	}



	/**
	 * For Debugging and Testing phases
	 */

}

/** TODO: to be implemented
 
*/