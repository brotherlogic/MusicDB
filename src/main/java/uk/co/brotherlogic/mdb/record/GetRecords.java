package uk.co.brotherlogic.mdb.record;

/**
 * Class to deal with getting groops
 * 
 * @author Simon Tucker
 */

import java.awt.Window;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;

import uk.co.brotherlogic.mdb.Artist;
import uk.co.brotherlogic.mdb.EntitySelector;
import uk.co.brotherlogic.mdb.Groop;
import uk.co.brotherlogic.mdb.GetArtists;
import uk.co.brotherlogic.mdb.GetCategories;
import uk.co.brotherlogic.mdb.GetFormats;
import uk.co.brotherlogic.mdb.GetGroops;
import uk.co.brotherlogic.mdb.GetLabels;
import uk.co.brotherlogic.mdb.Persistent;
import uk.co.brotherlogic.mdb.Track;

public class GetRecords
{

	static Persistent p;

	PreparedStatement addRecord;

	boolean cancelled;

	PreparedStatement getRecord;

	PreparedStatement getTracks;

	// Flag indicating overlap of record titles
	boolean nonOver;

	Map numberToRecords;

	Collection records;
	PreparedStatement updateTrack;

	PreparedStatement updateRecord;

	private static GetRecords singleton;

	private GetRecords() throws SQLException
	{
		// Set the required parameters
		p = Persistent.create();

		// Create the records
		records = new Vector();
		numberToRecords = new TreeMap();

		getTracks = p
				.getConnection()
				.getPreparedStatement(
						"SELECT TrackRefNumber FROM Tracks WHERE RecordNumber = ? AND TrackNumber = ?");
		addRecord = p
				.getConnection()
				.getPreparedStatement(
						"INSERT INTO Records (Title,BoughtDate,Format,Notes,ReleaseYear,Category,Author,ReleaseMonth,ReleaseType, modified,Owner,purchase_price) VALUES (?,?,?,?,?,?,?,?,?,now(),?,?)");
		getRecord = p
				.getConnection()
				.getPreparedStatement(
						"SELECT RecordNumber FROM Records WHERE Title = ? AND BoughtDate = ? AND Format = ? AND Notes = ? ORDER BY RecordNumber DESC");
		updateTrack = p
				.getConnection()
				.getPreparedStatement(
						"UPDATE TRACKS SET TrackName = ?, Length = ? WHERE RecordNumber = ? AND TrackNumber = ?");

		updateRecord = p
				.getConnection()
				.getPreparedStatement(
						"UPDATE Records SET Title = ?, BoughtDate = ?, Format = ?, Notes = ?, ReleaseYear = ?, Category = ?, Author = ?, ReleaseMonth = ?, ReleaseType = ?, modified = now(), owner = ?, purchase_price = ? WHERE RecordNumber = ?");

	}

	public void addGroop(int trackNumber, Groop groop) throws SQLException
	{
		// First get the groop number
		GetGroops.build().addGroop(groop);
		int grpNum = groop.getNumber();

		// Get the lineup number
		int lineUpNum = groop.getChosenLineup().getLineUpNumber();

		// Now add the groop into the line up set
		p.getConnection().runUpdate(
				"INSERT INTO LineUpSet (TrackNumber, LineUpNumber) VALUES ("
						+ trackNumber + "," + lineUpNum + ")");

	}

	public void addGroopsAndPersonnel(int trackNumber, Track toAdd)
			throws SQLException
	{

		// Now do the personnel
		int[] artNums = GetArtists.create().addArtists(toAdd.getPersonnel());

		// Add the entries in the personnel table
		for (int artNum : artNums)
			p.getConnection().runUpdate(
					"INSERT INTO Personnel (ArtistNumber,TrackNumber) VALUES ("
							+ artNum + "," + trackNumber + ")");

		// Now add the groups
		Iterator grIt = toAdd.getGroops().iterator();
		while (grIt.hasNext())
			addGroop(trackNumber, (Groop) grIt.next());
	}

