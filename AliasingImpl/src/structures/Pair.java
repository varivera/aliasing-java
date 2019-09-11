package structures;

import model.AliasObject;

/**
 * This class holds a pair of elements of any type (p1,p2) 
 *  
 *  @author Victor Rivera (victor.rivera@anu.edu.au)
 */

public class Pair<T1, T2> {
	public T1 prj1;
	public T2 prj2;
	
	public Pair(T1 prj1, T2 prj2) {
		this.prj1 = prj1;
		this.prj2 = prj2;
	}
	
	public String toString () {
		return "<"+
				(prj1 instanceof AliasObject? ""+((AliasObject) prj1).idNode(): prj1.toString())+","+
				(prj2 instanceof AliasObject? ""+((AliasObject) prj2).idNode(): prj2.toString())+">";
	}
}