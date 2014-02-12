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
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;

import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.project.Project;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.3 $, $Date: 2006/03/24 18:52:26 $
 */
public class FontSelectionDialog extends DialogWrapper
{
	private JList lstFonts;
	private JPanel pnlContent;

	private boolean returnValue;
	private String font;

	private String currentFont;

	public FontSelectionDialog(Project project, String currentFont)
	{
		super(project, false);
		setTitle("Select Font");
		this.currentFont=currentFont;
		initializeComponents();
		init();
	}

	public boolean getReturnValue()
	{
		return returnValue;
	}

	public String getFont()
	{
		return font;
	}

	public Action[] createActions()
	{
		return new Action[]{new ApplyAction(), new CancelAction()};
	}

	public JComponent createCenterPanel()
	{
		return pnlContent;
	}

	private void initializeComponents()
	{
		lstFonts=new JList(GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames());
		lstFonts.addListSelectionListener(new ListSelectionListener()
		{
			public void valueChanged(final ListSelectionEvent e)
			{
				if (!e.getValueIsAdjusting())
				{
					SwingUtilities.invokeLater(new Runnable()
					{
						public void run()
						{
							lstFonts.ensureIndexIsVisible(e.getFirstIndex());
						}
					});
				}
			}
		});

		pnlContent=new JPanel(new GridBagLayout());
		pnlContent.add(new JScrollPane(lstFonts),
					   new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(3, 3, 3, 3), 0, 0));

		if (currentFont!=null) lstFonts.setSelectedValue(currentFont, true);
	}

	public JComponent getPreferredFocusedComponent()
	{
		return lstFonts;
	}

	private class ApplyAction extends AbstractAction
	{
		public ApplyAction()
		{
			super("Ok");
			putValue(DEFAULT_ACTION, Boolean.TRUE);
		}

		public void actionPerformed(ActionEvent e)
		{
			font=(String)lstFonts.getSelectedValue();
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
			dispose();
		}
	}

}