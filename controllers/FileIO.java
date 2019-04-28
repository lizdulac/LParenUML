package controllers;

import model.*;
import views.CanvasView;
import views.VNode;

import java.util.*;

import controllers.Command.Action;
import controllers.Command.Scope;
import javafx.geometry.Point2D;
import javafx.scene.layout.Region;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

/**
 * FileIO object contains all objects and methods necessary to save the
 * UMLdiagram of an associated GUIcontroller. Files that can be opened/saved in
 * this program will have the extension ".uml".
 * 
 * CAUTION: Functions in this class alter PrintStream "System.out" during
 * execution.
 * 
 * 
 * @author Liz and Christine
 *
 */
public class FileIO
{
    /************************* FILEIO CLASS MEMBERS ***********************/
    private AppCtrl controller;
    private CanvasView view;
    private final String delim = ";&";

    /************************** FILEIO CONSTRUCTOR ************************/
    /**
     * 
     * @param gui
     */
    public FileIO (AppCtrl gui, CanvasView v)
    {
        controller = gui;
        view = v;
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
            filestream.close();
            return true;
        } catch (FileNotFoundException e)
        {
            return false;
            // how to create a new file
        }
    }

    /**
     * This method prints out 
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
            VNode vn = view.getVNode (nodeID);
            double x = vn.getX ();
            double y = vn.getY ();

            ArrayList<String> atts = node.getAttributes();
            ArrayList<String> funcs = node.getFunctions();
            ArrayList<String> misc = node.getMiscs();

            System.out.printf ("%d%s%s%s%f%s%f%s", nodeID, delim, nodeName, delim, x, delim, y, delim);
            printStrings(atts);
            printStrings(funcs);
            printStrings(misc);
            printInEdges(node.getInEdges ());
            printOutEdges(node.getOutEdges ());    
            System.out.println ();
        }
    }
    // coming in, and ending at our node
    // store that edges' startnode id

    /**
     * 
     * @param inEdges
     * @param graph
     * @return
     */
    public void printInEdges (ArrayList<UEdge> inEdges)
    {
        if (inEdges.size() >= 1)
        {
            for (int i = 0; i < inEdges.size (); i++)
            {
                UEdge edge = inEdges.get(i);
                UNode sNode = edge.getStartNode ();
                int sNodeID = sNode.getId ();
                int edgeID = edge.getId();

                System.out.print(edgeID + "," + sNodeID);
                for(int j = 1; j < inEdges.size(); j++)
                {
                    System.out.print("," + edgeID + "," + sNodeID);
                }
                System.out.print(delim);
            }
        }
        else
        System.out.print(delim);
    }

    /**
     * 
     * @param outEdges
     * @param graph
     * @return
     */
    public void printOutEdges (ArrayList<UEdge> outEdges)
    {
        if(outEdges.size() >= 1)
        {
            for (int i = 0; i < outEdges.size (); i++)
            {
                UEdge edge = outEdges.get(i);
                UNode eNode = edge.getEndNode ();
                int eNodeID = eNode.getId ();
                int edgeID = edge.getId();

                System.out.print(edgeID + "," + eNodeID);
                for(int j = 1; j < outEdges.size(); j++)
                {
                    System.out.print("," + edgeID + "," + eNodeID);
                }
                System.out.print(delim);
            }
        }
        else
        System.out.print(delim); 
    }

    //string .replaceAll quotes with /" "
    //surround it with quotes
    //if more than 1, we need a comma
    //String = quote string + quote + endquote
    public void printStrings (ArrayList<String> stringlist)
    {
        if (stringlist.size () >= 1){
                String s1 = stringlist.get(0);
                String replaceString = s1.replaceAll("\"", "\\\"");
                System.out.print("\"" + replaceString + "\"");
                
                for(int i = 1; i < stringlist.size (); i++)
                {
                    String s2 = stringlist.get(i);
                    String replaceString2 = s2.replaceAll("\"", "\\\"");
                    System.out.print("," + "\"" + replaceString + "\"");
                }
                System.out.print(delim);
            }
        else
             System.out.print(delim);
    }
    /**
     * Open file; recreates UGraph with all UNodes and UEdges
     * id;name;x;y;attributes;functions;miscs;edges;edges;
     * 1;a;0.45;34.8;;;;;2,3;
     * 
     * @param file
     * @return
     */
    public boolean open (File file)
    {
        /*
         * data[0] - id data[1] - name data[2] - x data[3] - y
         */
        Scanner fileScanner;
        try
        {
            fileScanner = new Scanner (file);
            while (fileScanner.hasNextLine ())
            {
                String line = fileScanner.nextLine ();
                Scanner lineScanner = new Scanner (line);
                lineScanner.useDelimiter (delim);
                int id = lineScanner.nextInt ();
                String name = lineScanner.next ();
                double x = lineScanner.nextDouble ();
                double y = lineScanner.nextDouble ();
                        
                // add node
                Object[] args = { id, name, x, y };
                Command addNode = new Command (Action.ADD_NODE, Scope.CANVAS, args);
                controller.executeCommand (addNode, true);
                // controller.theGraph.addNode (id, name);
                
                String attributes = lineScanner.next ();
                System.out.println ("FILEIO: attr " + attributes);
                Scanner attributeScanner = new Scanner (attributes);
                ArrayList<String> attribs = parseStrings(attributeScanner, controller.getNode(id));
                for (String a : attribs)
                {
                    System.out.println ("FILEIO: add attr " + a);
                    controller.getNode (id).addAttribute (a);
                }
                attributeScanner.close ();
                
                String functions = lineScanner.next ();
                System.out.println ("FILEIO: funcs " + functions);
                Scanner functionScanner = new Scanner (functions);
                ArrayList<String> funcs = parseStrings(functionScanner, controller.getNode (id));
                for (String f : funcs)
                {
                    System.out.println ("FILEIO: add func " + f);
                    controller.getNode (id).addFunction (f);
                }
                functionScanner.close ();
                
                String miscs = lineScanner.next ();
                System.out.println ("FILEIO: miscs " +  miscs);
                Scanner miscScanner = new Scanner (miscs);
                ArrayList<String> mis = parseStrings(miscScanner, controller.getNode (id));
                for (String m : mis)
                {
                    System.out.println ("FILEIO: add misc " + m);
                    controller.getNode (id).addMisc (m);
                }
                miscScanner.close ();

                String endEdges = lineScanner.next ();
                Scanner endEdgeScanner = new Scanner (endEdges);
                endEdgeScanner.useDelimiter (",");
                while (endEdgeScanner.hasNext ())
                {
                    int edgeId = endEdgeScanner.nextInt ();
                    int endNodeId = endEdgeScanner.nextInt ();
                    if (endNodeId > id)
                    {
                        break;
                    }
                    // add edge

                    if (controller.theGraph.getNode (id) == null || controller.theGraph.getNode (endNodeId) == null)
                    {
                        System.out.println ("Opening error: node of edge null");
                    } else
                    {
                        // Args: Pane, Point2D start, Point2D end
                        VNode vn1 = view.getVNode (id);
                        VNode vn2 = view.getVNode (endNodeId);
                        Region node1 = vn1.getRegion ();
                        Point2D pt1 = new Point2D (vn1.getX (), vn1.getY ());
                        Region node2 = vn2.getRegion ();
                        Point2D pt2 = new Point2D (vn2.getX (), vn2.getY ());
                        Object[] args2 = { node1, pt1, node2, pt2, edgeId };
                        Command addEdge = new Command (Action.ADD_EDGE, Scope.CANVAS, args2);
                        controller.executeCommand (addEdge, true);
                    }
                }
                endEdgeScanner.close ();

                String startEdges = lineScanner.next ();
                Scanner startEdgeScanner = new Scanner (startEdges);
                startEdgeScanner.useDelimiter (",");
                while (startEdgeScanner.hasNext ())
                {
                    int edgeId = startEdgeScanner.nextInt ();
                    int startNodeId = startEdgeScanner.nextInt ();
                    if (startNodeId > id)
                    {
                        break;
                    }
                    // add edge

                    if (controller.theGraph.getNode (id) == null || controller.theGraph.getNode (startNodeId) == null)
                    {
                        System.out.println ("Opening error: node of edge null");
                    } else
                    {
                        // Args: Pane, Point2D start, Point2D end
                        VNode vn1 = view.getVNode (startNodeId);
                        VNode vn2 = view.getVNode (id);
                        Region node1 = vn1.getRegion ();
                        Point2D pt1 = new Point2D (vn1.getX (), vn1.getY ());
                        Region node2 = vn2.getRegion ();
                        Point2D pt2 = new Point2D (vn2.getX (), vn2.getY ());
                        Object[] args2 = { node1, pt1, node2, pt2, edgeId  };
                        Command addEdge = new Command (Action.ADD_EDGE, Scope.CANVAS, args2);
                        controller.executeCommand (addEdge, true);
                    }
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
    
    public ArrayList<String> parseStrings (Scanner scan, UNode node)
    {
        ArrayList<String> strings = new ArrayList<String> ();
        scan.useDelimiter ("\",");
        String current = "";
        while (scan.hasNext ())
        {
            System.out.println ("FILEIOPRS: current is '" + current + "'");
            if (current.length () < 2)
            {
                
            }
            else if (current.charAt (current.length () - 1) == '\\')
            {
                current = current.substring (0, current.length () - 1) + "\",";
            }
            else
            {
                strings.add (current.substring (1));
                current = "";
            }
            current += scan.next ();
        }
        if (current.length () > 1)
        {
            strings.add (current.substring (1, current.length () - 1));
        }
        return strings;
    }
}