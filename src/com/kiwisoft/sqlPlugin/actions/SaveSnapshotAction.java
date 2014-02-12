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
package com.kiwisoft.sqlPlugin.actions;

import java.io.File;
import java.io.FileWriter;
import javax.swing.JFileChooser;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataConstants;
import com.intellij.openapi.project.Project;

import com.kiwisoft.utils.gui.IconManager;
import com.kiwisoft.utils.ExtentionFileFilter;
import com.kiwisoft.utils.Utils;
import com.kiwisoft.utils.xml.XMLWriter;
import com.kiwisoft.sqlPlugin.Icons;
import com.kiwisoft.sqlPlugin.browser.BrowserPanel;
import com.kiwisoft.sqlPlugin.config.SQLPluginAppConfig;
import com.kiwisoft.db.DatabaseManager;
import com.kiwisoft.db.Database;
import com.kiwisoft.db.DatabaseSchema;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.2 $, $Date: 2006/03/24 17:58:15 $
 */
public class SaveSnapshotAction extends AnAction
{
	private BrowserPanel browserPanel;

	public SaveSnapshotAction(BrowserPanel browserPanel)
	{
		super("Snapshot");
		this.browserPanel=browserPanel;
		getTemplatePresentation().setIcon(IconManager.getIcon(Icons.SNAPSHOT));
		getTemplatePresentation().setDescription("Save current database structure into XML file.");
	}

	public void actionPerformed(AnActionEvent event)
	{
		Database database=DatabaseManager.getApplicationInstance().getCurrentDatabase();
		if (database!=null)
		{
			Project project=(Project)event.getDataContext().getData(DataConstants.PROJECT);
			if (project!=null)
			{
				try
				{
					DatabaseSchema schema=database.loadSnapshot(project);
					JFileChooser chooser=new JFileChooser();
					chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
					chooser.setDialogType(JFileChooser.SAVE_DIALOG);
					chooser.setDialogTitle("Save Database Snapshot");
					chooser.setMultiSelectionEnabled(false);
					chooser.setCurrentDirectory(Utils.getExistingPath(SQLPluginAppConfig.getInstance().getQueryPath()));
					ExtentionFileFilter xmlFileFilter=new ExtentionFileFilter("XML Files", "xml");
					chooser.addChoosableFileFilter(xmlFileFilter);
					chooser.setFileFilter(xmlFileFilter);
					if (JFileChooser.APPROVE_OPTION==chooser.showDialog(browserPanel, "Save"))
					{
						File file=chooser.getSelectedFile();
						SQLPluginAppConfig.getInstance().setQueryPath(file.getParent());
						XMLWriter xmlWriter=new XMLWriter(new FileWriter(file), null);
						xmlWriter.start();
						xmlWriter.startElement("snapshot");
						schema.writeSnapshot(xmlWriter, project);
						xmlWriter.closeElement("snapshot");
						xmlWriter.close();
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	public void update(AnActionEvent event)
	{
		super.update(event);
		event.getPresentation().setEnabled(DatabaseManager.getApplicationInstance().getCurrentDatabase()!=null);
	}
}
