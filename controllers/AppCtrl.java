package controllers;
import model.*;
import controllers.Command.Scope;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tooltip;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;

import java.io.File;
import java.io.IOException;
import javafx.util.Duration;
import javafx.stage.Stage;
import javafx.stage.Screen;
import javafx.stage.StageStyle;
import javafx.stage.FileChooser;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

/**
 * 
 * @author 
 *
 */
public class AppCtrl
{
    /************************ APPCTRL CLASS MEMBERS ***********************/
    private static AppCtrl appCtrl;
    private PropertiesCtrl propCtrl;
    private CanvasCtrl canvasCtrl;
    private ToolState toolState;
    protected UGraph theGraph;
    private Stage appStage;
    private Scene appScene;
    private Stage sideStage;
    private Scene sideScene;
    private FileIO fileIO;

    /*********************** APPCTRL FINAL VARIABLES *********************/
    private final double margin = 50;
    private final double cornerRadius = 50;
    private final double toolW = cornerRadius + margin;
    private final String appName = "RParen - a UML Diagram Editor by LParen - ";
    private final String canvasBgHex = "#F2F2F2";
    // private final Background canvasBg = new Background (new BackgroundFill (Color.web (canvasBgHex), null, null));
    private final Background marginBg = new Background (new BackgroundFill (Color.WHITE, null, null));
    private final Background transparent = new Background (new BackgroundFill (Color.TRANSPARENT, null, null));

    /************************* APPCTRL CONSTRUCTOR *********************/
    /**
     * AppCtrl follows a singleton pattern so that
     * only one instance is ever created. This is the
     * only constructor & it is private. It is called
     * only one time, inside getAppController()
     * 
     * @param stage should be the JavaFX 'primaryStage' generated by Application.start()
     */
    private AppCtrl (Stage stage)
    {
        // initialization
        BorderPane borderPane;
        theGraph = new UGraph ();
        toolState = ToolState.SELECT;
        canvasCtrl = new CanvasCtrl (this);
        propCtrl = new PropertiesCtrl (this, canvasCtrl);
        fileIO = new FileIO (this, canvasCtrl.canvasView);

        // appStage - configure primary application window
        appStage = stage;
        borderPane = configureAppStage ();
        appScene = new Scene (borderPane);
        appStage.setScene (appScene);
        appStage.show ();

        // sideStage - configure the side panels
        configureSideStage (borderPane);
        sideStage.setScene (sideScene);
        sideStage.show ();

        // start - GUI interface is now visible
        System.out.println ("\n******* Start *******\n");
        appStage.setHeight (Screen.getPrimary ().getVisualBounds ().getHeight ());
        sideStage.setHeight (Screen.getPrimary ().getVisualBounds ().getHeight ());
    }


    /*********************** APPCTRL GRAPH FUNCTIONS *******************/
    /**
     * 
     * @return
     */
    public UGraph getGraph ()
    {
        return theGraph;
    }
    
    /**
     * 
     * @param id
     * @param name
     */
    public void addNode (Integer id, String name)
    {
        theGraph.addNode (id, name);
    }
    
    /**
     * 
     * @param id
     * @return
     */
    public UNode getNode(int id)
    {
        return theGraph.getNode (id);
    }
    
    /**
     * 
     * @param id
     */
    public void removeNode (Integer id)
    {
        cleanEdges (getNode(id));
        theGraph.removeNode (id);
    }
    
    /**
     * 
     * @param n1
     * @param n2
     * @param edge
     */
    public void addEdge (Integer id, UNode n1, UNode n2, String name)
    {
        theGraph.addEdge (id, n1, n2, name);
    }
    
    /**
     * 
     * @param id
     * @return
     */
    public UEdge getEdge (Integer id)
    {
        return theGraph.getEdge (id);
    }
    
    /**
     * 
     * @param id
     */
    public void removeEdge (Integer id)
    {
        theGraph.removeEdge (id);
        eraseEdge(id);
    }
    
    /*************************** UNODE FUNCTIONS **************************/
    /**
     *  Clean the edges off of a Node.
     * 
     * @version 3.0 Inbound Iteration 3 
     */
    public void cleanEdges(UNode n)
    {    	
        //clean outgoing edges and their ends  
        for (UEdge e: n.getOutEdges())
        {
        	theGraph.removeEdgeFromIn(e.getId ());
            eraseEdge(e.getId ());
        }
        
        //clean incoming edges and their starts
        for (UEdge e : n.getInEdges())
        {
        	theGraph.removeEdgeFromOut(e.getId ());
            eraseEdge(e.getId ());
        }
    }
    
