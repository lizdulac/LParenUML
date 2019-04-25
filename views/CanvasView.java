package views;

import java.util.HashMap;
import java.util.Map;

import controllers.CanvasCtrl;

import javafx.geometry.Point2D;
import javafx.scene.shape.Line;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.beans.binding.Bindings;

public class CanvasView
{
    private CanvasCtrl canvasCtrl;
    private Pane canvas;
    private Map<Integer, VNode> nodes;

    /**
     * CanvasView constructor
     * 
     * @param controller CanvasController containing all canvas eventhandlers
     * @param graph UGraph that this view is representing
     */
    // TODO: delete UGraph
    public CanvasView (CanvasCtrl controller)
    {
        canvasCtrl = controller;
        nodes = new HashMap<Integer, VNode>();

        canvas = new Pane ();
        canvas.setOnMousePressed (canvasCtrl.canvasMousePress);
        canvas.setOnMouseReleased (canvasCtrl.canvasMouseRelease);
        canvas.setOnMouseDragged (canvasCtrl.canvasDrag);
    }

    /**
     * 
     * @return JavaFX Pane representing visual workspace
     */
    public Pane getCanvas ()
    {
        return canvas;
    }

    /*************************** NODE FUNCTIONS ***************************/

    public VNode getVNode(Integer i)
    {
        return nodes.get (i);
    }
    
    /**
     * Draw the visual representation of a UNode.
     * 
     * NOTE: Nodes are stored as a child of the "canvas" panel in a group that
     * contains a StackPane, and then necessary text containers
     * 
     * @param x
     *            x coordinate of upper left corner of UNode
     * @param y
     *            y coordinate of upper left corner of UNode
     * @param uNodeId
     *            id of UNode represented by this display
     * @return JavaFX StackPane containing all visual elements of a UNode
     */
//    public StackPane drawNode (double x, double y, int uNodeId)
    // TODO: check this
    public VNode drawNode (double x, double y, int id)
    {
        VNode visual = new VNode (x, y, id);
        Region uNode = visual.getRegion ();

        // ***** REGISTER EVENT HANDLERS *****
        // Different pieces of the visual "uNode" need seperate handlers

        // nodeBody
        uNode.setOnMousePressed (canvasCtrl.uNodeMousePress);
        uNode.setOnDragDetected (canvasCtrl.uNodeDragDetected);

        // uNode
        uNode.setOnMouseDragged (canvasCtrl.uNodeDrag);
        uNode.setOnMouseReleased (canvasCtrl.uNodeMouseRelease);
        uNode.setOnMouseDragReleased (canvasCtrl.uNodeDragRelease);

        canvas.getChildren ().add (uNode);
        // add Pane to map "nodes"
        nodes.put(id, visual);
        return visual;
    }

    /**
     * Translate visual representation of a Node by difference between
     * lastClicked point, and dragPoint.
     * 
     * @param theNode JavaFX Pane containing all visual elements of a Node
     * @param lastClick last registered mouse location
     * @param dragPoint most recently registered mouse location
     */

    /**
     * Remove the visual representation of a Node from the window
     * 
     * @param theNode JavaFX Pane containing all visual elements of the
     * Node to be deleted
     */
    
    /**
     * 
     * @param mouseOriginal
     * @param mouseCurrent
     */
    public void shiftScene (Point2D mouseOriginal, Point2D mouseCurrent)
    {
        System.out.println ("VIEW: shiftScene");
        double x = mouseCurrent.getX () - mouseOriginal.getX ();
        double y = mouseCurrent.getY () - mouseOriginal.getY ();
        
        nodes.replaceAll ((k,v) -> {
            v.moveNode (x, y);
            return v;
        });
    }
    
    // TODO: this
    public Point2D getLocation (Integer nodeId)
    {
        //StackPane pane = nodes.get (nodeId).pane;
        return null;
    }

    /*************************** EDGE FUNCTIONS ***************************/
   
    /**
     * The first of the 3 functions required to draw an edge. Creates a
     * new Line that is 'bound' to srcNode at localPoint. The other end  
     * of the line remains 'unbound' and is temporarily set to localPoint.
     *
     * @param srcNode node that was clicked
     * @param sceneClickPoint the click point location is in the coordinate space of the scene
     * @return the javaFX Line that was created
     */
    public Line beginEdgeDraw (Region srcNode, Point2D sceneClickPoint)
    {
        Line theEdge;
        Point2D localPoint;
        int strokeWidth = 2;

        // Transforms a point from the coordinate space of the scene
        // into the local coordinate space of this Node.
        localPoint = srcNode.sceneToLocal (sceneClickPoint);

        theEdge = new Line ();
        theEdge.setStrokeWidth (strokeWidth);
        // disallow parent container node from changing/managing size & layout
        theEdge.setManaged (false);

        // bind/attach the starting point of the line to the srcNode
        theEdge.startXProperty ().bind (Bindings.add (srcNode.layoutXProperty (), localPoint.getX ()));
        theEdge.startYProperty ().bind (Bindings.add (srcNode.layoutYProperty (), localPoint.getY ()));

        // move ending point of the line to clickPoint/cursor to be dragged
        theEdge.setEndX (srcNode.getLayoutX () + localPoint.getX ());
        theEdge.setEndY (srcNode.getLayoutY () + localPoint.getY ());

        canvas.getChildren ().add (theEdge);
        return theEdge;
    }

    /**
     * The second of the 3 functions required to draw an edge. Updates 
     * the 'unbound' end of the line so that it remains attached to the
     * mouse cursor during the drag operation.
     *
     * @param srcNode node that was originally clicked
     * @param theEdge Line that was previously created & 'bound' to srcNode at one end
     * @param dragPoint current location of mouse cursor in the drag operation
     */
    public void animateEdge (Region srcNode, Line theEdge, Point2D dragPoint)
    {
        theEdge.setEndX (srcNode.getLayoutX () + dragPoint.getX ());
        theEdge.setEndY (srcNode.getLayoutY () + dragPoint.getY ());
    }

    /**
     * The third and final function required to draw an edge. The
     * 'unbound' end of the line is 'bound' to the releasePoint.
     *  
     * @param srcNode node that was originally clicked
     * @param theEdge Line that was previously created & 'bound' to srcNode at one end
     * @param releasePoint location of the mouse cursor when mouse button is released and drag operation ends
     */
    public void endEdgeDraw (Region srcNode, Line theEdge, Point2D releasePoint)
    {
        // bind/attach the ending point of the line to the srcNode
        theEdge.endXProperty ().bind (Bindings.add (srcNode.layoutXProperty (), releasePoint.getX ()));
        theEdge.endYProperty ().bind (Bindings.add (srcNode.layoutYProperty (), releasePoint.getY ()));
    }

    /**
     * Remove visual representation of an Edge from the window
     * 
     * @param theEdge Edge to be removed from view
     */ 
    public void removeEdge (Line theEdge)
    {
        canvas.getChildren ().remove (theEdge);
    }
}