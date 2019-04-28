package controllers;
import model.*;
import views.*;

import javafx.scene.shape.Line;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.MouseDragEvent;

import javafx.geometry.Point2D;
import javafx.event.EventHandler;
import controllers.Command.Scope;
import controllers.Command.Action;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * 
 * @author 
 *
 */
public class CanvasCtrl
{
    /******************** APPCTRL FILEIO EVENT HANDLERS *****************/
    private AppCtrl appCtrl;
    private PropertiesCtrl propCtrl;
    protected CanvasView canvasView;

    private int uNodeId;
    private int uEdgeId;
    private Boolean isCanvasRelease;
    private Line currentEdge;
    private UNode currentNode;
    private final ObjectProperty<Point2D> lastClick;

    /******************** APPCTRL FILEIO EVENT HANDLERS *****************/
    /**
     * 
     * @param controller
     */
    public CanvasCtrl (AppCtrl controller)
    {
        appCtrl = controller;
        propCtrl = appCtrl.getPropCtrl ();
        canvasView = new CanvasView (this);

        uNodeId = 0;
        uEdgeId = 0;
        isCanvasRelease = true;
        currentEdge = new Line ();
        currentNode = null;
        lastClick = new SimpleObjectProperty<> ();
    }

    /************************* CANVASCTRL GETTERS **********************/
    /**
     * 
     * @return
     */
    public Pane getCanvas ()
    {
        return canvasView.getCanvas ();
    }

    /**
     * 
     * @return
     */
    public Integer nextNodeId ()
    {
        return ++uNodeId;
    }
    
    /**
     * 
     * @return
     */
    public Integer nextEdgeId ()
    {
        return ++uEdgeId;
    }

    /**
     * 
     * @return
     */
    public UNode getCurrentNode ()
    {
        return currentNode;
    }

    /********************** CANVASCTRL EVENT HANDLERS *******************/
    /**
     * 
     */
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
                    