    /**
     * Erases Line representing Edge from canvas,
     * but leaves model unchanged
     * 
     * @param id
     */
    public void eraseEdge (Integer id)
    {
    	
        executeCommand (packageAction(Action.DELETE_EDGE, Scope.CANVAS, id), false);
    }  
    
    /**
     * Packages the parameters and the type of action into a Command class. The
     * execute_command method or other invoker style methods are responsible for
     * recasting the objects.
     * 
     * @param type
     *            declared in the Action enum in @Command.java
     * @param objects
     *            a templated list of parameters cast as objects.
     */
    private Command packageAction (Action type, Scope scope, Object... objects)
    {
        // add scope
        return new Command (type, scope, objects);
    }
    

    /*********************** APPCTRL GENERAL GETTERS *******************/
    /**
     * 
     * @return
     */
    public PropertiesCtrl getPropCtrl ()
    {
        return propCtrl;
    }

    /**
     * Exposes the tool width, which
     * is needed to build the GUI.
     * 
     * @return the tool width, which is cornerRadius + margin
     */
    public double getToolWidth ()
    {
        return toolW;
    }

    /**
     * Exposes the tool state, which is needed to
     * perform the correct actions on the canvas.
     * 
     * @return enum representing currently active tool
     */
    public ToolState getToolState ()
    {
        return toolState;
    }

    /**
     * Enforces a singleton pattern, ensuring that
     * only a single instance of AppCtrl is ever created.
     * 
     * @param stage should be the JavaFX 'primaryStage' generated by Application.start()
     * @return the application controller singleton
     */
    public static AppCtrl getAppController (Stage stage)
    {
        if (appCtrl == null)
        {
            appCtrl = new AppCtrl (stage);
        }
        return appCtrl;
    }

    /*********************** APPCTRL CONFIGURATIONS ********************/
    /**
     * This offset is necessary to overcome platform inconsistencies
     * in how a window is maximized vertically. Essentially the
     * offset is the difference between the actual height of the
     * screen and the height of a vertically maximized window. Ideally
     * these two values would always be the same and the offset would
     * be zero but this is not always true.
     * 
     * @param borderPane layout manager pane for primary application window
     * @return the calculated offset
     */
    private double calculateOffset (BorderPane borderPane)
    {
        double titleBarH = appScene.getY ();
        double appWindowH = appScene.getWindow ().getHeight ();
        double currentCanvasH = ((Pane) borderPane.getCenter ()).getHeight ();
        double anchorOffset = appWindowH - titleBarH - currentCanvasH - (margin * 2);

        if (anchorOffset < 0)
        {
            anchorOffset = 0;
        }
        return anchorOffset;
    }

    /**
     * Configure settings for primary application window (appStage).
     * This should be called before the application
     * window is made visible with Window.show()
     * 
     * @return layout manager pane for primary application window
     */
    private BorderPane configureAppStage ()
    {
        double canvasW = 700;
        double canvasH = canvasW;

        // Stage settings
        appStage.setMinWidth (canvasW);
        appStage.setMinHeight (canvasH);
        appStage.initStyle (StageStyle.DECORATED);
        appStage.setTitle (appName + "Untitled Document");

        // Set app titlebar icon to LParen logo.
        // Logo image file should be in root directory with Main.java
        try {
              appStage.getIcons ().add (new Image ("LParen.jpg"));
        } catch (Exception e) {
              System.out.println ("AppCtrl ConfigStage: unable to render LParen Icon");
        }

        // center app horizontally on screen
        double xScreenCenter = Screen.getPrimary ().getVisualBounds ().getWidth () / 2;
        appStage.setX (xScreenCenter - (canvasW + toolW * 2) / 2);

        appStage.setY (0);
        return configureBorderPane (canvasW, canvasH);
    }

