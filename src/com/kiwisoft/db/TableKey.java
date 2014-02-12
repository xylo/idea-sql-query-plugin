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

import com.intellij.openapi.project.Project;

import com.kiwisoft.utils.PropertyHolder;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.5 $, $Date: 2006/03/24 17:55:46 $
 */
public abstract class TableKey implements PropertyHolder
{
	protected static final String NAME="Name";
	protected static final String REFERENCE="Reference";
	protected static final String UPDATE_RULE="Update Rule";
	protected static final String DELETE_RULE="Delete Rule";
	protected static final String DEFERRABILITY="Deferrability";

	private DatabaseTable table;

	private String name;
	private short updateRule;
	private short deleteRule;
	private short deferrability;

	protected TableKey(DatabaseTable table, String keyName)
	{
		this.table=table;
		this.name=keyName;
	}

	public DatabaseTable getTable()
	{
		return table;
	}

	public String getName()
	{
		return name;
	}

	public void setUpdateRule(short updateRule)
	{
		this.updateRule=updateRule;
	}

	public void setDeleteRule(short deleteRule)
	{
		this.deleteRule=deleteRule;
	}

	public void setDeferrability(short deferrability)
	{
		this.deferrability=deferrability;
	}

	public abstract String getReference();

	public Object getProperty(Project project, String propertyName) throws Exception
	{
		if (NAME.equals(propertyName)) return name;
		if (UPDATE_RULE.equals(propertyName))
		{
			switch (updateRule)
			{
				case DatabaseMetaData.importedKeyNoAction:
				case DatabaseMetaData.importedKeyRestrict:
					return "Not Allowed";
				case DatabaseMetaData.importedKeyCascade:
					return "Cascade";
				case DatabaseMetaData.importedKeySetNull:
					return "Set to NULL";
				case DatabaseMetaData.importedKeySetDefault:
					return "Set to Default";
				default:
					return "Unknown";
			}
		}
		if (DELETE_RULE.equals(propertyName))
		{
			switch (deleteRule)
			{
				case DatabaseMetaData.importedKeyNoAction:
				case DatabaseMetaData.importedKeyRestrict:
					return "Not Allowed";
				case DatabaseMetaData.importedKeyCascade:
					return "Cascade";
				case DatabaseMetaData.importedKeySetNull:
					return "Set to NULL";
				case DatabaseMetaData.importedKeySetDefault:
					return "Set to Default";
				default:
					return "Unknown";
			}
		}
		if (DEFERRABILITY.equals(propertyName))
		{
			switch (deferrability)
			{
				case DatabaseMetaData.importedKeyInitiallyDeferred:
					return "Initially Deferred";
				case DatabaseMetaData.importedKeyInitiallyImmediate:
					return "Initially Immediate";
				case DatabaseMetaData.importedKeyNotDeferrable:
					return "Not Deferrable";
			}
		}
		return null;
	}

	public static String getRuleString(short rule)
	{
		switch (rule)
		{
			case DatabaseMetaData.importedKeyNoAction:
				return "noAction";
			case DatabaseMetaData.importedKeyRestrict:
				return "restrict";
			case DatabaseMetaData.importedKeyCascade:
				return "cascade";
			case DatabaseMetaData.importedKeySetNull:
				return "setNull";
			case DatabaseMetaData.importedKeySetDefault:
				return "setDefault";
			default:
				return Short.toString(rule);
		}
	}

	public static String getDeferrabilityString(short deferrability)
	{
		switch (deferrability)
		{
			case DatabaseMetaData.importedKeyInitiallyDeferred:
				return "initiallyDeferred";
			case DatabaseMetaData.importedKeyInitiallyImmediate:
				return "initiallyImmediate";
			case DatabaseMetaData.importedKeyNotDeferrable:
				return "notDeferrable";
			default:
				return Short.toString(deferrability);
		}
	}

	public short getUpdateRule()
	{
		return updateRule;
	}

	public short getDeleteRule()
	{
		return deleteRule;
	}

	public short getDeferrability()
	{
		return deferrability;
	}
}
