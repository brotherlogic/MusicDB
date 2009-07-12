package uk.co.brotherlogic.mdb;

/**
 * Class to represent a format
 * @author Simon Tucker
 */

import java.util.Collection;
import java.util.Iterator;
import java.util.TreeSet;

public class Format implements Comparable
{
	String name;
	TreeSet categories;
	int formatNumber;

	public Format()
	{
		name = "";
		categories = new TreeSet();
		formatNumber = 0;
	}

	public Format(int num, String sIn)
	{
		name = sIn;
		formatNumber = num;
		categories = new TreeSet();
	}

	public Format(int num, String sIn, Collection cats)
	{
		name = sIn;
		formatNumber = num;
		categories = new TreeSet(cats);
	}

	public void addCategory(Category cat)
	{
		categories.add(cat);
	}

	public int compareTo(Object o)
	{
		return name.compareTo(((Format) o).getName());
	}

	public String fullString()
	{
		String out = formatNumber + ": " + name + "\n";
		Iterator cIt = categories.iterator();
		while (cIt.hasNext())
			out += cIt.next() + ", ";

		return out;
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

	public void setCategories(Collection vec)
	{
		// Clear the old categories
		categories.clear();

		// Add the new formats
		categories.addAll(vec);
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public void setNumber(int number)
	{
		formatNumber = number;
	}

	public String toString()
	{
		return name;
	}
}
