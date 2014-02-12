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
import java.util.*;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.3 $, $Date: 2002/02/22 10:00:24 $
 */
public class FixedWidthReader extends DataFileReader
{
    private List columns;

	public FixedWidthReader(File file, List columns) throws FileNotFoundException
	{
		super(file);
		if (columns==null) throw new IllegalArgumentException("'columns' must not be null.");
		this.columns=columns;
	}

	public FixedWidthReader(Reader reader, List columns)
	{
		super(reader);
		if (columns==null) throw new IllegalArgumentException("'columns' must not be null.");
		this.columns=columns;
	}

	public String[] parseLine(String line) throws IOException
	{
		String[] row=new String[columns.size()+1];
		int columnStart=0;
		int column=0;
		for (Iterator it=columns.iterator(); it.hasNext();)
		{
			int columnEnd=((Integer)it.next()).intValue();
			if (columnStart<line.length())
			{
				if (columnEnd<line.length()) row[column]=line.substring(columnStart, columnEnd);
				else row[column]=line.substring(columnStart);
			}
			else row[column]=null;
			column++;
			columnStart=columnEnd;
		}
		if (columnStart<line.length()) row[column]=line.substring(columnStart);
		return row;
	}
}
