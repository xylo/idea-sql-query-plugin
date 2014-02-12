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

import java.util.Map;
import java.util.HashMap;
import java.util.Collection;

/**
 * @author Stefan Stiller
 * @version $Revision: $, $Date: $
 */
public class DataRow
{
	private Map data;

	public DataRow()
	{
		this.data=new HashMap();
	}

	public void set(String name, Object value)
	{
		data.put(name, value);
	}

	public Object get(String name)
	{
		return data.get(name);
	}

	public void clear()
	{
		data.clear();
	}

	public String toString()
	{
		return data.toString();
	}

	public Collection getFields()
	{
		return data.keySet();
	}
}
