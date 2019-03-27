package gui;
import java.util.*;


/**
 * 
 * @author Dryver, An
 *
 */

public class Command {
	
	/**
	 * This list stores all relevant information to a command. Beginning with 
	 */
	private Object[] data;
	private String actionName;
	
	public Command(String name, Object[] arr ) {
		actionName = name;
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
