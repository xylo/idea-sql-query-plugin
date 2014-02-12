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

import java.util.Collection;
import java.util.Iterator;
import javax.swing.JComponent;

import com.intellij.openapi.actionSystem.ex.ComboBoxAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;

import com.kiwisoft.db.DatabaseManager;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.2 $, $Date: 2006/03/24 17:58:15 $
 */
public class SelectGroupListAction extends ComboBoxAction
{
	private DatabaseManager databaseManager;

	public SelectGroupListAction(DatabaseManager databaseManager)
	{
		this.databaseManager=databaseManager;
	}

	public void update(AnActionEvent event)
	{
		super.update(event);
		String group=databaseManager.getCurrentGroup();
		if (group==null) event.getPresentation().setText("<Select Group>");
		else event.getPresentation().setText(group);
	}

	protected DefaultActionGroup createPopupActionGroup(JComponent jComponent)
	{
		DefaultActionGroup actionGroup=new DefaultActionGroup();
		DatabaseManager databaseManager=this.databaseManager;
		Collection groups=databaseManager.getGroups();
		for (Iterator it=groups.iterator(); it.hasNext();)
		{
			String group=(String)it.next();
			actionGroup.add(new SelectGroupAction(databaseManager, group));
		}
		actionGroup.addSeparator();
		actionGroup.add(new SelectGroupAction(databaseManager, null));
		return actionGroup;
	}
}