    /**
     * Configure settings & add elements to borderPane.
     * It is the layout manager for primary application window.
     * 
     * @param canvasW initial width of the canvas
     * @param canvasH initial height of the canvas
     * @return layout manager pane for primary application window
     */
    private BorderPane configureBorderPane (double canvasW, double canvasH)
    {
        MenuBar menuBar = configureMenuBar (canvasW);
        Pane canvas = canvasCtrl.getCanvas ();
        Pane rightCorners = new Pane ();
        BorderPane borderPane = new BorderPane ();

        // Margins
        StackPane topMargin = new StackPane (menuBar);
        StackPane btmMargin = new StackPane ();
        StackPane leftMargin = new StackPane ();
        StackPane rightMargin = new StackPane (rightCorners);
        StackPane.setMargin (rightCorners, new Insets (0, margin, 0, 0));
        StackPane.setAlignment (menuBar, Pos.TOP_LEFT);

        // Background Colors
        borderPane.setBackground (marginBg);
        topMargin.setBackground (marginBg);
        btmMargin.setBackground (marginBg);
        leftMargin.setBackground (marginBg);
        rightMargin.setBackground (marginBg);
        canvas.setStyle       ("-fx-background-color: " + canvasBgHex + ";"+
                               "-fx-background-radius: " + cornerRadius + " 0 0 " + cornerRadius + ";");
        rightCorners.setStyle ("-fx-background-color: " + canvasBgHex + ";"+
                               "-fx-background-radius: 0 " + cornerRadius + " " + cornerRadius + " 0;");

        // Dimensions
        canvas.setPrefWidth (canvasW);
        canvas.setPrefHeight (canvasH);
        topMargin.setPrefHeight (margin);
        btmMargin.setPrefHeight (margin);
        leftMargin.setPrefWidth (margin);
        rightMargin.setPrefWidth (toolW);
        rightCorners.setPrefWidth (cornerRadius);

        // BorderPane Layout
        borderPane.setCenter (canvas);
        borderPane.setTop (topMargin);
        borderPane.setBottom (btmMargin);
        borderPane.setLeft (leftMargin);
        borderPane.setRight (rightMargin);
        BorderPane.setAlignment (canvas, Pos.TOP_LEFT);
        return borderPane;
    }

    /**
     * Create new MenuBar, add items to it, and set the
     * appropriate event handlers. The menuBar is positioned
     * within the topMargin of the borderPane layout manager.
     * 
     * @param canvasW initial width of the canvas
     * @return the JavaFX MenuBar that was created
     */
    private MenuBar configureMenuBar (double canvasW)
    {
        // ************ FILE MENU ************
        // MenuItems
        MenuItem open = new MenuItem ("Open");
        MenuItem save = new MenuItem ("Save");
        MenuItem newF = new MenuItem ("New");
        // EventHandlers
        open.setOnAction (openFile);
        save.setOnAction (saveFile);
        newF.setOnAction (newFile);
        // Add Items
        Menu fileMenu = new Menu ("File", null, open, save, newF);


        // ************* MENU BAR *************
        MenuBar menuBar = new MenuBar(fileMenu);
        menuBar.setBackground (transparent);
        menuBar.setStyle ("-fx-border-color: darkgray; -fx-border-width: 0 0 2 0;");
        menuBar.setMaxWidth (margin + canvasW + 2.0);
        menuBar.setPrefHeight (30.0);
        menuBar.setMinHeight (30.0);
        return menuBar;
    }


