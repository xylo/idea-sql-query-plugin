/*
 * Copyright (C) 1998-2006 Stefan Stiller
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

/**
 * @author Stefan Stiller
 * @version $Revision: $, $Date: $
 */
public class CollectionChangeEvent
{
	public static final int ADDED=0;
	public static final int REMOVED=1;
	public static final int CHANGED=2;
	public static final int COLLECTION_CHANGED=3;

	private Object source;
	private String propertyName;
	private Object element;
	private int type;

	public CollectionChangeEvent(Object source, String propertyName, Object element, int type)
	{
		this.source=source;
		this.propertyName=propertyName;
		this.element=element;
		this.type=type;
	}

	public Object getSource()
	{
		return source;
	}

	public String getPropertyName()
	{
		return propertyName;
	}

	public Object getElement()
	{
		return element;
	}

	public int getType()
	{
		return type;
	}
}
