package controllers;
import model.*;
import views.*;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import controllers.Command.Scope;
import controllers.Command.Action;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;

public class PropertiesCtrl
{
	private AppCtrl appCtrl;
    private CanvasCtrl canvasCtrl;
    private UGraph theGraph;
    private PropertiesView propView;

	private Pane properties;
    private double propW = 230;
    private double propH = 330;

	public PropertiesCtrl (AppCtrl aController, CanvasCtrl cController)
    {
    	appCtrl = aController;
        canvasCtrl = cController;
        theGraph = appCtrl.getGraph ();
        propView = new PropertiesView (this, canvasCtrl);

        configureProperties ();
    }

    public void updateView ()
    {
        propView.updateView ();
    }

    public Pane getProperties ()
    {
        return properties;
    }

    public double getWidth()
    {
        return propW;
    }

    public boolean isVisible ()
    {
        return properties.isVisible ();
    }

    public void toggleVisible ()
    {
        if(properties.isVisible ())
        {
            properties.setVisible (false);
        }
        else
        {
            properties.setVisible (true);
        }
    }
    
    /**
     * Packages the parameters and the type of action into a Command class.
     * The execute_command method or other invoker style methods are responsible for recasting the objects.
     * 
     * @param type declared in the Action enum in @Command.java 
     * @param objects a templated list of parameters cast as objects. 
     */
    private Command packageAction(Action type, Scope scope, Object ... objects)
    {
        //add scope
    	return new Command(type, scope, objects);
    }
    
    private void configureProperties ()
    {
        properties = propView.getProperties ();

        properties.setVisible(false);
        properties.setPrefWidth (appCtrl.getToolWidth ());
        properties.setPrefHeight (propH);
        properties.setBackground(new Background(new BackgroundFill (Color.SKYBLUE, null, null)));
        properties.setStyle("-fx-border-color: lightgray; -fx-border-width: 5 5 5 0;");
    }

	public boolean executeCommand(Command cmd, boolean isHistory) {
		
		if(isHistory) {
			return false;
		}
		Object[] data = cmd.getData();
		
		if(cmd.actionType == Action.SELECT_NODE)
		{
			if(data.length != 2)
    		{
    			System.out.println("Data for adding a node is incorrect");
    			System.out.println("Data list expected 2 items but had: " + data.length);
    			return false;
    		}
			
			propView.drawNode ((int)data[0], (String)data[1]);
			System.out.println("EXECMD: PropCtrl updated prop view ");
			return true;
		}
		
		return false;
	}
}


/*

    private ListView configureListView ()
    {
        ListView listView = new ListView();

        listView.getItems().add("Item 1");
        listView.getItems().add("Item 2");
        listView.getItems().add("Item 3");
        listView.setStyle("-fx-border-color: darkgray; -fx-border-width: 5 5 5 0;");
        listView.setVisible(false);
        return listView;
    }

*/