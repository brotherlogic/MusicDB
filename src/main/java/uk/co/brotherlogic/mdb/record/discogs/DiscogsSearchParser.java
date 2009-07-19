package uk.co.brotherlogic.mdb.record.discogs;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import uk.co.brotherlogic.mdb.record.Record;

public class DiscogsSearchParser extends DefaultHandler
{
	List<Record> records = new LinkedList<Record>();
	boolean reading = false;
	Record currRecord = null;
	String text;

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

		if (name.equalsIgnoreCase("result"))
		{
			reading = false;
			if (currRecord != null)
				records.add(currRecord);
		}
		else if (name.equalsIgnoreCase("title"))
			currRecord.setTitle(text);
		else if (name.equalsIgnoreCase("uri"))
		{
			String[] elems = text.split("/");
			currRecord.setDiscogsNum(Integer.parseInt(elems[elems.length - 1]));
		}
	}

	public Collection<Record> getRecords()
	{
		return records;
	}

	@Override
	public void startElement(String uri, String localName, String name,
			Attributes attributes) throws SAXException
	{
		text = "";
		if (name.equalsIgnoreCase("result"))
			if (attributes.getValue("type").equalsIgnoreCase("release"))
			{
				currRecord = new Record();
				reading = true;
			}
	}
}
