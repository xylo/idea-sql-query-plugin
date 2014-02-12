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
package com.kiwisoft.db;

import java.util.*;

import org.jdom.Element;

import com.kiwisoft.utils.NotifyObject;
import com.kiwisoft.utils.idea.PluginUtils;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.7 $, $Date: 2006/03/24 17:51:18 $
 */
public class DatabaseManager extends Observable
{
	public static final String CURRENT="current";

	private static Map instanceMap=new WeakHashMap();

	private List databases;
	private Map groups;
	private Database currentDatabase;
	private String currentGroup;

	private DatabaseManager()
	{
		databases=new LinkedList();
		groups=new HashMap();
	}

	public static DatabaseManager getApplicationInstance()
	{
		DatabaseManager instance=(DatabaseManager)instanceMap.get("application");
		if (instance==null)
		{
			instance=new DatabaseManager();
			instanceMap.put("application", instance);
		}
		return instance;
	}

	public static void closeApplicationInstance()
	{
		DatabaseManager instance=(DatabaseManager)instanceMap.get("application");
		if (instance!=null) instance.closeAllConnections();
		instanceMap.remove("application");
	}

	public void closeAllConnections()
	{
		Iterator it=databases.iterator();
		while (it.hasNext())
		{
			((Database)it.next()).closeConnection();
		}
	}

	public List getDatabases()
	{
		return Collections.unmodifiableList(databases);
	}

	public List getDatabases(String group)
	{
		if (group==null)
			return getDatabases();
		else
			return (List)groups.get(group);
	}

	public void addDatabase(Database database)
	{
		if (!databases.contains(database))
		{
			databases.add(database);
			setGroup(database, database.getGroup());
		}
	}

	private void setGroup(Database database, String group)
	{
		if (databases.contains(database))
		{
			if (group!=null)
			{
				List groupDatabases=(List)groups.get(group);
				if (groupDatabases==null)
				{
					groupDatabases=new LinkedList();
					groups.put(group, groupDatabases);
				}
				groupDatabases.add(database);
			}
		}
	}

	public void setDatabases(List list)
	{
		databases.clear();
		groups.clear();
		Iterator it=list.iterator();
		while (it.hasNext())
		{
			Database database=(Database)it.next();
			databases.add(database);
			setGroup(database, database.getGroup());
		}
		if (currentDatabase!=null && !databases.contains(currentDatabase)) currentDatabase=null;
		if (currentGroup!=null)
		{
			if (!groups.containsKey(currentGroup))
				currentGroup=null;
			else if (currentDatabase!=null && !currentGroup.equals(currentDatabase.getGroup())) currentGroup=null;
		}
		setChanged();
		notifyObservers(new NotifyObject("databases changed"));
	}

	public void setCurrentDatabase(Database database)
	{
		if (currentDatabase!=database)
		{
			currentDatabase=database;
			setChanged();
			notifyObservers(new NotifyObject("current database changed"));
		}
	}

	public Database getCurrentDatabase()
	{
		return currentDatabase;
	}

	public String getCurrentGroup()
	{
		return currentGroup;
	}

	public void setCurrentGroup(String group)
	{
		if (currentGroup!=null ? !currentGroup.equals(group) : group!=null)
		{
			currentGroup=group;
			setChanged();
			notifyObservers(new NotifyObject("current group changed"));
			if (currentGroup!=null && currentDatabase!=null)
			{
				if (!currentGroup.equals(currentDatabase.getGroup())) setCurrentDatabase(null);
			}
		}
	}

	public void readExternal(Element element, boolean encodePasswords)
	{
		String currentGroup=PluginUtils.getString(element, "group", null);
		List children=element.getChildren("database");
		if (children!=null)
		{
			Database current=null;
			List databases=new LinkedList();
			Iterator it=children.iterator();
			while (it.hasNext())
			{
				Element dbElement=(Element)it.next();
				Database database=Database.readDatabase(dbElement, encodePasswords);
				boolean isCurrent="true".equals(dbElement.getAttributeValue(CURRENT));
				if (isCurrent) current=database;
				databases.add(database);
			}
			setDatabases(databases);
			setCurrentDatabase(current);
		}
		if (currentGroup!=null && groups!=null && groups.containsKey(currentGroup))
			setCurrentGroup(currentGroup);
	}

	public void writeExternal(Element element)
	{
		if (currentGroup!=null) element.setAttribute("group", currentGroup);
		Iterator it=getDatabases().iterator();
		while (it.hasNext())
		{
			Database database=(Database)it.next();
			Element dbElement=database.writeDatabase();
			if (database==currentDatabase) dbElement.setAttribute(CURRENT, "true");
			element.addContent(dbElement);
		}
	}

	public Collection getGroups()
	{
		return Collections.unmodifiableSet(groups.keySet());
	}
}
