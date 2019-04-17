package model;

import java.util.*;

/*
 * A UGraph is a collection of unique UNodes that have Edges connecting the UNodes.
 * UGraph is owned by the 'Controller', Nodes and Edges are created by the Controller via the UGraph
 *  
 * 
*/
public class UGraph {
    
    // Map of Nodes
    public Map< Integer ,UNode> uNodes;
    
       /*
    * Empty Constructor a new UGraph. Inintializes an empty Map to store unique nodes in.
       */
    public UGraph()
    {
        uNodes = new HashMap<Integer , UNode>();
    }
        
    
    public boolean addNode(Integer id, String nodeName)
    {
        if (uNodes.put( id, new UNode( id, nodeName)) != null) 
            return true;
        return false;
    }
    
    /*
     * Searches the map based on the key given
     * Parameters: Key
    */
    public UNode getNode(Integer nodeid)
    {
        return uNodes.get(nodeid);
    }
    

    
    
    /**
     * Inbound Iteration 2 
     */
    public void removeNode (String id)
    {
        uNodes.get(id).cleanEdges();
        uNodes.remove(id); //hard remove
    }
    
    
    
    /*
     * This Method links two nodes with a single UEdge
     * Parameters -
     */
    public void linkSingle(UNode n1, UNode n2, String edge)
    {
        UEdge e = new UEdge(n1, n2);
        n1.addOutEdge(e);
        n2.addInEdge(e);
    }
    
    public Integer[] getAllNodes ()
    {
        Object[] temp = uNodes.keySet ().toArray ();
        Integer[] keys = new Integer[temp.length];
        System.arraycopy (temp, 0, keys, 0, temp.length);
        Arrays.sort (keys);
        return keys;
    }

}