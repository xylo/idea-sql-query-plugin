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
package com.kiwisoft.sqlPlugin.browser;

import java.util.Iterator;
import javax.swing.ImageIcon;
import javax.swing.tree.DefaultTreeModel;

import com.kiwisoft.utils.gui.IconManager;
import com.kiwisoft.utils.gui.tree.DynamicTreeNode;
import com.kiwisoft.db.Database;
import com.kiwisoft.sqlPlugin.Icons;
import com.intellij.openapi.project.Project;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.5 $, $Date: 2006/03/24 17:56:38 $
 */
public class FunctionGroupNode extends DynamicTreeNode
{
	public static final int NUMERIC=0;
	public static final int STRING=1;
	public static final int DATE=2;
	public static final int SYSTEM=3;

	private Project project;
	private int type;

	/**
	 * Creates a new tree node.
	 */
	public FunctionGroupNode(Project project, Database database, int type, DefaultTreeModel aTreeModel)
	{
		super(database, aTreeModel);
		this.project=project;
		this.type=type;
	}

	/**
	 * Returns the document associated with this tree node.
	 */
	public Database getDatabase()
	{
		return (Database)getUserObject();
	}

	/**
	 * Returns if this tree node has children.
	 */
	public boolean isLeaf()
	{
		return false;
	}

	/**
	 * Method called to load all child nodes.
	 */
	protected void loadChildren()
	{
		try
		{
			Iterator it=null;
			if (type==NUMERIC) it=getDatabase().getNumericFunctions(project).iterator();
			else if (type==STRING) it=getDatabase().getStringFunctions(project).iterator();
			else if (type==DATE) it=getDatabase().getDateTimeFunctions(project).iterator();
			else if (type==SYSTEM) it=getDatabase().getSystemFunctions(project).iterator();
			if (it!=null)
			{
				while (it.hasNext()) insertNode(new FunctionNode((String)it.next(), treeModel));
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		super.loadChildren();
	}

	/**
	 * Returns the name used in the user interface.
	 */
	public String getText()
	{
		if (type==NUMERIC) return "Numeric Functions";
		if (type==STRING) return "String Functions";
		if (type==DATE) return "Date/Time Functions";
		if (type==SYSTEM) return "System Functions";
		return "Functions";
	}

	/**
	 * Returns the icon used in the user interface.
	 */
	public ImageIcon getIcon()
	{
		return IconManager.getIcon(Icons.FUNCTION_GROUP);
	}
}
