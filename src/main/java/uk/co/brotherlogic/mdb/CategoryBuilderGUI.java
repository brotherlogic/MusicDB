package uk.co.brotherlogic.mdb;

/**
 * A GUI for creating line ups from data
 * @author Simon Tucker
 */

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import uk.co.brotherlogic.mdb.categories.Category;
import uk.co.brotherlogic.mdb.format.Format;

public class CategoryBuilderGUI extends JDialog implements ActionListener {
	// MP3 Codes are at end of file

	// Controller Elements
	DefaultListModel listMod;
	DefaultListModel adListMod;
	Object[] elems;
	SortedSet<Category> objs;
	SortedSet<Category> selectObjs;

	// THe format data
	Vector<Format> combo;
	boolean cancelled = false;

	// GUI elements
	JButton butAdd = new JButton();
	JList listAdded;
	JButton butRem = new JButton();
	JList listMain;
	JScrollPane jScrollPane1 = new JScrollPane();
	JScrollPane jScrollPane2 = new JScrollPane();
	JComboBox comboNumber = new JComboBox();
	GridBagLayout gridBagLayout1 = new GridBagLayout();
	JButton butCancel = new JButton();
	JButton butSame = new JButton();
	JButton butOK = new JButton();
	JButton butNew = new JButton();

	Map<String, Integer> MP3Codes;

	String[] MP3s = new String[] { "Blues", "Classic Rock", "Country", "Dance",
			"Disco", "Funk", "Grunge", "Hip-Hop", "Jazz", "Metal", "New Age",
			"Oldies", "Other", "Pop", "R&B", "Rap", "Reggae", "Rock", "Techno",
			"Industrial", "Alternative", "Ska", "Death Metal", "Pranks",
			"Soundtrack", "Euro-Techno", "Ambient", "Trip-Hop", "Vocal",
			"Jazz+Funk", "Fusion", "Trance", "Classical", "Instrumental",
			"Acid", "House", "Game", "Sound Clip", "Gospel", "Noise",
			"AlternRock", "Bass", "Soul", "Punk", "Space", "Meditative",
			"Instrumental Pop", "Instrumental Rock", "Ethnic", "Gothic",
			"Darkwave", "Techno-Industrial", "Electronic", "Pop-Folk",
			"Eurodance", "Dream", "Southern Rock", "Comedy", "Cult", "Gangsta",
			"Top 40", "Christian Rap", "Pop/Funk", "Jungle", "Native American",
			"Cabaret", "New Wave", "Psychadelic", "Rave", "Showtunes",
			"Trailer", "Lo-Fi", "Tribal", "Acid Punk", "Acid Jazz", "Polka",
			"Retro", "Musical", "Rock & Roll", "Hard Rock", "Folk",
			"Folk-Rock", "National Folk", "Swing", "Fast Fusion", "Bebob",
			"Latin", "Revival", "Celtic", "Bluegrass", "Avantgarde",
			"Gothic Rock", "Progressive Rock", "Psychedelic Rock",
			"Symphonic Rock", "Slow Rock", "Big Band", "Chorus",
			"Easy Listening", "Acoustic", "Humour", "Speech", "Chanson",
			"Opera", "Chamber Music", "Sonata", "Symphony", "Booty Bass",
			"Primus", "Porn Groove", "Satire", "Slow Jam", "Club", "Tango",
			"Samba", "Folklore", "Ballad", "Power Ballad", "Rhythmic Soul",
			"Freestyle", "Duet", "Punk Rock", "Drum Solo", "Acapella",
			"Euro-House", "Dance Hall" };

