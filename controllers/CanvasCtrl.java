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
            if (appCtrl.getToolState () == ToolState.ADD_EDGE)
            {
                canvasView.removeEdge (currentEdge);
                currentEdge = null;

                System.out.println ("ADD_EDGE: canvasMouseRelease");
            } else if (appCtrl.getToolState () == ToolState.SELECT)
            {
                // TODO: may be unneeded
                lastClick.set (null);
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
            if (appCtrl.getToolState () == ToolState.ADD_EDGE)
            {
                currentEdge = canvasView.beginEdgeDraw (srcNode, lastClick.get ());
                currentEdge.setUserData (uNode.getId ());

                // line must be temporarily transparent to any mouse clicks
                currentEdge.setMouseTransparent (true);
            } else if (appCtrl.getToolState () == ToolState.DELETE)
            {
                appCtrl.removeNode (id);
                canvasView.getVNode (id).delete ();
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
                // consume MouseEvent, prevent canvasMouseRelease from getting
                // it
                e.consume ();
            }

            if (appCtrl.getToolState () == ToolState.ADD_EDGE)
            {
                System.out.println ("isCanvasRelease: " + isCanvasRelease);
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
    public EventHandler<MouseEvent> canvasDrag = new EventHandler<MouseEvent> ()
    {
        @Override
        public void handle (MouseEvent e)
        {
            if (appCtrl.getToolState () == ToolState.MOVE)
            {
                Point2D clickPoint = new Point2D (e.getX (), e.getY ());
                System.out.printf ("CANVAS: Enter Canvas drag to (%5.2f, %5.2f)\n", clickPoint.getX (),
                        clickPoint.getY ());
                if (lastClick.get () != null)
                {
                    System.out.printf (" from (%5.2f, %5.2f)\n", lastClick.get ().getX (), lastClick.get ().getY ());
                    canvasView.shiftScene (lastClick.get (), clickPoint);
                }
                System.out.println ();
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

            Region srcNode = (Region) e.getSource ();
            UNode uNode = appCtrl.getNode ((int) srcNode.getUserData ());

            if (appCtrl.getToolState () == ToolState.ADD_EDGE)
            {
                Point2D releasePoint = new Point2D (e.getX (), e.getY ());

                UNode start = appCtrl.getNode ((int) currentEdge.getUserData ());
                UNode end = uNode;
                String edgeName = start.getName () + end.getName ();
                // appctrl._______()
                appCtrl.linkSingle (start, end, edgeName);

                canvasView.endEdgeDraw (srcNode, currentEdge, releasePoint);

                // dragging is over, the line can begin accepting mouse events
                // again
                currentEdge.setMouseTransparent (false);
                lastClick.set (null);
            }
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
     * data[0] - id data[1] - name data[2] - x data[3] - y
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

        if (cmd.actionType == Action.ADD_NODE)
        {
            System.out.printf ("CanvasCtrl: ADD_NODE Command\n");
            if (data.length != 4)
            {
                System.out.println ("Data for adding a node is incorrect");
                System.out.println ("Data list expected 4 items but had: " + data.length);
                return false;
            }

            appCtrl.addNode ((Integer) data[0], (String) data[1]);
            canvasView.drawNode ((double) data[2], (double) data[3], (int) data[0]);
            System.out.println ("EXECMD: CanvasCtrl added node");
            return true;
        } else if (cmd.actionType == Action.ADD_EDGE)
        {
            // Arguments; Node1 pane, Node1 location as Point2D, Node2 pane, Node2 location as Point2D
            System.out.println ("CanvasCtrl: ADD_EDGE Command");
            // public void linkSingle(UNode n1, UNode n2, String edge)
            if (data.length != 4/* change */)
            {
                System.out.println ("Data for adding a node is incorrect");
                System.out.println ("Data list expected 3 items but had: " + data.length);
                return false;
            }
            Line l = canvasView.beginEdgeDraw ((Region) data[0], (Point2D) data[1]);
            canvasView.endEdgeDraw ((Region) data[2], l, (Point2D) data[3]);
        }

        return false;
    }
}