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

import com.kiwisoft.db.IntegerProperty;
import com.kiwisoft.db.StringProperty;

/**
 * @version $Revision: 1.5 $, $Date: 2006/03/24 17:53:36 $
 * @author Sergey Bervinov (sergey_bervinov@mail.ru)
 */
public class FirebirdDriver extends DatabaseDriver
{
	public static final DriverProperty PORT=new IntegerProperty("port", "Port", new Integer(3050));
	public static final DriverProperty DATABASE=new StringProperty("db", "Database");
	public static final DriverProperty ENCODING=new StringProperty("lc_ctype", "Encoding");

	public FirebirdDriver()
	{
		super("org.firebirdsql.jdbc.FBDriver");
	}

	public String getId()
	{
		return "jdbc:firebirdsql";
	}

	public String getName()
	{
		return "Firebird JCA/JDBC Driver (JayBird)";
	}

	public List getDriverProperties()
	{
		List list=new LinkedList();
		list.add(HOST);
		list.add(PORT);
		list.add(DATABASE);
		list.add(ENCODING);
		list.add(USER);
		list.add(AUTO_COMMIT);
		return list;
	}

	public String buildURL(DriverProperties map)
	{
		StringBuffer url=new StringBuffer("jdbc:firebirdsql://");
		Object host=map.getProperty(HOST);
		if (host!=null) url.append(host);
		url.append(":");
		Object port=map.getProperty(PORT);
		if (port!=null) url.append(port);
		url.append("/");
		Object db=map.getProperty(DATABASE);
		if (db!=null) url.append(db);
		Object enc=map.getProperty(ENCODING);
		if (enc!=null)
		{
			url.append("?lc_ctype=");
			url.append(enc);
		}
		return url.toString();
	}

	public Properties getConnectProperties(DriverProperties map)
	{
		return new Properties();
	}
}
