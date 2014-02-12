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

import com.kiwisoft.db.driver.DriverProperty;

/**
 * @author Chris Maurer
 * @version $Revision: 1.3 $, $Date: 2006/03/24 17:54:50 $
 */
public class LongProperty extends DriverProperty
{
	public LongProperty(String id, String name)
	{
		super(id, name);
	}

	public LongProperty(String id, String name, Integer defaultValue)
	{
		super(id, name, defaultValue);
	}

	public Class getType()
	{
		return Long.class;
	}

	public Object convert(Object value)
	{
		try
		{
			if (value==null) return null;
			if (value instanceof Number) return new Long(((Number)value).longValue());
			if (value instanceof String)
			{
				String s=(String)value;
				if (s.trim().length()==0) return null;
				return Long.valueOf(s);
			}
		}
		catch (Exception e)
		{
		}
		throw new IllegalArgumentException();
	}
}
