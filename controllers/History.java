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
 * May 1st: 
 * Bug: when there is one last item per stack. Calling undo() on 
 *     the last Action would throw error.
 *  
 */


public class History {
	private Stack<Command> undoStack = new Stack<Command>();
	private Stack<Command> redoStack = new Stack<Command>();
	private AppCtrl appCtrl;

	//A list of all Commands, including Action, Undo, Redo
	//Enum of the three ACTION, REDO, UNDO
	//if A on stack, remove it and put U
	//private Stack<Command> command_sequence = new Stack<Command>();
	
	/**
	*
	*/
	public enum Command_Sequence
	{
		
	}
	/**
	 * Actions that execute gets push into the undoStack() to have undo function
	 */
	public void execute (Command cmd)
	{
		undoStack.push(cmd);
		command_sequence.push(cmd);
	}
	
	/**
	 * undo() will take the cmd from the undoStack and pop the top cmd
	 * .that cmd will then be send over to appCtrl to do the opposite of it 
	 * i.e: if undoCmd has ADD_NODE then appCtrl will execute the opposite, DELETE_NODE
	 */
	public void undo()
	{
		System.out.println("undo()");
		//If the undoStack is not empty (ie, there's things to be undone)
		//pop the top command of undo and push that cmd int the redo stack
		if (!undoStack.isEmpty() && )
		{
			command_sequence.push(undoCmd);
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
		if ( !redoStack.isEmpty() && command_sequence.top
		{
			command_sequence.push(redoCmd);
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
