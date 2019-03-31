package model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class UGraphTest {

	@Test
	void addNodes() {
		UGraph graph = new UGraph();
		
		
		graph.addNode(42, "testnode_0");
		
		assertTrue(graph.size() > 0);
		
		
	}

}
