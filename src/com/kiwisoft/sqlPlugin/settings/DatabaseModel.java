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
package com.kiwisoft.sqlPlugin.settings;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Observable;
import java.util.Set;

import com.kiwisoft.db.Database;
import com.kiwisoft.db.driver.DatabaseDriver;
import com.kiwisoft.db.driver.DatabaseDriverManager;
import com.kiwisoft.db.driver.DefaultDriver;
import com.kiwisoft.db.driver.DriverProperties;
import com.kiwisoft.db.driver.DriverProperty;
import com.kiwisoft.utils.NotifyObject;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.4 $, $Date: 2006/03/24 18:12:25 $
 */
public class DatabaseModel extends Observable implements DriverProperties
{
    private Database database;

    private String name;
    private String driver;
	private String group;
	private Map properties=new HashMap();
	private Map customProperties=new HashMap();

	public DatabaseModel(Database database)
    {
        this.database=database;

		setName(database.getName());
		setDriver(database.getDriver());
		setGroup(database.getGroup());

		for (Iterator it=database.getProperties().iterator(); it.hasNext();)
		{
			DriverProperty property=(DriverProperty)it.next();
			properties.put(property, database.getProperty(property));
		}
		for (Iterator it=database.getCustomProperties().iterator(); it.hasNext();)
		{
			String property=(String)it.next();
			customProperties.put(property, database.getCustomProperty(property));
		}
	}

    public DatabaseModel(DatabaseModel databaseModel)
    {
        setName(databaseModel.getName()+" (2)");
        setDriver(databaseModel.getDriver());
		setGroup(databaseModel.getGroup());

		for (Iterator it=databaseModel.getProperties().iterator(); it.hasNext();)
		{
			DriverProperty property=(DriverProperty)it.next();
			properties.put(property, databaseModel.getProperty(property));
		}
		for (Iterator it=databaseModel.getCustomProperties().iterator(); it.hasNext();)
		{
			String property=(String)it.next();
			customProperties.put(property, databaseModel.getCustomProperty(property));
		}
    }

    public DatabaseModel()
    {
        setName("Unnamed");
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name=name;
    }

    public String getDriver()
    {
        return driver;
    }

    public void setDriver(String driver)
    {
        this.driver=driver;
    }

	public String getGroup()
	{
		return group;
	}

	public void setGroup(String group)
	{
		this.group=group;
	}

	public String getClassNames()
	{
		String classNames=null;
		if (driver!=null)
		{
			DatabaseDriver dbDriver=DatabaseDriverManager.getInstance().getDriver(driver);
			classNames=dbDriver.getClassNames();
		}
		if (classNames==null) classNames=(String)properties.get(DefaultDriver.CLASS);
		if (classNames==null) return "";
		return classNames;
	}

	public Set getProperties()
	{
		return properties.keySet();
	}

	public void setProperty(DriverProperty property, Object value)
	{
		Object oldValue=properties.get(property);
		if (oldValue!=null ? !oldValue.equals(value) : value!=null)
		{
			properties.put(property, value);
			setChanged();
			notifyObservers(new NotifyObject("url changed", buildURL()));
			if (property==DefaultDriver.CLASS)
			{
				setChanged();
				notifyObservers(new NotifyObject("class changed", getClassNames()));
			}
		}
	}

	public String buildURL()
	{
		if (driver!=null)
		{
			DatabaseDriver dbDriver=DatabaseDriverManager.getInstance().getDriver(driver);
			if (dbDriver!=null) return dbDriver.buildURL(this);
		}
		return "";
	}

	public Object getProperty(DriverProperty property)
	{
		Object value=properties.get(property);
		if (value==null) return property.getDefaultValue();
		return value;
	}

	public Set getCustomProperties()
	{
		return customProperties.keySet();
	}

	public String getCustomProperty(String name)
	{
		return (String)customProperties.get(name);
	}

	public String removeCustomProperty(String name)
	{
		return (String)customProperties.remove(name);
	}

	public void setCustomProperty(String name, String value)
	{
		customProperties.put(name, value);
	}

	public String toString()
    {
        if (name!=null) return name;
        return "";
    }

    public Database apply()
    {
        if (database==null) database=new Database(getName());
        else database.setName(getName());
        database.setDriver(getDriver());
		database.setGroup(getGroup());

		database.clearAllProperties();
		for (Iterator it=getProperties().iterator(); it.hasNext();)
		{
			DriverProperty property=(DriverProperty)it.next();
			database.setProperty(property, getProperty(property));
		}
		database.clearAllCustomProperties();
		for (Iterator it=getCustomProperties().iterator(); it.hasNext();)
		{
			String key=(String)it.next();
			database.setCustomProperty(key, getCustomProperty(key));
		}

		return database;
    }

	public Database getDatabase()
	{
		return database;
	}
}

