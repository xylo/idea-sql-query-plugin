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

import java.awt.event.ActionEvent;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.AbstractAction;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.project.Project;

/**
 * @author Stefan Stiller, Sven Krause
 * @version $Revision: 1.3 $, $Date: 2006/03/24 18:58:20 $
 */
public class ConfirmationDialog extends DialogWrapper
{
	private String returnValue;
	private boolean dontShowAgain;

	private String message;
	private String[] options;
	private String defaultOption;
	private boolean showAgainOption;

	private JPanel panel;

	public ConfirmationDialog(Project project, String title, String message, String[] options, String defaultOption, boolean showAgainOption)
	{
		super(project, false);
		setTitle(title);
		this.message=message;
		this.options=options;
		this.defaultOption=defaultOption;
		this.showAgainOption=showAgainOption;
		initializeComponents();
		init();
	}

	public String getReturnValue()
	{
		return returnValue;
	}

	public Action[] createActions()
	{
		Action[] actions=new Action[options.length];
		for (int i=0; i<options.length; i++) actions[i]=new Option(options[i]);
		return actions;
	}

	private void initializeComponents()
	{
		panel=new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));

		JLabel messageLabel=new JLabel(message);
		messageLabel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
		panel.add(messageLabel);

		if (showAgainOption)
		{
			final JCheckBox checkBox=new JCheckBox("Don't show this message again");
			checkBox.setMnemonic('d');
			checkBox.setSelected(dontShowAgain);

			checkBox.addChangeListener(new ChangeListener()
			{
				public void stateChanged(ChangeEvent e)
				{
					dontShowAgain=checkBox.isSelected();
				}
			});
			panel.add(checkBox);
		}
	}

	public JComponent createCenterPanel()
	{
		return panel;
	}

	public boolean isDontShowAgain()
	{
		return dontShowAgain;
	}

	private class Option extends AbstractAction
	{
		private String value;

		public Option(String value)
		{
			super(value);
			this.value=value;
			if (value.equals(defaultOption)) putValue(DEFAULT_ACTION, Boolean.TRUE);
		}

		public void actionPerformed(ActionEvent e)
		{
			returnValue=value;
			dispose();
		}
	}
}