    /**
     * Configure settings for the secondary window (sideStage).
     * This window contains the toolButtons and the properties
     * inspector. This method should be called before the
     * window is made visible with Window.show()
     * 
     * @param borderPane layout manager pane for primary application window
     */
    private void configureSideStage (BorderPane borderPane)
    {
        // initialization
        double xOrigin = borderPane.localToScreen (borderPane.getLayoutX (), borderPane.getLayoutY ()).getX ()
                + borderPane.getWidth () - toolW;
        double anchorOffset = calculateOffset (borderPane);
        AnchorPane anchorPane = new AnchorPane ();
        VBox propSlider = configurePropSlider ();
        Pane sidePane = configureSidePane ();
        sideScene = new Scene (anchorPane);
        sideStage = new Stage ();

        // anchorPane
        anchorPane.setPrefWidth (toolW * 4);
        anchorPane.prefHeightProperty ().bind (appScene.heightProperty ());
        anchorPane.getChildren ().addAll (propSlider, sidePane);
        AnchorPane.setBottomAnchor (sidePane, anchorOffset);
        anchorPane.setBackground (transparent);

        // propSlider
        propSlider.setLayoutX (cornerRadius);

     // sideStage
        sideStage.initStyle (StageStyle.TRANSPARENT);
        sideScene.setFill (Color.TRANSPARENT);
        sideStage.initOwner (appStage);
        sideStage.setX (xOrigin);
        sideStage.setY (0);

        // listen for changes to appStage
        appStage.heightProperty ().addListener (appChangeH);
        appStage.widthProperty ().addListener (appChangeW);
        appStage.yProperty ().addListener (appChangeY);
        appStage.xProperty ().addListener (appChangeX);

        //*** Development / Debugging Buttons ***
        //  Setting these event handlers requires traversing the JavaFX Scene Graph.
        //  The code is UGLY but:
        //     - it works :)
        //     - this code (and the 2 buttons) will be eventually be deleted
        //
        // PropSlider button triggers toggleSlider()
        ((Button) ((Pane) ((Pane) anchorPane.getChildren ().get (1)).getChildren ().get (1)).getChildren ().get (5))
                .setOnAction (e -> toggleSlider (((Pane) anchorPane.getChildren ().get (0))));
        
        // PrintStats button triggers ModelUtil.printStats()
        ((Button) ((Pane) ((Pane) anchorPane.getChildren ().get (1)).getChildren ().get (1)).getChildren ().get (6))
                .setOnAction (e -> ModelUtil.printStats (getGraph()));
    }

    /**
     * Create new Pane (sidePane) & add elements to it.
     * This sidePane contains the toolButtons panel.
     * This sidePane exists within the main
     * anchorPane of the sideStage window.
     * 
     * @return the Pane that was created
     */
    private Pane configureSidePane ()
    {
        // Initialization
        Pane marginOverlay = new Pane ();
        Pane toolButtons = configureToolButtons ();
        Pane sidePane = new Pane (marginOverlay, toolButtons);

        // Background Colors
        sidePane.setBackground (transparent);
        marginOverlay.setBackground (marginBg);
        toolButtons.setBackground (marginBg);
        toolButtons.setStyle ("-fx-border-color: darkgray; -fx-border-width: 0 0 2 2;");

        // Dimensions
        sidePane.prefHeightProperty ().bind (appScene.heightProperty ());
        marginOverlay.prefHeightProperty ().bind (appScene.heightProperty ().subtract (margin));
        marginOverlay.setPrefWidth (margin);
        toolButtons.setPrefWidth (toolW);

        // Layout
        marginOverlay.setLayoutX (cornerRadius);
        toolButtons.setLayoutY (30.0);
        return sidePane;
    }

    /**
     * Create new Pane (toolButtons), add buttons,
     * and set the buttonClick event handler. The
     * toolButtons pane is contained by sidePane.
     * 
     * @return the Pane that was created
     */
    private Pane configureToolButtons ()
    {
        /*
         * Unicode numbers for button icons, in order:
         * MOVE, SELECT, ADD_NODE, ADD_EDGE, DELETE
         */
        double fontSize = 20;
        final int[] UCodes = new int[] { 0x2723, 0x261D, 0x274F, 8594, 0x2620 };
        final String[] buttonNames = new String[] { "Move", "Select", "Create Node", "Create Edge", "Delete" };

        Font buttonsFont = Font.font ("sans-serif", FontWeight.BOLD, fontSize);

        // Gross CSS code to style buttons
        String buttonStyle = ".button {" + "-fx-background-color:linear-gradient(#f2f2f2, #d6d6d6),"
                + "linear-gradient(#fcfcfc 0%, #d9d9d9 20%, #d6d6d6 100%),linear-gradient(#dddddd 0%, #f6f6f6 50%);"
                + "-fx-background-radius: 8,7,6;-fx-background-insets: 0,1,2;-fx-text-fill: black;-fx-effect:"
                + "dropshadow( three-pass-box , rgba(0,0,0,0.6) , 5, 0.0 , 0 , 1 );}" + ".button:selected {"
                + "-fx-effect: dropshadow( three-pass-box , rgba(0,0,0,0.4) , 5, 0.0 , 0 , 1 );}";

        VBox toolButtons;
        double panelSpacing = 25.0;
        Pos panelAlignment = Pos.CENTER;
        toolButtons = new VBox (panelSpacing);
        toolButtons.setAlignment (panelAlignment);

        // create Button objects
        Button[] buttons = new Button[UCodes.length];
        for (int i = 0; i < UCodes.length; ++i)
        {
            buttons[i] = new Button (Character.toString ((char) UCodes[i]));
            // Set text to be displayed when button is hovered over
            if (i < buttonNames.length)
            {
                final Tooltip tt = new Tooltip ();
                tt.setText (buttonNames[i]);
                buttons[i].setTooltip (tt);
            }
            buttons[i].setFont (buttonsFont);
            buttons[i].setStyle (buttonStyle);
            toolButtons.getChildren ().add (buttons[i]);

            if (i < ToolState.values ().length)
            {
                buttons[i].setUserData (ToolState.values ()[i]);
                buttons[i].setOnAction (buttonClick);
            }
        }

        //*** Development / Debugging Buttons ***
        // PropSlider
        Button showHide = new Button ();
        showHide.setText ("PropSlider");
        showHide.setPrefWidth (fontSize * 4.1);
        // PrintStats
        Button printStats = new Button ();
        printStats.setText ("PrintStats");
        printStats.setPrefWidth (fontSize * 4.1);
        toolButtons.getChildren ().addAll (showHide, printStats);
        
        toolButtons.setPrefHeight ((fontSize + panelSpacing) * (UCodes.length) * 2.0);
        return toolButtons;
    }

