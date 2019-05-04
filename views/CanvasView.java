package views;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import controllers.CanvasCtrl;
import javafx.geometry.Point2D;
import javafx.scene.shape.Line;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;

/**
 * @author Liz and David
 */
public class CanvasView
{
    /********************** CANVASVIEW CLASS MEMBERS **********************/
    private CanvasCtrl canvasCtrl;
    private Pane canvas;
    private Map<Integer, VNode> nodes;
    private Map<Integer, Line> edges;
    private double scale = 1.0;

    /************************ CANVASVIEW CONSTRUCTOR **********************/
    /**
     * CanvasView constructor
     * 
     * @param controller CanvasController containing all canvas eventhandlers
     * @param graph UGraph that this view is representing
     */
    public CanvasView (CanvasCtrl controller)
    {
        canvasCtrl = controller;
        nodes = new HashMap<Integer, VNode>();
        edges = new HashMap<Integer, Line>();

        canvas = new Pane ();
        canvas.setOnMousePressed (canvasCtrl.canvasMousePress);
        canvas.setOnMouseDragged (canvasCtrl.canvasDrag);
        canvas.setOnMouseDragReleased (canvasCtrl.canvasDragRelease);
    }
    
    /************************* CANVASVIEW GETTERS *************************/
    /**
     * @return JavaFX Pane representing visual workspace
     */
    public Pane getCanvas ()
    {
        return canvas;
    }

    /**
       * @param i
     * @return
     */
    public VNode getVNode(Integer i)
    {
        return nodes.get (i);
    }

    /************************* CANVASVIEW FUNCTIONS ***********************/
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
    public VNode drawNode (double x, double y, int id, String name, ObservableList<String> attr, ObservableList<String> func, ObservableList<String> misc)
    {
        VNode vNode = new VNode (x, y, id, scale, name, attr, func, misc);
        vNode.relocate(x, y);
        
        // Deal with mouse events:
        // Each one is registered as an event filter rather than an event handler
        vNode.addEventFilter(MouseEvent.MOUSE_PRESSED, canvasCtrl.uNodeMousePress);
        vNode.addEventFilter(MouseEvent.MOUSE_RELEASED, canvasCtrl.uNodeMouseRelease);
        // Dragging
        vNode.addEventFilter(MouseEvent.DRAG_DETECTED, canvasCtrl.uNodeDragDetected);       
        vNode.addEventFilter(MouseEvent.MOUSE_DRAGGED, canvasCtrl.uNodeDrag);       
        vNode.addEventFilter(MouseDragEvent.MOUSE_DRAG_RELEASED, canvasCtrl.uNodeDragRelease);
        
        // add VNode to map of nodes
        nodes.put(id, vNode);
        canvas.getChildren ().add (vNode);
        return vNode;
    }
    
    public void unselectAllNodes(String selectedStyle)
    {
    	for (VNode vNode : nodes.values()) {
    		vNode.setSelected(false);
    		vNode.setStyle(selectedStyle);
    	}
    }   

    /**
     * Remove the visual representation of a Node from the window
     * 
     * @param id
     */
    public void removeNode (int id)
    {
        System.out.printf ("CVS-VW: removing Node %d\n", id);
        VNode node = getVNode(id);
        canvas.getChildren ().remove (node);
        nodes.remove (node);
    }
    
    /**
     * 
     * @param mouseOriginal
     * @param mouseCurrent
     */
    public void shiftScene (Point2D mouseOriginal, Point2D mouseCurrent)
    {
        double x = mouseCurrent.getX () - mouseOriginal.getX ();
        double y = mouseCurrent.getY () - mouseOriginal.getY ();
        
        nodes.replaceAll ((k,v) -> {
            v.moveNode (x, y);
            return v;
        });
    }
    
    /**
     * 
     */
    public void zoomReset ()
    {
        zoomIn (1.0 - (1.0 / scale));
    }
    
    /**
     * 
     * @param percent
     */
    public void zoomIn (double percent)
    {
        scale *= (1.0 - percent);
        Point2D center = new Point2D (canvas.getWidth () / 2.0, canvas.getHeight () / 2.0);
        nodes.replaceAll ((k,v) -> {
            double newX = percent * (center.getX () - v.getX ());
            double newY = percent * (center.getY () - v.getY ());
            v.moveNode (newX, newY);
            
            // iterate through children of the region, too
            v.setScaleX (scale);
            v.setScaleY (scale);
            
            return v;
        });
    }
    
    public Set<Integer> getNodeKeys ()
    {
        return nodes.keySet ();
    }

    /*************************** EDGE FUNCTIONS ***************************/
   
    /**
     * The first of the 3 functions required to draw an edge. Creates a
     * new Line that is 'bound' to startRgn at localPoint. The other end  
     * of the line remains 'unbound' and is temporarily set to localPoint.
     *
     * @param startRgn node that was clicked
     * @param sceneClickPoint the click point location is in the coordinate space of the scene
     * @return the javaFX Line that was created
     */
    public Line beginEdgeDraw (Region startRgn, Point2D sceneClickPoint)
    {
        Line theEdge;
        Point2D localPoint;
        int strokeWidth = 2;

        // Transforms a point from the coordinate space of the scene
        // into the local coordinate space of this Node.
        localPoint = startRgn.sceneToLocal (sceneClickPoint);

        theEdge = new Line ();
        theEdge.setStrokeWidth (strokeWidth);
        // disallow parent container node from changing/managing size & layout
        theEdge.setManaged (false);

        // bind/attach the starting point of the line to the startRgn
        theEdge.startXProperty ().bind (Bindings.add (startRgn.layoutXProperty (), localPoint.getX ()));
        theEdge.startYProperty ().bind (Bindings.add (startRgn.layoutYProperty (), localPoint.getY ()));

        // move ending point of the line to clickPoint/cursor to be dragged
        theEdge.setEndX (startRgn.getLayoutX () + localPoint.getX ());
        theEdge.setEndY (startRgn.getLayoutY () + localPoint.getY ());

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
     * @param endRgn node that was originally clicked
     * @param theEdge Line that was previously created & 'bound' to srcNode at one end
     * @param releasePoint location of the mouse cursor when mouse button is released and drag operation ends
     */
    public void endEdgeDraw (int id, Region endRgn, Line theEdge, Point2D releasePoint)
    {
        // bind/attach the ending point of the line to the srcNode
        theEdge.endXProperty ().bind (Bindings.add (endRgn.layoutXProperty (), releasePoint.getX ()));
        theEdge.endYProperty ().bind (Bindings.add (endRgn.layoutYProperty (), releasePoint.getY ()));
        theEdge.setUserData (id);
        theEdge.setOnMouseClicked (canvasCtrl.deleteEdge);
        edges.put (id, theEdge);
    }

    /**
     * Remove visual representation of an Edge from the window
     * 
     * @param theEdge Edge to be removed from view
     */ 
    public void removeEdge (Line theEdge)
    {
        int id = (int) theEdge.getUserData ();
        removeEdge(id);
    }
    
    /**
     * 
     * @param id
     */
    public void removeEdge (Integer id)
    {
        System.out.println ("Remove Edge: " + id);
        Line edge = edges.get (id);
        canvas.getChildren ().remove (edge);
        edges.remove (id);
    }
}