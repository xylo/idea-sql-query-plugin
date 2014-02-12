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
package com.kiwisoft.sqlPlugin.config;

import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeListener;

import org.jdom.Element;

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.WriteExternalException;
import com.intellij.openapi.util.JDOMExternalizable;
import com.intellij.openapi.project.Project;

import com.kiwisoft.db.QueryManager;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.3 $, $Date: 2006/03/24 17:59:52 $
 */
public class SQLPluginProjectConfig implements ProjectComponent, JDOMExternalizable, SQLPluginConstants
{
	private static SQLPluginProjectConfig instance;

	public static SQLPluginProjectConfig getInstance(Project project)
	{
		if (project!=null)
		{
			Object component=project.getComponent(SQLPluginProjectConfig.class);
			return (SQLPluginProjectConfig)component;
		}
		else
		{
			if (instance==null)
			{
				instance=new SQLPluginProjectConfig(null);
				instance.initComponent();
			}
			return instance;
		}
	}

	private Project project;


	public SQLPluginProjectConfig(Project project)
	{
		this.project=project;
	}


	// Listener Support

	private PropertyChangeSupport propertyChangeSupport=new PropertyChangeSupport(this);

	public void addPropertyChangeListener(PropertyChangeListener listener)
	{
		propertyChangeSupport.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener)
	{
		propertyChangeSupport.removePropertyChangeListener(listener);
	}

	// ApplicationComponent interface

	public String getComponentName()
	{
		return "SQLPlugin.ProjectConfiguration";
	}

	public void initComponent()
	{
	}

	public void disposeComponent()
	{
	}

	public void projectOpened()
	{
	}

	public void projectClosed()
	{
		QueryManager.closeInstance(project);
	}

	// JDOMExternalizable interface

	public void readExternal(Element element) throws InvalidDataException
	{
		try
		{
			Element queriesElement=element.getChild(QUERIES);
			if (queriesElement!=null)
			{
				QueryManager.getInstance(project).readExternal(queriesElement);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void writeExternal(Element element) throws WriteExternalException
	{
		try
		{
			// Queries
			Element child=new Element(QUERIES);
			if (SQLPluginAppConfig.getInstance().isSaveQueries()) QueryManager.getInstance(project).writeExternal(child);
			element.addContent(child);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
