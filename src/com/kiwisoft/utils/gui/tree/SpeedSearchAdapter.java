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

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Enumeration;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.*;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.3 $, $Date: 2006/03/24 18:50:50 $
 */
public class SpeedSearchAdapter extends KeyAdapter
{
	private String searchString="";

	public SpeedSearchAdapter()
	{
	}

	public void reset()
	{
		searchString="";
	}

	public void keyTyped(KeyEvent e)
	{
		if (e.getModifiers()==0 && e.getSource() instanceof JTree)
		{
			char ch=e.getKeyChar();
			if (ch>=32)
			{
				selectNode((JTree)e.getSource(), searchString+Character.toLowerCase(ch));
				e.consume();
			}
			else if (e.getKeyCode()==KeyEvent.VK_ESCAPE)
			{
				reset();
				e.consume();
			}
		}
	}

	private void selectNode(final JTree tree, String newSearch)
	{
		System.out.println("searchString = "+newSearch);
		TreeModel treeModel=tree.getModel();
		if (treeModel.getRoot() instanceof DefaultMutableTreeNode)
		{
			DefaultMutableTreeNode rootNode=(DefaultMutableTreeNode)treeModel.getRoot();
			Enumeration children=rootNode.depthFirstEnumeration();
			while (children.hasMoreElements())
			{
				final Object node=children.nextElement();
				if (node instanceof DefaultMutableTreeNode)
				{
					String text;
					if (node instanceof DynamicTreeNode)
					{
						DynamicTreeNode treeNode=(DynamicTreeNode)node;
						text=treeNode.getText();
					}
					else
						text=node.toString();
					System.out.println("text = "+text);
					if (text!=null && text.toLowerCase().startsWith(newSearch))
					{
						searchString=newSearch;
						System.out.println("found");
						SwingUtilities.invokeLater(new Runnable()
						{
							public void run()
							{
								tree.setSelectionPath(new TreePath(((DefaultMutableTreeNode)node).getPath()));
							}
						});
						return;
					}
				}
			}
		}
	}
}
