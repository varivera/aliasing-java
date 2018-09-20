package structures;

import model.AliasObject;
import model.nodeInfo;
import structures.graphRep.SetEdges;
import structures.helpers.Helpers;
import structures.helpers.Id;

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
		root = new ArrayList <AliasObject>();
		this.id = id;
		root.add(new AliasObject (currentClass, this.id.getId()));
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
		for (AliasObject ao: root) {
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
		for (AliasObject ao: root) {
			ao.addObjectAtt (o, tag);
		}
	}
	
	/**
	 * 
	 * @param ref reference to update the nodeInfo
	 * update: the list of objects associated to 'ref.tag', from the
	 * 			current context
	 */
	public void aliasObjects (nodeInfo ref)  {
		for (AliasObject ao: root) {
			//TODO: check if it exists
			ref.addObjects(ao.getObjects(ref.tag));
		}
	}
	
	public ArrayList<AliasObject> getRoots (){
		return root;
	}
	
	/**
	 * 
	 * @return the graph to be used by GraphViz
	 */
	public String toGraphViz () {
		return Helpers.toGraph(root);
	}
	
	/**
	 * 
	 * @return the graph as a set of edges
	 */
	public SetEdges toSetEdges () {
		return Helpers.toSetEdges(root);
	}
	
	public boolean isVariable (String s) {
		
		for (AliasObject ao: root) {
			if (ao.isIn(s)){
				return true;
			}
		}
		
		return false;
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