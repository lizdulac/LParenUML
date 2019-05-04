package views;

import controllers.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class PropertiesView
{
    private VBox properties;
    HBox hBox;
    ListView<String> listFields;
    ListView<HBox> listButtons;
    ObservableList<HBox> buttons;
   
    public PropertiesView (PropertiesCtrl pController)
    {       
        Button attrButton = new Button(" " + Character.toString ((char) 0x2795) + " ");
        Button funcButton = new Button(" " + Character.toString ((char) 0x2795) + " ");
        Button miscButton = new Button(" " + Character.toString ((char) 0x2795) + " ");
        
        attrButton.setStyle("-fx-font-size: 7;");
        funcButton.setStyle("-fx-font-size: 7;");
        miscButton.setStyle("-fx-font-size: 7;");
        
        Label attrLabel = new Label("   new Attribute");
        Label funcLabel = new Label("   new Function");
        Label miscLabel = new Label("   new Misc.");
        
        HBox blankBox = new HBox();
        HBox attrBox = new HBox(attrButton, attrLabel);
        HBox funcBox = new HBox(funcButton, funcLabel);
        HBox miscBox = new HBox(miscButton, miscLabel);       
        
        buttons = FXCollections.observableArrayList(blankBox, attrBox, funcBox, miscBox);
        listButtons = new ListView<HBox>(buttons);
        listButtons.setPrefWidth (150.0);
        listButtons.setFocusTraversable(false);
        //listButtons.setSelectionModel(new NoSelection<>());
        
        properties = new VBox ();
        hBox = new HBox(listButtons);
        hBox.setStyle("-fx-padding: 0 0 0 50");
        properties.getChildren().add(hBox);
    }

    public Pane getProperties ()
    {
        return properties;
    }
    
    public void redraw(ListView<String> lv) {        
        listFields = lv;
        listFields.setPrefWidth (150.0);
        
        hBox.getChildren().clear();
        hBox.getChildren().addAll(listFields, listButtons);
    }
    
    public void focusOnProp (int i)
    {
        listFields.requestFocus();
        listFields.getSelectionModel().select(i);
        listFields.getFocusModel().focus(i);
        listFields.edit(i);
    }
}