package uk.co.brotherlogic.mdb;

/**
 * Class to deal with adding a new format
 * @author Simon Tucker
 */

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.TreeSet;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class NewFormatGUI extends JDialog implements ActionListener
{
	GridBagLayout gridBagLayout1 = new GridBagLayout();
	JLabel jLabel1 = new JLabel();
	JLabel jLabel2 = new JLabel();
	JTextField textFormat = new JTextField();
	JComboBox comboOther = new JComboBox();
	JButton butOK = new JButton();
	JButton butCancel = new JButton();
	JPanel jPanel1 = new JPanel();

	// Flag to indicate cancellation
	boolean cancelled = false;

	public NewFormatGUI(Collection categories, JFrame in)
	{
		super(in, true);
		Vector cats = new Vector();
		cats.addAll(categories);

		try
		{
			jbInit(cats);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		// Set the size and center in the screen
		this.setSize(379, 149);
		this.setResizable(false);

		// Center the frame on screen
		int xSi = (int) this.getToolkit().getScreenSize().getWidth();
		int ySi = (int) this.getToolkit().getScreenSize().getHeight();
		Dimension d = this.getSize();
		int xCo = (xSi / 2) - d.width / 2;
		int yCo = (ySi / 2) - d.height / 2;
		this.setLocation(xCo, yCo);
	}

	public void actionPerformed(ActionEvent e)
	{
		if (e.getActionCommand().equals("ok"))
			setVisible(false);
		else if (e.getActionCommand().equals("cancel"))
		{
			cancelled = true;
			setVisible(false);
		}
	}

	public Format getFormat()
	{
		Format ret;

		// Build the format object using the string
		if (cancelled)
			return null;
		else
		{
			Format other = (Format) comboOther.getSelectedItem();

			if (other != null)
				ret = new Format(-1, textFormat.getText(), ((Format) comboOther
						.getSelectedItem()).getCategories());
			else
				ret = new Format(-1, textFormat.getText(), new TreeSet());

			return ret;
		}
	}

	private void jbInit(Vector categories) throws Exception
	{
		// Construct the Categories thing accordingly
		comboOther = new JComboBox(categories);

		jLabel1.setHorizontalAlignment(SwingConstants.RIGHT);
		jLabel1.setText("Format :");
		this.getContentPane().setLayout(gridBagLayout1);
		jLabel2.setText("Take Category From: ");
		textFormat.setMaximumSize(new Dimension(2147483647, 30));
		textFormat.setMinimumSize(new Dimension(4, 30));
		textFormat.setPreferredSize(new Dimension(4, 30));
		textFormat.setText("");
		textFormat.setHorizontalAlignment(SwingConstants.LEADING);
		comboOther.setMaximumSize(new Dimension(32767, 30));
		comboOther.setMinimumSize(new Dimension(31, 30));
		comboOther.setPreferredSize(new Dimension(31, 30));
		butOK.setMaximumSize(new Dimension(75, 30));
		butOK.setMinimumSize(new Dimension(75, 30));
		butOK.setPreferredSize(new Dimension(75, 30));
		butOK.setToolTipText("");
		butOK.setText("OK");
		butOK.setActionCommand("ok");
		butOK.addActionListener(this);
		butCancel.setMaximumSize(new Dimension(75, 30));
		butCancel.setMinimumSize(new Dimension(75, 30));
		butCancel.setPreferredSize(new Dimension(75, 30));
		butCancel.setText("Cancel");
		butCancel.setActionCommand("cancel");
		butCancel.addActionListener(this);
		this.getContentPane().add(
				jLabel1,
				new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
						GridBagConstraints.EAST, GridBagConstraints.NONE,
						new Insets(5, 5, 5, 5), 0, 0));
		this.getContentPane().add(
				jLabel2,
				new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
						GridBagConstraints.CENTER, GridBagConstraints.NONE,
						new Insets(5, 5, 0, 0), 0, 0));
		this.getContentPane().add(
				textFormat,
				new GridBagConstraints(1, 0, 3, 1, 1.0, 0.0,
						GridBagConstraints.CENTER,
						GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5),
						0, 0));
		this.getContentPane().add(
				comboOther,
				new GridBagConstraints(1, 1, 3, 1, 1.0, 0.0,
						GridBagConstraints.CENTER,
						GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0, 5),
						0, 0));
		this.getContentPane().add(
				butOK,
				new GridBagConstraints(3, 2, 1, 1, 0.0, 0.0,
						GridBagConstraints.EAST, GridBagConstraints.NONE,
						new Insets(5, 5, 5, 5), 0, 0));
		this.getContentPane().add(
				butCancel,
				new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0,
						GridBagConstraints.EAST, GridBagConstraints.NONE,
						new Insets(5, 5, 5, 5), 0, 0));
		this.getContentPane().add(
				jPanel1,
				new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0,
						GridBagConstraints.CENTER,
						GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0),
						0, 0));
	}
}
