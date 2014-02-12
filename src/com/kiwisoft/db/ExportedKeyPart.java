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
public class ExportedKeyPart implements Comparable
{
	private short sequence;

	private String foreignKeyCatalog;
	private String foreignKeySchema;
	private String foreignKeyTable;
	private String foreignKeyColumn;
	private String column;

	public ExportedKeyPart(short sequence)
	{
		this.sequence=sequence;
	}

	public String getForeignKeyCatalog()
	{
		return foreignKeyCatalog;
}

	public String getForeignKeySchema()
	{
		return foreignKeySchema;
}

	public String getForeignKeyTable()
	{
		return foreignKeyTable;
}

	public String getForeignKeyColumn()
	{
		return foreignKeyColumn;
}

	public String getColumn()
	{
		return column;
}

	public void setForeignKeyCatalog(String primaryKeyCatalog)
	{
		this.foreignKeyCatalog=primaryKeyCatalog;
	}

	public void setForeignKeySchema(String foreignKeySchema)
	{
		this.foreignKeySchema=foreignKeySchema;
	}

	public void setForeignKeyTable(String foreignKeyTable)
	{
		this.foreignKeyTable=foreignKeyTable;
	}

	public void setForeignKeyColumn(String foreignKeyColumn)
	{
		this.foreignKeyColumn=foreignKeyColumn;
	}

	public void setColumn(String column)
	{
		this.column=column;
	}

	public int compareTo(Object o)
	{
		ExportedKeyPart part=(ExportedKeyPart)o;
		if (sequence<part.sequence) return -1;
		else if (sequence>part.sequence) return 1;
		return 0;
	}

	public void writeSnapshot(XMLWriter xmlWriter) throws IOException
	{
		xmlWriter.startElement("exportedKeyPart");
		xmlWriter.setAttribute("column", column);
		xmlWriter.setAttribute("foreignKeySchema", foreignKeySchema);
		xmlWriter.setAttribute("foreignKeyTable", foreignKeyTable);
		xmlWriter.setAttribute("foreignKeyColumn", foreignKeyColumn);
		xmlWriter.closeElement("exportedKeyPart");
	}
}
