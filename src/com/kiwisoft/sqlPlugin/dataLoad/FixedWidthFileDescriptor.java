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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Reader;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

import com.kiwisoft.sqlPlugin.dataLoad.io.DataFileReader;
import com.kiwisoft.sqlPlugin.dataLoad.io.FixedWidthReader;
import com.kiwisoft.utils.xml.XMLWriter;
import com.kiwisoft.utils.xml.XMLContext;
import com.kiwisoft.utils.StringUtils;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.3 $, $Date: 2002/02/22 10:00:24 $
 */
public class FixedWidthFileDescriptor extends FileDescriptor
{
	private List columns;

	public DataFileReader createReader(File file) throws FileNotFoundException
	{
		FixedWidthReader reader=new FixedWidthReader(file, columns);
		reader.setRowsToSkip(getRowsToSkip());
		reader.setTitleRow(hasTitleRow());
		return reader;
	}

	public DataFileReader createReader(Reader reader) throws FileNotFoundException
	{
		FixedWidthReader fileReader=new FixedWidthReader(reader, columns);
		fileReader.setRowsToSkip(getRowsToSkip());
		fileReader.setTitleRow(hasTitleRow());
		return fileReader;
	}


	public void setColumns(List columns)
	{
		this.columns=columns;
	}

	public List getColumns()
	{
		return columns;
	}

	public boolean equals(Object o)
	{
		if (this==o) return true;
		if (o==null || getClass()!=o.getClass()) return false;
		if (!super.equals(o)) return false;

		final FixedWidthFileDescriptor that=(FixedWidthFileDescriptor)o;

		return !(columns!=null ? !columns.equals(that.columns) : that.columns!=null);
	}

	public int hashCode()
	{
		int result=super.hashCode();
		result=29*result+(columns!=null ? columns.hashCode() : 0);
		return result;
	}

	protected void writeXMLAttributes(XMLWriter xml) throws IOException
	{
		xml.setAttribute("type", "fixedWidth");
		if (columns!=null) xml.setAttribute("columns", StringUtils.enumerate(columns, ","));
		super.writeXMLAttributes(xml);
	}

	public void setXMLAttribute(XMLContext context, String name, String value)
	{
		if ("columns".equalsIgnoreCase(name))
		{
			columns=new ArrayList();
			String[] parts=value.split(",");
			for (int i=0; i<parts.length; i++) columns.add(parts[i].trim());
		}
		else super.setXMLAttribute(context, name, value);
	}

	public FileDescriptor copy()
	{
		FixedWidthFileDescriptor clone=new FixedWidthFileDescriptor();
		clone.setRowsToSkip(getRowsToSkip());
		clone.setTitleRow(hasTitleRow());
		if (columns!=null) clone.columns=new ArrayList(columns);
		return clone;
	}
}
