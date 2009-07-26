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

	public void setLineUpNumber(int numberIn)
	{
		lineUpNumber = numberIn;
	}

	public String toString()
	{
		return "" + lineUpNumber + artists;
	}
}
