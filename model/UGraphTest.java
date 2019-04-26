package model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class UGraphTest {

	@Test
	void addNodes() {
		UGraph graph = new UGraph();
			
		boolean node =graph.addNode(42, "testnode_0");
		assertTrue(graph.size() > 0);
		assertTrue(node);
		assertTrue(graph.getNode(42) != null, "Node is null, not inserted correctly");
	}
	
	@Test
	void deleteNode() {
		UGraph graph = new UGraph();
		
		boolean node =graph.addNode(42, "testnode_0");
		assertTrue(node, "node failed to be added");
		
		graph.removeNode(42);
		assertTrue(graph.size() == 0, "Node was not deleted.");
	}
	
	
	@Test
	void linkNodes() {
		UGraph graph = new UGraph();
		boolean node =graph.addNode(42, "testnode_0");
		boolean node2 =graph.addNode(13, "testnode_1");
		
		UNode n1 = graph.getNode(42);
		UNode n2 = graph.getNode(13);
		
		assertTrue(node, "node failed to be added");
		assertTrue(node2, "node failed to be added");
		
		graph.linkSingle(n1, n2, "");
		
		assertTrue(n1.getOutEdges().size() > 0, " no edge added to n1");
		assertTrue(n1.getInEdges().size() == 0, " improper edge added to n1");
		assertTrue(n2.getInEdges().size() > 0, " no edge added to n2");
		assertTrue(n2.getOutEdges().size() == 0, " improper edge added to n2");
		
	}
	

}