    /**
     * When a tool button is clicked, set
     * the appropriate toolState value.
     */
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

    /**
     * Create new Pane (propSlider) & add elements to it.
     * This propSlider contains the properties inspector.
     * This propSlider pane exists within the main
     * anchorPane of the sideStage window.
     * 
     * @return the Pane that was created
     */
    private VBox configurePropSlider ()
    {
        // initialization
        Pane properties = propCtrl.getProperties ();
        Pane topSpacer = new Pane ();
        VBox propSlider = new VBox ();

        // topSpacer
        topSpacer.setBackground (transparent);
        topSpacer.prefHeightProperty ()
                .bind (appScene.heightProperty ().subtract (properties.heightProperty ()).divide (2.0));
        topSpacer.setPrefWidth (toolW);

        // propSlider
        propSlider.getChildren ().addAll (topSpacer, properties);
        propSlider.setBackground (transparent);
        propSlider.setPrefWidth (toolW);
        return propSlider;
    }
    
    /**
     * The slider has two possible states: hidden or visible.
     * This method toggles the slider from its current state
     * to the opposite state. Visually the transition is an
     * animation of the pane sliding in/out from behind the
     * main application window.
     * 
     * @param propSlider the Pane that contains the properties inspector
     */
    private void toggleSlider (Pane propSlider)
    {
        double endW;
        DoubleProperty currentW = new SimpleDoubleProperty (propSlider.getWidth ());
        double propW = propCtrl.getWidth ();
        Timeline timeline = new Timeline ();

        if (propCtrl.isVisible ())
        {
            endW = 0;
            timeline.setOnFinished (event ->
            {
                propCtrl.toggleVisible ();
                System.out.println ("PROPTY: visible changed to " + propCtrl.isVisible ());
            });
        } else
        {
            endW = propW;
            propCtrl.toggleVisible ();
            timeline.setOnFinished (
                event -> System.out.println ("PROPTY: visible changed to " + propCtrl.isVisible ()));
        }

        currentW.addListener ( (obs, oldV, newV) -> propSlider.setPrefWidth (newV.doubleValue ()));
        timeline.getKeyFrames ().add (new KeyFrame (Duration.seconds (0.2), new KeyValue (currentW, endW)));
        timeline.play ();
    }
    
    /*********************** APPCTRL CHANGE LISTENERS *******************/
    /**
     * Whenever the height of appStage changes,
     * set the height of sideStage to match.
     */
    public ChangeListener<Number> appChangeH = new ChangeListener<Number> ()
    {
        @Override
        public void changed (ObservableValue<? extends Number> observable, Number oldValue, Number newValue)
        {
            sideStage.setHeight (appScene.getWindow ().getHeight ());
        }
    };

