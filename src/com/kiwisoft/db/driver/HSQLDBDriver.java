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
import com.kiwisoft.db.IntegerProperty;
import com.kiwisoft.db.StringProperty;

/**
 * @author Stefan Stiller, Sergey Bervinov
 * @version $Revision: 1.5 $, $Date: 2006/03/24 17:53:36 $
 */
public class HSQLDBDriver extends DatabaseDriver
{
	public static final String SERVER="Server";
	public static final String SERVER_SECURE="Server (Secure)";
	public static final String WEBSERVER="WebServer";
	public static final String WEBSERVER_SECURE="WebServer (Secure)";
	public static final String FILE="In-Process (File)";
	public static final String RESOURCE="In-Process (Resource)";
	public static final String IN_PROCESS="In-Process (Old)";

	public static final DriverProperty TYPE=new ChoiceProperty("type", "Type",
															   new String[]{SERVER, SERVER_SECURE,
																   WEBSERVER, WEBSERVER_SECURE,
																   FILE, RESOURCE, IN_PROCESS},
															   SERVER);
	public static final DriverProperty PORT=new IntegerProperty("port", "Port", new Integer(9001));
	public static final DriverProperty ALIAS=new StringProperty("alias", "Alias");
	public static final DriverProperty PATH=new StringProperty("path", "Path");
	public static final DriverProperty COLUMN_NAMES=new BooleanProperty("jdbc.get_column_name", "Column Names", Boolean.TRUE);

	public HSQLDBDriver()
	{
		super(new String[]{"org.hsqldb.jdbcDriver"});
	}

	public String getId()
	{
		return "jdbc:hsqldb";
	}

	public String getName()
	{
		return "HSQLDB";
	}

	public List getDriverProperties()
	{
		List list=new LinkedList();
		list.add(TYPE);
		list.add(HOST);
		list.add(PORT);
		list.add(ALIAS);
		list.add(PATH);
		list.add(USER);
		list.add(COLUMN_NAMES);
		list.add(AUTO_COMMIT);
		return list;
	}

	public boolean isRequired(DriverProperty property)
	{
		return property==TYPE || property==USER;
	}

	public String buildURL(DriverProperties map)
	{
		StringBuffer url=new StringBuffer("jdbc:hsqldb:");
		Object type=map.getProperty(TYPE);
//		Object name=map.getProperty(NAME);
		if (SERVER.equals(type)) createServerURL(url, "hsql", map);
		else if (SERVER_SECURE.equals(type)) createServerURL(url, "hsqls", map);
		else if (WEBSERVER.equals(type)) createServerURL(url, "http", map);
		else if (WEBSERVER_SECURE.equals(type)) createServerURL(url, "https", map);
		else if (FILE.equals(type)) createInProcessURL(url, "file", map);
		else if (RESOURCE.equals(type)) createInProcessURL(url, "res", map);
		else createInProcessURL(url, null, map); // Old syntax for in process
		return url.toString();
	}

	private void createServerURL(StringBuffer url, String protocol, DriverProperties map)
	{
		url.append(protocol).append("://");
		Object host=map.getProperty(HOST);
		if (host!=null) url.append(host);
		Object port=map.getProperty(PORT);
		if (port!=null) url.append(":").append(port);
		Object name=map.getProperty(ALIAS);
		if (name!=null) url.append("/").append(name);
	}

	private void createInProcessURL(StringBuffer url, String protocol, DriverProperties map)
	{
		if (protocol!=null) url.append(protocol).append(":");
		Object name=map.getProperty(PATH);
		if (name!=null) url.append(name);
	}

	public Properties getConnectProperties(DriverProperties map)
	{
		Properties properties=new Properties();
		copyProperty(map, properties, COLUMN_NAMES);
		return properties;
	}
}
