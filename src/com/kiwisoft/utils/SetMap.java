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

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.4 $, $Date: 2006/03/24 18:02:05 $
 */
public class SetMap
{
	private Map map;

	public SetMap()
	{
		map=new HashMap();
	}

	public synchronized void add(Object key, Object value)
	{
		Set set=(Set)map.get(key);
		if (set==null)
		{
			set=new HashSet();
			map.put(key,set);
		}
		set.add(value);
	}

	public synchronized Set remove(Object key)
	{
		Set set=(Set)map.remove(key);
		if (set!=null) return set;
		else return Collections.EMPTY_SET;
	}

	public synchronized void remove(Object key, Object value)
	{
		Set set=(Set)map.get(key);
		if (set!=null)
		{
			set.remove(value);
			if (set.isEmpty()) map.remove(key);
		}
	}

	public Set get(Object key)
	{
		Set set=(Set)map.get(key);
		if (set!=null) return Collections.unmodifiableSet(set);
		else return Collections.EMPTY_SET;
	}

	public boolean containsKey(Object key)
	{
		return map.containsKey(key);
	}

    public boolean contains(Object key, Object value)
    {
        Set set=(Set)map.get(key);
        if (set!=null) return set.contains(value);
        else return false;
    }

	public boolean isEmpty()
	{
		return map.isEmpty();
	}

	public Set keySet()
	{
		return map.keySet();
	}
}
