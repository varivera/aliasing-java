package structures.helpers;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
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
		objects.push(ao);
		int i=1;
		while (!objects.isEmpty()) {
			AliasObject <?> currentObject = objects.pop();
			if (!currentObject.isVisited()) {
				currentObject.setVisited(true);
				// to end of the list
				lines.add("n"+i+"[label=\""+currentObject.typeName()+"\"]");
				for (String suc: currentObject.attributes.keySet()){
					for (AliasObject<?> obj: currentObject.attributes.get(suc)) {
						objects.push(obj);
						// at the beginning of the list
						lines.add(0, "n"+i+"->n"+(i+objects.size())+" [label=\""+ suc +"\"]");
					}
				}
				i++;
			}
		}
		StringBuilder res = new StringBuilder();
		for (String s: lines) {
			res.append(s+"\n");
		}
		return res.toString();
	}

}
