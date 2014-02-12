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
package com.kiwisoft.utils.gui.table;

import java.awt.Color;
import java.awt.Component;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.UIManager;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.4 $, $Date: 2006/03/24 18:52:28 $
 */
public class ChoiceObjectEditor extends DefaultCellEditor implements ObjectEditor
{
	private static Object NULL_VALUE=new Object()
	{
		public String toString()
		{
			return " ";
		}
	};

	private Object[] choices;
	private boolean nullable;

	public ChoiceObjectEditor(Object[] choices, boolean nullable)
	{
		super(new JComboBox(buildArray(choices, nullable)));
		getComponent().setBackground(UIManager.getColor("Table.background"));
		this.choices=choices;
		this.nullable=nullable;
	}

	private static Object[] buildArray(Object[] choices, boolean nullable)
	{
		if (!nullable) return choices;
		else
		{
			Object[] newArray=new Object[choices.length+1];
			newArray[0]=NULL_VALUE;
			System.arraycopy(choices, 0, newArray, 1, choices.length);
			return newArray;
		}
	}

	public void setForeground(Color foreground)
	{
		getComponent().setForeground(foreground);
	}

	public void setBackground(Color background)
	{
		getComponent().setBackground(background);
	}

	public void setQuickEditable(boolean editable)
	{
	}

	public Object getCellEditorValue()
	{
		Object value=super.getCellEditorValue();
		if (value==NULL_VALUE) return null;
		return value;
	}

	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column)
	{
		if (value==null) value=NULL_VALUE;
		return super.getTableCellEditorComponent(table, value, isSelected, row, column);
	}

	public ObjectEditor cloneEditor()
	{
		return new ChoiceObjectEditor(choices, nullable);
	}
}
