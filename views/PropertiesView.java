package views;
import controllers.*;
import model.*;

import javafx.scene.layout.Pane;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.geometry.Pos;

public class PropertiesView
{
	private PropertiesCtrl propCtrl;
    private CanvasCtrl canvasCtrl;
    private StackPane properties;



    public PropertiesView (PropertiesCtrl pController, CanvasCtrl cController)
    {
        propCtrl = pController;
        canvasCtrl = cController;
        properties = new StackPane ();
        //updateView( );
    }

    public Pane getProperties ()
    {
    	return properties;
    }
    
    public void drawNode (int uNodeId, String name) {
    	Label idLabel = new Label(uNodeId + "\n");
    	Label nameLabel = new Label("\n" + name);
    	
        properties.getChildren().clear();
        properties.getChildren().addAll(idLabel, nameLabel);
        properties.setAlignment(idLabel, Pos.TOP_RIGHT);
        properties.setAlignment(nameLabel, Pos.TOP_RIGHT);
    }

    public void updateView ()
    {
        Label idLabel = new Label(canvasCtrl.getCurrentNode().getId().toString() + "\n");
        Label nameLabel = new Label("\n" + canvasCtrl.getCurrentNode().getName());

        System.out.println (idLabel.getText());
        System.out.println (nameLabel.getText()); 

        //Label idLabel = new Label("emptyId.");
        //Label nameLabel = new Label("emptyName.");

        properties.getChildren().clear();
        properties.getChildren().addAll(idLabel, nameLabel);
        properties.setAlignment(idLabel, Pos.TOP_RIGHT);
        properties.setAlignment(nameLabel, Pos.TOP_RIGHT);
       
        System.out.println("updateView.");
    }
}