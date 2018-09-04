package structures;

import model.AliasObject;
import model.nodeInfo;
import structures.helpers.Helpers;
import java.util.ArrayList;

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
 *  @author Victor Rivera
 */

public class AliasDiagram {
	
	/**
	 * the graph is rooted.
	 * 'root' represents the graph's root. It cannot be
	 * externally modified, but AliasDiagram will change 
	 * the root accordingly (moving the context of different
	 * computation)
	 * 
	 * It can be more than one root (all representing the same
	 * computation in different scenarios)
	 * 
	 */
	private ArrayList<AliasObject> root;
	
	/**
	 * edges of the graph are represented by the variables of the
	 * root node. No need to be represented here
	 */
	
	
	
	/**
	 * the graph's root is the current (this) object.
	 * It's class attributes will not be present, they will
	 * be added when they are present in the source code
	 * @param currentClass represents the current class
	 * 			being analysed. 
	 */
	public AliasDiagram (String currentClass) {
		root = new ArrayList <AliasObject>();
		root.add(new AliasObject (currentClass));
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
		for (AliasObject ao: root) {
			ao.addAttribute (tag, type);
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
		for (AliasObject ao: root) {
			ao.addObjectAtt (o, tag);
		}
	}
	
	/**
	 * 
	 * @param attributeName
	 * @return the list of objects associated to attributeName, from the
	 * 			current context
	 */
	public void aliasObjects (nodeInfo ref)  {
		for (AliasObject ao: root) {
			//TODO: check if it exists
			ref.addObjects(ao.getObjects(ref.tag));
		}
	}
	
	/**
	 * 
	 * @param tag variable name (in root)
	 * @return the set of alias objects that were last visited, with tag 'tag'
	 */
	public ArrayList<ArrayList<AliasObject>> getLastVisited (String tag){
		ArrayList<ArrayList<AliasObject>> res = new ArrayList<ArrayList<AliasObject>>(root.size());
		for (AliasObject r: root) {
			res.add(r.attributes.get(tag)); //reference to it
		}
		return res;
	}
	
	/**
	 * 
	 * @return the graph to be used by GraphViz
	 */
	public String toGraphViz () {
		return Helpers.toGraph(root);
	}

	/**
	 * For Debugging and Testing phases
	 */

}

/** TODO: to be implemented
 * **
	 * In OO computations, it is common to use the dot notation 
	 * to express that a feature will be apply to an object. e.g.
	 * 'x.getVal ()' means, apply feature 'getVal ()' to object 'x'.
	 * When this happens, the context of the computation is changed.
	 * 'changeRoot' implements such a change of computation 
	 * @param newRoots what are the new roots of the graph
	 *
	public void changeRoot (ArrayList <AliasObject> newRoots){ 
		// newRoots should be
	}

 *
*/