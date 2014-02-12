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
import java.sql.SQLException;
import java.util.Collection;

import com.kiwisoft.utils.PropertyHolder;
import com.intellij.openapi.project.Project;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.4 $, $Date: 2006/03/24 17:55:46 $
 */
public class StoredProcedure implements PropertyHolder
{
    private DatabaseSchema schema;

	private static final String NAME="Name";
    private static final String RESULT="Result";
    private static final String REMARK="Remark";

	private String name;
    private int result;
    private String remark;

    public StoredProcedure(DatabaseSchema schema, String procedureName)
    {
        this.schema=schema;
		this.name=procedureName;
	}

    public DatabaseSchema getSchema()
    {
        return schema;
    }

	public String getName()
	{
		return name;
	}

	public void setResult(int result)
	{
		this.result=result;
	}

    public void setRemark(String remark)
    {
        this.remark=remark;
    }

	public String[] getPropertyNames()
	{
		return new String[]{
			NAME, RESULT, REMARK
		};
	}

    public Object getProperty(Project project, String propertyName) throws Exception
	{
        if (NAME.equals(propertyName)) return name;
        if (RESULT.equals(propertyName))
		{
			switch (result)
			{
				case DatabaseMetaData.procedureReturnsResult: return "Returns Result";
				case DatabaseMetaData.procedureResultUnknown: return "Result Unknown";
				case DatabaseMetaData.procedureNoResult: return "Returns No Result";
			}
		}
        if (REMARK.equals(propertyName)) return remark;
        return null;
    }

	public Collection getParameters(Project project) throws SQLException
	{
		return getSchema().getDatabase().getProcedureParameters(project, this);
	}

	public boolean equals(Object o)
	{
		if (this==o) return true;
		if (!(o instanceof StoredProcedure)) return false;

		final StoredProcedure storedProcedure=(StoredProcedure)o;

		if (result!=storedProcedure.result) return false;
		if (name!=null ? !name.equals(storedProcedure.name) : storedProcedure.name!=null) return false;
		if (remark!=null ? !remark.equals(storedProcedure.remark) : storedProcedure.remark!=null) return false;
		return !(schema!=null ? !schema.equals(storedProcedure.schema) : storedProcedure.schema!=null);
	}

	public int hashCode()
	{
		return (name!=null ? name.hashCode() : 0);
	}
}
