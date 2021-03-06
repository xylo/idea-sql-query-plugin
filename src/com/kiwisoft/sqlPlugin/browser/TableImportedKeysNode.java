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

import com.kiwisoft.db.DatabaseTable;
import com.kiwisoft.db.ImportedKey;
import com.kiwisoft.sqlPlugin.Icons;
import com.kiwisoft.utils.gui.IconManager;
import com.kiwisoft.utils.gui.tree.DynamicTreeNode;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.4 $, $Date: 2006/03/24 17:56:39 $
 */
public class TableImportedKeysNode extends DynamicTreeNode
{
	private Project project;

	/**
	 * Creates a new tree node.
	 */
	public TableImportedKeysNode(Project project, DatabaseTable table, DefaultTreeModel aTreeModel)
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
	 * Adds this object to the children of this tree node.
	 */
	private void insertKey(ImportedKey key)
	{
		int size=0;
		if (children!=null) size=children.size();
		insert(new TableKeyNode(key, treeModel), size);
	}

	/**
	 * Method called to load all child nodes.
	 */
	protected void loadChildren()
	{
		try
		{
			Iterator it=getTable().getImportedKeys(project).iterator();
			while (it.hasNext()) insertKey((ImportedKey)it.next());
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
		return "Imported Keys";
	}

	/**
	 * Returns the icon used in the user interface.
	 */
	public ImageIcon getIcon()
	{
		return IconManager.getIcon(Icons.TABLE_INDICES);
	}
}
