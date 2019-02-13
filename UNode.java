import java.util.*;

public class UNode {

	private int x,y,z;
	private String name;
	
	private ArrayList<Edge> edgeStart = new ArrayList();
	private ArrayList<Edge> edgeEnd = new ArrayList();
	
	public UNode()
	{		
		x =0;
		y =0;
		z = 0; // 1? -1? depth of unplaced node should be... ??
	}
	
	public UNode(int x, int y, int z, String name )
	{
		this.x =x;
		this.y =y;
		this.z =z;
		this.name = name;
	}
	
	public UNode(int x, int y, int z, String name, ArrayList<Edge> end, ArrayList<Edge> start )
	{
		this.x =x;
		this.y =y;
		this.z =z;
		this.name = name;
		edgeEnd = end;
		edgeStart = start;
				
	}
	
	
	public int getX()
	{
		return x;
	}
	
	public int getY()
	{
		return y;
	}
	
	public int getZ()
	{
		return z;
	}
	
	public String getName()
	{
		return name;
	}
	
	
	
	
	
}
