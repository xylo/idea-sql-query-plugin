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

import java.util.Arrays;
import java.util.List;

import com.kiwisoft.db.driver.DriverProperty;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.3 $, $Date: 2006/03/24 17:51:18 $
 */
public class ChoiceProperty extends DriverProperty
{
	private List choices;

	public ChoiceProperty(String id, String name, Object[] choices, boolean nullable)
	{
		super(id, name, null, nullable);
		this.choices=Arrays.asList(choices);
	}

	public ChoiceProperty(String id, String name, Object[] choices, Object defaultValue)
	{
		super(id, name, defaultValue);
		this.choices=Arrays.asList(choices);
	}

	public Object[] getChoices()
	{
		return choices.toArray();
	}

	public Class getType()
	{
		return String.class;
	}

	public Object convert(Object value)
	{
		if (value==null || choices.contains(value)) return value;
        throw new IllegalArgumentException();
	}
}
