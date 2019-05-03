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
import javafx.collections.ObservableList;

/**
 * 
 * @author 
 *
 */
public class CanvasCtrl
{
    /******************** APPCTRL FILEIO EVENT HANDLERS *****************/
    private AppCtrl appCtrl;
    protected CanvasView canvasView;

    private int uNodeId;
    private int uEdgeId;
    private Boolean isCanvasRelease;
    private Line currentEdge;
    private final ObjectProperty<Point2D> lastClick;

    /******************** APPCTRL FILEIO EVENT HANDLERS *****************/
    /**
     * 
     * @param controller
     */
    public CanvasCtrl (AppCtrl controller)
    {
        appCtrl = controller;
        canvasView = new CanvasView (this);

        uNodeId = 0;
        uEdgeId = 0;
        isCanvasRelease = true;
        currentEdge = null;
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
     * Necessary for file opening/saving
     * 
     * @param id
     */
    public void setUNodeId (int id)
    {
        uNodeId = id;
    }
    
    /**
     * Necessary for file opening/saving
     * 
     * @param id
     */
    public void setUEdgeId (int id)
    {
        uEdgeId = id;
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
                    String name = "Class " + ((Character) ((char) (id + 96))).toString (); 
                    
                    // appCtrl.addNode() AND canvasView.drawNode()
                    appCtrl.executeCommand (
                            packageAction (Action.ADD_NODE, Scope.CANVAS, id, name, e.getX (), e.getY ()), false);
                } else
                {
                    System.out.println ("CANVAS: canvas clicked, no action");
                }
            }
            // Required for move drags to reset
            lastClick.set (null);
        }
    };
    
    /**
     * 
     */
    public EventHandler<MouseDragEvent> canvasDragRelease = new EventHandler<MouseDragEvent> ()
    {
        @Override
        public void handle (MouseDragEvent e)
        {           
            if (appCtrl.getToolState () == ToolState.ADD_EDGE)
            {
                getCanvas().getChildren().remove(currentEdge);
                currentEdge = null;
                System.out.println ("CVSCTR: edge creation cancelled.");
            }
            lastClick.set (null);
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

            VNode srcNode = (VNode) e.getSource ();
            int id = (int) srcNode.getUserData ();
            UNode uNode = appCtrl.getNode (id);

            if (appCtrl.getToolState () == ToolState.SELECT_MOVE)
            {// Selecting a node is not undo-able so actions are not packaged
            	
                ObservableList<String> atr = appCtrl.getNode(id).getAttributes();
                
            	// toggle VNode selected state
                if(canvasView.getVNode(id).getSelected()) {
                	canvasView.unselectAllNodes(appCtrl.getUnselectedStyle());
                	System.out.println ("U-NODE: node " + id + " changed to selected is " + canvasView.getVNode(id).getSelected());
                }
                else {
                	canvasView.unselectAllNodes(appCtrl.getUnselectedStyle());
                	canvasView.getVNode(id).setSelected(true);
                	canvasView.getVNode(id).setStyle(appCtrl.getSelectedStyle());
                	System.out.println ("U-NODE: node " + id + " changed to selected is " + canvasView.getVNode(id).getSelected());
                }            

                // only toggle the slider if it is currently hidden
                if( !appCtrl.propIsVisible() ) {
                	appCtrl.toggleSlider();
                }
                
                // update inspector data
                appCtrl.refreshPropData(appCtrl.getAsList(id));
                appCtrl.sideStage.requestFocus();
            }
            else if (appCtrl.getToolState () == ToolState.ADD_EDGE) {
            // DO NOT package these ADD_EDGE actions and send to executeCommand()
            // all necessary ADD_EDGE actions are taken care of in uNodeDragRelease
                AnchorMgr a = new AnchorMgr(srcNode);                
                Point2D anchor = a.getNearAnchor(new Point2D(e.getX(), e.getY()));
                
                //this math creates a point at the local space of the srcPane
                double sumX = anchor.getX() + srcNode.getBoundsInParent().getMinX() + 50;
                double sumY = anchor.getY() + srcNode.getBoundsInParent().getMinY() + 50;
                Point2D click = new Point2D(sumX, sumY);                
                lastClick.set(click); 
                
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
            lastClick.set (null);
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

            if (appCtrl.getToolState () == ToolState.SELECT_MOVE)
            {
                Point2D dragPoint = new Point2D (e.getSceneX (), e.getSceneY ());
                // canvasView.moveNode(srcNode, lastClick.get(), dragPoint);
                //VNode vn = canvasView.getVNode ((Integer) srcNode.getUserData ());

                double delX = dragPoint.getX () - lastClick.get ().getX ();
                double delY = dragPoint.getY () - lastClick.get ().getY ();

                canvasView.getVNode ((Integer) srcNode.getUserData ()).moveNode (delX, delY);
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
            if (appCtrl.getToolState () == ToolState.MOVE_GRAPH)
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

            VNode startPane = canvasView.getVNode ((int) currentEdge.getUserData ());
            VNode endPane = (VNode) e.getSource ();

            if (appCtrl.getToolState () == ToolState.ADD_EDGE)
            {
                Point2D currentEdgeStart = lastClick.get ();

                AnchorMgr a = new AnchorMgr(endPane);
                Point2D releasePoint = a.getNearAnchor (new Point2D (e.getX (), e.getY ()));

                int id = nextEdgeId ();
                String name = "edgeName";
                UNode startNode = appCtrl.getNode ((int) currentEdge.getUserData ());
                UNode endNode = appCtrl.getNode ((int) endPane.getUserData ());              
                
                getCanvas().getChildren().remove(currentEdge);      
                currentEdge = null;
                
                appCtrl.executeCommand (
                    packageAction (Action.ADD_EDGE, Scope.CANVAS, id, name, startNode, endNode, startPane, endPane, currentEdgeStart, releasePoint), false);              
            }
            lastClick.set (null); 
        }
    };
    
    /**
     * 
     * @param percent
     */
    public void zoomIn (double percent)
    {
        canvasView.zoomIn (percent);
    }
    
    /**
     * 
     */
    public void zoomReset ()
    {
        canvasView.zoomReset ();
    }
    
    /**
     * 
     * @param id
     * @param name
     */
    public void refreshVNode(Integer id, String name)
    {
        canvasView.getVNode(id).refreshName(name);
    }
    
    
    /**
     * 
     */
    public void clearScreen (Boolean history)
    {
        for (Integer i : canvasView.getNodeKeys ())
        {
            appCtrl.executeCommand(packageAction (Action.DELETE_NODE, Scope.CANVAS, i), history);
        }
    }

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
            ObservableList<String> attr = appCtrl.getGraph().getNode((int) data[0]).getAttributes();
            ObservableList<String> func = appCtrl.getGraph().getNode((int) data[0]).getFunctions();
            ObservableList<String> misc = appCtrl.getGraph().getNode((int) data[0]).getMiscs();
            
         // drawNode (double x, double y, int id, String name, ObservableList<String> attr, ObservableList<String> func, ObservableList<String> misc)
            canvasView.drawNode ((double) data[2], (double) data[3], (int) data[0], (String) data[1], attr, func, misc);
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
            
            currentEdge.toBack ();
            ((VNode) data[4]).toFront ();
            ((VNode) data[5]).toFront ();
            
            // dragging over, the line can begin accepting mouse events again
            currentEdge.setMouseTransparent (false);
            currentEdge = null;
            
            System.out.println ("CVSCTR: Command executed ADD_EDGE with id of " + (int) data[0]);
            
        return true;
        case DELETE_NODE:
        //(Action.DELETE_NODE, Scope.CANVAS, id)
            
            // Command length 1, Undo Command length 4
            if (data.length != 1 && data.length != 4)
            {
                System.out.println ("Data for deleting a node is incorrect.");
                System.out.println ("Data list expected 1 item but had: " + data.length);
                return false;
            }
            
            // removeNode (Integer id)
            appCtrl.removeNode ((int) data[0]);
            
            // removeNode (int id)
            canvasView.removeNode ((int) data[0]);
            
            System.out.println ("CVSCTR: Command executed DELETE_NODE on " + (int)data[0]);
            
        return true;
        case DELETE_EDGE:
        //(Action.DELETE_NODE, Scope.CANVAS, id)
            
            // Command length 1, Undo Command length 8
            if (data.length != 1 && data.length != 8)
            {
                System.out.println ("Data for deleting an edge is incorrect.");
                System.out.println ("Data list expected 1 item but had: " + data.length);
                return false;
            }
            
            // removeEdge (Integer id)
            canvasView.removeEdge ((int) data[0]);
            
            System.out.println ("CVSCTR: Command executed DELETE_EDGE on " + (int)data[0]);
            
        return true;       
        default:
        
        return false;
        }
    }
}
