package com.kiwisoft.db.driver;

import java.util.List;
import java.util.LinkedList;
import java.util.Properties;

import com.kiwisoft.db.StringProperty;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.4 $, $Date: 2006/03/24 17:53:36 $
 */
public class MicrosoftSQLServer2005Driver extends SQLServerDriver
{
	public static final DriverProperty INSTANCE=new StringProperty("instance", "Instance");
	public static final DriverProperty DATABASE_NAME=new StringProperty("databaseName", "Database");
	public static final DriverProperty WORKSTATION_ID=new StringProperty("workstationID", "Workstation ID");

	public MicrosoftSQLServer2005Driver()
	{
		super("com.microsoft.sqlserver.jdbc.SQLServerDriver");
	}

	public String getId()
	{
		return "jdbc:sqlserver";
	}

	public List getDriverProperties()
	{
		List list=new LinkedList();
		list.add(HOST);
		list.add(PORT);
		list.add(INSTANCE);
		list.add(DATABASE_NAME);
		list.add(USER);
		list.add(WORKSTATION_ID);
		list.add(SELECT_METHOD);
		list.add(AUTO_COMMIT);
		return list;
	}

	public String getName()
	{
		return "SQLServer 2005 (Microsoft)";
	}

	public String buildURL(DriverProperties map)
	{
		StringBuffer url=new StringBuffer("jdbc:sqlserver://");
		Object host=map.getProperty(HOST);
		if (host!=null) url.append(host);
		Object instance=map.getProperty(INSTANCE);
		if (instance!=null) url.append("\\").append(instance);
		Object port=map.getProperty(PORT);
		if (port!=null) url.append(":").append(port);
		return url.toString();
	}

	public Properties getConnectProperties(DriverProperties map)
	{
		Properties properties=super.getConnectProperties(map);
		copyProperty(map, properties, DATABASE_NAME);
		copyProperty(map, properties, WORKSTATION_ID);
		properties.setProperty("applicationName", "SQLQueryPlugin for IntelliJ IDEA");
		return properties;
	}
}
