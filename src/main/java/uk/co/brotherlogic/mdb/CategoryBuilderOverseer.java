package uk.co.brotherlogic.mdb;

/**
 * Class to oversee the construction of new categories and the like
 * @author Simon Tucker
 */

import java.sql.SQLException;
import java.util.Collection;

import javax.swing.JFrame;

import uk.co.brotherlogic.mdb.format.Format;
import uk.co.brotherlogic.mdb.format.GetFormats;

public class CategoryBuilderOverseer
{
	// The GUI
	CategoryBuilderGUI gui;

	// Persistent so we can use the database
	Persistent pers;

	public CategoryBuilderOverseer(JFrame par, Persistent pers,
			Collection<Category> categories, Collection<Format> formats)
	{
		// Set the persistent object
		this.pers = pers;

		// Make the gui
		gui = new CategoryBuilderGUI(par, categories, formats);

		// Begin
		gui.setVisible(true);
	}

	public CategoryBuilderOverseer(JFrame par, Persistent pers,
			Collection<Category> categories, Collection<Format> formats,
			Format newFormat)
	{
		this.pers = pers;

		// Add the new format to the format selection
		formats.add(newFormat);

		// Make the gui
		gui = new CategoryBuilderGUI(par, categories, formats);

		// Set it off
		gui.setVisible(true);
	}

	public void dealWithData() throws SQLException
	{

		// Get the new format
		Format newFormat = gui.getFormat();

		// If non-null then add this new format to the database
		if (newFormat != null)
			GetFormats.create().updateFormat(newFormat);
	}

}
