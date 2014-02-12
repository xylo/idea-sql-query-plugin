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
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.Iterator;

import com.kiwisoft.db.BooleanProperty;
import com.kiwisoft.db.Database;
import com.kiwisoft.db.StringProperty;
import com.kiwisoft.utils.StringUtils;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.5 $, $Date: 2006/03/24 17:52:12 $
 */
public abstract class DatabaseDriver
{
	public static final DriverProperty AUTO_COMMIT=new BooleanProperty("autocommit", "Auto Commit", null, true);
	public static final DriverProperty HOST=new StringProperty("host", "Host");
	public static final DriverProperty USER=new StringProperty("user", "User");

	public static final int ERROR=0;
	public static final int LOADED=1;
	public static final int NOT_LOADED=2;

	private int status=NOT_LOADED;

	private String classNames;
	private Driver driver;
	private String[] classes;

	protected DatabaseDriver(String aClass)
	{
		this(new String[]{aClass});
	}

	protected DatabaseDriver(String[] classes)
	{
		this.classes=classes;
		classNames=StringUtils.enumerate(this.classes, "; ");
		loadDriver();
	}

	protected DatabaseDriver()
	{
	}

	protected void loadDriver()
	{
		if (status!=LOADED && classes!=null)
		{
			for (int i=0; i<classes.length && driver==null; i++)
			{
				String aClass=classes[i];
				ClassLoader classLoader=DatabaseDriverManager.getClassLoader();
				System.out.print("Installing "+aClass+"...");
				try
				{
					Class driverClass=Class.forName(aClass, true, classLoader);
					driver=(Driver)driverClass.newInstance();
					System.out.println("Done");
					setStatus(LOADED);
				}
				catch (NoClassDefFoundError e)
				{
					System.out.println("Class not found");
					setStatus(ERROR);
				}
				catch (ClassNotFoundException e)
				{
					System.out.println("Class not found");
					setStatus(ERROR);
				}
				catch (Exception e)
				{
					System.out.println("Failed "+e.getMessage());
					setStatus(ERROR);
				}
			}
			if (driver==null) setStatus(ERROR);
		}
	}

	public int getStatus()
	{
		return status;
	}

	protected void setStatus(int status)
	{
		this.status=status;
	}

	public String getClassNames()
	{
		return classNames;
	}

	public abstract String getId();

	public abstract String getName();

	public List getDriverProperties()
	{
		return Collections.EMPTY_LIST;
	}

	public boolean isRequired(DriverProperty property)
	{
		return false;
	}

	public Connection createConnection(Database database, String password) throws Exception
	{
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

	public void addCustomProperties(Database database, Properties connectProperties)
	{
		for (Iterator it=database.getCustomProperties().iterator(); it.hasNext();)
		{
			String property=(String)it.next();
			connectProperties.put(property, database.getCustomProperty(property));
		}
	}

	public abstract String buildURL(DriverProperties map);

	public abstract Properties getConnectProperties(DriverProperties map);

	public String getDefaultSchemaName(Database database)
	{
		Object name=database.getProperty(USER);
		if (name==null) return null;
		return String.valueOf(name);
	}

	public String toString()
	{
		return getName();
	}

	public boolean isPasswordFailure(SQLException exception)
	{
		return false;
	}

	protected void copyProperty(DriverProperties map, Properties properties, DriverProperty property)
	{
		Object dateFormat=map.getProperty(property);
		if (dateFormat!=null) properties.setProperty(property.getId(), dateFormat.toString());
	}

	public boolean isValid(Database database)
	{
		return status==LOADED;
	}
}
