package model;

import java.util.ArrayList;

/**
 * It contains relevant information about a node to
 * be used in assignments.
 * 
 *  
 *  @author Victor Rivera (victor.rivera@anu.edu.au)
 */

public class nodeInfo {
	
	/**
	 * contains the Alias Object that 'tag' is
	 * pointing at.
	 * It is an array of arrays since there could
	 * be more than one root in the alias graph.
	 */
	public ArrayList <ArrayList<AliasObject>> pointingAt;

	/**
	 * tag name of the node
	 */
	public String tag;
	
	public nodeInfo (String tag) {
		this.tag = tag;
		pointingAt = new ArrayList <ArrayList<AliasObject>>();
	}
	
	/**
	 * 
	 * @param objs to be added (the reference) to the current nodeInfo
	 */
	public void addObjects (ArrayList<AliasObject> objs) {
		pointingAt.add(objs);
	}
}
