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

import com.kiwisoft.utils.gui.table.SortableTable;
import com.kiwisoft.utils.idea.PluginUtils;
import com.kiwisoft.sqlPlugin.ManageColumnsDialog;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.4 $, $Date: 2006/03/24 17:58:15 $
 */
public class ManageColumnsAction extends AnAction
{
	private Project project;
	private SortableTable table;

	public ManageColumnsAction(Project project, SortableTable table)
	{
		super("Manage Columns...");
		this.project=project;
		this.table=table;
	}

	public void actionPerformed(AnActionEvent e)
	{
		PluginUtils.showDialog(new ManageColumnsDialog(project, table), true, true);
	}
}
