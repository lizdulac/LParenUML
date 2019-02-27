// General required javafx Classes

import javafx.application.Application;

import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.StageStyle;
import javafx.scene.image.Image; // for icon

//*********************************************************

public class UMLEditor extends Application
{
    //*******************GLOBAL VARIABLES**********************
    
    
    //*************************MAIN****************************
    /*
     * Main: hands control to JavaFX
     * DO NOT CHANGE; logic begins in "start" method
     */
    public static void main (String[] args)
    {
        launch (args);
    }
    
    
    //********START (CONTROL FLOW BEGINS HERE)*****************
    /* 
     * start: Method required by JavaFX.
     * DO NOT CHANGE SIGNATURE (including override).
     * This method initializes the window, and sets the scene.
     * The scene has a group (root) of visual objects including
     * a background (canvas).
     */
    @Override
    public void start (Stage primaryStage) throws Exception
    {
        int initWindWidth = 1500;
        int initWindHeight = 650;
        Color initBackgroundColor = Color.WHITE;
        
        Canvas canvas = new Canvas (initWindWidth, initWindHeight);
        
        Group root = new Group ();
        root.getChildren ().add (canvas);
        
        Scene scene = new Scene (root, initWindWidth, initWindHeight, initBackgroundColor);

        
        //*******************LOGIC GOES HERE***********************
        // TODO: call functions that set stage/begin program
        UNode un = new UNode (90, 90, 0, "name");
        drawNode (canvas, un); // dummy call testing drawNode

        
        //*****************SET WINDOW FEATURES********************* 
        /* 
         * TODO: handle case where image cannot be found
         * For now, comment out if image cannot be found
        */
        primaryStage.setTitle ("LParen - UML Diagram Editor");
        primaryStage.initStyle (StageStyle.valueOf ("UNIFIED"));
        primaryStage.getIcons ().add (new Image ("LParen.jpg"));
        primaryStage.setScene (scene);
        primaryStage.show ();
    }

    
    //*****************DRAWING METHODS GO HERE*****************

    //************************DRAW NODE************************
    /* drawNode: draw rectangle with text "name" in upper center
     * canvas: Canvas on which UML Diagram node will be drawn
     * x, y: coordinates of upper left corner where UML Diagram
     *       node will be drawn
     */
    public void drawNode (Canvas canvas, UNode un)
    {
        /* TODO: determine rectangle width based off of name length
         * and maximum width. Determine rectangle height based off of
         * amount of words/boxes in diagram
         */
        double width = 100;
        double height = 100;
        
        Color fillColor = Color.WHITE;
        Color borderColor = Color.BLACK;
        double lineWidth = 1;
        
        Group root = (Group) canvas.getParent ();
        
        String nodeName = un.getName();
        
        //*****************DRAW RECTANGLE**************************
        Rectangle r = new Rectangle (un.getX(), un.getY(), width, height);
        r.setFill (fillColor);
        r.setStroke (borderColor);
        r.setStrokeWidth (lineWidth);
        
        /* 
         * TODO: format text programmatically, not hardcoding.
         */
        Text name = new Text (un.getX() + 30, un.getY() + 12, nodeName);
        root.getChildren ().addAll (r, name);
    }
}
