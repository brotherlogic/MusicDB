package uk.co.brotherlogic.mdb.format;

/**
 * Class to represent a format
 * @author Simon Tucker
 */

import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;
import java.util.TreeSet;

import uk.co.brotherlogic.mdb.Category;

/**
 * Represents a format
 * 
 * @author sat
 * 
 */
public class Format implements Comparable<Format>
{
	/** The name of the format */
	private final String name;

	/** The base name of the format */
	private final String baseFormat;

	/** The categories represented by this format */
	private final Collection<Category> categories;

	/** The id number of this format */
	private int formatNumber;

	/** Flag indicating if this format has been changed */
	private boolean formatUpdated = false;

	public Format()
	{
		name = "";
		baseFormat = "";
		categories = new TreeSet<Category>();
		formatNumber = -1;
	}

	public Format(int num, String sIn, String base)
	{
		name = sIn;
		formatNumber = num;
		baseFormat = base;
		categories = new TreeSet<Category>();
	}

	public Format(int num, String sIn, String base, Format categoryCopy)
	{
		name = sIn;
		formatNumber = num;
		baseFormat = base;
		categories = categoryCopy.categories;
	}

	public void addCategory(Category cat)
	{
		categories.add(cat);
	}

	public int compareTo(Format o)
	{
		return name.compareTo(o.name);
	}

	@Override
	public boolean equals(Object o)
	{
		if (o instanceof Format)
			return compareTo((Format) o) == 0;
		else
			return false;
	}

	public String fullString()
	{
		String out = formatNumber + ": " + name + "\n";
		Iterator<Category> cIt = categories.iterator();
		while (cIt.hasNext())
			out += cIt.next() + ", ";

		return out;
	}

	public String getBaseFormat()
	{
		return baseFormat;
	}

	public Collection<Category> getCategories()
	{
		return categories;
	}

	public String getName()
	{
		return name;
	}

	public int getNumber()
	{
		return formatNumber;
	}

	@Override
	public int hashCode()
	{
		return name.hashCode();
	}

	public int save() throws SQLException
	{
		if (formatNumber == -1 || formatUpdated)
			formatNumber = GetFormats.create().save(this);

		return formatNumber;
	}

	public void setCategories(Collection<Category> vec)
	{
		// Clear the old categories
		categories.clear();

		// Add the new formats
		categories.addAll(vec);

		formatUpdated = true;
	}

	@Override
	public String toString()
	{
		return name;
	}
}
