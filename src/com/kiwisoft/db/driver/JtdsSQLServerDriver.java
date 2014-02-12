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

import com.kiwisoft.db.*;

/**
 * @author Chris Maurer
 * @version $Revision: 1.4 $, $Date: 2006/03/24 17:53:36 $
 */
public class JtdsSQLServerDriver extends DatabaseDriver
{
	public static final DriverProperty DOMAIN=new StringProperty("domain", "Domain");
	public static final DriverProperty PORT=new IntegerProperty("port", "Port");
	public static final DriverProperty DATABASE=new StringProperty("db", "Database");
	public static final DriverProperty TDS=new StringProperty("TDS", "TDS");
	public static final DriverProperty INSTANCE=new StringProperty("instance", "Instance");
	public static final DriverProperty CHARSET=new StringProperty("charset", "Charset");
	public static final DriverProperty APP_NAME=new StringProperty("appname", "Application Name");
	public static final DriverProperty PROG_NAME=new StringProperty("progname", "Program Name");
	public static final DriverProperty WSID=new StringProperty("wsid", "WSID");
	public static final DriverProperty MAC_ADDRESS=new StringProperty("macadd", "Mac Address");
	public static final DriverProperty STRING_PARAMS_AS_UNICODE=new BooleanProperty("sendStringParametersAsUnicode", "Send String Parameters as Unicode");
	public static final DriverProperty LAST_UPDATE_COUNT=new BooleanProperty("lastUpdateCount", "Last Update Count");
	public static final DriverProperty PREPARE_SQL=new IntegerProperty("prepareSQL", "Prepare SQL");
	public static final DriverProperty PACKET_SIZE=new IntegerProperty("packetSize", "Packet Size");
	public static final DriverProperty TCP_NODELAY=new BooleanProperty("tcpNoDelay", "Tcp No Delay");
	public static final DriverProperty LOB_BUFFER=new LongProperty("lobBuffer", "Lob Buffer");
	public static final DriverProperty MAX_STATEMENTS=new IntegerProperty("maxStatements", "Max Statements");
	public static final DriverProperty LOGIN_TIMEOUT=new IntegerProperty("loginTimeout", "Login Timeout");
	public static final DriverProperty NAMED_PIPE=new BooleanProperty("namedPipe", "Named Pipe");
	public static final DriverProperty XA_EMULATION=new BooleanProperty("xaEmulation", "XA Emulation");
	public static final DriverProperty SSL=new StringProperty("ssl", "SSL");
	public static final DriverProperty BATCH_SIZE=new IntegerProperty("batchSize", "Batch Size");

	public JtdsSQLServerDriver()
	{
		super("net.sourceforge.jtds.jdbc.Driver");
	}

	public String getId()
	{
		return "jdbc:jtds:sqlserver";
	}

	public String getName()
	{
		return "Jtds SQL Server Driver";
	}

	public List getDriverProperties()
	{
		List list=new LinkedList();
		list.add(DOMAIN);
		list.add(USER);
		list.add(HOST);
		list.add(PORT);
		list.add(DATABASE);
		list.add(TDS);
		list.add(INSTANCE);
		list.add(CHARSET);
		list.add(APP_NAME);
		list.add(PROG_NAME);
		list.add(WSID);
		list.add(MAC_ADDRESS);
		list.add(STRING_PARAMS_AS_UNICODE);
		list.add(LAST_UPDATE_COUNT);
		list.add(PREPARE_SQL);
		list.add(PACKET_SIZE);
		list.add(TCP_NODELAY);
		list.add(LOB_BUFFER);
		list.add(MAX_STATEMENTS);
		list.add(LOGIN_TIMEOUT);
		list.add(NAMED_PIPE);
		list.add(XA_EMULATION);
		list.add(SSL);
		list.add(BATCH_SIZE);
		return list;
	}

	public String buildURL(DriverProperties map)
	{
		StringBuffer url=new StringBuffer("jdbc:jtds:sqlserver://");
		Object host=map.getProperty(HOST);
		if (host!=null) url.append(host);
		url.append(":");
		Object port=map.getProperty(PORT);
		if (port!=null) url.append(port);
		Object db=map.getProperty(DATABASE);
		if (db!=null) url.append("/").append(db);
		return url.toString();
	}

	public Properties getConnectProperties(DriverProperties map)
	{
		Properties properties=new Properties();
		copyProperty(map, properties, DOMAIN);
		copyProperty(map, properties, TDS);
		copyProperty(map, properties, INSTANCE);
		copyProperty(map, properties, CHARSET);
		copyProperty(map, properties, APP_NAME);
		copyProperty(map, properties, PROG_NAME);
		copyProperty(map, properties, WSID);
		copyProperty(map, properties, MAC_ADDRESS);
		copyProperty(map, properties, STRING_PARAMS_AS_UNICODE);
		copyProperty(map, properties, LAST_UPDATE_COUNT);
		copyProperty(map, properties, PREPARE_SQL);
		copyProperty(map, properties, PACKET_SIZE);
		copyProperty(map, properties, TCP_NODELAY);
		copyProperty(map, properties, LOB_BUFFER);
		copyProperty(map, properties, MAX_STATEMENTS);
		copyProperty(map, properties, LOGIN_TIMEOUT);
		copyProperty(map, properties, NAMED_PIPE);
		copyProperty(map, properties, XA_EMULATION);
		copyProperty(map, properties, SSL);
		copyProperty(map, properties, BATCH_SIZE);
		return properties;
	}
}
