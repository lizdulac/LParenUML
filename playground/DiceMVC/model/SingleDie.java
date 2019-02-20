package model;

import javafx.beans.property.SimpleIntegerProperty;


public class SingleDie
{
	// NUM_SIDES:	the die is a cube with six sides
	private final int NUM_SIDES = 6;

	/**
	 *	value:	holds the number showing on the face of the die
	 *
	 *	Value is stored as a SimpleIntegerProperty so
	 * 	that the view can listen for changes and update
	 *	the graphical representation when the value changes
	 *
	 **/
	private final SimpleIntegerProperty value = new SimpleIntegerProperty(this, "value", 5);
	
	// the empty constructor creates a die with default value five
	public SingleDie()
	{
		setValue(5);
	}

	// create a die with a given initialValue  
	public SingleDie(int initialValue)
	{
		setValue(initialValue);
	}

	public int getValue()
	{
		return value.get();
	}

	private void setValue(int newValue)
	{
		value.set(newValue);
	}

	// die's value is changed to a random int 
	// possible random values are dependent on NUM_SIDES  
	public void roll()
	{
		setValue((int)(Math.random() * NUM_SIDES) + 1);
	}

	// this method returns a Property object not a numeric value
	public SimpleIntegerProperty valueProperty()
	{
		return value;
	}
}
