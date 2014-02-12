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
package com.kiwisoft.utils.gui.table;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.4 $, $Date: 2006/03/24 18:04:06 $
 */
public interface TableConfiguration
{
	boolean isSupportTitles();

	boolean isSupportWidths();

	boolean isColumnIndexIdentifier();

	Integer getSortDirection(Object identifier);

	void setSortDirection(Object identifier, Integer direction);

	int getSortIndex(Object identifier);

	void setSortIndex(Object identifier, int sortIndex);

	int getIndex(Object identifier);

	void setIndex(Object identifier, int viewIndex);

	int getWidth(Object identifier);

	void setWidth(Object identifier, int width);

	String getTitle(Object identifier);

	void setHidden(Object identifier, boolean hidden);

	boolean isHidden(Object identifier);
}
