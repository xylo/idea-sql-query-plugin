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

import com.kiwisoft.db.TableKey;
import com.kiwisoft.utils.gui.IconManager;
import com.kiwisoft.utils.gui.tree.DynamicTreeNode;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.sqlPlugin.Icons;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.5 $, $Date: 2006/03/24 17:56:39 $
 */
public class TableKeyNode extends DynamicTreeNode
{
	/**
     * Creates a new tree node.
     */
    public TableKeyNode(TableKey key, DefaultTreeModel aTreeModel)
    {
        super(key, aTreeModel);
	}

    /**
     * Returns the document associated with this tree node.
     */
    public TableKey getTableKey()
    {
        return (TableKey)getUserObject();
    }

    /**
     * Returns if this tree node has children.
     */
    public boolean isLeaf()
    {
        return true;
    }

    /**
     * Returns the name used in the user interface.
     */
    public String getText()
    {
		TableKey tableKey=getTableKey();
		String name=tableKey.getName();
		if (StringUtils.isEmpty(name)) name="<"+tableKey.getReference()+">";
		return name;
    }

    /**
     * Returns the icon used in the user interface.
     */
    public ImageIcon getIcon()
    {
        return IconManager.getIcon(Icons.TABLE_INDEX);
    }
}
