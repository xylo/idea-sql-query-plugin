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

import com.kiwisoft.utils.xml.XMLWriter;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.4 $, $Date: 2006/03/24 17:54:49 $
 */
public class ImportedKeyPart implements Comparable
{
	private short sequence;

	private String primaryKeyCatalog;
	private String primaryKeySchema;
	private String primaryKeyTable;
	private String primaryKeyColumn;
	private String column;

	public ImportedKeyPart(short sequence)
	{
		this.sequence=sequence;
	}

	public String getPrimaryKeyCatalog()
	{
		return primaryKeyCatalog;
	}

	public String getPrimaryKeySchema()
	{
		return primaryKeySchema;
	}

	public String getPrimaryKeyTable()
	{
		return primaryKeyTable;
	}

	public String getPrimaryKeyColumn()
	{
		return primaryKeyColumn;
	}

	public String getColumn()
	{
		return column;
	}

	public void setPrimaryKeyCatalog(String primaryKeyCatalog)
	{
		this.primaryKeyCatalog=primaryKeyCatalog;
	}

	public void setPrimaryKeySchema(String primaryKeySchema)
	{
		this.primaryKeySchema=primaryKeySchema;
	}

	public void setPrimaryKeyTable(String primaryKeyTable)
	{
		this.primaryKeyTable=primaryKeyTable;
	}

	public void setPrimaryKeyColumn(String primaryKeyColumn)
	{
		this.primaryKeyColumn=primaryKeyColumn;
	}

	public void setColumn(String column)
	{
		this.column=column;
	}

	public int compareTo(Object o)
	{
		ImportedKeyPart part=(ImportedKeyPart)o;
		if (sequence<part.sequence) return -1;
		else if (sequence>part.sequence) return 1;
		return 0;
	}

	public void writeSnapshot(XMLWriter xmlWriter) throws IOException
	{
		xmlWriter.startElement("importedKeyPart");
		xmlWriter.setAttribute("column", column);
		xmlWriter.setAttribute("primaryKeySchema", primaryKeySchema);
		xmlWriter.setAttribute("primaryKeyTable", primaryKeyTable);
		xmlWriter.setAttribute("primaryKeyColumn", primaryKeyColumn);
		xmlWriter.closeElement("importedKeyPart");
	}
}
