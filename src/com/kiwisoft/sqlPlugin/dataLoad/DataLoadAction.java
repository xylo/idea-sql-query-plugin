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
package com.kiwisoft.sqlPlugin.dataLoad;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;

import com.kiwisoft.utils.gui.IconManager;
import com.kiwisoft.utils.idea.PluginUtils;
import com.kiwisoft.db.DatabaseManager;
import com.kiwisoft.db.Database;
import com.kiwisoft.sqlPlugin.dataLoad.wizard.DataLoadWizard;
import com.kiwisoft.sqlPlugin.Icons;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.3 $, $Date: 2002/02/22 10:00:24 $
 */
public class DataLoadAction extends AnAction
{
	private Project project;

	public DataLoadAction(Project project)
	{
		super("Import");
		this.project=project;
		getTemplatePresentation().setIcon(IconManager.getIcon(Icons.DATA_IMPORT));
	}

	public void actionPerformed(AnActionEvent event)
	{
		Database database=DatabaseManager.getApplicationInstance().getCurrentDatabase();
		// todo: edited line
		VirtualFile[] files=FileChooser.chooseFiles(new FileChooserDescriptor(true, false, true, true, true, false), project, null);
		if (files.length==1 && database!=null) PluginUtils.showDialog(new DataLoadWizard(project, database, files[0]), false, false);
	}

	public void update(AnActionEvent event)
	{
		super.update(event);
		event.getPresentation().setEnabled(DatabaseManager.getApplicationInstance().getCurrentDatabase()!=null);
	}
}
