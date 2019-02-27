import java.util.function.Function;
import javafx.application.Application;

import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;
import javafx.scene.shape.Rectangle;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.RadioButton;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.ObservableNumberValue;
import javafx.event.EventHandler;

public class Collision extends Application
{
    // GLOBAL VARIABLES
    double orgSceneX, orgSceneY;
    Rectangle lastClicked = new Rectangle();
    Rectangle redRect, pplRect, bluRect, grnRect;

    public static void main (String[] args)
    {
        launch (args);
    }

    @Override
    public void start (Stage primaryStage) throws Exception 
    {
        Group root = new Group();
        Scene scene = new Scene(root, 550, 500);

        // set up Collision radio buttons        
        RadioButton radioOn = new RadioButton("Collision ON");
        RadioButton radioOff = new RadioButton("Collision OFF");

        // add radio buttons to ToggleGroup and VBox
        VBox vbox = new VBox(radioOff, radioOn);
        ToggleGroup radioGroup = new ToggleGroup();
        radioOn.setToggleGroup(radioGroup);
        radioOff.setToggleGroup(radioGroup);

        // default collision state is off
        radioOff.setSelected(true);
        System.out.println("\n******* Start *******");
        System.out.println("\nCollision is now OFF.");

        // add listener to OFF radio button to detect & handle toggling
        radioOff.selectedProperty().addListener( new ChangeListener<Boolean>() {
            @Override 
            public void changed( ObservableValue<? extends Boolean> obs,
                                 Boolean wasSelected,
                                 Boolean isNowSelected)
            {
                // Collision turned off
                if(isNowSelected)
                {
                    System.out.println("Collision is now OFF.");

                    // remove collision handlers
                    redRect.setOnMouseDragged(null);
                    pplRect.setOnMouseDragged(null);
                    bluRect.setOnMouseDragged(null);
                    grnRect.setOnMouseDragged(null);

                    // add overlap handlers
                    redRect.setOnMouseDragged(dragOverlapHandler);
                    pplRect.setOnMouseDragged(dragOverlapHandler);
                    bluRect.setOnMouseDragged(dragOverlapHandler);
                    grnRect.setOnMouseDragged(dragOverlapHandler);
                }
                // Collision turned on
                else
                {
                    System.out.println("Collision is now ON.");

                    // remove overlap handlers
                    redRect.setOnMouseDragged(null);
                    pplRect.setOnMouseDragged(null);
                    bluRect.setOnMouseDragged(null);
                    grnRect.setOnMouseDragged(null);

                    // add collision handlers
                    redRect.setOnMouseDragged(dragCollisionHandler);
                    pplRect.setOnMouseDragged(dragCollisionHandler);
                    bluRect.setOnMouseDragged(dragCollisionHandler);
                    grnRect.setOnMouseDragged(dragCollisionHandler);
                }
            } } );

        // set up rectangles
        redRect = createRect(70, 70, Color.TOMATO);
        redRect.setX(100);
        redRect.setY(100);
        pplRect = createRect(70, 70, Color.PLUM);
        pplRect.setX(350);
        pplRect.setY(200);
        bluRect = createRect(70, 70, Color.SKYBLUE);
        bluRect.setX(300);
        bluRect.setY(350);
        grnRect = createRect(70, 70, Color.YELLOWGREEN);
        grnRect.setX(400);
        grnRect.setY(350);

        // set up connecting lines
        Line lineRP = connect(redRect, pplRect);
        Line linePB = connect(pplRect, bluRect);
        Line linePO = connect(pplRect, grnRect);

        // add nodes to the root Group
        root.getChildren().add(vbox);
        root.getChildren().add(redRect);
        root.getChildren().add(pplRect);
        root.getChildren().add(bluRect);
        root.getChildren().add(grnRect);
        root.getChildren().add(lineRP);
        root.getChildren().add(linePB);
        root.getChildren().add(linePO);

        // set the scene
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // returns new Rectangle of given width, height & color
    private Rectangle createRect(double width, double height, Color color)
    {
        Rectangle r = new Rectangle(width, height, color);

        // set mouse event handlers 
        r.setOnMousePressed(mousePressedHandler);
        r.setOnMouseReleased(mouseReleasedHandler);

        // start with overlap handler because collision is off by default
        r.setOnMouseDragged(dragOverlapHandler);

        return r;
    }

    // returns new line connecting two rectangles r1 & r2 
    private Line connect(Rectangle r1, Rectangle r2)
    {
        Line l = new Line();
        l.setStrokeWidth(2);

        l.startXProperty().bind(Bindings.add(r1.xProperty(), getRectCenterX(r1)));
        l.startYProperty().bind(Bindings.add(r1.yProperty(), getRectCenterY(r1)));
 
        l.endXProperty().bind(Bindings.add(r2.xProperty(), getRectCenterX(r2)));
        l.endYProperty().bind(Bindings.add(r2.yProperty(), getRectCenterY(r2)));

        return l;
    }

    // returns X coordinate for center of Rectangle r
    private ObservableNumberValue getRectCenterX(Rectangle r)
    {
        return Bindings.divide(r.widthProperty(), 2.0);
    }

    // returns Y coordinate for center of Rectangle r
    private ObservableNumberValue getRectCenterY(Rectangle r)
    {
        return Bindings.divide(r.heightProperty(), 2.0);
    }


//************************** EVENT HANDLERS **************************


    private EventHandler<MouseEvent> mousePressedHandler = new EventHandler<MouseEvent>()
    {
        @Override 
        public void handle(MouseEvent t)
        {
            Rectangle source = ((Rectangle) t.getSource());

            // get the starting coordinates
            orgSceneX = t.getSceneX();
            orgSceneY = t.getSceneY();

            source.toFront();
            lastClicked = source;
        }
    };

    private EventHandler<MouseEvent> mouseReleasedHandler = new EventHandler<MouseEvent>()
    {
        @Override 
        public void handle(MouseEvent t)
        {
            ((Rectangle) t.getSource()).toBack();
        }
    };

    private EventHandler<MouseEvent> dragOverlapHandler = new EventHandler<MouseEvent>()
    {
        @Override 
        public void handle(MouseEvent t)
        {
            Rectangle source = ((Rectangle) t.getSource());

            double offsetX = t.getSceneX() - orgSceneX;
            double offsetY = t.getSceneY() - orgSceneY;

            source.setX(source.getX() + offsetX);
            source.setY(source.getY() + offsetY);

            orgSceneX = t.getSceneX();
            orgSceneY = t.getSceneY();
        }
    };

    private EventHandler<MouseEvent> dragCollisionHandler = new EventHandler<MouseEvent>()
    {
        @Override 
        public void handle(MouseEvent t)
        {
            // hasCollision() checks for collisions, returns true or false
            Function<Shape, Boolean> hasCollision = s ->
            {
                Shape intersectRed = new Rectangle();
                Shape intersectPpl = new Rectangle();
                Shape intersectBlu = new Rectangle();
                Shape intersectGrn = new Rectangle();
                
                // shapes cannot collide with themselves
                if (s != redRect) {
                    intersectRed = Shape.intersect(s, redRect);
                }
                if (s != pplRect) {
                    intersectPpl = Shape.intersect(s, pplRect);
                }
                if (s != bluRect) {
                    intersectBlu = Shape.intersect(s, bluRect);
                }
                if (s != grnRect) {
                    intersectGrn = Shape.intersect(s, grnRect);
                }

                // check for collisions with every other shape
                if (intersectRed.getBoundsInParent().getWidth() > 0 |
                    intersectPpl.getBoundsInParent().getWidth() > 0 |
                    intersectBlu.getBoundsInParent().getWidth() > 0 |
                    intersectGrn.getBoundsInParent().getWidth() > 0  )
                {
                    return true;
                }
                else
                {
                    return false;
                }
            };

            Rectangle source = ((Rectangle) t.getSource());

            // source Rectangle has a collision
            if(hasCollision.apply(source) && source != lastClicked)
            {
                source.setOpacity(0.5);
            }
            // source Rectangle does not have a collision
            else
            {
                double offsetX = t.getSceneX() - orgSceneX;
                double offsetY = t.getSceneY() - orgSceneY;

                source.setX(source.getX() + offsetX);
                source.setY(source.getY() + offsetY);

                orgSceneX = t.getSceneX();
                orgSceneY = t.getSceneY();

                source.setOpacity(1.0);
                lastClicked = null;
            }
        }
    };
}
