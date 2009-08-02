package uk.co.brotherlogic.mdb.record.discogs;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

import uk.co.brotherlogic.mdb.App;
import uk.co.brotherlogic.mdb.GetArtists;
import uk.co.brotherlogic.mdb.GetCategories;
import uk.co.brotherlogic.mdb.GetFormats;
import uk.co.brotherlogic.mdb.GetLabels;
import uk.co.brotherlogic.mdb.groop.GetGroops;
import uk.co.brotherlogic.mdb.record.AddRecordOverseer;
import uk.co.brotherlogic.mdb.record.Record;

public class DiscogsReaderBuilder
{
	private static final String API_KEY = "67668099b8";

	public Record buildRecordFromDiscogs(String artist, String title)
	{
		Record toRet = new Record();

		List<Record> records = searchForTitle(title);
		for (Record record : records)
		{
			fillOutRecord(record);
			if (record.getAuthor() != null
					&& record.getAuthor().equalsIgnoreCase(artist))
				toRet = record;
			else
				System.err.println(record.getAuthor() + ", " + artist);
		}

		// Deal with a non-find here

		return toRet;
	}

	private void fillOutRecord(Record record)
	{
		try
		{
			String rURL = "http://www.discogs.com/release/"
					+ record.getDiscogsURI() + "?f=xml&api_key=" + API_KEY;
			System.out.println("http://www.discogs.com/release/"
					+ record.getDiscogsURI() + "?f=xml&api_key=" + API_KEY);
			InputStream rIs = new GZIPInputStream(new URL(rURL).openStream());
			DiscogsReaderParser parser = new DiscogsReaderParser(record);
			SAXParser p = SAXParserFactory.newInstance().newSAXParser();
			p.parse(rIs, parser);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (ParserConfigurationException e)
		{
			e.printStackTrace();
		}
		catch (SAXException e)
		{
			e.printStackTrace();
		}
	}

	private List<Record> searchForTitle(String title)
	{
		List<Record> records = new LinkedList<Record>();

		try
		{
			String sURL = "http://www.discogs.com/search?type=title&q="
					+ title.replace(" ", "+") + "&f=xml&api_key=" + API_KEY;
			System.out.println(sURL);
			InputStream sIs = new GZIPInputStream(new URL(sURL).openStream());
			DiscogsSearchParser parser = new DiscogsSearchParser();
			SAXParser p = SAXParserFactory.newInstance().newSAXParser();
			p.parse(sIs, parser);
			records.addAll(parser.getRecords());
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (ParserConfigurationException e)
		{
			e.printStackTrace();
		}
		catch (SAXException e)
		{
			e.printStackTrace();
		}

		return records;
	}

	public static void main(String[] args) throws Exception
	{
		DiscogsReaderBuilder builder = new DiscogsReaderBuilder();
		Record rec = (builder.buildRecordFromDiscogs("Sharon Van Etten",
				"Because I Was Love"));
		AddRecordOverseer over = new AddRecordOverseer(new App(), GetArtists
				.create().getArtists(), GetLabels.create().getLabels(),
				GetFormats.create().getFormats(), GetGroops.build()
						.getGroopMap(), GetCategories.build().getCategories(),
				rec);
		System.out.println(over);
	}
}
