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
package com.kiwisoft.sqlPlugin;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.project.Project;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.3 $, $Date: 2006/03/24 18:12:23 $
 */
public class RenameDialog extends DialogWrapper
{
	private JTextField textField;

	private String returnValue;
	private String oldValue;

	private ApplyAction applyAction;
	private JPanel pnlContent;

	public RenameDialog(Project project, String title, String oldValue)
	{
		super(project, false);
		setTitle(title);
		this.oldValue=oldValue;
		initializeComponents();
		init();
	}

	public String getReturnValue()
	{
		return returnValue;
	}

	public Action[] createActions()
	{
		return new Action[]{applyAction, new CancelAction()};
	}

	public JComponent createCenterPanel()
	{
		return pnlContent;
	}

	public JComponent getPreferredFocusedComponent()
	{
		return textField;
	}

	private void initializeComponents()
	{
		applyAction=new ApplyAction();
		applyAction.putValue(DEFAULT_ACTION, Boolean.TRUE);

		textField=new JTextField(20);
		textField.setAction(applyAction);
		textField.setText(oldValue);

		pnlContent=new JPanel(new GridBagLayout());
		pnlContent.add(new JLabel("New Name:"),
					   new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(3, 3, 3, 3), 0, 0));
		pnlContent.add(textField,
					   new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(3, 3, 3, 3), 0, 0));
	}

	private class ApplyAction extends AbstractAction
	{
		public ApplyAction()
		{
			super("Ok");
		}

		public void actionPerformed(ActionEvent e)
		{
			returnValue=textField.getText();
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