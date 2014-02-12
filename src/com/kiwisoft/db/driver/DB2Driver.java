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

import com.kiwisoft.db.StringProperty;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.3 $, $Date: 2006/03/24 17:52:12 $
 */
public class DB2Driver extends DatabaseDriver
{
	public static final DriverProperty DATABASE=new StringProperty("database", "Database");

	public DB2Driver()
	{
		super("COM.ibm.db2.jdbc.app.DB2Driver");
	}

	public String getId()
	{
		return "jdbc:db2";
	}

	public String getName()
	{
		return "DB2 Driver";
	}

	public List getDriverProperties()
	{
		List list=new LinkedList();
		list.add(DATABASE);
		list.add(USER);
		list.add(AUTO_COMMIT);
		return list;
	}

	public boolean isRequired(DriverProperty property)
	{
		return property==USER || property==DATABASE;
	}

	public String buildURL(DriverProperties map)
	{
		StringBuffer buffer=new StringBuffer("jdbc:db2:");
		Object db=map.getProperty(DATABASE);
		if (db!=null) buffer.append(db);
		return buffer.toString();
	}

	public Properties getConnectProperties(DriverProperties map)
	{
		Properties properties=new Properties();
		properties.setProperty("prompt", "false");
		return properties;
	}
}
