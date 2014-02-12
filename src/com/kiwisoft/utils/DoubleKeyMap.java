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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.4 $, $Date: 2006/03/24 18:27:22 $
 */
public class DoubleKeyMap
{
	private Map map=new HashMap();

	public DoubleKeyMap()
	{
	}

	public Object get(Object key1, Object key2)
	{
		Map nextMap=(Map)map.get(key1);
		if (nextMap==null) return null;
		return nextMap.get(key2);
	}

	public void put(Object key1, Object key2, Object value)
	{
		Map nextMap=(Map)map.get(key1);
		if (nextMap==null)
		{
			nextMap=new HashMap();
			map.put(key1, nextMap);
		}
		nextMap.put(key2, value);
	}

	public Collection get(Object key1)
	{
		Map nextMap=(Map)map.get(key1);
		if (nextMap==null) return Collections.EMPTY_SET;
		return nextMap.values();
	}

	public Collection getKeys()
	{
		return map.keySet();
	}

	public Collection getKeys(Object key1)
	{
		Map nextMap=(Map)map.get(key1);
		if (nextMap==null) return Collections.EMPTY_SET;
		return nextMap.keySet();
	}

	public boolean containsKey(Object key1, Object key2)
	{
		Map nextMap=(Map)map.get(key1);
		if (nextMap==null) return false;
		else return nextMap.containsKey(key2);
	}

	public void remove(Object key1)
	{
		map.remove(key1);
	}

	public void remove(Object key1, Object key2)
	{
		Map nextMap=(Map)map.get(key1);
		if (nextMap!=null) nextMap.remove(key2);
	}

	public String toString()
	{
		return "DoubleKeyMap "+map;
	}

	public boolean isEmpty()
	{
		return map.isEmpty();
	}
}
