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

import com.intellij.openapi.project.Project;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataConstants;

import com.kiwisoft.utils.gui.IconManager;
import com.kiwisoft.utils.idea.PluginUtils;
import com.kiwisoft.sqlPlugin.settings.ExportConfigurationDialog;
import com.kiwisoft.sqlPlugin.Icons;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.5 $, $Date: 2006/03/24 17:58:14 $
 */
public class ConfigureExportAction extends AnAction
{
	public ConfigureExportAction()
	{
		super("Configure...");
		getTemplatePresentation().setIcon(IconManager.getIcon(Icons.CONFIGURATION));
	}

	public void actionPerformed(AnActionEvent event)
	{
		Project project=(Project)event.getDataContext().getData(DataConstants.PROJECT);
		PluginUtils.showDialog(new ExportConfigurationDialog(project), false, true);
	}
}
