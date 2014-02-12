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
import javax.swing.*;

import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.project.Project;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.3 $, $Date: 2006/03/24 17:59:51 $
 */
public class PasswordDialog extends DialogWrapper
{
	private JPasswordField tfPassword;
	private JPanel pnlContent;
	private JCheckBox cbNoPassword;

	private boolean returnValue;
	private String password;

	private ApplyAction applyAction;

	public PasswordDialog(Project project, String title)
	{
		super(project, false);
		setTitle(title);
		initializeComponents();
		init();
	}

	public boolean getReturnValue()
	{
		return returnValue;
	}

	public String getPassword()
	{
		return password;
	}

	public Action[] createActions()
	{
		return new Action[]{applyAction, new CancelAction()};
	}

	public JComponent createCenterPanel()
	{
		return pnlContent;
	}

	private void initializeComponents()
	{
		applyAction=new ApplyAction();
		applyAction.putValue(DEFAULT_ACTION, Boolean.TRUE);

		tfPassword=new JPasswordField(20);
		tfPassword.setAction(applyAction);

		cbNoPassword=new JCheckBox("No Password:");

		pnlContent=new JPanel(new GridBagLayout());
		pnlContent.add(new JLabel("Password:"),
					   new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(3, 3, 3, 3), 0, 0));
		pnlContent.add(tfPassword,
					   new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(3, 3, 3, 3), 0, 0));
		pnlContent.add(cbNoPassword,
					   new GridBagConstraints(0, 2, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(3, 3, 3, 3), 0, 0));
	}

	public JComponent getPreferredFocusedComponent()
	{
		return tfPassword;
	}

	private class ApplyAction extends AbstractAction
	{
		public ApplyAction()
		{
			super("Ok");
		}

		public void actionPerformed(ActionEvent e)
		{
			password=new String(tfPassword.getPassword());
			if (cbNoPassword.isSelected()) password=null;
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