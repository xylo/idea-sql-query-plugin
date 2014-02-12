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
package com.kiwisoft.sqlPlugin.dataLoad.io;

import java.io.*;

/**
 * @author Stefan Stiller
 * @version $Revision: $, $Date: $
 */
public abstract class DataFileReader
{
	private BufferedReader reader;
	private int rowsToSkip;
	private boolean titleRow;
	private String[] titles;
	private long position;
	private int lineNumber;
	private final static int SEPARATOR_LENGTH=System.getProperty("line.separator").length();
	private String line;
	private String titleLine;

	protected DataFileReader(File file) throws FileNotFoundException
	{
		if (file==null) throw new IllegalArgumentException("'file' must not be null.");
		reader=new BufferedReader(new FileReader(file));
	}

	protected DataFileReader(Reader reader)
	{
		if (reader==null) throw new IllegalArgumentException("'file' must not be null.");
		this.reader=new BufferedReader(reader);
	}

	protected BufferedReader getReader()
	{
		return reader;
	}

	public int getRowsToSkip()
	{
		return rowsToSkip;
	}

	public void setRowsToSkip(int rowsToSkip)
	{
		this.rowsToSkip=rowsToSkip;
	}

	public boolean isTitleRow()
	{
		return titleRow;
	}

	public void setTitleRow(boolean titleRow)
	{
		this.titleRow=titleRow;
	}

	public String[] getTitles()
	{
		return titles;
	}

	public int getLineNumber()
	{
		return lineNumber;
	}

	/**
	 * Returns the reading position in the file. May notbe correct.
	 */
	public long getPosition()
	{
		return position;
	}


	public void start() throws IOException
	{
		for (int i=0;i<rowsToSkip; i++)
		{
			line=reader.readLine();
			if (line==null) return;
		}
		if (titleRow)
		{
			titles=readRow();
			titleLine=line;
		}
	}

	public String[] readRow() throws IOException
	{
		lineNumber++;
		line=reader.readLine();
		if (line!=null)
		{
			position+=line.getBytes().length+SEPARATOR_LENGTH;
			return parseLine(line);
		}
		return null;
	}

	public String getLine()
	{
		return line;
	}

	public String getTitleLine()
	{
		return titleLine;
	}

	protected abstract String[] parseLine(String line) throws IOException;


	public void close() throws IOException
	{
		reader.close();
	}
}
