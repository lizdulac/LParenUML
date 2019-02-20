package model;


public class PairOfDice
{
	// by definition a pair of dice contains two individual dies
	private SingleDie die1;
	private SingleDie die2;

	// the default values for a pair of dice are: 3 & 6
	public PairOfDice()
	{
		die1 = new SingleDie(3);
		die2 = new SingleDie(6);
	}

	public SingleDie getDie1()
	{
		return die1;
	}

	public SingleDie getDie2()
	{
		return die2;
	}

	// roll both dice by calling the roll() method of each die
	public void rollPair()
	{
		die1.roll();
		die2.roll();
	}
}
