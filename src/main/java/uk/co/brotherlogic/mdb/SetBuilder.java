package uk.co.brotherlogic.mdb;

/**
 * A GUI for creating sets of data!
 * NB: Updated to use solely DatabaseElements
 * @author Simon Tucker
 */

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

public class SetBuilder<X extends Comparable<X>> extends JDialog implements
		ActionListener, CaretListener
{
	int element;

	// Controls whether we can add new elements
	boolean addOnly = false;

	// Controller Elements
	private final DefaultListModel selectFrom;
	private final DefaultListModel selectTo;

	// The collection of objects
	List<X> sortedObjs;
	Collection<X> selectedObjs;
	Collection<X> originalElements;

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
	Builder<X> builder;

	public SetBuilder(String tit, JFrame parent, Builder<X> build)
	{
		// Set the GUI to be modal
		super(parent, tit, true);

		this.builder = build;

		// Prepare the selector
		selectFrom = new DefaultListModel();
		selectTo = new DefaultListModel();

		// Prepare the objects
		sortedObjs = new LinkedList<X>();
		selectedObjs = new LinkedList<X>();

		// Set the size of the component
		setSize(447, 307);

		// Center the frame on the window
		this.setLocationRelativeTo(parent);

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
				@SuppressWarnings("unchecked")
				X selected = (X) listMain.getSelectedValue();
				addElem(selected);
			}
		}
		else if (e.getActionCommand() == "remove")
		{
			@SuppressWarnings("unchecked")
			X selected = (X) listAdded.getSelectedValue();
			remElem(selected);
		}
		else if (e.getActionCommand() == "all")
		{
			// Select all the values
			listAdded.setSelectionInterval(0, selectTo.getSize());
			Object[] objs = listAdded.getSelectedValues();
			for (Object object : objs)
			{
				@SuppressWarnings("unchecked")
				X selected = (X) object;
				remElem(selected);
			}
		}
		else if (e.getActionCommand() == "ok")
		{
			textTrackNumber.setText("0");
			this.setVisible(false);
		}
		else if (e.getActionCommand() == "cancel")
		{
			// Set data to the original
			this.setVisible(false);

			// Set the original objects to null!
			selectedObjs = null;
		}
		else if (e.getActionCommand() == "text")
		{

			if (!addOnly)
				// Make sure that we're not adding from the list
				if (listMain.getSelectedValue() != null
						&& textIn.getText().equalsIgnoreCase(
								(listMain.getSelectedValue().toString())))
				{
					// First remove this item from the main list
					@SuppressWarnings("unchecked")
					X selected = (X) listMain.getSelectedValue();
					addElem(selected);
				}
				else
				{
					X newVer = builder.build(textIn.getText());
					addElem(newVer);
				}
		}
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

	public void addAllElements(Collection<X> elems)
	{
		// Add the data to the listModel
		for (X elem : elems)
			selectFrom.addElement(elem);
	}

	private void addElem(X in)
	{
		if (sortedObjs.contains(in))
		{
			// Remove from one list and add to the other
			selectFrom.removeElement(in);
			sortedObjs.remove(in);
		}

		selectTo.addElement(in);
		selectedObjs.add(in);

		// Clear the text thingy
		textIn.setText("");
	}

	public void caretUpdate(CaretEvent e)
	{
		// Find the closest string alphabetically to this one
		X close = getClosest(textIn.getText());

		// Set this value in the selected thingy - wrap the string to a database
		// element
		listMain.setSelectedValue(close, true);

		// Make sure that the selection is visible
		listMain.ensureIndexIsVisible(Math.min(listMain.getSelectedIndex() + 4,
				sortedObjs.size() + 1));
	}

	public void clean()
	{
		super.dispose();

		// Now remove all of the data
		selectFrom.clear();
		selectTo.clear();
		sortedObjs.clear();
		selectedObjs.clear();
		originalElements.clear();
	}

	public void clearLists()
	{
		// Clear the current list models
		selectFrom.clear();
		selectTo.clear();
	}

	public X getClosest(String attempt)
	{
		int bPoint = 0;
		int tPoint = sortedObjs.size();

		while (tPoint - bPoint > 1)
		{
			int midPoint = (tPoint + bPoint) / 2;
			X obj = sortedObjs.get(midPoint);
			if (attempt.toLowerCase().compareTo(obj.toString().toLowerCase()) > 0)
				tPoint = midPoint;
			else
				bPoint = midPoint;
		}

		return sortedObjs.get(bPoint);
	}

	public Collection<X> getData()
	{
		// Return the data
		return selectedObjs;
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
		listMain = new JList(selectFrom);
		listAdded = new JList(selectTo);
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

	private void remElem(X in)
	{
		// Remove the data from the lists and objects
		selectTo.removeElement(in);
		selectedObjs.remove(in);

		// Find the point at which the val should be added
		Object near = getClosest(in.toString());
		int nearPoint = selectFrom.indexOf(near);

		// Add at this point unless nothing was found to be close
		if (nearPoint < 0)
			selectFrom.addElement(in);
		else
			selectFrom.add(nearPoint, in);
		sortedObjs.add(in);
	}

	public void setAddOnly(boolean in)
	{
		addOnly = in;
	}

	public void setData(Collection<X> listElems, Collection<X> chosen)
	{
		// Keep one of the lists for cancellation purposes
		originalElements = new TreeSet<X>(chosen);

		// Clear the current lists
		clearLists();

		// If a list of elements is provided
		if (listElems.size() > 0)
		{
			// Set the lists visible
			setListsVisible(true);

			// Prepare the data
			sortedObjs.addAll(listElems);
			Collections.sort(sortedObjs);

			// Add the data to the list
			addAllElements(listElems);
		}
		else
			// Remove the main list and just allow adding and removing of unique
			// strings
			setListsVisible(false);

		// Add any pre-chosen elements
		for (X object : chosen)
			addElem(object);

		// Initialise the thing
		start();
	}

	public void setData(Collection<X> listElems, Collection<X> chosen,
			int entTrack, int maxTrack)
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

	public void start()
	{
		Object close = getClosest("");
		listMain.setSelectedValue(close, true);
		listMain.ensureIndexIsVisible(Math.min(listMain.getSelectedIndex() + 4,
				sortedObjs.size() + 1));
	}
}