package gui;

import model.*;
import java.util.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

/**
 * FileIO object contains all objects and methods necessary to save the UMLdiagram of an associated GUIcontroller.
 * Files that can be opened/saved in this program will have the extension ".uml".
 * 
 * CAUTION: Functions in this class alter PrintStream "System.out" during execution.
 * 
 * @author Liz and Christine
 *
 */
public class FileIO
{
    /************************* FILEIO CLASS MEMBERS ***********************/
    private GUIcontroller controller;
    File f;

    /************************** FILEIO CONSTRUCTOR ************************/
    /**
     * 
     * @param gui
     */
    public FileIO (GUIcontroller gui)
    {
        controller = gui;
    }

    /*************************** FILEIO FUNCTIONS *************************/
    /**
     * 
     * @param file
     * @return
     */
    public boolean save (File file)
    {
        try
        {
            PrintStream console = System.out;
            PrintStream filestream = new PrintStream (file);
            System.setOut (filestream);
            saveUGraph ();
            System.setOut (console);
            return true;
        } catch (FileNotFoundException e)
        {
            return false;
            // how to create a new file
        }
    }

    /**
     * 
     */
    private void saveUGraph ()
    {
        // iterate through array of Integers (call get all nodes)
        // for each Integer get node with that Integer key value (getNode)
        UGraph g = controller.theGraph;
        Integer intArray[] = g.getAllNodes ();

        for (int i = 0; i < intArray.length; i++)
        {
            UNode node = g.getNode (intArray[i]);
            Integer nodeID = node.getId ();
            String nodeName = node.getName ();

            ArrayList<UNode> nodeIns = inEdgesList (node.getInEdges (), g);
            ArrayList<UNode> nodeOuts = outEdgesList (node.getOutEdges (), g);

            System.out.printf ("%d;%s;", nodeID, nodeName);
            if (nodeIns.size () > 1)
            {
                System.out.print (nodeIns.get (0));
                for (int j = 1; j < nodeIns.size (); j++)
                {
                    System.out.print (", " + nodeIns.get (j));
                }
                System.out.print (";");
            } else
                System.out.print (";");

            if (nodeOuts.size () > 1)
            {
                System.out.print (nodeOuts.get (0));
                for (int k = 1; k < nodeOuts.size (); k++)
                {
                    System.out.print (", " + nodeOuts.get (k));
                }
                System.out.println (";");
            } else
                System.out.println (";");
        }

        System.out.close ();
    }
    // coming in, and ending at our node
    // store that edges' startnode id

    /**
     * 
     * @param inEdges
     * @param graph
     * @return
     */
    public ArrayList<UNode> inEdgesList (ArrayList<UEdge> inEdges, UGraph graph)
    {
        ArrayList<UNode> nodeIns2 = new ArrayList<UNode> ();
        for (int i = 0; i < inEdges.size (); i++)
        {
            UNode sNode = inEdges.get (i).getStartNode ();
            int sNodeID = sNode.getId ();
            nodeIns2.add (graph.getNode (sNodeID));
        }
        return nodeIns2;
    }

    /**
     * 
     * @param outEdges
     * @param graph
     * @return
     */
    public ArrayList<UNode> outEdgesList (ArrayList<UEdge> outEdges, UGraph graph)
    {
        ArrayList<UNode> nodeOuts2 = new ArrayList<UNode> ();
        for (int i = 0; i < outEdges.size (); i++)
        {
            UNode eNode = outEdges.get (i).getEndNode ();
            int eNodeID = eNode.getId ();
            nodeOuts2.add (graph.getNode (eNodeID));
        }
        return nodeOuts2;
    }

    /**
     * Open file; recreates UGraph with all UNodes and UEdges
     * 
     * @param file
     * @return
     */
    public boolean open (File file)
    {
        Scanner fileScanner;
        try
        {
            fileScanner = new Scanner (file);
            while (fileScanner.hasNextLine ())
            {
                String line = fileScanner.nextLine ();
                Scanner lineScanner = new Scanner (line);
                lineScanner.useDelimiter (";");
                int id = lineScanner.nextInt ();
                String name = lineScanner.next ();
                controller.theGraph.addNode (id, name);

                String endEdges = lineScanner.next ();
                Scanner endEdgeScanner = new Scanner (endEdges);
                endEdgeScanner.useDelimiter (",");
                while (endEdgeScanner.hasNext ())
                {
                    int endNodeId = endEdgeScanner.nextInt ();
                    if (endNodeId > id)
                    {
                        break;
                    }
                    controller.theGraph.linkSingle (controller.theGraph.getNode (id),
                            controller.theGraph.getNode (endNodeId), "");
                }
                endEdgeScanner.close ();

                String startEdges = lineScanner.next ();
                Scanner startEdgeScanner = new Scanner (startEdges);
                startEdgeScanner.useDelimiter (",");
                while (startEdgeScanner.hasNext ())
                {
                    int startNodeId = startEdgeScanner.nextInt ();
                    if (startNodeId > id)
                    {
                        break;
                    }
                    controller.theGraph.linkSingle (controller.theGraph.getNode (startNodeId),
                            controller.theGraph.getNode (id), "");
                }
                startEdgeScanner.close ();

                lineScanner.close ();
            }
            fileScanner.close ();
            return true;
        } catch (FileNotFoundException e)
        {
            return false;
        }
    }
}
