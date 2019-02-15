
// General required javafx Classes
import javafx.application.Application;

import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
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
        int init_wind_width = 1500;
        int init_wind_height = 650;
        Color init_background_color = Color.WHITE;
        
        Canvas canvas = new Canvas (init_wind_width, init_wind_height);
        
        Group root = new Group ();
        root.getChildren ().add (canvas);
        
        Scene scene = new Scene (root, init_wind_width, init_wind_height, init_background_color);

        
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
        
        Color outline_color = Color.BLACK;
        double line_width = 1;
        
        Group root = (Group) canvas.getParent ();
        GraphicsContext context = canvas.getGraphicsContext2D ();
        
        String node_name = un.getName();
        
        //*****************DRAW RECTANGLE**************************
        context.setStroke (outline_color);
        context.setLineWidth (line_width);
        context.strokeRect (un.getX(), un.getY(), width, height);
        
        /* 
         * TODO: format text programmatically, not hardcoding.
         */
        Text name = new Text (un.getX() + 30, un.getY() + 12, node_name);
        root.getChildren ().add (name);
    }
}
