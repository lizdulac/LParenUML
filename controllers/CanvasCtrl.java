package controllers;
import model.*;
import views.*;

import javafx.scene.Node;
import javafx.scene.shape.Line;
import javafx.scene.layout.Pane;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.MouseDragEvent;

import javafx.geometry.Point2D;
import javafx.event.EventHandler;
import controllers.Command.Scope;
import controllers.Command.Action;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class CanvasCtrl
{
    private AppCtrl appCtrl;
    private PropertiesCtrl propCtrl;
    private UGraph theGraph;
    private CanvasView canvasView;
    
    private int uNodeId;
    private Boolean isCanvasRelease;
    private Line currentEdge;
    private UNode currentNode;
    private final ObjectProperty<Point2D> lastClick;

	public CanvasCtrl (AppCtrl controller)
    {
        appCtrl = controller;
        propCtrl = appCtrl.getPropCtrl ();
        theGraph = appCtrl.getGraph ();
        canvasView = new CanvasView (this, theGraph);
    	
        uNodeId = 0;
        isCanvasRelease = true;
        currentEdge = new Line ();
        currentNode = null;
        lastClick = new SimpleObjectProperty<> ();
    }

    public Pane getCanvas ()
    {
        return canvasView.getCanvas ();
    }

    public Integer nextNodeId ()
    {
        return ++uNodeId;
    }

    public UNode getCurrentNode ()
    {
        return currentNode;
    }
    
    /**
     * Packages the parameters and the type of action into a Command class.
     * The execute_command method or other invoker style methods are responsible for recasting the objects.
     * 
     * @param type declared in the Action enum in @Command.java 
     * @param objects a templated list of parameters cast as objects. 
     */
    private Command packageAction(Action type, Scope scope, Object ... objects)
    {
        //add scope
    	return new Command(type, scope, objects);
    }
    
    public EventHandler<MouseEvent> canvasMousePress = new EventHandler<MouseEvent> ()
    {
        @Override
        public void handle (MouseEvent e)
        {
            if (e.getTarget () == e.getSource ())
            {
                if (appCtrl.getToolState () == ToolState.ADD_NODE)
                {
                	int id = nextNodeId ();
                    String name = ((Character) ((char) (id + 96))).toString ();                    
                    appCtrl.executeCommand(packageAction(Action.ADD_NODE, Scope.CANVAS, id, name, e.getX(), e.getY()), false);
                }
                else
                {
                    System.out.println ("CANVAS: canvas clicked, no action");
                }
            }
        }
    };

    public EventHandler<MouseEvent> canvasMouseRelease = new EventHandler<MouseEvent> ()
    {
        @Override
        public void handle (MouseEvent e)
        {
            if (appCtrl.getToolState () == ToolState.ADD_EDGE)
            {
                canvasView.removeEdge (currentEdge);
                currentEdge = null;

                System.out.println("ADD_EDGE: canvasMouseRelease");
            }   
        }
    };

    public EventHandler<MouseEvent> uNodeMousePress = new EventHandler<MouseEvent> ()
    {
        @Override
        public void handle (MouseEvent e)
        {
            Point2D clickPoint = new Point2D(e.getSceneX (), e.getSceneY ());
            lastClick.set(clickPoint);

            Pane srcNode = (Pane) ((Node) e.getSource()).getParent ();
            
            // appCtrl.getNode ()
            UNode uNode = theGraph.getNode ((int) srcNode.getUserData ());

            if (appCtrl.getToolState () == ToolState.SELECT)
            {
            	appCtrl.executeCommand(packageAction(Action.SELECT_NODE, Scope.PROPERTY, uNode.getId(), uNode.getName()), false);
                System.out.println ("U-NODE: this node selected: " + currentNode);
            }
            if (appCtrl.getToolState () == ToolState.ADD_EDGE)
            {                
                currentEdge = canvasView.beginEdgeDraw (srcNode, lastClick.get());
                currentEdge.setUserData (uNode.getId ());

                // line must be temporarily transparent to any mouse clicks
                currentEdge.setMouseTransparent(true);
            }            
            else if (appCtrl.getToolState () == ToolState.DELETE)
            {
                canvasView.deleteNode (srcNode);
            }
            //System.out.println ("U-NODE: uNode clicked");
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

            if (appCtrl.getToolState () == ToolState.ADD_EDGE)
            {
                System.out.println("isCanvasRelease: "+isCanvasRelease);
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

//            System.out.println("startFullDrag: "+srcNode.getUserData());

            if (appCtrl.getToolState () == ToolState.ADD_EDGE)
            {
                // this call is required for uNodeDragRelease to operate
                srcNode.startFullDrag();
            }
        }
    };

    public EventHandler<MouseEvent> uNodeDrag = new EventHandler<MouseEvent> ()
    {
        @Override
        public void handle (MouseEvent e)
        {
            Pane srcNode = (Pane) e.getSource();

            if(appCtrl.getToolState () == ToolState.SELECT)
            {
                Point2D dragPoint = new Point2D(e.getSceneX(), e.getSceneY());
                canvasView.moveNode(srcNode, lastClick.get(), dragPoint);
                lastClick.set(dragPoint);
//              System.out.println("U-NODE: drag");
            }
            else if (appCtrl.getToolState () == ToolState.ADD_EDGE)
            {
                // update mouse/cursor coordinates
                Point2D dragPoint = new Point2D(e.getX(), e.getY());
                canvasView.animateEdge(srcNode, currentEdge, dragPoint);
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
            UNode uNode = theGraph.getNode ((int) srcNode.getUserData ());

            if (appCtrl.getToolState () == ToolState.ADD_EDGE)
            {
                Point2D releasePoint = new Point2D(e.getX(), e.getY());

                UNode start = theGraph.getNode ((int) currentEdge.getUserData ());
                UNode end = uNode;
                String edgeName = start.getName () + end.getName ();
                //appctrl._______()
                theGraph.linkSingle (start, end, edgeName);

                canvasView.endEdgeDraw(srcNode, currentEdge, releasePoint);

                // dragging is over, the line can begin accepting mouse events again
                currentEdge.setMouseTransparent(false);
            }
        }
    };
    
    /**
     * data[0] - id
     * data[1] - name
     * data[2] - x
     * data[3] - y
     * 
     * @param cmd
     * @param isHistory
     * @return
     */
    public boolean executeCommand(Command cmd, boolean isHistory) {

    	/*if(isHistory) {
    		return false;
    	}*/
    	Object[] data = cmd.getData();
    	
    	if(cmd.actionType == Action.ADD_NODE)
    	{
    	    System.out.printf ("CanvasCtrl: ADD_NODE Command\n");
    		if(data.length != 4)
    		{
    			System.out.println("Data for adding a node is incorrect");
    			System.out.println("Data list expected 4 items but had: " + data.length);
    			return false;
    		}
    		
            theGraph.addNode((Integer)data[0], (String)data[1]);
            canvasView.drawNode ((double)data[2],(double)data[3], (Integer)data[0]);
            System.out.println("EXECMD: CanvasCtrl added node");
    		return true;
    	}
    	else if(cmd.actionType == Action.ADD_EDGE)
    	{
    	    // public void linkSingle(UNode n1, UNode n2, String edge)
    	    if(data.length != 4/*change*/)
    	    {
                System.out.println("Data for adding a node is incorrect");
                System.out.println("Data list expected 4 items but had: " + data.length);
                return false;
    	    }
//    	    Pane canvas = canvasView.getCanvas ().getChildren ().get (index)
    	    
    	}
    	
    	return false;
    }
}