package gui;

import model.*;

import javafx.scene.Node;
import javafx.scene.shape.Rectangle;
import javafx.scene.input.MouseEvent;

import javafx.stage.Stage;
import javafx.geometry.Point2D;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class GUIcontroller
{
    public enum ToolState
    {
        SELECT (0), ADD_NODE (1), ADD_EDGE (2), DELETE (3);
        
        private final int value; 

        ToolState(int value) { 
            this.value = value; 
        }
    };

    private static GUIcontroller sharedController;
    private GUIview theView;
    private UGraph theGraph;
    private ToolState toolState;
    private int uNodeId;
    private final ObjectProperty<Point2D> lastClick;

    private GUIcontroller (Stage stage)
    {
        theGraph = new UGraph ();
        toolState = ToolState.SELECT;
        uNodeId = 0;
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
            System.out.println ("TSTATE: changed to " + toolState);
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

                    // theGraph.addNode(id, name);
                    theView.drawNode (e.getX (), e.getY ());

                    System.out.println ("U-NODE: node created -> id=" + id + " name=" + name);
                } else
                {
                    System.out.println ("CANVAS: canvas clicked, no action");
                }
            }
        }
    };

    public EventHandler<MouseEvent> uNodeMousePress = new EventHandler<MouseEvent> ()
    {
        @Override
        public void handle (MouseEvent e)
        {
            double x = e.getSceneX ();
            double y = e.getSceneY ();
            lastClick.set (null);
            
            Rectangle source = ((Rectangle) e.getSource ());
            
            if (toolState == ToolState.DELETE)
            {
                theView.deleteNode (source);
            }

            System.out.println ("U-NODE: uNode clicked -> x=" + x + " y=" + y);
        }
    };

    public EventHandler<MouseEvent> uNodeDrag = new EventHandler<MouseEvent> ()
    {
        @Override
        public void handle (MouseEvent e)
        {

            if (toolState == ToolState.SELECT)
            {
                Rectangle source = ((Rectangle) e.getSource ());

                if (lastClick.get () != null)
                {
                    double offsetX = e.getSceneX () - lastClick.get ().getX ();
                    double offsetY = e.getSceneY () - lastClick.get ().getY ();

                    theView.moveNode (source, offsetX, offsetY);

                    Point2D dragPoint = new Point2D (e.getSceneX (), e.getSceneY ());
                    lastClick.set (dragPoint);

                    System.out.println ("U-NODE: drag");
                }

                double x = e.getSceneX ();
                double y = e.getSceneY ();
                Point2D clickPoint = new Point2D (x, y);
                lastClick.set (clickPoint);
            }
        }
    };
}
