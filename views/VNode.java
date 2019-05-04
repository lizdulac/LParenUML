package views;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Control;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import javafx.util.Callback;


/************************** VNODE DEPENDANT CLASS *************************/
/**
 * @author David
 */
class NestedListCell extends ListCell<ListView<String>>
{
    public NestedListCell () { }
    
    @Override
    protected void updateItem(ListView<String> item, boolean empty) {
        // calling super here is important
        super.updateItem(item, empty);
        
        setGraphic(item);
        setStyle("-fx-background-color: white; -fx-border-width: 0 0 1 0; -fx-border-color: black;");
    }
}

/**
 * @author Liz
 */
public class VNode extends Pane
{
    /************************** VNODE CLASS MEMBERS ***********************/
    private int id;
    private boolean isSelected;
    protected ListView<ListView<String>> parentLV;
    ObservableList<ListView<String>> sectionsList;
    ObservableList<String> className;

    private final double cellHeight = 25;
    /************************** VNODE CONSTRUCTORS ************************/   
    /**
     * 
     * @param x
     * @param y
     * @param nodeID
     * @param scale
     * @param name
     */
    public VNode (double x, double y, int nodeID, double scale, String name, ObservableList<String> attr, ObservableList<String> func, ObservableList<String> misc)
    {
        id = nodeID;
        isSelected = false;
        // this id must match the model
        this.setUserData (nodeID);     
        
        sectionsList = FXCollections.observableArrayList();
        parentLV = new ListView<ListView<String>>(sectionsList);         
        parentLV.setPrefWidth(150.0);
        parentLV.setFocusTraversable(false);
        
        className = FXCollections.observableArrayList(name);
        ObservableList<String> attrList = attr;
        ObservableList<String> funcList = func;
        ObservableList<String> miscList = misc;
        
        ListView<String> nameLV = generateListView(className);
        ListView<String> attrLV = generateListView(attrList);
        ListView<String> funcLV = generateListView(funcList);
        ListView<String> miscLV = generateListView(miscList);
        sectionsList.addAll(nameLV, attrLV, funcLV, miscLV);
        
        parentLV.setCellFactory(new Callback<ListView<ListView<String>>, ListCell<ListView<String>>>() {
            @Override public ListCell<ListView<String>> call(ListView<ListView<String>> list) {
                return new NestedListCell();
            }
        });
        
        parentLV.prefHeightProperty()
           .bind(nameLV.prefHeightProperty()
            .add(attrLV.prefHeightProperty()
            .add(funcLV.prefHeightProperty()
            .add(miscLV.prefHeightProperty()
            .add(30.0))))
        );
        
        this.getChildren().add(parentLV);
        System.out.printf ("V-NODE: VNode %d created\n", nodeID);
        
        this.setScaleX (scale);
        this.setScaleY (scale);
        
        this.setLayoutX (x);
        this.setLayoutY (y);
    }
    /************************* VNODE GENERAL GETTERS **********************/
    /**
     * 
     * @return VNode id (matches corresponding UNode id)
     */
    public int getIntId ()
    {
        return id;
    }
    
    /**
     * 
     * @return 
     */
    public double getX ()
    {
        return this.getLayoutX ();
    }
 
    /**
     * 
     * @return
     */
    public double getY ()
    {
        return this.getLayoutY ();
    }

    /*************************** VNODE FUNCTIONS **************************/
    /**
     * 
     * @param x
     * @param y
     */
    public void moveNode (double x, double y)
    {
        this.setLayoutX (this.getLayoutX () + x);
        this.setLayoutY (this.getLayoutY () + y);
    }
    
    /**
     * 
     * @param list
     * @return
     */
    private ListView<String> generateListView(ObservableList<String> list)
    {
        ListProperty<String> listProperty = new SimpleListProperty<>(list);     
        ListView<String> lView = new ListView<String>(list);
        
        lView.setFocusTraversable(false);
        lView.setStyle("-fx-background-insets: 0; -fx-padding: 0;");
        lView.setPrefWidth(parentLV.getPrefWidth() - 30.0);
        lView.prefHeightProperty().bind(Bindings.size(listProperty).multiply(cellHeight));
        
        lView.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
            @Override
            public ListCell<String> call(ListView<String> list) {
                
                ListCell<String> cell = new ListCell<String>()
                {
                    {
                        prefWidthProperty().bind(lView.widthProperty().subtract(2));
                        setMaxWidth(Control.USE_PREF_SIZE);
                    }
                    
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        
                        if(item != null) {
                            setText(item);
                        }
                        setStyle("-fx-background-color: white;");
                    }
                };
                return cell;
           }
        });
        return lView;
    }

    /**
     * 
     * @param name
     */
    public void refreshName(String name)
    {
        className.setAll(name);
    }
    
    /**
     * 
     * @return
     */
    public boolean getSelected()
    {
    	return isSelected;
    }
    
    /**
     * 
     * @param value
     */
    public void setSelected(boolean value)
    {
    	isSelected = value;
    }
}