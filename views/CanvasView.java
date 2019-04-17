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
    private CanvasCtrl canvasCtrl;
    private UGraph theGraph;
    private Pane canvas;

    public CanvasView (CanvasCtrl controller, UGraph graph)
    {
        canvasCtrl = controller;
        theGraph = graph;

        canvas = new Pane ();
        canvas.setOnMousePressed(canvasCtrl.canvasMousePress);
        canvas.setOnMouseReleased(canvasCtrl.canvasMouseRelease);
    }

    public Pane getCanvas ()
    {
    	return canvas;
    }


    /*************************** NODE FUNCTIONS ***************************/

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

    public void moveNode (Pane theNode, Point2D lastClick, Point2D dragPoint)
    {
        double offsetX = dragPoint.getX() - lastClick.getX();
        double offsetY = dragPoint.getY() - lastClick.getY();

        // move/animate the node across the canvas
        theNode.setLayoutX(theNode.getLayoutX() + offsetX);
        theNode.setLayoutY(theNode.getLayoutY() + offsetY);
    }

    public void deleteNode (Pane theNode)
    {
        canvas.getChildren ().remove(theNode);
    }


    /*************************** EDGE FUNCTIONS ***************************/

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

    public void animateEdge(Pane srcNode, Line theEdge, Point2D dragPoint)
    {
        theEdge.setEndX(srcNode.getLayoutX() + dragPoint.getX());
        theEdge.setEndY(srcNode.getLayoutY() + dragPoint.getY());
    }

    public void endEdgeDraw(Pane srcNode, Line theEdge, Point2D releasePoint)
    {
        // bind/attach the ending point of the line to the srcNode
        theEdge.endXProperty ().bind(Bindings.add (srcNode.layoutXProperty (), releasePoint.getX ()));
        theEdge.endYProperty ().bind(Bindings.add (srcNode.layoutYProperty (), releasePoint.getY ()));
    }

    public void removeEdge(Line theEdge)
    {
        canvas.getChildren ().remove (theEdge);
    }
}