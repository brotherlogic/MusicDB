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

	public boolean equals(Object o)
	{
		if (o instanceof Label)
			return this.compareTo((Label) o) == 0;
		else
			return false;
	}

	public String getName()
	{
		return labelName;
	}

	public int getNumber()
	{
		return labNo;
	}

	public int hashCode()
	{
		return labelName.hashCode();
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