	public void addRecord(Record in) throws SQLException, InterruptedException
	{
		// First get the format number
		int formatNumber = (GetFormats.create().addFormat(in.getFormat(), in
				.getCategory())).getNumber();

		// Re set the format!
		in.getFormat().setNumber(formatNumber);

		// Get the date formatter - AMERICAN DATE FORMAT
		DateFormat amForm = new SimpleDateFormat("MM/dd/yy");
		DateFormat myForm = new SimpleDateFormat("dd/MM/yy");

		// Get tbe category number
		int catNum = GetCategories.build().addCategory(in.getCategory());

		NumberFormat nForm = NumberFormat.getInstance();
		nForm.setMaximumFractionDigits(2);
		nForm.setMinimumFractionDigits(2);

		// Add the record itself
		addRecord.setString(1, in.getTitle());
		addRecord.setDate(2,
				new java.sql.Date(in.getDate().getTime().getTime()));
		addRecord.setInt(3, formatNumber);
		addRecord.setString(4, in.getNotes());
		addRecord.setInt(5, in.getReleaseYear());
		addRecord.setInt(6, catNum);
		addRecord.setString(7, in.getAuthor());
		addRecord.setInt(8, in.getReleaseMonth());
		addRecord.setInt(9, in.getReleaseType());
		addRecord.setInt(10, in.getOwner());
		addRecord.setDouble(11, in.getPrice());
		addRecord.execute();

		getRecord.setString(1, in.getTitle());
		getRecord.setDate(2,
				new java.sql.Date(in.getDate().getTime().getTime()));
		getRecord.setInt(3, formatNumber);
		getRecord.setString(4, in.getNotes());
		ResultSet rs = getRecord.executeQuery();
		rs.next();
		int recordNumber = rs.getInt(1);

		rs.close();

		// Get the label numbers
		int[] labNums = GetLabels.create().addLabels(in.getLabels());
		for (int labNum : labNums)
			// Add the numbers to the label set
			p.getConnection().runUpdate(
					"INSERT INTO LabelSet (RecordNumber,LabelNumber) VALUES ("
							+ recordNumber + "," + labNum + ")");

		// Add the catalogue numbers
		Iterator cIt = in.getCatNos().iterator();
		while (cIt.hasNext())
		{
			String catNo = (String) cIt.next();
			p.getConnection().runUpdate(
					"INSERT INTO CatNoSet (RecordNumber,CatNo) VALUES ("
							+ recordNumber + ",\'" + p.cleanString(catNo)
							+ "\')");
		}

		// Add the tracks
		Iterator tIt = in.getTracks().iterator();
		while (tIt.hasNext())
			addTrack(recordNumber, (Track) tIt.next());

		// save the record
		in.save();

		commitRecord(in);
	}

	public void addTrack(int recordNumber, Track toAdd) throws SQLException
	{
		// First add the track data and get the track number
		p.getConnection().runUpdate(
				"INSERT INTO Tracks (RecordNumber,TrackNumber,TrackName,Length) VALUES ("
						+ recordNumber + "," + toAdd.getTrackNumber() + ",\'"
						+ p.cleanString(toAdd.getTitle()) + "\',"
						+ toAdd.getLengthInSeconds() + ")");

		// Now get that track number
		// Statement s = p.getConnection().getStatement();
		getTracks.setInt(1, recordNumber);
		getTracks.setInt(2, toAdd.getTrackNumber());
		/*
		 * ResultSet rs = s.executeQuery( "SELECT TrackRefNum FROM Tracks WHERE
		 * RecordNumber = " + recordNumber + " AND TrackNumber = " +
		 * toAdd.getTrackNumber());
		 */
		ResultSet rs = getTracks.executeQuery();

		rs.next();
		int trackNumber = rs.getInt(1);

		rs.close();

		addGroopsAndPersonnel(trackNumber, toAdd);
	}

