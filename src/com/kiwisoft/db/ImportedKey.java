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
public class ImportedKey extends TableKey
{
	private static final String PRIMARY_KEY = "Primary Key";

	private Set parts;
	private String primaryKeyName;

	public ImportedKey(DatabaseTable table, String keyName)
	{
		super(table, keyName);
		this.parts=new TreeSet();
	}

	public ImportedKeyPart createPart(short sequence)
	{
		ImportedKeyPart part=new ImportedKeyPart(sequence);
		parts.add(part);
		return part;
	}

	public void setPrimaryKeyName(String primaryKeyName)
	{
		this.primaryKeyName=primaryKeyName;
	}

	public String[] getPropertyNames()
	{
		return new String[]{NAME, REFERENCE, PRIMARY_KEY, UPDATE_RULE, DELETE_RULE, DEFERRABILITY};
	}

	public Object getProperty(Project project, String propertyName) throws Exception
	{
		if (REFERENCE.equals(propertyName))
		{
			return getReference();
		}
		if (PRIMARY_KEY.equals(propertyName)) return primaryKeyName;
		return super.getProperty(project, propertyName);
	}

	public String getReference()
	{
		StringBuffer foreignKey=new StringBuffer();
		StringBuffer primaryKey=new StringBuffer();
		for (Iterator it=parts.iterator(); it.hasNext();)
		{
			ImportedKeyPart keyPart=(ImportedKeyPart)it.next();
			foreignKey.append(keyPart.getColumn());
			String primaryKeySchema=keyPart.getPrimaryKeySchema();
			if (!StringUtils.isEmpty(primaryKeySchema) && !primaryKeySchema.equals(getTable().getSchema().getSchemaName()))
				primaryKey.append(primaryKeySchema).append(".");
			primaryKey.append(keyPart.getPrimaryKeyTable()).append(".").append(keyPart.getPrimaryKeyColumn());
			if (it.hasNext())
			{
				foreignKey.append(", ");
				primaryKey.append(", ");
			}
		}
		foreignKey.append(" -> ").append(primaryKey);
		return foreignKey.toString();
	}

	public void writeSnapshot(XMLWriter xmlWriter) throws IOException
	{
		xmlWriter.startElement("importedKey");
		xmlWriter.setAttribute("name", getName());
		xmlWriter.setAttribute("updateRule", getRuleString(getUpdateRule()));
		xmlWriter.setAttribute("deleteRule", getRuleString(getDeleteRule()));
		xmlWriter.setAttribute("deferrability", getDeferrabilityString(getDeferrability()));
		xmlWriter.setAttribute("primaryKeyName", primaryKeyName);
		for (Iterator it=parts.iterator(); it.hasNext();)
		{
			ImportedKeyPart part=(ImportedKeyPart)it.next();
			part.writeSnapshot(xmlWriter);
		}
		xmlWriter.closeElement("importedKey");
	}
}
