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
 * labels and text fields. Implementations must not directly implement this interface but
 * have to implement on of the child interfaces {@link TextFormat} or {@link BooleanFormat}. 
 *
 * @author Stefan Stiller
 * @version $Revision: 1.5 $, $Date: 2006/03/24 18:33:39 $
 */
public interface ObjectFormat
{
	/**
	 * Used for default formats.
	 * @see #getName()
	 */
	public static final String DEFAULT="Default";

	/**
	 * Returns the name of the format. Return "Default" if the format should be the default format
	 * for the handled classes (works only if no default format has already been installed).
	 * @return The name of the format. Must not be <code>null</code>.
	 */
	String getName();

	/**
	 * The name of the group the format belongs to. The group defines the sub menu under which
	 * the format will appear in the context menu.
	 *
	 * @return The name of the format group. If <code>null</code> the format will appear in the root menu.
	 */
	String getGroup();

	/**
	 * @return <code>true</code> if the format can format objects of the specified class.
	 */
	boolean canFormat(Class aClass);

	/**
	 * @return <code>true</code> if the format can create objects of the specified class
	 * from <code>String</code> objects.
	 */
	boolean canParse(Class aClass);


}
