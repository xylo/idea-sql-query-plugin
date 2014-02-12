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
import java.sql.SQLException;

import com.kiwisoft.db.BooleanProperty;
import com.kiwisoft.db.IntegerProperty;
import com.kiwisoft.db.StringProperty;
import com.kiwisoft.db.Database;

/**
 * @author Stefan Stiller, Sergey Bervinov
 * @version $Revision: 1.4 $, $Date: 2006/03/24 17:53:36 $
 */
public class MySQLDriver extends DatabaseDriver
{
	public static final DriverProperty DATABASE=new StringProperty("db", "Database");
	public static final DriverProperty PORT=new IntegerProperty("port", "Port", new Integer(3306));
	public static final DriverProperty AUTO_RECONNECT=new BooleanProperty("autoReconnect", "Auto Reconnect");
	public static final DriverProperty CHARACTER_ENCODING=new StringProperty("characterEncoding", "Character Encoding");
	public static final DriverProperty MAX_RECONNECTS=new IntegerProperty("maxReconnects", "Max. Reconnects", new Integer(3));
	public static final DriverProperty INITIAL_TIMEOUT=new IntegerProperty("initialTimeout", "Initial Timeout", new Integer(2));
	public static final DriverProperty PROFILE_SQL=new BooleanProperty("profileSQL", "Profile SQL");

    public MySQLDriver()
    {
		super(new String[]{"com.mysql.jdbc.Driver", "org.gjt.mm.mysql.Driver"});
    }

    public String getId()
    {
        return "jdbc:mysql";
    }

    public String getName()
    {
        return "MySQL Connector/J";
    }

	public List getDriverProperties()
	{
		List list=new LinkedList();
		list.add(HOST);
		list.add(PORT);
		list.add(DATABASE);
		list.add(USER);
		list.add(CHARACTER_ENCODING);
		list.add(AUTO_RECONNECT);
		list.add(MAX_RECONNECTS);
		list.add(INITIAL_TIMEOUT);
		list.add(PROFILE_SQL);
		list.add(AUTO_COMMIT);
		return list;
	}

	public boolean isRequired(DriverProperty property)
	{
		return property==HOST || property==USER;
	}

    public String buildURL(DriverProperties map)
    {
        StringBuffer url=new StringBuffer("jdbc:mysql://");

		Object host=map.getProperty(HOST);
		if (host!=null) url.append(host);
		Object port=map.getProperty(PORT);
        if (port!=null) url.append(":").append(port);
        url.append("/");
		Object db=map.getProperty(DATABASE);
        if (db!=null) url.append(db);

        return url.toString();
    }

	public String getDefaultSchemaName(Database database)
	{
		Object name=database.getProperty(DATABASE);
		if (name==null) return null;
		return String.valueOf(name);
	}

	public Properties getConnectProperties(DriverProperties map)
	{
		Properties properties=new Properties();
		copyProperty(map, properties, CHARACTER_ENCODING);
		copyProperty(map, properties, AUTO_RECONNECT);
		copyProperty(map, properties, MAX_RECONNECTS);
		copyProperty(map, properties, INITIAL_TIMEOUT);
		copyProperty(map, properties, PROFILE_SQL);
		return properties;
	}

	public boolean isPasswordFailure(SQLException exception)
	{
		return exception.getErrorCode()==1045;
	}

}
