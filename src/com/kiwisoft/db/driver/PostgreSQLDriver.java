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
 * @author Stefan Stiller, Sergey Bervinov
 * @version $Revision: 1.4 $, $Date: 2006/03/24 17:53:36 $
 */
public class PostgreSQLDriver extends DatabaseDriver
{
	public static final DriverProperty DATABASE=new StringProperty("db", "Database");
	public static final DriverProperty PORT=new IntegerProperty("port", "Port", new Integer(5432));
	public static final DriverProperty CHARACTER_SET=new StringProperty("charSet", "Character Set");
	public static final DriverProperty COMPATIBLE=new StringProperty("compatible", "Compatible");

    public PostgreSQLDriver()
    {
		super("org.postgresql.Driver");
    }

    public String getId()
    {
        return "jdbc:postgresql";
    }

    public String getName()
    {
        return "PostgreSQL";
    }

	public List getDriverProperties()
	{
		List list=new LinkedList();
		list.add(HOST);
		list.add(PORT);
		list.add(DATABASE);
		list.add(USER);
		list.add(CHARACTER_SET);
		list.add(COMPATIBLE);
		list.add(AUTO_COMMIT);
		return list;
	}

	public boolean isRequired(DriverProperty property)
	{
		return property==DATABASE || property==USER;
	}

    public String buildURL(DriverProperties map)
    {
        StringBuffer url=new StringBuffer("jdbc:postgresql:");

		Object host=map.getProperty(HOST);
		if (host!=null)
		{
			url.append("//");
			url.append(host);
			Object port=map.getProperty(PORT);
			if (port!=null) url.append(":").append(port);
			url.append("/");
		}
		Object db=map.getProperty(DATABASE);
        if (db!=null) url.append(db);

        return url.toString();
    }

	public Properties getConnectProperties(DriverProperties map)
	{
		Properties properties=new Properties();
		copyProperty(map, properties, CHARACTER_SET);
		copyProperty(map, properties, COMPATIBLE);
		return properties;
	}

}
