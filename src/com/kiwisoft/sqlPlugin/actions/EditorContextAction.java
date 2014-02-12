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

import com.intellij.openapi.actionSystem.DataConstants;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorAction;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;

import com.kiwisoft.db.QueryManager;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.sqlPlugin.SQLPlugin;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.5 $, $Date: 2006/03/24 17:58:15 $
 */
public class EditorContextAction extends EditorAction
{
	public EditorContextAction()
	{
		super(new ActionHandler());
	}

	private static class ActionHandler extends EditorActionHandler
	{
		public boolean isEnabled(Editor editor, DataContext dataContext)
		{
			return true;
		}

		public void execute(Editor editor, DataContext dataContext)
		{
			String text=editor.getSelectionModel().getSelectedText();
			if (StringUtils.isEmpty(text)) text=editor.getDocument().getText();
			if (!StringUtils.isEmpty(text))
			{
				Project project=(Project)dataContext.getData(DataConstants.PROJECT);
				ToolWindowManager toolWindowManager=ToolWindowManager.getInstance(project);
				ToolWindow toolWindow=toolWindowManager.getToolWindow(SQLPlugin.SQL_TOOL_WINDOW);
				if (toolWindow!=null)
				{
					toolWindow.show(null);
					toolWindow.activate(null);

					QueryManager queryManager=QueryManager.getInstance(project);
					queryManager.createQuery(StringUtils.convertJavaString(text));
				}
			}
		}
	}
}
