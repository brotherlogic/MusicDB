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
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Calendar;
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
import uk.co.brotherlogic.mdb.GetArtists;
import uk.co.brotherlogic.mdb.GetCategories;
import uk.co.brotherlogic.mdb.GetLabels;
import uk.co.brotherlogic.mdb.Label;
import uk.co.brotherlogic.mdb.LineUp;
import uk.co.brotherlogic.mdb.Persistent;
import uk.co.brotherlogic.mdb.Track;
import uk.co.brotherlogic.mdb.format.GetFormats;
import uk.co.brotherlogic.mdb.groop.GetGroops;
import uk.co.brotherlogic.mdb.groop.Groop;

public class GetRecords
{

	static Persistent p;

	PreparedStatement addRecord;

	PreparedStatement getRecord;

	PreparedStatement getTracks;

	// Flag indicating overlap of record titles
	boolean nonOver;

	Map<Integer, Record> numberToRecords;

	Collection<Record> records;
	PreparedStatement updateTrack;
	PreparedStatement updateRecord;
	PreparedStatement getPersonnel;

	private static GetRecords singleton;

	private GetRecords() throws SQLException
	{
		// Set the required parameters
		p = Persistent.create();

		// Create the records
		records = new Vector<Record>();
		numberToRecords = new TreeMap<Integer, Record>();

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
						"UPDATE Records SET Title = ?, BoughtDate = ?, Format = ?, Notes = ?, ReleaseYear = ?, Category = ?, Author = ?, ReleaseMonth = ?, ReleaseType = ?, modified = now(), owner = ?, purchase_price = ?, shelfpos = ? WHERE RecordNumber = ?");

