package gui;

import javafx.stage.Stage;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Background;

public class GUIview
{
    private GUIcontroller controller;
    private Scene scene;

    private double windowW = 800.0;
    private double windowH = 600.0;

    /*************************** INITIALIZATION ***************************/
    public GUIview (GUIcontroller aController, Stage aStage)
    {
        controller = aController;
        scene = new Scene (configureUI (), windowW, windowH);
        aStage.setScene (scene);
    }

    private BorderPane configureUI ()
    {
        int topMargin = 25;
        int leftMargin = 100;
        int rightMargin = 50;
        int bottomMargin = 25;
        
        BorderPane borderPane = new BorderPane ();

        Separator topSpacer = new Separator ();
        Pane buttonPanel = createButtonPanel ();
        Pane canvas = new Pane ();
        Separator rightSpacer = new Separator ();
        Separator bottomSpacer = new Separator ();

        canvas.setStyle ("-fx-background-color: #f2f2f2; -fx-background-radius: 50;");
        canvas.addEventHandler (MouseEvent.MOUSE_CLICKED, controller.canvasMousePress);
        topSpacer.setVisible (false);
        rightSpacer.setVisible (false);
        bottomSpacer.setVisible (false);

        topSpacer.setPrefHeight (topMargin);
        buttonPanel.setPrefWidth (leftMargin);
        rightSpacer.setPrefWidth (rightMargin);
        bottomSpacer.setPrefHeight (bottomMargin);

        borderPane.setTop (topSpacer);
        borderPane.setLeft (buttonPanel);
        borderPane.setCenter (canvas);
        borderPane.setRight (rightSpacer);
        borderPane.setBottom (bottomSpacer);

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

    
    /*************************** NODE FUNCTIONS ***************************/
    /* Nodes are stored as a child of the "canvas" panel in a group that
     * contains a Rectangle, and then necessary text containers
     */
    public Rectangle drawNode (double x, double y)
    {
        // TODO: only include necessary textboxes
        Group g = new Group();
        int textHeight = 30;
        int height = 4 * textHeight;
        int width = 100;

        Rectangle rect = new Rectangle (x, y, width, height);
        rect.setArcHeight (5);
        rect.setArcWidth (5);
        rect.setFill (Color.WHITE);
        rect.setStroke (Color.web ("0x101010"));
        rect.addEventHandler (MouseEvent.MOUSE_DRAGGED, controller.uNodeDrag);
        rect.addEventHandler (MouseEvent.MOUSE_CLICKED, controller.uNodeMousePress);

        g.getChildren ().add (rect);
        
        Font rectFont = Font.font ("sans-serif", FontWeight.MEDIUM, 12);
        
        // TODO: replace TextAreas with Text or TextBoxes
        // TODO: determine num of required textboxes
        for (int i = 0; i < 1/*4*/; ++i)
        {
            TextArea ta = new TextArea();
            ta.setPrefRowCount (1);
            ta.setPrefWidth (width);
            ta.setPrefHeight (textHeight);
            ta.setFont (rectFont);
            ta.setLayoutX (x);
            ta.setLayoutY (y + i * textHeight);
            g.getChildren ().add (ta);
        }
        
        BorderPane root = (BorderPane) scene.getRoot ();
        root.getChildren ().add (g);

        return rect;
    }
    
    // Translate Rectangle and related graphical elements to the right x, down y
    public void moveNode (Rectangle rect, double x, double y)
    {
        Group group = (Group) rect.getParent ();
        
        ObservableList<Node> list = group.getChildren ();
        
        for (int i = 0; i < list.size (); ++i)
        {
            list.get (i).setTranslateX (list.get (i).getTranslateX () + x);
            list.get (i).setTranslateY (list.get (i).getTranslateY () + y);
        }
    }
    
    // Deletes Group containing Rectangle and text containers related to the UNode
    public void deleteNode (Rectangle rect)
    {
        Group group = (Group) rect.getParent ();
        
        Pane parent = (Pane) group.getParent ();
        
        parent.getChildren ().remove (group);
    }
}
