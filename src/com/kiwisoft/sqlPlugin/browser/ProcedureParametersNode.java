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

import com.kiwisoft.db.StoredProcedure;
import com.kiwisoft.db.ProcedureParameter;
import com.kiwisoft.utils.gui.IconManager;
import com.kiwisoft.utils.gui.tree.DynamicTreeNode;
import com.kiwisoft.sqlPlugin.Icons;
import com.intellij.openapi.project.Project;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.5 $, $Date: 2006/03/24 17:56:38 $
 */
public class ProcedureParametersNode extends DynamicTreeNode
{
	private Project project;

	/**
     * Creates a new tree node.
     */
    public ProcedureParametersNode(Project project, StoredProcedure procedure, DefaultTreeModel aTreeModel)
    {
        super(procedure, aTreeModel);
		this.project=project;
	}

    /**
     * Returns the document associated with this tree node.
     */
    public StoredProcedure getProcedure()
    {
        return (StoredProcedure)getUserObject();
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
    private void insertColumn(ProcedureParameter column)
    {
        int size=0;
        if (children!=null) size=children.size();
        insert(new ProcedureParameterNode(column, treeModel), size);
    }

    /**
     * Method called to load all child nodes.
     */
    protected void loadChildren()
    {
        try
        {
            Iterator it=getProcedure().getParameters(project).iterator();
            while (it.hasNext()) insertColumn((ProcedureParameter) it.next());
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
        return "Parameters";
    }

    /**
     * Returns the icon used in the user interface.
     */
    public ImageIcon getIcon()
    {
        return IconManager.getIcon(Icons.PARAMETERS);
    }
}
