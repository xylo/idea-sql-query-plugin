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

import java.util.List;
import java.util.Iterator;
import java.util.Properties;
import java.util.Map;
import javax.swing.JComponent;

import com.intellij.openapi.actionSystem.ex.ComboBoxAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.project.Project;

import com.kiwisoft.db.DatabaseManager;
import com.kiwisoft.db.Database;
import com.kiwisoft.db.driver.DatabaseDriver;
import com.kiwisoft.utils.gui.IconManager;
import com.kiwisoft.sqlPlugin.Icons;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.3 $, $Date: 2006/03/24 17:58:15 $
 */
public class SelectDatabaseListAction extends ComboBoxAction
{
	private Project project;
	private DatabaseManager databaseManager;

	public SelectDatabaseListAction(Project project, DatabaseManager databaseManager)
	{
		this.project=project;
		this.databaseManager=databaseManager;
	}

	public void update(AnActionEvent event)
	{
		super.update(event);
		Presentation presentation=event.getPresentation();
		Database database=databaseManager.getCurrentDatabase();
		if (database==null) presentation.setText("<Select Database>");
		else
		{
			if (database.isConnected()) presentation.setIcon(IconManager.getIcon(Icons.CONNECTED));
			else if (database.isDriverValid()) presentation.setIcon(IconManager.getIcon(Icons.NOT_CONNECTED));
			else presentation.setIcon(IconManager.getIcon(Icons.DRIVER_NOT_FOUND));
			presentation.setText(database.getName());
			presentation.setDescription(getToolTip(database));
		}
	}

	private static String getToolTip(Database database)
	{
		StringBuffer toolTip=new StringBuffer();
		toolTip.append("<html>");
		toolTip.append(database.getURL());
		DatabaseDriver driver=database.getDatabaseDriver();
		if (driver!=null)
		{
			Properties properties=driver.getConnectProperties(database);
			driver.addCustomProperties(database, properties);
			toolTip.append("<small>");
			for (Iterator it=properties.entrySet().iterator(); it.hasNext();)
			{
				Map.Entry entry=(Map.Entry)it.next();
				toolTip.append("<br>&nbsp;&nbsp;");
				toolTip.append(entry.getKey());
				toolTip.append("=");
				toolTip.append(entry.getValue());
			}
			toolTip.append("</small>");
		}
		toolTip.append("</html>");
		return toolTip.toString();
	}

	protected DefaultActionGroup createPopupActionGroup(JComponent jComponent)
	{
		DefaultActionGroup actionGroup=new DefaultActionGroup();
		String group=databaseManager.getCurrentGroup();
		List databases;
		if (group==null) databases=databaseManager.getDatabases();
		else databases=databaseManager.getDatabases(group);
		for (Iterator it=databases.iterator(); it.hasNext();)
		{
			Database database=(Database)it.next();
			actionGroup.add(new SelectDatabaseAction(databaseManager, database));
		}
		actionGroup.addSeparator();
		actionGroup.add(new PropertiesAction(project));
		return actionGroup;
	}
}
