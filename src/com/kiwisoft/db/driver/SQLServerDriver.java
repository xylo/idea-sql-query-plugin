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

import java.util.List;
import java.util.LinkedList;
import java.util.Properties;

import com.kiwisoft.db.Database;
import com.kiwisoft.db.ChoiceProperty;
import com.kiwisoft.db.StringProperty;
import com.kiwisoft.db.IntegerProperty;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.4 $, $Date: 2006/03/24 17:53:37 $
 */
public abstract class SQLServerDriver extends DatabaseDriver
{
	public static final DriverProperty SELECT_METHOD=new ChoiceProperty("selectmethod", "Select Method", new Object[]{"cursor", "direct"}, true);
	public static final DriverProperty DATABASE=new StringProperty("db", "Database");
	public static final DriverProperty PORT=new IntegerProperty("port", "Port");

	protected SQLServerDriver(String aClass)
	{
		super(aClass);
	}

	public List getDriverProperties()
	{
		List list=new LinkedList();
		list.add(HOST);
		list.add(PORT);
		list.add(DATABASE);
		list.add(USER);
		list.add(SELECT_METHOD);
		list.add(AUTO_COMMIT);
		return list;
	}

	public String getDefaultSchemaName(Database database)
	{
		return "dbo";
	}

	public Properties getConnectProperties(DriverProperties map)
	{
		Properties properties=new Properties();
		copyProperty(map, properties, SELECT_METHOD);
		return properties;
	}

}
