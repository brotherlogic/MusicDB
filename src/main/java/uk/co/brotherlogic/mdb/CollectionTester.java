package uk.co.brotherlogic.mdb;
/**
 * Class to assess the collection ability
 */

 import java.util.*;
 import javax.swing.*;
 import java.io.*;

 public class CollectionTester
 {

  public CollectionTester() throws Exception
  {

     //Open a file open dialog thingy
    JFileChooser choose = new JFileChooser("e:\\records\\java\\version4.0\\");
    choose.setFileFilter(new javax.swing.filechooser.FileFilter(){
      public boolean accept(File in)
      {
	return in.getName().endsWith(".txt");
      }

      public String getDescription()
      {
	return "what?";
      }
    });

    int retVal = choose.showOpenDialog(null);
    File chosen = choose.getSelectedFile();
    choose = null;

    //Open a reader which reads by file
    FileReader fr = new FileReader(chosen);
    BufferedReader br = new BufferedReader(fr);

    int count = 0;
    String line = br.readLine();
    while(line != null)
    {
      if (line.startsWith("TITLE"))
      {
	 count++;
      }

	 line = br.readLine();
    }

  }

  public static void main(String[] args) throws Exception
  {
    CollectionTester mine = new CollectionTester();
  }

}