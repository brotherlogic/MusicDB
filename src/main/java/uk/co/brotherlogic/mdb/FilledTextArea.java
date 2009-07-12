package uk.co.brotherlogic.mdb;

/**
 * A Class to do fancy text filling fields
 * @author Simon Tucker
 */

import java.awt.Component;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

public class FilledTextArea extends JTextField
{

	static class UpperCaseDocument extends PlainDocument
	{
		String typed;
		Collection sce;
		FilledTextArea par;
		String prevBest;
		AttributeSet basicSet;
		boolean flag = false;

		public UpperCaseDocument(FilledTextArea parent)
		{
			par = parent;
			typed = "";
			prevBest = "";
			// flag = true;
		}

		public String findBest(String start)
		{
			Iterator sIt = sce.iterator();
			while (sIt.hasNext())
			{
				String comp = sIt.next().toString();
				if ((comp).toLowerCase().startsWith(start.toLowerCase()))
					return comp;
			}

			return "";
		}

		public void insertString(int offs, String str, AttributeSet a)
				throws BadLocationException
		{
			prepareDocument();
			basicSet = a;

			typed += str;

			// And Insert new stuff
			String mine;
			if (typed.compareTo("") == 0)
				mine = "";
			else
				mine = findBest(typed);

			// Check that legitimate group can be found
			if (!flag)
			{
				if (mine == "")
				{
					// No group found -> need to add the typed text!
					super.insertString(0, typed, a);
					super.remove(typed.length(), prevBest.length());
					prevBest = typed;
				}
				else
				{
					super.insertString(0, mine, a);
					super.remove(mine.length(), prevBest.length());
					prevBest = mine;
				}
			}
			else
			{
				super.insertString(0, mine, a);
				prevBest = mine;
				flag = false;
			}
		}

		public void prepareDocument()
		{
			sce = par.getSource();
		}

		public void remove(int offs, int len) throws BadLocationException
		{
			// If len is 1 and the previous best is not of length 1 then remove
			// typed letter 1
			if (len == 1 && !(prevBest.length() == 1))
			{
				// Remove the letter
				typed = typed.substring(0, typed.length() - 1);

				// And update the string
				insertString(typed.length(), "", basicSet);
			}
			else
				super.remove(offs, len);
		}

		public void setText(String inStr)
		{
			// typed = inStr;
			// flag = true;
			prevBest = "";
			typed = "";
		}
	}

	// Array containing strings to be compared to
	Collection sce; // Should be string....
	String typed;

	UpperCaseDocument doc;

	public FilledTextArea()
	{
		// Do nothing here???
	}

	public FilledTextArea(Collection src)
	{
		sce = src;
	}

	protected Document createDefaultModel()
	{
		doc = new UpperCaseDocument(this);
		return doc;
	}

	public Collection getSource()
	{
		return sce;
	}

	public Component getTableCellEditorComponent(JTable table, Object value,
			boolean isSelected, int row, int column)
	{
		return this;
	}

	public void setText(String text)
	{
		// Set the default model thing
		doc.setText(text);
		super.setText(text);
	}

}
