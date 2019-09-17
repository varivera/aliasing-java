package structures;

import model.AliasObject;

/**
 * Variable class models the variable of the class and the computational
 * 		path in which the variable appears (a^[c], where 'a' is a variable
 * 		name and 'c' is a natural number. Computational path is
 * 		modelled using a list of natural numbers. This list has
 * 		at least one element (the computational path).
 * 
 * 		Examples
 * 		a^[0]: 	variable 'a' in the base computation
 * 		d^[1,1]: variable 'd' in a conditional. The following code might be useful
 * 				then d=xxx; end ('d' is modified not in all computational paths, only
 * 								in the ones where the conditional is executed.) 
 *  
 *  
 *  @author Victor Rivera (victor.rivera@anu.edu.au)
 */

public final class Variable {
	
	/**
	 * Variable's name
	 */
	private final String name;
	
	/**
	 * Computational Path.
	 */
	private final int[] comp;
	
	/**
	 * Mark when subsume is performed
	 */
	private AliasObject subsume;
	
	public boolean isSubsumed () {
		return subsume!=null;
	}
	
	public boolean isSubsumed(AliasObject o) {
		return subsume!=null && subsume.equals(o);
	}
	
	public Variable (String name, int[] comp) {
		assert name!=null;
		assert comp!=null && comp.length>0;
		this.name = name;
		this.comp = comp;
	}
	
	public Variable (String name) {
		assert name!=null;
		this.name = name;
		this.comp = new int[] {0};
	}
	
	public String getName() {
		return name;
	}
	
	public int[] getCompP() {
		return comp;
	}
	
	public void varSubsumed(AliasObject o) {
		subsume = o;
	}
	
	
	@Override
    public boolean equals(Object o) {
		/**
		 * TODO: depending the number of comparisons, we should come out with a 
		 * 			more efficient way of comparing them 
		 */
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Variable var = (Variable) o;
        if (!name.equals(var.name)) return false;
        if (comp.length != var.comp.length) return false;
        for (int i=0;i<comp.length;i++) {
        	if (comp[i] != var.comp[i]) return false;
        }
        return true;
    }
    @Override
    public int hashCode() {
        int result = 1;
        result = 31 * result + name.hashCode();
        for (int i=0;i<comp.length;i++) {
        	result = 31 * result + comp[i];
        }
        return result;
    }
    @Override
    public String toString() {
    	StringBuilder res = new StringBuilder();
    	res.append(name);
    	res.append("<");
    	for (int i=0;i<comp.length;i++) {
    		res.append(comp[i]);
    		if (i<comp.length-1) {
    			res.append(",");
    		}
    	}
    	res.append(">");
        return res.toString();
    }	
    
    public String toStringWithSubsume(AliasObject o) {
    	StringBuilder res = new StringBuilder();
    	res.append(name+(o.equals(subsume)?"*":""));
    	res.append("<");
    	for (int i=0;i<comp.length;i++) {
    		res.append(comp[i]);
    		if (i<comp.length-1) {
    			res.append(",");
    		}
    	}
    	res.append(">");
        return res.toString();
    }	
	
}