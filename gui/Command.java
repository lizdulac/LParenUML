package gui;
import java.util.*;


/**
 * 
 * @author Dryver, An
 *
 */




public class Command {
			
	
	public enum Action
	{
	   ADD_NODE (0), ADD_EDGE (1), DELETE_NODE (2), DELETE_EDGE(3);
	    
	    private final int value; 
	    
	    Action (int value) { 
	        this.value = value; 
	    }
	};	
	
	
	/**
	 * This list stores all relevant information to a command. Beginning with 
	 */
	private Object[] data;	
	protected Action actionType;
	

	//use int/enum
	public Command(Action type, Object[] arr ) {
		actionType = type;
		data = arr;		
	}	
	
	public Object[] getData()
	{
		return data;
	}
	
	public void setData(Object[] list)
	{
		data = list;
	}
	
	
	
	
}



