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
package com.kiwisoft.db;

import java.io.IOException;

import com.kiwisoft.utils.PropertyHolder;
import com.kiwisoft.utils.xml.XMLWriter;

import com.intellij.openapi.project.Project;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.5 $, $Date: 2006/03/24 17:51:18 $
 */
public class DatabaseColumn implements PropertyHolder
{
    private DatabaseTable table;

    private static final String COLUMN_NAME="Name";
    private static final String TYPE="Type";
    private static final String SIZE="Size";
    private static final String NULLABLE="Nullable";
    private static final String REMARK="Remark";
    private static final String DEFAULT="Default";

    private String columnName;
    private String type;
    private int size;
    private Boolean nullable;
    private String remarks;
    private String defaultValue;

    public DatabaseColumn(DatabaseTable table, String columnName)
    {
        this.table=table;
        this.columnName=columnName;
    }

    public DatabaseTable getTable()
    {
        return table;
    }

    public String getColumnName()
    {
        return columnName;
    }

    public String[] getPropertyNames()
    {
        return new String[]{
            COLUMN_NAME, TYPE, SIZE, NULLABLE, DEFAULT, REMARK
        };
    }

    public Object getProperty(Project project, String propertyName)
    {
        if (COLUMN_NAME.equals(propertyName)) return columnName;
        if (TYPE.equals(propertyName)) return type;
        if (SIZE.equals(propertyName)) return new Integer(size);
        if (REMARK.equals(propertyName)) return remarks;
        if (DEFAULT.equals(propertyName)) return defaultValue;
        if (NULLABLE.equals(propertyName))
        {
            if (nullable!=null)
                return nullable;
            else
                return "";
        }
        return null;
    }

    public void setType(String value)
    {
        type=value;
    }

    public void setSize(int value)
    {
        size=value;
    }

    public void setNullable(Boolean nullable)
    {
        this.nullable=nullable;
    }

    public void setRemarks(String string)
    {
        remarks=string;
    }

	public String toString()
	{
		return columnName;
	}

    public void setDefaultValue(String defaultValue)
    {
        this.defaultValue=defaultValue;
    }

	public void writeSnapshot(XMLWriter xmlWriter, Project project) throws IOException
	{
		xmlWriter.startElement("column");
		xmlWriter.setAttribute("name", columnName);
		xmlWriter.setAttribute("type", type);
		xmlWriter.setAttribute("size", size);
		if (nullable!=null) xmlWriter.setAttribute("nullable", nullable.toString());
		xmlWriter.setAttribute("defaultValue", defaultValue);
		xmlWriter.closeElement("column");
	}
}
