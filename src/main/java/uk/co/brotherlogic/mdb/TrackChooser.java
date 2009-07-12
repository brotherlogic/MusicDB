package uk.co.brotherlogic.mdb;
/**
 * Class to deal with the choosing and joining of tracks in the make CD File overseer
 * @author Simon Tucker
 */

import java.awt.*;
import java.util.*;
import javax.swing.*;
import java.awt.event.*;

public class TrackChooser extends JDialog implements ActionListener
{

  Collection tracks;

  Vector trackOut;

  JButton butOkay;
  JButton butCan;

  LinkedList buttons;
  LinkedList elems;

  JPanel midPan;
  JScrollPane scroller;

  public TrackChooser(JFrame in,Collection trackListing)
  {
    super(in,"Choose Joining Tracks",true);

    //Prepare the middle panel
    midPan = new JPanel();
    midPan.setLayout(new BoxLayout(midPan,BoxLayout.Y_AXIS));
    scroller = new JScrollPane();
    getContentPane().add(scroller);

    tracks = trackListing;
    buttons = new LinkedList();

    prepareList();
    displayTracks();
    showTracksFirst();
    setSize(500,500);
  }

  public void prepareList()
  {
    //Deal with the elems
    elems = new LinkedList();
    Iterator tIt = tracks.iterator();
    while(tIt.hasNext())
    {
      LinkedList temp = new LinkedList();
      temp.addLast(tIt.next());
      elems.addLast(temp);
    }
  }


  public void actionPerformed(ActionEvent e)
  {
    if (e.getActionCommand().equals("ok"))
    {
      //Do nothing
      doTracks();
    }
    else if (e.getActionCommand().equals("command"))
    {
      //Restore the lists to their original settings
      prepareList();
      showTracks();
    }
    else
    {
      //Button pressed - get the number
      int buttonPressed = Integer.parseInt(e.getActionCommand().substring(3,e.getActionCommand().length()));

      if (buttonPressed > 0)
      {
         //Subsume the correct tracks
         LinkedList move = (LinkedList)elems.remove(buttonPressed);
         LinkedList receive = (LinkedList)elems.get(buttonPressed-1);

         //Receive the new tracks
         Iterator mIt = move.iterator();
         while(mIt.hasNext())
            receive.addLast(mIt.next());
      }

      showTracks();
    }
  }

  public void doTracks()
  {
    //Prepare the list for output

    setVisible(false);
  }

  public LinkedList getTrackData()
  {
    return elems;
  }

  private void displayTracks()
  {
    getContentPane().setLayout(new BorderLayout());

    //First construct the buttons
    butOkay = new JButton("OK");
    butOkay.setActionCommand("ok");
    butOkay.addActionListener(this);
    butCan = new JButton("Cancel");
    butCan.setActionCommand("cancel");
    butCan.addActionListener(this);

    //Put the buttons in a wee frame
    JPanel botPan = new JPanel();
    botPan.setLayout(new FlowLayout(FlowLayout.RIGHT));
    botPan.add(butCan);
    botPan.add(butOkay);

    getContentPane().add(botPan,BorderLayout.SOUTH);
   }

   public void showTracks()
   {

      Iterator butIt = buttons.iterator();
      int count = 0;
      while(butIt.hasNext())
      {
         //Get the button and the track list
         JButton tempBut = (JButton)butIt.next();

         if(count < elems.size())
         {
            LinkedList stuff = (LinkedList)elems.get(count);

            //Generate a track title
            Iterator vIt = stuff.iterator();
            String rep = "";
            while(vIt.hasNext())
               rep += ((Track)vIt.next()).getTitle() + " / ";

            rep = rep.substring(0,rep.length()-3);

            tempBut.setText(rep);
         }
         else
         {
            tempBut.setText("");
            tempBut.setEnabled(false);
         }

         count++;
     }


   }

   public void showTracksFirst()
   {
      getContentPane().remove(scroller);
      midPan.removeAll();

      //Add the track details
      Iterator tIt = elems.iterator();
      int count = 0;
      while(tIt.hasNext())
      {
        LinkedList vec = (LinkedList)tIt.next();
        Iterator vIt = vec.iterator();
        String rep = "";
        while(vIt.hasNext())
           rep += ((Track)vIt.next()).getTitle();

        JButton tempBut = new JButton(rep);
        tempBut.setActionCommand("but" + count);
        tempBut.addActionListener(this);
        midPan.add(tempBut);
        count++;

        buttons.addLast(tempBut);
      }

    scroller = new JScrollPane(midPan);
    //getContentPane().removeAll();
    getContentPane().add(scroller,BorderLayout.CENTER);
  }

  public static void main(String[] args)
  {
    TrackChooser mine = new TrackChooser(null,new TreeSet());
  }
}