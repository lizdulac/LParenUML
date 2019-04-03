package gui;
import model.*;

import java.util.*;

import gui.Command.Action;
import javafx.scene.Node;
import javafx.stage.Stage;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.layout.Pane;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.MouseDragEvent;

import javafx.geometry.Point2D;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;


public class GUIcontroller
{
    public enum ToolState
    {
        SELECT (0), ADD_NODE (1), ADD_EDGE (2), DELETE (3);
        
        private final int value; 

        ToolState (int value) { 
            this.value = value; 
        }
    };

    private static GUIcontroller sharedController;
    private GUIview theView;
    private UGraph theGraph;
    private ToolState toolState;
    private int uNodeId;
    private Line currentEdge;
    private Boolean isCanvasRelease;
    private final ObjectProperty<Point2D> lastClick;

    /**Stacks for redo/undo functionality */
    private Stack<Command> undo_stack;
    private Stack<Command> redo_stack;
    
    
    private GUIcontroller (Stage stage)
    {
        theGraph = new UGraph ();
        toolState = ToolState.SELECT;
        uNodeId = 0;
        isCanvasRelease = true;
        lastClick = new SimpleObjectProperty<> ();
        undo_stack = new Stack<Command>();
        redo_stack = new Stack<Command>();        

        System.out.println ("\n******* Start *******\n");
        theView = new GUIview (this, stage);
        stage.show ();
    }

    public static GUIcontroller getSharedController (Stage stage)
    {
        if (sharedController == null)
        {
            sharedController = new GUIcontroller (stage);
        }
        return sharedController;
    }

    public ToolState getToolState ()
    {
        return toolState;
    }

    public Integer nextNodeId ()
    {
        return (Integer) (++uNodeId);
    }

    // ******************************** EVENT HANDLERS ********************************

    public EventHandler<ActionEvent> buttonClick = new EventHandler<ActionEvent> ()
    {
        @Override
        public void handle (ActionEvent e)
        {
            ToolState sourceButton = (ToolState) ((Node) e.getSource ()).getUserData ();

            switch (sourceButton)
            {
            case SELECT:
                toolState = ToolState.SELECT;
                break;
            case ADD_NODE:
                toolState = ToolState.ADD_NODE;
                break;
            case ADD_EDGE:
                toolState = ToolState.ADD_EDGE;
                break;
            case DELETE:
                toolState = ToolState.DELETE;
                break;
            default:
                toolState = ToolState.SELECT;
            }
            
//            System.out.println ("TSTATE: changed to " + toolState);
        }
    };

    public EventHandler<MouseEvent> canvasMousePress = new EventHandler<MouseEvent> ()
    {
        @Override
        public void handle (MouseEvent e)
        {
            if (e.getTarget () == e.getSource ())
            {
                if (toolState == ToolState.ADD_NODE)
                {
                                    	
                	int id = nextNodeId ();
                    String name = ((Character) ((char) (id + 96))).toString ();
                    
                    /*data order for add Node:*/ 
                    execute_command(packageAction( Action.ADD_NODE, id, name, e.getX(), e.getY()), false, false);
                    
//                    System.out.println ("U-NODE: node created -> id=" + id + " name=" + name);
                }
                else
                {
//                    System.out.println ("CANVAS: canvas clicked, no action");
                }
            }
        }
    };

    public EventHandler<MouseEvent> canvasMouseRelease = new EventHandler<MouseEvent> ()
    {
        @Override
        public void handle (MouseEvent e)
        {
            if (toolState == ToolState.ADD_EDGE)
            {
                theView.removeEdge (currentEdge);
                currentEdge = null;

//                System.out.println("ADD_EDGE canvasMouseRelease");
            }   
        }
    };

    public EventHandler<MouseEvent> uNodeMousePress = new EventHandler<MouseEvent> ()
    {
        @Override
        public void handle (MouseEvent e)
        {
            Point2D clickPoint = new Point2D(e.getSceneX (), e.getSceneY ());
            // lastClick is a class variable of GUIcontroller
            lastClick.set(clickPoint);
            Pane srcNode = (Pane) ((Node) e.getSource()).getParent ();

            if (toolState == ToolState.ADD_EDGE)
            {
                // currentEdge is a class variable of GUI controller
                currentEdge = theView.beginEdgeDraw (srcNode, lastClick.get());
                // line must be temporarily transparent to any mouse clicks
                currentEdge.setMouseTransparent(true);
            }
            else if (toolState == ToolState.DELETE)
            {
                theView.deleteNode (srcNode);
            }
//            System.out.println ("U-NODE: uNode clicked");
        }
    };

    public EventHandler<MouseEvent> uNodeMouseRelease = new EventHandler<MouseEvent> ()
    {
        @Override
        public void handle (MouseEvent e)
        {
            // mouse is released within some element other than the canvas
            if( !isCanvasRelease )
            {
                // consume MouseEvent, prevent canvasMouseRelease from getting it
                e.consume();
            }

            if (toolState == ToolState.ADD_EDGE)
            {
//                System.out.println("isCanvasRelease: "+isCanvasRelease);
            }
        }
    };

