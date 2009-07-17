package uk.co.brotherlogic.mdb;

/**
 * Class to represent a line up
 * @author Simon Tucker
 */

import java.util.Collection;
import java.util.Vector;

public class LineUp implements Comparable
{
	// Lineup properties
	int lineUpNumber;
	Collection artists;
	String groopName = "";

	public LineUp()
	{
		lineUpNumber = -1;
		artists = new Vector();
	}

	public LineUp(int number, Collection arts)
	{
		// Set the number
		lineUpNumber = number;

		// Create the artists collection and add the arts
		artists = new Vector();
		artists.addAll(arts);
	}

	public void addArtist(Artist art)
	{
		artists.add(art);
	}

	public int compareTo(Object o)
	{
		return this.toString().compareTo(o.toString());
	}

	public Collection<Artist> getArtists()
	{
		return artists;
	}

	public String getGroopName()
	{
		return groopName;
	}

	public int getLineUpNumber()
	{
		return lineUpNumber;
	}

	public void setLineUpNumber(int numberIn)
	{
		lineUpNumber = numberIn;
	}

	public String toString()
	{
		return "" + lineUpNumber + artists;
	}
}
