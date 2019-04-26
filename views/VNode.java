package views;

import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import javafx.scene.control.ListView;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Control;
import javafx.scene.control.ListCell;
import java.text.NumberFormat;
import javafx.scene.control.MultipleSelectionModel;
import javafx.util.Callback;

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
    ListView<ListView<String>> listView;
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
        //this (x, y, nodeID, "");
    }
    
    /**
     * 
     * @param x
     * @param y
     * @param nodeID
     * @param name
     */
    public VNode (double x, double y, int nodeID, String name, ObservableList<String> atr)
    {
        System.out.printf ("VNODE: VNode %d created\n", nodeID);
        id = nodeID;
        xTrans = x;
        yTrans = y;
        
        int textHeight = 30;
        int height = 4 * textHeight;
        int width = 100;
        Font nodeFont = Font.font ("sans-serif", FontWeight.MEDIUM, 12);
        
        ObservableList<ListView<String>> sectionList = FXCollections.observableArrayList();
        listView = new ListView<ListView<String>>(sectionList);
        listView.setPrefWidth(150.0);
        listView.setSelectionModel(new NoSelectionModel<>());
        
        ObservableList<String> list1 = FXCollections.observableArrayList(name);
        ObservableList<String> list2 = atr;
        ObservableList<String> list3 = FXCollections.observableArrayList("function1 ( )", "function2 ( )");
        ObservableList<String> list4 = FXCollections.observableArrayList("miscellaneous text", "goes in this area");
        
        ListView<String> section1 = nextClassBoxSection(list1);
        ListView<String> section2 = nextClassBoxSection(list2);
        ListView<String> section3 = nextClassBoxSection(list3);
        ListView<String> section4 = nextClassBoxSection(list4);
        sectionList.addAll(section1, section2, section3, section4);
        
        listView.setCellFactory(new Callback<ListView<ListView<String>>, ListCell<ListView<String>>>() {
            @Override public ListCell<ListView<String>> call(ListView<ListView<String>> list) {
                return new NestedListCell();
            }
        });
        listView.prefHeightProperty().bind(section1.prefHeightProperty().add(section2.prefHeightProperty()).add(section3.prefHeightProperty()).add(section4.prefHeightProperty().add(50.0)));
        
        listView.setUserData(nodeID);

        //region = new VBox ();
        //region.setPrefHeight (height);
        //region.setPrefWidth (width);
        //region.relocate (x, y);
        //region.setStyle ("-fx-background-color: white;" + "-fx-border-color: black;");
        // this id matches with the model
        //region.setUserData (nodeID);
    }
    
    private ListView<String> nextClassBoxSection(ObservableList<String> list)
    {
        final int LIST_CELL_HEIGHT = 25;
    	ListProperty<String> lProperty = new SimpleListProperty<>(list);
    	
    	ListView<String> lView = new ListView<String>(list);
    	lView.setSelectionModel(new NoSelectionModel<>());
    	lView.setPrefWidth(listView.getPrefWidth() - 25.0);
    	lView.prefHeightProperty().bind(Bindings.size(lProperty).multiply(LIST_CELL_HEIGHT));
    	
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

    /************************* UNODE GENERAL GETTERS **********************/
    /**
     * 
     * @return
     */
    public double getX ()
    {
        return xTrans;
    }
 
    /**
     * 
     * @return
     */
    public double getY ()
    {
        return yTrans;
    }

    /**
     * 
     * @return
     */
    public ListView<ListView<String>> getRegion ()
    {
        return listView;
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
    
    /**
     * 
     */
    public void delete()
    {
        Pane canvas = (Pane) region.getParent ();
        canvas.getChildren ().remove (this);
    }
}

class NestedListCell extends ListCell<ListView<String>> {
    public NestedListCell() {    }
    @Override
    protected void updateItem(ListView<String> item, boolean empty) {
    	// calling super here is very important - don't skip this!
        super.updateItem(item, empty);
        
        setGraphic(item);
        setStyle("-fx-background-color: white;");
    }
}

class NoSelectionModel<T> extends MultipleSelectionModel<T> {

    @Override
    public ObservableList<Integer> getSelectedIndices() {
        return FXCollections.emptyObservableList();
    }

    @Override
    public ObservableList<T> getSelectedItems() {
        return FXCollections.emptyObservableList();
    }

    @Override
    public void selectIndices(int index, int... indices) {
    }

    @Override
    public void selectAll() {
    }

    @Override
    public void selectFirst() {
    }

    @Override
    public void selectLast() {
    }

    @Override
    public void clearAndSelect(int index) {
    }

    @Override
    public void select(int index) {
    }

    @Override
    public void select(T obj) {
    }

    @Override
    public void clearSelection(int index) {
    }

    @Override
    public void clearSelection() {
    }

    @Override
    public boolean isSelected(int index) {
        return false;
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public void selectPrevious() {
    }

    @Override
    public void selectNext() {
    }
}