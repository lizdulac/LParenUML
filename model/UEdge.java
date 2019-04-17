package model;

/**
 * The UEdge class represents a directional Line between two UNodes.
 * 
 * @author jamesdryver
 *
 */
public class UEdge {
    protected String name;
    protected UNode end, start;

    /**
     * Basic Constructor for a nameless edge.
     * 
     * @param n1 start node
     * @param n2 end node
     */
    public UEdge(UNode n1, UNode n2) {
        start = n1;
        end = n2;
        name = "";
    }

    /**
     * Basic Constructor for a named edge.
     * 
     * @param n1 start node
     * @param n2 end node
     * @param name name of edge
     */
    public UEdge(UNode n1, UNode n2, String name) {
        start = n1;
        end = n2;
        this.name = name;
    }

    /**
     * Expose the starting node of the edge.
     * 
     * @return start node
     */
    public UNode getStartNode()
    {
        return start;
    }
    
    /**
     * Exposes the ending node of the edge.
     * 
     * @return end node
     */
    public UNode getEndNode()
    {
        return end;
    }
}


/**
 * The general class is a subclass of UEdge that represents a general relationship.
 * 
 * @author jamesdryver
 *
 */
class general extends UEdge {
    /**
     *  These values represent the multiplicity values of the start and end nodes.
     */
    protected char s1,s2,f1,f2;
    
    /**
     * This constructor takes in the two UNodes and the multiplicity values for the start and end nodes
     * 
     * @param n1 start node
     * @param n2 end node
     * @param s1 start multiplicity minimum
     * @param s2 start multiplicity maximum
     * @param f1 end multiplicity minimum
     * @param f2 first multiplicity maximum
     */
    public general(UNode n1, UNode n2, char s1, char s2, char f1, char f2)
    {
        super(n1, n2);
        this.s1 =s1;
        this.s2 =s2;
        this.f1 =f1;
        this.f2 =f2;
    }
    
    /**
     * Change the multiplicity values of the starting UNode.
     * 
     * @param s1 multiplicity minimum
     * @param s2 multiplicity maximum
     */
    public void setStart(char s1, char s2)
    {
        this.s1 =s1;
        this.s2 =s2;
    }
    
    /**
     * Change the multiplicity values of the ending UNode.
     * 
     * @param f1 multiplicity minimum
     * @param f2 multiplicity maximum
     */
    public void setEnd(char f1, char f2)
    {
        this.f1 =f1;
        this.f2 =f2;
    }
    
    
    /**
     * Concatenates the multiplicity starting values as a String.
     * 
     * @return multiplicity of the starting UNode
     */
    public String getStart()
    {
        String coord= s1 +".." + s2;
        return coord;
    }
    
    /**
     * Concatenates the multiplicity ending values as a String.
     * 
     * @return multiplicity of ending UNode
     */
    public String getEnd()
    {
        String coord= f1 +".." + f2;
        return coord;
    }
}

/**
 * This class extends UEdge representing aggregate relationships.
 * 
 * @author jamesdryver
 *
 */
class aggregate extends UEdge {
    protected char s1,s2;
        
    /**
     * This constructor takes in the two UNodes and the multiplicity values for the start node.
     * 
     * @param n1 start node
     * @param n2 end node
     * @param s1 start multiplicity minimum
     * @param s2 start multiplicity maximum
     */
    public aggregate(UNode n1, UNode n2, char s1, char s2)
    {
        super(n1, n2);
        this.s1 =s1;
        this.s2 =s2;
    }
    
    /**
     * Change the multiplicity of the aggregated node
     * 
     * @param s1 multiplicity minimum
     * @param s2 multiplicity maximum
     */
    public void setStart(char s1, char s2)
    {
        this.s1 =s1;
        this.s2 =s2;
    }
    
    /**
     * Concatenates the multiplicity starting values as a String.
     * 
     * @return multiplicity of the aggregated UNode
     */
    public String getStart()
    {
        String coord= s1 +".." + s2;
        return coord;
    }   
}

/**
 * This class extends UEdge representing composite relationships.
 * 
 * @author jamesdryver
 *
 */
class composite extends UEdge {
    protected char s1,s2;
    
    /**
     * This constructor takes in the two UNodes and the multiplicity values for the start node.
     * 
     * @param n1 start node
     * @param n2 end node
     * @param s1 start multiplicity minimum
     * @param s2 start multiplicity maximum
     */
    public composite(UNode n1, UNode n2, char s1, char s2)
    {
        super(n1, n2);
        this.s1 =s1;
        this.s2 =s2;
    }
    
    /**
     * Change the multiplicity of the composite node
     * 
     * @param s1 multiplicity minimum
     * @param s2 multiplicity maximum
     */
    public void setStart(char s1, char s2)
    {
        this.s1 =s1;
        this.s2 =s2;
    }
    
    /**
     * Concatenates the multiplicity starting values as a String.
     * 
     * @return multiplicity of the composite UNode
     */
    public String getStart()
    {
        String coord= s1 +".." + s2;
        return coord;
    }   
}