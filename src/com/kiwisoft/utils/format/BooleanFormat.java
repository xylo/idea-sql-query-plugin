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
 * Interface class for displaying objects in UI two state fields like checkboxes.
 *
 * @author Stefan Stiller
 * @version $Revision: 1.2 $, $Date: 2006/03/24 18:05:38 $
 */
public interface BooleanFormat extends ObjectFormat
{
	/**
	 * Returns the <code>Boolean</code> representing the specified value.
	 *
	 * @param value The object which should be formated.
	 * @return The Boolean representing the specified object.
	 * @see #canFormat(Class)
	 */
	Boolean format(Object value);

	/**
	 * Returns the <code>Object</code> created from the specified <code>Boolean</code>.
	 * The method may throw an <code>RuntimeException</code> if the <code>Boolean</code> is invalid.
	 *
	 * @param value	   The <code>Boolean</code> which should be parsed.
	 * @param targetClass The <code>Class</code> the return value should have. Can be ignored if
	 *                    the format handles only one class.
	 * @return The <code>Object</code> created from the <code>Boolean</code>.
	 * @see #canParse(Class)
	 */
	Object parse(Boolean value, Class targetClass);
}
