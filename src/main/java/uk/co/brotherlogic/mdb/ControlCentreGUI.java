package uk.co.brotherlogic.mdb;

/**
 * A GUI for the control centre
 * @author Simon Tucker
 */

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;

public class ControlCentreGUI extends JFrame
{
	GridLayout gridLayout1 = new GridLayout();
	JButton addRecordButton = new JButton();
	JButton compButton = new JButton();
	JButton boxButton = new JButton();
	JButton editButton = new JButton();

	public ControlCentreGUI(ActionListener list)
	{
		try
		{
			jbInit(list);
		} catch (Exception e)
		{
			e.printStackTrace();
		}

		this.setSize(500, 500);
		// Center the frame on screen
		int xSi = (int) this.getToolkit().getScreenSize().getWidth();
		int ySi = (int) this.getToolkit().getScreenSize().getHeight();
		Dimension d = this.getSize();
		int xCo = (xSi / 2) - d.width / 2;
		int yCo = (ySi / 2) - d.height / 2;
		this.setLocation(xCo, yCo);
	}

	private void jbInit(ActionListener list) throws Exception
	{
		addRecordButton.setActionCommand("addrecord");
		addRecordButton.setMnemonic('R');
		addRecordButton.setText("Add Record");
		addRecordButton.addActionListener(list);
		gridLayout1.setRows(2);
		gridLayout1.setColumns(2);
		gridLayout1.setHgap(5);
		gridLayout1.setVgap(5);
		this.getContentPane().setLayout(gridLayout1);
		compButton.setActionCommand("compilation");
		compButton.setMnemonic('C');
		compButton.setText("Compilation");
		compButton.addActionListener(list);
		boxButton.setActionCommand("discog");
		boxButton.setMnemonic('D');
		boxButton.setText("Discog Add");
		boxButton.addActionListener(list);
		editButton.setActionCommand("edit");
		editButton.setMnemonic('E');
		editButton.setText("Edit");
		editButton.addActionListener(list);
		this.getContentPane().add(addRecordButton, null);
		this.getContentPane().add(compButton, null);
		this.getContentPane().add(boxButton, null);
		this.getContentPane().add(editButton, null);
	}

}