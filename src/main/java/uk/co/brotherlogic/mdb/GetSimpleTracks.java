package uk.co.brotherlogic.mdb;

/**
 * Class to deal with getting simple tracks
 * @author Simon Tucker
 */

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

public class GetSimpleTracks
{
	List<Track> tracks;
	Persistent p;

	public GetSimpleTracks(Persistent p)
	{
		this.p = p;
	}

	/**
	 * Sets non-length tracks to the average of the length tracks
	 */
	public void execute() throws SQLException
	{
		// Get a statement and run the query
		Statement s = p.getConnection().getStatement();
		ResultSet rs = s.executeQuery("SELECT * FROM SimpleTrackInfo");

		// Create two collections for each type of data
		List<Track> withLength = new Vector<Track>();
		List<Track> withoutLength = new Vector<Track>();

		// To calculate the average
		int avgVal = 0;

		// One to prevent divide by zero
		int avgCount = 1;

		// Fill the set
		while (rs.next())
		{
			String recTitle = rs.getString(1);
			int trackNumber = rs.getInt(2);
			String trackName = rs.getString(3);
			int lengthInSeconds = rs.getInt(4);
			int recordNumber = rs.getInt(5);

			// Store the track accordingly
			if (lengthInSeconds == 0)
				withoutLength.add(new SimpleTrack(recTitle, trackName,
						trackNumber, lengthInSeconds, recordNumber));
			else
			{
				withLength.add(new SimpleTrack(recTitle, trackName,
						trackNumber, lengthInSeconds, recordNumber));
				avgVal += lengthInSeconds;
				avgCount++;
			}
		}

		// Now set the withoutLengths to the average length
		int averageLength = Math.round(avgVal / avgCount);
		Iterator<Track> i = withoutLength.iterator();
		while (i.hasNext())
			((SimpleTrack) i.next()).setLengthInSeconds(averageLength);

		// Close the database objects
		rs.close();
		s.close();

		// Add the without length tracks
		withLength.addAll(withoutLength);
		tracks = withLength;
	}

	public List<Track> getTracks()
	{
		return tracks;
	}
}