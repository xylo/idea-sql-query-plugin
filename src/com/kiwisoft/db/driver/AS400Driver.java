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

import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import com.kiwisoft.db.BooleanProperty;
import com.kiwisoft.db.ChoiceProperty;
import com.kiwisoft.db.StringProperty;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.3 $, $Date: 2006/03/24 17:52:12 $
 */
public class AS400Driver extends DatabaseDriver
{
	public static final DriverProperty SYSTEM_NAME=new StringProperty("systemName", "System Name");
	public static final DriverProperty CURSOR_HOLD=new BooleanProperty("cursor hold", "Cursor Hold", null, true);
	public static final DriverProperty LIBRARIES=new StringProperty("libraries", "Libraries");
	public static final DriverProperty TRANSACTION_ISOLATION=new ChoiceProperty("transaction isolation", "Transaction Isolation",
			new String[]{"none", "read uncommitted", "read committed", "repeatable read", "serializable"}, true);
	public static final DriverProperty DATE_FORMAT=new ChoiceProperty("date format", "Date Format",
			new String[]{"mdy", "dmy", "ymd", "usa", "iso", "eur", "jis", "julian"}, true);
	public static final DriverProperty DATE_SEPARATOR=new ChoiceProperty("date spearator", "Date Separator",
			new String[]{"/", "-", ".", ",", "b"}, true);
	public static final DriverProperty NAMING=new ChoiceProperty("naming", "Naming",
			new String[]{"sql", "system"}, true);
	public static final DriverProperty TIME_FORMAT=new ChoiceProperty("time format", "Time Format",
			new String[]{"hms", "usa", "iso", "eur", "jis"}, true);
	public static final DriverProperty TIME_SEPARATOR=new ChoiceProperty("time separator", "Time Separator",
			new String[]{":", ".", ",", "b"}, true);
	public static final DriverProperty SORT=new ChoiceProperty("sort", "Sorting",
			new String[]{"hex", "job", "language", "table"}, true);
	public static final DriverProperty SORT_LANGUAGE=new StringProperty("sort language", "Sort Language");
	public static final DriverProperty SORT_TABLE=new StringProperty("sort table", "Sort Table");
	public static final DriverProperty SORT_WEIGHT=new ChoiceProperty("sort weight", "Sort Weight",
			new String[]{"shared", "unique"}, true);
	public static final DriverProperty DATA_TRUNCATION=new BooleanProperty("data truncation", "Data Truncation", null, true);
	public static final DriverProperty ERRORS=new ChoiceProperty("errors", "Errors",
			new String[]{"basic", "full"}, true);
	public static final DriverProperty PROXY_SERVER=new StringProperty("proxy server", "Proxy Server");
	public static final DriverProperty REMARKS=new ChoiceProperty("remarks", "Remarks",
			new String[]{"sql", "system"}, true);
	public static final DriverProperty SECURE=new BooleanProperty("secure", "Secure", null, true);
	public static final DriverProperty TRANSLATE_BINARY=new BooleanProperty("translate binary", "Translate Binary", null, true);


	public AS400Driver()
	{
		super("com.ibm.as400.access.AS400JDBCDriver");
	}

	public String getId()
	{
		return "jdbc:as400";
	}

	public String getName()
	{
		return "AS/400 Driver";
	}

	public List getDriverProperties()
	{
		List list=new LinkedList();
		list.add(SYSTEM_NAME);
		list.add(USER);
		list.add(AUTO_COMMIT);
		list.add(CURSOR_HOLD);
		list.add(LIBRARIES);
		list.add(TRANSACTION_ISOLATION);
		list.add(NAMING);
		list.add(DATE_FORMAT);
		list.add(DATE_SEPARATOR);
		list.add(TIME_FORMAT);
		list.add(TIME_SEPARATOR);
		list.add(SORT);
		list.add(SORT_LANGUAGE);
		list.add(SORT_TABLE);
		list.add(SORT_WEIGHT);
		list.add(DATA_TRUNCATION);
		list.add(ERRORS);
		list.add(PROXY_SERVER);
		list.add(REMARKS);
		list.add(SECURE);
		list.add(TRANSLATE_BINARY);
		return list;
	}

	public boolean isRequired(DriverProperty property)
	{
		return property==USER || property==SYSTEM_NAME;
	}

	public String buildURL(DriverProperties map)
	{
		StringBuffer buffer=new StringBuffer("jdbc:as400://");
		Object systemName=map.getProperty(SYSTEM_NAME);
		if (systemName!=null) buffer.append(systemName);
		return buffer.toString();
	}

	public Properties getConnectProperties(DriverProperties map)
	{
		Properties properties=new Properties();
		properties.setProperty("prompt", "false");
		copyProperty(map, properties, CURSOR_HOLD);
		copyProperty(map, properties, LIBRARIES);
		copyProperty(map, properties, TRANSACTION_ISOLATION);
		copyProperty(map, properties, DATE_FORMAT);
		copyProperty(map, properties, DATE_SEPARATOR);
		copyProperty(map, properties, TIME_FORMAT);
		copyProperty(map, properties, TIME_SEPARATOR);
		copyProperty(map, properties, NAMING);
		copyProperty(map, properties, SORT);
		copyProperty(map, properties, SORT_LANGUAGE);
		copyProperty(map, properties, SORT_TABLE);
		copyProperty(map, properties, SORT_WEIGHT);
		copyProperty(map, properties, DATA_TRUNCATION);
		copyProperty(map, properties, ERRORS);
		copyProperty(map, properties, PROXY_SERVER);
		copyProperty(map, properties, REMARKS);
		copyProperty(map, properties, SECURE);
		copyProperty(map, properties, TRANSLATE_BINARY);
		return properties;
	}
}
