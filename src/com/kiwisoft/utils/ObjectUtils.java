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

/**
 * @author Stefan Stiller
 * @version $Revision: 1.3 $, $Date: 2006/03/24 18:27:20 $
 */
public class ObjectUtils
{
	private ObjectUtils()
	{
	}

	public static Class getBaseClass(Class class1, Class class2)
	{
		if (class1==null) return class2;
		if (class2==null) return class1;
		else
		{
			class1=wrapPrimitive(class1);
			class2=wrapPrimitive(class2);
			while (class1!=Object.class && !class1.isAssignableFrom(class2))
			{
				class1=class1.getSuperclass();
				if (class1==null) return Object.class;
			}
		}
		return class1;
	}

	public static Class wrapPrimitive(Class type)
	{
		if (type!=null && type.isPrimitive())
		{
			if (type==Byte.TYPE) return Byte.class;
			if (type==Integer.TYPE) return Integer.class;
			if (type==Long.TYPE) return Long.class;
			if (type==Double.TYPE) return Double.class;
			if (type==Float.TYPE) return Float.class;
			if (type==Boolean.TYPE) return Boolean.class;
			if (type==Short.TYPE) return Short.class;
			if (type==Void.TYPE) return Void.class;
		}
		return type;

	}

}
