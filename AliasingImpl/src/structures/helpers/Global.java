package structures.helpers;


/**
 * Stores the number of new computational paths. When the analyser encounters a conditional
 * or a loop, this number will increase. The number is kept globally.
 *  
 *  @author Victor Rivera (victor.rivera@anu.edu.au)
 */


public class Global {
	private int n;
	private int iter;
	
	public Global() {
		n = 0;
		iter = 3;
	}
	
	public int getCurrentCP () {
		return n;
	}
	
	public void increaseCP() {
		n++;
	}
	
	public int getIter() {
		return iter;
	}
}
