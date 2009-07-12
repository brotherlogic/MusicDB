package uk.co.brotherlogic.mdb.cdbuilder;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;

public class OrderingUI extends JDialog
{
	private final JList stringList;
	private final DefaultListModel listModel;
	private final JButton upButton = new JButton("Up");
	private final JButton downButton = new JButton("Down");
	private final JButton okayButton = new JButton("OK");

	public OrderingUI(Object[] objects)
	{
		listModel = new DefaultListModel();
		for (Object object : objects)
			listModel.addElement(object);
		stringList = new JList(listModel);
		initGUI();
	}

	private void down()
	{
		int selIndex = stringList.getSelectedIndex();
		if (selIndex >= 0 && selIndex < listModel.getSize() - 1)
		{
			Object o = listModel.remove(selIndex);
			listModel.add(selIndex + 1, o);
			stringList.setSelectedIndex(selIndex + 1);
		}

	}

	public Object[] getOrdering()
	{
		return listModel.toArray();
	}

	private void initGUI()
	{
		GridBagLayout gbl = new GridBagLayout();
		this.setLayout(gbl);

		gbl.setConstraints(stringList, new GridBagConstraints(0, 0, 1, 3, 1.0,
				1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(5, 5, 5, 5), 0, 0));
		add(stringList);

		gbl.setConstraints(upButton, new GridBagConstraints(1, 0, 1, 1, 0.0,
				0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
				new Insets(5, 5, 5, 5), 0, 0));
		add(upButton);

		gbl.setConstraints(downButton, new GridBagConstraints(1, 1, 1, 1, 0.0,
				1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
				new Insets(5, 5, 5, 5), 0, 0));
		add(downButton);

		gbl.setConstraints(okayButton, new GridBagConstraints(1, 2, 1, 1, 0.0,
				1.0, GridBagConstraints.SOUTHWEST, GridBagConstraints.NONE,
				new Insets(5, 5, 5, 5), 0, 0));
		add(okayButton);
		pack();
		setModal(true);
		setLocationRelativeTo(null);

		okayButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				setVisible(false);
			}
		});

		upButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				up();
			}
		});

		downButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				down();
			}
		});
	}

	private void up()
	{
		int selIndex = stringList.getSelectedIndex();
		if (selIndex > 0)
		{
			Object o = listModel.remove(selIndex);
			listModel.add(selIndex - 1, o);
			stringList.setSelectedIndex(selIndex - 1);
		}

	}

	public static void main(String[] args)
	{
		OrderingUI oui = new OrderingUI(new Object[]
		{ "Donkey", "Magic", "Man" });
		oui.setVisible(true);
	}
}
