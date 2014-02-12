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
package com.kiwisoft.sqlPlugin.dataLoad;

import java.util.Date;
import java.util.Map;
import java.util.HashMap;
import java.util.TimeZone;
import java.text.*;

import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.utils.BooleanFormat;

/**
 * @author Stefan Stiller
 * @version $Revision: $, $Date: $
 */
public abstract class DataType
{
	private static Map values=new HashMap();

	private static final TimeZone GMT=TimeZone.getTimeZone("GMT");

	public static final DataType TEXT = new DataType("Text", String.class, false)
	{
		public Object parse(String value, String format)
		{
			return value;
		}
	};
	public static final DataType NUMBER = new DataType("Number", Number.class, true)
	{
		public Object parse(String value, String format) throws ParseException
		{
			if (format==null) return NumberFormat.getInstance().parse(value);
			else return new DecimalFormat(format).parse(value);
		}
	};
	public static final DataType FLAG = new DataType("Flag", Boolean.class, true)
	{
		public Object parse(String value, String format) 
		{
			if (format==null) return Boolean.valueOf(value);
			return new BooleanFormat(format).parse(value);
		}
	};
	public static final DataType DATE = new DataType("Date", Date.class, true)
	{
		public Object parse(String value, String format) throws ParseException
		{
			if (StringUtils.isEmpty(format)) return DateFormat.getDateInstance().parse(value);
			return new SimpleDateFormat(format).parse(value);
		}
	};
	public static final DataType TIME_PERIOD = new DataType("Time Period", Long.class, true)
	{
		public Object parse(String value, String format) throws ParseException
		{
			if (StringUtils.isEmpty(value)) return null;
			DateFormat dateFormat;
			if (StringUtils.isEmpty(format)) dateFormat=DateFormat.getDateInstance();
			else dateFormat=new SimpleDateFormat(format);
			dateFormat.setTimeZone(GMT);
			return new Long(dateFormat.parse(value).getTime());
		}
	};

	private final String name;
	private Class type;
	private boolean pattern;

	private DataType(String name, Class type, boolean pattern)
	{
		this.name = name;
		this.type=type;
		this.pattern = pattern;
		values.put(name, this);
	}

	public boolean usePattern()
	{
		return pattern;
	}

	public Class getType()
	{
		return type;
	}

	public String getName()
	{
		return name;
	}

	public String toString()
	{
		return name;
	}

	public abstract Object parse(String value, String format) throws ParseException;

	public static DataType valueOf(String value)
	{
		return (DataType)values.get(value);
	}

	public static DataType[] values()
	{
		return (DataType[])values.values().toArray(new DataType[0]);
	}
}
