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

import com.kiwisoft.utils.PropertyHolder;
import com.kiwisoft.utils.Tristate;
import com.intellij.openapi.project.Project;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.5 $, $Date: 2006/03/24 17:54:50 $
 */
public class ProcedureParameter implements PropertyHolder
{
    private StoredProcedure procedure;

	private static final String NAME="Name";
	private static final String TYPE="Type";
	private static final String DATA_TYPE="Data Type (java.sql.Types)";
	private static final String TYPE_NAME="Type Name";
	private static final String PRECISION="Precision";
	private static final String LENGTH="Length";
	private static final String RADIX="Radix";
	private static final String SCALE="Scale";
	private static final String NULLABLE="Nullable";
	private static final String REMARKS="Remarks";

	private String name;
	private short type;
	private int dataType;
	private String typeName;
	private long precision;
	private int length;
	private short radix;
	private short scale;
	private short nullable;
	private String remarks;

	public ProcedureParameter(StoredProcedure procedure, String procedureName)
    {
		this.procedure=procedure;
		this.name=procedureName;
	}

	public StoredProcedure getProcedure()
	{
		return procedure;
	}

	public String getName()
	{
		return name;
	}

	public String[] getPropertyNames()
	{
		return new String[]{NAME, TYPE, DATA_TYPE, TYPE_NAME, PRECISION, LENGTH, RADIX, SCALE, NULLABLE, REMARKS};
	}

	public short getType()
	{
		return type;
	}

    public Object getProperty(Project project, String propertyName) throws Exception
	{
        if (NAME.equals(propertyName)) return name;
		if (TYPE.equals(propertyName)) return getTypeString();
		if (DATA_TYPE.equals(propertyName)) return DatabaseUtils.getTypeString(dataType)+" ["+dataType+']';
		if (TYPE_NAME.equals(propertyName)) return typeName;
		if (PRECISION.equals(propertyName)) return new Long(precision);
		if (LENGTH.equals(propertyName)) return new Integer(length);
		if (RADIX.equals(propertyName)) return new Short(radix);
		if (SCALE.equals(propertyName)) return new Short(scale);
		if (NULLABLE.equals(propertyName))
		{
			switch (nullable)
			{
				case DatabaseMetaData.procedureNoNulls: return Tristate.FALSE;
				case DatabaseMetaData.procedureNullable: return Tristate.TRUE;
				case DatabaseMetaData.procedureNullableUnknown: return Tristate.UNDEFINED;
			}
		}
		if (REMARKS.equals(propertyName)) return remarks;
        return null;
    }

	public String getTypeString()
	{
		switch (type)
		{
			case DatabaseMetaData.procedureColumnUnknown: return "Unknown";
			case DatabaseMetaData.procedureColumnIn: return "In";
			case DatabaseMetaData.procedureColumnInOut: return "In/Out";
			case DatabaseMetaData.procedureColumnOut: return "Out";
			case DatabaseMetaData.procedureColumnReturn: return "Return Value";
			case DatabaseMetaData.procedureColumnResult: return "Results";
		}
		return null;
	}


	public void setType(short value)
	{
		type=value;
	}

	public void setDataType(int value)
	{
		dataType=value;
	}

	public void setTypeName(String value)
	{
		typeName=value;
	}

	public void setPrecision(long value)
	{
		precision=value;
	}

	public void setLength(int value)
	{
		length=value;
	}

	public void setScale(short value)
	{
		scale=value;
	}

	public void setRadix(short value)
	{
		radix=value;
	}

	public void setNullable(short value)
	{
		nullable=value;
	}

	public void setRemarks(String value)
	{
		remarks=value;
	}
}
