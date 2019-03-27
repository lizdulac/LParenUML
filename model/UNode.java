package model;

import java.util.*;

public class UNode {

	private Integer id;
	private String name;
	

	private ArrayList<UEdge> edgeStart = new ArrayList();
	private ArrayList<UEdge> edgeEnd = new ArrayList();
	
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
	
	public UNode(Integer id, String name, ArrayList<UEdge> end, ArrayList<UEdge> start )
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
	
	public void addOutEdge( UEdge e){
		edgeStart.add(e);											
	}
	
	public void addInEdge( UEdge e){
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
