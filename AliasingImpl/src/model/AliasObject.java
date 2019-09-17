package model;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import exceptions.AliasException;
import structures.Variable;
import structures.Pair;

/**
 * 
 * AliasObject represents a possible object computation
 * at run-time. AliasObjects are Nodes of the 
 * Alias Diagram.
 * 
 * @author Victor Rivera  (victor.rivera@anu.edu.au)
 *
 */

public class AliasObject {
	
	/**
	 * Successors of the AliasObject
	 * mapping of the AliasObject. For example,
	 * AliasObject could be the variables of a class, or 
	 * arguments of a routine
	 */
	private Map<Variable, ArrayList<AliasObject>> succ;
	
	/**
	 * Predecessor of the AliasObject
	 */
	private Map<Variable, ArrayList<AliasObject>> pred;
	
	public AliasObject(int id) {
		succ = new HashMap<Variable, ArrayList<AliasObject>>();
		pred = new HashMap<Variable, ArrayList<AliasObject>>();
		setVisited(false);
		this.id = id;
	}
	
	/**
	 * Add 'tag' to the map of the current object
	 * @param mapName
	 * @param compP: computational path
	 * @param id 
	 */
	public void addMap (String mapName, int[] compP, int id)  {
		Variable key = null;
		if (compP == null) {
			key = new Variable (mapName);
		}else {
			key = new Variable (mapName, compP);
		}
		
		if (!succ.containsKey(key)) {
			succ.put (key, new ArrayList<AliasObject>());
		}
		AliasObject newO = new AliasObject (id);
		succ.get(key).add(newO);
		//update predecessor
		newO.pred.put(key, new ArrayList<AliasObject>());
		newO.pred.get(key).add(this);
	}
	
	/**
	 * Add 'tag' to the mapping of the current
	 * object only if it was not there already
	 * @param mapName
	 * @param compP: computational path
	 * @param id 
	 */
	public void initValMap (String mapName, int[] compP, int id)  {
		Variable key = null;
		if (compP == null) {
			key = new Variable (mapName);
		}else {
			key = new Variable (mapName, compP);
		}
		if (!succ.containsKey(key)) {
			succ.put (key, new ArrayList<AliasObject>());
			AliasObject newO = new AliasObject (id);
			succ.get(key).add(newO);
			//update predecessor
			newO.pred.put(key, new ArrayList<AliasObject>());
			newO.pred.get(key).add(this);
		} 
	}
	
	/**
	 * Add object 'o' to the map[mapName] 
	 * @param o Object
	 * @param mapName tag
	 * @param compP: computational path
	 */
	public void addObjectAtt (AliasObject o, String mapName, int[] compP)  {
		Variable key = new Variable (mapName, compP);
		if (!succ.containsKey(key)) {
			succ.put (key, new ArrayList<AliasObject>());
		}
		succ.get(key).add(o); 
		//update predecessor
		if (!o.pred.containsKey(key)) {
			o.pred.put(key, new ArrayList<AliasObject>());
		}
		o.pred.get(key).add(this);
	}
	
	/**
	 * @param tagName
	 * @return the list of objects associated to a tag. This does not take
	 * 			into consideration the computational path
	 */
	public void getObjects (nodeInfo ref)  {
		for (Variable s: succ.keySet()) {
			if (s.getName().equals(ref.tag) && sameCP(ref.CurrentCP, s.getCompP())) {
				for (AliasObject o: succ.get(s)) {
					ref.addEdge(this, s, o);
				}
			}
		}
	}
	
	/**
	 * Updates the name of the key
	 * @param key
	 * @throws AliasException 
	 */
	public void updateInHold (Variable oldKey, Variable newKey) throws AliasException {
		if (!succ.containsKey(oldKey)) {
			throw new AliasException("Alias Object is not holding any key: " + oldKey);
		}
		ArrayList<AliasObject> val = succ.get(oldKey);
		succ.remove(oldKey);
		succ.put(newKey, val);
		// update the oldKey in the predecessors
		for (AliasObject p: val) {
			ArrayList<AliasObject> val2 = p.pred.get(oldKey);
			p.pred.remove(oldKey);
			p.pred.put(newKey, val2);
		}
	}
	
	/**
	 * Is the Alias Object waiting for info to be updated?
	 * @param oldKey
	 * @return
	 */
	public boolean isUpdateNeeded (Variable oldKey) {
		return succ.containsKey(oldKey);
	}
	
