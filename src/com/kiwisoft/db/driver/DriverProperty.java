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
package com.kiwisoft.db.driver;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.3 $, $Date: 2006/03/24 17:53:36 $
 */
public abstract class DriverProperty
{
	private String id;
	private String name;
	private Object defaultValue;
	protected boolean nullable;

	protected DriverProperty(String id, String name)
	{
		this.id=id;
		this.name=name;
	}

	protected DriverProperty(String id, String name, Object defaultValue)
	{
		this.id=id;
		this.name=name;
		this.defaultValue=defaultValue;
	}

	protected DriverProperty(String id, String name, Object defaultValue, boolean nullable)
	{
		this.id=id;
		this.name=name;
		this.defaultValue=defaultValue;
		this.nullable=nullable;
	}

	public String getId()
	{
		return id;
	}

	public String getName()
	{
		return name;
	}

	public abstract Class getType();

	public abstract Object convert(Object value);

	public Object getDefaultValue()
	{
		return defaultValue;
	}

	public String toString()
	{
		return getName();
	}

	public boolean isNullable()
	{
		return nullable;
	}
}
