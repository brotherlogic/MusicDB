package uk.co.brotherlogic.mdb.cdbuilder;

/**
 * Class to construct a suitable CD File for the other machine
 * @author Simon Tucker
 * @date 17/04/03
 */

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.swing.JOptionPane;

import uk.co.brotherlogic.mdb.EntitySelector;
import uk.co.brotherlogic.mdb.Groop;
import uk.co.brotherlogic.mdb.LineUp;
import uk.co.brotherlogic.mdb.Track;
import uk.co.brotherlogic.mdb.TrackChooser;
import uk.co.brotherlogic.mdb.record.GetRecords;
import uk.co.brotherlogic.mdb.record.Record;

public class MakeCDFileOverseer
{
	GetRecords rec;
	Record outRec;
	File outFile;
	File outDir = new File("/usr/share/hancock_multimedia/convert/");
	boolean nonOver;

	// Where the CD files should be stored
	String fileLoc;

	public MakeCDFileOverseer(GetRecords recIn, String fileString)
	{
		try
		{
			// Set the file location
			fileLoc = fileString;

			// Set the get records
			rec = recIn;

			// Get the record
			outRec = recIn.selectRecord(null);
			nonOver = recIn.getMyState();

			// Get the output point
			outFile = chooseFile();

			if (outFile != null)
				// And write the information
				writeFile(false);
		}
		catch (SQLException e)
		{
			JOptionPane.showMessageDialog(null, "Error in CD Making" + e,
					"Error!", JOptionPane.ERROR_MESSAGE);
		}
		catch (IOException e)
		{
			JOptionPane.showMessageDialog(null,
					"Error in file selection/writing: " + e, "Error!",
					JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
			System.exit(1);
		}
		catch (NullPointerException e)
		{
			JOptionPane.showMessageDialog(null, "Can't find server: " + e,
					"Error!", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}

	public MakeCDFileOverseer(Record recIn, File of, boolean no,
			String fileString)
	{
		try
		{
			// Set the file location
			fileLoc = fileString;

			// Get the record
			outRec = recIn;
			nonOver = no;

			// Get the output point
			outFile = of;

			if (outFile != null)
				// And write the information
				writeFile(true);
		}
		catch (IOException e)
		{
			JOptionPane.showMessageDialog(null,
					"Error in file selection/writing: " + e, "Error!",
					JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
			System.exit(1);
		}
		catch (NullPointerException e)
		{
			JOptionPane.showMessageDialog(null, "Can't find server: " + e,
					"Error!", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}

	}

	// Function to choose the output file directory
	public File chooseFile() throws IOException, NullPointerException
	{
		// First get the disc-directory on the server
		File musicDir = new File(fileLoc);

		// Search the sub-directories of this
		SortedSet<String> examples = new TreeSet<String>();
		Map<String, String> trans = new TreeMap<String, String>();

		// Construct the correct file
		File sDir = new File(musicDir.getCanonicalPath() + File.separator
				+ "convert");

		// Search the sub-directories of this too!
		File[] dirs2 = sDir.listFiles();
		for (File element : dirs2)
		{
			examples.add(element.getName());
			trans.put(element.getName(), element.getCanonicalPath());
		}

		// Now choose from this
		EntitySelector select = new EntitySelector(null);
		select.setData(examples);
		String fname = select.getData();

		String resultingDir = null;
		resultingDir = fname;

		File ret;
		if (resultingDir != null)
		{
			ret = new File(trans.get(resultingDir) + File.separator
					+ "CDout.txt");
			outDir = new File(trans.get(resultingDir));
		}
		else
			ret = null;
		return ret;
	}

	/** Copys directory from to the new directory to and tidies up */
	/*
	 * private void copyDir(String from, String to) { long start =
	 * System.currentTimeMillis(); File toCopyFrom = new
	 * File(outDir.getAbsolutePath() + File.separator + from); File toCopyTo =
	 * new File(outDir.getAbsoluteFile() + File.separator + to); if
	 * (!toCopyFrom.exists() || !toCopyTo.exists()) {
	 * System.err.println("Cannot locate: " + toCopyFrom + " => " +
	 * toCopyFrom.exists()); System.err.println("Cannot locate: " + toCopyTo +
	 * " => " + toCopyFrom.exists()); System.exit(1); }
	 * 
	 * // Get the current maximal number int offset = 1; for (String name :
	 * toCopyTo.list()) if (name.toLowerCase().endsWith("mp3") ||
	 * name.toLowerCase().endsWith("wav")) offset++;
	 * 
	 * // Perform the copy operation List<File> lFiles =
	 * Arrays.asList(toCopyFrom.listFiles()); Collections.sort(lFiles);
	 * 
	 * for (File f : lFiles) copyFile(f, toCopyTo, offset++);
	 * 
	 * // Delete the directory toCopyFrom.delete();
	 * 
	 * System.out.println("Time taken: " + ((System.currentTimeMillis() - start)
	 * / 1000.0)); }
	 * 
	 * private void copyFile(File toCopy, File outputDir, int num) { // Sort out
	 * the track number String numStr = "" + num; if (num < 10) numStr = "0" +
	 * numStr;
	 * 
	 * File outputFile = new File(outputDir.getAbsolutePath() + File.separator +
	 * numStr + "-Track_" + numStr +
	 * toCopy.getName().substring(toCopy.getName().length() - 4));
	 * 
	 * System.out.println(num + " Copying " + toCopy + " to " + outputFile); try
	 * { InputStream is = new BufferedInputStream( new FileInputStream(toCopy));
	 * OutputStream os = new BufferedOutputStream(new FileOutputStream(
	 * outputFile));
	 * 
	 * byte[] barr = new byte[1024]; int read = is.read(barr); while (read > 0)
	 * { os.write(barr, 0, read); read = is.read(barr); } os.close();
	 * is.close();
	 * 
	 * // Now delete the last file toCopy.delete(); } catch (IOException e) {
	 * e.printStackTrace(); System.exit(1); }
	 * 
	 * }
	 */

	// Function to write the info file
	public boolean writeFile(boolean auto) throws IOException
	{

		// Check that the number of tracks is equal to the number of files
		int noFiles = outDir.listFiles().length;

		TrackChooser track = new TrackChooser(null, outRec.getTracks());
		if (!auto && noFiles != outRec.getNoTracks())
			// Build a viewer to deal with the tracks
			track.setVisible(true);
		else
			track.doTracks();

		// Get the data
		LinkedList<LinkedList<Track>> trackData = track.getTrackData();

		// Construct the writer
		PrintWriter w = new PrintWriter(new FileWriter(outFile), true);

		// Get the groop string
		String groop = outRec.getGroopString();

		if (nonOver)
			w.println(groop + "~" + outRec.getTitleWithCat());
		else
			w.println(groop + "~" + outRec.getTitle());

		// Print out the year
		if (outRec.getYear() > 0)
			w.println(outRec.getYear());
		else
			w.println("1999");

		// Print the genre
		w.println(outRec.getGenre());

		// Print the recordnumber
		w.println(outRec.getNumber());

		// Now write the track information
		for (int i = 0; i < trackData.size(); i++)
		{
			// Build the collection of groops and the track name
			Collection<Groop> groops = new TreeSet<Groop>();
			String trackName = "";
			String trackNumber = "";

			// Get the vector
			LinkedList<Track> currVec = trackData.get(i);

			// Work each track number
			Iterator<Track> vIt = currVec.iterator();
			while (vIt.hasNext())
			{
				Track currTrack = vIt.next();

				// Add the groops if it's not already there
				Iterator<LineUp> grpIt = currTrack.getLineUps().iterator();
				while (grpIt.hasNext())
				{
					Groop currGroop = grpIt.next().getGroop();
					if (!groops.contains(currGroop))
						groops.add(currGroop);
				}

				// And build the track title
				trackName += currTrack.getTitle() + " - ";
				trackNumber += currTrack.getTrackNumber() + ",";
			}

			// Remove the trailing | from the title
			trackName = trackName.substring(0, trackName.length() - 3);
			trackNumber = trackNumber.substring(0, trackNumber.length() - 1);

			// Construct the groop string
			String grps = "";
			Iterator<Groop> grIt = groops.iterator();
			while (grIt.hasNext())
				grps += grIt.next().getTidyName() + " & ";
			grps = grps.substring(0, grps.length() - 3);

			w.println((i + 1) + "~" + grps + "~" + trackNumber + "~"
					+ trackName);
		}

		// Return the error value
		boolean retVal = w.checkError();

		// Close the file
		w.close();

		return retVal;
	}
}
