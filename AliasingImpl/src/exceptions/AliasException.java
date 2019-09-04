package exceptions;

/**
 * This class captures all exceptions during an Alias Analysis computation.	
 *  
 *  
 *  @author Victor Rivera (victor.rivera@anu.edu.au)
 */

public class AliasException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2738100477254712338L;
	
	public AliasException (String message) {
		super (message);
	}
	
}
