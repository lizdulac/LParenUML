package views;
import controllers.*;
import model.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
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
    ListView<String> listView;
   
    public PropertiesView (PropertiesCtrl pController)
    {
        propCtrl = pController;
        properties = new AnchorPane ();
    }

    public Pane getProperties ()
    {
        return properties;
    }
    
    public void drawNode (ListView<String> lv) {        
        listView = lv;
        listView.setPrefWidth(180.0);
        listView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                listView.edit(listView.getSelectionModel().getSelectedIndex());
                System.out.println("clicked on " + listView.getSelectionModel().getSelectedItem());
            }
        });
        
        properties.getChildren().clear();
        properties.getChildren().add(listView);
        AnchorPane.setRightAnchor(listView, 0.0);
    }
    
    public void focusOnProp (int i)
    {
        listView.requestFocus();
        listView.getSelectionModel().select(i);
        listView.getFocusModel().focus(i);
        listView.edit(i);
    }
}