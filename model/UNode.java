package model;

import java.util.*;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * The UNode class keeps a list of outgoing edges and incoming edges. 
 * 
 * @author jamesdryver
 *
 */
public class UNode {

    /************************** UNODE CLASS MEMBERS ***********************/
	private Integer id;
	private String name;
	private ObservableList<String> attributes;
	private ArrayList<String> functions;
	private ArrayList<String> misc;

	private ArrayList<UEdge> edgeStart = new ArrayList<UEdge>();
	private ArrayList<UEdge> edgeEnd = new ArrayList<UEdge>();

    /************************** UNODE CONSTRUCTORS ************************/
	/**
	 * Blank constructor for a UNode. This constructor should never be called.
	 * 
	 * @deprecated use UNode( Integer id, String name) instead.
	 */
	@Deprecated
	public UNode()
	{		
		  id = -1;
		  name ="";
	}
	
	/**
	 * UNode's basic constructor with an id and name.
	 * 
	 * @param id id of node
	 * @param name name of node
	 */
	public UNode( Integer id, String name)
	{
		this.id = id;
		this.name = name;
		this.attributes = FXCollections.observableArrayList("<attribute1>");
		
	}
	
	/**
	 * A UNode's fillable constructor, allowing for UEdge initialization.
	 * 
	 * @param id id of node
	 * @param name name of node
	 * @param end list of incoming edges
	 * @param start list of outgoing edges
	 */
	public UNode(Integer id, String name, ArrayList<UEdge> end, ArrayList<UEdge> start )
	{
		this.id = id;
		this.name = name;
		edgeEnd = end;
		edgeStart = start;		
	}

    /************************** UNODE TEXT EDITING ************************/
	/**
	 * 
	 * @param index
	 * @return
	 */
    public ObservableList<String> getAttributes ()
    {
        return attributes;
    }
	/**
	 * 
	 * @param index
	 * @return
	 */
    public String getAttribute (int index)
    {
        return attributes.get (index);
    }
    
    /**
     * 
     * @param index
     * @param s
     */
    public void setAttribute (int index, String s)
    {
        attributes.set (index, s);
    }
    
    /**
     * 
     * @param attribute
     */
    public void addAttribute (String attribute)
    {
        attributes.add (attribute);
    }
    
    /**
     * 
     * @param attribute
     */
    public void removeAttribute (String attribute)
    {
        attributes.remove (attribute);
    }
    
    /**
     * 
     * @param index
     */
    public void removeAttribute (int index)
    {
        attributes.remove (index);
    }
    
    /**
     * 
     * @param index1
     * @param index2
     */
    public void swapAttributes (int index1, int index2)
    {
        String temp = attributes.get (index1);
        attributes.set (index1, attributes.get (index2));
        attributes.set (index2, temp);
    }
    
    /**
     * 
     * @param index
     * @return
     */
    public String getFunction (int index)
    {
        return functions.get (index);
    }
    
    /**
     * 
     * @param index
     * @param s
     */
    public void setFunction (int index, String s)
    {
        functions.set (index, s);
    }
    
    /**
     * 
     * @param function
     */
    public void addFunction (String function)
    {
        functions.add (function);
    }
    
    /**
     * 
     * @param function
     */
    public void removeFunction (String function)
    {
        functions.remove (function);
    }
    
    /**
     * 
     * @param index
     */
    public void removeFunction (int index)
    {
        functions.remove (index);
    }
    
    /**
     * 
     * @param index1
     * @param index2
     */
    public void swapFunctions (int index1, int index2)
    {
        String temp = functions.get (index1);
        functions.set (index1, functions.get (index2));
        functions.set (index1, temp);
    }
    
    /**
     * 
     * @param index
     * @return
     */
    public String getMisc (int index)
    {
        return misc.get (index);
    }
    
    /**
     * 
     * @param index
     * @param s
     */
    public void setMisc (int index, String s)
    {
        misc.set (index, s);
    }
    
    /**
     * 
     * @param m
     */
    public void addMisc (String m)
    {
        misc.add (m);
    }

    /**
     * 
     * @param m
     */
    public void removeMisc (String m)
    {
        misc.remove (m);
    }
    
    /**
     * 
     * @param index
     */
    public void removeMisc (int index)
    {
        misc.remove (index);
    }
    
    /**
     * 
     * @param index1
     * @param index2
     */
    public void swapMisc (int index1, int index2)
    {
        String temp = misc.get (index1);
        misc.set (index1, misc.get (index2));
        misc.set (index2, temp);
    }
    
    /************************* UNODE GENERAL GETTERS **********************/
	/**
	 * Exposes the id attribute.
	 * 
	 * @return id of node
	 */
	public Integer getId()
	{
		return id;
	}
	
	/**
	 * Exposes the name attribute.
	 * 
	 * @return name of node
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 * Change the name of the Node.
	 * 
	 * @param str new node name
	 */
	public void setName(String str)
	{
		name = str;
	}
	
	/**
	 * Adds a new Edge to outgoing edges.
	 * 
	 * @param e new edge
	 */
	public boolean addOutEdge( UEdge e){
		return edgeStart.add(e);											
	}
	
	/**
	 * Adds a new Edge to incoming edges.
	 * @param e new edge
	 */
	public boolean addInEdge( UEdge e){
		return edgeEnd.add(e);
	}
	
	/**
	 * Exposes the list of incoming edges.
	 * 
	 * @return the list of incoming edges
	 */
	public ArrayList<UEdge> getInEdges(){
		return edgeEnd;
	}
	
	/**
	 * Exposes the list of outgoing edges
	 * 
	 * @return the list of outgoing edges
	 */
	public ArrayList<UEdge> getOutEdges(){
		return edgeStart;
	}
}