	public void commitRecord(Record in) throws SQLException,
			InterruptedException
	{
		// Commit the label
		GetLabels.create().commitLabels(in.getLabels());

		// Commit the formats
		GetFormats.create().commitFormat(in.getFormat());

		// Commit the artists
		GetArtists.create().commitArtists();

		// Commit each lineup
		GetGroops.build().commitGroops();

		// Commit the category
		GetCategories.build().commitCategory(in.getCategory());

		// Commit the record
		records.add(in);
		numberToRecords.put(new Integer(in.getNumber()), in);

		// Commit the transaction
		p.getConnection().commitTrans();

	}

	public void deleteRecord(Record rec) throws SQLException
	{
		// Delete each of the tracks
		for (int i = 1; i <= rec.getTracks().size(); i++)
		{
			Track track = rec.getTrack(i);
			deleteTrack(rec.getNumber(), track.getTrackNumber(), track
					.getTrackRefNumber());
		}

		// Now delete the cat no set
		p.getConnection().runDelete(
				"DELETE FROM CatNoSet WHERE RecordNumber = " + rec.getNumber());

		// And delete the labelset
		p.getConnection().runDelete(
				"DELETE FROM LabelSet WHERE RecordNumber = " + rec.getNumber());

		p.getConnection().runDelete(
				"DELETE FROM RECORDS WHERE RecordNumber = " + rec.getNumber());

		p.getConnection().commitTrans();
	}

	public void deleteTrack(int recordNumber, int trackNumber,
			int trackRefNumber) throws SQLException
	{

		// First delete rows in the connecting tables
		p.getConnection().runDelete(
				"DELETE FROM LineUpSet WHERE TrackNumber = " + trackRefNumber);
		p.getConnection().runDelete(
				"DELETE FROM Personnel WHERE TrackNumber = " + trackRefNumber);

		// Now delete the actual track
		p.getConnection().runDelete(
				"DELETE FROM Tracks WHERE RecordNumber = " + recordNumber
						+ " AND TrackNumber = " + trackNumber);
	}

	public void execute() throws SQLException
	{
		// Run the query
		Statement s = p.getConnection().getStatement();
		ResultSet rs = s.executeQuery("Select * FROM GetAllRecords");

		// Prepare the objects
		boolean first = true;
		Record currRec = new Record();
		Collection labels = new Vector();
		Collection catnos = new Vector();
		int currNum = -1;
		String currLabel = "";

		while (rs.next())
		{
			// Get the record number
			int recNum = rs.getInt(1);

			if (recNum != currNum)
			{
				// New record
				if (!first)
				{
					// Add the record
					currRec.setLabels(labels);
					currRec.setCatNos(catnos);
					records.add(currRec);
					numberToRecords.put(new Integer(currRec.getNumber()),
							currRec);

				}
				else
					first = false;

				// Create new bits
				currRec = new Record();
				labels = new Vector();
				catnos = new Vector();

				String title = rs.getString(2);

				String boughtdate = rs.getString(3);
				String format = rs.getString(4);
				currRec.setNumber(recNum);
				currRec.setTitle(title);
				try
				{
					currRec.setDate(boughtdate);
				}
				catch (ParseException e)
				{
					// Cheeky!
					throw new SQLException("ERROR IN DATE PARSING");
				}
				currRec.setFormat(GetFormats.create().getFormat(format));
				currRec.setTracks(getTracks(recNum));

				String label = rs.getString(5);
				String catno = rs.getString(6);
				labels.add(label);
				catnos.add(catno);
				currLabel = label;
				currNum = recNum;
			}
			else
			{
				String catNo = rs.getString(6);
				String lab = rs.getString(5);
				labels.add(lab);
				catnos.add(catNo);
			}
		}

		if (!cancelled)
		{
			currRec.setLabels(labels);
			currRec.setCatNos(catnos);
			records.add(currRec);
			numberToRecords.put(new Integer(currRec.getNumber()), currRec);
		}

		// Close the statements
		rs.close();
		s.close();

	}

	public Set getCatNos(int recNumber) throws SQLException
	{
		Statement s = p.getConnection().getStatement();
		ResultSet rs = s
				.executeQuery("SELECT CatNo FROM CatNoSet WHERE RecordNumber = "
						+ recNumber);

		Set retSet = new TreeSet();
		while (rs.next())
			retSet.add(rs.getString(1));

		return retSet;
	}

