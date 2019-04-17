package model;

import java.util.*;

/**
 * The UNode class keeps a list of outgoing edges and incoming edges. 
 * 
 * @author jamesdryver
 *
 */
public class UNode {

	private Integer id;
	private String name;

	private ArrayList<UEdge> edgeStart = new ArrayList<UEdge>();
	private ArrayList<UEdge> edgeEnd = new ArrayList<UEdge>();
	
	/**
	 * Blank constructor for a UNode. This constructor should never be called.
	 * 
	 * @deprecated use UNode( Integer id, String name) instead.
	 */
	@Deprecated
	public UNode()
	{		
		  id = -1;
		  name ="";
	}
	
	/**
	 * UNode's basic constructor with an id and name.
	 * 
	 * @param id id of node
	 * @param name name of node
	 */
	public UNode( Integer id, String name)
	{
		this.id = id;
		this.name = name;
		
	}
	
	/**
	 * A UNode's fillable constructor, allowing for UEdge initialization.
	 * 
	 * @param id id of node
	 * @param name name of node
	 * @param end list of incoming edges
	 * @param start list of outgoing edges
	 */
	public UNode(Integer id, String name, ArrayList<UEdge> end, ArrayList<UEdge> start )
	{
		this.id = id;
		this.name = name;
		edgeEnd = end;
		edgeStart = start;		
	}
	
	/**
	 * Exposes the id attribute.
	 * 
	 * @return id of node
	 */
	public Integer getId()
	{
		return id;
	}
	
	/**
	 * Exposes the name attribute.
	 * 
	 * @return name of node
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 * Change the name of the Node.
	 * 
	 * @param str new node name
	 */
	public void setName(String str)
	{
		name = str;
	}
	
	/**
	 * Adds a new Edge to outgoing edges.
	 * 
	 * @param e new edge
	 */
	public void addOutEdge( UEdge e){
		edgeStart.add(e);											
	}
	
	/**
	 * Adds a new Edge to incoming edges.
	 * @param e new edge
	 */
	public void addInEdge( UEdge e){
		edgeEnd.add(e);
	}
	
	/**
	 * Exposes the list of incoming edges.
	 * 
	 * @return the list of incoming edges
	 */
	public ArrayList<UEdge> getInEdges(){
		return edgeEnd;
	}
	
	/**
	 * Exposes the list of outgoing edges
	 * 
	 * @return the list of outgoing edges
	 */
	public ArrayList<UEdge> getOutEdges(){
		return edgeStart;
	}
	
	
	
	/**
	 *  Clean the outgoing edges off of a Node.
	 * 
	 * @version 3.0 Inbound Iteration 3 
	 */
	public void cleanEdges()
	{
		//clean outgoing edges and their ends		
	    for (UEdge e: edgeStart)
	    {
	        e.start.getOutEdges ().remove (e);
	    }
		
		//clean incoming edges and their starts
		for (UEdge e: edgeEnd)
		{
		    e.end.getInEdges ().remove (e);
		}
	}
	
}
