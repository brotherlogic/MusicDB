package uk.co.brotherlogic.mdb;
/**
 * Class to represent the choices that the user has
 * @author Simon Tucker
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class EditChooserGUI extends JDialog
{
	public static final int EDIT_RECORD = 0;
	public static final int EDIT_GROOP = 1;
	public static final int RENAME_ARTIST = 2;
	public static final int RESOLVE_ARTIST = 3;

	public final int CANCEL = -1;

	private int result;

  GridLayout gridLayout1 = new GridLayout();
  JButton editRecBut = new JButton();
  JButton editGroopBut = new JButton();
  JButton renArtBut = new JButton();
  JButton resArtBut = new JButton();
  JButton cancelBut = new JButton();

  public EditChooserGUI(JFrame par)
  {
  	super(par,"Select...",true);
    try
    {
      jbInit();
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }


		result = -1;
    this.setSize(289,229);

    //Center the frame on screen
    int xSi = (int)this.getToolkit().getScreenSize().getWidth();
    int ySi = (int)this.getToolkit().getScreenSize().getHeight();
    Dimension d = this.getSize();
    int xCo = (xSi / 2) - d.width/2;
    int yCo = (ySi / 2) - d.height/2;
    this.setLocation(xCo,yCo);
  }

  public int getResult()
  {
  	return result;
  }

  public static void main (String[] args)
  {
    EditChooserGUI mine = new EditChooserGUI(null);
    mine.setVisible(true);
  }

  private void jbInit() throws Exception
  {
    editRecBut.setText("Edit Record");
    editRecBut.setEnabled(false);
    editRecBut.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        editRecBut_actionPerformed(e);
      }
    });
    gridLayout1.setRows(5);
    gridLayout1.setColumns(1);
    this.getContentPane().setLayout(gridLayout1);
    editGroopBut.setText("Edit Groop");
    editGroopBut.setEnabled(false);
    editGroopBut.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        editGroopBut_actionPerformed(e);
      }
    });
    renArtBut.setText("Rename Artist");
    renArtBut.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        renArtBut_actionPerformed(e);
      }
    });
    resArtBut.setText("Resolve Artist");
    resArtBut.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        resArtBut_actionPerformed(e);
      }
    });
    cancelBut.setText("Cancel");
    cancelBut.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        cancelBut_actionPerformed(e);
      }
    });
    this.getContentPane().add(editRecBut,null);
    this.getContentPane().add(editGroopBut, null);
    this.getContentPane().add(renArtBut, null);
    this.getContentPane().add(resArtBut, null);
    this.getContentPane().add(cancelBut, null);
  }

  void cancelButton_actionPerformed(ActionEvent e)
  {
  }

  void cancelBut_actionPerformed(ActionEvent e)
  {
  	result = CANCEL;
  	this.setVisible(false);
  }

  void editRecBut_actionPerformed(ActionEvent e)
  {
  	result = EDIT_RECORD;
  	this.setVisible(false);
  }

  void editGroopBut_actionPerformed(ActionEvent e)
  {
		result = EDIT_GROOP;
		this.setVisible(false);
  }

  void renArtBut_actionPerformed(ActionEvent e)
  {
		result = RENAME_ARTIST;
		this.setVisible(false);
  }

  void resArtBut_actionPerformed(ActionEvent e)
  {
		result = RESOLVE_ARTIST;
		this.setVisible(false);
  }
}