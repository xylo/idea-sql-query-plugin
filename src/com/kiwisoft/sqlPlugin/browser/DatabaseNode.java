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

import com.intellij.openapi.project.Project;

import com.kiwisoft.db.Database;
import com.kiwisoft.db.DatabaseSchema;
import com.kiwisoft.sqlPlugin.Icons;
import com.kiwisoft.utils.gui.IconManager;
import com.kiwisoft.utils.gui.inspect.InfoNode;
import com.kiwisoft.utils.gui.tree.DynamicTreeNode;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.6 $, $Date: 2006/03/24 17:56:38 $
 */
public class DatabaseNode extends DynamicTreeNode
{
	private Project project;

	/**
	 * Creates a new tree node.
	 */
	public DatabaseNode(Project project, Database database, DefaultTreeModel aTreeModel)
	{
		super(database, aTreeModel);
		this.project=project;
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
			Database database=getDatabase();
			DatabaseSchema defaultSchema=database.getDefaultSchema(project);
			if (defaultSchema!=null)
			{
				insertNode(new SchemasNode(project, database, treeModel));
				insertNode(new FunctionsNode(project, database, treeModel));
				insertNode(new DataTypesNode(project, database, treeModel));
				for (Iterator it=database.getTableTypes(project).iterator(); it.hasNext();)
				{
					String type=(String)it.next();
					insertNode(new TablesNode(project, defaultSchema, type, treeModel));
				}
				insertNode(new ProceduresNode(project, defaultSchema, treeModel));
			}
			else insertNode(new InfoNode("No connection established.", InfoNode.ERROR, treeModel));
		}
		catch (Exception e)
		{
			e.printStackTrace();
			insertNode(new InfoNode(e.getMessage(), InfoNode.ERROR, treeModel));
		}
		super.loadChildren();
	}

	/**
	 * Returns the name used in the user interface.
	 */
	public String getText()
	{
		return getDatabase().getName();
	}

	/**
	 * Returns the icon used in the user interface.
	 */
	public ImageIcon getIcon()
	{
		return IconManager.getIcon(Icons.DATABASE_NODE);
	}
}
