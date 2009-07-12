package uk.co.brotherlogic.mdb;
/**
 * Class to represent an abstract track
 * @author Simon Tucker
 */

import java.util.*;

public class Track implements Comparable
{
	//Variables
	int trackNumber;
	String title;
	int lengthInSeconds;
	Collection groops;
	Collection personnel;
	int refNumber = -20;

	public Track()
	{
		groops = new LinkedList();
		personnel = new LinkedList();
		title = "";
		lengthInSeconds = -1;
		trackNumber = -1;
	}

	public String toString()
	{
	  String ret = "";

	// Do the static stuff
	ret += "#T#" + trackNumber + "~" + title + "~" + lengthInSeconds;

	//Do the personnel
	Iterator pIt = personnel.iterator();
	while(pIt.hasNext())
	{
		ret += "~" + pIt.next();
	}
	ret += "\n";

	//Do the groups
	Iterator gIt = groops.iterator();
	while(gIt.hasNext())
		ret += ((Groop)gIt.next()).printGroop();

	return ret;

	}

	public int compareTo(Object in)
	{
		int otherNum = ((Track)in).getTrackNumber();
		return trackNumber-otherNum;
	}

	public Track(String titleIn, int lengthIn, Collection groopsIn, Collection personnelIn,int trackNumberIn)
	{
		title = titleIn;
		lengthInSeconds = lengthIn;
		groops = new LinkedList();
		groops.addAll(groopsIn);
		personnel = new LinkedList();
		personnel.addAll(personnelIn);
		trackNumber = trackNumberIn;
	}

	//Set methods
	public void setTitle(String titleIn)
	{
		title = titleIn;
	}

	public void setLengthInSeconds(int secondsIn)
	{
		lengthInSeconds = secondsIn;
	}

	public void setGroops(Collection groopsIn)
	{
		groops.clear();
		groops.addAll(groopsIn);
	}

	public void setPersonnel(Collection personnelIn)
	{
		personnel.clear();
		personnel.addAll(personnelIn);
	}

	public void addPersonnel(Collection personnelIn)
	{
	  personnel.addAll(personnelIn);
	}

  public int getTrackRefNumber()
  {
	return refNumber;
}

  public void setTrackRefNumber(int in)
  {
	refNumber = in;
}

	public void setTrackNumber(int trackNumberIn)
	{
		trackNumber = trackNumberIn;
	}

	//Get methods
	public String getTitle()
	{
		return title;
	}

	public int getLengthInSeconds()
	{
		return lengthInSeconds;
	}

	public Collection getGroops()
	{
		return groops;
	}

	public Collection getPersonnel()
	{
		return personnel;
	}

	public int getTrackNumber()
	{
		return trackNumber;
	}
}
