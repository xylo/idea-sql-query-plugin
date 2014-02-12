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
import java.io.IOException;
import java.io.Reader;

import com.kiwisoft.sqlPlugin.dataLoad.io.CSVReader;
import com.kiwisoft.sqlPlugin.dataLoad.io.DataFileReader;
import com.kiwisoft.utils.xml.XMLWriter;
import com.kiwisoft.utils.xml.XMLContext;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.3 $, $Date: 2002/02/22 10:00:24 $
 */
public class CSVFileDescriptor extends FileDescriptor
{
	private char delimiter;
	private char qualifier;

	public void setDelimiter(char delimiter)
	{
		this.delimiter=delimiter;
	}

	public void setQualifier(char qualifier)
	{
		this.qualifier=qualifier;
	}

	public char getDelimiter()
	{
		return delimiter;
	}

	public char getQualifier()
	{
		return qualifier;
	}

	public DataFileReader createReader(File file) throws FileNotFoundException
	{
		CSVReader reader=new CSVReader(file, delimiter, qualifier);
		reader.setRowsToSkip(getRowsToSkip());
		reader.setTitleRow(hasTitleRow());
		return reader;
	}

	public DataFileReader createReader(Reader reader) throws FileNotFoundException
	{
		CSVReader csvReader=new CSVReader(reader, delimiter, qualifier);
		csvReader.setRowsToSkip(getRowsToSkip());
		csvReader.setTitleRow(hasTitleRow());
		return csvReader;
	}

	public boolean equals(Object o)
	{
		if (this==o) return true;
		if (o==null || getClass()!=o.getClass()) return false;
		if (!super.equals(o)) return false;

		final CSVFileDescriptor that=(CSVFileDescriptor)o;

		if (delimiter!=that.delimiter) return false;
		return qualifier==that.qualifier;
	}

	public int hashCode()
	{
		int result=super.hashCode();
		result=29*result+(int)delimiter;
		result=29*result+(int)qualifier;
		return result;
	}

	protected void writeXMLAttributes(XMLWriter xml) throws IOException
	{
		xml.setAttribute("type", "csv");
		xml.setAttribute("delimiter", (int)delimiter);
		xml.setAttribute("qualifier", (int)qualifier);
		super.writeXMLAttributes(xml);
	}

	public void setXMLAttribute(XMLContext context, String name, String value)
	{
		if ("delimiter".equalsIgnoreCase(name)) delimiter=(char)Integer.parseInt(value);
		else if ("qualifier".equalsIgnoreCase(name)) qualifier=(char)Integer.parseInt(value);
		else super.setXMLAttribute(context, name, value);
	}

	public FileDescriptor copy()
	{
		CSVFileDescriptor clone=new CSVFileDescriptor();
		clone.setRowsToSkip(getRowsToSkip());
		clone.setTitleRow(hasTitleRow());
		clone.delimiter=delimiter;
		clone.qualifier=qualifier;
		return clone;
	}
}
