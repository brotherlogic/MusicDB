package uk.co.brotherlogic.mdb;

/**
 * Class to represent an abstract track
 * @author Simon Tucker
 */

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

public class Track implements Comparable<Track>
{
	// Variables
	int trackNumber;
	String title;
	int lengthInSeconds;
	Collection<LineUp> groops;
	Collection<Artist> personnel;
	int refNumber = -20;

	public Track()
	{
		groops = new LinkedList<LineUp>();
		personnel = new LinkedList<Artist>();
		title = "";
		lengthInSeconds = -1;
		trackNumber = -1;
	}

	public Track(String titleIn, int lengthIn, Collection<LineUp> groopsIn,
			Collection<Artist> personnelIn, int trackNumberIn)
	{
		title = titleIn;
		lengthInSeconds = lengthIn;
		groops = new LinkedList<LineUp>();
		groops.addAll(groopsIn);
		personnel = new LinkedList<Artist>();
		personnel.addAll(personnelIn);
		trackNumber = trackNumberIn;
	}

	public void addPersonnel(Collection<Artist> personnelIn)
	{
		personnel.addAll(personnelIn);
	}

	public int compareTo(Track in)
	{
		int otherNum = in.getTrackNumber();
		return trackNumber - otherNum;
	}

	public Collection<LineUp> getGroops()
	{
		return groops;
	}

	public int getLengthInSeconds()
	{
		return lengthInSeconds;
	}

	public Collection<Artist> getPersonnel()
	{
		return personnel;
	}

	// Get methods
	public String getTitle()
	{
		return title;
	}

	public int getTrackNumber()
	{
		return trackNumber;
	}

	public int getTrackRefNumber()
	{
		return refNumber;
	}

	public void setGroops(Collection<LineUp> groopsIn)
	{
		groops.clear();
		groops.addAll(groopsIn);
	}

	public void setLengthInSeconds(int secondsIn)
	{
		lengthInSeconds = secondsIn;
	}

	public void setPersonnel(Collection<Artist> personnelIn)
	{
		personnel.clear();
		personnel.addAll(personnelIn);
	}

	// Set methods
	public void setTitle(String titleIn)
	{
		title = titleIn;
	}

	public void setTrackNumber(int trackNumberIn)
	{
		trackNumber = trackNumberIn;
	}

	public void setTrackRefNumber(int in)
	{
		refNumber = in;
	}

	public String toString()
	{
		String ret = "";

		// Do the static stuff
		ret += "#T#" + trackNumber + "~" + title + "~" + lengthInSeconds;

		// Do the personnel
		Iterator<Artist> pIt = personnel.iterator();
		while (pIt.hasNext())
			ret += "~" + pIt.next();
		ret += "\n";

		// Do the groups
		Iterator<LineUp> gIt = groops.iterator();
		while (gIt.hasNext())
			ret += (gIt.next());

		return ret;

	}
}