                    // appCtrl.addNode() AND canvasView.drawNode()
                    appCtrl.executeCommand (
                            packageAction (Action.ADD_NODE, Scope.CANVAS, id, name, e.getX (), e.getY ()), false);
                } else
                {
                    System.out.println ("CANVAS: canvas clicked, no action");
                }
            }
        }
    };

    /**
     * 
     */
    public EventHandler<MouseEvent> canvasMouseRelease = new EventHandler<MouseEvent> ()
    {
        @Override
        public void handle (MouseEvent e)
        {
            //lastClick.set (null);
            if (appCtrl.getToolState () == ToolState.ADD_EDGE)
            {
            	getCanvas().getChildren().remove(currentEdge);
                currentEdge = null;

                System.out.println ("CVSCTR: edge creation ");
            }
        }
    };

    /**
     * 
     */
    public EventHandler<MouseEvent> uNodeMousePress = new EventHandler<MouseEvent> ()
    {
        @Override
        public void handle (MouseEvent e)
        {
            Point2D clickPoint = new Point2D (e.getSceneX (), e.getSceneY ());
            lastClick.set (clickPoint);

            Region srcNode = (Region) e.getSource ();
            int id = (int) srcNode.getUserData ();

            UNode uNode = appCtrl.getNode (id);

            if (appCtrl.getToolState () == ToolState.SELECT)
            {
                appCtrl.executeCommand (
                        packageAction (Action.SELECT_NODE, Scope.PROPERTY, uNode.getId (), uNode.getName ()), false);
                System.out.println ("U-NODE: this node selected: " + currentNode);
            }
            else if (appCtrl.getToolState () == ToolState.ADD_EDGE) {
            // DO NOT package these ADD_EDGE actions and send to executeCommand()
            // all necessary ADD_EDGE actions are taken care of in uNodeDragRelease
            	
                currentEdge = canvasView.beginEdgeDraw (srcNode, lastClick.get ());                
                // currentEdge ID must be the same as startNode ID 
                currentEdge.setUserData (uNode.getId ());
                // line must be temporarily transparent to any mouse clicks
                currentEdge.setMouseTransparent (true);
                
            } else if (appCtrl.getToolState () == ToolState.DELETE)
            {
                appCtrl.executeCommand(packageAction (Action.DELETE_NODE, Scope.CANVAS, id), false);
            }
            // System.out.println ("U-NODE: uNode clicked");
        }
    };

    /**
     * 
     */
    public EventHandler<MouseEvent> uNodeMouseRelease = new EventHandler<MouseEvent> ()
    {
        @Override
        public void handle (MouseEvent e)
        {
            // mouse is released within some element other than the canvas
            if (!isCanvasRelease)
            {
                // consume MouseEvent, prevent canvasMouseRelease from getting it
                e.consume ();
            }

            if (appCtrl.getToolState () == ToolState.ADD_EDGE)
            {
                System.out.println ("CVSRLS: canvas release is " + isCanvasRelease);
            }
        }
    };

    /**
     * 
     */
    public EventHandler<MouseEvent> uNodeDragDetected = new EventHandler<MouseEvent> ()
    {
        @Override
        public void handle (MouseEvent e)
        {
            // default assumption is true, may be falsified by another handler
            isCanvasRelease = true;
            Region srcNode = (Region) e.getSource ();

            // System.out.println("startFullDrag: "+srcNode.getUserData());

            if (appCtrl.getToolState () == ToolState.ADD_EDGE)
            {
                // this call is required for uNodeDragRelease to operate
                srcNode.startFullDrag ();
            }
        }
    };

    /**
     * 
     */
    public EventHandler<MouseEvent> uNodeDrag = new EventHandler<MouseEvent> ()
    {
        @Override
        public void handle (MouseEvent e)
        {
            Region srcNode = (Region) e.getSource ();

            if (appCtrl.getToolState () == ToolState.SELECT)
            {
                Point2D dragPoint = new Point2D (e.getSceneX (), e.getSceneY ());
                // canvasView.moveNode(srcNode, lastClick.get(), dragPoint);
                VNode vn = canvasView.getVNode ((Integer) srcNode.getUserData ());

                double delX = dragPoint.getX () - lastClick.get ().getX ();
                double delY = dragPoint.getY () - lastClick.get ().getY ();

                vn.moveNode (delX, delY);
                lastClick.set (dragPoint);
                // System.out.println("U-NODE: drag");
            } else if (appCtrl.getToolState () == ToolState.ADD_EDGE)
            {
                // update mouse/cursor coordinates
                Point2D dragPoint = new Point2D (e.getX (), e.getY ());
                canvasView.animateEdge (srcNode, currentEdge, dragPoint);
            }
        }
    };
    
    /**
     * 
     */
    public EventHandler<MouseEvent> deleteEdge = new EventHandler<MouseEvent> ()
    {
        @Override
        public void handle (MouseEvent e)
        {
            Line edge = (Line) e.getSource ();
            
            if (appCtrl.getToolState () == ToolState.DELETE)
            {
                int id = (int) edge.getUserData ();
                //appCtrl packages this command
                appCtrl.removeEdge (id);
            }
        }
    };

    /**
     * 
     */
    public EventHandler<MouseEvent> canvasDrag = new EventHandler<MouseEvent> ()
    {
        @Override
        public void handle (MouseEvent e)
        {
            if (appCtrl.getToolState () == ToolState.MOVE)
            {
                Point2D clickPoint = new Point2D (e.getX (), e.getY ());
                if (lastClick.get () != null)
                {
                    canvasView.shiftScene (lastClick.get (), clickPoint);
                }
                lastClick.set (clickPoint);
            }
        }

    };

    /**
     * 
     */
    public EventHandler<MouseDragEvent> uNodeDragRelease = new EventHandler<MouseDragEvent> ()
    {
        @Override
        public void handle (MouseDragEvent e)
        {
        	// mouse released within a UNode
        	isCanvasRelease = false;
        	
            Region startRgn = canvasView.getVNode ((int) currentEdge.getUserData ()).getRegion ();
            Region endRgn = (Region) e.getSource ();
            
            if (appCtrl.getToolState () == ToolState.ADD_EDGE)
            {
            	Point2D currentEdgeStart = lastClick.get ();
            	Point2D releasePoint = new Point2D (e.getX (), e.getY ());
            	
            	int id = nextEdgeId ();
            	String name = "edgeName";
            	UNode startNode = appCtrl.getNode ((int) currentEdge.getUserData ());
            	UNode endNode = appCtrl.getNode ((int) endRgn.getUserData ());
            	
            	getCanvas().getChildren().remove(currentEdge);
            	currentEdge = null;
            	            	       	            	
            	appCtrl.executeCommand (
            			packageAction (Action.ADD_EDGE, Scope.CANVAS, id, name, startNode, endNode, startRgn, endRgn, currentEdgeStart, releasePoint), false);            	
            }
            
            // dragging over, the line can begin accepting mouse events again
            currentEdge.setMouseTransparent (false);
            lastClick.set (null);
        }
    };

    /********************** CANVASCTRL EXECUTE COMMAND ******************/
    /**
     * Packages the parameters and the type of action into a Command class. The
     * execute_command method or other invoker style methods are responsible for
     * recasting the objects.
     * 
     * @param type
     *            declared in the Action enum in @Command.java
     * @param objects
     *            a templated list of parameters cast as objects.
     */
    private Command packageAction (Action type, Scope scope, Object... objects)
    {
        // add scope
        return new Command (type, scope, objects);
    }
    
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
    public boolean executeCommand (Command cmd, boolean isHistory)
    {
        /*
         * if(isHistory) { return false; }
         */
    	
        Object[] data = cmd.getData ();        
        Action currentAction = cmd.actionType;
        
        switch (currentAction)
        {
        case ADD_NODE:        	
        //(Action.ADD_NODE, Scope.CANVAS, id, name, e.getX (), e.getY ())
        	
        	if (data.length != 4)
        	{
                System.out.println ("Data for adding a node is incorrect.");
                System.out.println ("Data list expected 4 items but had: " + data.length);
                return false;
            }
            // addNode (Integer id, String name)
            appCtrl.addNode ((Integer) data[0], (String) data[1]);
            
            // drawNode (double x, double y, int id)
            canvasView.drawNode ((double) data[2], (double) data[3], (int) data[0]);
            System.out.println ("CVSCTR: Command executed ADD_NODE with id of " + (int) data[0]);    
       
        return true;            
        case ADD_EDGE:
        //(Action.ADD_EDGE, Scope.CANVAS, id, name, startNode, endNode, startRgn, endRgn, currentEdgeStart, releasePoint)
        	
            if (data.length != 8)
            {
                System.out.println ("Data for adding a node is incorrect.");
                System.out.println ("Data list expected 8 items but had: " + data.length);
                return false;
            }            
            // addEdge (Integer id, UNode n1, UNode n2, String name)
            appCtrl.addEdge ((Integer) data[0], (UNode) data[2], (UNode) data[3], (String) data[1]);
            
            // beginEdgeDraw (Region startRgn, Point2D sceneClickPoint)
            currentEdge = canvasView.beginEdgeDraw ((Region) data[4], (Point2D) data[6]);
            
            // endEdgeDraw (int id, Region endRgn, Line theEdge, Point2D releasePoint)
            canvasView.endEdgeDraw ((int) data[0], (Region) data[5], (Line) currentEdge, (Point2D) data[7]);            
            System.out.println ("CVSCTR: Command executed ADD_EDGE with id of " + (int) data[0]);
            
        return true;
        case DELETE_NODE:
        //(Action.DELETE_NODE, Scope.CANVAS, id)
        	
        	int id = (int) data[0];
            appCtrl.removeNode (id);
            canvasView.removeNode (id);
            System.out.println ("CVSCTR: Command executed DELETE_NODE");
            
        return true;
        case DELETE_EDGE:
        //data[0] - id
        	
            canvasView.removeEdge ((int) data[0]);
            System.out.println ("CVSCTR: Command executed DELETE_EDGE");
            return true;
        
        default: return false;
        }
    }
}