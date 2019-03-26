package model;

import java.util.*;

public class UNode {

	private Integer id;
	private String name;
	

	private ArrayList<Edge> edgeStart = new ArrayList();
	private ArrayList<Edge> edgeEnd = new ArrayList();
	
	public UNode()
	{		
		  id = -1;
		  name ="";
	}
	
	public UNode( Integer id, String name)
	{
		this.id = id;
		this.name = name;
		
	}
	
	public UNode(Integer id, String name, ArrayList<Edge> end, ArrayList<Edge> start )
	{
		this.id = id;
		this.name = name;
		edgeEnd = end;
		edgeStart = start;		
	}
	
	public String getName()
	{
		return name;
	}
	
	public void setName(String str)
	{
		name = str;
	}
	
	public void addOutEdge( Edge e){
		edgeStart.add(e);											
	}
	
	public void addInEdge( Edge e){
		edgeStart.add(e);
	}
	
	/*
	 * Inbound Iteration 2
	 */
	public void cleanEdges()
	{
		//clean outgoing edges and their ends		
		
		//clean incoming edges and their starts
		
	}
	
}
