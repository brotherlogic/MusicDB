package uk.co.brotherlogic.mdb;

/**
 * Class to deal with getting formats
 * @author Simon Tucker
 */

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;

public class GetFormats
{
	// Maps format name to format
	Map<String, Format> formats;
	Persistent p;

	private static GetFormats singleton;

	private GetFormats() throws SQLException
	{
		// Set the required parameters
		p = Persistent.create();

		formats = new TreeMap<String, Format>();
	}

	public Format addFormat(Format toAdd, Category relCat) throws SQLException
	{
		// Check that the format is not already present
		if (toAdd.getNumber() > 0)
		{
			// Do Nothing
		}
		else if (formats.containsKey(toAdd.getName()))
			toAdd = formats.get(toAdd.getName());
		else
		{

			// Totally new format! need to manually construct this one

			// Add the new format and commit the update
			p.getConnection().runUpdate(
					"INSERT INTO Formats (FormatName) VALUES (\'"
							+ toAdd.getName() + "\')");

			// Get the new format number
			Statement s = p.getConnection().getStatement();
			ResultSet rs = s
					.executeQuery("SELECT FormatNumber FROM Formats WHERE FormatName = \'"
							+ toAdd.getName() + "\'");

			rs.next();
			int val = rs.getInt(1);

			toAdd.setNumber(val);

			// Close the database objects
			rs.close();
			s.close();
		}

		// Now we've got the definitive format we need to check on the category
		// The category could be part of the format already - but if it's not we
		// need to add it!
		if (!toAdd.getCategories().contains(relCat))
		{
			// Get the category number
			int catNum = GetCategories.build().addCategory(relCat);

			// Set this category number
			relCat.setNumber(catNum);

			// Add the category to the catform table
			// p.getConnection().runDelete(
			// "INSERT INTO CatForm (FormatNumber, CategoryNumber) VALUES (" +
			// toAdd.getNumber() + "," + catNum + ")");

			// And add it to the format
			toAdd.getCategories().add(relCat);
		}

		return toAdd;
	}

	public Format addFormat(String in) throws SQLException
	{
		// Check if this format is already present
		if (formats.keySet().contains(in))
			return formats.get(in);

		// Totally new format! need to manually construct this one

		// Add the new format and commit the update
		p.getConnection().runUpdate(
				"INSERT INTO Formats (FormatName) VALUES (\'" + in + "\')");

		// Get the new format number
		Statement s = p.getConnection().getStatement();
		ResultSet rs = s
				.executeQuery("SELECT FormatNumber FROM Formats WHERE FormatName = \'"
						+ in + "\'");

		rs.next();
		int val = rs.getInt(1);

		Format ret = new Format(val, in);

		// Close the database objects
		rs.close();
		s.close();

		return ret;

	}

	public void cancel()
	{
		// Necessary for this to finish, so just leave in background
	}

	public void commitFormat(Format in)
	{
		formats.put(in.getName(), in);
	}

	public void execute() throws SQLException
	{
		// Get a statement and run the query
		Statement s = p.getConnection().getStatement();
		ResultSet rs = s
				.executeQuery("SELECT FormatName,FormatNumber FROM Formats");

		// Fill the set
		while (rs.next())
		{
			// Construct the new format
			Format temp = new Format();
			temp.setName(rs.getString(1));
			temp.setNumber(rs.getInt(2));

			// Now add the corresponding categories
			temp.setCategories(getCategories(temp.getNumber()));

			formats.put(temp.getName(), temp);
		}

		// Close the database objects
		rs.close();
		s.close();
	}

	public Collection<Category> getCategories(int num) throws SQLException
	{
		// Vector to be returned
		Vector<Category> ret = new Vector<Category>();

		Statement s = p.getConnection().getStatement();
		ResultSet rs = s
				.executeQuery("SELECT DISTINCT CategoryName,CategoryNumber FROM Categories, Records WHERE Categories.CategoryNumber = Records.category AND format = "
						+ num);

		while (rs.next())
			ret.add(GetCategories.build().getCategory(rs.getString(1),
					rs.getInt(2)));

		rs.close();
		return ret;
	}

	public Format getFormat(String in) throws SQLException
	{
		long sTime = System.currentTimeMillis();
		if (formats.keySet().contains(in))
			return formats.get(in);
		else
		{
			Statement s = p.getConnection().getStatement();
			ResultSet rs = s
					.executeQuery("SELECT FormatNumber FROM Formats WHERE FormatName = \'"
							+ in + "\'");

			if (rs.next())
			{
				int num = rs.getInt(1);
				rs.close();
				s.close();
				System.out.println("get_format: "
						+ ((System.currentTimeMillis() - sTime) / 1000.0));
				return new Format(num, in);
			}
			else
			{
				rs.close();
				s.close();
				return new Format(-1, in);
			}
		}
	}

	public Collection<Format> getFormats()
	{
		try
		{
			if (formats.size() == 0)
				execute();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}

		return new TreeSet<Format>(formats.values());
	}

	public void updateFormat(Format toUpdate) throws SQLException
	{
		// We need to remove all the old categories and change the format name
		// First the name

		String newName = toUpdate.getName();
		int id = toUpdate.getNumber();
		p.getConnection().runUpdate(
				"UPDATE Formats SET FormatName = \'" + newName
						+ "\' WHERE FormatNumber = " + id);

		// Now delete all the old categories
		p.getConnection().runDelete(
				"DELETE FROM CatForm WHERE FormNumber = " + id);

		// And add all the new categories
		for (Category cat : toUpdate.getCategories())
			p.getConnection().runUpdate(
					"INSERT INTO CatForm (CatNumber,FormNumber) VALUES ("
							+ cat.getNumber() + "," + id + ")");

	}

	public static GetFormats create() throws SQLException
	{
		if (singleton == null)
			singleton = new GetFormats();
		return singleton;
	}
}