	public CategoryBuilderGUI(JFrame refIn, Collection<Category> cats,
			Collection<Format> forms) {
		// Set up the display and list models
		super(refIn, "Build Categories", true);

		// Build the MP3 thingy
		buildMP3();

		objs = new TreeSet<Category>(cats);
		selectObjs = new TreeSet<Category>();

		// Set the base size of the window
		setSize(447, 357);

		// Ensure that we have some formats to play with
		if (forms.size() > 0) {
			combo = new Vector<Format>(forms);
			comboNumber = new JComboBox(combo);

			listMod = new DefaultListModel();
			adListMod = new DefaultListModel();

			// Add all the categories
			Iterator<Category> cIt = cats.iterator();
			while (cIt.hasNext())

				listMod.addElement(cIt.next());
		}

		else {
			comboNumber.setEnabled(false);
			butNew.setEnabled(false);
			butAdd.setEnabled(false);
			butRem.setEnabled(false);
			butSame.setEnabled(false);

			// Setup the list models but add no formats/categories
			listMod = new DefaultListModel();
			adListMod = new DefaultListModel();

		}

		// Center the frame on screen
		int xSi = (int) this.getToolkit().getScreenSize().getWidth();
		int ySi = (int) this.getToolkit().getScreenSize().getHeight();
		Dimension d = this.getSize();
		int xCo = (xSi / 2) - d.width / 2;
		int yCo = (ySi / 2) - d.height / 2;
		this.setLocation(xCo, yCo);

		// Initialise the Display
		setResizable(false);

		try {
			jbInit();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand() == "add") {
			if (listMain.getSelectedValue() != null) {
				// First remove this item from the main list
				Category selected = (Category) listMain.getSelectedValue();
				addElem(selected);
			}
		} else if (e.getActionCommand() == "ok")
			this.setVisible(false);
		else if (e.getActionCommand().equalsIgnoreCase("cancel")) {
			cancelled = true;

			// Remove all the data and return
			selectObjs.clear();
			this.setVisible(false);
		} else if (e.getActionCommand() == "remove") {
			Category selected = (Category) listAdded.getSelectedValue();
			if (selected != null)
				remElem(selected);
		} else if (e.getActionCommand() == "all") {
			int res = JOptionPane.showConfirmDialog(this, "Are You Sure?",
					"Confirm", JOptionPane.YES_NO_OPTION);

			// Assuming 0 means no
			if (res < 1)
				// Remove all the entries
				while (selectObjs.size() > 0) {
					// Remove the lowest numbered value
					listAdded.setSelectedIndex(0);
					remElem((Category) listAdded.getSelectedValue());
				}
		} else if (e.getActionCommand() == "newcat") {
			// Get the new category
			Category newCat = makeNewCategory();

			// Add the new category to the category list
			listMod.addElement(newCat);
		}

	}

	private void addElem(Category in) {
		// Move the name across
		listMod.removeElement(in);
		objs.remove(in);
		adListMod.addElement(in);
		selectObjs.add(in);
	}

	private void buildMP3() {
		// Build a tree map for the mp3 genres
		MP3Codes = new TreeMap<String, Integer>();
		for (int i = 0; i < MP3s.length; i++)
			MP3Codes.put(MP3s[i], i);

		// Now sort the array
		Arrays.sort(MP3s);
	}

	public Category getClosest(Category attempt) {
		// Retrieve the tail of the selection set
		SortedSet<Category> temp = objs.tailSet(attempt);

		// Return the relevant item
		if (temp.size() > 0)
			return temp.first();
		else
			return null;
	}

	public Format getFormat() {
		Format retForm = null;

		// Check that cancel wasn't pressed and that formats were available
		if (!cancelled && comboNumber.getItemCount() > 0)
			// Create a new format using the chosen format name and the selected
			// data
			retForm = (Format) comboNumber.getSelectedItem();

		return retForm;
	}

