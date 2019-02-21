package gui;
import model.SingleDie;
import model.PairOfDice;
import javafx.stage.Stage;

import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.scene.paint.Color;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Pane;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.control.Button;
import javafx.scene.layout.TilePane;
import javafx.scene.text.FontWeight;
import javafx.scene.control.Separator;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Background;

import java.util.*;
import javafx.geometry.Pos;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.ObservableNumberValue;


public class GUIview
{
	// controller is initialized from the sharedController in GUIcontroller  
	private GUIcontroller controller;

	// graphical interface components get attached to the scene
	// the scene must be attached to a stage to be visible 
	private Scene scene;

	// tabletop contains the graphical dice labels
	private HBox tabletop;

	// width & height of graphical window
	private double startingWidth = 800.0;
	private double startingHeight = 600.0;
	
	// this background color will be applied to the scene
	private Color background = Color.SILVER;

	// set the font-family, font-weight, and font size for dice & buttons
	private Font diceFont = Font.font("sans-serif", 140);
	private Font buttonsFont = Font.font("sans-serif", FontWeight.BOLD, 25);

	// this view constructor is called inside the controller constructor
	public GUIview(GUIcontroller aController, Stage aStage)
	{
		controller = aController;

		// configureUI() returns a BorderPane that gets attached to scene
		scene = new Scene(configureUI(), startingWidth, startingHeight);
		scene.setFill(background);
		
		// attach scene to the stage (graphical window)
		aStage.setScene(scene);
	}

	public PairOfDice getDice()
	{
		return controller.getDice();
	}

	// return any kind of fully configured javaFX layout Pane
	private Region configureUI()
	{
		// one of several options for javaFX layout panes
		// contains the buttonPanel and the tabletop
		BorderPane borderPane;

		// buttonPanel holds the three buttons
		Pane buttonPanel = createButtonPanel();
		
		// adds components to tabletop, which is a class variable
		setupTabletop();

		// three Separators used to control the BorderPane layout 
		Separator topSpacer = new Separator();
		Separator rightSpacer = new Separator();
		Separator bottomSpacer = new Separator();		

		// make Separators invisible, they're only needed to fill space
		topSpacer.setVisible(false);
		rightSpacer.setVisible(false);
		bottomSpacer.setVisible(false);
		
		// adjust interface layout by setting the preferred widths
		buttonPanel.setPrefWidth(290);
		rightSpacer.setPrefWidth(110);
		topSpacer.setPrefHeight(100);
		bottomSpacer.setPrefHeight(100);

		// attach components to the BorderPane
		borderPane = new BorderPane(
			tabletop, topSpacer, rightSpacer, bottomSpacer, buttonPanel);
		borderPane.setBackground(Background.EMPTY);

		return borderPane;
	}

	// return any kind of Pane that contains buttons 
	private Pane createButtonPanel()
	{
		// VBox arranges elements vertically in a single column
		VBox buttonPanel;
		double panelSpacing = 25.0;
		Pos panelAlignment = Pos.CENTER;

		// create Button objects
		Button roll1 = new Button("Roll\n #1");
		Button roll2 = new Button("Roll\n #2");
		Button rollPair = new Button("Roll\nPair");

		// set the buttons' font
		roll1.setFont(buttonsFont);
		roll2.setFont(buttonsFont);
		rollPair.setFont(buttonsFont);

		// set the userData for each button to contain a numeric ID
		roll1.setUserData(1);
		roll2.setUserData(2);
		rollPair.setUserData(3);

		// set EventHandlers for the buttons  
		roll1.setOnAction(controller.buttonClickedHandler);
		roll2.setOnAction(controller.buttonClickedHandler);
		rollPair.setOnAction(controller.buttonClickedHandler);

		// attach components to the buttonPanel
		buttonPanel = new VBox(panelSpacing, roll1, roll2, rollPair);
		buttonPanel.setAlignment(panelAlignment);
		
		return buttonPanel;
	}

