package uk.co.brotherlogic.mdb;

/**
 * Instantion of abstract groop to be used in the database
 * @author Simon Tucker
 */

import java.util.Collection;
import java.util.Iterator;

public class Groop extends AbstractGroop
{
	// Use a single lineup instead of a collection
	LineUp thisLineUp;

	// Boolean flag which indicates whether the group name contains a comma
	boolean commaInGroopName;

	public Groop()
	{
		groopName = "";
		thisLineUp = new LineUp();
	}

	// Stub for now
	public Groop(String name, int lineUpNumber, Collection artists)
	{
		// Set name
		groopName = name;

		// Create and set the line up
		thisLineUp = new LineUp(lineUpNumber, artists);

		commaInGroopName = false;
	}

	public void addArtist(Artist artistName)
	{
		thisLineUp.addArtist(artistName);
	}

	public LineUp getLineUp()
	{
		return thisLineUp;
	}

	public String getTidyName()
	{
		String name = groopName;
		if (groopName.indexOf(",") >= 0)
		{
			// Get the location of the first comman
			int loc = groopName.indexOf(",");

			// Flip the string around the comman
			name = groopName.substring(loc + 2) + " "
					+ groopName.substring(0, loc);

		}

		return name;

	}

	public String printGroop()
	{
		String ret = "#G#~" + groopName;

		Iterator mIt = thisLineUp.getArtists().iterator();
		while (mIt.hasNext())
			ret += "~" + mIt.next();
		ret += "\n";

		return ret;
	}

	public void setCommaInGroopName(boolean comma)
	{
		commaInGroopName = comma;
	}

	public void setLineUpNumber(int lineUpNumber)
	{
		thisLineUp.setLineUpNumber(lineUpNumber);
	}

	public String toString()
	{
		return groopName; // + " :~ " + thisLineUp;
	}

}
