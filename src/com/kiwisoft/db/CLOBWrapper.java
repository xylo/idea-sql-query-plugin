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

import java.io.IOException;
import java.io.Reader;
import java.sql.Clob;
import java.sql.SQLException;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.4 $, $Date: 2006/03/24 17:51:18 $
 */
public class CLOBWrapper
{
	private String text;
	private boolean loaded;

	public CLOBWrapper(Clob clob, boolean loadLOBs) throws SQLException
	{
		if (loadLOBs)
		{
			try
			{
				StringBuffer buffer=new StringBuffer();
				Reader reader=clob.getCharacterStream();
				char[] bytes=new char[1024];
				int length;
				while ((length=reader.read(bytes))!=-1) buffer.append(bytes, 0, length);
				text=buffer.toString();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			loaded=true;
		}
	}

    public String getText()
    {
        return text;
    }

	public String toString()
	{
		if (loaded) return "CLOB";
		else return "CLOB: <Not Loaded>";
	}

	public boolean isLoaded()
	{
		return loaded;
}
}
