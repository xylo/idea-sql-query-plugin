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

import java.awt.Color;
import java.util.Iterator;

import com.kiwisoft.db.BooleanProperty;
import com.kiwisoft.db.ChoiceProperty;
import com.kiwisoft.db.driver.DatabaseDriver;
import com.kiwisoft.db.driver.DatabaseDriverManager;
import com.kiwisoft.db.driver.DriverProperty;
import com.kiwisoft.utils.gui.ObjectStyle;
import com.kiwisoft.utils.gui.table.SortableTableModel;
import com.kiwisoft.utils.gui.table.SortableTableRow;
import com.kiwisoft.utils.gui.table.ObjectEditor;
import com.kiwisoft.utils.gui.table.ChoiceObjectEditor;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.4 $, $Date: 2006/03/24 18:12:25 $
 */
public class DriverPropertiesTableModel extends SortableTableModel
{
	private static final ObjectStyle REQUIRED_STYLE=new ObjectStyle(null, new Color(200, 255, 255));

	private String[] names={"property", "value"};

	private DatabaseModel database;
	private DatabaseDriver driver;

	public DriverPropertiesTableModel(DatabaseModel database, DatabaseDriver driver)
	{
		super();
		this.database=database;
		this.driver=driver;
		createData();
	}

	public void setDriver(DatabaseDriver driver)
	{
		if (this.driver!=driver)
		{
			clear();
			this.driver=driver;
			createData();
		}
	}

	public void setDatabase(DatabaseModel database)
	{
		if (this.database!=database)
		{
			clear();
			this.database=database;
			if (database!=null) driver=DatabaseDriverManager.getInstance().getDriver(database.getDriver());
			else driver=null;
			createData();
		}
	}

	private void createData()
	{
		if (driver!=null)
		{
			Iterator it=driver.getDriverProperties().iterator();
			while (it.hasNext())
			{
				DriverProperty property=(DriverProperty)it.next();
				addRow(new TableRow(property));
			}
			sort();
		}
	}

	public int getColumnCount()
	{
		return names.length;
	}

	public String getColumnName(int col)
	{
		return names[col];
	}

	private class TableRow extends SortableTableRow
	{
		private DriverProperty property;
		private String variant;
		private ObjectEditor editor;

		public TableRow(DriverProperty property)
		{
			super(property);
			this.property=property;
			if (property instanceof ChoiceProperty)
			{
				ChoiceProperty choiceProperty=(ChoiceProperty)property;
				editor=new ChoiceObjectEditor(choiceProperty.getChoices(), choiceProperty.isNullable());
			}
			else if (property instanceof BooleanProperty)
			{
				BooleanProperty booleanProperty=(BooleanProperty) property;
				if (booleanProperty.isNullable()) variant="Tristate";
			}
		}

		public ObjectEditor getObjectEditor(int col)
		{
			return editor;
		}

		public ObjectStyle getCellStyle(int col)
		{
			if (driver.isRequired(property))
			{
				if (database==null || property.getDefaultValue()==null && database.getProperty(property)==null) return REQUIRED_STYLE;
			}
			return null;
		}

		public String getCellFormat(int col)
		{
			if (col==1) return variant;
			return null;
		}

		public Comparable getSortValue(int col)
		{
			Object value=getDisplayValue(col);
			if (value instanceof Comparable)
				return (Comparable)value;
			else if (value==null)
				return "";
			else
				return String.valueOf(value);
		}

		public boolean isEditable(int column)
		{
			return database!=null && column==1;
		}

		public Class getCellClass(int col)
		{
			if (col==1) return property.getType();
			return super.getCellClass(col);
		}

		public Object getDisplayValue(int col)
		{
			if (col==0) return property.getName();
			if (col==1 && database!=null) return database.getProperty(property);
			return null;
		}

		public int setValue(Object value, int col)
		{
			if (database!=null)
			{
				try
				{
					database.setProperty(property, property.convert(value));
					return ROW_UPDATE;
				}
				catch (IllegalArgumentException e)
				{
				}
			}
			return NO_UPDATE;
		}
	}

}
