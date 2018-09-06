package structures.helpers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import exceptions.Log;
import model.AliasObject;
import model.Routine;
import structures.AliasDiagram;
import structures.graphRep.Edge;
import structures.graphRep.SetEdges;

public class Helpers {
	
	/**
	 * 
	 * @param g Diagram Graph
	 * @return string representation of the diagram
	 * 			to be used by GraphViz
	 */
	public static String toGraph (ArrayList<AliasObject> g) {
		StringBuilder res = new StringBuilder();
		res.append ("digraph G {\n");
		for (AliasObject a: g) {
			res.append (nodesAndEdges (a));
		}
		res.append("}\n");
		notVisited (g, 0);
		return res.toString();
	}
	
	/**
	 * 
	 * @param ao root node of the graph
	 * @return string representation of the diagram (nodes and their
	 * 		relationship)
	 */
	private static String nodesAndEdges (AliasObject ao) {
		ArrayList<String> lines = new ArrayList<String>();
		
		Queue <AliasObject> objects = new LinkedList <AliasObject>();
		// workaround
		HashMap <String, String> nodeIds = new HashMap <String, String>();
		
		objects.add(ao);
		
		while (!objects.isEmpty()) {
			AliasObject currentObject = objects.remove();
			if (!currentObject.isVisited()) {
				nodeIds.put("n"+currentObject.idNode(), currentObject.printableTypeName());
				
				currentObject.setVisited(true);
				for (String suc: currentObject.mapping.keySet()){
					for (AliasObject obj: currentObject.mapping.get(suc)) {
						objects.add(obj);
						nodeIds.put("n"+obj.idNode(), obj.printableTypeName());
						
						
						// at the beginning of the list
						lines.add(0, "n"+currentObject.idNode()+"->n"+obj.idNode()+" [label=\""+ suc +"\"]");
						
					}
				}
			}
		}
		// to end of the list
		for (String ni: nodeIds.keySet()) {
			lines.add(ni+"[label=\""+nodeIds.get(ni)+"\"]");
		}
		StringBuilder res = new StringBuilder();
		for (String s: lines) {
			res.append(s+"\n");
		}
		return res.toString();
	}
	
	/**
	 * Returns not just the Alias Diagram but also the local variables
	 * (return values, local variables, arguments) of the current calls 'stackCalls'
	 * @param g Diagram Graph
	 * @param stackCall 
	 * @return string representation of the diagram
	 * 			to be used by GraphViz
	 */
	public static String toGraphAll (ArrayList<AliasObject> g, Deque <Routine> stackCall) {
		StringBuilder res = new StringBuilder();
		res.append ("digraph G {\n");
		
		for (AliasObject a: g) {
			res.append (nodesAndEdges (a));
		}
		
		for (Routine r: stackCall) {
			AliasObject [] sig = r.getSignatureObjects ();
			assert (sig.length == 3);
			res.append (nodesAndEdges (sig[0])); // return type
			res.append (nodesAndEdges (sig[1])); // arguments
			res.append (nodesAndEdges (sig[2])); // local variables
		}
		
		res.append("}\n");
		notVisited (g, 0);
		return res.toString();
	}
	
	/**
	 * creates a dot file with name 'name' in path 'path'. The dot file
	 * is a graph representation of 'g'
	 * @param g graph representation 
	 * @param name of the file
	 * @param path where the file will be located
	 */
	public static void createDot (String g, String name, String path) {
		BufferedWriter output;
		try {
			output = new BufferedWriter(new FileWriter(path+File.separator+name+".dot"));
			output.write(g);
		    output.close();
		} catch (IOException e) {
			e.printStackTrace();
			Log.log.push(e);
		}
	}
	
	/**
	 * returns the content of filePath, if exists.
	 * Throw an exception otherwise
	 */
	public static String getFileContent (String filePath) throws FileNotFoundException, IOException{
		BufferedReader br = new BufferedReader (new FileReader (filePath));
		StringBuilder sb = new StringBuilder();
		String line = br.readLine();
		while (line != null) {
			sb.append(line);
			sb.append(System.lineSeparator());
			line = br.readLine();
		}
		br.close();
		return sb.toString();
	}
	
	
	/**
	 * 
	 * @param graph set of roots in the alias diagrams
	 * @return another representation of 'graph'. As a set of edges (triples)
	 */
	public static SetEdges toSetEdges (ArrayList<AliasObject> graph) {
		SetEdges res = new SetEdges();
		Cell c = new Cell(1);
		for (AliasObject a: graph) {
			res.union(toSetEdges (a, c));
		}
		notVisited (graph, 0);
		return res;
	}
	
	/**
	 * 
	 * @param graph set of roots in the alias diagrams
	 * @return another representation of 'graph'. As a set of edges (triples)
	 */
	public static SetEdges toSetEdges (AliasObject ao, Cell id) {
		SetEdges res = new SetEdges();
		
		
		Queue <AliasObject> objects = new LinkedList <AliasObject>();
		// workaround
		HashMap <Integer, Integer> nodeIds = new HashMap <Integer, Integer>();
		
		objects.add(ao);
		
		while (!objects.isEmpty()) {
			AliasObject currentObject = objects.poll();
			if (!currentObject.isVisited()) {
				nodeIds.put(currentObject.idNode(), currentObject.idNode());
				
				currentObject.setVisited(true);
				for (String suc: currentObject.mapping.keySet()){
					for (AliasObject obj: currentObject.mapping.get(suc)) {
						objects.add(obj);
						nodeIds.put(obj.idNode(), obj.idNode());
						res.addEdge(new Edge (currentObject.idNode(), suc, obj.idNode()));
					}
				}
			}
		}
		
		return res;
	}
	
	
	/**
	 * sets all nodes to visited False 
	 * @param g the graph to be visited
	 */
	public static void notVisited (ArrayList<AliasObject> g, int i) {
		if (i<0 || i >= g.size()) return;
		AliasObject n = g.get (i);
		ArrayList<AliasObject> visited = new ArrayList<AliasObject>();
		if (!isIn (n, visited)) {
			visited.add(n);
		}
		int ind=0;
		
		while (ind < visited.size()) {
			n = visited.get(ind);
			n.setVisited(false);
			for (String suc: n.mapping.keySet()){
				for (AliasObject obj: n.mapping.get(suc)) {
					if (!isIn (obj, visited)) {
						visited.add(obj);
					}
				}
			}
			ind++;
		}
		notVisited (g,i+1);
	}
	
	public static boolean isIn (AliasObject a, ArrayList<AliasObject> l) {
		for (AliasObject obj: l) {
			if (a == obj) return true;
		}
		
		return false;
	}
	
	static class Cell{
		int i;
		Cell (int i){ this.i = i; }
		public void inc () { i++; };
	}

}
