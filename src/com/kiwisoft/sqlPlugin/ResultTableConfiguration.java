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

import java.awt.Font;
import java.util.Collection;

import javax.swing.UIManager;

import com.kiwisoft.utils.DoubleKeyMap;
import com.kiwisoft.utils.gui.table.TableConfiguration;
import com.kiwisoft.db.Database;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.5 $, $Date: 2006/03/24 18:12:24 $
 */
public class ResultTableConfiguration implements TableConfiguration
{
	private DoubleKeyMap properties=new DoubleKeyMap();
	private Database database;
	private String statement;
	private boolean initalized;

	public ResultTableConfiguration(Database database, String statement)
	{
		this.database=database;
		this.statement=statement;
	}

	public boolean isColumnIndexIdentifier()
	{
		return true;
	}

	public void setInitalized(boolean initalized)
	{
		this.initalized=initalized;
		database.configurationChanged(statement, this);
	}

	public boolean isInitalized()
	{
		return initalized;
	}

	public boolean isSupportWidths()
	{
		return initalized;
	}

	public boolean isSupportTitles()
	{
		return false;
	}

	public Collection getColumns()
	{
		return properties.getKeys();
	}

	public void setFormat(int column, String format)
	{
		if (format!=null)
			properties.put(new Integer(column), "format", format);
		else
			properties.remove(new Integer(column), "format");
		database.configurationChanged(statement, this);
	}

	public String getFormat(int column)
	{
		return (String)properties.get(new Integer(column), "format");
	}

	public void setFont(int column, Font font)
	{
		if (font!=null)
			properties.put(new Integer(column), "font", font.getFamily());
		else
			properties.remove(new Integer(column), "font");
		database.configurationChanged(statement, this);
	}

	public Font getFont(int column)
	{
		Font tableFont=UIManager.getFont("Table.font");
		String fontName=(String)properties.get(new Integer(column), "font");
		if (fontName!=null) return new Font(fontName, Font.PLAIN, tableFont.getSize());
		return null;
	}

	public void setFontName(int column, String font)
	{
		if (font!=null)
			properties.put(new Integer(column), "font", font);
		else
			properties.remove(new Integer(column), "font");
		database.configurationChanged(statement, this);
	}

	public String getFontName(int column)
	{
		return (String)properties.get(new Integer(column), "font");
	}

	public void setSortIndex(Object identifier, int sortIndex)
	{
		if (identifier==null || !(identifier instanceof Integer)) throw new IllegalArgumentException();
		if (sortIndex>=0)
			properties.put(identifier, "sortIndex", new Integer(sortIndex));
		else
			properties.remove(identifier, "sortIndex");
		database.configurationChanged(statement, this);
	}

	public int getSortIndex(Object identifier)
	{
		Integer sortIndex=(Integer)properties.get(identifier, "sortIndex");
		if (sortIndex!=null) return sortIndex.intValue();
		return -1;
	}

	public void setSortDirection(Object identifier, Integer direction)
	{
		if (identifier==null || !(identifier instanceof Integer)) throw new IllegalArgumentException();
		if (direction!=null) properties.put(identifier, "sortDir", direction);
		else properties.remove(identifier);
		database.configurationChanged(statement, this);
	}

	public Integer getSortDirection(Object identifier)
	{
		return (Integer)properties.get(identifier, "sortDir");
	}

	public void setIndex(Object identifier, int viewIndex)
	{
		if (identifier==null || !(identifier instanceof Integer)) throw new IllegalArgumentException();
		if (viewIndex>=0)
			properties.put(identifier, "viewIndex", new Integer(viewIndex));
		else
			properties.remove(identifier, "viewIndex");
		database.configurationChanged(statement, this);
	}

	public int getIndex(Object identifier)
	{
		Integer viewIndex=(Integer)properties.get(identifier, "viewIndex");
		if (viewIndex!=null) return viewIndex.intValue();
		return -1;
	}

	public String getTitle(Object identifier)
	{
		return null;
	}

	public void setWidth(Object identifier, int width)
	{
		if (identifier==null || !(identifier instanceof Integer)) throw new IllegalArgumentException();
		if (width>=0)
			properties.put(identifier, "width", new Integer(width));
		else
			properties.remove(identifier, "width");
		database.configurationChanged(statement, this);
	}

	public int getWidth(Object identifier)
	{
		Integer width=(Integer)properties.get(identifier, "width");
		if (width!=null) return width.intValue();
		return 100;
	}

	public void setHidden(Object identifier, boolean hidden)
	{
		if (identifier==null || !(identifier instanceof Integer)) throw new IllegalArgumentException();
		if (hidden)
			properties.put(identifier, "hidden", Boolean.TRUE);
		else
			properties.remove(identifier, "hidden");
		database.configurationChanged(statement, this);
	}

	public boolean isHidden(Object identifier)
	{
		return Boolean.TRUE.equals(properties.get(identifier, "hidden"));
	}
}
