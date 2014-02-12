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

import java.sql.DatabaseMetaData;
import java.io.IOException;

import com.kiwisoft.utils.PropertyHolder;
import com.kiwisoft.utils.xml.XMLWriter;

import com.intellij.openapi.project.Project;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.6 $, $Date: 2006/03/24 17:54:49 $
 */
public class Index implements PropertyHolder
{
	private DatabaseTable table;

	private static final String NAME="Name";
	private static final String NON_UNIQUE="Non Unique";
	private static final String TYPE="Type";
	private static final String COLUMN_NAME="Column";
	private static final String SORTING="Sorting";
	private static final String CARDINALITY="Cardinality";
	private static final String PAGES="Pages";
	private static final String FILTER_CONDITION="Filter Condition";

	private String name;
	private boolean nonUnique;
	private short type;
	private String columnName;
	private String sorting;
	private int cardinality;
	private int pages;
	private String filterCondition;

	public Index(DatabaseTable table, String indexName)
	{
		this.table=table;
		this.name=indexName;
	}

	public DatabaseTable getTable()
	{
		return table;
	}

	public String getName()
	{
		return name;
	}

	public String[] getPropertyNames()
	{
		return new String[]{NAME, NON_UNIQUE, TYPE, COLUMN_NAME, SORTING, CARDINALITY, PAGES, FILTER_CONDITION};
	}

	public Object getProperty(Project project, String propertyName)
	{
		if (NAME.equals(propertyName)) return name;
		if (NON_UNIQUE.equals(propertyName)) return Boolean.valueOf(nonUnique);
		if (TYPE.equals(propertyName)) return getTypeString();
		if (COLUMN_NAME.equals(propertyName)) return columnName;
		if (SORTING.equals(propertyName))
		{
			if ("A".equals(sorting)) return "Ascending";
			if ("D".equals(sorting)) return "Descending";
			return sorting;
		}
		if (CARDINALITY.equals(propertyName)) return new Integer(cardinality);
		if (PAGES.equals(propertyName)) return new Integer(pages);
		if (FILTER_CONDITION.equals(propertyName)) return filterCondition;
		return null;
	}

	public String getTypeString()
	{
		switch (type)
		{
			case DatabaseMetaData.tableIndexClustered:
				return "Clustered Index";
			case DatabaseMetaData.tableIndexHashed:
				return "Hashed Index";
			case DatabaseMetaData.tableIndexOther:
				return "Unknown Index Type";
			case DatabaseMetaData.tableIndexStatistic:
				return "Table Statistics";
		}
		return null;
	}
	public void setNonUnique(boolean value)
	{
		nonUnique=value;
	}

	public void setType(short value)
	{
		type=value;
	}

	public void setColumnName(String value)
	{
		columnName=value;
	}

	public void setSorting(String value)
	{
		sorting=value;
	}

	public void setCardinality(int value)
	{
		cardinality=value;
	}

	public void setPages(int value)
	{
		pages=value;
	}

	public void setFilterCondition(String value)
	{
		filterCondition=value;
	}

	public void writeSnapshot(XMLWriter xmlWriter) throws IOException
	{
		xmlWriter.startElement("index");
		xmlWriter.setAttribute("name", name);
		xmlWriter.setAttribute("type", type);
		xmlWriter.setAttribute("columnName", columnName);
		xmlWriter.setAttribute("sorting", sorting);
		xmlWriter.setAttribute("cardinality", cardinality);
		xmlWriter.setAttribute("pages", pages);
		xmlWriter.setAttribute("filterCondition", filterCondition);
		xmlWriter.setAttribute("nonUnique", nonUnique);
		xmlWriter.closeElement("index");
	}
}
