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


public class History {
	private Stack<Command> undoStack = new Stack<Command>();
	private Stack<Command> redoStack = new Stack<Command>();
	private AppCtrl appCtrl;

	
	/**
	 * Actions that execute gets push into the undoStack() to have undo function
	 */
	public void execute (Command cmd)
	{
		undoStack.push(cmd);
	}
	
	/**
	 * undo() will take the cmd from the undoStack and pop the top cmd
	 * .that cmd will then be send over to appCtrl to do the opposite of it 
	 *  i.e: if undoCmd has ADD_NODE then appCtrl will execute the opposite, DELETE_NODE
	 */
	public void undo()
	{
		System.out.println("undo()");
		//If the undoStack is not empty (ie, there's things to be undone)
		//pop the top command of undo and push that cmd int the redo stack
		if (!undoStack.isEmpty())
		{
			Command undoCmd = undoStack.pop();
			appCtrl.executeCommand(undoCmd, true);
			redoStack.push(undoCmd);
		} else {
			System.out.println ("Nothing to undo here");
		}
	}

	/**
	 *  redo() will be called when there is an item on the stack. 
	 *  popping the top cmd and put it in redoCmd, sending it 
	 *  over to appCtrl to run its opposite. 
	 *  i.e: if DELETE_NODE was in redoCmd, then appCtrl will 
	 *       do its opposite
	 */
	public void redo()
	{
		System.out.println("redo()");
		if ( !redoStack.isEmpty())
		{
			Command redoCmd = undoStack.pop();
			appCtrl.executeCommand(redoCmd,true);
			undoStack.push(redoCmd);

		}
		else {
			System.out.println ("Nothing to redo here");
		}
	}

	public History(AppCtrl controller)
	{
		appCtrl = controller; 
	}
}
