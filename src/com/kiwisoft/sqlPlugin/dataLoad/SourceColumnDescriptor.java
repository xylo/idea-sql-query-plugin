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

import java.text.ParseException;
import java.io.IOException;

import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.utils.xml.XMLWriter;
import com.kiwisoft.utils.xml.XMLAdapter;
import com.kiwisoft.utils.xml.XMLContext;

/**
 * @author Stefan Stiller
 * @version $Revision: $, $Date: $
 */
public class SourceColumnDescriptor extends XMLAdapter
{
	private String name;
	private DataType type;
	private String pattern;
	private boolean trim;

	public SourceColumnDescriptor(String name)
	{
		this.name=name;
		this.type=DataType.TEXT;
		this.trim=true;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public DataType getType()
	{
		return type;
	}

	public void setType(DataType type)
	{
		if (this.type!=null ? !this.type.equals(type) : type!=null)
		{
			this.type = type;
			this.pattern=null;
		}
	}

	public String getPattern()
	{
		return pattern;
	}

	public void setPattern(String pattern)
	{
		this.pattern = pattern;
	}

	public boolean isTrim()
	{
		return trim;
	}

	public void setTrim(boolean trim)
	{
		this.trim = trim;
	}

	public String toString()
	{
		return name;
	}

	public Object parse(String cellValue) throws ParseException
	{
		if (cellValue==null) return null;
		if (isTrim()) cellValue=cellValue.trim();
		return getType().parse(cellValue, getPattern());
	}

	public boolean isValid()
	{
		return !StringUtils.isEmpty(name);
	}

	public void writeXML(XMLWriter xml) throws IOException
	{
		xml.startElement("sourceColumn");
		if (name!=null) xml.setAttribute("name", name);
		if (pattern!=null) xml.setAttribute("pattern", pattern);
		if (type!=null) xml.setAttribute("type", type.getName());
		xml.setAttribute("trim", trim);
		xml.closeElement("sourceColumn");
	}

	public SourceColumnDescriptor(XMLContext dummy, String aName)
	{
		super(dummy, aName);
	}

	public void setXMLAttribute(XMLContext context, String name, String value)
	{
		if ("name".equalsIgnoreCase(name)) this.name=value;
		else if ("pattern".equalsIgnoreCase(name)) this.pattern=value;
		else if ("type".equalsIgnoreCase(name)) this.type=DataType.valueOf(value);
		else if ("trim".equalsIgnoreCase(name)) this.trim=Boolean.valueOf(value).booleanValue();
	}

	public SourceColumnDescriptor copy()
	{
		SourceColumnDescriptor clone=new SourceColumnDescriptor(name);
		clone.type=type;
		clone.trim=trim;
		clone.pattern=pattern;
		return clone;
	}
}
