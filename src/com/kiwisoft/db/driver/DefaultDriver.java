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

import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import com.kiwisoft.db.Database;
import com.kiwisoft.db.StringProperty;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.5 $, $Date: 2006/03/24 17:52:12 $
 */
public class DefaultDriver extends DatabaseDriver
{
	public static final DriverProperty CLASS=new StringProperty("class", "Class");
	public static final DriverProperty URL=new StringProperty("url", "URL");

    public String getId()
    {
        return "jdbc";
    }

    public String getName()
    {
        return "Other Driver";
    }

    public int isAvailable()
    {
        return NOT_LOADED;
    }

	public List getDriverProperties()
	{
		List list=new LinkedList();
		list.add(CLASS);
		list.add(URL);
		list.add(USER);
		list.add(AUTO_COMMIT);
		return list;
	}

	public boolean isRequired(DriverProperty property)
	{
		return property==CLASS || property==URL;
	}

    public Connection createConnection(Database database, String password) throws Exception
    {
        Class driverClass=Class.forName((String)database.getProperty(CLASS), true, DatabaseDriverManager.getClassLoader());
        Driver driver=(Driver) driverClass.newInstance();
		if (driver!=null)
		{
	        String url=buildURL(database);
    	    System.out.println("Connecting ("+url+")...");
		    Properties connectProperties=getConnectProperties(database);
			addCustomProperties(database, connectProperties);
			if (database.getProperty(USER)!=null) connectProperties.put("user", database.getProperty(USER));
		    if (password!=null) connectProperties.put("password", password);
		    return driver.connect(url, connectProperties);
		}
		else throw new SQLException("No suitable driver.");
    }

	public String buildURL(DriverProperties map)
	{
		return (String)map.getProperty(URL);
	}

	public Properties getConnectProperties(DriverProperties map)
	{
		return new Properties();
	}

	public boolean isValid(Database database)
	{
		try
		{
			Class.forName((String)database.getProperty(CLASS), true, DatabaseDriverManager.getClassLoader());
			return true;
		}
		catch (ClassNotFoundException e)
		{
			return false;
		}
	}
}