	/**
	 * returns the successors of 'tag' without taking into consideration
	 * the computational path
	 */
	public ArrayList<AliasObject> getSucc (String tag){
		ArrayList<AliasObject> res = new ArrayList<AliasObject>();
		
		for (Variable v: succ.keySet()) {
			if (v.getName().equals(tag)) {
				for (AliasObject o: succ.get(v)) {
					res.add(o);
				}
			}
		}
		
		return res;
	}
	
	/**
	 * Similar to getSucc but returns more information
	 */
	public ArrayList<Pair<Variable,AliasObject>> getSucc2 (String tag){
		ArrayList<Pair<Variable,AliasObject>> res = new ArrayList<Pair<Variable,AliasObject>>();
		
		for (Variable v: succ.keySet()) {
			if (v.getName().equals(tag)) {
				for (AliasObject o: succ.get(v)) {
					res.add(new Pair<Variable,AliasObject>(v, o));
				}
			}
		}
		
		return res;
	}
	
	/**
	 * returns the predecessors of 'tag' without taking into consideration
	 * the computational path
	 */
	public ArrayList<AliasObject> getPred (String tag){
		ArrayList<AliasObject> res = new ArrayList<AliasObject>();
		
		for (Variable v: pred.keySet()) {
			if (v.getName().equals(tag)) {
				for (AliasObject o: pred.get(v)) {
					res.add(o);
				}
			}
		}
		
		return res;
	}
	
	/**
	 * returns the predecessors of 'tag' that are in the same computational
	 * path 'compP'
	 */
	public ArrayList<AliasObject> getPredSimilarCP (String tag, int[] compP){
		ArrayList<AliasObject> res = new ArrayList<AliasObject>();
		
		for (Variable v: pred.keySet()) {
			if (v.getName().equals(tag) && sameCP (compP, v.getCompP())) {
				for (AliasObject o: pred.get(v)) {
					res.add(o);
				}
			}
		}
		
		return res;
	}
	
	/**
	 * returns true is cp1 and cp2 are in the same computational
	 * path
	 */
	private boolean sameCP (int[] cp1, int[] cp2) {
		assert cp1!=null && cp2!=null;
		if ((cp1.length == 1 && cp1[0] == 0) || (cp2.length == 1 && cp2[0]==0)) return true; //base CP
		if ((cp1.length > 0 && cp2.length > 0 && cp1[0]!=cp2[0])) return true; // different CP
		int[] d1;
		int[] d2;
		if (cp1.length <= cp2.length) {
			d1 = cp1;
			d2 = cp2;
		}else {
			d1 = cp2;
			d2 = cp1;
		}
		for (int i=0;i<d1.length;i++) {
			if (d1[i] != d2[i]) return false;
		}
		return true;
	}
	
	/**
	 * returns true if 'tag' is a successor. It does not take into consideration
	 * the computational path
	 */
	public boolean containsSucc (String tag){
		for (Variable v: succ.keySet()) {
			if (v.getName().equals(tag)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * returns true if 'tag' is a predecessor. It does not take into consideration
	 * the computational path
	 */
	public boolean containsPred (String tag){
		for (Variable v: pred.keySet()) {
			if (v.getName().equals(tag)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * returns true if variable 'v' is a successor. 
	 */
	public boolean containsSucc (Variable v){
		return succ.containsKey(v);
	}
	
	/**
	 * returns true if variable 'v' is a predecessor. 
	 */
	public boolean containsPred (Variable v){
		return pred.containsKey(v);
	}
	
	
	public String toString() {
		return "#" + this.hashCode();
	}
	
	
	/**
	 * Node of a graph
	 */
	
	private boolean visited;
	
	public boolean isVisited() {
		return visited;
	}

	public void setVisited(boolean visited) {
		this.visited = visited;
	}
	
	public boolean isIn (Variable tag) {
		return succ.containsKey(tag);
	}
	
	//idNode
	private int id;
	//Avoiding changes from outside the class
	public int idNode() {
		return id;
	}
		
	
	/**
	 * For Debugging and Testing phases
	 */
	
	public static void main (String[] arg) throws NoSuchFieldException, SecurityException {
		/*Field[] fields = t.type.getDeclaredFields();
		System.out.println (fields.length);
		for (int i=0;i<fields.length;i++) {
			System.out.println (fields[i].getName());
		}*/
		
	}

}
