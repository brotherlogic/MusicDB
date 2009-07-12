package uk.co.brotherlogic.mdb;

/**
 * Class to communicate with database
 * @author Simon Tucker
 */

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeSet;

import uk.co.brotherlogic.mdb.record.Record;

public class Persistent
{
	private static Persistent singleton;

	// Have we got graphics
	boolean headless = false;

	// Database connection
	Connect con;

	private Persistent(Connect con) throws SQLException
	{
		// Set the connection
		this.con = con;
	}

	public void alterAllTracks() throws SQLException
	{
		// Method to copy the track info over
		Statement s = con.getStatement();
		ResultSet rs = s.executeQuery("SELECT * FROM Tracks");

		while (rs.next())
		{
			// Get the relevant trackInfo
			String length = rs.getString(4);
			int trackRefNum = rs.getInt(5);
			int newLengthInSeconds = 0;

			// Resolve the length factor
			StringTokenizer tok = new StringTokenizer(length, ":");

			// Should be (Date...)hr:min:sec
			if (tok.countTokens() == 3)
			{
				// Miss the date
				String dateAndTime = tok.nextToken();
				int hours = Integer.parseInt(dateAndTime.substring(dateAndTime
						.length() - 2));
				int minutes = Integer.parseInt(tok.nextToken());
				int seconds = Integer.parseInt(tok.nextToken());
				newLengthInSeconds = hours * 3600 + minutes * 60 + seconds;
			}
			// Replace this track
			con.runUpdate("UPDATE Tracks SET LengthInSeconds = "
					+ newLengthInSeconds + " WHERE TrackRefNum = "
					+ trackRefNum);
		}

		// Commit these transactions
		con.commitTrans();
	}

	public String cleanString(String in)
	{
		StringBuffer sb = new StringBuffer(in);
		for (int i = 0; i < sb.length(); i++)
			if (sb.charAt(i) == '\'')
			{
				sb.insert(i, "'");
				i++;
			}

		return sb.toString();
	}

	public boolean determineOver(Record in)
	{
		// Get the artist
		String artist = in.getGroopString();

		// Get the title
		String title = in.getTitle();

		// Get a count of the records with this format
		return false;
	}

	public Collection getAliases(String in) throws SQLException
	{

		Collection reps = new TreeSet();
		Collection ret = new TreeSet();

		// Get left side of aliases stuff
		Statement s = con.getStatement();
		ResultSet rs = s
				.executeQuery("SELECT RightSide FROM FullArtistLinks WHERE LeftSide = \'"
						+ in + "\'");
		while (rs.next())
			reps.add(rs.getString(1));
		rs.close();
		s.close();

		// Now convert the numbers into groups and artists
		Iterator rIt = reps.iterator();
		while (rIt.hasNext())
		{
			String val = (String) rIt.next();
			if (val.startsWith("G"))
			{
				FullGroop grp = GetGroops.build().getGroop(
						Integer.parseInt(val.substring(1)));
				ret.add(grp);
			}
			else
			{
				Artist art = GetArtists.create().getArtist(
						Integer.parseInt(val.substring(1)));
				ret.add(art);
			}
		}

		return ret;
	}

	public Collection getAllRecordNumbers() throws SQLException
	{
		Collection retSet = new TreeSet();

		// Get all the record numbers and get the corresponding records
		Statement s = con.getStatement();
		ResultSet rs = s.executeQuery("SELECT RecordNumber FROM Records");

		while (rs.next())
			retSet.add(new Integer(rs.getInt(1)));

		rs.close();
		s.close();

		return retSet;

	}

	public Connect getConnection()
	{
		return con;
	}

	public int[] getHipHopNumbers() throws SQLException
	{
		return getNumbers("BestHipHop");
	}

	public SortedSet getNewNumbers(Calendar dat) throws SQLException
	{
		// Initialise the set
		TreeSet retSet = new TreeSet();
		DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, Locale.US);

		Statement s = con.getStatement();
		ResultSet rs = s
				.executeQuery("SELECT * FROM GetRecentRecords WHERE BoughtDate > #"
						+ df.format(dat.getTime()) + "#");

		while (rs.next())
		{
			int temp = rs.getInt(1);
			retSet.add(new Integer(temp));
		}

		rs.close();
		s.close();

		return retSet;

	}

	public int[] getNumbers(String in) throws SQLException
	{
		Statement s = con.getStatement();
		ResultSet rs = s.executeQuery("SELECT * FROM " + in);

		int[] ret = new int[30];
		for (int i = 0; i < 30; i++)
		{
			// Move the result set cursor on
			rs.next();

			// Set the number
			ret[i] = rs.getInt(1);
		}

		rs.close();
		s.close();

		return ret;
	}

	public int[] getOtherNumbers() throws SQLException
	{
		return getNumbers("BestOther");
	}

	public int[] getRecentNumbers() throws SQLException
	{
		return getNumbers("OrderedRecentRecords");
	}

	public int getSize(String countString) throws SQLException
	{
		// Create the SQL handshake
		Statement s = con.getStatement();
		ResultSet rs = s.executeQuery("SELECT Count(*) FROM " + countString);

		// Collect the data
		rs.next();
		int ret = rs.getInt(1);

		// Release the objects
		rs.close();
		s.close();

		return ret;
	}

	public void setAliases(String elem, Collection alias) throws SQLException
	{
		// First delete all relevant alias
		String delKey = elem;
		con.runDelete("DELETE * FROM  Aliases WHERE LeftSide = '" + delKey
				+ "' OR RightSide = '" + delKey + "'");

		// And add the new ones
		Iterator aIt = alias.iterator();
		while (aIt.hasNext())
		{
			String tempElem = (String) aIt.next();
			con.runUpdate("INSERT INTO Aliases VALUES ('" + delKey + "','"
					+ tempElem + "')");
		}
	}

	public void setExceptions(Collection exceptions) throws SQLException
	{
		// Remove the current exceptions
		con.runDelete("DELETE FROM exceptions");

		// Iterate and add through exceptions
		Iterator exIt = exceptions.iterator();
		while (exIt.hasNext())
			// Add the current exception
			con.runUpdate("INSERT INTO exceptions VALUES ('" + exIt.next()
					+ "')");

		// Now commit the transactions
		con.commitTrans();
	}

	public void updateRecordsForAuthor() throws SQLException
	{
		// First get all the record numbers
	}

	public static Persistent create() throws SQLException
	{
		if (singleton == null)
			singleton = new Persistent(Connect.getConnection());

		return singleton;
	}
}
