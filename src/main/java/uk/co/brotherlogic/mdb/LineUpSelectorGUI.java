package uk.co.brotherlogic.mdb;

/**
 * A GUI for creating line ups from data
 * @author Simon Tucker
 */

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

public class LineUpSelectorGUI extends JDialog implements ActionListener,
		CaretListener
{
	// Controller Elements
	DefaultListModel listMod;
	DefaultListModel adListMod;
	Object[] elems;
	List<Artist> objs;
	List<Artist> selectedObjs;
	Groop currentGroop;
	int lineUpNumber;

	// GUI elements
	JTextField textIn = new JTextField();
	JButton buttAdd = new JButton();
	JList listAdded;
	JButton buttRem = new JButton();
	JList listMain;
	JButton butOK = new JButton();
	JButton buttCancel = new JButton();
	JScrollPane jScrollPane1 = new JScrollPane();
	JScrollPane jScrollPane2 = new JScrollPane();
	JComboBox comboNumber = new JComboBox();
	JButton jButton1 = new JButton();
	JButton butAll = new JButton();

	public LineUpSelectorGUI(Groop grp, JFrame refIn)
	{
		// Set up the display and list models
		super(refIn, grp.getSortName(), true);
		currentGroop = grp;

		listMod = new DefaultListModel();
		adListMod = new DefaultListModel();
		setSize(447, 357);

		this.setLocationRelativeTo(refIn);

		// Initialise the Display
		setResizable(false);
		try
		{
			jbInit();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.exit(1);
		}
	}

	public void actionPerformed(ActionEvent e)
	{
		if (e.getActionCommand() == "add")
		{
			if (listMain.getSelectedValue() != null)
			{
				// First remove this item from the main list
				Artist selected = (Artist) listMain.getSelectedValue();
				addElem(selected);
			}
		}
		else if (e.getActionCommand() == "ok")
			this.setVisible(false);
		else if (e.getActionCommand().equalsIgnoreCase("cancel"))
		{
			// Remove all the data and return
			selectedObjs.clear();
			this.setVisible(false);
		}
		else if (e.getActionCommand() == "remove")
		{
			Artist selected = (Artist) listAdded.getSelectedValue();
			remElem(selected);
		}
		else if (e.getActionCommand() == "text")
		{
			// Make sure that we're not adding from the list
			if (listMain.getSelectedValue() != null
					&& textIn.getText().equalsIgnoreCase(
							(listMain.getSelectedValue().toString())))

			{
				// First remove this item from the main list
				Artist selected = (Artist) listMain.getSelectedValue();
				addElem(selected);
			}
			else
				// Add a label by just placing it into the addlist
				addElem(new Artist(textIn.getText(), -1));
		}
		else if (e.getActionCommand() == "all")
		{
			int res = JOptionPane.showConfirmDialog(this, "Are You Sure?",
					"Confirm", JOptionPane.YES_NO_OPTION);

			// Assuming 0 means no
			if (res < 1)
				// Remove all the entries
				while (selectedObjs.size() > 0)
				{
					// Remove the lowest numbered value
					listAdded.setSelectedIndex(0);
					remElem((Artist) listAdded.getSelectedValue());
				}
		}
		else if (e.getActionCommand() == "lineup")
			setForExistLineUp();
		else if (e.getActionCommand() == "newlineup")
			addNewLineup();

	}

	private void addElem(Artist in)
	{
		// Move the name across
		listMod.removeElement(in);
		objs.remove(in);
		adListMod.addElement(in);
		selectedObjs.add(in);

		// Clear the text thingy
		textIn.setText("");
	}

	private void addNewLineup()
	{
		lineUpNumber = -1;
		comboNumber.setEnabled(false);
		buttAdd.setEnabled(true);
		buttRem.setEnabled(true);
		butAll.setEnabled(true);
		textIn.setEnabled(true);
		jButton1.setEnabled(false);

		// Clear the current line up
		while (selectedObjs.size() > 0)
		{
			Artist val = selectedObjs.get(0);
			remElem(val);
		}
	}

	public void caretUpdate(CaretEvent e)
	{
		// Find the closest string alphabetically to this one
		Object close = getClosest(textIn.getText());

		// Set this value in the selected thingy
		listMain.setSelectedValue(close, true);

		// Make sure that the selection is visible
		listMain.ensureIndexIsVisible(Math.min(listMain.getSelectedIndex() + 4,
				objs.size() + 1));
	}

	public void clean()
	{
		super.dispose();

		// Now remove all of the data
		listMod.clear();
		adListMod.clear();
		elems = new Object[0];

		objs.clear();
		selectedObjs.clear();
	}

	public Artist getClosest(String attempt)
	{
		int bPoint = 0;
		int tPoint = objs.size();

		while (tPoint - bPoint > 1)
		{
			int midPoint = (tPoint + bPoint) / 2;
			Artist obj = objs.get(midPoint);
			if (attempt.toLowerCase().compareTo(obj.toString().toLowerCase()) > 0)
				tPoint = midPoint;
			else
				bPoint = midPoint;
		}

		return objs.get(bPoint);
	}

	public LineUp getData()
	{
		if (selectedObjs.size() > 0)
			return new LineUp(lineUpNumber, selectedObjs, currentGroop);
		else
			return null;
	}

	private void jbInit() throws Exception
	{
		textIn.setEnabled(false);
		textIn.setBounds(new Rectangle(8, 45, 173, 27));
		textIn.addCaretListener(this);
		textIn.addActionListener(this);
		textIn.setActionCommand("text");
		this.getContentPane().setLayout(null);
		buttAdd.setText("+");
		buttAdd.setBounds(new Rectangle(188, 45, 70, 30));
		buttAdd.addActionListener(this);
		buttAdd.setEnabled(false);
		buttAdd.setActionCommand("add");
		buttAdd.setMnemonic('A');
		buttRem.setText("-");
		buttRem.setBounds(new Rectangle(188, 84, 70, 30));
		buttRem.addActionListener(this);
		buttRem.setEnabled(false);
		buttRem.setActionCommand("remove");
		listMain = new JList(listMod);
		listAdded = new JList(adListMod);
		butOK.setToolTipText("");
		butOK.setText("OK");
		butOK.setBounds(new Rectangle(362, 288, 70, 30));
		butOK.addActionListener(this);
		butOK.setActionCommand("ok");
		buttCancel.setText("Cancel");
		buttCancel.setBounds(new Rectangle(281, 288, 73, 30));
		buttCancel.addActionListener(this);
		buttCancel.setActionCommand("cancel");
		jScrollPane1.setBounds(new Rectangle(8, 79, 173, 197));
		jScrollPane2.setBounds(new Rectangle(265, 45, 167, 232));
		comboNumber.setBounds(new Rectangle(8, 10, 173, 27));
		comboNumber.addActionListener(this);
		comboNumber.setActionCommand("lineup");
		jButton1.setActionCommand("newlineup");
		jButton1.setText("New Lineup");
		jButton1.setBounds(new Rectangle(265, 10, 167, 27));
		jButton1.addActionListener(this);
		butAll.setActionCommand("all");
		butAll.setMnemonic('L');
		butAll.setText("All");
		butAll.setBounds(new Rectangle(188, 123, 70, 30));
		butAll.setEnabled(false);
		this.getContentPane().add(comboNumber, null);
		this.getContentPane().add(jButton1, null);
		this.getContentPane().add(textIn, null);
		this.getContentPane().add(buttAdd, null);
		this.getContentPane().add(buttRem, null);
		this.getContentPane().add(jScrollPane1, null);
		this.getContentPane().add(jScrollPane2, null);
		this.getContentPane().add(butOK, null);
		this.getContentPane().add(buttCancel, null);
		this.getContentPane().add(butAll, null);
		jScrollPane2.getViewport().add(listAdded, null);
		jScrollPane1.getViewport().add(listMain, null);
		listMain.getSelectionModel().setSelectionMode(
				ListSelectionModel.SINGLE_SELECTION);
		listAdded.getSelectionModel().setSelectionMode(
				ListSelectionModel.SINGLE_SELECTION);
	}

	private void remElem(Artist in)
	{
		adListMod.removeElement(in);
		selectedObjs.remove(in);

		// Find the point at which the val should be added
		Artist near = getClosest(in.toString());
		int nearPoint = listMod.indexOf(near);

		// Add at this point unless nothing was found to be close
		if (nearPoint < 0)
			listMod.addElement(in);
		else
			listMod.add(nearPoint, in);

		// Re-add the data into the list
		objs.add(in);
	}

	public void setData(Collection<Artist> listElems)
	{
		// Prepare for new data
		objs = new LinkedList<Artist>();
		selectedObjs = new LinkedList<Artist>();
		listMod.clear();
		adListMod.clear();

		// Retrieve the relevant data stuff
		elems = listElems.toArray();
		objs.addAll(listElems);
		Collections.sort(objs);

		// Add the data to the listModel
		for (Object elem : elems)
			listMod.addElement(elem);

		if (currentGroop != null && currentGroop.getNumber() > 0)
		{
			// Add the keys into the drop-down box
			comboNumber.removeAllItems();
			Iterator<LineUp> keyIt = currentGroop.getLineUps().iterator();
			LineUp currLineUp;
			while (keyIt.hasNext())
			{
				currLineUp = keyIt.next();
				comboNumber.addItem(new Integer(currLineUp.getLineUpNumber()));
			}

			// Prepare for current line up
			setForExistLineUp();
		}
		else
			addNewLineup();

		start();
	}

	private void setForExistLineUp()
	{
		// Get the selected index number
		lineUpNumber = ((Integer) comboNumber.getSelectedItem()).intValue();

		// Get the lineup from the current line up map
		LineUp lineUp = currentGroop.getLineUp(lineUpNumber);

		// Clear the current line up
		while (selectedObjs.size() > 0)
		{
			Artist val = selectedObjs.get(0);
			remElem(val);
		}

		// Add the new values
		for (Artist art : lineUp.getArtists())
			addElem(art);

	}

	public void start()
	{
		Object close = getClosest("");
		listMain.setSelectedValue(close, true);
		listMain.ensureIndexIsVisible(Math.min(listMain.getSelectedIndex() + 4,
				objs.size() + 1));
	}
}
