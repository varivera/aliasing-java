package structures.helpers;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;

import model.AliasObject;

public class Helpers {
	
	/**
	 * 
	 * @param g Diagram Graph
	 * @return string representation of the diagram
	 * 			to be used by GraphViz
	 */
	public static String toGraph (AliasObject <?> g) {
		StringBuilder res = new StringBuilder();
		res.append ("digraph G {\n");
		res.append (nodesAndEdges (g));
		res.append("}\n");		
		return res.toString();
	}
	
	public static String nodesAndEdges (AliasObject <?> ao) {
		ArrayList<String> lines = new ArrayList<String>();
		
		Deque <AliasObject <?>> objects = new ArrayDeque <AliasObject <?>>();
		// workaround
		HashMap <String, String> nodeIds = new HashMap <String, String>();
		
		int i=1;
		
		
		objects.push(ao);
		
		while (!objects.isEmpty()) {
			AliasObject <?> currentObject = objects.pop();
			if (!currentObject.isVisited()) {
				//nodeIds.put(currentObject.idNode, currentObject.typeName());
				//if (nodeIds.containsKey(key))
				if (!nodeIds.containsKey(currentObject.idNode)) {
					currentObject.idNode = "n"+i;
					i++;
					nodeIds.put(currentObject.idNode, currentObject.idNode);
				}
				nodeIds.put(currentObject.idNode, currentObject.idNode);
				currentObject.setVisited(true);
				for (String suc: currentObject.attributes.keySet()){
					for (AliasObject<?> obj: currentObject.attributes.get(suc)) {
						objects.push(obj);
						if (!nodeIds.containsKey(currentObject.idNode)) {
							currentObject.idNode = "n"+i;
							i++;
							nodeIds.put(currentObject.idNode, currentObject.idNode);
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

}
