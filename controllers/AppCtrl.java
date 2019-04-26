package controllers;

import model.*;
import javafx.scene.Node;
import javafx.scene.Scene;
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
import javafx.scene.image.Image;

import javafx.util.Duration;
import javafx.stage.Stage;
import javafx.stage.Screen;
import javafx.stage.StageStyle;
import javafx.stage.FileChooser;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import java.io.File;
import java.io.IOException;

// import controllers.Command.Action;
import controllers.Command.Scope;
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
    private final String appName = "RParen - a UML Diagram Editor by LParen - ";
    private final double margin = 50;
    private final double cornerRadius = 50;
    private final double toolW = cornerRadius + margin;
    private final String canvasBgHex = "#F2F2F2";
    // private final Background canvasBg = new Background (new BackgroundFill (Color.web (canvasBgHex), null, null));
    private final Background marginBg = new Background (new BackgroundFill (Color.WHITE, null, null));
    private final Background transparent = new Background (new BackgroundFill (Color.TRANSPARENT, null, null));

    /************************* APPCTRL CONSTRUCTOR *********************/
    /**
     * 
     * @param stage
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
     * @return
     */
    public UNode getNode(int id)
    {
        return theGraph.getNode (id);
    }
    
    /**
     * 
     * @param n1
     * @param n2
     * @param edge
     */
    public void linkSingle (UNode n1, UNode n2, String edge)
    {
        theGraph.linkSingle (n1, n2, edge);
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
     */
    public void removeNode (Integer id)
    {
        theGraph.removeNode (id);
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
     * 
     * @return
     */
    public double getToolWidth ()
    {
        return toolW;
    }

    /**
     * 
     * @return
     */
    public ToolState getToolState ()
    {
        return toolState;
    }

    /**
     * 
     * @param stage
     * @return
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
     * 
     * @param borderPane
     * @return
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
     * 
     * @return
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
        // Set Icon to LParen image. Image should be included with code outside
        // of packages
        try
        {
            appStage.getIcons ().add (new Image ("LParen.jpg"));
        } catch (Exception e)
        {
            System.out.println ("AppCtrl ConfigStage: unable to render LParen Icon");
        }

        // vertical - top
        appStage.setY (0);
        // horizontal - center on screen
        double xScreenCenter = Screen.getPrimary ().getVisualBounds ().getWidth () / 2;
        appStage.setX (xScreenCenter - (canvasW + toolW * 2) / 2);
        return configureBorderPane (canvasW, canvasH);
    }

    /**
     * 
     * @param canvasW
     * @param canvasH
     * @return
     */
    private BorderPane configureBorderPane (double canvasW, double canvasH)
    {
        // elements
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
        canvas.setStyle ("-fx-border-color: transparent; -fx-background-color: " + canvasBgHex
                + "; -fx-background-radius: " + cornerRadius + " 0 0 " + cornerRadius + ";");
        rightCorners.setStyle ("-fx-background-color: " + canvasBgHex + "; -fx-background-radius: 0 " + cornerRadius
                + " " + cornerRadius + " 0;");

        borderPane.setBackground (marginBg);
        topMargin.setBackground (marginBg);
        btmMargin.setBackground (marginBg);
        leftMargin.setBackground (marginBg);
        rightMargin.setBackground (marginBg);

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
     * 
     * @param canvasW
     * @return
     */
    private MenuBar configureMenuBar (double canvasW)
    {
        MenuBar menuBar = new MenuBar ();
        Menu menu1 = new Menu ("File");
        menuBar.getMenus ().add (menu1);
        MenuItem open = new MenuItem ("Open");
        MenuItem save = new MenuItem ("Save");
        MenuItem newF = new MenuItem ("New");

        menu1.getItems ().add (open);
        menu1.getItems ().add (save);
        menu1.getItems ().add (newF);

        open.setOnAction (openFile);
        save.setOnAction (saveFile);
        newF.setOnAction (newFile);

        menuBar.setStyle ("-fx-border-color: darkgray; -fx-border-width: 0 0 2 0;");
        menuBar.setBackground (transparent);
        menuBar.setMaxWidth (margin + canvasW + 2.0);
        menuBar.setPrefHeight (30.0);
        menuBar.setMinHeight (30.0);

        return menuBar;
    }


    /**
     * 
     * @param borderPane
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

        // Call toggleSlider() when Properties button is pressed
        ((Button) ((Pane) ((Pane) anchorPane.getChildren ().get (1)).getChildren ().get (1)).getChildren ().get (4))
                .setOnAction (e -> toggleSlider (((Pane) anchorPane.getChildren ().get (0))));
        ((Button) ((Pane) ((Pane) anchorPane.getChildren ().get (1)).getChildren ().get (1)).getChildren ().get (5))
                .setOnAction (e -> ModelUtil.printStats (theGraph));
        // anchorPane.setStyle("-fx-border-color: yellow; -fx-border-width:
        // 5;");

        // listen for changes to appStage
        appStage.heightProperty ().addListener (appChangeH);
        appStage.widthProperty ().addListener (appChangeW);
        appStage.yProperty ().addListener (appChangeY);
        appStage.xProperty ().addListener (appChangeX);
    }

    /**
     * 
     * @return
     */
    private Pane configureSidePane ()
    {
        // Initialization
        Pane marginOverlay = new Pane ();
        Pane toolButtons = configureToolButtons ();
        Pane sidePane = new Pane (marginOverlay, toolButtons);

        toolButtons.setStyle ("-fx-border-color: darkgray; -fx-border-width: 0 0 2 2;");

        // Background Colors
        sidePane.setBackground (transparent);
        marginOverlay.setBackground (marginBg);
        toolButtons.setBackground (marginBg);

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
     * 
     * @return
     */
    private Pane configureToolButtons ()
    {
        /*
         * UniCode numbers for characters on buttons in order: move, createNode,
         * createEdge, delete, edit, undo, redo
         */
        double fontSize = 20;
        final int[] UCodes = new int[] { 0x261D, 0x274F, 8594, 0x2620 };
        final String[] buttonNames = new String[] { "Select", "Create Node", "Create Edge", "Delete" };

        Font buttonsFont = Font.font ("sans-serif", FontWeight.BOLD, fontSize);

        // Gross CSS code to style buttons
        String buttonStyle = ".button {" + "-fx-background-color:linear-gradient(#f2f2f2, #d6d6d6),"
                + "linear-gradient(#fcfcfc 0%, #d9d9d9 20%, #d6d6d6 100%),linear-gradient(#dddddd 0%, #f6f6f6 50%);"
                + "-fx-background-radius: 8,7,6;-fx-background-insets: 0,1,2;-fx-text-fill: black;-fx-effect:"
                + "dropshadow( three-pass-box , rgba(0,0,0,0.6) , 5, 0.0 , 0 , 1 );}" + ".button:selected {"
                + "-fx-effect: dropshadow( three-pass-box , rgba(0,0,0,0.4) , 5, 0.0 , 0 , 1 );}";

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
            buttonPanel.getChildren ().add (buttons[i]);

            if (i < ToolState.values ().length)
            {
                buttons[i].setUserData (ToolState.values ()[i]);
                buttons[i].setOnAction (buttonClick);
            }
        }

        Button showHide = new Button ();
        showHide.setText ("PropSlider");
        showHide.setPrefWidth (fontSize * 4.1);

        Button printStats = new Button ();
        printStats.setText ("PrintStats");
        printStats.setPrefWidth (fontSize * 4.1);

        buttonPanel.getChildren ().addAll (showHide, printStats);
        buttonPanel.setPrefHeight ((fontSize + panelSpacing) * (UCodes.length) * 2.0);
        return buttonPanel;
    }

    /**
     * 
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
     * 
     * @return
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
     * 
     * @param propSlider
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
                // propCtrl.updateView ();
                System.out.println ("PROPTY: visible changed to " + propCtrl.isVisible ());
            });
        } else
        {
            endW = propW;
            propCtrl.toggleVisible ();
            timeline.setOnFinished (event ->
            {
                // propCtrl.updateView ();
                System.out.println ("PROPTY: visible changed to " + propCtrl.isVisible ());
            });
        }

        currentW.addListener ( (obs, oldV, newV) -> propSlider.setPrefWidth (newV.doubleValue ()));
        timeline.getKeyFrames ().add (new KeyFrame (Duration.seconds (0.2), new KeyValue (currentW, endW)));
        timeline.play ();
    }
    
    /*********************** APPCTRL CHANGE LISTENERS *******************/
    /**
     * 
     */
    public ChangeListener<Number> appChangeH = new ChangeListener<Number> ()
    {
        @Override
        public void changed (ObservableValue<? extends Number> observable, Number oldValue, Number newValue)
        {
            // Update sideStage height
            sideStage.setHeight (appScene.getWindow ().getHeight ());
        }
    };

    /**
     * 
     */
    public ChangeListener<Number> appChangeW = new ChangeListener<Number> ()
    {
        @Override
        public void changed (ObservableValue<? extends Number> observable, Number oldValue, Number newValue)
        {
            // Reposition sideStage
            double widthDelta = newValue.doubleValue () - oldValue.doubleValue ();
            sideStage.setX (sideStage.getX () + widthDelta);
        }
    };

    /**
     * 
     */
    private ChangeListener<Number> appChangeY = new ChangeListener<Number> ()
    {
        @Override
        public void changed (ObservableValue<? extends Number> observable, Number oldValue, Number newValue)
        {
            // Reposition sideStage
            double yDelta = newValue.doubleValue () - oldValue.doubleValue ();
            sideStage.setY (sideStage.getY () + yDelta);
        }
    };

    /**
     * 
     */
    private ChangeListener<Number> appChangeX = new ChangeListener<Number> ()
    {
        @Override
        public void changed (ObservableValue<? extends Number> observable, Number oldValue, Number newValue)
        {
            // Reposition sideStage
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
     * This method takes a packaged Command and executes it on the prerequisite
     * that the cmd action has all relevant data needed to call its associated
     * methods.
     *
     * @param cmd
     *            the command to be executed
     * @param isUndo
     *            if this value is true then the command is from the History
     *            class(inbound iteration 3)
     * @return the command executed
     */
    public boolean executeCommand (Command cmd, boolean isHistory)
    {

        /*
         * if(isHistory) { return false; }
         */

        if (cmd.actionScope == Scope.CANVAS)
        {
            canvasCtrl.executeCommand (cmd, isHistory);
        } else if (cmd.actionScope == Scope.PROPERTY)
        {
            propCtrl.executeCommand (cmd, isHistory);
        }

        return false;
    }
}