		getPersonnel = p
				.getConnection()
				.getPreparedStatement(
						"SELECT Personnel.TrackNumber, Artists.sort_name FROM Artists INNER JOIN Personnel ON Artists.artist_id = Personnel.ArtistNumber WHERE (((Personnel.TrackNumber)=?))");

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
		Iterator<LineUp> grIt = toAdd.getLineUps().iterator();
		while (grIt.hasNext())
			addLineUp(trackNumber, grIt.next());
	}

	public void addLineUp(int trackNumber, LineUp lineup) throws SQLException
	{
		// First get the groop number
		GetGroops.build().addLineUp(lineup);

		// Get the lineup number
		int lineUpNum = lineup.getLineUpNumber();

		// Now add the groop into the line up set
		p.getConnection().runUpdate(
				"INSERT INTO LineUpSet (TrackNumber, LineUpNumber) VALUES ("
						+ trackNumber + "," + lineUpNum + ")");

	}

	public void addRecord(Record in) throws SQLException, InterruptedException
	{
		// First get the format number
		int formatNumber = in.getFormat().save();

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
		Iterator<String> cIt = in.getCatNos().iterator();
		while (cIt.hasNext())
		{
			String catNo = cIt.next();
			p.getConnection().runUpdate(
					"INSERT INTO CatNoSet (RecordNumber,CatNo) VALUES ("
							+ recordNumber + ",\'" + p.cleanString(catNo)
							+ "\')");
		}

		// Add the tracks
		Iterator<Track> tIt = in.getTracks().iterator();
		while (tIt.hasNext())
			addTrack(recordNumber, tIt.next());

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

		// Commit the artists
		GetArtists.create().commitArtists();

		// Commit each lineup
		GetGroops.build().commitGroops();

		// Commit the category
		GetCategories.build().commitCategory(in.getCategory());

		// Commit the record
		records.add(in);
		numberToRecords.put(in.getNumber(), in);

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

	public Set<String> getCatNos(int recNumber) throws SQLException
	{
		Statement s = p.getConnection().getStatement();
		ResultSet rs = s
				.executeQuery("SELECT CatNo FROM CatNoSet WHERE RecordNumber = "
						+ recNumber);

		Set<String> retSet = new TreeSet<String>();
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

	public Set<Label> getLabels(int recNumber) throws SQLException
	{
		Statement s = p.getConnection().getStatement();
		ResultSet rs = s
				.executeQuery("SELECT LabelName FROM Labels,LabelSet WHERE Labels.LabelNumber = LabelSet.LabelNumber AND RecordNumber = "
						+ recNumber);

		Set<Label> retSet = new TreeSet<Label>();
		while (rs.next())
			retSet.add(GetLabels.create().getLabel(rs.getString(1)));

		return retSet;
	}

	public Set<LineUp> getLineUps(int trackNumber) throws SQLException
	{
		// Prepare the set to be returned
		Set<LineUp> retSet = new TreeSet<LineUp>();

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

			Groop tempGroop = GetGroops.build().getSingleGroop(groopNumber);
			retSet.add(tempGroop.getLineUp(lineUpNumber));
		}

		return retSet;
	}

	public Map<Integer, Record> getMap()
	{
		return numberToRecords;
	}

	public boolean getMyState()
	{
		return nonOver;
	}

	public Set<Artist> getPersonnel(int trackNumber) throws SQLException
	{
		Set<Artist> retSet = new TreeSet<Artist>();

		// Set the parameter
		getPersonnel.setInt(1, trackNumber);
		ResultSet rs = getPersonnel.executeQuery();

		while (rs.next())
			retSet.add(GetArtists.create().getArtist(rs.getString(2)));

		rs.close();

		return retSet;
	}

	public Record getRecord(int recNumber) throws SQLException
	{
		Record rec = null;
		try
		{
			if (numberToRecords.keySet().contains(recNumber))
				rec = numberToRecords.get(recNumber);
			else
			{
				// Get the single record
				rec = getSingleRecord(recNumber);

				// Add this to the map
				numberToRecords.put(recNumber, rec);
				// rec = getSingleRecord2(recNumber);
			}
		}
		catch (ParseException e)
		{
			e.printStackTrace();
		}

		return rec;
	}

	public Collection<Integer> getRecordNumbers() throws SQLException
	{
		// Use a tree set to keep things in order
		Set<Integer> titleSet = new TreeSet<Integer>();

		// Collect the titles
		Statement s = p.getConnection().getStatement();
		ResultSet rs = s.executeQuery("SELECT RecordNumber FROM Records");
		while (rs.next())
			titleSet.add(rs.getInt(1));
		rs.close();
		s.close();

		// Return the collection
		return titleSet;
	}

	public Collection<Integer> getRecordNumbersWithoutAuthors()
			throws SQLException
	{
		// Use a tree set to keep things in order
		Set<Integer> titleSet = new TreeSet<Integer>();

		// Collect the titles
		Statement s = p.getConnection().getStatement();
		ResultSet rs = s
				.executeQuery("SELECT RecordNumber FROM Records WHERE Author is null");
		while (rs.next())
			titleSet.add(rs.getInt(1));
		rs.close();
		s.close();

		// Return the collection
		return titleSet;
	}

	public List<Record> getRecords(String title) throws SQLException
	{
		Collection<Integer> numbers = new Vector<Integer>();
		List<Record> records = new Vector<Record>();

		// First generate a list of all the record numbers with this title
		Statement s = p.getConnection().getStatement();
		ResultSet rs = s
				.executeQuery("SELECT RecordNumber FROM Records WHERE Title = '"
						+ p.cleanString(title) + "'");
		while (rs.next())
			numbers.add(rs.getInt(1));
		rs.close();
		s.close();

		// Now get all the records for these numbers
		Iterator<Integer> lIt = numbers.iterator();
		while (lIt.hasNext())
			records.add(getRecord((lIt.next()).intValue()));
		return records;
	}

	public Collection<String> getRecordTitles() throws SQLException
	{
		// Use a tree set to keep things in order
		Set<String> titleSet = new TreeSet<String>();

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
		long sTime = System.currentTimeMillis();

		// Run the query
		Statement s = p.getConnection().getStatement();
		ResultSet rs = s
				.executeQuery("Select Title, BoughtDate, Notes, ReleaseYear, Format, CategoryName,ReleaseMonth,ReleaseType,Author, Owner, purchase_price,shelfpos FROM Records, Categories WHERE Categories.CategoryNumber = Records.Category  AND RecordNumber = "
						+ recNumber);

		Record currRec;

		// Move the pointer on
		if (rs.next())
		{

			String title = rs.getString(1);
			Calendar boughtDate = Calendar.getInstance();
			boughtDate.setTimeInMillis(rs.getDate(2).getTime());
			int format = rs.getInt(5);
			String notes = rs.getString(3);
			int year = rs.getInt(4);
			String category = rs.getString(6);
			int month = rs.getInt(7);
			int type = rs.getInt(8);
			String aut = rs.getString(9);
			int own = rs.getInt(10);
			double price = rs.getDouble(11);
			int shelfpos = rs.getInt(12);

			currRec = new Record(title, GetFormats.create().getFormat(format),
					boughtDate, getCatNos(recNumber), getLabels(recNumber),
					getTracks(recNumber), shelfpos);
			currRec.setNumber(recNumber);
			currRec.setNotes(notes);
			currRec.setYear(year);
			currRec.setReleaseMonth(month);
			currRec.setReleaseType(type);
			currRec.setAuthor(aut);
			currRec.setOwner(own);
			currRec.setPrice(price);

			currRec
					.setCategory(GetCategories.build()
							.getCategory(category, -1));

			System.err.println("TIME = " + (System.currentTimeMillis() - sTime)
					/ 1000.0);

			// Return this record
			return currRec;
		}
		else
			return null;

	}

	public Set<Track> getTracks(int recNumber) throws SQLException
	{
		Set<Track> retSet = new TreeSet<Track>();

		// First Build the bare track details
		Statement s = p.getConnection().getStatement();
		ResultSet rs = s
				.executeQuery("SELECT TrackRefNumber, TrackName, Length, TrackNumber FROM Tracks  WHERE RecordNumber ="
						+ recNumber + " ORDER BY TrackNumber");
		// Naive approach to check for spped
		Track currTrack;
		long cTime = 0;
		while (rs.next())
		{
			int trckNum = rs.getInt(4);

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

			long sTime = System.currentTimeMillis();
			currTrack.setPersonnel(getPersonnel(refNum));
			cTime += System.currentTimeMillis() - sTime;
			currTrack.setLineUps(getLineUps(refNum));

			retSet.add(currTrack);
		}
		rs.close();
		s.close();

		System.err.println("TIME = " + cTime / 1000.0);
		return retSet;
	}

	public Collection<String> getTrackTitles() throws SQLException
	{
		List<String> lis = new LinkedList<String>();

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
		Collection<String> titles = getRecordTitles();

		// Now build a chooser to select this record
		EntitySelector sel = new EntitySelector(owner);
		sel.setData(titles, "Select Record");
		String wrap = sel.getData();

		if (wrap == null || wrap.length() == 0)
			ret = null;
		else
		{
			// Now get the record associated with this
			List<Record> records = getRecords(wrap);

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
					catNos.addLast(rec.getCatNoString() + " ["
							+ rec.getNumber() + "]");
				}

				sel.setData(catNos, "Select Catalogue Number");
				int val = catNos.indexOf(sel.getData());
				ret = records.get(val);
			}
		}
		return ret;
	}

	public void updateRecord(Record in) throws SQLException,
			InterruptedException
	{
		// First get the format number
		int formatNumber = in.getFormat().save();

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
		updateRecord.setInt(12, in.getShelfPos());
		updateRecord.setInt(13, in.getNumber());

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
		Iterator<String> cIt = in.getCatNos().iterator();
		while (cIt.hasNext())
		{
			String catNo = cIt.next();
			p.getConnection().runUpdate(
					"INSERT INTO CatNoSet (RecordNumber,CatNo) VALUES ("
							+ recordNumber + ",\'" + p.cleanString(catNo)
							+ "\')");
		}

		// Get the other number of tracks
		Collection<Track> otherTracks = getTracks(in.getNumber());

		if (otherTracks.size() < in.getTracks().size())
		{
			// We need to add tracks here
			Iterator<Track> tIt = in.getTracks().iterator();
			while (tIt.hasNext())
			{
				Track toDeal = tIt.next();

				if (toDeal.getTrackNumber() > otherTracks.size())
					addTrack(in.getNumber(), toDeal);
				else
					updateTrack(in.getNumber(), toDeal);
			}

		}
		else if (otherTracks.size() > in.getTracks().size())
		{

			// First delete the tracks
			Iterator<Track> otIt = otherTracks.iterator();
			while (otIt.hasNext())
			{
				Track currTrack = otIt.next();
				if (currTrack.getTrackNumber() > in.getTracks().size())
					deleteTrack(in.getNumber(), currTrack.getTrackNumber(),
							currTrack.getTrackRefNumber());
			}

			// Update the rest
			Iterator<Track> tIt = in.getTracks().iterator();
			while (tIt.hasNext())
				updateTrack(recordNumber, tIt.next());

		}
		else
		{
			// Just add the tracks
			Iterator<Track> tIt = in.getTracks().iterator();
			while (tIt.hasNext())
				updateTrack(recordNumber, tIt.next());
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

	public static void main(String[] args) throws Exception
	{
		GetRecords.create().getSingleRecord(9811);
	}
}