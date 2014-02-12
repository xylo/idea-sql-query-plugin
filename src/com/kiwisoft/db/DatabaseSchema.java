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
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.sql.SQLException;

import com.kiwisoft.utils.PropertyHolder;
import com.kiwisoft.utils.xml.XMLWriter;

import com.intellij.openapi.project.Project;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.5 $, $Date: 2006/03/24 17:52:12 $
 */
public class DatabaseSchema implements PropertyHolder
{
	private Database database;

	private final static String NAME="Name";
	private final static String CATALOG="Catalog";

	private String schemaName;
	private String catalog;
	private Map tablesMap=new HashMap();

	public DatabaseSchema(Database database, String schemaName, String catalog)
	{
		this.database=database;
		this.schemaName=schemaName;
		this.catalog=catalog;
	}

	public Database getDatabase()
	{
		return database;
	}

	public String getCatalog()
	{
		return catalog;
	}

	public String getName()
	{
		if (schemaName!=null)
			return schemaName;
		else
			return "Default Schema";
	}

	public String getSchemaName()
	{
		return schemaName;
	}

	public Collection getTables(Project project, String tableType) throws Exception
	{
		Collection tables=(Collection)tablesMap.get(tableType);
		if (tables==null)
		{
			tables=database.getTables(project, this, tableType);
			tablesMap.put(tableType, tables);
		}
		return tables;
	}

	public Collection getProcedures(Project project) throws SQLException
	{
		return database.getProcecures(project, this);
	}

	public String[] getPropertyNames()
	{
		return new String[]{
			NAME, CATALOG
		};
	}

	public Object getProperty(Project project, String propertyName)
	{
		if (NAME.equals(propertyName)) return schemaName;
		if (CATALOG.equals(propertyName)) return catalog;
		return null;
	}

	public void writeSnapshot(XMLWriter xmlWriter, Project project) throws Exception
	{
		xmlWriter.startElement("schema");
		xmlWriter.setAttribute("name", schemaName);
		for (Iterator itTypes=getDatabase().getTableTypes(project).iterator(); itTypes.hasNext();)
		{
			String tableType=(String)itTypes.next();
			for (Iterator it=getTables(project, tableType).iterator(); it.hasNext();)
			{
				DatabaseTable table=(DatabaseTable)it.next();
				table.writeSnapshot(xmlWriter, project);
			}
		}
		xmlWriter.closeElement("schema");
	}
}
