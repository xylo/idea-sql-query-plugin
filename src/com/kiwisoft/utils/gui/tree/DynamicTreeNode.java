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
import java.awt.Font;
import java.util.Enumeration;
import java.util.Observer;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.6 $, $Date: 2006/03/24 18:02:56 $
 */
public class DynamicTreeNode extends DefaultMutableTreeNode implements Observer
{
	private boolean hasLoaded;
	private boolean loading;
	protected DefaultTreeModel treeModel;

	public DynamicTreeNode(Object anObject,DefaultTreeModel aTreeModel)
	{
		super(anObject);
		treeModel=aTreeModel;
	}

	public int getChildCount()
	{
		if (!hasLoaded)
		{
			loading=true;
			loadChildren();
		}
		return super.getChildCount();
	}

	public boolean isLeaf()
	{
		return false;
	}

	protected void loadChildren()
	{
		hasLoaded=true;
		loading=false;
	}

	public String getText()
	{
		return String.valueOf(getUserObject());
	}

	public Font getFont()
	{
		return null;
	}

	public ImageIcon getIcon()
	{
		return null;
	}

	public ImageIcon getExpandedIcon()
	{
		return getIcon();
	}

    public Color getColor()
    {
        return null;
    }

	public void setTreeModel(DefaultTreeModel aTreeModel)
	{
		treeModel=aTreeModel;
	}

	public JComponent[] getPopupMenu()
	{
		return null;
	}

	/**
	 * Adds this object to the children of this tree node.
	 */
	protected void insertNode(DynamicTreeNode treeNode)
	{
		int size=0;
		if (children!=null) size=children.size();
		if (treeNode!=null) insert(treeNode, size);
	}

	protected void insert(DefaultMutableTreeNode aNode,int anIndex)
	{
		if (loading || hasLoaded) super.insert(aNode,anIndex);
		if (hasLoaded)
		{
			int childIndices[]={anIndex};
			if (treeModel!=null) treeModel.nodesWereInserted(this,childIndices);
		}
		else
		{
			if (treeModel!=null) treeModel.nodeChanged(this);
		}
	}

	private void remove(DefaultMutableTreeNode aNode)
	{
		if  (hasLoaded && aNode!=null)
		{
			Object objects[]={aNode};
			int indices[]={getIndex(aNode)};
			super.remove(aNode);
			if (treeModel!=null) treeModel.nodesWereRemoved(this,indices,objects);
		}
		else
		{
			if (treeModel!=null) treeModel.nodeChanged(this);
		}
	}

	protected void removeChild(Object anObject)
	{
		Enumeration nodes=this.children();
		while (nodes.hasMoreElements())
		{
			DefaultMutableTreeNode node=(DefaultMutableTreeNode)nodes.nextElement();
			if (node.getUserObject()==anObject)
            {
                remove(node);
                return;
            }
        }
	}

	protected int getChildIndex(Object anObject)
	{
		Enumeration nodes=this.children();
		int index=0;
		while (nodes.hasMoreElements())
		{
			DefaultMutableTreeNode node=(DefaultMutableTreeNode)nodes.nextElement();
			if (node.getUserObject()==anObject) return index;
			index++;
        }
        return -1;
    }

    public DefaultMutableTreeNode getChild(Object anObject)
    {
		Enumeration nodes=this.children();
		while (nodes.hasMoreElements())
		{
			DefaultMutableTreeNode node=(DefaultMutableTreeNode)nodes.nextElement();
			if (node.getUserObject()==anObject) return node;
        }
        return null;
	}

    protected boolean hasChild(Object anObject)
    {
		Enumeration nodes=this.children();
		while (nodes.hasMoreElements())
		{
			DefaultMutableTreeNode node=(DefaultMutableTreeNode)nodes.nextElement();
			if (node.getUserObject()==anObject) return true;
        }
        return false;
    }

	public JFrame getEditor()
	{
		return null;
	}

    public void update(final java.util.Observable p1,final java.lang.Object p2)
    {
    }

	public DynamicTreeNode getParent(int level)
	{
		DynamicTreeNode node=this;
		for (int i=0;i<level && node!=null;i++)
		{
			if (node.getParent() instanceof DynamicTreeNode) node=(DynamicTreeNode)node.getParent();
			else node=null;
		}
		return node;
	}

	public String toString()
	{
		// Also used by Copy'n'Paste
		return getText();
	}
}
