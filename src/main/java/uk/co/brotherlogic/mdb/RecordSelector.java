package uk.co.brotherlogic.mdb;

import java.awt.Window;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import uk.co.brotherlogic.mdb.record.GetRecords;
import uk.co.brotherlogic.mdb.record.Record;

public class RecordSelector
{
	boolean nonOver;

	public boolean getMyState()
	{
		return nonOver;
	}

	public Record selectRecord(Window owner) throws SQLException
	{
		// Set the state accordingly
		nonOver = false;

		// The record to return
		Record ret;

		// First get a list of all the record titles
		Collection<String> titles = GetRecords.create().getRecordTitles();

		// Now build a chooser to select this record
		EntitySelector sel = new EntitySelector(owner);
		sel.setData(titles, "Select Record");
		String wrap = sel.getData();

		if (wrap == null || wrap.length() == 0)
			ret = null;
		else
		{
			// Now get the record associated with this
			List<Record> records = GetRecords.create().getRecords(wrap);

			// Check that we have one record
			if (records.size() == 1)
			{
				nonOver = false;
				ret = records.get(0);
			}
			else
			{
				nonOver = true;
				LinkedList<String> catNos = new LinkedList<String>();
				Iterator<Record> rIt = records.iterator();
				while (rIt.hasNext())
				{
					Record rec = rIt.next();
					catNos.addLast(rec.getCatNoString() + " [" + rec.getNumber() + "]");
				}

				sel.setData(catNos, "Select Catalogue Number");
				int val = catNos.indexOf(sel.getData());
				ret = records.get(val);
			}
		}

		System.err.println("GOT: " + ret);
		return ret;
	}
}
