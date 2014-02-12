/*
 * Copyright (C) 2002-2006 Stefan Stiller
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package com.kiwisoft.utils.gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import javax.swing.*;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.4 $, $Date: 2006/03/24 18:02:56 $
 */
public class TextEditorDialog extends JDialog
{
	private JTextPane textPane;
	private String text;
	private boolean returnValue;

	public TextEditorDialog(String text, boolean modal)
	{
		super((Frame)null, "Text Editor", modal);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		textPane=new JTextPane();
		textPane.setText(text);

		JScrollPane pnlContent=new JScrollPane(textPane);
		pnlContent.setPreferredSize(new Dimension(400, 200));

		JPanel pnlBottom=new JPanel(new FlowLayout(FlowLayout.RIGHT));
		Action[] actions=new Action[]{new ApplyAction(), new CancelAction()};
		JButton defaultButton=null;
		for (int i=0; i<actions.length; i++)
		{
			Action action=actions[i];
			JButton button=new JButton(action);
			button.setPreferredSize(new Dimension(80, 25));
			pnlBottom.add(button);
			if (action.getValue("DefaultAction")!=null) defaultButton=button;
		}

		JPanel pnlMain=new JPanel(new GridBagLayout());
		pnlMain.add(pnlContent,
					new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(3, 3, 3, 3), 0, 0));
		pnlMain.add(pnlBottom,
					new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(3, 3, 3, 3), 0, 0));

		setContentPane(pnlMain);
		if (defaultButton!=null) getRootPane().setDefaultButton(defaultButton);
		pack();
	}

	public void show()
	{
		GuiUtils.arrangeWindow(null, this);
		textPane.requestFocus();
		super.show();
	}

	public String getText()
	{
		return text;
	}

	public boolean getReturnValue()
	{
		return returnValue;
	}

	private class ApplyAction extends AbstractAction
	{
		public ApplyAction()
		{
			super("Ok");
			putValue("DefaultAction", Boolean.TRUE);
		}

		public void actionPerformed(ActionEvent e)
		{
			text=textPane.getText();
			returnValue=true;
			dispose();
		}
	}

	private class CancelAction extends AbstractAction
	{
		public CancelAction()
		{
			super("Cancel");
		}

		public void actionPerformed(ActionEvent e)
		{
			returnValue=false;
			dispose();
		}
	}
}