	public Collection<Artist> getCompilers(Record rec) throws SQLException
	{
		Collection<Artist> artists = new LinkedList<Artist>();
		String sql = "SELECT artist_id FROM compiler where record_id = ?";
		PreparedStatement ps = p.getConnection().getPreparedStatement(sql);
		ps.setInt(1, rec.getNumber());
		ps.execute();
		ResultSet rs = ps.getResultSet();
		while (rs.next())
			artists.add(GetArtists.create().getArtist(rs.getInt(1)));
		return artists;
	}

	public Set getGroops(int trackNumber) throws SQLException
	{
		System.out.println("get_groops " + trackNumber);
		// Prepare the set to be returned
		Set retSet = new TreeSet();

		Statement s = p.getConnection().getStatement();
		ResultSet rs = s
				.executeQuery("SELECT LineUpSet.LineUpNumber,Groops.GroopNumber FROM LineUpSet,Groops,LineUp WHERE LineUp.LineUpNumber = LineUpSet.LineUpNumber AND LineUp.GroopNumber = Groops.GroopNumber AND TrackNumber = "
						+ trackNumber);

		// Process this query
		while (rs.next())
		{
			// Get the line up number and groop name
			int lineUpNumber = rs.getInt(1);
			int groopNumber = rs.getInt(2);

			System.out.println("building lineup " + lineUpNumber);

			Groop tempGroop = GetGroops.build().getSingleGroop(groopNumber);
			tempGroop.setChosenLineup(tempGroop.getLineUp(lineUpNumber));
			retSet.add(tempGroop);
		}

		return retSet;
	}

	public Set getLabels(int recNumber) throws SQLException
	{
		Statement s = p.getConnection().getStatement();
		ResultSet rs = s
				.executeQuery("SELECT LabelName FROM Labels,LabelSet WHERE Labels.LabelNumber = LabelSet.LabelNumber AND RecordNumber = "
						+ recNumber);

		Set retSet = new TreeSet();
		while (rs.next())
			retSet.add(GetLabels.create().getLabel(rs.getString(1)));

		return retSet;
	}

	public Map getMap()
	{
		return numberToRecords;
	}

	public boolean getMyState()
	{
		return nonOver;
	}

	public Set getPersonnel(int trackNumber) throws SQLException
	{
		System.out.println("Getting personnel for track " + trackNumber);
		Set retSet = new TreeSet();

		// Set the parameter
		Statement s = p.getConnection().getStatement();
		ResultSet rs = s
				.executeQuery("SELECT Personnel.TrackNumber, Artists.sort_name FROM Artists INNER JOIN Personnel ON Artists.artist_id = Personnel.ArtistNumber WHERE (((Personnel.TrackNumber)="
						+ trackNumber + "))");

		System.out.println("Ran query");

		while (rs.next())
			retSet.add(GetArtists.create().getArtist(rs.getString(2)));

		rs.close();
		s.close();

		return retSet;
	}

	public Record getRecord(int recNumber) throws SQLException
	{
		Record rec = null;
		try
		{
			if (numberToRecords.keySet().contains(new Integer(recNumber)))
				rec = (Record) numberToRecords.get(new Integer(recNumber));
			else
			{
				// Get the single record
				rec = getSingleRecord(recNumber);

				// Add this to the map
				numberToRecords.put(new Integer(recNumber), rec);
				// rec = getSingleRecord2(recNumber);
			}
		}
		catch (ParseException e)
		{
			e.printStackTrace();
		}

		return rec;
	}

	public Collection getRecordNumbers() throws SQLException
	{
		// Use a tree set to keep things in order
		Set titleSet = new TreeSet();

		// Collect the titles
		Statement s = p.getConnection().getStatement();
		ResultSet rs = s.executeQuery("SELECT RecordNumber FROM Records");
		while (rs.next())
			titleSet.add(new Integer(rs.getInt(1)));
		rs.close();
		s.close();

		// Return the collection
		return titleSet;
	}

