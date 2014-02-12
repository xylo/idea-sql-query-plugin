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
package com.kiwisoft.sqlPlugin;

import java.util.ResourceBundle;

import com.kiwisoft.utils.gui.table.TableConfiguration;
import com.kiwisoft.sqlPlugin.config.SQLPluginAppConfig;
import com.kiwisoft.sqlPlugin.config.TableConfigurationAdapter;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.5 $, $Date: 2006/03/24 17:59:51 $
 */
public class DefaultTableConfiguration implements TableConfiguration
{
	private ResourceBundle bundle=ResourceBundle.getBundle(getClass().getName());
	private TableConfigurationAdapter configuration;
	private String key;
	private boolean supportWidths=true;

	public DefaultTableConfiguration(String key)
	{
		configuration=SQLPluginAppConfig.getInstance().getTableAdapter();
		this.key = key;
	}

	public void setSupportWidths(boolean supportWidths)
	{
		this.supportWidths=supportWidths;
	}

	public boolean isSupportWidths()
	{
		return supportWidths;
	}

	public boolean isSupportTitles()
	{
		return true;
	}

	public boolean isColumnIndexIdentifier()
	{
		return false;
	}

	public int getWidth(Object identifier)
	{
		return configuration.getInt(key, identifier+".width", 100);
	}

	public void setWidth(Object identifier, int width)
	{
		configuration.setInt(key, identifier+".width", new Integer(width));
	}

	public String getTitle(Object identifier)
	{
		return bundle.getString(key+"."+identifier);
	}

	public Integer getSortDirection(Object identifier)
	{
		return null;
	}

	public int getIndex(Object identifier)
	{
		return -1;
	}

	public void setSortIndex(Object identifier, int sortIndex)
	{
	}

	public void setIndex(Object identifier, int viewIndex)
	{
	}

	public void setSortDirection(Object identifier, Integer direction)
	{
	}

	public int getSortIndex(Object identifier)
	{
		return -1;
	}

	public void setHidden(Object identifier, boolean hidden)
	{
	}

	public boolean isHidden(Object identifier)
	{
		return false;
	}
}
