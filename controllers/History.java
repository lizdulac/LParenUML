package controllers;
import java.util.*;


/**
 * 
 * @author Dryver, An
 *
 */

/* In our case, the GUIController basically works as the History. But I wanted
 * to create a History class to make things easier to understand.
 * History is a frame-work class that models the History for REDO UNDO 
 * but a lot more simplistic. 
 * It is purely to save executed commands and to undo/redo them
 *
 */


public final class History {
	private final Stack<Command> undoStack = new Stack<Command>();
	private final Stack<Command> redoStack = new Stack<Command>();

	
	//deprecated?
	public void execute (final Command cmd)
	{
		undoStack.push(cmd);
		//cmd.execute();
	}
	
	public void undo()
	{
		if (!undoStack.isEmpty())
		{
			Command cmd = undoStack.pop();
			
		} else {
			System.out.println ("Nothing to undo here");
		}
	}
	
	public void redo()
	{
		if (!undoStack.isEmpty())
		{
			Command cmd = redoStack.pop();
		} else {
			System.out.println ("Nothing to undo here");
		}
	}

	

}



