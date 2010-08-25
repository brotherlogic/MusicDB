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
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.swing.JOptionPane;

import uk.co.brotherlogic.mdb.EntitySelector;
import uk.co.brotherlogic.mdb.RecordSelector;
import uk.co.brotherlogic.mdb.record.GetRecords;
import uk.co.brotherlogic.mdb.record.Record;

public class MakeCDFileOverseer {
	Record outRec;
	File outFile;

	String fileLoc;

	public MakeCDFileOverseer(GetRecords recIn, String fileString) {
		try {
			RecordSelector sel = new RecordSelector();

			// Set the file location
			fileLoc = fileString;

			// Get the record
			outRec = sel.selectRecord(null);

			// Get the output point
			outFile = chooseFile();

			if (outFile != null)
				// And write the information
				writeFile(false);
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "Error in CD Making" + e,
					"Error!", JOptionPane.ERROR_MESSAGE);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null,
					"Error in file selection/writing: " + e, "Error!",
					JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		} catch (NullPointerException e) {
			JOptionPane.showMessageDialog(null, "Can't find server: " + e,
					"Error!", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}

	public MakeCDFileOverseer(Record recIn, File of, boolean no,
			String fileString) throws SQLException {
		try {
			// Set the file location
			fileLoc = fileString;

			// Get the record
			outRec = recIn;

			// Get the output point
			outFile = of;

			if (outFile != null)
				// And write the information
				writeFile(true);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null,
					"Error in file selection/writing: " + e, "Error!",
					JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		} catch (NullPointerException e) {
			JOptionPane.showMessageDialog(null, "Can't find server: " + e,
					"Error!", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}

	}

	// Function to choose the output file directory
	public File chooseFile() throws IOException, NullPointerException {
		// First get the disc-directory on the server
		File musicDir = new File(fileLoc);

		// Search the sub-directories of this
		SortedSet<String> examples = new TreeSet<String>();
		Map<String, String> trans = new TreeMap<String, String>();

		// Construct the correct file
		File sDir = new File(musicDir.getAbsolutePath());

		// Search the sub-directories of this too!
		File[] dirs2 = sDir.listFiles();
		for (File element : dirs2)
			if (element.isDirectory())
				if (!(new File(element, "CDout.txt").exists())) {
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
			ret = new File(trans.get(resultingDir) + File.separator
					+ "CDout.txt");
		else
			ret = null;
		return ret;
	}

	// Function to write the info file
	public boolean writeFile(boolean auto) throws SQLException, IOException {
		// Construct the writer
		PrintWriter w = new PrintWriter(new FileWriter(outFile), true);

		// Print the recordnumber
		w.println(outRec.getNumber());
		// Close the file
		w.close();

		return true;
	}
}
