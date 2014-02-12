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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.tree.DefaultTreeModel;

import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.project.Project;

import com.kiwisoft.utils.gui.tree.DynamicTree;
import com.kiwisoft.utils.gui.tree.DynamicTreeNode;
import com.kiwisoft.utils.gui.tree.DynamicTreeNodeRenderer;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.3 $, $Date: 2006/03/24 18:52:27 $
 */
public class InspectorDialog extends DialogWrapper
{
	private String name;
    private Object object;
    private JScrollPane scrlTree;

    public InspectorDialog(Project project, String name, Object object)
	{
		super(project, true);
		setTitle("Object Explorer");
		this.name=name;
		this.object=object;
		initializeComponents();
		init();
	}

    public JComponent createCenterPanel()
    {
        return scrlTree;
    }

    public Action[] createActions()
    {
        return new Action[]{new CloseAction()};
    }

    private void initializeComponents()
    {
        DynamicTree tree=new DynamicTree();
        tree.setBackground(Color.white);
        tree.setCellRenderer(new DynamicTreeNodeRenderer());
        DynamicTreeNode rootNode=new ObjectNode(name, object.getClass(), object, false, null);
        DefaultTreeModel treeModel=new DefaultTreeModel(rootNode);
        rootNode.setTreeModel(treeModel);
        tree.setModel(treeModel);

        scrlTree=new JScrollPane(tree);
		scrlTree.setPreferredSize(new Dimension(500,300));
    }

    private class CloseAction extends AbstractAction
    {
        public CloseAction()
        {
            super("Close");
	        putValue(DEFAULT_ACTION, Boolean.TRUE);
        }

        public void actionPerformed(ActionEvent e)
        {
            dispose();
        }
    }
}
