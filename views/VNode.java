package views;

import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import model.UNode;

public class VNode
{
    StackPane pane;
    UNode node;
    protected double xTrans;
    protected double yTrans;
    VBox uNode;
    
    public VNode (UNode n)
    {
        this (90, 90, n);
    }
    
    public VNode (double x, double y, UNode n)
    {
        node = n;
        xTrans = x;
        yTrans = y;
        
        // TODO: only include necessary textboxes
        int textHeight = 30;
        int height = 4 * textHeight;
        int width = 100;
        Font nodeFont = Font.font ("sans-serif", FontWeight.MEDIUM, 12);

        // StackPane allows visual elements to be layered
        pane = new StackPane ();

        pane.setPrefHeight (height);
        pane.setPrefWidth (width);
        pane.relocate (x, y);
        pane.setStyle ("-fx-background-color: white;" + "-fx-border-color: black;");

        // TODO: replace TextAreas with Text or TextBoxes
        // TODO: determine num of required textboxes
        TextArea ta = new TextArea ();
        for (int i = 0; i < 1/* 4 */; ++i)
        {
            ta.setText (node.getName ());
            ta.setPrefRowCount (1);
            ta.setPrefWidth (width);
            ta.setPrefHeight (textHeight);
            ta.setFont (nodeFont);
            ta.setLayoutX (x);
            ta.setLayoutY (y + i * textHeight);
            ta.setStyle ("-fx-border-color: black; -fx-border-style: solid solid none solid;");
        }

        // VBox uNode contains TextArea ta and StackPane nodeBody
        uNode = new VBox (ta, pane);

        uNode.setPrefHeight (height);
        uNode.setPrefWidth (width);
        uNode.relocate (x, y);
        // this id matches with the model
        uNode.setUserData (node.getId ());   
    }
    
    public double getX ()
    {
        return xTrans;
    }
    public double getY ()
    {
        return yTrans;
    }
    public Pane getPane ()
    {
        return uNode;
    }
    public void moveNode (double x, double y)
    {
        System.out.printf ("VNODE%d: moveNode %5.2f %5.2f\n", node.getId (), x, y);
        System.out.printf ("        moveNode from (%5.2f, %5.2f)\n", pane.getLayoutX (), pane.getLayoutY ());

        uNode.setLayoutX (uNode.getLayoutX () + x);
        uNode.setLayoutY (uNode.getLayoutY () + y);
        
        xTrans += x;
        yTrans += y;
        System.out.printf ("        moveNode to (%5.2f, %5.2f)\n", pane.getLayoutX (), pane.getLayoutY ());
    }
    
    public void delete()
    {
        Pane canvas = (Pane) pane.getParent ();
        canvas.getChildren ().remove (this);
    }
    
}
