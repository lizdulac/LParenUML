package gui;

import java.util.*;

import javafx.scene.layout.Pane;
import javafx.geometry.*;
import java.lang.Math;

/**
 * This class creates a number of Point2D objects on each edge of given Pane
 * 
 * @author jamesdryver
 */
public class AnchorMgr {
	private Point2D[] anchorlist = new Point2D[12];
	private Pane pane;
	private final int numPoints = 3;
	private double snapDistance = 6.5;
	
	/**
	 * 
	 * @param pn the given pane
	 */
	public AnchorMgr(Pane pn){
		pane =pn;
		generate_anchors();
	}
	
	
	public void setPane(Pane pn)
	{
		pane =pn;
		generate_anchors();
	}
	
	public Pane getPane() {
		return pane;
	}

	
	/**
	 * Generates anchors for a given pane
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
		double h = pane.getHeight();
		double w = pane.getWidth();	
		double p =0;
		
		for(int i = 0; i < numPoints; ++i ) {
			
			p = 1/(double)numPoints * (i + 1);
			
			//anchor points need adjustment for Pane Layout X & Y
			
			anchorlist[(i * numPoints)+ i] = new Point2D(p*w, 0);
			anchorlist[(i * numPoints)+ i + 1] = new Point2D(p*w, w);
			anchorlist[(i * numPoints)+ i + 2] = new Point2D(0, p*h);
			anchorlist[(i * numPoints)+ i + 3] = new Point2D(h, p*h);			
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
	
	
	/**
	 * 
	 * This method detects if the cursor is within a radius of any of these points and returns that point.
	 * 

	 * @param cursor where the cursor is currently
	 * @return Point2D closest to the cursor on the released Node
	 */
	public Point2D getNearAnchor(Point2D cursor) {
		double h = pane.getHeight();
		double w = pane.getWidth();	
		double px = pane.getLayoutX();
		double py = pane.getLayoutY();
		double x = cursor.getX();
		double y = cursor.getY();
		
		Point2D cur = cursor;
		
		//upper Right Quadrant
		if(x > h/numPoints *2 && y >= w/numPoints *2){
			
			double topRight = distance(x, y, anchorlist[8].getX(), anchorlist[8].getY());
			double rightTop = distance(x, y, anchorlist[3].getX(), anchorlist[3].getY());
			
			if(topRight < rightTop)
			{
				
				cur = anchorlist[8];
			}
			else
			{
				
				cur = anchorlist[3];
			}
						
		}
		else if(x <= h/numPoints *2 && y >= w/numPoints *2){// Upper Left Quadrant
			
			double topLeft = distance(x, y, anchorlist[0].getX(), anchorlist[0].getY());
			double leftTop = distance(x, y, anchorlist[2].getX(), anchorlist[2].getY());
			
			if(topLeft < leftTop)
			{
				
				cur = anchorlist[0];
			}
			else
			{
				cur = anchorlist[2];
			}			
			
		}
		else if(x > h/numPoints *2 && y < w/numPoints *2){//Lower Right Quadrant
	
			double botRight = distance(x, y, anchorlist[9].getX(), anchorlist[9].getY());
			double rightBot = distance(x, y, anchorlist[11].getX(), anchorlist[11].getY());
			
			if(botRight < rightBot)
			{
				
				cur = anchorlist[9];
			}
			else
			{
				cur = anchorlist[11];
			}
			
		}
		else if(x <= h/numPoints *2 && y < w/numPoints *2){// Lower Left Quadrant
			double botLeft = distance(x, y, anchorlist[10].getX(), anchorlist[10].getY());
			double leftBot = distance(x, y, anchorlist[1].getX(), anchorlist[1].getY());
			
			if(botLeft < leftBot)
			{
				
				cur = anchorlist[10];
			}
			else
			{
				cur = anchorlist[1];
			}
			
		}
			

		return cur;
	}

	
	public boolean hasSnap(Point2D cursor)
	{
		double h = pane.getHeight();
		double w = pane.getWidth();	
		double px = pane.getLayoutX();
		double py = pane.getLayoutY();
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
