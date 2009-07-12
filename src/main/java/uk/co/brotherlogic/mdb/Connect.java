package uk.co.brotherlogic.mdb;

/**
 * Class to deal with database connection
 * @author Simon Tucker
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class Connect
{
	Connection locDB;

	private static Connect singleton;

	private Connect() throws SQLException
	{
		makeConnection();
	}

	public void cancelTrans() throws SQLException
	{
		locDB.rollback();
	}

	public void commitTrans() throws SQLException
	{
		locDB.commit();
	}

	public PreparedStatement getPreparedStatement(String sql)
			throws SQLException
	{
		// Create the statement
		PreparedStatement ps = locDB.prepareStatement(sql);

		return ps;
	}

	public Statement getStatement() throws SQLException
	{
		// Create the statement
		Statement s = locDB.createStatement();

		// s.close();
		return s;

	}

	public void makeConnection() throws SQLException
	{
		try
		{
			// Load all the drivers and initialise the database connection
			Class.forName("org.postgresql.Driver");
			locDB = DriverManager
					.getConnection("jdbc:postgresql://hancock/music?user=music");

			// Switch off auto commit
			locDB.setAutoCommit(false);
		}
		catch (ClassNotFoundException e)
		{
			throw new SQLException(e);
		}
	}

	public PreparedStatement prepState(String in) throws SQLException
	{
		return locDB.prepareStatement(in);
	}

	public void rollbackTrans() throws SQLException
	{
		locDB.rollback();
	}

	public void runDelete(String delete) throws SQLException
	{
		// Create the update
		Statement s = locDB.createStatement();

		// Run the update
		s.executeUpdate(delete);
		s.close();
	}

	public void runUpdate(String update) throws SQLException
	{
		// Create the update
		Statement s = locDB.createStatement();

		// Run the update
		s.executeUpdate(update);

		// Close the connection
		s.close();
	}

	public static Connect getConnection() throws SQLException
	{
		if (singleton == null)
			singleton = new Connect();
		return singleton;
	}

	public static void main(String[] args)
	{
		try
		{
			// Attempt to establish a database connection
			Connect con = new Connect();
			con.makeConnection();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
