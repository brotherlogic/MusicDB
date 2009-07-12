package uk.co.brotherlogic.mdb;
/**
 * Class to deal with no case comparisons
 * @author Simon Tucker
 */
 
import java.util.*;
 
class StringNoCaseComparator implements Comparator
{
	public int compare(Object o1,Object o2)
	{
		return ((String)o1).compareToIgnoreCase(((String)o2));
	}
	
	public boolean equals(Object o1, Object o2)
  {
    return ((String)o1).equalsIgnoreCase(((String)o2));
  }
	
}
