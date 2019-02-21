import gui.GUIcontroller;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 *	This exists simply to launch the application:
 *	  - main() method calls javaFX launch() 
 *	  - launch() / start() initializes a javaFX application
 *	  - "primaryStage" (GUI window) passed to our GUIcontroller
 *	
 *	Current implementation specifically hard-codes
 *	our GUIcontroller inside start() method however
 *	future changes could allow for additional UI modes
 *	like a command-line interface or other type of view.
 *
 *	This is a separate class from the controller because
 * 	it is required to extend the javaFX Application class.
 *	More info:
 *				https://stackoverflow.com/a/33304137
 **/

public class Main extends Application
{
	public static void main(String[] args)
	{
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception
	{
		GUIcontroller.getSharedController(primaryStage);
	}
}
