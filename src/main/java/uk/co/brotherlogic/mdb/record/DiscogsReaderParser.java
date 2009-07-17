package uk.co.brotherlogic.mdb.record;

import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import uk.co.brotherlogic.mdb.Artist;
import uk.co.brotherlogic.mdb.GetArtists;
import uk.co.brotherlogic.mdb.GetLabels;
import uk.co.brotherlogic.mdb.Groop;
import uk.co.brotherlogic.mdb.Label;
import uk.co.brotherlogic.mdb.Track;

public class DiscogsReaderParser extends DefaultHandler
{
	Record rec;

	private static final int NULL_STATE = 0;
	private static final int READING_AUTHORS = 1;
	private static final int READING_LABELS = 2;
	private static final int READING_TRACK = 3;
	private int state = NULL_STATE;

	Collection<Groop> overallGroups = new LinkedList<Groop>();

	Track currTrack;

	private String text = "";

	int trackNumber = 1;

	public DiscogsReaderParser(Record record)
	{
		rec = record;
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException
	{
		super.characters(ch, start, length);
		text += new String(ch, start, length);
	}

	@Override
	public void endElement(String uri, String localName, String name)
			throws SAXException
	{
		super.endElement(uri, localName, name);

		if (name.equalsIgnoreCase("name") && state == READING_AUTHORS)
			try
			{
				Artist art = GetArtists.create().getArtistFromShowName(text);
				// overallGroups.add(GetGroops.build().getGroop(text));
				rec.setAuthor(art.getShowName());
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		else if (name.equalsIgnoreCase("released"))
			rec.setYear(Integer.parseInt(text));
		else if (name.equalsIgnoreCase("labels"))
			state = NULL_STATE;
		else if (name.equalsIgnoreCase("artists"))
			state = NULL_STATE;
		else if (name.equalsIgnoreCase("track"))
		{
			state = NULL_STATE;
			rec.addTrack(currTrack);
		}
		else if (name.equals("position") && state == READING_TRACK)
			currTrack.setTrackNumber(trackNumber++);
		else if (name.equals("title") && state == READING_TRACK)
			currTrack.setTitle(text);
		else if (name.equals("duration") && state == READING_TRACK)
		{
			String[] elems = text.split(":");
			int timeInSeconds = Integer.parseInt(elems[0]) * 60
					+ Integer.parseInt(elems[1]);
			currTrack.setLengthInSeconds(timeInSeconds);
		}
	}

	@Override
	public void startElement(String uri, String localName, String name,
			Attributes attributes) throws SAXException
	{
		super.startElement(uri, localName, name, attributes);
		if (name.equalsIgnoreCase("artists"))
			state = READING_AUTHORS;
		else if (name.equalsIgnoreCase("labels"))
			state = READING_LABELS;
		else if (name.equalsIgnoreCase("label") && state == READING_LABELS)
			try
			{
				String lName = attributes.getValue("name");
				String catNo = attributes.getValue("catno");
				System.out.println("LABEL = " + lName);
				Label lab = GetLabels.create().getLabel(lName);
				rec.addLabel(lab);
				rec.addCatNo(catNo);
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		else if (name.equalsIgnoreCase("track"))
		{
			currTrack = new Track();
			if (!rec.getAuthor().equalsIgnoreCase("various"))
				currTrack.setGroops(overallGroups);
			state = READING_TRACK;
		}

		text = "";
	}
}
