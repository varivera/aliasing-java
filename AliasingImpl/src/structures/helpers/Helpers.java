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
import java.util.Set;

import exceptions.Log;
import model.AliasObject;
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
		Cell c = new Cell(1);
		for (AliasObject a: g) {
			res.append ("digraph G {\n");
			res.append (nodesAndEdges (a, c));
			res.append("}\n");
		}
		notVisited (g, 0);
		return res.toString();
	}
	
	/**
	 * 
	 * @param ao root node of the graph
	 * @param id starting id for nodes
	 * @return string representation of the diagram (nodes and their
	 * 		relationship)
	 */
	private static String nodesAndEdges (AliasObject ao, Cell id) {
		ArrayList<String> lines = new ArrayList<String>();
		
		Deque <AliasObject> objects = new ArrayDeque <AliasObject>();
		// workaround
		HashMap <String, String> nodeIds = new HashMap <String, String>();
		
		objects.push(ao);
		
		while (!objects.isEmpty()) {
			AliasObject currentObject = objects.pop();
			if (!currentObject.isVisited()) {
				if (currentObject.idNode == null || currentObject.idNode.isEmpty()) {
					currentObject.idNode = "n"+id.i;
					id.inc();
					//nodeIds.put(currentObject.idNode, currentObject.idNode);
					nodeIds.put(currentObject.idNode, currentObject.printableTypeName());
				}
				//nodeIds.put(currentObject.idNode, currentObject.idNode);
				currentObject.setVisited(true);
				for (String suc: currentObject.attributes.keySet()){
					for (AliasObject obj: currentObject.attributes.get(suc)) {
						objects.push(obj);
						if (obj.idNode == null || obj.idNode.isEmpty()) {
							obj.idNode = "n"+id.i;
							id.inc();
							//nodeIds.put(obj.idNode, obj.idNode);
							nodeIds.put(obj.idNode, obj.printableTypeName());
						}
						
						// at the beginning of the list
						lines.add(0, currentObject.idNode+"->"+obj.idNode+" [label=\""+ suc +"\"]");
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
		
		
		Deque <AliasObject> objects = new ArrayDeque <AliasObject>();
		// workaround
		HashMap <Integer, Integer> nodeIds = new HashMap <Integer, Integer>();
		
		objects.push(ao);
		
		while (!objects.isEmpty()) {
			AliasObject currentObject = objects.pop();
			if (!currentObject.isVisited()) {
				if (currentObject.idNode == null || currentObject.idNode.isEmpty()) {
					currentObject.idNode = ""+id.i;
					id.inc();
					//nodeIds.put(currentObject.idNode, currentObject.idNode);
					Integer ii = Integer.parseInt(currentObject.idNode);
					nodeIds.put(ii, ii);
				}
				//nodeIds.put(currentObject.idNode, currentObject.idNode);
				currentObject.setVisited(true);
				for (String suc: currentObject.attributes.keySet()){
					for (AliasObject obj: currentObject.attributes.get(suc)) {
						objects.push(obj);
						if (obj.idNode == null || obj.idNode.isEmpty()) {
							obj.idNode = ""+id.i;
							id.inc();
							//nodeIds.put(obj.idNode, obj.idNode);
							Integer i2 = Integer.parseInt(obj.idNode);
							nodeIds.put(i2, i2);
						}
						
						res.addEdge(new Edge (Integer.parseInt(currentObject.idNode), suc, Integer.parseInt(obj.idNode)));
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
			n.idNode = "";
			for (String suc: n.attributes.keySet()){
				for (AliasObject obj: n.attributes.get(suc)) {
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
