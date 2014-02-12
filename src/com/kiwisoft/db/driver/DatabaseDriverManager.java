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

import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

import com.intellij.openapi.vfs.VfsUtil;

import com.kiwisoft.sqlPlugin.JdbcLibrary;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.6 $, $Date: 2006/03/24 17:52:12 $
 */
public class DatabaseDriverManager
{
	private static ClassLoader classLoader=DatabaseDriverManager.class.getClassLoader();

	public static ClassLoader getClassLoader()
	{
		return classLoader;
	}

	public static void reloadDrivers(Set driverUrls)
	{
		List urls=new ArrayList();
		for (Iterator it=driverUrls.iterator(); it.hasNext();)
		{
			String vfsUrl=((JdbcLibrary)it.next()).getVfsUrl();
			URL url=VfsUtil.convertToURL(vfsUrl);
			if (url!=null) urls.add(url);
		}
		if (!urls.isEmpty())
			classLoader=new URLClassLoader((URL[])urls.toArray(new URL[0]), DatabaseDriverManager.class.getClassLoader());
		if (instance!=null)
		{
			for (Iterator itDrivers=instance.drivers.values().iterator(); itDrivers.hasNext();)
			{
				DatabaseDriver driver=(DatabaseDriver)itDrivers.next();
				driver.loadDriver();
			}
		}
	}

	private static DatabaseDriverManager instance;

	public synchronized static DatabaseDriverManager getInstance()
	{
		if (instance==null) instance=new DatabaseDriverManager();
		return instance;
	}

	private Map drivers;

	private DatabaseDriverManager()
	{
		drivers=new HashMap();
		installDriver(new OracleDriver());
		installDriver(new MySQLDriver());
		installDriver(new DefaultDriver());
		installDriver(new JdbcOdbcDriver());
		installDriver(new SybaseDriver());
		installDriver(new MicrosoftSQLServerDriver());
		installDriver(new MicrosoftSQLServer2005Driver());
		installDriver(new DataDirectSQLServerDriver());
		installDriver(new FirebirdDriver());
		installDriver(new PostgreSQLDriver());
		installDriver(new AS400Driver());
		installDriver(new InformixDriver());
		installDriver(new HSQLDBDriver());
		installDriver(new DB2Driver());
		installDriver(new JtdsSQLServerDriver());
	}

	private void installDriver(DatabaseDriver driver)
	{
		drivers.put(driver.getId(), driver);
	}

	public Collection getAllDrivers()
	{
		return Collections.unmodifiableCollection(drivers.values());
	}

	public DatabaseDriver getDriver(String driverName)
	{
		return (DatabaseDriver)drivers.get(driverName);
	}
}

