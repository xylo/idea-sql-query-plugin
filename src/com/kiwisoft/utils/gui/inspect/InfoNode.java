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
package com.kiwisoft.utils.gui.inspect;

import javax.swing.ImageIcon;
import javax.swing.tree.DefaultTreeModel;

import com.kiwisoft.utils.gui.IconManager;
import com.kiwisoft.utils.gui.BaseIcons;
import com.kiwisoft.utils.gui.tree.DynamicTreeNode;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.4 $, $Date: 2006/03/24 18:52:27 $
 */
public class InfoNode extends DynamicTreeNode
{
	public static final int INFO=0;
	public static final int ERROR=1;

	private int type;

	public InfoNode(String text, int type, DefaultTreeModel aTreeModel)
    {
        super(text, aTreeModel);
	    this.type=type;
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
        return getUserObject().toString();
    }

    /**
     * Returns the icon used in the user interface.
     */
    public ImageIcon getIcon()
    {
	    switch (type)
	    {
		    case INFO:
			    return IconManager.getIcon(BaseIcons.INFO_NODE);
			case ERROR:
			    return IconManager.getIcon(BaseIcons.ERROR_NODE);
	    }
	    return null;
    }
}
