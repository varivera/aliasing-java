package tests;

import java.util.ArrayList;

import demo.T2;
import demo.sourceClass;
import model.AliasObject;
import structures.AliasDiagram;

public class AliasDiagramTest {

	public static void main(String[] args) {
		AliasDiagram<?> graph = new AliasDiagram <sourceClass>(
				new AliasObject <sourceClass>(new sourceClass()));
		
		graph.addEdge("a");
		graph.addEdge(new AliasObject<T2>(new T2()), "a");
		graph.addEdge("b");
		
		/*{a : [obj (a), obj (a2)]
		 * b : [obj (b)]}
		*/
		AliasObject <?> n = (AliasObject<?>) graph.getObjects("a").get(0);
		n.attributes.put("c", new ArrayList<>());
		n.attributes.get("c").add(new AliasObject<T2>(new T2()));
		/*AliasObject <?> n2 = (AliasObject<?>) graph.getObjects("b").get(0);
		n2.attributes.put("d", new ArrayList<>());
		n2.attributes.get("d").add(n);*/
		
		System.out.println(graph.toGraphViz());
	}

}
