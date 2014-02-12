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
package com.kiwisoft.sqlPlugin.dataLoad.wizard;

import java.awt.event.ActionEvent;
import javax.swing.*;

import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.project.Project;

import com.kiwisoft.sqlPlugin.settings.DataLoadPropertiesPanel;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.3 $, $Date: 2006/03/24 18:52:26 $
 */
public class DataLoadDescriptorsDialog extends DialogWrapper
{
	private boolean returnValue;
	private Project project;

	private DataLoadPropertiesPanel panel;

	public DataLoadDescriptorsDialog(Project project)
	{
		super(project, false);
		this.project=project;
		setTitle("Configure Data Load Formats");
		initializeComponents();
		init();
	}

	public boolean getReturnValue()
	{
		return returnValue;
	}

	public Action[] createActions()
	{
		return new Action[]{new ApplyAction(), new CancelAction()};
	}

	public JComponent createCenterPanel()
	{
		return panel;
	}

	private void initializeComponents()
	{
		panel=new DataLoadPropertiesPanel(project);
		panel.initializeComponents();
	}

	public JComponent getPreferredFocusedComponent()
	{
		return panel.getPreferredFocusedComponent();
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
			if (panel.canApply())
			{
				panel.apply();
				returnValue=true;
				dispose();
			}
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