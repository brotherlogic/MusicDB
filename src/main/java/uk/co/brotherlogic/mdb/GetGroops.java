package uk.co.brotherlogic.mdb;

/**
 * Class to deal with getting groops
 * @author Simon Tucker
 */

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;

public class GetGroops
{
	private static String delString = "DELETE_ME";
	private static Map<Integer, FullGroop> groopMap = new TreeMap<Integer, FullGroop>();

	static Persistent p;

	// Maps groopnumber to Groop
	Map groops;

	// Temporary store of groop name -> lineup
	Map tempStore;

	private static GetGroops singleton = null;

	private GetGroops() throws SQLException
	{
		// Set the required parameters
		p = Persistent.create();
		tempStore = new TreeMap();
		groops = new TreeMap();
	}

	public void addGroop(FullGroop in) throws SQLException
	{
		// Get the groop number
		int groopNumber = in.getNumber();
		if (groopNumber < 1 && groops.containsKey(in.getGroopName()))
			groopNumber = ((FullGroop) groops.get(in.getGroopName()))
					.getNumber();
		if (!(groopNumber > 0))
			if (tempStore.containsKey(in.getGroopName()))
				groopNumber = ((FullGroop) tempStore.get(in.getGroopName()))
						.getNumber();
			else
			{
				// Add the groop name and get the number
				p.getConnection().runUpdate(
						"INSERT INTO Groops (GroopName) VALUES (\'"
								+ p.cleanString(in.getGroopName()) + "\')");

				// Now get the group number
				Statement s = p.getConnection().getStatement();
				ResultSet rs = s
						.executeQuery("SELECT GroopNumber FROM Groops WHERE GroopName = \'"
								+ p.cleanString(in.getGroopName()) + "\'");
				rs.next();

				// Set the groop number
				groopNumber = rs.getInt(1);
				rs.close();
				s.close();
			}

		// Set the number in the groop
		in.setNumber(groopNumber);

		// Get the lineup number
		int lineUpNum = -1;
		if (in.getChosenLineup().getLineUpNumber() == -1)
			lineUpNum = getLineUpNum(in);
		else
			lineUpNum = in.getChosenLineup().getLineUpNumber();
		in.getChosenLineup().setLineUpNumber(lineUpNum);

	}

	public void cancel()
	{
		// Necessary for this to finish, so just leave in background
	}

	public void commitGroops()
	{
		Iterator kIt = tempStore.keySet().iterator();
		while (kIt.hasNext())
		{
			// Get the groop name
			String groopName = (String) kIt.next();
			FullGroop grp = (FullGroop) tempStore.get(groopName);

			// Get the full groop
			if (!groops.keySet().contains(groopName))
				groops.put(groopName, grp);
			else
				((FullGroop) groops.get(groopName))
						.addLineUps(((FullGroop) tempStore.get(groopName))
								.getLineUps());
		}
		tempStore.clear();
	}

	public void execute() throws SQLException
	{
		Statement ss = p.getConnection().getStatement();
		ResultSet rss = ss
				.executeQuery("Select Count(GroopNumber) FROM Groops");
		rss.next();
		rss.close();
		ss.close();

		// Initialise the groop store
		groops = new TreeMap();

		// Get the bare bones of the groops
		String sql = "SELECT GroopName, GroopNumber from Groops";
		PreparedStatement ps = p.getConnection().getPreparedStatement(sql);
		ps.execute();
		ResultSet rs = ps.getResultSet();
		while (rs.next())
		{
			String groopName = rs.getString(1);
			int groopNumber = rs.getInt(2);

			FullGroop fGroop = new FullGroop(groopName, groopNumber);
			groops.put(groopName, fGroop);
		}
	}

	public Collection getData()
	{
		return groops.values();
	}

	public FullGroop getGroop(int num) throws SQLException
	{
		// Get the groop name
		Statement s = p.getConnection().getStatement();
		ResultSet rs = s
				.executeQuery("SELECT GroopName FROM Groops WHERE GroopNumber = "
						+ num);

		FullGroop ret = new FullGroop("ERROR!", 1, new TreeSet());
		while (rs.next())
			ret = getGroop(rs.getString(1));

		rs.close();

		// Cache the groop
		groopMap.put(ret.getNumber(), ret);

		return ret;
	}

	public FullGroop getGroop(String groopName)
	{
		if (groops.containsKey(groopName))
			return (FullGroop) groops.get(groopName);
		else
			return null;
	}

