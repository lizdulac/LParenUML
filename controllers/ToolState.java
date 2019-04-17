package controllers;

public enum ToolState
{
	SELECT (0), ADD_NODE (1), ADD_EDGE (2), DELETE (3);

	private final int value; 

	ToolState (int value) { 
		this.value = value; 
	}
};