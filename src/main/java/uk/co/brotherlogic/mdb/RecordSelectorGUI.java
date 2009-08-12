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
import java.awt.event.WindowAdapter;
import java.util.Collection;
import java.util.Collections;
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

public class RecordSelectorGUI extends JDialog implements ActionListener,
		CaretListener
{
	// List model for the text list
	DefaultListModel listMod;
	Object[] elems;
	List<Record> listElements = new LinkedList<Record>();
	List<Record> objs;

	// The resulting selected object
	Object ans;

	JTextField textIn = new JTextField();
	JList listMain;
	JButton butOK = new JButton();
	JButton buttCancel = new JButton();
	JScrollPane jScrollPane1 = new JScrollPane();

	public RecordSelectorGUI(Frame refIn)
	{
		super(refIn, "Select Record", true);
		// Prepare the selector
		listMod = new DefaultListModel();

		setSize(431, 306);

		// Center the frame on screen
		int xSi = (int) this.getToolkit().getScreenSize().getWidth();
		int ySi = (int) this.getToolkit().getScreenSize().getHeight();
		Dimension d = this.getSize();
		int xCo = (xSi / 2) - d.width / 2;
		int yCo = (ySi / 2) - d.height / 2;
		this.setLocation(xCo, yCo);

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
			ans = listMain.getSelectedValue();
			this.setVisible(false);
		}
		else if (e.getActionCommand() == "cancel")
			this.setVisible(false);
		else if (e.getActionCommand() == "text")
		{
			// same as ok
			ans = listMain.getSelectedValue();
			this.setVisible(false);
		}
	}

	public void caretUpdate(CaretEvent e)
	{
		// Get the closest option
		Record close = getClosest(textIn.getText());

		// Select the closest option
		listMain.setSelectedValue(close, true);

		// Make sure that the selection is visible
		listMain.ensureIndexIsVisible(Math.min(listMain.getSelectedIndex() + 4,
				objs.size() + 1));
	}

	public Record getClosest(String attempt)
	{
		int bPoint = 0;
		int tPoint = listElements.size();

		while (tPoint - bPoint > 1)
		{
			int midPoint = (tPoint + bPoint) / 2;
			Record obj = listElements.get(midPoint);
			if (attempt.toLowerCase().compareTo(obj.toString().toLowerCase()) > 0)
				tPoint = midPoint;
			else
				bPoint = midPoint;
		}

		return listElements.get(bPoint);
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
				ListSelectionModel.SINGLE_SELECTION);
	}

	public void setData(Collection<Record> listElems)
	{
		objs = new LinkedList<Record>();
		listMod.clear();

		// If a list of elements is provided
		if (listElems.size() > 0)
		{
			listMain.setVisible(true);
			jScrollPane1.setVisible(true);
			listMain.setEnabled(true);
			jScrollPane1.setEnabled(true);
			objs.addAll(listElems);
			Collections.sort(objs);

			// Add the data to the listModel
			for (Object elem : objs)
				listMod.addElement(elem);
		}
		else
		{
			listMain.setVisible(false);
			jScrollPane1.setVisible(false);
		}
	}

	public void setData(Collection<Record> c, String tit)
	{
		ans = "";
		setData(c);
		setTitle(tit);
	}

}