	public Collection getRecordNumbersWithoutAuthors() throws SQLException
	{
		// Use a tree set to keep things in order
		Set titleSet = new TreeSet();

		// Collect the titles
		Statement s = p.getConnection().getStatement();
		ResultSet rs = s
				.executeQuery("SELECT RecordNumber FROM Records WHERE Author is null");
		while (rs.next())
			titleSet.add(new Integer(rs.getInt(1)));
		rs.close();
		s.close();

		// Return the collection
		return titleSet;
	}

	public List getRecords(String title) throws SQLException
	{
		Collection numbers = new Vector();
		List records = new Vector();

		// First generate a list of all the record numbers with this title
		Statement s = p.getConnection().getStatement();
		ResultSet rs = s
				.executeQuery("SELECT RecordNumber FROM Records WHERE Title = '"
						+ p.cleanString(title) + "'");
		while (rs.next())
			numbers.add(new Integer(rs.getInt(1)));
		rs.close();
		s.close();

		// Now get all the records for these numbers
		Iterator lIt = numbers.iterator();
		while (lIt.hasNext())
			records.add(getRecord(((Integer) lIt.next()).intValue()));
		return records;
	}

	public Collection getRecordTitles() throws SQLException
	{
		// Use a tree set to keep things in order
		Set titleSet = new TreeSet();

		// Collect the titles
		Statement s = p.getConnection().getStatement();
		ResultSet rs = s.executeQuery("SELECT Title FROM Records");
		while (rs.next())
			titleSet.add(rs.getString(1));
		rs.close();
		s.close();

		// Return the collection
		return titleSet;
	}

	public Record getSingleRecord(int recNumber) throws SQLException,
			ParseException
	{
		System.out.println("Getting: " + recNumber);

		// Run the query
		Statement s = p.getConnection().getStatement();
		ResultSet rs = s
				.executeQuery("Select Title, BoughtDate, Notes, ReleaseYear, FormatName, CategoryName,ReleaseMonth,ReleaseType,Author, Owner, purchase_price FROM Records, Categories, Formats WHERE Categories.CategoryNumber = Records.Category AND Formats.FormatNumber = Records.Format AND RecordNumber = "
						+ recNumber);

		// Prepare the objects
		Record currRec = new Record();

		// Move the pointer on
		if (rs.next())
		{

			String title = rs.getString(1);
			String boughtdate = rs.getString(2);
			String format = rs.getString(5);
			String notes = rs.getString(3);
			int year = rs.getInt(4);
			String category = rs.getString(6);
			int month = rs.getInt(7);
			int type = rs.getInt(8);
			String aut = rs.getString(9);
			int own = rs.getInt(10);
			double price = rs.getDouble(11);

			currRec.setNumber(recNumber);
			currRec.setTitle(title);
			currRec.setDate(boughtdate);
			currRec.setNotes(notes);
			currRec.setYear(year);
			currRec.setReleaseMonth(month);
			currRec.setReleaseType(type);
			currRec.setAuthor(aut);
			currRec.setOwner(own);
			currRec.setPrice(price);

			currRec.setFormat(GetFormats.create().getFormat(format));
			currRec.setTracks(getTracks(recNumber));
			currRec
					.setCategory(GetCategories.build()
							.getCategory(category, -1));
			currRec.setLabels(getLabels(recNumber));
			currRec.setCatNos(getCatNos(recNumber));

			// Return this record
			return currRec;
		}
		else
			return null;

	}

	public Set getTracks(int recNumber) throws SQLException
	{
		Set retSet = new TreeSet();

		// First Build the bare track details
		Statement s = p.getConnection().getStatement();
		ResultSet rs = s
				.executeQuery("SELECT TrackRefNumber, TrackName, Length, TrackNumber FROM Tracks  WHERE RecordNumber ="
						+ recNumber + " ORDER BY TrackNumber");
		// Naive approach to check for spped
		Track currTrack;
		while (rs.next())
		{
			int trckNum = rs.getInt(4);

			System.out.println("Getting track: " + trckNum);

			// Create new track
			String name = rs.getString(2);
			if (name == null)
				name = "";
			int len = rs.getInt(3);
			int refNum = rs.getInt(1);
			currTrack = new Track();
			currTrack.setTitle(name);
			currTrack.setLengthInSeconds(len);
			currTrack.setTrackNumber(trckNum);
			currTrack.setTrackRefNumber(refNum);

			currTrack.setPersonnel(getPersonnel(refNum));
			currTrack.setGroops(getGroops(refNum));

			retSet.add(currTrack);
		}
		rs.close();
		s.close();

		return retSet;
	}

