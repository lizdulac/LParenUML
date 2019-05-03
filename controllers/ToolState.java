package controllers;

public enum ToolState
{
	MOVE_GRAPH (0), SELECT_MOVE (1), ADD_NODE (2), ADD_EDGE (3), DELETE (4);

	private final int value; 

	ToolState (int value) { 
		this.value = value; 
	}
};