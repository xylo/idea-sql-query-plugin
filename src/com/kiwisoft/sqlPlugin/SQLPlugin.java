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

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.openapi.wm.ToolWindow;

import com.kiwisoft.utils.gui.IconManager;
import com.kiwisoft.sqlPlugin.browser.BrowserPanel;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.3 $, $Date: 2006/03/24 18:15:17 $
 */
public class SQLPlugin implements ProjectComponent
{
	private Project project;

	public static final String SQL_TOOL_WINDOW = "SQL";
	public static final String BROWSER_TOOL_WINDOW = "SQL Schema";

	public SQLPlugin(Project project)
	{
		this.project = project;
	}

	public void projectOpened()
	{
		ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(project);

		ToolWindow toolWindow=toolWindowManager.registerToolWindow(SQL_TOOL_WINDOW, SQLPluginPanel.getInstance(project, true), ToolWindowAnchor.BOTTOM);
		toolWindow.setTitle("Console");
		toolWindow.setIcon(IconManager.getIcon(Icons.APPLICATION));
	}

	public void projectClosed()
	{
		// Unregister all project specific components
		SQLPluginPanel.closeInstance(project);
		BrowserPanel.closeInstance(project);

		// Unregister all tool windows
		ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(project);
		if (toolWindowManager.getToolWindow(SQL_TOOL_WINDOW)!=null) toolWindowManager.unregisterToolWindow(SQL_TOOL_WINDOW);
		if (toolWindowManager.getToolWindow(BROWSER_TOOL_WINDOW)!=null) toolWindowManager.unregisterToolWindow(BROWSER_TOOL_WINDOW);
	}

	public void initComponent()
	{
	}

	public void disposeComponent()
	{
	}

	public String getComponentName()
	{
		return "SQL";
	}

}