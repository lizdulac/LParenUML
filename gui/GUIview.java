package gui;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TextArea;
import javafx.scene.control.Separator;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;

import javafx.stage.Stage;
import javafx.geometry.Pos;
import javafx.geometry.Point2D;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.stage.FileChooser;


public class GUIview
{
    private GUIcontroller controller;
    private Scene scene;
    private Pane canvas;
    private double windowW = 800.0;
    private double windowH = 600.0;

    /*************************** INITIALIZATION ***************************/
    public GUIview (GUIcontroller aController, Stage aStage)
    {
        controller = aController;
        canvas = new Pane();
        scene = new Scene (configureUI (aStage), windowW, windowH);
        aStage.setScene (scene);
    }

    private BorderPane configureUI (Stage aStage)
    {
        int topMargin = 25;
        int leftMargin = 100;
        int rightMargin = 50;
        int bottomMargin = 25;
        BorderPane borderPane;

        // canvas is a class variable of GUIview
        canvas.setOnMousePressed(controller.canvasMousePress);
        canvas.setOnMouseReleased(controller.canvasMouseRelease);
        canvas.setStyle ("-fx-background-color: #f2f2f2; -fx-background-radius: 50;");

        MenuBar menuBar = createMenuBar(aStage);
        Pane buttonPanel = createButtonPanel ();
        Separator rightSpacer = new Separator ();
        Separator bottomSpacer = new Separator ();

       // topSpacer.setVisible (false);
        rightSpacer.setVisible (false);
        bottomSpacer.setVisible (false);

       // topSpacer.setPrefHeight (topMargin);
        buttonPanel.setPrefWidth (leftMargin);
        rightSpacer.setPrefWidth (rightMargin);
        bottomSpacer.setPrefHeight (bottomMargin);

        borderPane = new BorderPane(canvas, menuBar, rightSpacer, bottomSpacer, buttonPanel);
        borderPane.setBackground (Background.EMPTY);

        return borderPane;
    }
    
    // Button Style reference:
    // http://fxexperience.com/2011/12/styling-fx-buttons-with-css/
    /*
     * WARNING: UCodes, buttonNames, and GUIcontroller.ToolState enum values must all match up
     */
    private Pane createButtonPanel ()
    {
        /*
         * UniCode numbers for characters on buttons in order: move, createNode,
         * createEdge, delete, edit, undo, redo
         */
        final int[] UCodes = new int[] { 0x261D, 0x274F, 8594, 0x2620, 0x270E, 0x21BA, 0x21BB };
        final String[] buttonNames = new String[] { "Move", "Create Node", "Create Edge", "Delete", "Edit", "Undo",
                "Redo" };

        Font buttonsFont = Font.font ("sans-serif", FontWeight.BOLD, 20);

        // Gross CSS code to style buttons
        String buttonStyle = "-fx-background-color:linear-gradient(#f2f2f2, #d6d6d6),"
                + "linear-gradient(#fcfcfc 0%, #d9d9d9 20%, #d6d6d6 100%),linear-gradient(#dddddd 0%, #f6f6f6 50%);"
                + "-fx-background-radius: 8,7,6;-fx-background-insets: 0,1,2;-fx-text-fill: black;-fx-effect:"
                + "dropshadow( three-pass-box , rgba(0,0,0,0.6) , 5, 0.0 , 0 , 1 );";

        
        // VBox arranges elements vertically in a single column
        VBox buttonPanel;
        double panelSpacing = 25.0;
        Pos panelAlignment = Pos.CENTER;
        buttonPanel = new VBox (panelSpacing);
        buttonPanel.setAlignment (panelAlignment);

        // create Button objects
        Button[] buttons = new Button[UCodes.length];
        for (int i = 0; i < UCodes.length; ++i)
        {
            // Set Button text to associated UniCode character
            buttons[i] = new Button (Character.toString ((char) UCodes[i]));
            final Tooltip tt = new Tooltip ();
            // Set text to be displayed when button is hovered over
            tt.setText (buttonNames[i]);
            buttons[i].setTooltip (tt);
            buttons[i].setFont (buttonsFont);
            buttons[i].setStyle (buttonStyle);
            // 
            if (i < GUIcontroller.ToolState.values ().length)
            {
                buttons[i].setUserData (GUIcontroller.ToolState.values ()[i]);
                buttons[i].setOnAction (controller.buttonClick);
            }
            // add Button to panel
            buttonPanel.getChildren ().add (buttons[i]);
        }

        return buttonPanel;
    }

    /*************************** MENUBAR **********************************/
    private MenuBar createMenuBar(Stage aStage)
    {
        MenuBar menuBar = new MenuBar();
        Menu menu1 = new Menu("File");
        menuBar.getMenus().add(menu1);
        MenuItem menuItem1 = new MenuItem("Open");
        //MenuItem menuItem2 = new MenuItem("Save");
        
        menu1.getItems().add(menuItem1);


        menuItem1.setOnAction(e -> {
        FileChooser file = new FileChooser();  
        file.setTitle("Open File");  
        file.showOpenDialog(aStage);
        });

        return menuBar;
    }
    
    /*************************** NODE FUNCTIONS ***************************/
    /* Nodes are stored as a child of the "canvas" panel in a group that
     * contains a StackPane, and then necessary text containers
     */
    public StackPane drawNode (double x, double y, int uNodeId)
    {
        // TODO: only include necessary textboxes
        VBox uNode;
        int textHeight = 30;
        int height = 4 * textHeight;
        int width = 100;
        Font nodeFont = Font.font ("sans-serif", FontWeight.MEDIUM, 12);

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
     //
     // nodeBody
        nodeBody.setOnMousePressed (controller.uNodeMousePress);
        nodeBody.setOnDragDetected (controller.uNodeDragDetected);
     //
     // uNode
        uNode.setOnMouseDragged (controller.uNodeDrag);
        uNode.setOnMouseReleased (controller.uNodeMouseRelease);
        uNode.setOnMouseDragReleased (controller.uNodeDragRelease);

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
    /* 
     * 
     */
    public Line beginEdgeDraw(Pane srcNode, Point2D sceneClickPoint)
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
        canvas.getChildren ().remove(theEdge);
    }
}
