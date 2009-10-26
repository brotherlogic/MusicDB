package uk.co.brotherlogic.mdb.record.discogs;

import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import uk.co.brotherlogic.mdb.artist.Artist;
import uk.co.brotherlogic.mdb.artist.GetArtists;
import uk.co.brotherlogic.mdb.groop.GetGroops;
import uk.co.brotherlogic.mdb.groop.Groop;
import uk.co.brotherlogic.mdb.groop.LineUp;
import uk.co.brotherlogic.mdb.label.GetLabels;
import uk.co.brotherlogic.mdb.label.Label;
import uk.co.brotherlogic.mdb.record.Record;
import uk.co.brotherlogic.mdb.record.Track;

public class DiscogsReaderParser extends DefaultHandler
{
	Record rec;

	private static final int NULL_STATE = 0;
	private static final int READING_AUTHORS = 1;
	private static final int READING_LABELS = 2;
	private static final int READING_TRACK = 3;
	private int state = NULL_STATE;

	private final Collection<Groop> overallGroops = new LinkedList<Groop>();

	private Track currTrack;

	private String text = "";

	private int trackNumber = 1;

	public DiscogsReaderParser(Record record)
	{
		rec = record;
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException
	{
		super.characters(ch, start, length);
		text += new String(ch, start, length);
	}

	@Override
	public void endElement(String uri, String localName, String name) throws SAXException
	{
		super.endElement(uri, localName, name);

		if (name.equalsIgnoreCase("name") && state == READING_AUTHORS)
			try
			{
				Artist art = GetArtists.create().getArtistFromShowName(text);
				overallGroops.add(GetGroops.build().getGroop(text));
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
		else if (name.equals("title") && state == NULL_STATE)
			rec.setTitle(text);
		else if (name.equals("duration") && state == READING_TRACK)
		{
			String[] elems = text.split(":");
			int timeInSeconds = Integer.parseInt(elems[0]) * 60 + Integer.parseInt(elems[1]);
			currTrack.setLengthInSeconds(timeInSeconds);
		}
	}

	@Override
	public void startElement(String uri, String localName, String name, Attributes attributes)
			throws SAXException
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
			{
				List<LineUp> finishedLineUps = new LinkedList<LineUp>();
				for (Groop grp : overallGroops)
				{
					List<LineUp> lineups = new LinkedList<LineUp>(grp.getLineUps());
					if (lineups.size() == 0)
					{
						List<Artist> artists = new LinkedList<Artist>();
						try
						{
							artists.add(GetArtists.create()
									.getArtistFromShowName(grp.getShowName()));
						}
						catch (SQLException e)
						{
							e.printStackTrace();
						}
						LineUp nLineUp = new LineUp(1, artists, grp);
						finishedLineUps.add(nLineUp);
					}
					else
						finishedLineUps.add(lineups.get(0));
				}

				try
				{
					for (Track track : rec.getTracks())
						for (LineUp lineup : track.getLineUps())
							track.addLineUp(lineup);
				}
				catch (SQLException e)
				{
					throw new SAXException(e);
				}
				state = READING_TRACK;
			}

			text = "";
		}
	}
}
