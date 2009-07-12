package uk.co.brotherlogic.mdb;
/**
 * Class to deal with creating and editing compilation
 * @author Simon Tucker
 */

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.awt.event.*;

public class CompilationGUI extends JDialog
{
  BorderLayout borderLayout1 = new BorderLayout();
  JScrollPane jScrollPane1 = new JScrollPane();
  JPanel jPanel1 = new JPanel();
  JButton jButton1 = new JButton();
  JButton jButton2 = new JButton();
  JList mainList = new JList();
  
  DefaultListModel listMod;

  public CompilationGUI(JFrame par,ActionListener list)
  {
  	super(par,"Compilation",true);
  	setResizable(true);
  	
  	setSize(400,400);

    //Center the frame on screen
    int xSi = (int)this.getToolkit().getScreenSize().getWidth();
    int ySi = (int)this.getToolkit().getScreenSize().getHeight();
    Dimension d = this.getSize();
    int xCo = (xSi / 2) - d.width/2;
    int yCo = (ySi / 2) - d.height/2;
    this.setLocation(xCo,yCo);

    try
    {
      jbInit(list);
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
  }
  
  public void displayCompilation(Compilation compIn)
  {
  	//Collect the compilation tracks
  	Collection tracks = compIn.getChosenTracks();
  	
  	//And add them to the list
  	listMod = new DefaultListModel();
  	Iterator trIt = tracks.iterator();
  	while(trIt.hasNext())
  	{
  		listMod.addElement(trIt.next());
  	}
  	
 		mainList.setModel(listMod);
 	}
 	
 	public Collection getSelectedTracks()
 	{
 		//Prepare the collection
 		Collection ret = new TreeSet();
 		
 		//Get the chosen tracks
 		int[] sel = mainList.getSelectedIndices();
 		
 		//Fill the collection
 		for (int i = 0 ; i < sel.length ; i++)
 		{
 			// Get the next selected track and add it to the collection
 			ret.add(listMod.elementAt(sel[i]));
 		}
 		
 		return ret;
 	}
 		
  
  private void jbInit(ActionListener list) throws Exception
  {
    this.getContentPane().setLayout(borderLayout1);
    jButton1.setText("Change Tracks");
    jButton1.addActionListener(list);
    jButton1.setActionCommand("change");
    jButton2.setText("OK");
    jButton2.addActionListener(list);
    jButton2.setActionCommand("ok");
    this.getContentPane().add(jScrollPane1,  BorderLayout.CENTER);
    jScrollPane1.getViewport().add(mainList, null);
    this.getContentPane().add(jPanel1,  BorderLayout.SOUTH);
    jPanel1.add(jButton1, null);
    jPanel1.add(jButton2, null);
  }
  
  public static void main(String[] args)
  {
  	CompilationGUI mine = new CompilationGUI(null,null);
  	mine.show();
  }  	
}