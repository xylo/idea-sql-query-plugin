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
import com.kiwisoft.db.IntegerProperty;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.3 $, $Date: 2006/03/24 17:53:36 $
 */
public class InformixDriver extends DatabaseDriver
{
	public static final DriverProperty PORT=new IntegerProperty("port", "Port");
	public static final DriverProperty DATABASE=new StringProperty("database", "Database");
	public static final DriverProperty SERVER=new StringProperty("INFORMIXSERVER", "Server");
	public static final DriverProperty NEWCODESET=new StringProperty("NEWCODESET", "Code Set");
	public static final DriverProperty NEWLOCAL=new StringProperty("NEWLOCAL", "Local");

	public InformixDriver()
	{
		super("com.informix.jdbc.IfxDriver");
	}

	public String getId()
	{
		return "jdbc:informix-sqli";
	}

	public String getName()
	{
		return "Informix Driver";
	}

	public List getDriverProperties()
	{
		List list=new LinkedList();
		list.add(HOST);
		list.add(PORT);
		list.add(SERVER);
		list.add(DATABASE);
		list.add(USER);
		list.add(NEWCODESET);
		list.add(NEWLOCAL);
		list.add(AUTO_COMMIT);
		return list;
	}

	public boolean isRequired(DriverProperty property)
	{
		return property==USER || property==HOST || property==DATABASE || property==SERVER;
	}

	public String buildURL(DriverProperties map)
	{
		StringBuffer buffer=new StringBuffer("jdbc:informix-sqli://");
		Object host=map.getProperty(HOST);
		if (host!=null) buffer.append(host);
		Object port=map.getProperty(PORT);
		if (port!=null) buffer.append(":").append(port);
		buffer.append("/");
		Object database=map.getProperty(DATABASE);
		if (database!=null) buffer.append(database);
		buffer.append(":");
		Object server=map.getProperty(SERVER);
		if (server!=null) buffer.append(SERVER.getId()).append("=").append(server);
		return buffer.toString();
	}

	public Properties getConnectProperties(DriverProperties map)
	{
		Properties properties=new Properties();
		copyProperty(map, properties, NEWCODESET);
		copyProperty(map, properties, NEWLOCAL);
		return properties;
	}
}
