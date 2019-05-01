package views;
import controllers.*;
import model.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Pos;

public class PropertiesView
{
    private PropertiesCtrl propCtrl;
    private AnchorPane properties;
    HBox hBox;
    ListView<String> listFields;
    ListView<Button> listButtons;
    ObservableList<Button> buttons;
   
    public PropertiesView (PropertiesCtrl pController)
    {
        propCtrl = pController;
        
        Button blank = new Button();
        blank.setVisible(false);
        Button attr = new Button("+ New Attr.");
        Button func = new Button("+ New Func.");
        Button misc = new Button("+ New Misc.");
        
        buttons = FXCollections.observableArrayList(blank, attr, func, misc);
        listButtons = new ListView<Button>(buttons);
        listButtons.setPrefWidth (100.0);
        
        properties = new AnchorPane ();
        hBox = new HBox(listButtons);
        hBox.setStyle("-fx-padding: 0 0 0 55");
        properties.getChildren().add(hBox);
        AnchorPane.setRightAnchor (hBox, 0.0);
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