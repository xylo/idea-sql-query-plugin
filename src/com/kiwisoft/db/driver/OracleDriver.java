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

import com.kiwisoft.db.IntegerProperty;
import com.kiwisoft.db.StringProperty;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.4 $, $Date: 2006/03/24 17:53:36 $
 */
public class OracleDriver extends DatabaseDriver
{
	public static final DriverProperty SID=new StringProperty("sid", "SID");
	public static final DriverProperty PORT=new IntegerProperty("port", "Port", new Integer(1521));

	public OracleDriver()
    {
        super("oracle.jdbc.driver.OracleDriver");
    }

    public String getId()
    {
        return "jdbc:oracle:thin";
    }

    public String getName()
    {
        return "Oracle Thin Driver";
    }

	public List getDriverProperties()
	{
		List list=new LinkedList();
		list.add(HOST);
		list.add(PORT);
		list.add(SID);
		list.add(USER);
		list.add(AUTO_COMMIT);
		return list;
	}

	public boolean isRequired(DriverProperty property)
	{
		return property==HOST || property==USER || property==SID;
	}

	public String buildURL(DriverProperties map)
    {
		StringBuffer buffer=new StringBuffer("jdbc:oracle:thin:@");
		buffer.append(map.getProperty(HOST));
		buffer.append(":");
		buffer.append(map.getProperty(PORT));
        Object sid=map.getProperty(SID);
        if (sid!=null) buffer.append(":").append(sid);
        return buffer.toString();
    }

	public Properties getConnectProperties(DriverProperties map)
	{
		return new Properties();
	}

	public boolean isPasswordFailure(SQLException exception)
	{
		return exception.getErrorCode()==1017;
	}
}