	// adds components to tabletop, which is a class variable
	private void setupTabletop()
	{
		// VBox arranges elements horizontally in a single row
		tabletop = new HBox();

		// get the model from the controller
		PairOfDice theDice = getDice();

		double tableSpacing = 15.0;
		Pos tableAlignment = Pos.CENTER;

		// register a change listener on each of the dice 
		theDice.getDie1().valueProperty().addListener(dieValueListener);
		theDice.getDie2().valueProperty().addListener(dieValueListener);

		// dice represented graphically using Label objects with Unicode symbols
		Label die1 = createLabel(theDice.getDie1());
		Label die2 = createLabel(theDice.getDie2());

		// background-radius controls roundness of the rectangle's corners
		tabletop.setStyle(
			"-fx-background-color: white; -fx-background-radius: 50;");
		tabletop.setAlignment(tableAlignment);
	}

	// return a Label object with the correct die symbol
	public Label createLabel(SingleDie theDie)
	{
		int dieValue = theDie.getValue();

		// create label & call intToUnicode() to look up Unicode symbol 
		Label label = new Label(intToUnicode(dieValue));
		label.setFont(diceFont);

		// user clicked rollPair button
		if(controller.lastClicked == 3)
		{
			refreshDice(label);
			controller.lastClicked = 2;
		}
		// user rolled a single die individually
		else
		{
			refreshDice(label);
		}

		return label;
	}

	/**
	 *	redraw/repaint the dice graphics
	 *
	 *	basic steps:
	 *		- determine which die(s) need to be redrawn
	 *		- remove old die label(s) from tabletop
	 *		- attach new die label(s) to tabletop 
	 *
	 **/
	public void refreshDice(Label dieLabel)
	{
		if(tabletop.getChildren().isEmpty() && controller.lastClicked == 0)
		{
			tabletop.getChildren().add(dieLabel);
		}
		else if(controller.lastClicked == 0)
		{
			tabletop.getChildren().add(dieLabel);
		}

		if(controller.lastClicked == 1)
		{
			tabletop.getChildren().remove(0);
			tabletop.getChildren().add(0, dieLabel);
		}
		else if(controller.lastClicked == 2)
		{
			tabletop.getChildren().remove(1);
			tabletop.getChildren().add(1, dieLabel);
		}
		else if(controller.lastClicked == 3)
		{
			tabletop.getChildren().remove(0);
			tabletop.getChildren().add(0, dieLabel);
		}
	}

	// look up Unicode dice graphic based on face value
	private String intToUnicode(int dieValue)
	{
		String unicodeDie;

		switch(dieValue)
		{
			case 1:
				unicodeDie = "\u2680";
				break;
			case 2:
				unicodeDie = "\u2681";
				break;
			case 3:
				unicodeDie = "\u2682";
				break;
			case 4:
				unicodeDie = "\u2683";
				break;
			case 5:
				unicodeDie = "\u2684";
				break;
			case 6:
				unicodeDie = "\u2685";
				break;		
			default:
				unicodeDie = "\u26A0";													
		}
		return unicodeDie;
	}


//***************************** CHANGE LISTENERS *****************************

	/**
	 *	a ChangeListener is similar to an event handler
	 *  
	 *	when the value of a die changes, call createLabel() to 
	 *	generate a new die graphic label & refreshDice()	
	 *
	 **/  
	public ChangeListener<Number> dieValueListener = new ChangeListener<Number>()
	{
		@Override 
		public void changed(ObservableValue<? extends Number> observable,
							Number oldValue,
            				Number newValue )
		{
			SimpleIntegerProperty sourceProperty = (SimpleIntegerProperty) observable;
			
			// get the SingleDie object whose value has changed
			SingleDie source = (SingleDie) sourceProperty.getBean();

			// generate new label
			createLabel(source);
		}
	};
}
