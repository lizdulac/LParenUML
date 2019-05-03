/**
 * 
 */
package model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * 
 * @author jamesdryver
 *
 */
class UNodeTest {
	@Test
	void testNode() {
		UNode n2 = new UNode(42, "testNode_2");
		
		assertTrue(n2.getName().equals("testNode_2"));
		assertTrue(n2.getId() == 42);		
	}
	
	@Test
	void setAttribute() {
		UNode n = new UNode(1, "");
		String attribute = "testing attr";
		
		n.setAttribute(0, attribute);
		assertTrue(n.getAttribute(0).equals(attribute));
	}
}
