package model;
import model.*;

public final class ModelUtil {

	private ModelUtil() {
		throw new java.lang.UnsupportedOperationException ("This is a utility class and cannot be instantiated.");
	}

	public static void printStats (UGraph theGraph)
	{
		System.out.println("\n********** Graph **********");
		System.out.println("********* " + theGraph.size () + " Nodes *********\n");
		int edgeTotal = 0;

		for(int i = 0; i < theGraph.size (); i++)
		{
			UNode uNode = theGraph.getNode (i+1);

			System.out.println ("------ Node ------\n");
			System.out.println ("       id: " + uNode.getId ());
			System.out.println ("     name: " + uNode.getName ());
			System.out.println ("      atr: " + uNode.getAttributeList());
			
			System.out.println ("  inEdges: ");
			for (UEdge edge: uNode.getInEdges ())
			{
				edgeTotal++;
				UNode start = edge.getStartNode();
				UNode end = edge.getEndNode();
				System.out.println("           " + start.getName () + " -> " + end.getName ());
			}
			
			System.out.println (" outEdges: ");
			for (UEdge edge: uNode.getOutEdges ())
			{
				UNode start = edge.getStartNode();
				UNode end = edge.getEndNode();
				System.out.println("           " + start.getName () + " -> " + end.getName ());
			}

			System.out.print("\n");
		}

		System.out.println("****** " + edgeTotal + " Edges Total ******");
		System.out.println("***************************\n");
	}
}