	private void jbInit() throws Exception {
		this.getContentPane().setLayout(gridBagLayout1);
		butAdd.setText("+");
		butAdd.addActionListener(this);
		butAdd.setMaximumSize(new Dimension(75, 30));
		butAdd.setMinimumSize(new Dimension(75, 30));
		butAdd.setPreferredSize(new Dimension(75, 30));
		butAdd.setActionCommand("add");
		butAdd.setMnemonic('A');
		butRem.setText("-");
		butRem.addActionListener(this);
		butRem.setMaximumSize(new Dimension(75, 30));
		butRem.setMinimumSize(new Dimension(75, 30));
		butRem.setPreferredSize(new Dimension(75, 30));
		butRem.setActionCommand("remove");
		listMain = new JList(listMod);
		listAdded = new JList(adListMod);
		comboNumber.addActionListener(this);
		comboNumber.setMaximumSize(new Dimension(31, 30));
		comboNumber.setMinimumSize(new Dimension(31, 30));
		comboNumber.setPreferredSize(new Dimension(31, 30));
		comboNumber.setActionCommand("format");
		butCancel.setMaximumSize(new Dimension(75, 30));
		butCancel.setMinimumSize(new Dimension(75, 30));
		butCancel.setPreferredSize(new Dimension(75, 30));
		butCancel.setText("Cancel");
		butCancel.setActionCommand("cancel");
		butCancel.addActionListener(this);
		butSame.setMaximumSize(new Dimension(75, 30));
		butSame.setMinimumSize(new Dimension(75, 30));
		butSame.setPreferredSize(new Dimension(75, 30));
		butSame.setText("Same");
		butSame.setActionCommand("same");
		butSame.addActionListener(this);
		butOK.setMaximumSize(new Dimension(75, 30));
		butOK.setMinimumSize(new Dimension(75, 30));
		butOK.setPreferredSize(new Dimension(75, 30));
		butOK.setText("OK");
		butOK.setActionCommand("ok");
		butOK.addActionListener(this);
		butNew.setMaximumSize(new Dimension(66, 30));
		butNew.setMinimumSize(new Dimension(66, 30));
		butNew.setPreferredSize(new Dimension(66, 30));
		butNew.setActionCommand("newcat");
		butNew.setText("New Category");
		butNew.addActionListener(this);
		this.getContentPane().add(
				comboNumber,
				new GridBagConstraints(0, 0, 1, 1, 4.0, 0.0,
						GridBagConstraints.CENTER,
						GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5),
						0, 0));
		this.getContentPane().add(
				butAdd,
				new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
						GridBagConstraints.CENTER, GridBagConstraints.NONE,
						new Insets(5, 5, 5, 5), 0, 0));
		this.getContentPane().add(
				butRem,
				new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0,
						GridBagConstraints.NORTH, GridBagConstraints.NONE,
						new Insets(5, 5, 0, 5), 0, 0));
		this.getContentPane().add(
				jScrollPane1,
				new GridBagConstraints(0, 1, 1, 3, 4.0, 1.0,
						GridBagConstraints.CENTER, GridBagConstraints.BOTH,
						new Insets(5, 5, 5, 5), 0, 0));
		jScrollPane1.getViewport().add(listMain, null);
		this.getContentPane().add(
				jScrollPane2,
				new GridBagConstraints(2, 1, 3, 3, 1.0, 1.0,
						GridBagConstraints.CENTER, GridBagConstraints.BOTH,
						new Insets(5, 5, 5, 5), 0, 0));
		this.getContentPane().add(
				butCancel,
				new GridBagConstraints(3, 4, 1, 1, 0.0, 0.0,
						GridBagConstraints.EAST, GridBagConstraints.NONE,
						new Insets(5, 5, 5, 5), 0, 0));
		this.getContentPane().add(
				butSame,
				new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0,
						GridBagConstraints.WEST, GridBagConstraints.NONE,
						new Insets(5, 5, 5, 5), 0, 0));
		this.getContentPane().add(
				butOK,
				new GridBagConstraints(4, 4, 1, 1, 0.0, 0.0,
						GridBagConstraints.EAST, GridBagConstraints.NONE,
						new Insets(5, 5, 5, 5), 0, 0));
		this.getContentPane().add(
				butNew,
				new GridBagConstraints(2, 0, 3, 1, 1.0, 0.0,
						GridBagConstraints.CENTER,
						GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5),
						0, 0));
		jScrollPane2.getViewport().add(listAdded, null);
		listMain.getSelectionModel().setSelectionMode(
				ListSelectionModel.SINGLE_SELECTION);
		listAdded.getSelectionModel().setSelectionMode(
				ListSelectionModel.SINGLE_SELECTION);
	}

	public Category makeNewCategory() {
		Category ret = null;

		// Bring up a text box to collect the new category
		String newCat = JOptionPane.showInputDialog(this,
				"Enter The New Category");

		// Get the MP3 Code
		String MP3Num = (String) JOptionPane.showInputDialog(this,
				"Select The MP3 Genre", "MP3 Genre",
				JOptionPane.QUESTION_MESSAGE, null, MP3s, MP3s[0]);

		// Collect the MP3 Code
		if (!newCat.equalsIgnoreCase("") && !MP3Num.equals(""))
			// Add the new category to the category list
			ret = new Category(newCat, -1, (MP3Codes.get(MP3Num)).intValue());

		// Return the new category
		return ret;

	}

	private void remElem(Category in) {
		adListMod.removeElement(in);
		selectObjs.remove(in);

		// Find the point at which the val should be added
		Category near = getClosest(in);
		int nearPoint = listMod.indexOf(near);

		// Add at this point unless nothing was found to be close
		if (nearPoint < 0)
			listMod.addElement(in);
		else
			listMod.add(nearPoint, in);

		// Re-add the data into the list
		objs.add(in);
	}
}
