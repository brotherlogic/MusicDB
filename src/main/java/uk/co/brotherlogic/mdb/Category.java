package uk.co.brotherlogic.mdb;

/**
 * Class to represent a category
 * 
 * @author Simon Tucker
 */

public class Category implements Comparable<Category>
{
	private String catName;
	private int catNumber;
	private int mp3Number;

	public Category()
	{
		catName = "";
		catNumber = 0;
		mp3Number = 0;
	}

	public Category(String name, int number, int mp3)
	{
		catName = name;
		catNumber = number;
		mp3Number = mp3;
	}

	public int compareTo(Category o)
	{
		return this.toString().compareTo(o.toString());
	}

	public int getMP3Number()
	{
		return mp3Number;
	}

	public String getName()
	{
		return catName;
	}

	public int getNumber()
	{
		return catNumber;
	}

	public String print()
	{
		return catName + " [" + catNumber + "]";
	}

	public void setMP3Number(int number)
	{
		mp3Number = number;
	}

	public void setName(String name)
	{
		catName = name;
	}

	public void setNumber(int number)
	{
		catNumber = number;
	}

	public String toString()
	{
		return catName;
	}
}