	public Collection getTrackTitles() throws SQLException
	{
		List lis = new LinkedList();

		// Set the parameter
		Statement s = p.getConnection().getStatement();
		ResultSet rs = s.executeQuery("SELECT DISTINCT TrackName FROM Tracks");

		while (rs.next())
			lis.add(rs.getString(1));

		rs.close();
		s.close();

		return lis;

	}

	public void saveCompilers(Record record) throws SQLException
	{
		// Delete the current compilers
		String delSQL = "DELETE FROM compiler WHERE record_id = ?";
		PreparedStatement dps = p.getConnection().getPreparedStatement(delSQL);
		dps.setInt(1, record.getNumber());
		dps.execute();

		// Add the current compilers
		String addSQL = "INSERT INTO compiler (record_id, artist_id) VALUES (?,?)";
		PreparedStatement aps = p.getConnection().getPreparedStatement(addSQL);

		// Ensure the artists are added
		GetArtists.create().addArtists(record.getCompilers());

		// Add the compiler details
		for (Artist compiler : record.getCompilers())
		{
			aps.clearParameters();
			aps.setInt(1, record.getNumber());
			aps.setInt(2, compiler.getId());
			aps.addBatch();
		}

		aps.executeBatch();
		System.err.println("Saved compilers");
	}

	public Record selectRecord(Window owner) throws SQLException
	{
		// Set the state accordingly
		nonOver = false;

		// The record to return
		Record ret;

		// First get a list of all the record titles
		Collection titles = getRecordTitles();

		// Now build a chooser to select this record
		EntitySelector sel = new EntitySelector(owner);
		sel.setData(titles, "Select Record");
		Object[] wrap = (Object[]) sel.getData();

		if (wrap == null || wrap.length == 0)
			ret = null;
		else
		{
			// Now get the record associated with this
			List records = getRecords((String) wrap[0]);

			// Check that we have one record
			if (records.size() == 1)
			{
				nonOver = false;
				ret = (Record) records.get(0);
			}
			else
			{
				nonOver = true;
				LinkedList catNos = new LinkedList();
				Iterator rIt = records.iterator();
				while (rIt.hasNext())
				{
					Record rec = (Record) rIt.next();
					catNos.addLast(rec.getCatNoString() + " ["
							+ rec.getNumber() + "]");
				}

				sel.setData(catNos, "Select Catalogue Number");
				int val = catNos.indexOf(((Object[]) sel.getData())[0]);
				ret = (Record) records.get(val);
			}
		}
		return ret;
	}

