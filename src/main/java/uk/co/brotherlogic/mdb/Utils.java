package uk.co.brotherlogic.mdb;

public class Utils
{
	public static String flipString(String toFlip)
	{
		if (toFlip.contains(","))
		{
			int indexOfComma = toFlip.indexOf(",");
			return toFlip.substring(indexOfComma + 1).trim() + " "
					+ toFlip.substring(0, indexOfComma);
		}
		else
			return toFlip;
	}

	public static void main(String[] args)
	{
		System.out.println(Utils.flipString("donkey"));
		System.out.println(Utils.flipString("Dyble, Judy"));
		System.out.println(Utils.flipString("Earth, Wind and Fire"));
	}
}
