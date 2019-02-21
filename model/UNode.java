package model;

import java.util.*;


public class UNode {

	private String id;
	

	private ArrayList<Edge> edgeStart = new ArrayList();
	private ArrayList<Edge> edgeEnd = new ArrayList();
	
	public UNode()
	{		
		  id = "";
	}
	
	public UNode( String name)
	{
		this.id = name;
		
	}
	
	public UNode( String name, ArrayList<Edge> end, ArrayList<Edge> start )
	{
		id = name;
		edgeEnd = end;
		edgeStart = start;		
	}
	
	public String getName()
	{
		return id;
	}
	
	public void addOutEdge(UEdge e){
		edgeStart.add(e);											
	}
	
	public void addInEdge(UEdge e){
		edgeStart.add(e);
	}
	
	
	public void cleanEdges()
	{
		
	}
	
}