    public EventHandler<MouseEvent> uNodeDragDetected = new EventHandler<MouseEvent> ()
    {
        @Override
        public void handle (MouseEvent e)
        {
            // default assumption is true, may be falsified by another handler
            isCanvasRelease = true;
            Pane srcNode = (Pane) e.getSource();

            if (toolState == ToolState.ADD_EDGE)
            {
                // this call is required for uNodeDragRelease to operate
                srcNode.startFullDrag();

//              System.out.println("startFullDrag: "+srcNode.getUserData());
            }
        }
    };

    
    public EventHandler<MouseEvent> uNodeDrag = new EventHandler<MouseEvent> ()
    {
        @Override
        public void handle (MouseEvent e)
        {
            Pane srcNode = (Pane) e.getSource();

            if(toolState == ToolState.SELECT)
            {
                Point2D dragPoint = new Point2D(e.getSceneX(), e.getSceneY());
                theView.moveNode(srcNode, lastClick.get(), dragPoint);
                lastClick.set(dragPoint);
//              System.out.println("U-NODE: drag");
            }
            else if (toolState == ToolState.ADD_EDGE)
            {
                // update mouse/cursor coordinates
                Point2D dragPoint = new Point2D(e.getX(), e.getY());
                theView.animateEdge(srcNode, currentEdge, dragPoint);
            }
        }
    };

    public EventHandler<MouseDragEvent> uNodeDragRelease = new EventHandler<MouseDragEvent> ()
    {
        @Override
        public void handle (MouseDragEvent e)
        {
            // mouse released within a UNode
            isCanvasRelease = false;

            Pane srcNode = (Pane) e.getSource();

            if (toolState == ToolState.ADD_EDGE)
            {
                
            	AnchorMgr a = new AnchorMgr(srcNode);
            	Point2D releasePoint = a.getNearAnchor(new Point2D(e.getX(), e.getY()));
                execute_command(packageAction( Action.ADD_EDGE, srcNode, currentEdge, releasePoint ), false, false);

                // dragging is over, the line can begin accepting mouse events again
                currentEdge.setMouseTransparent(false);
            }
        }
    };
    
    

    /**
     * Packages the parameters and the type of action into a Command class.
     * The execute_command method or other invoker style methods are responsible for recasting the objects.
     * 
     * @param type declared in the Action enum in @Command.java 
     * @param objects a templated list of parameters cast as objects. 
     */
    private Command packageAction(Action type, Object ... objects)
    {
    	return  new Command(type,objects);
    }
    
    /**
     * Pushes a Command onto the appropriate stack.
     * 
     * @version 2.0 iteration 2; this feature will be moved to History in iteration 3  
     * @param cmd the Command to be pushed
     * @param redo determines which stack to place it on
     */
    private void pushAction(Command cmd, boolean redo) {
    	if(redo)
    		redo_stack.push(cmd);
    	else
    		undo_stack.push(cmd);
    }

    /**
     * Pop a Command onto the appropriate stack.
     * 
     * @version 2.0 iteration 2; this feature will be moved to History in iteration 3  
     * @param redo determines which stack to pop
     */
    private Command popAction(boolean redo)
    {
    	if(redo)
    		return redo_stack.pop();
    	else
    		return undo_stack.pop();
    }
    
    
    //params and return subject to change
    //inbound iteration 3 on click
    private void undo()
    {
    	
    	
    }
    
    //params and return subject to change
    //inbound iteration 3 on click
    private void redo()
    {
    	
    }
    

    /**
     * This method takes a packaged Command and executes it on the prerequisite
     * that the cmd action has all relevant data needed to call its associated methods.
     *
     * @param cmd the command to be executed
     * @param isUndo if this value is true then the command is from the History class(inbound iteration 3)
     * @return the command executed
     */
    private boolean execute_command(Command cmd, boolean isUndo) {
    	
    	if( isUndo && isRedo) {
    		return false;
    	}
    	
    	Object[] data = cmd.getData();
    	
    	if(cmd.actionType == Action.ADD_EDGE)
    	{
    		
    		if(data.length != 3)
    		{
    			System.out.println("Data for adding an edge is incorrect");
    			System.out.println("Data list was expected to be 2 but was: " + data.length);
    			return false;
    		}
    		
    		pushAction(cmd, false);
    		
        	theView.endEdgeDraw((Pane)data[0], (Line)data[1], (Point2D)data[2]);
        	
        	return true;
    	}
    	else if(cmd.actionType == Action.ADD_NODE)
    	{
    		
    		if(data.length != 4)
    		{
    			System.out.println("Data for adding a node is incorrect");
    			System.out.println("Data list was expected to be 4 but was: " + data.length);
    			return false;
    		}
    		
    		pushAction(cmd, false);
    		
            theGraph.addNode((Integer)data[0], (String)data[1]);
            theView.drawNode ((double)data[2],(double)data[3], (Integer)data[0]);
    		
    		return true;

    	}
    	else if(cmd.actionType == Action.DELETE_NODE)
    	{
    		
    		
    		return true;

    	}
    	else if(cmd.actionType == Action.DELETE_NODE)
    	{
    		
    		
    		return true;
    	}
    	
    	
    	return false;
    }
    
}
