package controllers;
import model.*;
import views.*;

import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import controllers.Command.Scope;
import controllers.Command.Action;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;

public class PropertiesCtrl
{
	private AppCtrl appCtrl;
    public PropertiesView propView;

    private double propW = 300;
    private double propH = 400;
    private ListView<String> listView;
    private ObservableList<String> oList;
    private ObservableList<String> nodeData;

	public PropertiesCtrl (AppCtrl aController)
    {
    	appCtrl = aController;
        propView = new PropertiesView (this);        
        configureProperties ();
    }
	
    public double getToolWidth ()
    {
        return appCtrl.getToolWidth();
    }

    public Pane getProperties ()
    {
    	propView.getProperties();
        return propView.getProperties();
    }

    public double getWidth()
    {
        return propW;
    }

    public boolean isVisible ()
    {
        return propView.getProperties().isVisible ();
    }

    public void toggleVisible ()
    {
        if(propView.getProperties().isVisible ())
        {
        	propView.getProperties().setVisible (false);
        }
        else
        {
        	propView.getProperties().setVisible (true);
        }
    }
    
    /**
    *
    * 
    * @param
    */
    public void refreshPropData(ObservableList<String> list)
    {
    	nodeData = list;
    	
    	// happens only the first time this method is called
    	if( !listView.isEditable() )
    	{
            listView.setEditable (true);
            listView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            	@Override
            	public void handle(MouseEvent event) {
            		listView.edit(listView.getSelectionModel().getSelectedIndex());
            		System.out.println("PROPTY: now editing '" + listView.getSelectionModel().getSelectedItem() + "'");
            	}
            });
        }
    	
    	// node id is not a user editable field
    	//listView.setUserData (Integer.parseInt (nodeData.get (0)));
    	listView.getProperties().put("id", (Integer.parseInt (nodeData.get (0))));
    	nodeData.remove (0);
    	
    	// the last String in the list stores the length of the 3 preceding sublists
    	listView.getProperties().put("sizeCode", nodeData.get(nodeData.size() - 1));
    	nodeData.remove(nodeData.size() - 1);
    	
    	// reset using the new data
    	oList.setAll (nodeData);
        propView.redraw (listView);
    }

	/**
	 * 
	 */
	public EventHandler<ListView.EditEvent<String>> propWriteData = new EventHandler<ListView.EditEvent<String>> ()
	{
		@Override
		public void handle(ListView.EditEvent<String> e) {
			
			int id = (int) listView.getProperties().get("id");
			String sizeCode = (String) listView.getProperties().get("sizeCode");
			System.out.println(id + ", " + sizeCode);			
					
			for(char c : sizeCode.toCharArray())
			{				
		    	int currentSize = Character.getNumericValue(((String) listView.getProperties().get("sizeCode")).charAt(0));
		    	for(int i = currentSize; i > 0; i--)
				{
		    		System.out.println("i");
				}
			}
			
			// set newValue from user text edit
			nodeData.set(e.getIndex(), e.getNewValue());
			String name = nodeData.get(0);
			
			// nodeData[0] is node id
			// nodeData[last] is sizeCode
			nodeData.add(0, ((Integer) id).toString());
			nodeData.add(nodeData.size(), "111");
			
			System.out.println("nodeData: " + nodeData);
			
			appCtrl.executeCommand (
					packageAction (Action.EDIT_DATA, Scope.PROPERTY, id, name, nodeData), false);
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
    	return new Command(type, scope, objects);
    }
    
    private void configureProperties ()
    {
    	// Setup properties
    	propView.getProperties().setVisible(false);
    	propView.getProperties().setPrefWidth (appCtrl.getToolWidth ());
    	propView.getProperties().setPrefHeight (propH);
    	propView.getProperties().setBackground(new Background(new BackgroundFill (Color.DARKGRAY, null, null)));
    	propView.getProperties().setStyle("-fx-border-color: gray; -fx-border-width: 2 2 2 2;");
        
        // Table Header
        Text message = new Text("CLICK to Edit Text\n"+
        						"ENTER to Save Text\n"+
   								"   ESC to Cancel    ");        
        TextFlow flow = new TextFlow(message);
        flow.setStyle("-fx-padding: 10 10 10 60; -fx-font-size: 13.5; -fx-font-weight: bold; -fx-font-family: 'monospaced'; -fx-text-alignment: center;");
        flow.setPrefWidth(propW);
        AnchorPane header = new AnchorPane(flow);
        AnchorPane.setBottomAnchor(flow, 0.0);
        header.setPrefWidth(propW);
        header.setPrefHeight(flow.getHeight());
        header.setStyle("-fx-background-color: #F2F2F2; -fx-border-color: darkgray; -fx-border-width: 1px;");
        
        // Table Data - see PropertiesView
        oList = FXCollections.observableArrayList("< Select a ClassBox >");
        listView = new ListView<String>(oList);
        listView.setCellFactory(TextFieldListCell.forListView());
        listView.setOnEditCommit(propWriteData);
        propView.redraw(listView);
        
        // Table Footer
        Button closePanel = new Button(Character.toString ((char) 0x2b05) + " Close Panel");
        closePanel.setOnAction (e -> { if( isVisible())   appCtrl.toggleSlider() ;});
        closePanel.setStyle("-fx-font-size: 15px;");        
        BorderPane footer = new BorderPane();
        footer.setPrefWidth(propW);
        footer.setPrefHeight(170.0);
        footer.setCenter(closePanel);
        footer.setStyle("-fx-padding: 0 0 0 50; -fx-background-color: #F2F2F2; -fx-border-color: darkgray; -fx-border-width: 1px;");        
        
        // Add to properties pane
        propView.getProperties().getChildren().add(0, header);
        propView.getProperties().getChildren().add(2, footer);        
    }

	public boolean executeCommand(Command cmd, boolean isHistory) {
		
		if(isHistory) {
			return false;
		}
		Object[] data = cmd.getData();
				
		if(cmd.actionType == Action.EDIT_DATA)
		{ //(Action.EDIT_DATA, Scope.PROPERTY, id, name, nodeData)
			
			if(data.length != 3)
    		{
    			System.out.println("Data for adding a node is incorrect");
    			System.out.println("Data list expected 3 items but had: " + data.length);
    			return false;
    		}
			// setFromList (Integer id, ObservableList<String> nodeData)
			appCtrl.setFromList((int) data[0], (ObservableList<String>) data[2]);
			
			// refreshVNode(Integer id, String name)
			appCtrl.refreshVNode((int) data[0], (String) data[1]);
			
			// refreshPropData(ObservableList<String> list)
			appCtrl.refreshPropData((ObservableList<String>) data[2]);
			
			System.out.println ("PTYCTR: Command executed EDIT_DATA");
			return true;
		}
				
		return false;
	}
}