package uk.co.brotherlogic.mdb;

/**
 * Application level artist object
 * 
 * @author Simon Tucker
 */

public class Artist implements Comparable<Artist>, Builder<Artist>
{
	int id;

	String sortName;
	String showName;

	public Artist()
	{
		// Do Nothing
	}

	public Artist(String sortName, int number, String showName)
	{
		this.sortName = sortName;
		id = number;
		this.showName = showName;
	}

	public Artist(String sortName, String showName, int id)
	{
		this.sortName = sortName;
		this.id = id;
		this.showName = showName;
	}

	@Override
	public Artist build(String name)
	{
		return new Artist(name, Utils.flipString(name), -1);
	}

	@Override
	public int compareTo(Artist o)
	{
		return -sortName.toLowerCase().compareTo(o.sortName.toLowerCase());
	}

	public String displayInList()
	{
		return sortName;
	}

	public boolean equals(Object o)
	{
		if (o instanceof Artist)
			return this.compareTo((Artist) o) == 0;
		else
			return false;
	}

	public int getId()
	{
		return id;
	}

	public String getShowName()
	{
		return showName;
	}

	public String getSortName()
	{
		return sortName;
	}

	public int hashCode()
	{
		return sortName.hashCode();
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public String toString()
	{
		return sortName;
	}
}