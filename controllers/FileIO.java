package controllers;

import model.*;
import views.CanvasView;
import views.VNode;

import java.util.*;

import controllers.Command.Action;
import controllers.Command.Scope;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
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
            filestream.close ();
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

            ObservableList<String> atts = node.getAttributes ();
            ObservableList<String> funcs = node.getFunctions ();
            ObservableList<String> misc = node.getMiscs ();

            System.out.printf ("%d%s%s%s%f%s%f%s", nodeID, delim, nodeName, delim, x, delim, y, delim);
            printStrings (atts);
            printStrings (funcs);
            printStrings (misc);
            printInEdges (node.getInEdges ());
            printOutEdges (node.getOutEdges ());
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
        if (inEdges.size () >= 1)
        {
            UEdge edge = inEdges.get (0);
            UNode sNode = edge.getStartNode ();
            int sNodeID = sNode.getId ();
            int edgeID = edge.getId ();

            System.out.print (edgeID + "," + sNodeID);
            for (int j = 1; j < inEdges.size (); j++)
            {
                UEdge e = inEdges.get (j);
                UNode n = e.getStartNode ();
                int nId = n.getId ();
                int eId = e.getId ();

                System.out.print ("," + eId + "," + nId);
            }
            System.out.print (delim);
        } else
            System.out.print (delim);
    }

    /**
     * 
     * @param outEdges
     * @param graph
     * @return
     */
    public void printOutEdges (ArrayList<UEdge> outEdges)
    {
        if (outEdges.size () >= 1)
        {
            UEdge edge = outEdges.get (0);
            UNode eNode = edge.getEndNode ();
            int eNodeID = eNode.getId ();
            int edgeID = edge.getId ();

            System.out.print (edgeID + "," + eNodeID);
            for (int j = 1; j < outEdges.size (); j++)
            {
                UEdge e = outEdges.get (j);
                UNode n = e.getEndNode ();
                int nId = n.getId ();
                int eId = e.getId ();
                
                System.out.print ("," + eId + "," + nId);
            }
            System.out.print (delim);
        } else
            System.out.print (delim);
    }

    // string .replaceAll quotes with /" "
    // surround it with quotes
    // if more than 1, we need a comma
    // String = quote string + quote + endquote
    public void printStrings (ObservableList<String> stringlist)
    {
        if (stringlist.size () >= 1)
        {
            String s1 = stringlist.get (0);
            String replaceString = s1.replaceAll ("\"", "\\\"");
            System.out.print ("\"" + replaceString + "\"");

            for (int i = 1; i < stringlist.size (); i++)
            {
                String s2 = stringlist.get (i);
                String replaceString2 = s2.replaceAll ("\"", "\\\"");
                System.out.print ("," + "\"" + replaceString + "\"");
            }
            System.out.print (delim);
        } else
            System.out.print (delim);
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
                Scanner attributeScanner = new Scanner (attributes);
                ArrayList<String> attribs = parseStrings (attributeScanner, controller.getNode (id));
                for (int i = 0; i < attribs.size (); ++i)
                {
                    if ( i < controller.getNode (id).getAttributes ().size ())
                    {
                        controller.getNode (id).setAttribute (i, attribs.get (i));
                    }
                    else
                    {
                        controller.getNode (id).addAttribute (attribs.get (i));
                    }
                }
                attributeScanner.close ();

                String functions = lineScanner.next ();
                Scanner functionScanner = new Scanner (functions);
                ArrayList<String> funcs = parseStrings (functionScanner, controller.getNode (id));
                for (int i = 0; i < funcs.size (); ++i)
                {
                    if ( i < controller.getNode (id).getFunctions ().size ())
                    {
                        controller.getNode (id).setFunction (i, funcs.get (i));
                    }
                    else
                    {
                        controller.getNode (id).addFunction (funcs.get (i));
                    }
                }
                functionScanner.close ();

                String miscs = lineScanner.next ();
                Scanner miscScanner = new Scanner (miscs);
                ArrayList<String> mis = parseStrings (miscScanner, controller.getNode (id));
                for (int i = 0; i < mis.size (); ++i)
                {
                    if ( i < controller.getNode (id).getMiscs ().size ())
                    {
                        controller.getNode (id).setMisc (i, mis.get (i));
                    }
                    else
                    {
                        controller.getNode (id).addMisc (mis.get (i));
                    }
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

                        String edgeName = "";
                        VNode vn1 = view.getVNode (id);
                        VNode vn2 = view.getVNode (endNodeId);

                        AnchorMgr a = new AnchorMgr (vn1);

                        Point2D anchor = a.getNearAnchor (new Point2D (vn1.getX (), vn1.getY ()));

                        // this math creates a point at the local space of the
                        // srcPane
                        double sumX = anchor.getX () + vn1.getBoundsInParent ().getMinX () + 50;
                        double sumY = anchor.getY () + vn1.getBoundsInParent ().getMinY () + 50;
                        Point2D pt1 = new Point2D (sumX, sumY);

                        AnchorMgr b = new AnchorMgr (vn2);

                        b.setPane (vn2);
                        Point2D pt2 = b.getNearAnchor (new Point2D (vn2.getX (), vn2.getY ()));

                        // id, name, startNode, endNode, startRgn, endRgn,
                        // currentEdgeStart, releasePoint
                        Object[] args2 = { edgeId, edgeName, controller.getNode (id), controller.getNode (endNodeId),
                                vn1, vn2, pt1, pt2 };
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
                    Integer edgeId = startEdgeScanner.nextInt ();
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
                        String edgeName = "";
                        VNode vn1 = view.getVNode (startNodeId);
                        VNode vn2 = view.getVNode (id);

                        AnchorMgr a = new AnchorMgr (vn1);

                        Point2D anchor = a.getNearAnchor (new Point2D (vn1.getX (), vn1.getY ()));

                        // this math creates a point at the local space of the
                        // srcPane
                        double sumX = anchor.getX () + vn1.getBoundsInParent ().getMinX () + 50;
                        double sumY = anchor.getY () + vn1.getBoundsInParent ().getMinY () + 50;
                        Point2D pt1 = new Point2D (sumX, sumY);

                        AnchorMgr b = new AnchorMgr (vn2);

                        b.setPane (vn2);
                        Point2D pt2 = b.getNearAnchor (new Point2D (vn2.getX (), vn2.getY ()));
                        // id, name, startNode, endNode, startRgn, endRgn,
                        // currentEdgeStart, releasePoint
                        Object[] args2 = { edgeId, edgeName, controller.getNode (startNodeId), controller.getNode (id),
                                vn1, vn2, pt1, pt2 };
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
            if (current.length () < 2)
            {

            } else if (current.charAt (current.length () - 1) == '\\')
            {
                current = current.substring (0, current.length () - 1) + "\",";
            } else
            {
                current = current.substring (1);
                strings.add (current.replaceAll ("\\\"", "\""));
                current = "";
            }
            current += scan.next ();
        }
        if (current.length () > 1)
        {
            current = current.substring (1, current.length () - 1);
            strings.add (current.replaceAll ("\\\"", "\""));
        }
        return strings;
    }
}