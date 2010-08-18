package uk.co.brotherlogic.mdb;

/**
 * Class to deal with the choosing and joining of tracks in the make CD File overseer
 * @author Simon Tucker
 */

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import uk.co.brotherlogic.mdb.record.Track;

public class TrackChooser extends JDialog implements ActionListener {

	Collection<Track> tracks;

	Vector<Track> trackOut;

	JButton butOkay;
	JButton butCan;

	LinkedList<JButton> buttons;
	LinkedList<LinkedList<Track>> elems;

	JPanel midPan;
	JScrollPane scroller;

	public TrackChooser(JFrame in, Collection<Track> trackListing) {
		super(in, "Choose Joining Tracks", true);

		// Prepare the middle panel
		midPan = new JPanel();
		midPan.setLayout(new BoxLayout(midPan, BoxLayout.Y_AXIS));
		scroller = new JScrollPane();
		getContentPane().add(scroller);

		tracks = trackListing;
		buttons = new LinkedList<JButton>();

		prepareList();
		displayTracks();
		showTracksFirst();
		setSize(500, 500);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("ok"))
			// Do nothing
			doTracks();
		else if (e.getActionCommand().equals("command")) {
			// Restore the lists to their original settings
			prepareList();
			showTracks();
		} else {
			// Button pressed - get the number
			int buttonPressed = Integer.parseInt(e.getActionCommand()
					.substring(3, e.getActionCommand().length()));

			if (buttonPressed > 0) {
				// Subsume the correct tracks
				LinkedList<Track> move = elems.remove(buttonPressed);
				LinkedList<Track> receive = elems.get(buttonPressed - 1);

				// Receive the new tracks
				Iterator<Track> mIt = move.iterator();
				while (mIt.hasNext())
					receive.addLast(mIt.next());
			}

			showTracks();
		}
	}

	private void displayTracks() {
		getContentPane().setLayout(new BorderLayout());

		// First construct the buttons
		butOkay = new JButton("OK");
		butOkay.setActionCommand("ok");
		butOkay.addActionListener(this);
		butCan = new JButton("Cancel");
		butCan.setActionCommand("cancel");
		butCan.addActionListener(this);

		// Put the buttons in a wee frame
		JPanel botPan = new JPanel();
		botPan.setLayout(new FlowLayout(FlowLayout.RIGHT));
		botPan.add(butCan);
		botPan.add(butOkay);

		getContentPane().add(botPan, BorderLayout.SOUTH);
	}

	public void doTracks() {
		// Prepare the list for output

		setVisible(false);
	}

	public LinkedList<LinkedList<Track>> getTrackData() {
		return elems;
	}

	public void prepareList() {
		// Deal with the elems
		elems = new LinkedList<LinkedList<Track>>();
		Iterator<Track> tIt = tracks.iterator();
		while (tIt.hasNext()) {
			LinkedList<Track> temp = new LinkedList<Track>();
			temp.addLast(tIt.next());
			elems.addLast(temp);
		}
	}

	public void showTracks() {

		Iterator<JButton> butIt = buttons.iterator();
		int count = 0;
		while (butIt.hasNext()) {
			// Get the button and the track list
			JButton tempBut = butIt.next();

			if (count < elems.size()) {
				LinkedList<Track> stuff = elems.get(count);

				// Generate a track title
				StringBuffer rep = new StringBuffer(stuff.get(0).getTitle());
				for (Track trck : stuff.subList(1, stuff.size() - 1))
					rep.append(" / " + trck.getTitle());
				tempBut.setText(rep.toString());
			} else {
				tempBut.setText("");
				tempBut.setEnabled(false);
			}

			count++;
		}

	}

	public void showTracksFirst() {
		getContentPane().remove(scroller);
		midPan.removeAll();

		// Add the track details
		Iterator<LinkedList<Track>> tIt = elems.iterator();
		int count = 0;
		while (tIt.hasNext()) {
			LinkedList<Track> vec = tIt.next();
			Iterator<Track> vIt = vec.iterator();
			StringBuffer rep = new StringBuffer();
			while (vIt.hasNext())
				rep.append(vIt.next().getTitle());

			JButton tempBut = new JButton(rep.toString());
			tempBut.setActionCommand("but" + count);
			tempBut.addActionListener(this);
			midPan.add(tempBut);
			count++;

			buttons.addLast(tempBut);
		}

		scroller = new JScrollPane(midPan);
		// getContentPane().removeAll();
		getContentPane().add(scroller, BorderLayout.CENTER);
	}
}