	public void updateRecord(Record in) throws SQLException,
			InterruptedException
	{
		// First get the format number
		int formatNumber = GetFormats.create().addFormat(in.getFormat(),
				in.getCategory()).getNumber();

		// Get the date formatter - AMERICAN DATE FORMAT
		DateFormat amForm = new SimpleDateFormat("MM/dd/yy");
		DateFormat myForm = new SimpleDateFormat("dd/MM/yy");

		// Get the new category number
		int catNum = GetCategories.build().addCategory(in.getCategory());

		// Add the record itself
		updateRecord.setString(1, in.getTitle());
		updateRecord.setDate(2, new java.sql.Date(in.getDate().getTime()
				.getTime()));
		updateRecord.setInt(3, formatNumber);
		updateRecord.setString(4, in.getNotes());
		updateRecord.setInt(5, in.getReleaseYear());
		updateRecord.setInt(6, catNum);
		updateRecord.setString(7, in.getAuthor());
		updateRecord.setInt(8, in.getReleaseMonth());
		updateRecord.setInt(9, in.getReleaseType());
		updateRecord.setInt(10, in.getOwner());
		updateRecord.setDouble(11, in.getPrice());

		updateRecord.setInt(12, in.getNumber());

		updateRecord.execute();
		int recordNumber = in.getNumber();

		// Delete the label numbers
		p.getConnection().runDelete(
				"DELETE FROM LabelSet WHERE RecordNumber = " + recordNumber);

		// Get the label numbers
		int[] labNums = GetLabels.create().addLabels(in.getLabels());
		for (int labNum : labNums)
			// Add the numbers to the label set
			p.getConnection().runUpdate(
					"INSERT INTO LabelSet (RecordNumber,LabelNumber) VALUES ("
							+ recordNumber + "," + labNum + ")");

		// Delete the catalogue numbers
		p.getConnection().runDelete(
				"DELETE FROM CatNoSet WHERE RecordNumber = " + recordNumber);

		// Add the catalogue numbers
		Iterator cIt = in.getCatNos().iterator();
		while (cIt.hasNext())
		{
			Object o = cIt.next();
			String catNo = (String) o;
			p.getConnection().runUpdate(
					"INSERT INTO CatNoSet (RecordNumber,CatNo) VALUES ("
							+ recordNumber + ",\'" + p.cleanString(catNo)
							+ "\')");
		}

		// Get the other number of tracks
		Collection otherTracks = getTracks(in.getNumber());

		if (otherTracks.size() < in.getTracks().size())
		{
			// We need to add tracks here
			Iterator tIt = in.getTracks().iterator();
			while (tIt.hasNext())
			{
				Track toDeal = (Track) tIt.next();

				if (toDeal.getTrackNumber() > otherTracks.size())
					addTrack(in.getNumber(), toDeal);
				else
					updateTrack(in.getNumber(), toDeal);
			}

		}
		else if (otherTracks.size() > in.getTracks().size())
		{

			// First delete the tracks
			Iterator otIt = otherTracks.iterator();
			while (otIt.hasNext())
			{
				Track currTrack = (Track) otIt.next();
				if (currTrack.getTrackNumber() > in.getTracks().size())
					deleteTrack(in.getNumber(), currTrack.getTrackNumber(),
							currTrack.getTrackRefNumber());
			}

			// Update the rest
			Iterator tIt = in.getTracks().iterator();
			while (tIt.hasNext())
				updateTrack(recordNumber, (Track) tIt.next());

		}
		else
		{
			// Just add the tracks
			Iterator tIt = in.getTracks().iterator();
			while (tIt.hasNext())
				updateTrack(recordNumber, (Track) tIt.next());
		}

		in.save();
		commitRecord(in);
	}

	public void updateTrack(int recordNumber, Track newTrack)
			throws SQLException
	{
		// SAFE to assume that this track will exist permantly - so set the
		// update parameters
		updateTrack.setString(1, newTrack.getTitle());
		updateTrack.setInt(2, newTrack.getLengthInSeconds());
		updateTrack.setInt(3, recordNumber);
		updateTrack.setInt(4, newTrack.getTrackNumber());

		// Run the update
		updateTrack.execute();

		// Now get the track reference number
		Statement s = p.getConnection().getStatement();
		ResultSet rs = s
				.executeQuery("SELECT TrackRefNumber FROM Tracks WHERE RecordNumber = "
						+ recordNumber
						+ " AND TrackNumber = "
						+ newTrack.getTrackNumber());

		// Move on the result set and collect the reference number
		rs.next();
		int refNum = rs.getInt(1);

		rs.close();
		s.close();

		// Now update the groops and personnel - just delete these
		p.getConnection().runDelete(
				"DELETE FROM Personnel WHERE TrackNumber = " + refNum);
		p.getConnection().runDelete(
				"DELETE FROM LineUpSet WHERE TrackNumber = " + refNum);

		// Now add the new data
		addGroopsAndPersonnel(refNum, newTrack);

	}

	public static GetRecords create() throws SQLException
	{
		if (singleton == null)
			singleton = new GetRecords();
		return singleton;
	}
}