package structures.helpers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;

import exceptions.Log;
import model.AliasObject;

public class Helpers {
	
	/**
	 * 
	 * @param g Diagram Graph
	 * @return string representation of the diagram
	 * 			to be used by GraphViz
	 */
	public static String toGraph (ArrayList<AliasObject> g) {
		StringBuilder res = new StringBuilder();
		
		for (AliasObject a: g) {
			res.append ("digraph G {\n");
			res.append (nodesAndEdges (a));
			res.append("}\n");
		}
		return res.toString();
	}
	
	/**
	 * 
	 * @param ao root node of the graph
	 * @return string representation of the diagram (nodes and their
	 * 		relationship)
	 */
	public static String nodesAndEdges (AliasObject ao) {
		ArrayList<String> lines = new ArrayList<String>();
		
		Deque <AliasObject> objects = new ArrayDeque <AliasObject>();
		// workaround
		HashMap <String, String> nodeIds = new HashMap <String, String>();
		
		int i=1;
		
		
		objects.push(ao);
		
		while (!objects.isEmpty()) {
			AliasObject currentObject = objects.pop();
			if (!currentObject.isVisited()) {
				if (currentObject.idNode == null || currentObject.idNode.isEmpty()) {
					currentObject.idNode = "n"+i;
					i++;
					//nodeIds.put(currentObject.idNode, currentObject.idNode);
					nodeIds.put(currentObject.idNode, currentObject.printableTypeName());
				}
				//nodeIds.put(currentObject.idNode, currentObject.idNode);
				currentObject.setVisited(true);
				for (String suc: currentObject.attributes.keySet()){
					for (AliasObject obj: currentObject.attributes.get(suc)) {
						objects.push(obj);
						if (obj.idNode == null || obj.idNode.isEmpty()) {
							obj.idNode = "n"+i;
							i++;
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

}
