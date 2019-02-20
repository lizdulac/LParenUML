package gui;
import model.PairOfDice;

import javafx.scene.Node;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;


public class GUIcontroller
{
    // both the controller & view are owned by GUIcontroller
    private static GUIcontroller sharedController;
    private GUIview theView;

    // the model
    private PairOfDice theDice;

    // stageTitle is displayed in the graphical window's titlebar
    private String stageTitle = "Dice";

    // ID value of the most recently clicked button
    public int lastClicked = 0;


    // constructor is private to enforce a Singleton pattern
    private GUIcontroller(Stage stage)
    {
        // initialize the model & view
	theDice = new PairOfDice();
	theView = new GUIview(this, stage);

        // make the graphical window visible
	stage.setTitle(stageTitle);
	stage.show();
    }

    // return the sharedController which is a Singleton
    public static GUIcontroller getSharedController(Stage stage)
    {
	if(sharedController == null)
	{
	    // initialize the controller if one does not exist
	    sharedController = new GUIcontroller(stage);
	}
	return sharedController;
    }

    public PairOfDice getDice()
    {
        return theDice;
    }


//***************************** EVENT HANDLERS *****************************


    // all button click events are handled by this single handler
    public EventHandler<ActionEvent> buttonClickedHandler = new EventHandler<ActionEvent>()
    {
        @Override 
        public void handle(ActionEvent e)
        {
	    // determine the source button that generated the event
            Node source = (Node) e.getSource();

            // userData contains the ID of each button
	    int userData = (int) source.getUserData();


            // click originated from roll1 button
	    if(userData == 1)
	    {
		lastClicked = userData;
		theDice.getDie1().roll();
	    }

            // click originated from roll2 button
            else if(userData == 2)
	    {
	        lastClicked = userData;
        	theDice.getDie2().roll();
            }

            // click originated from rollPair
	    else if(userData == 3)
	    {
	        lastClicked = userData;
        	theDice.rollPair();
            }
        }
    };
}
