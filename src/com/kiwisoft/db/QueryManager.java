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

import java.io.File;
import java.util.*;

import com.intellij.openapi.project.Project;

import com.kiwisoft.utils.NotifyObject;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.utils.idea.PluginUtils;
import com.kiwisoft.db.sql.SystemStatement;

import org.jdom.Element;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.6 $, $Date: 2006/03/24 17:54:50 $
 */
public class QueryManager extends Observable implements Observer
{
	private static Map instanceMap=new WeakHashMap();

	private int queryId=1;

	private List queries;

	private QueryManager()
	{
		queries=new LinkedList();
	}

	public static QueryManager getInstance(Project project)
	{
		QueryManager instance=(QueryManager)instanceMap.get(project);
		if (instance==null)
		{
			instance=new QueryManager();
			instanceMap.put(project, instance);
		}
		return instance;
	}

	public static void closeInstance(Project project)
	{
		QueryManager queryManager=(QueryManager)instanceMap.get(project);
		if (queryManager!=null) queryManager.dispose();
		instanceMap.remove(project);
	}

	private void dispose()
	{
		deleteObservers();
	}

	private String createQueryName()
	{
		String name="Query "+(queryId++);
		while (getQueryByName(name)!=null) name="Query "+(queryId++);
		return name;
	}

	public Query getQueryByName(String name)
	{
		for (Iterator it=queries.iterator(); it.hasNext();)
		{
			Query query=(Query)it.next();
			if (name.equals(query.getName())) return query;
		}
		return null;
	}

	public Query createQuery(SystemStatement statement)
	{
		Query query=new Query(createQueryName(), statement);
		addQuery(query);
		return query;
	}

	public Query createQuery()
	{
		Query query=new Query(createQueryName());
		addQuery(query);
		return query;
	}

	public Query createQuery(String text)
	{
		Query query=new Query(createQueryName(), text);
		addQuery(query);
		return query;
	}

	public Query createQuery(Query query, String text)
	{
		Query clone=new Query(createQueryName());
		clone.setStatement(text);
		addQuery(clone);
		return clone;
	}

	public Query createQuery(File file, String statement)
	{
		Query query=new Query(file, statement);
		addQuery(query);
		return query;
	}

	public void addQuery(Query query)
	{
		queries.add(query);
		query.addObserver(this);
		setChanged();
		notifyObservers(new NotifyObject("query added", query));
	}

	public void removeQuery(Query query)
	{
		queries.remove(query);
		query.deleteObserver(this);
		setChanged();
		notifyObservers(new NotifyObject("query removed", query));
	}

	public void update(Observable o, Object arg)
	{
		NotifyObject note=(NotifyObject)arg;
		Object type=note.getArgument(0);
		setChanged();
		notifyObservers(new NotifyObject("query "+type, o));
	}

	public void writeExternal(Element element)
	{
		Iterator it=queries.iterator();
		while (it.hasNext())
		{
			Query query=(Query)it.next();
			if (query.getStatement()!=null && !query.isSystemQuery())
			{
				Element child=new Element(Query.QUERY);
				PluginUtils.setValue(child, Query.NAME, query.getName());
				PluginUtils.setValue(child, Query.FILE, query.getFile());
				PluginUtils.setValue(child, Query.CHANGED, String.valueOf(query.isChanged()));
				Element statementElement=new Element(Query.STATEMENT);
				statementElement.addContent(StringUtils.encodeURL(query.getStatement()));
				child.addContent(statementElement);
				element.addContent(child);
			}
		}
	}

	public void readExternal(Element element)
	{
		List queryElements=element.getChildren(Query.QUERY);
		if (queryElements!=null)
		{
			Iterator it=queryElements.iterator();
			while (it.hasNext())
			{
				Element queryElement=(Element)it.next();
				Query query=new Query(queryElement.getAttributeValue(Query.NAME));
				String fileName=queryElement.getAttributeValue(Query.FILE);
				if (!StringUtils.isEmpty(fileName)) query.setFile(new File(fileName));
				Element statementElement=queryElement.getChild(Query.STATEMENT);
				if (statementElement!=null) query.setStatement(StringUtils.decodeURL(statementElement.getText()));
				query.setChanged(PluginUtils.getBoolean(queryElement, Query.CHANGED, true));
				addQuery(query);
			}
		}
	}

	public List getQueries()
	{
		return Collections.unmodifiableList(queries);
	}
}
