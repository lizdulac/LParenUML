package controllers;


import javafx.scene.layout.Region;
import javafx.geometry.*;
import java.lang.Math;

/**
 * This class creates a number of Point2D objects on each edge of given Pane.
 * 
 * @author jamesdryver
 */
public class AnchorMgr {
	private Point2D[] anchorlist = new Point2D[12];
	private Region region;
	private final int numPoints = 3;
	private double snapDistance = 6.5;
	
	/**
	 * Basic constructor for AnchorMgr.  
	 * Generates anchorpoints immediately.
	 *
	 * @param pn pane to be anchored 
	 */
	public AnchorMgr(Region pn){
		region =pn;
		generate_anchors();
	}
	
	/**
	 * Sets a new pane to be anchored, generates new anchors.
	 *
	 * @param pn pane to be anchored 
	 */
	public void setPane(Region pn)
	{
		region =pn;
		generate_anchors();
	}
	
	/**
	 * Exposes the anchored pane 
	 *
	 */
	public Region getPane() {
		return region;
	}

	
	/**
	 * Generates anchors for a given pane based on the numPoint value.
	 *Anchor points laid out in array as follows:
	 * Ex: numPoints = 3;
	 *         0     4     8
	 *   [ x x x x x x x x x x x ]
	 *   [ x x x x x x x x x x x ]
	 *  2[ x x x x x x x x x x x ]3
	 *   [ x x x x x x x x x x x ]
	 *   [ x x x x x x x x x x x ]
	 *  6[ x x x x x x x x x x x ]7
	 *   [ x x x x x x x x x x x ]
	 *   [ x x x x x x x x x x x ]
	 * 10[ x x x x x x x x x x x ]11
	 *   [ x x x x x x x x x x x ]
	 *   [ x x x x x x x x x x x ]
	 *         1     5     9
	 *
	 */
	public void generate_anchors()
	{
		double h = region.getMaxHeight();
		double w = region.getMaxWidth();	
		double p =0;
		
		for(int i = 0; i < numPoints; ++i ) {
			
			p = 1/(double)(1 + numPoints) * (i + 1);
			
			//anchor points need adjustment for Pane Layout X & Y ??
			
			anchorlist[(i * numPoints)+ i] = new Point2D(p*w , 0 );//0, 4, 8 
			
			anchorlist[(i * numPoints)+ i + 1] = new Point2D(p*w, h );//1, 5, 9
			
			anchorlist[(i * numPoints)+ i + 2] = new Point2D(0, p*h); //2, 6, 10
			
			anchorlist[(i * numPoints)+ i + 3] = new Point2D(w, p*h );//3, 7, 11			
		}	
		
		snapDistance = (1/(double)numPoints *((h+w)/2)) *.35;
		
		for(int i = 0; i < anchorlist.length; ++i)
		{
			if(anchorlist[i] == null)
			{
				System.out.println("Error in anchor data at index: "+ i);
			}
		}
		
	}
	
	public Point2D[] getAnchorlist() {
		return anchorlist;
	}
	
	
	/**
	 * This method detects if the cursor is within a radius of any of these points and returns that point.
	 * 
	 * @param cursor where the cursor is currently
	 * @return Point2D closest to the cursor on the released Node
	 */
	public Point2D getNearAnchor(Point2D cursor) {
		double h = region.getHeight();
		double w = region.getWidth();	
		System.out.println("pane weight: " + w);
		System.out.println("pane height: " + h);
		
		double x = cursor.getX();
		double y = cursor.getY();
		System.out.println("cursor x: " + x);
		System.out.println("cursor y: " + y);
		
		Point2D cur = cursor;
		double topMiddle   = distance(x, y, anchorlist[4].getX(), anchorlist[4].getY());
		double botMiddle   = distance(x, y, anchorlist[5].getX(), anchorlist[5].getY());
		double leftMiddle  = distance(x, y, anchorlist[6].getX(), anchorlist[6].getY());
		double rightMiddle = distance(x, y, anchorlist[7].getX(), anchorlist[7].getY());
		
		
		//upper Right Quadrant
		if(x > w/numPoints *2 && y <= h/numPoints *2){
			
			double topRight = distance(x, y, anchorlist[8].getX(), anchorlist[8].getY());
			double rightTop = distance(x, y, anchorlist[3].getX(), anchorlist[3].getY());
			
			if(topRight < rightTop)
			{
				if(topRight < topMiddle)
					cur = anchorlist[8];
				else
					cur = anchorlist[4];
			}
			else
			{
				if(rightTop < rightMiddle)
					cur = anchorlist[3];
				else
					cur = anchorlist[7];
			}				
		}
		else if(x <= w/numPoints *2 && y <= h/numPoints *2){// Upper Left Quadrant
			
			double topLeft = distance(x, y, anchorlist[0].getX(), anchorlist[0].getY());
			double leftTop = distance(x, y, anchorlist[2].getX(), anchorlist[2].getY());
			
			if(topLeft < leftTop)
			{
				if(topLeft < topMiddle)
					cur = anchorlist[0];
				else
					cur = anchorlist[4];
			}
			else
			{
				if(leftTop < leftMiddle)
					cur = anchorlist[2];
				else
					cur = anchorlist[6];
			}						
		}
		else if(x > w/numPoints *2 && y > h/numPoints *2){//Lower Right Quadrant
	
			double botRight = distance(x, y, anchorlist[9].getX(), anchorlist[9].getY());
			double rightBot = distance(x, y, anchorlist[11].getX(), anchorlist[11].getY());
			
			System.out.println("lower right");
			if(botRight < rightBot)
			{
				System.out.println("--> 1");
				if(botRight < botMiddle)
					cur = anchorlist[9];
				else
					cur = anchorlist[5];
			}
			else
			{
				System.out.println("--> 2");
				if(rightBot < rightMiddle) {
					System.out.println("--> 2.1");
					cur = anchorlist[11];
				}
				else {
					System.out.println("--> 2.2");
					cur = anchorlist[7];
				}
			}
			
		}
		else if(x <= w/numPoints *2 && y > h/numPoints *2){// Lower Left Quadrant
			double leftBot = distance(x, y, anchorlist[10].getX(), anchorlist[10].getY());
			double botLeft = distance(x, y, anchorlist[1].getX(), anchorlist[1].getY());
			
			System.out.println("lower left");
			if(botLeft < leftBot)
			{	
				if(botLeft < botMiddle)
					cur = anchorlist[1];
				else
					cur = anchorlist[5];
			}
			else
			{
				
				if(leftBot < leftMiddle)
					cur = anchorlist[10];
				else
					cur = anchorlist[6];				
			}
		}
		
		System.out.println("anchor x: " + cur.getX());
		System.out.println("anchor y: " + cur.getY());
		return cur;
	}

	
	/**
	 * Returns whether the cursor is close enough to the pane to snap to the anchored pane.
	 *
	 * @param cursor position
	 */
	public boolean hasSnap(Point2D cursor)
	{
		double h = region.getHeight();
		double w = region.getWidth();	
		double px = region.getLayoutX();
		double py = region.getLayoutY();
		double x = cursor.getX();
		double y = cursor.getY();

		
		//if cursor is too far from the pane in question 
		if(x > snapDistance + w + px || x < px - snapDistance 
		|| y > snapDistance + h + py || y < py - snapDistance ) {
			return false;
		}
		return true;
	}
	
	
	public double distance(double x1, double y1, double x2, double y2) {
		return Math.sqrt( (x2 -x1)*(x2 -x1) + (y2-y1)*(y2-y1) );
	}
	
	
}
