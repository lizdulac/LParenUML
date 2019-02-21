package gui;

import javafx.stage.Stage;


public class GUIcontroller
{
	private static GUIcontroller sharedController;
	private GUIview theView;

	private GUIcontroller(Stage stage)
	{
		theView = new GUIview(this, stage);
		stage.show();
	}

	public static GUIcontroller getSharedController(Stage stage)
	{
		if(sharedController == null)
		{
			sharedController = new GUIcontroller(stage);
		}
		return sharedController;
	}
}
