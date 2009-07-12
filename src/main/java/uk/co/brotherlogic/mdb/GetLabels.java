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
import java.util.Map;
import java.util.TreeMap;

public class GetLabels
{
	// Map of labelName --> Label
	Map<String, Label> labels;
	Persistent p;

	PreparedStatement insertQuery;
	PreparedStatement collectQuery;

	private static GetLabels singleton;

	private GetLabels() throws SQLException
	{
		// Initialise the set
		labels = new TreeMap<String, Label>();

		// Set the required parameters
		p = Persistent.create();

		insertQuery = p.getConnection().getPreparedStatement(
				"INSERT INTO Labels (LabelName) VALUES (?)");
		collectQuery = p.getConnection().getPreparedStatement(
				"SELECT LabelNumber FROM Labels WHERE LabelName = ?");
	}

	public int[] addLabels(Collection<Label> labelsIn) throws SQLException
	{
		// Initialise the array
		int[] retArray = new int[labelsIn.size()];

		// Work through the array
		int count = 0;
		for (Label currLabel : labelsIn)
		{
			if (currLabel.getNumber() > 0)
				retArray[count] = currLabel.getNumber();
			else if (labels.keySet().contains(currLabel.getName()))
			{
				retArray[count] = (labels.get(currLabel.getName())).getNumber();
				currLabel.setNumber(retArray[count]);

			}
			else
			{
				// Add the new label
				insertQuery.setString(1, currLabel.getName());
				insertQuery.execute();

				// Get the number
				collectQuery.setString(1, currLabel.getName());
				ResultSet rs = collectQuery.executeQuery();
				rs.next();

				retArray[count] = rs.getInt(1);
				currLabel.setNumber(retArray[count]);

				rs.close();
			}

			// Step the count
			count++;
		}

		return retArray;
	}

	public void commitLabels(Collection<Label> commit)
	{
		for (Label tempLab : commit)
			if (!labels.containsKey(tempLab.getName()))
				labels.put(tempLab.getName(), tempLab);
	}

	private void execute() throws SQLException
	{
		// Get a statement and run the query
		Statement s = p.getConnection().getStatement();
		ResultSet rs = s
				.executeQuery("SELECT LabelName, LabelNumber FROM Labels");

		// Initialise the Set
		labels = new TreeMap<String, Label>();

		// Fill the set
		while (rs.next())
		{
			String name = rs.getString(1);
			int number = rs.getInt(2);
			labels.put(name, new Label(name, number));
		}

		// Close the database objects
		rs.close();
		s.close();
	}

	public Label getLabel(String name) throws SQLException
	{
		if (labels.containsKey(name))
			return labels.get(name);
		else
		{
			// Try to manually retrieve the label
			collectQuery.setString(1, name);
			ResultSet rs = collectQuery.executeQuery();

			if (rs.next())
			{
				Label temp = new Label(name, rs.getInt(1));
				rs.close();
				return temp;
			}
			else
			{
				rs.close();
				return new Label(name, -1);
			}
		}
	}

	public Collection<Label> getLabels()
	{
		try
		{
			if (labels.size() == 0)
				execute();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		return labels.values();
	}

	public static GetLabels create() throws SQLException
	{
		if (singleton == null)
			singleton = new GetLabels();
		return singleton;
	}

}
