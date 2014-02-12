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

import java.util.Collection;
import java.util.Set;
import java.util.Iterator;

import com.kiwisoft.utils.PropertyHolder;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.utils.xml.XMLWriter;

import com.intellij.openapi.project.Project;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.7 $, $Date: 2006/03/24 17:52:12 $
 */
public class DatabaseTable implements PropertyHolder
{
    private DatabaseSchema schema;

    private static final String NAME="Name";
    private static final String TYPE="Type";
    private static final String REMARK="Remark";
    private static final String PRIMARY_KEY="Primary Key";

    private String tableName;
    private String type;
    private String remark;
    private Set primaryKeys;
	private Collection importedKeys;
	private Collection exportedKeys;
	private Collection columns;
	private long rowCount;
	private Collection indices;

	public DatabaseTable(DatabaseSchema schema, String tableName)
	{
		this.schema=schema;
		this.tableName=tableName;
	}

    public DatabaseSchema getSchema()
    {
        return schema;
    }

    private Database getDatabase()
    {
        return schema.getDatabase();
    }

    public String getTableName()
    {
        return tableName;
    }

    public Collection getColumns(Project project) throws Exception
    {
		if (columns==null) columns=getDatabase().getTableColumns(project, this);
		return columns;
    }

    public Collection getIndices(Project project) throws Exception
    {
		if (indices==null) indices=getDatabase().getTableIndices(project, this);
		return indices;
    }

    public void setType(String type)
    {
        this.type=type;
    }

    public void setRemark(String remark)
    {
        this.remark=remark;
    }

    public String[] getPropertyNames()
    {
        return new String[]{
            NAME, TYPE, PRIMARY_KEY, REMARK
        };
    }

    public Object getProperty(Project project, String propertyName) throws Exception
	{
        if (NAME.equals(propertyName)) return tableName;
        if (TYPE.equals(propertyName)) return type;
        if (REMARK.equals(propertyName)) return remark;
        if (PRIMARY_KEY.equals(propertyName)) return getPrimaryKeys(project);
        return null;
    }

    public Set getPrimaryKeys(Project project) throws Exception
	{
        if (primaryKeys==null) primaryKeys=getDatabase().getPrimaryKeys(project, this);
        return primaryKeys;
    }

	public Collection getImportedKeys(Project project) throws Exception
	{
		if (importedKeys==null) importedKeys=getDatabase().getImportedKeys(project, this);
		return importedKeys;
	}

	public Collection getExportedKeys(Project project) throws Exception
	{
		if (exportedKeys==null) exportedKeys=getDatabase().getExportedKeys(project, this);
		return exportedKeys;
	}

    public String getType()
    {
        return type;
    }

	public void setRowCount(long count)
	{
		this.rowCount=count;
	}

	public long getRowCount()
	{
		return rowCount;
	}

	public void writeSnapshot(XMLWriter xmlWriter, Project project) throws Exception
	{
		xmlWriter.startElement("table");
		xmlWriter.setAttribute("name", tableName);
		xmlWriter.setAttribute("type", type);
		if ("TABLE".equals(type)) xmlWriter.setAttribute("rowCount", Long.toString(rowCount));
		Set primaryKeys=getPrimaryKeys(project);
		if (!primaryKeys.isEmpty()) xmlWriter.setAttribute("primaryKey", StringUtils.enumerate(primaryKeys, ","));
		for (Iterator it=getColumns(project).iterator(); it.hasNext();)
		{
			DatabaseColumn column=(DatabaseColumn)it.next();
			column.writeSnapshot(xmlWriter, project);
		}
		for (Iterator it=getIndices(project).iterator(); it.hasNext();)
		{
			Index index=(Index)it.next();
			index.writeSnapshot(xmlWriter);
		}
		for (Iterator it=getImportedKeys(project).iterator(); it.hasNext();)
		{
			ImportedKey key=(ImportedKey)it.next();
			key.writeSnapshot(xmlWriter);
		}
		for (Iterator it=getExportedKeys(project).iterator(); it.hasNext();)
		{
			ExportedKey key=(ExportedKey)it.next();
			key.writeSnapshot(xmlWriter);			
		}
		xmlWriter.closeElement("table");
	}
}
