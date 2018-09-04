package tests;

import java.util.ArrayList;

import demo.T2;
import demo.sourceClass;
import model.AliasObject;
import structures.AliasDiagram;
import structures.helpers.Helpers;

/**
 * 
 * Testing the Alias Diagram
 * 
 * @author Victor Rivera
 *
 */

public class AliasDiagramTest {

	public static void main(String[] args) {
		/*AliasDiagram graph = new AliasDiagram("sourceClass");
		
		graph.addEdge("a", "T2");
		AliasObject n = (AliasObject) graph.getObjects("a").get(0);
		n.attributes.put("c", new ArrayList<>());
		n.attributes.get("c").add(new AliasObject("T2"));
		
		graph.addEdge(new AliasObject("T2"), "a");
		graph.addEdge("b", "T2");
		graph.addEdge("yy", "String");
		
		
		
		AliasObject n2 = graph.getObjects("b").get(0);
		n2.attributes.put("d", new ArrayList<>());
		n2.attributes.get("d").add(n);
		String g = graph.toGraphViz();
		Helpers.createDot (g, "t", "source");
		System.out.println(g);*/
	}

}
