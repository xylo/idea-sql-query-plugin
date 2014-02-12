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
package com.kiwisoft.sqlPlugin.settings;

import java.util.Iterator;

import com.kiwisoft.utils.gui.table.SortableTableModel;
import com.kiwisoft.utils.gui.table.SortableTableRow;
import com.kiwisoft.utils.gui.table.TableConstants;
import com.kiwisoft.utils.StringUtils;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.3 $, $Date: 2006/03/24 18:12:25 $
 */
class CustomPropertiesTableModel extends SortableTableModel
{
	private static final String[] COLUMNS={"key", "value"};

	private DatabaseModel database;

	public CustomPropertiesTableModel(DatabaseModel database)
	{
		this.database=database;
		createData();
	}

	public int getColumnCount()
	{
		return COLUMNS.length;
	}

	public String getColumnName(int column)
	{
		return COLUMNS[column];
	}

	public void setDatabase(DatabaseModel database)
	{
		if (this.database!=database)
		{
			clear();
			this.database=database;
			createData();
		}
	}

	private void createData()
	{
		if (database!=null)
		{
			for (Iterator it=database.getCustomProperties().iterator(); it.hasNext();)
			{
				String key=(String)it.next();
				addRow(new Row(key));
			}
			sort();
		}
	}

	public void createRow()
	{
		addRow(new Row(null));
	}

	public class Row extends SortableTableRow
	{
		private Row(String key)
		{
			super(key);
		}

		public Object getDisplayValue(int column)
		{
			switch (column)
			{
				case 0: return getKey();
				case 1: return database.getCustomProperty(getKey());
			}
			return null;
		}

		public boolean isEditable(int column)
		{
			switch (column)
			{
				case 0: return true;
				case 1: return !StringUtils.isEmpty(getKey());
			}
			return false;
		}

		public int setValue(Object value, int col)
		{
			if (value instanceof String || value==null)
			{
				String textValue=(String)value;
				switch (col)
				{
					case 0:
						if (StringUtils.isEmpty(textValue))
							return TableConstants.NO_UPDATE;
						if (StringUtils.equal(textValue, getKey()))
							return TableConstants.NO_UPDATE;
						else if (database.getCustomProperties().contains(textValue))
							return TableConstants.NO_UPDATE;
						else
						{
							String propertyValue=database.removeCustomProperty(getKey());
							database.setCustomProperty(textValue, propertyValue);
							setUserObject(textValue);
							return TableConstants.CELL_UPDATE;
						}
					case 1:
						database.setCustomProperty(getKey(), textValue);
						return TableConstants.CELL_UPDATE;
				}
			}
			return super.setValue(value, col);
		}

		public String getKey()
		{
			return (String)getUserObject();
		}

		public void drop()
		{
			database.removeCustomProperty(getKey());
			removeRow(this);
		}
	}
}
