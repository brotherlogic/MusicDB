package uk.co.brotherlogic.mdb;

/**
 * Class to make a compilation
 * @author Simon Tucker
 */

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;
import java.util.Vector;

public class Compilation
{
	// Chosen tracks
	List chosen;

	// Discarded tracks
	Collection discarded;

	// Stores of the various lengths
	int totLen;
	int maxLen;
	int currLen;

	public Compilation(int maxLength, int totalLength, List allTracks)
	{
		// Store maximums for later use
		totLen = totalLength;
		maxLen = maxLength;

		// Initialise the track stores
		chosen = new Vector();
		discarded = new TreeSet();

		// Set the initial compilation length to zero
		currLen = 0;

		// Find a suitable amount of tracks
		while (currLen < totalLength)
		{
			// Add a track
			SimpleTrack chosenTrack = chooseTrack(allTracks);
			currLen += chosenTrack.getLengthInSeconds();
			chosen.add(chosenTrack);
		}
	}

	public SimpleTrack chooseTrack(List allTracks)
	{
		// SimpleTrack to return
		SimpleTrack ret = new SimpleTrack();

		// Does this track meet the required constraints?
		boolean constraintsMet = false;

		// Search for a suitable track
		while (!constraintsMet)
		{
			// Set the constraints to true
			constraintsMet = true;

			// Choose an index number
			int indexNumber = (int) (Math.random() * allTracks.size());

			// Get the track
			ret = (SimpleTrack) allTracks.get(indexNumber);

			// Is the track the required length?
			if (ret.getLengthInSeconds() > maxLen)
				constraintsMet = false;

			// Has this album already been chosen?
			Iterator it = chosen.iterator();
			while (it.hasNext())
				if (((SimpleTrack) it.next()).getRecordNumber() == ret
						.getRecordNumber())
					constraintsMet = false;

			if (!constraintsMet)
				discarded.add(ret);

			// Check we haven't extinguished the possibilities
			if (allTracks.size() == chosen.size() + discarded.size())
				return new SimpleTrack("", "", -1, 9999999, -1);
		}

		// Add this track to the discarded
		discarded.add(ret);

		return ret;
	}

	public Collection getChosenTracks()
	{
		return chosen;
	}

	public Collection repickTrack(SimpleTrack toRemove, List allTracks)
	{
		// Choose a new track
		SimpleTrack newTrack = chooseTrack(allTracks);

		// Adjust the current length
		currLen -= toRemove.getLengthInSeconds();

		while (newTrack.getLengthInSeconds() - 60 < toRemove
				.getLengthInSeconds())
			newTrack = chooseTrack(allTracks);

		// Replace the track
		int index = chosen.indexOf(toRemove);
		chosen.remove(toRemove);
		chosen.add(index, newTrack);
		// discarded.add(toRemove);

		// Return the new set
		return chosen;
	}

}
