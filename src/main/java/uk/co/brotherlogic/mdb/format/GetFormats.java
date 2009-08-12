package uk.co.brotherlogic.mdb.format;

/**
 * Class to deal with getting formats
 * @author Simon Tucker
 */

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;

import uk.co.brotherlogic.mdb.Category;
import uk.co.brotherlogic.mdb.GetCategories;
import uk.co.brotherlogic.mdb.Persistent;

public class GetFormats
{
	// Maps format name to format
	Map<String, Format> formats;
	Set<String> baseFormats;
	Persistent p;

	private static GetFormats singleton;

	private GetFormats() throws SQLException
	{
		// Set the required parameters
		p = Persistent.create();

		formats = new TreeMap<String, Format>();
	}

	public Format addFormat(Format in) throws SQLException
	{
		// Check if this format is already present
		if (formats.keySet().contains(in.getName()))
			return formats.get(in.getName());

		// Totally new format! need to manually construct this one

		// Add the new format and commit the update
		PreparedStatement ps = p.getConnection().prepState(
				"INSERT INTO Formats (FormatName, baseformat) VALUES (?,?)");
		ps.setString(1, in.getName());
		ps.setString(2, in.getBaseFormat());
		ps.execute();

		// Get the new format number
		PreparedStatement ps2 = p.getConnection().prepState(
				"SELECT FormatNumber FROM Formats WHERE FormatName = ?");
		ps2.setString(1, in.getName());
		ResultSet rs = ps2.executeQuery();
		rs.next();
		int val = rs.getInt(1);

		Format ret = new Format(val, in.getName(), in.getBaseFormat());

		// Close the database objects
		rs.close();

		return ret;

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
				.executeQuery("SELECT FormatName,FormatNumber,baseformat  FROM Formats");

		baseFormats = new TreeSet<String>();

		// Fill the set
		while (rs.next())
		{
			// Construct the new format
			Format temp = new Format(rs.getInt(2), rs.getString(1), rs
					.getString(3));

			// Now add the corresponding categories
			temp.setCategories(getCategories(temp.getNumber()));

			baseFormats.add(temp.getBaseFormat());
			formats.put(temp.getName(), temp);
		}

		// Close the database objects
		rs.close();
		s.close();
	}

	public Collection<String> getBaseFormats()
	{
		if (baseFormats == null)
			try
			{
				execute();
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}

		return baseFormats;
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
		if (formats.keySet().contains(in))
			return formats.get(in);
		else
		{
			Statement s = p.getConnection().getStatement();
			ResultSet rs = s
					.executeQuery("SELECT FormatNumber, baseformat FROM Formats WHERE FormatName = \'"
							+ in + "\'");

			if (rs.next())
			{
				int num = rs.getInt(1);
				String base = rs.getString(2);
				rs.close();
				s.close();
				return new Format(num, in, base);
			}
			else
			{
				rs.close();
				s.close();
				return new Format(-1, in, "");
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
