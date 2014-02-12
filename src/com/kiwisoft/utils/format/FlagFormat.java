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
package com.kiwisoft.utils.format;

/**
 * Interface class for displaying objects in UI text components like table cells, lists,
 * labels and text fields.
 *
 * @author Stefan Stiller
 * @version $Revision: 1.2 $, $Date: 2006/03/24 18:05:39 $
 */
public class FlagFormat implements BooleanFormat
{
	private String name;

	public FlagFormat(String name)
	{
		this.name=name;
	}

	public String getName()
	{
		return name;
	}

	public String getGroup()
	{
		return null;
	}

	public boolean canFormat(Class aClass)
	{
		return aClass!=null && Integer.class==aClass || Long.class==aClass || Byte.class==aClass || Short.class==aClass || String.class==aClass;
	}

	public boolean canParse(Class aClass)
	{
		return canFormat(aClass);
	}

	public Boolean format(Object value)
	{
		if (value==null) return Boolean.FALSE;
		if (value instanceof Number) return Boolean.valueOf(((Number)value).intValue()==1);
		if (value instanceof String) return Boolean.valueOf("1".equals(value));
		return null;
	}

	public Object parse(Boolean value, Class targetClass)
	{
		boolean flag=Boolean.TRUE.equals(value);
		if (targetClass==String.class) return flag ? "1" : "0";
		if (targetClass==Byte.class) return flag ? new Byte((byte)1) : new Byte((byte)0);
		if (targetClass==Short.class) return flag ? new Short((short)1) : new Short((short)0);
		if (targetClass==Integer.class) return flag ? new Integer(1) : new Integer(0);
		if (targetClass==Long.class) return flag ? new Long(1L) : new Long(0L);
		return Boolean.FALSE;
	}
}
