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
package com.kiwisoft.utils.gui.table;

import java.util.Vector;

import com.intellij.openapi.project.Project;

import com.kiwisoft.utils.PropertyHolder;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.3 $, $Date: 2006/03/24 18:58:21 $
 */
public class PropertiesTableModel extends SortableTableModel
{
    private String[] names={"Property", "Value"};
    private PropertyHolder propertyHolder;
	private Project project;

    public PropertiesTableModel(Project project, PropertyHolder propertyHolder)
    {
		super();
		this.project=project;
        this.propertyHolder=propertyHolder;
        createData();
    }

    public void setPropertyHolder(PropertyHolder propertyHolder)
    {
        if (this.propertyHolder!=propertyHolder)
        {
            clear();
            this.propertyHolder=propertyHolder;
            createData();
        }
    }

    private void createData()
    {
        if (propertyHolder!=null)
        {
            String[] propertyNames=propertyHolder.getPropertyNames();
            if (propertyNames!=null)
            {
                for (int i=0; i<propertyNames.length; i++)
                {
                    Vector row=new Vector();
                    String propertyName=propertyNames[i];
                    row.add(propertyName);
                    Object value;
					try
					{
						value=propertyHolder.getProperty(project, propertyName);
					}
					catch (Exception e)
					{
						value="<Error>";
					}
					row.add(value);
                    addRow(new DefaultSortableTableRow(row));
                }
            }
            sort();
        }
    }

    public int getColumnCount()
    {
        return names.length;
    }

    public String getColumnName(int col)
    {
        return names[col];
    }
}
