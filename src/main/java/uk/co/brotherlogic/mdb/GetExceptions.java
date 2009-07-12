package uk.co.brotherlogic.mdb;

/**
 * Class to deal with getting exceptions
 * @author Simon Tucker
 */

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.TreeSet;

public class GetExceptions
{
	Collection exceptions;
	Persistent p;

	public GetExceptions(Persistent pers)
	{
		// Set the required parameters
		p = pers;
	}

	public void cancel()
	{
		// Necessary for this to finish, so just leave in background
	}

	public void execute() throws SQLException
	{
		// Get a statement and run the query
		Statement s = p.getConnection().getStatement();
		ResultSet rs = s.executeQuery("SELECT * FROM Exceptions");

		// Initialise the Set
		exceptions = new TreeSet();

		// Fill the set
		while (rs.next())
		{
			String ex = rs.getString(1);
			exceptions.add(ex);
		}

		// Close the database objects
		rs.close();
		s.close();
	}

	public Collection getData()
	{
		return exceptions;
	}

}
