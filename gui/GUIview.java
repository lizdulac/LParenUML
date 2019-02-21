package gui;

import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.control.Separator;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Background;


public class GUIview
{
	private GUIcontroller controller;
	private Scene scene;

	private double windowW = 800.0;
	private double windowH = 600.0;

	public GUIview(GUIcontroller aController, Stage aStage)
	{
		controller = aController;
		scene = new Scene(configureUI(), windowW, windowH);
		aStage.setScene(scene);
	}

	private Region configureUI()
	{
		BorderPane borderPane = new BorderPane();

		Separator topSpacer = new Separator();
		Pane buttonPanel = new Pane();
		Pane canvas = new Pane();
		Separator rightSpacer = new Separator();
		Separator bottomSpacer = new Separator();

		topSpacer.setStyle("-fx-background-color: skyblue;");
		buttonPanel.setStyle("-fx-background-color: mediumseagreen;");
		canvas.setStyle("-fx-background-color: white;");
		rightSpacer.setStyle("-fx-background-color: sandybrown");
		bottomSpacer.setStyle("-fx-background-color: tomato;");

/*
		topSpacer.setVisible(false);
		rightSpacer.setVisible(false);
		bottomSpacer.setVisible(false);
*/
		topSpacer.setPrefHeight(100);
		buttonPanel.setPrefWidth(290);
		rightSpacer.setPrefWidth(110);
		rightSpacer.setPrefHeight(400);
		bottomSpacer.setPrefHeight(100);

		borderPane.setTop(topSpacer);
		borderPane.setLeft(buttonPanel);
		borderPane.setCenter(canvas);
		borderPane.setRight(rightSpacer);
		borderPane.setBottom(bottomSpacer);

		borderPane.setBackground(Background.EMPTY);

		return borderPane;
	}
}
