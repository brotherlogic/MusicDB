package uk.co.brotherlogic.mdb;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;

import uk.co.brotherlogic.mdb.cdbuilder.MakeCDFileOverseer;
import uk.co.brotherlogic.mdb.format.GetFormats;
import uk.co.brotherlogic.mdb.groop.GetGroops;
import uk.co.brotherlogic.mdb.record.AddRecordOverseer;
import uk.co.brotherlogic.mdb.record.GetRecords;
import uk.co.brotherlogic.mdb.record.Record;

public class App extends JFrame
{
	// Persistent Object to handle database calls
	private Persistent pers;
	JButton buttonAdd = new JButton();
	JButton buttonEdit = new JButton();
	JButton buttonCD = new JButton();

	boolean edit = false;
	int recNum = -1;

	AddRecordOverseer over;
	JPanel jPanel1 = new JPanel();
	GridLayout gridLayout1 = new GridLayout();
	JButton butAlias = new JButton();
	JButton butCat = new JButton();
	JButton butAuth = new JButton();

	// Default for windows
	String fileString = "i:\\";
	JPanel jPanel2 = new JPanel();
	JButton buttonCDAdd = new JButton();
	GridLayout gridLayout2 = new GridLayout();
	GridLayout gridLayout3 = new GridLayout();

	// Indicates where we're running
	boolean unix = false;

	public App()
	{
		try
		{
			pers = Persistent.create();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			pers = null;
		}
	}

	public void actionPerformed(ActionEvent e)
	{
		// Do Nothing for now
	}

	private void addCD() throws SQLException
	{
		// Bring up the add record overseer
		this.setVisible(false);
		edit = false;
		over = new AddRecordOverseer(this, GetArtists.create().getArtists(),
				GetLabels.create().getLabels(), GetFormats.create()
						.getFormats(), GetGroops.build().getGroopMap(),
				GetCategories.build().getCategories());
		over.showGUI(this);
	}

	public void addDone()
	{
		// Add record is done!
		try
		{
			if (!edit)
			{
				Record gotRec = over.getRecordWhenDone();
				GetRecords.create().addRecord(gotRec);
			}
			else
			{
				Record gotRec = over.getRecordWhenDone();
				GetRecords.create().updateRecord(gotRec);
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

		this.setVisible(true);
	}

	void buttonAdd_actionPerformed(ActionEvent e)
	{
		try
		{
			addCD();
		}
		catch (SQLException e2)
		{
			JOptionPane.showMessageDialog(this, e2.getMessage());
		}
	}

	void buttonCD_actionPerformed(ActionEvent e)
	{
		try
		{
			// Run the button CD overseer
			this.setVisible(false);
			new MakeCDFileOverseer(GetRecords.create(), fileString);
			this.setVisible(true);
		}
		catch (SQLException e2)
		{
			JOptionPane.showMessageDialog(this, e2.getMessage());
		}

	}

	void buttonDelete_actionPerformed(ActionEvent e)
	{
		try
		{
			this.setVisible(false);

			// Get a record
			Record toDelete = GetRecords.create().selectRecord(this);
			toDelete = GetRecords.create()
					.getSingleRecord(toDelete.getNumber());

			// Check that this is what we want to do
			String message = "Are you sure you want to delete "
					+ toDelete.getAuthor() + " - " + toDelete.getTitle();
			int res = JOptionPane.showConfirmDialog(this, message);

			if (res == 0)
				// Do the delete
				GetRecords.create().deleteRecord(toDelete);

			this.setVisible(true);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	void buttonEdit_actionPerformed(ActionEvent e)
	{
		try
		{
			// Choose a file to examine
			Record examine = GetRecords.create().selectRecord(this);

			if (examine != null)
			{
				// Prepare the viewer
				this.setVisible(false);
				edit = true;
				recNum = examine.getNumber();

				over = new AddRecordOverseer(this, GetArtists.create()
						.getArtists(), GetLabels.create().getLabels(),
						GetFormats.create().getFormats(), GetGroops.build()
								.getGroopMap(), GetCategories.build()
								.getCategories(), examine);
				over.showGUI(this);
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

	}

	public void cancel()
	{
		this.setVisible(true);
	}

	public Persistent getPersistent()
	{
		return pers;
	}

	private void jbInit() throws Exception
	{
		buttonAdd.setText("Add Record");
		buttonAdd.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				buttonAdd_actionPerformed(e);
			}
		});
		this.getContentPane().setLayout(gridLayout3);
		buttonEdit.setText("Edit Record");
		buttonEdit.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				buttonEdit_actionPerformed(e);
			}
		});
		buttonCD.setText("Make CD File");
		buttonCD.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				buttonCD_actionPerformed(e);
			}
		});
		this.setDefaultCloseOperation(3);
		this.setTitle("");
		jPanel1.setLayout(gridLayout1);
		gridLayout1.setColumns(1);
		gridLayout1.setRows(3);

		jPanel2.setMaximumSize(new Dimension(32767, 32767));
		jPanel2.setLayout(gridLayout2);
		gridLayout3.setColumns(2);
		gridLayout3.setRows(2);
		gridLayout2.setColumns(1);
		gridLayout2.setRows(1);
		this.getContentPane().add(jPanel2, null);
		jPanel2.add(buttonAdd, null);
		this.getContentPane().add(buttonCD, null);
		this.getContentPane().add(buttonEdit, null);
		this.getContentPane().add(jPanel1, null);
		jPanel1.add(butAlias, null);
		jPanel1.add(butCat, null);
		jPanel1.add(butAuth, null);
	}

	public void runApp()
	{

		// Now construct the gui
		try
		{
			jbInit();
			this.setSize(500, 500);

			// Center the frame on screen
			this.setLocationRelativeTo(null);

			// Display the giu
			this.setVisible(true);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	public void setFileString(String in)
	{
		fileString = in;
	}

	public void setUnix(boolean in)
	{
		unix = in;
	}

	public static void main(String[] args) throws Exception
	{
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

		App c = new App();
		if (System.getProperty("os.name").compareToIgnoreCase("Linux") == 0)
			c.setFileString("/usr/share/hancock_multimedia/");
		c.runApp();
	}

}
