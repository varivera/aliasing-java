package structures.helpers;


/**
 * Stores the global number of Conditionals. This is used to determined the Computational Path
 *  
 *  @author Victor Rivera (victor.rivera@anu.edu.au)
 */


public class GlobalCond {
	private int n;
	
	public GlobalCond() {
		n = 0;
	}
	
	public int getNumber () {
		return n;
	}
	
	public void count() {
		n++;
	}
}
