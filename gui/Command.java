package gui;
import java.util.*;


/**
 * This class keeps an action, prepared to be executed.  
 *
 * @author Dryver, An
 *
 */
public class Command {
            
    
    /**
     * Enum of Actions that are able to be undone/redone
     */
    public enum Action
    {
       ADD_NODE (0), ADD_EDGE (1), DELETE_NODE (2), DELETE_EDGE(3);
        
        private final int value; 
        
        Action (int value) { 
            this.value = value; 
        }
    };  
    
    
    /**
     * This list stores all relevant information to methods enacted within a command, 
     * beginning with ids, names, and booleans, followed by any x or y coordinates of the first relevant method within the command.
     * The second method's possible ids, names, booleans, coordinates follow after. 
     * The third method ... etc.
     */
    private Object[] data;  
    protected Action actionType;
    

    /**
     * Basic constructor for the Command class.
     *
     * @param type the type of action
     * @param arr array of parameters for the action
     */
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