    /**
     * Whenever the width of appStage changes, reposition
     * sideStage horizontally by the same amount
     */
    public ChangeListener<Number> appChangeW = new ChangeListener<Number> ()
    {
        @Override
        public void changed (ObservableValue<? extends Number> observable, Number oldValue, Number newValue)
        {
            double widthDelta = newValue.doubleValue () - oldValue.doubleValue ();
            sideStage.setX (sideStage.getX () + widthDelta);
        }
    };

    /**
     * Whenever the vertical position of appStage changes,
     * reposition sideStage vertically by the same amount.
     */
    private ChangeListener<Number> appChangeY = new ChangeListener<Number> ()
    {
        @Override
        public void changed (ObservableValue<? extends Number> observable, Number oldValue, Number newValue)
        {
            double yDelta = newValue.doubleValue () - oldValue.doubleValue ();
            sideStage.setY (sideStage.getY () + yDelta);
        }
    };

    /**
     * Whenever the horizontal position of appStage changes,
     * reposition sideStage horizontally by the same amount.
     */
    private ChangeListener<Number> appChangeX = new ChangeListener<Number> ()
    {
        @Override
        public void changed (ObservableValue<? extends Number> observable, Number oldValue, Number newValue)
        {
            double xDelta = newValue.doubleValue () - oldValue.doubleValue ();
            sideStage.setX (sideStage.getX () + xDelta);
        }
    };
    
    /******************** APPCTRL FILEIO EVENT HANDLERS *****************/
    /**
     * 
     */
    public EventHandler<ActionEvent> saveFile = new EventHandler<ActionEvent> ()
    {
        @Override
        public void handle (ActionEvent e)
        {
            FileChooser fc = new FileChooser ();
            fc.setTitle ("Save File");

            // Set extension filter
            //FileChooser.ExtensionFilter extFiler = new FileChooser.ExtensionFilter ("UML files (*.uml)", "*.uml");
            // For easier debugging:
            FileChooser.ExtensionFilter extFiler = new FileChooser.ExtensionFilter ("TXT files (*.txt)", "*.txt");
            fc.getExtensionFilters ().add (extFiler);

            File file = fc.showSaveDialog (appStage);
            if (file != null)
            {
                fileIO.save (file);
                appStage.setTitle (appName + file.getName ());
            }
        }
    };

    /**
     * 
     */
    public EventHandler<ActionEvent> openFile = new EventHandler<ActionEvent> ()
    {
        @Override
        public void handle (ActionEvent e)
        {
            FileChooser fc = new FileChooser ();
            fc.setTitle ("Open File");

            // Set extension filter
            //FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter ("UML files (*.uml)", "*.uml");
            // For easier debugging:
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter ("TXT files (*.txt)", "*.txt");
            fc.getExtensionFilters ().add (extFilter);

            File file = fc.showOpenDialog (appStage);
            if (file != null)
            {
                fileIO.open (file);
                appStage.setTitle (appName + file.getName ());
            }
        }
    };

    /**
     * re-launch this program in a new process
     */
    public EventHandler<ActionEvent> newFile = new EventHandler<ActionEvent> ()
    {
        @Override
        public void handle (ActionEvent e)
        {
            try
            {
                System.out.println (AppCtrl.class.getResource ("AppCtrl.class"));
                // jar:file:/C:/Users/Liz/Desktop/umleditor.jar!/controllers/AppCtrl.class
                
                // file:/C:/Users/Liz/workspace/LParen/controllers/AppCtrl.class

                
                Runtime.getRuntime ().exec ("java Main");
            } catch (IOException ex)
            {
                System.out.println ("newFile exec error");
            }
        }
    };

    /*********************** APPCTRL EXECUTE COMMAND ********************/  

    /**
     * Route the provided Command to the appropriate controller for 
     * execution. The value of cmd.actionScope (either CANVAS or PROPERTY)
     * determines which controller will receive the Command.
     *
     * @param cmd the command to be executed
     * @param isHistory if this value is true then the command is from the History class
     * @return a boolean value
     */
    public boolean executeCommand (Command cmd, boolean isHistory)
    {
        // if(isHistory) { return false; }

        if (cmd.actionScope == Scope.CANVAS) {
            canvasCtrl.executeCommand (cmd, isHistory);
        }
        else if (cmd.actionScope == Scope.PROPERTY) {
            propCtrl.executeCommand (cmd, isHistory);
        }

        return false;
    }
}