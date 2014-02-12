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
 * @version $Revision: 1.3 $, $Date: 2006/03/24 18:02:04 $
 */
public class ListMap
{
	private Map map;

	public ListMap()
	{
		map=new HashMap();
	}

	public synchronized void add(Object key, Object value)
	{
		List list=(List)map.get(key);
		if (list==null)
		{
			list=new ArrayList();
			map.put(key, list);
		}
		list.add(value);
	}

	public synchronized void remove(Object key)
	{
		map.remove(key);
	}

	public synchronized void remove(Object key, Object value)
	{
		List list=(List)map.get(key);
		if (list!=null)
		{
			list.remove(value);
			if (list.isEmpty()) map.remove(key);
		}
	}

	public List get(Object key)
	{
		List list=(List)map.get(key);
		if (list!=null)
			return Collections.unmodifiableList(list);
		else
			return Collections.EMPTY_LIST;
	}

	public boolean containsKey(Object key)
	{
		return map.containsKey(key);
	}

	public boolean contains(Object key, Object value)
	{
		List list=(List)map.get(key);
		if (list!=null)
			return list.contains(value);
		else
			return false;
	}

	public boolean isEmpty()
	{
		return map.isEmpty();
	}

	public Set keySet()
	{
		return map.keySet();
	}

	public int size()
	{
		return map.size();
	}
}
