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
package com.kiwisoft.sqlPlugin.dataLoad;

import java.io.IOException;
import java.util.Map;

import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.utils.xml.XMLWriter;
import com.kiwisoft.utils.xml.XMLAdapter;
import com.kiwisoft.utils.xml.XMLContext;

/**
 * @author Stefan Stiller
 * @version $Revision: $, $Date: $
 */
public class TargetColumnDescriptor extends XMLAdapter
{
	private String name;
	private int jdbcType;
	private String sql;
	private String sourceColumn;
	private boolean included;

	public TargetColumnDescriptor(String name, int jdbcType)
	{
		this.name=name;
		this.included=true;
		this.jdbcType=jdbcType;
	}

	public String getName()
	{
		return name;
	}

	public int getJdbcType()
	{
		return jdbcType;
	}

	public String getSql()
	{
		return sql;
	}

	public String getSourceColumn()
	{
		return sourceColumn;
	}

	public boolean isIncluded()
	{
		return included;
	}

	public void setIncluded(boolean included)
	{
		this.included=included;
	}

	public void setSql(String sql)
	{
		this.sql=sql;
	}

	public void setSourceColumn(String sourceColumn)
	{
		this.sourceColumn=sourceColumn;
	}

	public boolean isValid()
	{
		if (isIncluded())
		{
			if (StringUtils.isEmpty(name)) return false;
			return StringUtils.isEmpty(sourceColumn)!=StringUtils.isEmpty(sql);
		}
		return true;
	}

	public void writeXML(XMLWriter xml) throws IOException
	{
		xml.startElement("column");
		xml.setAttribute("name", name);
		xml.setAttribute("included", included);
		xml.setAttribute("type", jdbcType);
		xml.setAttribute("source", sourceColumn);
		if (sql!=null) xml.setAttribute("sql", StringUtils.encodeURL(sql));
		xml.closeElement("column");
	}

	public TargetColumnDescriptor(XMLContext dummy, String aName)
	{
		super(dummy, aName);
	}

	public void setXMLAttribute(XMLContext context, String name, String value)
	{
		if ("name".equalsIgnoreCase(name)) this.name=value;
		else if ("included".equalsIgnoreCase(name)) included=Boolean.valueOf(value).booleanValue();
		else if ("type".equalsIgnoreCase(name)) jdbcType=Integer.parseInt(value);
		else if ("sql".equalsIgnoreCase(name)) sql=StringUtils.decodeURL(value);
		else if ("source".equalsIgnoreCase(name)) sourceColumn=value;
	}

	public TargetColumnDescriptor copy(Map sourceColumnMap)
	{
		TargetColumnDescriptor clone=new TargetColumnDescriptor(name, jdbcType);
		clone.sql=sql;
		clone.sourceColumn=sourceColumn;
		return clone;
	}
}

