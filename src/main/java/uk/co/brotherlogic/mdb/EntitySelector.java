package uk.co.brotherlogic.mdb;

/**
 * A GUI for creating sets of data!
 * @author Simon Tucker
 */

import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import uk.co.brotherlogic.mdb.record.Record;

public class EntitySelector extends JDialog implements ActionListener,
		CaretListener
{
	// Stuff needed for the list model
	DefaultListModel listMod;
	Record[] elems;
	Collection<String> listElements;
	List<String> sortedObjs;
	Object ans;

	// Elements of the GUI
	JTextField textIn = new JTextField();
	JList listMain;
	JButton butOK = new JButton();
	JButton buttCancel = new JButton();
	JScrollPane jScrollPane1 = new JScrollPane();

	public EntitySelector(Window refIn)
	{
		super(refIn, "Select Record", ModalityType.DOCUMENT_MODAL);

		// Prepare the selector
		listMod = new DefaultListModel();

		// To hold the data
		sortedObjs = new LinkedList<String>();

		setSize(431, 306);

		// Center the dialog over the owner
		this.setLocationRelativeTo(refIn);

		// Organise the dialog box
		setResizable(false);
		addWindowListener(new WindowAdapter()
		{
		});

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
		if (e.getActionCommand() == "ok")
		{
			ans = listMain.getSelectedValues();
			this.setVisible(false);
		}
		else if (e.getActionCommand() == "cancel")
			this.setVisible(false);
		else if (e.getActionCommand() == "text")
		{
			// same as ok
			ans = listMain.getSelectedValues();
			this.setVisible(false);
		}
	}

	public void addAllElements(Object[] elems)
	{
		// Add the data to the listModel
		for (Object elem : elems)
			listMod.addElement(elem);
	}

	public void caretUpdate(CaretEvent e)
	{
		// Find the closest string alphabetically to this one
		String close = getClosest(textIn.getText());

		// Set this value in the selected thingy - wrap the string to a database
		// element
		listMain.setSelectedValue(close, true);

		// Make sure that the selection is visible
		listMain.ensureIndexIsVisible(Math.min(listMain.getSelectedIndex() + 4,
				sortedObjs.size() + 1));
	}

	public void clearLists()
	{
		// Clear the current list models
		listMod.clear();
	}

	public String getClosest(String attempt)
	{
		int bPoint = 0;
		int tPoint = sortedObjs.size();

		while (tPoint - bPoint > 1)
		{
			int midPoint = (tPoint + bPoint) / 2;
			String obj = sortedObjs.get(midPoint);
			if (attempt.toLowerCase().compareTo(obj.toString().toLowerCase()) > 0)
				tPoint = midPoint;
			else
				bPoint = midPoint;
		}

		return sortedObjs.get(bPoint);
	}

	public Object getData()
	{
		setVisible(true);
		// Return the data
		return ans;
	}

	private void jbInit() throws Exception
	{
		textIn.setBounds(new Rectangle(6, 7, 413, 27));
		textIn.addCaretListener(this);
		textIn.addActionListener(this);
		textIn.setActionCommand("text");
		this.getContentPane().setLayout(null);
		listMain = new JList(listMod);
		butOK.setToolTipText("");
		butOK.setText("OK");
		butOK.setBounds(new Rectangle(349, 240, 70, 30));
		butOK.addActionListener(this);
		butOK.setActionCommand("ok");
		buttCancel.setText("Cancel");
		buttCancel.setBounds(new Rectangle(272, 240, 73, 30));
		buttCancel.addActionListener(this);
		buttCancel.setActionCommand("cancel");
		jScrollPane1.setBounds(new Rectangle(6, 41, 414, 197));
		this.getContentPane().add(textIn, null);
		this.getContentPane().add(jScrollPane1, null);
		this.getContentPane().add(butOK, null);
		this.getContentPane().add(buttCancel, null);
		jScrollPane1.getViewport().add(listMain, null);
		listMain.getSelectionModel().setSelectionMode(
				ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
	}

	public void receiveData(Collection in)
	{
		// Don't need this method
	}

	public void setData(Collection c, String tit)
	{
		setData(c);
		setTitle(tit);
	}

	public void setData(Collection<String> listElems)
	{
		// Clear the current lists
		clearLists();

		// If a list of elements is provided
		if (listElems.size() > 0)
		{
			// Prepare the data
			sortedObjs.addAll(listElems);
			Collections.sort(sortedObjs, new Comparator<String>()
			{
				public int compare(String o1, String o2)
				{
					return -1 * o1.toLowerCase().compareTo(o2.toLowerCase());
				}
			});

			// Add the data to the list
			addAllElements(listElems.toArray(new String[0]));
		}
	}
}
