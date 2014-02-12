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
package com.kiwisoft.sqlPlugin.settings;

import java.awt.event.ActionEvent;
import javax.swing.*;

import com.intellij.openapi.help.HelpManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.3 $, $Date: 2006/03/24 18:12:25 $
 */
public class GlobalSettingsDialog extends DialogWrapper
{
	private Project project;

	private JTabbedPane pnlTabs;
	private GlobalSettingsPanel[] panels;

	public GlobalSettingsDialog(Project project)
	{
		super(project, true);
		setTitle("Properties");
		this.project=project;
		initializeComponents();
		init();
	}

	private void initializeComponents()
	{
		panels=new GlobalSettingsPanel[]
			{
				new ConnectionPropertiesPanel(),
				new ResultPropertiesPanel(),
				new MiscPropertiesPanel(project),
				new TemplateSettingsPanel(project),
				new DataLoadPropertiesPanel(project)
			};
		pnlTabs=new JTabbedPane();
		for (int i=0; i<panels.length; i++)
		{
			GlobalSettingsPanel panel=panels[i];
			panel.initializeComponents();
			panel.installListeners();
			panel.initializeData();
			pnlTabs.addTab(panel.getTitle(), panel);
		}
	}

	public JComponent createCenterPanel()
	{
		return pnlTabs;
	}

	public Action[] createActions()
	{
		return new Action[]{new HelpAction(), new ApplyAction(true), new CancelAction()};
	}

	private class ApplyAction extends AbstractAction
	{
		public ApplyAction(boolean defaultAction)
		{
			super("Ok");
			if (defaultAction) putValue(DEFAULT_ACTION, Boolean.TRUE);
		}

		public void actionPerformed(ActionEvent e)
		{
			for (int i=0; i<panels.length; i++)
			{
				GlobalSettingsPanel panel=panels[i];
				if (!panel.canApply()) pnlTabs.setSelectedComponent(panel);
			}
			try
			{
				for (int i=0; i<panels.length; i++)
				{
					GlobalSettingsPanel panel=panels[i];
					panel.apply();
				}
			}
			catch (Exception e1)
			{
				e1.printStackTrace();
				JOptionPane.showMessageDialog(pnlTabs, e1.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
			}
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

	private class HelpAction extends AbstractAction
	{
		public HelpAction()
		{
			super("Help");
		}

		public void actionPerformed(ActionEvent e)
		{
			GlobalSettingsPanel panel=(GlobalSettingsPanel)pnlTabs.getSelectedComponent();
			String helpTopic=null;
			if (panel!=null)
			{
				helpTopic=panel.getHelpTopic();
			}
			if (helpTopic==null) helpTopic="KiwiSQL.settingsDialog";
			HelpManager.getInstance().invokeHelp(helpTopic);
		}
	}

}