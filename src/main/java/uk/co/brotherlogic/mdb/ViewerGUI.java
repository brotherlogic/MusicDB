package uk.co.brotherlogic.mdb;

/**
 * Class to control the viewing of the database info
 * @author Simon Tucker
 */

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

public class ViewerGUI extends JFrame implements CaretListener
{
	// Data elements
	SortedSet<Object> objs;
	DefaultListModel listMod;
	Object[] elems;

	// Gui Elements
	BorderLayout borderLayout1 = new BorderLayout();
	JSplitPane jSplitPane1 = new JSplitPane();
	JPanel jPanel1 = new JPanel();
	GridBagLayout gridBagLayout1 = new GridBagLayout();
	JButton jButton1 = new JButton();
	JButton jButton2 = new JButton();
	JButton jButton3 = new JButton();
	JButton jButton4 = new JButton();
	JTextField textIn = new JTextField();
	JScrollPane jScrollPane1 = new JScrollPane();
	JList listMain = new JList();
	JScrollPane jScrollPane2 = new JScrollPane();
	JEditorPane jEditorPane1 = new JEditorPane();

	public ViewerGUI(ActionListener cont, MouseListener mlist)
	{
		// Prepare the data holders
		listMod = new DefaultListModel();
		objs = new TreeSet<Object>();
		listMain = new JList(listMod);

		this.setSize(300, 300);

		try
		{
			jbInit(cont, mlist);
		}
		catch (Exception e)
		{
			e.printStackTrace();
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
		Object close = getClosest(textIn.getText());

		// Set this value in the selected thingy - wrap the string to a database
		// element
		listMain.setSelectedValue(close, true);

		// Make sure that the selection is visible
		listMain.ensureIndexIsVisible(Math.min(listMain.getSelectedIndex() + 4,
				objs.size() + 1));

	}

	public void clearLists()
	{
		// Clear the current list models
		listMod.clear();
	}

	public Object getClosest(Object attempt)
	{
		// Retrieve the tail of the selection set
		SortedSet<Object> temp = objs.tailSet(attempt);

		// Return the relevant item
		if (temp.size() > 0)
			return temp.first();
		else
			return null;

	}

	public Object getSelected()
	{
		return listMain.getSelectedValue();
	}

	private void jbInit(ActionListener cont, MouseListener mlist)
			throws Exception
	{
		this.getContentPane().setLayout(borderLayout1);
		jSplitPane1.setDoubleBuffered(true);
		jSplitPane1.setToolTipText("");
		jSplitPane1.setBottomComponent(null);
		jSplitPane1.setTopComponent(null);
		jPanel1.setLayout(gridBagLayout1);
		jButton1.setText("Labels");
		jButton1.setActionCommand("label");
		jButton1.addActionListener(cont);
		jButton2.setText("Groops");
		jButton2.setActionCommand("groop");
		jButton2.addActionListener(cont);
		jButton3.setText("Records");
		jButton3.setActionCommand("record");
		jButton3.addActionListener(cont);
		jButton4.setText("Tracks");
		jButton4.setActionCommand("track");
		jButton4.addActionListener(cont);
		textIn.setText("");
		textIn.addCaretListener(this);
		textIn.addActionListener(cont);
		textIn.setActionCommand("enter");
		jEditorPane1.setText("jEditorPane1");
		this.getContentPane().add(jSplitPane1, BorderLayout.CENTER);
		jSplitPane1.add(jPanel1, JSplitPane.LEFT);
		jPanel1.add(jButton1, new GridBagConstraints(0, 0, 1, 1, 0.5, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
						0, 0, 0, 0), 0, 0));
		jPanel1.add(jButton2, new GridBagConstraints(1, 0, 1, 1, 0.5, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(0, 0, 0, 0), 0, 0));
		jPanel1.add(jButton3, new GridBagConstraints(0, 1, 1, 1, 0.5, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(0, 0, 0, 0), 0, 0));
		jPanel1.add(jButton4, new GridBagConstraints(1, 1, 1, 1, 0.5, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(0, 0, 0, 0), 0, 0));
		jPanel1.add(textIn, new GridBagConstraints(0, 2, 3, 1, 1.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(0, 0, 0, 0), 0, 0));
		jPanel1.add(jScrollPane1, new GridBagConstraints(0, 3, 2, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
						0, 0, 0, 0), 0, 0));
		listMain.addMouseListener(mlist);
		jSplitPane1.add(jScrollPane2, JSplitPane.RIGHT);
		jScrollPane2.getViewport().add(jEditorPane1, null);
		jScrollPane1.getViewport().add(listMain, null);
	}

	public void setData(Collection<Object> listElems)
	{
		// Clear the current lists
		clearLists();

		// If a list of elements is provided
		if (listElems.size() > 0)
		{
			// Prepare the data
			elems = listElems.toArray();
			objs.addAll(listElems);

			// Add the data to the list
			addAllElements(elems);
		}

	}

	public void setFile(String url)
	{
		try
		{
			URL locator = new URL(url);
			jEditorPane1.setPage(locator);
		}
		catch (MalformedURLException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
