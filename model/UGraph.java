package model;

import java.util.*;

/*
 * A UGraph is a collection of unique UNodes that have UEdges connecting the UNodes.
 * UGraph is owned by the 'Controller', Nodes and Edges are created by the Controller via the UGraph
 *  
 * 
 * 
*/
public class UGraph {
	
private AbstractMap< String ,UNode> uNodes;
	
	public UGraph()
	{
		//uNodes = new AbstractMap<String, UNode>();	
	}
	
	
	public boolean addNode(String nodeName)
	{
		if (uNodes.put(nodeName, new UNode( nodeName)) != null) 
			return true;
		return false;
	}
	
	public void removeNode (String id)
	{
		uNodes.get(id).cleanEdges();
		uNodes.remove(id); //hard remove
	}
	
	/*
	 * n1 would be the click node and n2 would be the release node
	 */
	public void linkSingle(UNode n1, UNode n2, String edge)
	{
		UEdge e = new UEdge(n1, n2);
		n1.addOutEdge(e);
		n2.addInEdge(e);
	}

}