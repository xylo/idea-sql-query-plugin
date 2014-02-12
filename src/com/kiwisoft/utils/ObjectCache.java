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
package com.kiwisoft.utils;

import java.util.*;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.4 $, $Date: 2006/03/24 18:27:20 $
 */
public class ObjectCache
{
	private int maximalSize;
	private Map values=new HashMap();
	private LinkedList keys=new LinkedList();

	public ObjectCache(int maximalSize)
	{
		this.maximalSize=maximalSize;
	}

	public void put(Object key, Object value)
	{
		values.put(key, value);
		keys.remove(key);
		keys.addLast(key);
		if (values.size()>maximalSize)
		{
			Object oldestKey=keys.getFirst();
			keys.remove(oldestKey);
			values.remove(oldestKey);
		}
	}

	public Iterator keyIterator()
	{
		return new LinkedList(keys).iterator();
	}

	public Object get(Object key)
	{
		Object value=values.get(key);
		if (value!=null)
		{
			keys.remove(key);
			keys.addLast(key);
		}
		return value;
	}

	public Object get(Object key, boolean touch)
	{
		if (!touch) return values.get(key);
		else return get(key);
	}

	public String toString()
	{
		StringBuffer buffer=new StringBuffer();
		buffer.append("Cache (max=").append(maximalSize).append(")\n");
		Iterator it=keys.iterator();
		while (it.hasNext())
			buffer.append(it.next()).append("\n");
		return buffer.toString();
	}
}
