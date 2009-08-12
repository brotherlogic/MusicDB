package uk.co.brotherlogic.mdb;

/**
 * Class to represent a line up
 * @author Simon Tucker
 */

import java.util.Collection;
import java.util.Vector;

import uk.co.brotherlogic.mdb.groop.Groop;

public class LineUp implements Comparable<LineUp>
{
	// Lineup properties
	int lineUpNumber;
	Collection<Artist> artists;
	Groop grp;

	public LineUp()
	{
		lineUpNumber = -1;
		artists = new Vector<Artist>();
	}

	public LineUp(int number, Collection<Artist> arts, Groop groop)
	{
		grp = groop;

		// Set the number
		lineUpNumber = number;

		// Create the artists collection and add the arts
		artists = new Vector<Artist>();
		artists.addAll(arts);
	}

	public void addArtist(Artist art)
	{
		artists.add(art);
	}

	public int compareTo(LineUp o)
	{
		return this.toString().compareTo(o.toString());
	}

	@Override
	public boolean equals(Object o)
	{
		if (o instanceof LineUp)
			return this.compareTo((LineUp) o) == 0;
		else
			return false;
	}

	public Collection<Artist> getArtists()
	{
		return artists;
	}

	public Groop getGroop()
	{
		return grp;
	}

	public int getLineUpNumber()
	{
		return lineUpNumber;
	}

	@Override
	public int hashCode()
	{
		return grp.getShowName().hashCode() + lineUpNumber;
	}

	public void setLineUpNumber(int numberIn)
	{
		lineUpNumber = numberIn;
	}

	@Override
	public String toString()
	{
		return "" + lineUpNumber + artists;
	}
}
