package controllers;
import model.*;
import views.*;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import controllers.Command.Scope;
import controllers.Command.Action;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;

public class PropertiesCtrl
{
	private AppCtrl appCtrl;
    public PropertiesView propView;

	private Pane properties;
    private double propW = 230;
    private double propH = 230;
    private ListView<String> listView;
    private ObservableList<String> oList;

	public PropertiesCtrl (AppCtrl aController)
    {
    	appCtrl = aController;
        propView = new PropertiesView (this);
        oList = FXCollections.observableArrayList("< Select a ClassBox >");
        listView = new ListView<String>(oList);
        propView.drawNode(listView);


        configureProperties ();
    }
	
    public double getToolWidth ()
    {
        return appCtrl.getToolWidth();
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
    
    public void refreshData(int id, String name, ObservableList<String> atr)
    {
    	oList.clear();
    	oList = FXCollections.observableArrayList(name);
     	for (String s : atr) {
     		oList.add(s);
     	}
     	
     	listView = new ListView<String>(oList);
     	listView.setUserData(id);
        listView.setEditable(true);
        listView.setCellFactory(TextFieldListCell.forListView());
        listView.setOnEditCommit(propChangeData);
        
        propView.drawNode(listView);
        
        /*
        listView.setOnEditCommit(
 			@Override
 			public void handle(ListView.EditEvent<String> t) {
 				
 				System.out.println("setOnEditCommit");
 			}
 		});    	
         
 		listView.setOnEditCommit(new EventHandler<ListView.EditEvent<String>>() {
 			@Override
 			public void handle(ListView.EditEvent<String> t) {
 				listView.getItems().set(t.getIndex(), t.getNewValue());
 				System.out.println("setOnEditCommit");
 				}
 		});
 		
 		listView.setOnEditCancel(new EventHandler<ListView.EditEvent<String>>() {
 			@Override
 			public void handle(ListView.EditEvent<String> t) {
 				System.out.println("setOnEditCancel");
 			}
 		});	
 		*/
    }


	//new EventHandler<ListView.EditEvent<String>>() {

	/**
	 * 
	 */
	public EventHandler<ListView.EditEvent<String>> propChangeData = new EventHandler<ListView.EditEvent<String>> ()
	{
		@Override
		public void handle(ListView.EditEvent<String> e) {
			
			int id = (int) listView.getUserData();
			
			if(e.getIndex() == 0)
			{
				String newName = e.getNewValue();				
				listView.getItems().set(e.getIndex(), newName);
				String atr = listView.getItems().get(e.getIndex() + 1);
				
				appCtrl.executeCommand (
						packageAction (Action.RENAME_NODE, Scope.PROPERTY, id, newName, atr), false);
			}
			else if(e.getIndex() == 1)
			{
				String name = listView.getItems().get(e.getIndex() - 1);				
				String newAtr =  e.getNewValue();
				listView.getItems().set(e.getIndex(), newAtr);
				
				appCtrl.executeCommand (
						packageAction (Action.UPDATE_ATR, Scope.PROPERTY, id, name, newAtr), false);
			}
			
			System.out.println("propChangeData");
		}
	};
    
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
        properties.setBackground(new Background(new BackgroundFill (Color.GRAY, null, null)));
        properties.setStyle("-fx-border-color: gray; -fx-border-width: 5 5 5 1;");
    }

	public boolean executeCommand(Command cmd, boolean isHistory) {
		
		if(isHistory) {
			return false;
		}
		Object[] data = cmd.getData();
				
		if(cmd.actionType == Action.RENAME_NODE)
		{ //(Action.RENAME_NODE, Scope.PROPERTY, id, newValue)
			
			if(data.length != 3)
    		{
    			System.out.println("Data for adding a node is incorrect");
    			System.out.println("Data list expected 3 items but had: " + data.length);
    			return false;
    		}
			
			appCtrl.getNode((int) data[0]).setName((String) data[1]);;
			appCtrl.getCanvasCtrl().canvasView.redrawVNode((int) data[0], (String) data[1], (String) data[2]);
			
			System.out.println("EXECMD: node " + (int) data[0] + " was renamed by propInspctr");
			return true;
		}
		else if(cmd.actionType == Action.UPDATE_ATR)
		{ //(Action.RENAME_NODE, Scope.PROPERTY, id, newValue)
			
			if(data.length != 3)
    		{
    			System.out.println("Data for adding a node is incorrect");
    			System.out.println("Data list expected 3 items but had: " + data.length);
    			return false;
    		}
			
			appCtrl.getNode((int) data[0]).setName((String) data[1]);;
			appCtrl.getCanvasCtrl().canvasView.redrawVNode((int) data[0], (String) data[1], (String) data[2]);
			
			System.out.println("EXECMD: node " + (int) data[0] + " was renamed by propInspctr");
			return true;
		}
		
		return false;
	}
}