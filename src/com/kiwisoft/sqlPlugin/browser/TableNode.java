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

import javax.swing.ImageIcon;
import javax.swing.tree.DefaultTreeModel;

import com.kiwisoft.db.DatabaseTable;
import com.kiwisoft.utils.gui.IconManager;
import com.kiwisoft.utils.gui.tree.DynamicTreeNode;
import com.kiwisoft.sqlPlugin.Icons;
import com.intellij.openapi.project.Project;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.6 $, $Date: 2006/03/24 17:58:46 $
 */
public class TableNode extends DynamicTreeNode
{
	private Project project;

	/**
	 * Creates a new tree node.
	 */
	public TableNode(Project project, DatabaseTable table, DefaultTreeModel aTreeModel)
	{
		super(table, aTreeModel);
		this.project=project;
	}

	/**
	 * Returns the document associated with this tree node.
	 */
	public DatabaseTable getTable()
	{
		return (DatabaseTable)getUserObject();
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
			DatabaseTable table=getTable();
			insertNode(new TableColumnsNode(project, table, treeModel));
			insertNode(new TableImportedKeysNode(project, table, treeModel));
			insertNode(new TableExportedKeysNode(project, table, treeModel));
			if (!"VIEW".equals(table.getType())) insertNode(new TableIndicesNode(project, table, treeModel));
		}
		catch (Exception e)
		{
			e.printStackTrace();  //To change body of catch statement use Options | File Templates.
		}
		super.loadChildren();
	}

	/**
	 * Returns the name used in the user interface.
	 */
	public String getText()
	{
		return getTable().getTableName();
	}

	/**
	 * Returns the icon used in the user interface.
	 */
	public ImageIcon getIcon()
	{
		String type=getTable().getType();
		if (type!=null)
		{
			if ("SYNONYM".equals(type) || "ALIAS".equals(type))
				return IconManager.getIcon(Icons.SYNONYM);
			if (type.indexOf("TEMPORARY")>=0)
				return IconManager.getIcon(Icons.TEMP_TABLE);
			if ("SYSTEM_TABLE".equals(type))
				return IconManager.getIcon(Icons.SYSTEM_TABLE);
			if ("VIEW".equals(type))
				return IconManager.getIcon(Icons.VIEW);
		}
		return IconManager.getIcon(Icons.TABLE);
	}
}
