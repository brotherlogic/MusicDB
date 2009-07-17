package uk.co.brotherlogic.mdb;

/**
 * Class to deal with getting groops
 * @author Simon Tucker
 * NB: Updated for Postgres
 */

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

public class GetArtists
{
	// Map of name --> artist
	private final Map<String, Artist> artists;
	private final Persistent p;

	private final Map<String, Artist> tempStore;

	// Prepared Statements to use
	private final PreparedStatement insertQuery;
	private final PreparedStatement collectQuery;
	private final PreparedStatement collectQueryShowName;

	private boolean executed = false;
	private static GetArtists singleton;

	private GetArtists(Persistent pers) throws SQLException
	{
		// Set the required parameters
		p = pers;
		tempStore = new TreeMap<String, Artist>();

		// Initialise the Set
		artists = new TreeMap<String, Artist>();

		// Build the set
		insertQuery = p.getConnection().getPreparedStatement(
				"INSERT INTO Artists (sort_name, show_name) VALUES (?,?)");
		collectQuery = p.getConnection().getPreparedStatement(
				"SELECT artist_id,show_name FROM Artists WHERE sort_name = ?");
		collectQueryShowName = p.getConnection().getPreparedStatement(
				"SELECT artist_id,sort_name FROM Artists WHERE show_name = ?");
	}

	public int[] addArtists(Collection<Artist> art) throws SQLException
	{
		// Prepare the array
		int[] ret = new int[art.size()];

		// Iterate through the array
		int count = 0;
		for (Artist tempArt : art)
		{
			// See if the number is defined
			if (tempArt.getId() > 0)
				ret[count] = tempArt.getId();
			else if (artists.containsKey(tempArt.getSortName()))
			{
				ret[count] = (artists.get(tempArt.getSortName())).getId();
				(tempArt).setId(ret[count]);
			}
			else if (tempStore.containsKey(tempArt.getSortName()))
			{
				ret[count] = (tempStore.get(tempArt.getSortName())).getId();
				(tempArt).setId(ret[count]);
			}
			else
			{
				// Add this new artist
				insertQuery.setString(1, tempArt.getSortName());
				insertQuery.setString(2, tempArt.getShowName());
				insertQuery.execute();

				// Get the new number
				collectQuery.setString(1, tempArt.getSortName());
				ResultSet rs = collectQuery.executeQuery();

				rs.next();
				ret[count] = rs.getInt(1);
				tempArt.setId(ret[count]);

				rs.close();

				tempStore.put(tempArt.getSortName(), new Artist(tempArt
						.getSortName(), ret[count], tempArt.getShowName()));
			}

			// Increment the count
			count++;
		}

		// Return the constructed array
		return ret;
	}

	public void cancel()
	{
		// Necessary for this to finish, so just leave in background
	}

	public void commitArtists()
	{
		artists.putAll(tempStore);
		tempStore.clear();
	}

	public void execute() throws SQLException
	{
		// Get a statement and run the query
		Statement s = p.getConnection().getStatement();
		ResultSet rs = s
				.executeQuery("SELECT sort_name,artist_id,show_name FROM Artists");

		// Fill the set
		while (rs.next())
		{
			String art = rs.getString(1);
			int num = rs.getInt(2);
			String show = rs.getString(3);

			artists.put(art, new Artist(art, num, show));
		}

		// Close the database objects
		rs.close();
		s.close();

		executed = true;
	}

	public boolean exist(String name)
	{
		return artists.keySet().contains(name);
	}

	public Artist getArtist(int num) throws SQLException
	{
		Statement s = p.getConnection().getStatement();
		ResultSet rs = s
				.executeQuery("SELECT sort_name, show_name FROM Artists WHERE artist_id = "
						+ num);

		// Move on and return the relevant artust
		rs.next();
		String sort = rs.getString(1);
		String show = rs.getString(2);

		rs.close();
		s.close();

		// Add this new artist
		artists.put(sort, new Artist(sort, num, show));

		return artists.get(sort);
	}

	public Artist getArtist(String name) throws SQLException
	{
		if (exist(name))
			return artists.get(name);
		else if (tempStore.containsKey(name))
			return tempStore.get(name);
		else
		{
			collectQuery.setString(1, name);
			ResultSet rs = collectQuery.executeQuery();

			// Move on and return the relevant artust
			if (rs.next())
			{
				int num = rs.getInt(1);
				String showName = rs.getString(2);

				rs.close();

				// Add this new artist
				artists.put(name, new Artist(name, num, showName));

				return artists.get(name);
			}
			else
			{
				rs.close();
				return new Artist(name, -1);
			}
		}
	}

	public Artist getArtistFromShowName(String name) throws SQLException
	{
		if (exist(name))
			return artists.get(name);
		else if (tempStore.containsKey(name))
			return tempStore.get(name);
		else
		{
			collectQueryShowName.setString(1, name);
			ResultSet rs = collectQueryShowName.executeQuery();

			// Move on and return the relevant artust
			if (rs.next())
			{
				int num = rs.getInt(1);
				String sortName = rs.getString(2);

				rs.close();

				// Add this new artist
				artists.put(name, new Artist(sortName, num, name));

				return artists.get(name);
			}
			else
			{
				rs.close();
				return new Artist(name, -1);
			}
		}
	}

	public Collection<Artist> getArtists()
	{
		try
		{
			if (!executed)
				execute();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}

		return artists.values();
	}

	public static GetArtists create() throws SQLException
	{
		if (singleton == null)
			singleton = new GetArtists(Persistent.create());

		return singleton;
	}
}
