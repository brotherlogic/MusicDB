package uk.co.brotherlogic.mdb;

/**
 * Class to deal with the collection and addition of categories
 * @author Simon Tucker
 * Updated for PostGres
 */

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

public class GetCategories
{
	// Maps category number to category
	Map<String, Category> categories;
	Persistent p;

	// Stores the insert query
	PreparedStatement insertQuery;
	PreparedStatement collectQuery;

	private static GetCategories singleton;

	private GetCategories() throws SQLException
	{
		// Set the required parameters
		p = Persistent.create();
		categories = new TreeMap<String, Category>();

		insertQuery = p
				.getConnection()
				.getPreparedStatement(
						"INSERT INTO Categories(CategoryName,MP3Category) VALUES (?,?)");

		collectQuery = p.getConnection().getPreparedStatement(
				"SELECT CategoryNumber FROM Categories WHERE CategoryName = ?");
	}

	public int addCategory(Category in) throws SQLException
	{
		// Build the categories if we need to
		if (categories.size() == 0)
			execute();

		if (exists(in.getName()))
			return (categories.get(in.getName())).getNumber();
		else if (in.getNumber() > 0)
			return in.getNumber();
		else
		{
			// Add the category
			insertQuery.setString(1, in.getName());
			insertQuery.setInt(2, in.getMP3Number());
			insertQuery.execute();

			collectQuery.setString(1, in.getName());
			ResultSet rs = collectQuery.executeQuery();

			// Move the cursor one step onewards and then choose the
			// categorynumber
			rs.next();
			int catNum = rs.getInt(1);

			// Set the category number and return it
			in.setNumber(catNum);
			return catNum;
		}
	}

	public void cancel()
	{
		// Necessary for this to finish, so just leave in background
	}

	public void commitCategory(Category in)
	{
		if (!exists(in.getName()))
			categories.put(in.getName(), in);
	}

	public void execute() throws SQLException
	{
		// Initialise the groop store
		categories = new TreeMap<String, Category>();

		// Get a statement and run the query
		Statement s = p.getConnection().getStatement();
		ResultSet rs = s.executeQuery("SELECT * FROM Categories");

		// Extract the information
		while (rs.next())
		{
			// Get the information from the result set
			int categoryNumber = rs.getInt(1);
			String categoryName = rs.getString(2);
			int mp3Number = rs.getInt(3);

			// Construct and store the new category
			Category newCat = new Category(categoryName, categoryNumber,
					mp3Number);
			categories.put(categoryName, newCat);

		}

		// Close the statements
		rs.close();
		s.close();
	}

	public boolean exists(String name)
	{
		return categories.keySet().contains(name);
	}

	public Collection<Category> getCategories()
	{
		TreeSet<Category> ret = new TreeSet<Category>();
		ret.addAll(categories.values());
		return ret;
	}

	public Category getCategory(String name, int mp3Number) throws SQLException
	{
		if (categories.size() == 0)
			execute();

		if (exists(name))
			return categories.get(name);
		else
			return new Category(name, -1, mp3Number);
	}

	public String toString()
	{
		return "Categories";
	}

	public static GetCategories build() throws SQLException
	{
		if (singleton == null)
			singleton = new GetCategories();
		return singleton;
	}

}
