package uk.co.brotherlogic.mdb.record;

/**
 * Class to represent a record
 * @author Simon Tucker
 */

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;
import java.util.Map.Entry;

import uk.co.brotherlogic.mdb.Artist;
import uk.co.brotherlogic.mdb.Category;
import uk.co.brotherlogic.mdb.Label;
import uk.co.brotherlogic.mdb.LineUp;
import uk.co.brotherlogic.mdb.Track;
import uk.co.brotherlogic.mdb.format.Format;
import uk.co.brotherlogic.mdb.groop.Groop;

public class Record implements Comparable<Record>
{

	private static final double GROOP_RATIO = 0.8;

	String author;

	private int discogsNum = -1;

	Calendar boughtDate;

	Category category;

	Collection<String> catnos = new LinkedList<String>();

	Format format;

	Collection<Label> labels = new LinkedList<Label>();

	String notes;

	int number;

	int owner;

	String title;

	Collection<Track> tracks = new LinkedList<Track>();

	int year;

	int releaseMonth;

	int releaseType;

	double price;

	Collection<Artist> compilers;

	/** The location of the record on it's respective shelf */
	private int shelfpos;

	public Record()
	{
		title = "";
		notes = " ";
		year = -1;
		boughtDate = Calendar.getInstance();
		catnos = new LinkedList<String>();
		labels = new LinkedList<Label>();
		tracks = new LinkedList<Track>();

		price = 0.0;
	}

	public Record(String title, Format format, Calendar boughtDate,
			Collection<String> catnos, Collection<Label> labels,
			Collection<Track> tracks, int shelfpos)
	{
		this();
		this.title = title;
		this.format = format;
		this.boughtDate = boughtDate;
		this.catnos = catnos;
		this.labels = labels;
		this.tracks = tracks;
		this.shelfpos = shelfpos;
	}

	public void addCatNo(String catNo)
	{
		catnos.add(catNo);
	}

	public void addLabel(Label label)
	{
		labels.add(label);
	}

	public void addPersonnel(int trackNumber, Collection<Artist> pers)
	{
		Track intTrack = getTrack(trackNumber);
		intTrack.addPersonnel(pers);
	}

	public void addTrack(Track trk)
	{
		tracks.add(trk);
	}

	public void addTracks(int addPoint, int noToAdd)
	{
		// Work through the tracks
		Iterator<Track> tIt = tracks.iterator();
		Collection<LineUp> groops = new Vector<LineUp>();
		Collection<Artist> pers = new Vector<Artist>();

		while (tIt.hasNext())
		{
			// Get the current track
			Track currTrack = tIt.next();

			// If the track is beyond the addition point - move it along
			if (currTrack.getTrackNumber() > addPoint)
				// Update the trackNumber
				currTrack.setTrackNumber(currTrack.getTrackNumber() + noToAdd);
			else if (currTrack.getTrackNumber() == addPoint)
			{
				// Collect the information from the previous track
				groops = currTrack.getLineUps();
				pers = currTrack.getPersonnel();
				// currTrack.setTrackNumber(currTrack.getTrackNumber() +
				// noToAdd);
			}
		}

		// Now add the new tracks using the new information collected above
		for (int i = addPoint + 1; i < addPoint + noToAdd + 1; i++)
			tracks.add(new Track("", 0, groops, pers, i));
	}

	public int compareTo(Record o)
	{
		return (title.toLowerCase() + number).compareTo(o.getTitle()
				.toLowerCase()
				+ (o.getNumber()));
	}

	public void createTracks(int noTracks)
	{
		for (int i = 0; i < noTracks; i++)
			tracks.add(new Track("", 0, new Vector<LineUp>(),
					new Vector<Artist>(), i + 1));
	}

	@Override
	public boolean equals(Object o)
	{
		if (o instanceof Record)
			return this.compareTo((Record) o) == 0;
		else
			return false;
	}

	public Collection<LineUp> getAllLineUps()
	{
		Collection<LineUp> allGroops = new Vector<LineUp>();

		Iterator<Track> tIt = tracks.iterator();
		while (tIt.hasNext())
			allGroops.addAll((tIt.next()).getLineUps());

		return allGroops;
	}

