package uk.co.brotherlogic.mdb;

/**
 * Simple track is used to create compilations
 * 
 * @author Simon Tucker
 */

public class SimpleTrack extends Track
{

	String recordTitle;
	int recordNumber;

	public SimpleTrack()
	{
		// Default values
		recordTitle = "";
		title = "";
		trackNumber = -1;
		lengthInSeconds = -1;
	}

	public SimpleTrack(String recordTitleIn, String trackTitleIn,
			int trackNumberIn, int lengthInSecondsIn, int recordNumberIn)
	{
		recordTitle = recordTitleIn;
		title = trackTitleIn;
		trackNumber = trackNumberIn;
		lengthInSeconds = lengthInSecondsIn;
		recordNumber = recordNumberIn;
	}

	public int compareTo(SimpleTrack o)
	{
		return this.toString().compareTo(o.toString());
	}

	public int getRecordNumber()
	{
		return recordNumber;
	}

	public String getRecordTitle()
	{
		return recordTitle;
	}

	public void setLengthInSeconds(int len)
	{
		lengthInSeconds = len;
	}

	public void setRecordNumber(int recordNumberIn)
	{
		recordNumber = recordNumberIn;
	}

	public void setRecordTitle(String recordTitleIn)
	{
		recordTitle = recordTitleIn;
	}

	public String toString()
	{
		return trackNumber + ":" + title + " [" + recordTitle + "]";
	}
}