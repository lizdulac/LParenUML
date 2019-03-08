package gui;
import model.*;

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

    private GUIcontroller (Stage stage)
    {
        theGraph = new UGraph ();
        toolState = ToolState.SELECT;
        uNodeId = 0;
        isCanvasRelease = true;
        lastClick = new SimpleObjectProperty<> ();

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

                    theGraph.addNode(id, name);
                    theView.drawNode (e.getX (), e.getY (), id);

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
//          lastClick.set (null);

            Pane srcNode = (Pane)  ((Node) e.getSource()).getParent ();

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

                //System.out.println("startFullDrag: "+srcNode.getUserData());
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
                   double offsetX = e.getSceneX() - lastClick.get().getX();
                   double offsetY = e.getSceneY() - lastClick.get().getY();

                // move/animate the node across the canvas
                srcNode.setLayoutX(srcNode.getLayoutX() + offsetX);
                srcNode.setLayoutY(srcNode.getLayoutY() + offsetY);
                //theView.moveNode (source, offsetX, offsetY);

                Point2D dragPoint = new Point2D(e.getSceneX(), e.getSceneY());
                lastClick.set(dragPoint);

//                System.out.println("U-NODE: drag");
            }
            else if (toolState == ToolState.ADD_EDGE)
            {
                // update mouse/cursor coordinates
                Point2D dragPoint = new Point2D(e.getX(), e.getY());

                // move/animate the edge as it is dragged by the mouse
                currentEdge.setEndX(srcNode.getLayoutX() + dragPoint.getX());
                currentEdge.setEndY(srcNode.getLayoutY() + dragPoint.getY());
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
                // bind/attach the ending point of the line to the srcNode
                currentEdge.endXProperty ().bind(Bindings.add (srcNode.layoutXProperty (), e.getX ()));
                currentEdge.endYProperty ().bind(Bindings.add (srcNode.layoutYProperty (), e.getY ()));

                // draggging over, so the line can begin accepting mouse events again
                currentEdge.setMouseTransparent(false);
            }
        }
    };
}