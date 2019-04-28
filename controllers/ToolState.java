package controllers;

public enum ToolState
{
	MOVE (0), SELECT (1), ADD_NODE (2), ADD_EDGE (3), DELETE (4);

	private final int value; 

	ToolState (int value) { 
		this.value = value; 
	}
};