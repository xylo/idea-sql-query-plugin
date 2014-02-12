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

import java.text.DateFormat;
import java.util.Date;
import javax.swing.SwingConstants;

import com.kiwisoft.utils.DateUtils;
import com.kiwisoft.utils.StringUtils;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.3 $, $Date: 2006/03/24 18:05:39 $
 */
public class DefaultDateFormat extends DefaultObjectFormat
{
	private DateFormat dateFormat;
	private String name;

	public DefaultDateFormat(String name, DateFormat dateFormat)
	{
		super(name);
		this.dateFormat=dateFormat;
		this.name=name;
	}

	public DefaultDateFormat(String name)
	{
		super(name);
		this.dateFormat=DateFormat.getDateTimeInstance();
	}

	public String getName()
	{
		if (name!=null) return name;
		return super.getName();
	}

	public boolean canFormat(Class aClass)
	{
		return aClass!=null && Date.class.isAssignableFrom(aClass);
	}

	public String format(Object value)
	{
		if (value instanceof Date) return dateFormat.format((Date)value);
		return super.format(value);
	}

	public boolean canParse(Class aClass)
	{
		return aClass!=null && Date.class.isAssignableFrom(aClass);
	}

	public Object parse(String value, Class targetClass)
	{
		if (StringUtils.isEmpty(value)) return null;
		return DateUtils.parseDate(value);
	}

	public int getHorizontalAlignment(Object value)
	{
		return SwingConstants.TRAILING;
	}
}
