package views;

import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * 
 * @author Liz
 *
 */
public class VNode
{
    /************************** UNODE CLASS MEMBERS ***********************/
    int id;
    protected double xTrans;
    protected double yTrans;
    Region region;

    /************************** UNODE CONSTRUCTORS ************************/
    /**
     * 
     * @param nodeID
     */
    public VNode (int nodeID)
    {
        this (90, 90, nodeID);
    }
    
    /**
     * 
     * @param x
     * @param y
     * @param nodeID
     */
    public VNode (double x, double y, int nodeID)
    {
        this (x, y, nodeID, "");
    }
    
    /**
     * 
     * @param x
     * @param y
     * @param nodeID
     * @param name
     */
    public VNode (double x, double y, int nodeID, String name)
    {
        System.out.printf ("VNODE: VNode %d created\n", nodeID);
        id = nodeID;
        xTrans = x;
        yTrans = y;
        
        int textHeight = 30;
        int height = 4 * textHeight;
        int width = 100;
        Font nodeFont = Font.font ("sans-serif", FontWeight.MEDIUM, 12);

        region = new VBox ();
        region.setPrefHeight (height);
        region.setPrefWidth (width);
        region.relocate (x, y);
        region.setStyle ("-fx-background-color: white;" + "-fx-border-color: black;");
        // this id matches with the model
        region.setUserData (nodeID);
    }

    /************************* UNODE GENERAL GETTERS **********************/
    /**
     * 
     * @return
     */
    public double getX ()
    {
        return region.getLayoutX ();
    }
 
    /**
     * 
     * @return
     */
    public double getY ()
    {
        return region.getLayoutY ();
    }

    /**
     * 
     * @return
     */
    public Region getRegion ()
    {
        return region;
    }

    /*************************** UNODE FUNCTIONS **************************/
    /**
     * 
     * @param x
     * @param y
     */
    public void moveNode (double x, double y)
    {
        region.setLayoutX (region.getLayoutX () + x);
        region.setLayoutY (region.getLayoutY () + y);
        
        xTrans += x;
        yTrans += y;
    }
}
