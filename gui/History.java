package gui;
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
	private static History instance = null;
	private final stack<Command> undoStack = new Stack<Command>();
	
	public void execute (final Command cmd)
	{
		undoStack.push(cmd);
		cmd.execute();
	}
	
	public void undo()
	{
		if (!undoStack.isEmpty())
		{
			Command cmd = undoStack.pop();
			cmd.undo();
		} else {
			System.out.println ("Nothing to undo here");
		}
	}
	public static History getInstance()
	{
		if ( History.instance == null) {
			sychronized (History.class) {
				if ( History.instance == null ) {
					History.instance = new History ();
				}
			}
		}
		return History.instance;
	}
	private History() {}
}



