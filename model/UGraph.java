package softwaredev;

import java.util.*;

/*
 * A UGraph is a collection of unique UNodes that have Edges connecting the UNodes.
 * UGraph is owned by the 'Controller', Nodes and Edges are created by the Controller via the UGraph
 *  
 * 
*/
public class UGraph {
	
	public Map< Integer ,UNode> uNodes;
	
	public UGraph()
	{
		uNodes = new HashMap<Integer , UNode>();	
	}
	
	
	public UNode getNode(Integer nodeid)
	{
		return uNodes.get(nodeid);
	}
	
	public boolean addNode(Integer id, String nodeName)
	{
		if (uNodes.put( id, new UNode( id, nodeName)) != null) 
			return true;
		return false;
	}
	
	
	/*
	 * Inbound Iteration 2 
	 */
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
		Edge e = new Edge(n1, n2);
		n1.addOutEdge(e);
		n2.addInEdge(e);
	}

}
