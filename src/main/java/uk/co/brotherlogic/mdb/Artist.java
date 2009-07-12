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

	public Artist(String sortName, int id)
	{
		this.sortName = sortName;
		this.id = id;

		showName = resolve(sortName);
	}

	public Artist(String sortName, int number, String showName)
	{
		this.sortName = sortName;
		id = number;
		this.showName = showName;
	}

	@Override
	public Artist build(String name)
	{
		return new Artist(name, -1);
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

	private String resolve(final String name)
	{
		int commaPos = name.indexOf(',');
		if (commaPos > 0)
			return name.substring(commaPos).trim() + " "
					+ name.substring(0, commaPos);
		else
			return name;
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