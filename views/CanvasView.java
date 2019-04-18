package views;
import controllers.*;
import model.*;

import javafx.geometry.Point2D;
import javafx.scene.shape.Line;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.control.TextArea;
import javafx.beans.binding.Bindings;

public class CanvasView
{
    /*********************** CanvasView CLASS MEMBERS ***********************/
    private CanvasCtrl canvasCtrl;
    private UGraph theGraph;
    private Pane canvas;

    /**
     * CanvasView constructor
     * 
     * @param controller CanvasController containing all canvas eventhandlers
     * @param graph UGraph that this view is representing
     */
    public CanvasView (CanvasCtrl controller, UGraph graph)
    {
        canvasCtrl = controller;
        theGraph = graph;

        canvas = new Pane ();
        canvas.setOnMousePressed(canvasCtrl.canvasMousePress);
        canvas.setOnMouseReleased(canvasCtrl.canvasMouseRelease);
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
    public StackPane drawNode (double x, double y, int uNodeId)
    {
        // TODO: only include necessary textboxes
        VBox uNode;
        int textHeight = 30;
        int height = 4 * textHeight;
        int width = 100;
        Font nodeFont = Font.font ("sans-serif", FontWeight.MEDIUM, 12);
        UNode graphNode = theGraph.getNode (uNodeId);

        // StackPane allows visual elements to be layered
        StackPane nodeBody = new StackPane ();

        nodeBody.setPrefHeight (height);
        nodeBody.setPrefWidth (width);
        nodeBody.relocate (x, y);
        nodeBody.setStyle ( "-fx-background-color: white;"+
                            "-fx-border-color: black;");

        // TODO: replace TextAreas with Text or TextBoxes
        // TODO: determine num of required textboxes
        TextArea ta = new TextArea ();
        for (int i = 0; i < 1/*4*/; ++i)
        {
            ta.setText (graphNode.getName ());
            ta.setPrefRowCount (1);
            ta.setPrefWidth (width);
            ta.setPrefHeight (textHeight);
            ta.setFont (nodeFont);
            ta.setLayoutX (x);
            ta.setLayoutY (y + i * textHeight);
            ta.setStyle ("-fx-border-color: black; -fx-border-style: solid solid none solid;");
        }

        // VBox uNode contains TextArea ta and StackPane nodeBody 
        uNode = new VBox (ta, nodeBody);

        uNode.setPrefHeight (height);
        uNode.setPrefWidth (width);
        uNode.relocate (x, y);
        // this id matches with the model
        uNode.setUserData (uNodeId);


     // ***** REGISTER EVENT HANDLERS *****
     // Different pieces of the visual "uNode" need seperate handlers

     // nodeBody
        nodeBody.setOnMousePressed (canvasCtrl.uNodeMousePress);
        nodeBody.setOnDragDetected (canvasCtrl.uNodeDragDetected);

     // uNode
        uNode.setOnMouseDragged (canvasCtrl.uNodeDrag);
        uNode.setOnMouseReleased (canvasCtrl.uNodeMouseRelease);
        uNode.setOnMouseDragReleased (canvasCtrl.uNodeDragRelease);

        canvas.getChildren ().add (uNode);
        return nodeBody;
    }

    /**
     * Translate visual representation of a Node by difference between
     * lastClicked point, and dragPoint.
     * 
     * @param theNode JavaFX Pane containing all visual elements of a Node
     * @param lastClick last registered mouse location
     * @param dragPoint most recently registered mouse location
     */
    public void moveNode (Pane theNode, Point2D lastClick, Point2D dragPoint)
    {
        double offsetX = dragPoint.getX() - lastClick.getX();
        double offsetY = dragPoint.getY() - lastClick.getY();

        // move/animate the node across the canvas
        theNode.setLayoutX(theNode.getLayoutX() + offsetX);
        theNode.setLayoutY(theNode.getLayoutY() + offsetY);
    }

    /**
     * Remove the visual representation of a Node from the window
     * 
     * @param theNode JavaFX Pane containing all visual elements of the
     * Node to be deleted
     */
    public void deleteNode (Pane theNode)
    {
        canvas.getChildren ().remove(theNode);
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
    public Line beginEdgeDraw(Pane srcNode, Point2D sceneClickPoint)
    {
        Line theEdge;
        Point2D localPoint;
        int strokeWidth = 2;

        // Transforms a point from the coordinate space of the scene
        // into the local coordinate space of this Node.
        localPoint = srcNode.sceneToLocal (sceneClickPoint);

        theEdge = new Line();
        theEdge.setStrokeWidth (strokeWidth);
        // disallow parent container node from changing/managing size & layout 
        theEdge.setManaged(false);

        // bind/attach the starting point of the line to the srcNode
        theEdge.startXProperty ().bind(
            Bindings.add (srcNode.layoutXProperty (), localPoint.getX ()));
        theEdge.startYProperty ().bind(
            Bindings.add (srcNode.layoutYProperty (), localPoint.getY ()));

        // move ending point of the line to clickPoint/cursor to be dragged
        theEdge.setEndX(srcNode.getLayoutX () + localPoint.getX ());
        theEdge.setEndY(srcNode.getLayoutY () + localPoint.getY ());

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
    public void animateEdge(Pane srcNode, Line theEdge, Point2D dragPoint)
    {
        theEdge.setEndX(srcNode.getLayoutX() + dragPoint.getX());
        theEdge.setEndY(srcNode.getLayoutY() + dragPoint.getY());
    }

    /**
     * The third and final function required to draw an edge. The
     * 'unbound' end of the line is 'bound' to the releasePoint.
     *  
     * @param srcNode node that was originally clicked
     * @param theEdge Line that was previously created & 'bound' to srcNode at one end
     * @param releasePoint location of the mouse cursor when mouse button is released and drag operation ends
     */
    public void endEdgeDraw(Pane srcNode, Line theEdge, Point2D releasePoint)
    {
        // bind/attach the ending point of the line to the srcNode
        theEdge.endXProperty ().bind(Bindings.add (srcNode.layoutXProperty (), releasePoint.getX ()));
        theEdge.endYProperty ().bind(Bindings.add (srcNode.layoutYProperty (), releasePoint.getY ()));
    }

    /**
     * Remove visual representation of an Edge from the window
     * 
     * @param theEdge Edge to be removed from view
     */ 
    public void removeEdge(Line theEdge)
    {
        canvas.getChildren ().remove (theEdge);
    }
}
