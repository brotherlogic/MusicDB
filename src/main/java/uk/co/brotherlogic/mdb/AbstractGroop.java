package uk.co.brotherlogic.mdb;

/**
 * Class to represent an abstract groop
 * @author Simon Tucker
 */

import java.util.Collection;

public abstract class AbstractGroop
{
	// Groop properties
	String groopName;
	int groopNumber;
	Collection<LineUp> lineUps;

	public String getGroopName()
	{
		return groopName;
	}

	public Collection<LineUp> getLineUps()
	{
		return lineUps;
	}

	// Get methods
	public String getName()
	{
		return groopName;
	}

	public int getNumber()
	{
		return groopNumber;
	}

	public String getSimpRep()
	{
		return "G" + groopNumber;
	}

	public void setLineUps(Collection<LineUp> lineUpsIn)
	{
		// Clear and add lineUpsIn
		lineUps.clear();
		lineUps.addAll(lineUpsIn);
	}

	// Set methods
	public void setName(String groopIn)
	{
		groopName = groopIn;
	}

	public void setNumber(int in)
	{
		groopNumber = in;
	}
}