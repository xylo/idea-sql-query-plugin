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

import javax.swing.Icon;
import javax.swing.SwingConstants;

/**
 * Default implementation of {@link ObjectFormat} interface. Uses {@link Object#toString()}
 * to format the objects. This class can format all objects.
 *
 * @author Stefan Stiller
 * @version $Revision: 1.5 $, $Date: 2006/03/24 18:05:39 $
 * @see ObjectFormat
 */
public class DefaultObjectFormat implements TextFormat
{
	private String name;

	/**
	 * @param name The name of the format.
	 */
	public DefaultObjectFormat(String name)
	{
		this.name=name;
	}

	/**
	 * Returns the name of this format.
	 * @return The name of the format.
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * The name of the group the format belongs to. The group defines the sub menu under which
	 * the format will appear in the context menu.
	 *
	 * @return <code>null</code>.
	 */
	public String getGroup()
	{
		return null;
	}

	/**
	 * Returns if this class can format object of the specified class.
	 * @param aClass The class of the objects which should be formated.
	 * @return Always <code>true<code>.
	 * @see #format(Object)
	 */
	public boolean canFormat(Class aClass)
	{
		return true;
	}

	/**
	 * Returns if the format can create objects of the specified class
	 * from <code>String</code> objects.
	 * @param aClass The class of the objects which should be created.
	 * @return Always <code>false<code>.
	 * @see #parse(String, Class)
	 */
	public boolean canParse(Class aClass)
	{
		return false;
	}

	/**
	 * Returns the <code>String</code> representing the specified value.
	 * @param value The object which should be formated.
	 * @return <code>null</code> if <code>value</code> is null otherwise
	 * the value of <code>value.toString()</code>.
	 */
	public String format(Object value)
	{
		if (value instanceof Icon) return null;
		if (value!=null) return value.toString();
		return null;
	}

	/**
	 * Returns the <code>Object</code> created from the specified <code>String</code>.
	 * @param value The <code>String</code> which should be parsed.
	 * @param targetClass The <code>Class</code> the return value should have. Can be ignored if
	 * the format handles only one class.
	 * @return This method always throws an {@link UnsupportedOperationException}.
	 */
	public Object parse(String value, Class targetClass)
	{
		throw new UnsupportedOperationException();
	}

	/**
	 * Returns the icon for the specified value;
	 * @param value The unformated value.
	 * @return Always <code>null</code>.
	 */
	public Icon getIcon(Object value)
	{
		if (value instanceof Icon) return (Icon)value;
		return null;
	}

	/**
	 * Returns the horizontal alignment of the formatted text.
	 * @param value The unformated value.
	 * @return Always {@link SwingConstants#LEADING}
	 */
	public int getHorizontalAlignment(Object value)
	{
		if (value instanceof Icon) return SwingConstants.CENTER;
		return SwingConstants.LEADING;
	}
}