	public String getAuthor()
	{
		return author;
	}

	public Category getCategory()
	{
		return category;
	}

	public Collection<String> getCatNos()
	{
		return catnos;
	}

	public String getCatNoString()
	{
		String ret = "";
		Iterator<String> cIt = catnos.iterator();
		while (cIt.hasNext())
			ret += cIt.next();

		return ret;
	}

	public Collection<Artist> getCompilers() throws SQLException
	{
		if (compilers == null)
			compilers = new LinkedList<Artist>(GetRecords.create()
					.getCompilers(this));

		return compilers;
	}

	public Calendar getDate()
	{
		return boughtDate;
	}

	public int getDiscogsURI()
	{
		return discogsNum;
	}

	public Format getFormat()
	{
		return format;
	}

	public int getGenre()
	{
		return category.getMP3Number();
	}

	public String getGroopString()
	{
		// Construct the groop string
		Collection<String> main = getMainGroops();
		Iterator<String> gIt = main.iterator();
		String groop = "";
		while (gIt.hasNext())
			groop += gIt.next() + " & ";

		// Remove the trailing & or replace with various
		if (groop.length() > 0)
			groop = groop.substring(0, groop.length() - 3);
		else
			groop = "Various";

		return groop;

	}

	public Collection<Label> getLabels()
	{
		return labels;
	}

	public Collection<String> getMainGroops()
	{
		// A Map of groopName --> Count
		Map<String, Integer> mainGroopMap = new TreeMap<String, Integer>();
		Collection<String> mainGroops = new Vector<String>();

		Iterator<Track> tIt = tracks.iterator();
		while (tIt.hasNext())
		{
			// Increment the count for each groop
			Collection<LineUp> groops = (tIt.next()).getLineUps();
			Iterator<LineUp> gIt = groops.iterator();
			while (gIt.hasNext())
			{
				Groop grp = gIt.next().getGroop();
				String groopName = grp.getSortName();

				Integer intVal;
				if (mainGroopMap.containsKey(groopName))
				{
					intVal = mainGroopMap.get(groopName);
					intVal = intVal.intValue() + 1;
				}
				else
					intVal = 1;

				mainGroopMap.put(groopName, intVal);
			}
		}

		// Select only groops who appear on the right number of tracks
		for (Entry<String, Integer> ent : mainGroopMap.entrySet())
			if (((ent.getValue()).doubleValue() / tracks.size()) > GROOP_RATIO)
				mainGroops.add(ent.getKey());

		return mainGroops;

	}

	public String getNotes()
	{
		return notes;
	}

	public int getNoTracks()
	{
		return tracks.size();
	}

	public int getNumber()
	{
		return number;
	}

	public int getOwner()
	{
		return owner;
	}

	public double getPrice()
	{
		return price;
	}

	/**
	 * @return Returns the releaseMonth.
	 */
	public int getReleaseMonth()
	{
		return releaseMonth;
	}

	/**
	 * @return Returns the releaseType.
	 */
	public int getReleaseType()
	{
		return releaseType;
	}

	public int getReleaseYear()
	{
		return year;
	}

	public int getShelfPos()
	{
		return shelfpos;
	}

	public String getTitle()
	{
		return title;
	}

	public String getTitleWithCat()
	{
		return getTitle() + getCatNoString();
	}

	public Track getTrack(int trackNumber)
	{
		Track ret = new Track();

		// Search all the tracks
		boolean found = false;
		Iterator<Track> tIt = tracks.iterator();
		while (tIt.hasNext() && !found)
		{
			Track currTrack = tIt.next();
			if (currTrack.getTrackNumber() == trackNumber)
			{
				ret = currTrack;
				found = true;
			}
		}
		return ret;
	}

	public Collection<Track> getTracks()
	{
		return tracks;
	}

	public Collection<String> getTrackTitles()
	{
		Collection<String> retSet = new Vector<String>();
		Iterator<Track> tIt = tracks.iterator();
		while (tIt.hasNext())
			retSet.add((tIt.next()).getTitle());

		return retSet;
	}

	public int getYear()
	{
		return year;
	}

	@Override
	public int hashCode()
	{
		return number;
	}

