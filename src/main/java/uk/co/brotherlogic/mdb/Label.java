package uk.co.brotherlogic.mdb;

/**
 * Class to represent a label
 * 
 * @author Simon Tucker
 */

public class Label implements Comparable<Label>, Builder<Label>
{
	private final String labelName;

	private int labNo;

	public Label()
	{
		labelName = "";
	}

	public Label(String name, int number)
	{
		labelName = name;
		labNo = number;
	}

	@Override
	public Label build(String name)
	{
		return new Label(name, -1);
	}

	@Override
	public int compareTo(Label o)
	{
		return -labelName.toLowerCase().compareTo(o.labelName.toLowerCase());
	}

	public String getName()
	{
		return labelName;
	}

	public int getNumber()
	{
		return labNo;
	}

	public void setNumber(int num)
	{
		this.labNo = num;
	}

	public String toString()
	{
		return labelName;
	}
}