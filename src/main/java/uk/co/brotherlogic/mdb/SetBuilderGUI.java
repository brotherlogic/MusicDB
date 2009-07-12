package uk.co.brotherlogic.mdb;

/**
 * A GUI for creating sets of data!
 * @author Simon Tucker
 */

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

public class SetBuilderGUI extends JDialog implements ActionListener,
		CaretListener
{
	// Controller Elements
	DefaultListModel listMod;
	DefaultListModel adListMod;

	// The collection of objects
	Object[] elems;
	Collection listElements;
	SortedSet objs;
	SortedSet selectObjs;

	// Current track number
	int trackNumber;
	int maxTrack;

	// GUI Elements
	JTextField textIn = new JTextField();
	JButton buttAdd = new JButton();
	JList listAdded;
	JButton buttRem = new JButton();
	JList listMain;
	JButton butOK = new JButton();
	JButton buttCancel = new JButton();
	JScrollPane jScrollPane1 = new JScrollPane();
	JScrollPane jScrollPane2 = new JScrollPane();
	JButton butAll = new JButton();
	JButton butSame = new JButton();
	JTextField textTrackNumber = new JTextField();

	public SetBuilderGUI(String tit, Frame parent)
	{
		// Set the GUI to be modal
		super(parent, tit, true);

		// Prepare the selector
		listMod = new DefaultListModel();
		adListMod = new DefaultListModel();

		// Prepare the objects
		objs = new TreeSet();
		selectObjs = new TreeSet();

		// Set the size of the component
		setSize(447, 307);

		// Center the frame on screen
		int xSi = (int) this.getToolkit().getScreenSize().getWidth();
		int ySi = (int) this.getToolkit().getScreenSize().getHeight();
		Dimension d = this.getSize();
		int xCo = (xSi / 2) - d.width / 2;
		int yCo = (ySi / 2) - d.height / 2;
		this.setLocation(xCo, yCo);

		// Organise the dialog box
		setResizable(false);
		setTitle(tit);

		// Initialise the gui elements
		try
		{
			jbInit();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void actionPerformed(ActionEvent e)
	{
		if (e.getActionCommand() == "add")
		{
			if (listMain.getSelectedValue() != null)
			{
				// First remove this item from the main list
				String selected = (String) listMain.getSelectedValue();
				addElem(selected);
			}
		}
		else if (e.getActionCommand() == "remove")
		{
			String selected = (String) listAdded.getSelectedValue();
			remElem(selected);
		}
		else if (e.getActionCommand() == "all")
		{
			int res = JOptionPane.showConfirmDialog(this, "Are You Sure?",
					"Confirm", JOptionPane.YES_NO_OPTION);

			// Assuming 0 means no
			if (res < 1)
				// Remove all the entries
				while (selectObjs.size() > 0)
				{
					// Remove the lowest numbered value
					listAdded.setSelectedIndex(0);
					remElem((String) listAdded.getSelectedValue());
				}
		}

		else if (e.getActionCommand() == "ok")
			this.setVisible(false);
		else if (e.getActionCommand() == "cancel")
		{
			this.setVisible(false);
			selectObjs.clear();
		}
		else if (e.getActionCommand() == "text")
			// Add a label by just placing it into the addlist
			addElem(textIn.getText());
		else if (e.getActionCommand() == "same")
			try
			{
				trackNumber = Integer.parseInt(textTrackNumber.getText());

				if (trackNumber > maxTrack || trackNumber < 1)
					JOptionPane.showMessageDialog(this, "Invalid Track Number",
							"Error", JOptionPane.ERROR_MESSAGE);
				else
					this.setVisible(false);
			}
			catch (NumberFormatException ex)
			{
				JOptionPane.showMessageDialog(this, "Invalid Track Number",
						"Error", JOptionPane.ERROR_MESSAGE);
			}
	}

	public void addAllElements(Object[] elems)
	{
		// Add the data to the listModel
		for (Object elem : elems)
			listMod.addElement(elem);
	}

	private void addElem(String in)
	{
		// Remove from one list and add to the other
		listMod.removeElement(in);
		adListMod.addElement(in);

		// Clear the text thingy
		textIn.setText("");
	}

	public void caretUpdate(CaretEvent e)
	{
		// Find the closest string alphabetically to this one
		String close = getClosest(textIn.getText());

		// Set this value in the selected thingy
		listMain.setSelectedValue(close, true);

		// Make sure that the selection is visible
		listMain.ensureIndexIsVisible(Math.min(listMain.getSelectedIndex() + 4,
				objs.size() + 1));
	}

	public void clearLists()
	{
		// Clear the current list models
		listMod.clear();
		adListMod.clear();
	}

	public String getClosest(String attempt)
	{
		// Retrieve the tail of the selection set
		SortedSet temp = objs.tailSet(attempt);

		// Return the relevant item
		if (temp.size() > 0)
			return (String) temp.first();
		else
			return "";

	}

	public Collection getData()
	{
		// Return the data
		return selectObjs;
	}

	public int getTrackNumber()
	{
		// Return the entered track number
		return Integer.parseInt(textTrackNumber.getText());
	}

	private void jbInit() throws Exception
	{
		textIn.setBounds(new Rectangle(6, 7, 173, 27));
		textIn.addCaretListener(this);
		textIn.addActionListener(this);
		textIn.setActionCommand("text");
		this.getContentPane().setLayout(null);
		buttAdd.setText("+");
		buttAdd.setBounds(new Rectangle(186, 7, 70, 30));
		buttAdd.addActionListener(this);
		buttAdd.setActionCommand("add");
		buttAdd.setMnemonic('A');
		buttRem.setText("-");
		buttRem.setBounds(new Rectangle(186, 48, 70, 30));
		buttRem.addActionListener(this);
		buttRem.setActionCommand("remove");
		listMain = new JList(listMod);
		listAdded = new JList(adListMod);
		butOK.setToolTipText("");
		butOK.setText("OK");
		butOK.setBounds(new Rectangle(366, 244, 70, 30));
		butOK.addActionListener(this);
		butOK.setActionCommand("ok");
		buttCancel.setText("Cancel");
		buttCancel.setBounds(new Rectangle(284, 244, 73, 30));
		buttCancel.addActionListener(this);
		buttCancel.setActionCommand("cancel");
		jScrollPane1.setBounds(new Rectangle(6, 41, 173, 197));
		jScrollPane2.setBounds(new Rectangle(264, 7, 167, 232));
		butAll.setActionCommand("all");
		butAll.setMnemonic('L');
		butAll.setText("All");
		butAll.setBounds(new Rectangle(186, 89, 70, 30));
		butAll.addActionListener(this);
		butSame.setEnabled(false);
		butSame.setVisible(false);
		butSame.setActionCommand("same");
		butSame.setText("Same");
		butSame.setBounds(new Rectangle(202, 244, 73, 30));
		butSame.addActionListener(this);
		textTrackNumber.setEnabled(false);
		textTrackNumber.setVisible(false);
		textTrackNumber.setBounds(new Rectangle(7, 247, 73, 30));
		this.getContentPane().add(textIn, null);
		this.getContentPane().add(buttAdd, null);
		this.getContentPane().add(buttRem, null);
		this.getContentPane().add(butOK, null);
		this.getContentPane().add(buttCancel, null);
		this.getContentPane().add(jScrollPane1, null);
		this.getContentPane().add(jScrollPane2, null);
		this.getContentPane().add(butAll, null);
		this.getContentPane().add(butSame, null);
		this.getContentPane().add(textTrackNumber, null);
		jScrollPane2.getViewport().add(listAdded, null);
		jScrollPane1.getViewport().add(listMain, null);
		listMain.getSelectionModel().setSelectionMode(
				ListSelectionModel.SINGLE_SELECTION);
		listAdded.getSelectionModel().setSelectionMode(
				ListSelectionModel.SINGLE_SELECTION);
	}

	private void remElem(String in)
	{
		// Remove the data from the lists and objects
		adListMod.removeElement(in);

		// Find the point at which the val should be added
		String near = getClosest(in);
		int nearPoint = listMod.indexOf(near);

		// Add at this point unless nothing was found to be close
		if (nearPoint < 0)
			listMod.addElement(in);
		else
			listMod.add(nearPoint, in);
	}

	public void setData(Collection listElems, Collection chosen)
	{
		// Clear the current lists
		clearLists();

		// If a list of elements is provided
		if (listElems.size() > 0)
		{
			// Set the lists visible
			setListsVisible(true);

			// Prepare the data
			elems = listElems.toArray();
			objs.addAll(listElems);

			// Add the data to the list
			addAllElements(elems);
		}
		else
			// Remove the main list and just allow adding and removing of unique
			// strings
			setListsVisible(false);

		// Add any pre-chosen elements
		Iterator it = chosen.iterator();
		while (it.hasNext() && chosen.size() > 0)
			addElem((String) it.next());
	}

	public void setData(Collection listElems, Collection chosen, int entTrack,
			int maxTrack)
	{
		// Prepare the form
		setData(listElems, chosen);

		// Set for track
		butSame.setEnabled(true);
		butSame.setVisible(true);
		textTrackNumber.setEnabled(true);
		textTrackNumber.setVisible(true);
		textTrackNumber.setText("" + entTrack);
		this.maxTrack = maxTrack;
	}

	public void setListsVisible(boolean vis)
	{
		listMain.setVisible(vis);
		jScrollPane1.setVisible(vis);
		listMain.setEnabled(vis);
		jScrollPane1.setEnabled(vis);
	}
}