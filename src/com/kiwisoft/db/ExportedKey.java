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

import java.util.Set;
import java.util.TreeSet;
import java.util.Iterator;
import java.io.IOException;

import com.intellij.openapi.project.Project;

import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.utils.xml.XMLWriter;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.5 $, $Date: 2006/03/24 17:54:49 $
 */
public class ExportedKey extends TableKey
{
	private static final String FOREIGN_KEY = "Foreign Key";

	private Set parts;
	private String foreignKeyName;

	public ExportedKey(DatabaseTable table, String keyName)
	{
		super(table, keyName);
		this.parts=new TreeSet();
	}

	public ExportedKeyPart createPart(short sequence)
	{
		ExportedKeyPart part=new ExportedKeyPart(sequence);
		parts.add(part);
		return part;
	}

	public void setForeignKeyName(String foreignKeyName)
	{
		this.foreignKeyName=foreignKeyName;
	}

	public String[] getPropertyNames()
	{
		return new String[]{NAME, REFERENCE, FOREIGN_KEY, UPDATE_RULE, DELETE_RULE, DEFERRABILITY};
	}

	public Object getProperty(Project project, String propertyName) throws Exception
	{
		if (REFERENCE.equals(propertyName)) return getReference();
		if (FOREIGN_KEY.equals(propertyName)) return foreignKeyName;
		return super.getProperty(project, propertyName);
	}

	public String getReference()
	{
		StringBuffer foreignKey=new StringBuffer();
		StringBuffer primaryKey=new StringBuffer();
		for (Iterator it=parts.iterator(); it.hasNext();)
		{
			ExportedKeyPart keyPart=(ExportedKeyPart)it.next();
			primaryKey.append(keyPart.getColumn());
			String foreignKeySchema=keyPart.getForeignKeySchema();
			if (!StringUtils.isEmpty(foreignKeySchema) && !foreignKeySchema.equals(getTable().getSchema().getSchemaName()))
				foreignKey.append(foreignKeySchema).append(".");
			foreignKey.append(keyPart.getForeignKeyTable()).append(".").append(keyPart.getForeignKeyColumn());
			if (it.hasNext())
			{
				foreignKey.append(", ");
				primaryKey.append(", ");
			}
		}
		primaryKey.append(" <- ").append(foreignKey);
		return primaryKey.toString();
	}

	public void writeSnapshot(XMLWriter xmlWriter) throws IOException
	{
		xmlWriter.startElement("exportedKey");
		xmlWriter.setAttribute("name", getName());
		xmlWriter.setAttribute("updateRule", getRuleString(getUpdateRule()));
		xmlWriter.setAttribute("deleteRule", getRuleString(getDeleteRule()));
		xmlWriter.setAttribute("deferrability", getDeferrabilityString(getDeferrability()));
		xmlWriter.setAttribute("foreignKeyName", foreignKeyName);
		for (Iterator it=parts.iterator(); it.hasNext();)
		{
			ExportedKeyPart part=(ExportedKeyPart)it.next();
			part.writeSnapshot(xmlWriter);
		}
		xmlWriter.closeElement("exportedKey");
	}
}
