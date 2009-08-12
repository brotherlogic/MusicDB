package uk.co.brotherlogic.mdb;

/**
 * Class to deal with no case comparisons
 * @author Simon Tucker
 */

import java.io.Serializable;
import java.util.Comparator;

class StringNoCaseComparator implements Comparator<String>, Serializable
{
	public int compare(String o1, String o2)
	{
		return (o1).compareToIgnoreCase((o2));
	}

	public boolean equals(String o1, String o2)
	{
		return (o1).equalsIgnoreCase((o2));
	}

}
