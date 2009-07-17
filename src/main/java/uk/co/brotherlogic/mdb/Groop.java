package uk.co.brotherlogic.mdb;

/**
 * Class to represent a full groop with all the lineups
 * @author Simon Tucker
 */

import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

public class Groop extends AbstractGroop implements Comparable<Groop>,
		Builder<Groop>
{
	LineUp chosenLineup;

	public Groop()
	{

	}

	public Groop(String name, int num)
	{
		// Set the variables
		groopName = name;
		groopNumber = num;
		this.lineUps = null;
	}

	public Groop(String name, int num, Collection lineUps)
	{
		// Set the variables
		groopName = name;
		groopNumber = num;
		this.lineUps = new Vector();
		this.lineUps.addAll(lineUps);
	}

	public Groop(String name, int num, Collection lineUps,
			LineUp chosenLineup)
	{
		this(name, num, lineUps);
		this.chosenLineup = chosenLineup;
		lineUps.add(chosenLineup);
	}

	public void addLineUp(LineUp in)
	{
		lineUps.add(in);
	}

	public void addLineUps(Collection lineUpsToAdd)
	{
		lineUps.addAll(lineUpsToAdd);
	}

	@Override
	public Groop build(String name)
	{
		return new Groop(name, -1);
	}

	@Override
	public int compareTo(Groop o)
	{
		return -groopName.toLowerCase().compareTo(o.groopName.toLowerCase());
	}

	private void fillLineUp()
	{
		try
		{
			this.lineUps = GetGroops.build().getSingleGroop(this.groopNumber)
					.getLineUps();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			System.exit(1);
		}
	}

	public LineUp getChosenLineup()
	{
		if (chosenLineup == null)
			if (lineUps.size() == 1)
				chosenLineup = lineUps.iterator().next();
		return chosenLineup;
	}

	public LineUp getLineUp(int in)
	{
		if (lineUps == null)
			fillLineUp();

		LineUp ret = null;
		// Move and iterator to the right point
		boolean found = false;
		Iterator it = lineUps.iterator();
		while (!found && it.hasNext())
		{
			LineUp temp = (LineUp) it.next();
			if (temp.getLineUpNumber() == in)
			{
				ret = temp;
				found = true;
			}
		}

		return ret;
	}

	@Override
	public Collection getLineUps()
	{
		if (lineUps == null)
			fillLineUp();
		return super.getLineUps();
	}

	public int getNoLineUps()
	{
		return lineUps.size();
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

	public void setChosenLineup(LineUp chosenLineup)
	{
		this.chosenLineup = chosenLineup;
	}

	public String toString()
	{
		// Simple for now
		String ret = "";
		ret += groopName;

		return ret;
	}

}
