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

import com.kiwisoft.db.DatabaseSchema;
import com.kiwisoft.db.DatabaseTable;
import com.kiwisoft.utils.gui.IconManager;
import com.kiwisoft.utils.gui.tree.DynamicTreeNode;
import com.kiwisoft.sqlPlugin.Icons;
import com.intellij.openapi.project.Project;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.5 $, $Date: 2006/03/24 17:58:46 $
 */
public class TablesNode extends DynamicTreeNode
{
	private Project project;
	private String tableType;

	/**
	 * Creates a new tree node.
	 */
	public TablesNode(Project project, DatabaseSchema schema, String tableType, DefaultTreeModel aTreeModel)
	{
		super(schema, aTreeModel);
		this.project=project;
		this.tableType=tableType;
	}

	/**
	 * Returns the document associated with this tree node.
	 */
	public DatabaseSchema getSchema()
	{
		return (DatabaseSchema)getUserObject();
	}

	/**
	 * Returns if this tree node has children.
	 */
	public boolean isLeaf()
	{
		return false;
	}

	/**
	 * Adds this object to the children of this tree node.
	 */
	private void insertTable(DatabaseTable table)
	{
		int size=0;
		if (children!=null) size=children.size();
		insert(new TableNode(project, table, treeModel), size);
	}

	/**
	 * Method called to load all child nodes.
	 */
	protected void loadChildren()
	{
		try
		{
			Iterator it=getSchema().getTables(project, tableType).iterator();
			while (it.hasNext()) insertTable((DatabaseTable)it.next());
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
		if ("TABLE".equals(tableType)) return "Tables";
		if ("VIEW".equals(tableType)) return "Views";
		if ("SYSTEM TABLE".equals(tableType)) return "System Tables";
		if ("SYNONYM".equals(tableType)) return "Synonyms";
		if ("ALIAS".equals(tableType)) return "Aliases";
		if ("LOCAL TEMPORARY".equals(tableType)) return "Local Temporary Tables";
		if ("GLOBAL TEMPORARY".equals(tableType)) return "Global Temporary Tables";
		return tableType;
	}

	/**
	 * Returns the icon used in the user interface.
	 */
	public ImageIcon getIcon()
	{
		return IconManager.getIcon(Icons.TABLES);
	}
}
