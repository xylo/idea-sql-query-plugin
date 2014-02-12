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

/**
 * @author Stefan Stiller
 * @version $Revision: 1.5 $, $Date: 2006/03/24 17:51:18 $
 */
public class ColumnData
{
	private String column;
	private String table;
	private String schema;

	public ColumnData(String schema, String table, String column)
	{
		this.schema=schema;
		this.table=table;
		this.column=column;
	}

	public String getColumn()
	{
		return column;
	}

	public String getTable()
	{
		return table;
	}

	public String getSchema()
	{
		return schema;
	}

	public boolean equals(Object o)
	{
		if (this==o) return true;
		if (!(o instanceof ColumnData)) return false;

		final ColumnData columnData=(ColumnData)o;

		if (column!=null ? !column.equals(columnData.column) : columnData.column!=null) return false;
		if (schema!=null ? !schema.equals(columnData.schema) : columnData.schema!=null) return false;
		return !(table!=null ? !table.equals(columnData.table) : columnData.table!=null);
	}

	public int hashCode()
	{
		int result;
		result=(column!=null ? column.hashCode() : 0);
		result=29*result+(table!=null ? table.hashCode() : 0);
		result=29*result+(schema!=null ? schema.hashCode() : 0);
		return result;
	}
}
