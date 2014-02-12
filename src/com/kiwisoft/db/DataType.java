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
import com.kiwisoft.utils.Tristate;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.4 $, $Date: 2006/03/24 17:52:12 $
 */
public class DataType implements PropertyHolder
{
    private static final String NAME="Name";
    private static final String DATA_TYPE="Data Type (java.sql.Types)";
    private static final String PRECISION="Precision";
    private static final String LITERAL_PREFIX="Literal Prefix";
    private static final String LITERAL_SUFFIX="Literal Suffix";
	private static final String CREATE_PARAMS="Create Parameters";
	private static final String NULLABLE="Nullable";
	private static final String CASE_SENSITIVE="Case Sensitive";
	private static final String SEARCHABLE="Searchable";
	private static final String UNSIGNED="Unsigned";
	private static final String FIXED_PRECISION_SCALE="Fixed Precision Scale";
	private static final String AUTO_INCREMENT="Auto Increment";
	private static final String LOCAL_TYPE_NAME="Local Type Name";
	private static final String MINIMUM_SCALE="Minimum Scale";
	private static final String MAXIMUM_SCALE="Maximum Scale";

    private String name;
	private int dataType;
	private long precision;
	private String literalPrefix;
	private String literalSuffix;
	private String createParams;
	private short nullable;
	private boolean caseSensitive;
	private short searchable;
	private boolean unsigned;
	private boolean fixedPrecisionScale;
	private boolean autoIncrement;
	private String localTypeName;
	private short miniumScale;
	private short maximumScale;

	public DataType(String name)
    {
		this.name=name;
	}

	public String getName()
	{
		return name;
	}

	public void setDataType(int type)
	{
		dataType=type;
	}

    public String[] getPropertyNames()
    {
        return new String[]{
            NAME, DATA_TYPE, PRECISION, LITERAL_PREFIX, LITERAL_SUFFIX,
			CREATE_PARAMS, NULLABLE, CASE_SENSITIVE, SEARCHABLE,
			UNSIGNED, FIXED_PRECISION_SCALE, AUTO_INCREMENT,
			LOCAL_TYPE_NAME, MINIMUM_SCALE, MAXIMUM_SCALE
        };
    }

    public Object getProperty(Project project, String propertyName) throws Exception
	{
        if (NAME.equals(propertyName)) return name;
        if (DATA_TYPE.equals(propertyName)) return DatabaseUtils.getTypeString(dataType)+" ["+dataType+']';
		if (PRECISION.equals(propertyName)) return new Long(precision);
		if (LITERAL_PREFIX.equals(propertyName)) return literalPrefix;
		if (LITERAL_SUFFIX.equals(propertyName)) return literalSuffix;
		if (CREATE_PARAMS.equals(propertyName)) return createParams;
		if (NULLABLE.equals(propertyName))
		{
			switch (nullable)
			{
				case DatabaseMetaData.attributeNullable: return Tristate.TRUE;
				case DatabaseMetaData.attributeNoNulls: return Tristate.FALSE;
				case DatabaseMetaData.attributeNullableUnknown: return Tristate.UNDEFINED;
			}
		}
		if (CASE_SENSITIVE.equals(propertyName)) return Boolean.valueOf(caseSensitive);
		if (SEARCHABLE.equals(propertyName))
		{
			switch (searchable)
			{
				case DatabaseMetaData.typePredNone: return "Not supported";
				case DatabaseMetaData.typePredChar: return "Only supported with WHERE ... LIKE";
				case DatabaseMetaData.typePredBasic: return "Supported except for WHERE ... LIKE";
				case DatabaseMetaData.typeSearchable: return "Fully supported";
			}
		}
		if (UNSIGNED.equals(propertyName)) return Boolean.valueOf(unsigned);
		if (FIXED_PRECISION_SCALE.equals(propertyName)) return Boolean.valueOf(fixedPrecisionScale);
		if (AUTO_INCREMENT.equals(propertyName)) return Boolean.valueOf(autoIncrement);
		if (LOCAL_TYPE_NAME.equals(propertyName)) return localTypeName;
		if (MINIMUM_SCALE.equals(propertyName)) return new Short(miniumScale);
		if (MAXIMUM_SCALE.equals(propertyName)) return new Short(maximumScale);
        return null;
    }

	public void setPrecision(long precision)
	{
		this.precision=precision;
	}

	public void setLiteralPrefix(String value)
	{
		literalPrefix=value;
	}

	public void setLiteralSuffix(String value)
	{
		literalSuffix=value;
	}

	public void setCreateParams(String value)
	{
		createParams=value;
	}

	public void setNullable(short value)
	{
		nullable=value;
	}

	public void setCaseSensitive(boolean value)
	{
		caseSensitive=value;
	}

	public void setSearchable(short value)
	{
		searchable=value;
	}

	public void setUnsigned(boolean value)
	{
		unsigned=value;
	}

	public void setFixedPrecisionScale(boolean value)
	{
		fixedPrecisionScale=value;
	}

	public void setAutoIncrement(boolean value)
	{
		autoIncrement=value;
	}

	public void setLocalTypeName(String value)
	{
		localTypeName=value;
	}

	public void setMinimumScale(short value)
	{
		miniumScale=value;
	}

	public void setMaximumScale(short value)
	{
		maximumScale=value;
	}
}
