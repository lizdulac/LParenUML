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
 ******
 * April 26: History might need to know the timeline action sequence
 * to determine where how many undo you can spam and how many redo 
 * you can spam. 
 * peaking at the top of the stack and see what the latest action is when
 * redo / undo is called to see if the action is redoable or undoable.
 ******
 * For multi-levels of undo. Instead of remembering the latest command, 
 * Keep a list of all commands and a reference to the 'current' one.
 * When we execute a command, we append it to the list and that 
 * represent where we currently are. 
 * When undo(), we undo() the current command and move the current
 * pointer back. When redo(), we advance the pointer and then 
 * re-execute the cmd. But if they choose to have a new Action
 * after undoing some already before, everything in the list after
 * the current cmd will be wiped. 
 */


public final class History {
	private static History instance = null;
	private final Stack<Command> undoStack = new Stack<Command>();
	private final Stack<Command> redoStack = new Stack<Command>();

	//Keeping a list of commands, executes, undoes and redoes 
	//by using the concept of history
	private List<Command> command_sequence();
	
	public void execute (final Command cmd)
	{
		undoStack.push(cmd);
		//cmd.execute();
	}
	
	/**
	 * undo() will take the cmd from the undoStack and pop the top cmd
	 * for it to be disapear from the view
	 */
	public void undo()
	{
		//If the undoStack is not empty (ie, there's things to be undone)
		//pop the top command of undo and push that cmd int the redo stack
		if (!undoStack.isEmpty())
		{
			Command undoCmd = undoStack.pop();
			redoStack.push(undoCmd);
			//cmd.undo();
		} else {
			System.out.println ("Nothing to undo here");
		}
	}

	/**
	 * redo() must leave exactly the same setate as execute(). 
	 * very similar to execute
	 */
	public void redo()
	{
		//If the redo stack is not empty and the command is redoAble
		// pop the cmd from redo stack and push it over to undoStack
		// Redo would re-execute the 
		if ( !redoStack.isEmpty())
		{
			redoCmd = undoStack.top();
			undoStack.push(redoCmd);

			//redoCmd.execute ???????
		}
		else {
			System.out.println ("Nothing to redo here");
		}
	}
	
	}
	private History() {}
}



