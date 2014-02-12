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

/**
 * @author Stefan Stiller
 * @version $Revision: 1.4 $, $Date: 2006/03/24 17:53:36 $
 */
public class MicrosoftSQLServerDriver extends SQLServerDriver
{
	public MicrosoftSQLServerDriver()
	{
		super("com.microsoft.jdbc.sqlserver.SQLServerDriver");
	}

	public String getId()
	{
		return "jdbc:microsoft:sqlserver";
	}

	public String getName()
	{
		return "SQLServer (Microsoft)";
	}

	public String buildURL(DriverProperties map)
	{
		StringBuffer url=new StringBuffer("jdbc:microsoft:sqlserver://");
		Object host=map.getProperty(HOST);
		if (host!=null) url.append(host);
		url.append(":");
		Object port=map.getProperty(PORT);
		if (port!=null) url.append(port);
		Object db=map.getProperty(DATABASE);
		if (db!=null) url.append(";DatabaseName=").append(db);
		return url.toString();
	}
}
