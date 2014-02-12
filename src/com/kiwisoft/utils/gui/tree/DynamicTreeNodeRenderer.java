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
package com.kiwisoft.utils.gui.tree;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.5 $, $Date: 2006/03/24 18:50:50 $
 */
public class DynamicTreeNodeRenderer extends DefaultTreeCellRenderer
{
	private final static Font defaultFont=new Font("Dialog",0,12);

	public DynamicTreeNodeRenderer()
	{
	}

	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,boolean leaf, int row,boolean hasFocus)
	{
		if (value instanceof DynamicTreeNode)
		{
			this.selected=selected;
			DynamicTreeNode node=(DynamicTreeNode)value;
			setText(node.getText());
			if (!leaf && expanded) setIcon(node.getExpandedIcon());
			else setIcon(node.getIcon());
			if (selected)
			{
				setBackgroundSelectionColor((Color)UIManager.get("Tree.selectionBackground"));
				setForeground(getTextSelectionColor());
			}
			else
			{
				setBackgroundNonSelectionColor(tree.getBackground());
				Color color=node.getColor();
				if (color==null) setForeground(tree.getForeground());
				else setForeground(color);
			}
			if (node.getFont()==null) setFont(defaultFont);
			else setFont(node.getFont());
		}
		return this;
	}

}
