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

import java.util.List;
import java.util.Iterator;

import org.jdom.Element;

import com.intellij.openapi.util.JDOMExternalizable;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.WriteExternalException;

import com.kiwisoft.utils.DoubleKeyMap;
import com.kiwisoft.utils.StringUtils;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.3 $, $Date: 2006/03/24 17:59:52 $
 */
public class TableConfigurationAdapter implements JDOMExternalizable
{
	TableConfigurationAdapter()
	{
	}

	private DoubleKeyMap values=new DoubleKeyMap();

	public int getInt(String tableId, String property, int defaultValue)
	{
		String value=(String) values.get(tableId, property);
		if (value!=null) return Integer.parseInt(value);
		return defaultValue;
	}

	public void setInt(String tableId, String property, Integer value)
	{
		values.put(tableId, property, String.valueOf(value));
	}

	public String getString(String tableId, String property)
	{
		return (String) values.get(tableId, property);
	}

	public void setString(String tableId, String property, String value)
	{
		values.put(tableId, property, value);
	}


	public void readExternal(Element element) throws InvalidDataException
	{
		List children=element.getChildren("table");
		if (children!=null)
		{
			for (Iterator it=children.iterator(); it.hasNext();)
			{
				Element tableElement=(Element) it.next();
				String tableId=tableElement.getAttributeValue("id");
				List properties=tableElement.getChildren("property");
				if (properties!=null && !StringUtils.isEmpty(tableId))
				{
					for (Iterator itProps=properties.iterator(); itProps.hasNext();)
					{
						Element propertyElement=(Element) itProps.next();
					    String property=propertyElement.getAttributeValue("id");
					    String value=propertyElement.getAttributeValue("value");
						if (!StringUtils.isEmpty("id") && !StringUtils.isEmpty("value"))
						{
							values.put(tableId, property, value);
						}
					}
				}
			}
		}
	}

	public void writeExternal(Element element) throws WriteExternalException
	{
		for (Iterator it=values.getKeys().iterator(); it.hasNext();)
		{
			String tableId=(String) it.next();
			Element tableElement=new Element("table");
			tableElement.setAttribute("id", tableId);
			for (Iterator itProperties=values.getKeys(tableId).iterator(); itProperties.hasNext();)
			{
				String property=(String) itProperties.next();
				String value=(String) values.get(tableId, property);
				Element propertyElement=new Element("property");
				propertyElement.setAttribute("id", property);
				propertyElement.setAttribute("value", value);
				tableElement.addContent(propertyElement);
			}
			element.addContent(tableElement);
		}
	}
}