	private void resetShelfPos()
	{
		System.err.println("RESETTING SHELF POS: " + shelfpos);
		if (shelfpos > 0)
			shelfpos = 0;
	}

	public void save() throws SQLException
	{
		GetRecords.create().saveCompilers(this);
	}

	public void setAuthor(String in)
	{
		author = in;
	}

	public void setCategory(Category cat)
	{
		category = cat;
	}

	public void setCatNos(Collection<String> cats)
	{
		// Remove and add
		catnos.clear();
		catnos.addAll(cats);
	}

	public void setCompilers(Collection<Artist> compilers)
	{
		this.compilers = new LinkedList<Artist>(compilers);
	}

	public void setDate(String dat) throws ParseException
	{
		if (dat.length() > 8)
		{
			// dat is YYYY-MM-DD
			boughtDate = Calendar.getInstance();
			DateFormat longForm = new SimpleDateFormat("yyyy-MM-dd");
			boughtDate.setTime(longForm.parse(dat));
		}
		else if (dat.length() < 6)
			throw new NumberFormatException("No String!");
		else
		{
			// Assume date to be DD/MM/YY
			boughtDate = Calendar.getInstance();
			DateFormat shortForm = new SimpleDateFormat("dd/MM/yy");
			boughtDate.setTime(shortForm.parse(dat));
		}
	}

	public void setDiscogsNum(int dNum)
	{
		discogsNum = dNum;
	}

	public void setFormat(Format form)
	{
		// Reset the shelfpos
		resetShelfPos();
		format = form;
	}

	public void setGroops(int trackNumber, Collection<LineUp> lineups)
	{
		Track intTrack = getTrack(trackNumber);
		intTrack.setLineUps(lineups);
	}

	public void setLabels(Collection<Label> labs)
	{
		// Remove and add
		labels.clear();
		labels.addAll(labs);
	}

	public void setNotes(String in)
	{
		notes = in;
	}

	public void setNumber(int num)
	{
		number = num;
	}

	public void setOwner(int in)
	{
		owner = in;
	}

	public void setPersonnel(int trackNumber, Collection<Artist> pers)
	{
		Track intTrack = getTrack(trackNumber);
		intTrack.setPersonnel(pers);
	}

	public void setPrice(double price)
	{
		this.price = price;
	}

	/**
	 * @param releaseMonth
	 *            The releaseMonth to set.
	 */
	public void setReleaseMonth(int releaseMonth)
	{
		this.releaseMonth = releaseMonth;
	}

	/**
	 * @param releaseType
	 *            The releaseType to set.
	 */
	public void setReleaseType(int releaseType)
	{
		this.releaseType = releaseType;
	}

	public void setTitle(String tit)
	{
		title = tit;
	}

	public void setTracks(Collection<Track> tracksIn)
	{
		tracks.clear();
		tracks.addAll(tracksIn);
	}

	public void setTracks(int maxNumber)
	{
		// Only include relevant tracks
		Collection<Track> newTracks = new LinkedList<Track>();
		Iterator<Track> trIt = tracks.iterator();
		while (trIt.hasNext())
		{
			Track currTrack = trIt.next();
			if (currTrack.getTrackNumber() <= maxNumber)
				newTracks.add(currTrack);
		}

		// Replace the tracks
		tracks = newTracks;

	}

	public void setYear(int in)
	{
		year = in;
	}

	@Override
	public String toString()
	{
		Collection<String> fullGroops = getMainGroops();
		Iterator<String> fIt = fullGroops.iterator();
		String grp = "";
		while (fIt.hasNext())
			grp += fIt.next() + " & ";

		if (grp.length() > 2)
			grp = grp.substring(0, grp.length() - 3);
		else
			grp = "Various (but " + getAuthor() + ")";

		String ret = "";
		ret += grp + " - " + title + "(" + format + ") " + labels + category
				+ "\n";

		Iterator<Track> tIt = tracks.iterator();
		while (tIt.hasNext())
		{
			Track next = tIt.next();
			ret += next.getTrackNumber() + ": ";
			ret += next.getLineUps() + " - ";
			ret += next.getTitle() + "[";
			ret += next.getPersonnel() + "]";
			ret += " / " + next.getTrackRefNumber() + "\n";
		}
		return ret;
	}
}