	public Map getGroopMap()
	{
		if (groops.size() == 0)
			try
			{
				execute();
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		return groops;
	}

	public int getLineUpNum(FullGroop grp) throws SQLException
	{
		// Initialise the return value
		int ret = 0;

		// Get the line ups for this groop
		Collection lineups = new Vector();
		if (GetGroops.build().getGroop(grp.getGroopName()) != null)
			lineups = GetGroops.build().getGroop(grp.getGroopName())
					.getLineUps();

		// Work through each line up
		Iterator lIt = lineups.iterator();
		int maxLineUp = 0;
		while (lIt.hasNext())
		{
			// Compare the two artist sets
			LineUp tempLineUp = (LineUp) lIt.next();
			Collection arts = tempLineUp.getArtists();

			if (arts.containsAll(grp.getChosenLineup().getArtists())
					&& grp.getChosenLineup().getArtists().containsAll(arts))
				ret = tempLineUp.getLineUpNumber();
		}

		// Search the temporary store if we haven't found the value yet
		if (tempStore.containsKey(grp.getGroopName()) && ret < 1)
		{

			FullGroop tempGroop = (FullGroop) tempStore.get(grp.getGroopName());

			Iterator tLupIt = tempGroop.getLineUps().iterator();
			while (tLupIt.hasNext())
			{
				// Compare the two artist sets
				LineUp tempLineUp = (LineUp) tLupIt.next();
				Collection arts = tempLineUp.getArtists();

				if (arts.containsAll(grp.getChosenLineup().getArtists())
						&& grp.getChosenLineup().getArtists().containsAll(arts))
					ret = tempLineUp.getLineUpNumber();
			}
		}

		// If we haven't seen this line up before
		if (ret < 1)
		{
			// Add the line up

			// First register the new line up and retrieve the number
			p.getConnection().runUpdate(
					"INSERT INTO LineUp (GroopNumber) VALUES ("
							+ grp.getNumber() + ")");

			// Now select the lineup ID which has a line up containing zero
			// artists (i.e....)
			Statement s = p.getConnection().getStatement();
			ResultSet rs = s
					.executeQuery("SELECT DISTINCT LineUp.LineUpNumber FROM LineUp LEFT JOIN LineUpDetails ON LineUp.LineUpNumber = LineUpDetails.LineUpNumber WHERE (((LineUpDetails.LineUpNumber) Is Null))");

			// Grab the first entry
			rs.next();
			ret = rs.getInt(1);

			// Close the statements
			rs.close();
			s.close();

			// Now add the artists
			int[] artNums = GetArtists.create().addArtists(
					grp.getChosenLineup().getArtists());

			for (int artNum : artNums)
				p.getConnection().runUpdate(
						"INSERT INTO LineUpDetails (LineUpNumber,ArtistNumber) VALUES ("
								+ ret + "," + artNum + ")");

			// Construct the new full groop if required
			if (!tempStore.containsKey(grp.getGroopName()))
			{
				FullGroop newGrp = new FullGroop(grp.getGroopName(), grp
						.getNumber(), new Vector());
				tempStore.put(newGrp.getGroopName(), newGrp);
			}

			// Add the new lineup
			FullGroop tempGrp = (FullGroop) tempStore.get(grp.getGroopName());
			grp.getChosenLineup().setLineUpNumber(ret);
			tempGrp.addLineUp(new LineUp(ret, grp.getChosenLineup()
					.getArtists()));
		}

		return ret;

	}

	public FullGroop getSingleGroop(int num) throws SQLException
	{
		System.out.println("build_groop: " + num);

		if (groopMap.containsKey(num))
			return groopMap.get(num);

		// Get a statement and run the query
		String sql = "SELECT Groops.GroopNumber, GroopName, LineUp.LineUpNumber, ArtistNumber FROM Groops,LineUp,LineUpDetails WHERE Groops.groopnumber = ? AND Groops.GroopNumber = LineUp.GroopNumber AND LineUp.LineUpNumber = LineUpDetails.LineUpNumber ORDER BY GroopName, LineUp.LineUpNumber ASC";
		PreparedStatement ps = p.getConnection().getPreparedStatement(sql);
		ps.setInt(1, num);
		ps.execute();
		ResultSet rs = ps.getResultSet();

		FullGroop currGroop = null;
		LineUp currLineUp = null;
		while (rs.next())
		{
			// Read the info
			int groopNumber = rs.getInt(1);
			String groopName = rs.getString(2);
			int lineUpNumber = rs.getInt(3);
			int artistNumber = rs.getInt(4);

			if (currGroop == null)
			{
				System.out.println("Creaing: " + groopName);
				// Construct the current groop and line up
				currGroop = new FullGroop(groopName, groopNumber, new TreeSet());
				currLineUp = new LineUp(lineUpNumber, new TreeSet());
				currLineUp.addArtist(GetArtists.create()
						.getArtist(artistNumber));
			}
			else if (!groopName.equals(currGroop.getGroopName()))
			{
				System.out.println("New Groop: " + groopName);
				// Add the groop and create a new one
				// Ensure that we add the last lineUp
				currGroop.addLineUp(currLineUp);

				// Construct the current groop and line up
				currGroop = new FullGroop(groopName, groopNumber, new TreeSet());
				currLineUp = new LineUp(lineUpNumber, new TreeSet());
				currLineUp.addArtist(GetArtists.create()
						.getArtist(artistNumber));

			}
			else if (currLineUp.getLineUpNumber() != lineUpNumber)
			{
				System.out.println("Adding lineup: " + lineUpNumber);
				// Add the line up
				currGroop.addLineUp(currLineUp);

				// Construct the new line up
				currLineUp = new LineUp(lineUpNumber, new TreeSet());
				currLineUp.addArtist(GetArtists.create()
						.getArtist(artistNumber));
			}
			else
			{
				System.out.println("Adding artist:" + artistNumber);
				currLineUp.addArtist(GetArtists.create()
						.getArtist(artistNumber));
			}
		}

		currGroop.addLineUp(currLineUp);

		groopMap.put(currGroop.getNumber(), currGroop);

		System.out.println("Built");
		return currGroop;
	}

	public String toString()
	{
		return "Groops";
	}

	public static GetGroops build() throws SQLException
	{
		if (singleton == null)
			singleton = new GetGroops();

		return singleton;
	}
}
