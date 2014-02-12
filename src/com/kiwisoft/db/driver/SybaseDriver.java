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
import com.kiwisoft.db.Database;
import com.kiwisoft.db.IntegerProperty;
import com.kiwisoft.db.StringProperty;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.3 $, $Date: 2006/03/24 17:53:37 $
 */
public class SybaseDriver extends DatabaseDriver
{
	public static final DriverProperty DATABASE=new StringProperty("db", "Database");
	public static final DriverProperty PORT=new IntegerProperty("port", "Port");
	public static final DriverProperty FAKE_METADATA=new BooleanProperty("fakeMetaData", "Fake Metadata", Boolean.TRUE);

	public SybaseDriver()
	{
		super(new String[]{"com.sybase.jdbc3.jdbc.SybDriver",
						   "com.sybase.jdbc2.jdbc.SybDriver",
						   "com.sybase.jdbc.SybDriver"});
	}

	public String getId()
	{
		return "jdbc:sybase:Tds";
	}

	public String getName()
	{
		return "Sybase jConnect";
	}

	public String getDefaultSchemaName(Database database)
	{
		return "dbo";
	}

	public List getDriverProperties()
	{
		List list=new LinkedList();
		list.add(HOST);
		list.add(PORT);
		list.add(DATABASE);
		list.add(USER);
		list.add(AUTO_COMMIT);
		list.add(FAKE_METADATA);
		return list;
	}

	public String buildURL(DriverProperties map)
	{
		StringBuffer url=new StringBuffer("jdbc:sybase:Tds");
		url.append(":");
		Object host=map.getProperty(HOST);
		if (host!=null) url.append(host);
		url.append(":");
		Object port=map.getProperty(PORT);
		if (port!=null) url.append(port);
		url.append("/");
		Object db=map.getProperty(DATABASE);
		if (db!=null) url.append(db);
		return url.toString();
	}

	public Properties getConnectProperties(DriverProperties map)
	{
		Properties properties=new Properties();
		Object fakeMetaData=map.getProperty(FAKE_METADATA);
		if (fakeMetaData!=null) properties.put("FAKE_METADATA", fakeMetaData.toString());
		return properties;
	}
}
