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

import org.xml.sax.Attributes;

import com.kiwisoft.sqlPlugin.dataLoad.io.DataFileReader;
import com.kiwisoft.utils.xml.*;

/**
 * @author Stefan Stiller
 * @version $Revision: $, $Date: $
 */
public abstract class FileDescriptor extends XMLAdapter
{
	private int rowsToSkip;
	private boolean titleRow;

	public abstract DataFileReader createReader(File file) throws FileNotFoundException;

	public abstract DataFileReader createReader(Reader reader) throws FileNotFoundException;

	public void setRowsToSkip(int rowsToSkip)
	{
		this.rowsToSkip=rowsToSkip;
	}

	public void setTitleRow(boolean titleRow)
	{
		this.titleRow=titleRow;
	}

	public int getRowsToSkip()
	{
		return rowsToSkip;
	}

	public boolean hasTitleRow()
	{
		return titleRow;
	}

	public boolean equals(Object o)
	{
		if (this==o) return true;
		if (o==null || getClass()!=o.getClass()) return false;

		final FileDescriptor that=(FileDescriptor)o;

		if (rowsToSkip!=that.rowsToSkip) return false;
		return titleRow==that.titleRow;
	}

	public int hashCode()
	{
		int result;
		result=rowsToSkip;
		result=29*result+(titleRow ? 1 : 0);
		return result;
	}

	public void writeXML(XMLWriter xml) throws IOException
	{
		xml.startElement("fileType");
		writeXMLAttributes(xml);
		xml.closeElement("fileType");
	}

	protected void writeXMLAttributes(XMLWriter xml) throws IOException
	{
		xml.setAttribute("rowsToSkip", rowsToSkip);
		xml.setAttribute("titleRow", titleRow);
	}

	public void setXMLAttribute(XMLContext context, String name, String value)
	{
		if ("rowsToSkip".equalsIgnoreCase(name)) rowsToSkip=Integer.parseInt(value);
		else if ("titleRow".equalsIgnoreCase(name)) titleRow=Boolean.valueOf(value).booleanValue();
	}

	public static XMLObjectFactory getXMLFactory()
	{
		return new XMLObjectFactory()
		{
			public XMLObject createElement(XMLContext context, String name, Attributes attributes)
			{
				String type=attributes.getValue("type");
				if ("fixedWidth".equals(type))
				{
					return new FixedWidthFileDescriptor();
				}
				return new CSVFileDescriptor();
			}
		};
	}

	public abstract FileDescriptor copy();
}
