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
import javax.swing.JCheckBox;
import javax.swing.JTable;

import com.kiwisoft.utils.format.BooleanFormat;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.5 $, $Date: 2006/03/24 18:52:28 $
 */
public class BooleanObjectEditor extends DefaultCellEditor implements ObjectEditor
{
	private BooleanFormat format;
	private Class targetClass;

	public BooleanObjectEditor()
	{
		super(new JCheckBox());
		((JCheckBox)getComponent()).setHorizontalAlignment(JCheckBox.CENTER);
	}

	public BooleanObjectEditor(Class targetClass, BooleanFormat format)
	{
		this();
		this.format=format;
		this.targetClass=targetClass;
	}

	private Color unselectedForeground;
	private Color unselectedBackground;

	public ObjectEditor cloneEditor()
	{
		return new BooleanObjectEditor(targetClass, format);
	}

	public void setForeground(Color foreground)
	{
		getComponent().setForeground(foreground);
		unselectedForeground=foreground;
	}

	public void setBackground(Color background)
	{
		getComponent().setBackground(background);
		unselectedBackground=background;
	}

	public void setQuickEditable(boolean editable)
	{
		setClickCountToStart(editable ? 1 : 2);
	}

	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column)
	{
		JCheckBox checkBox=(JCheckBox)getComponent();
		if (isSelected)
		{
			checkBox.setForeground(table.getSelectionForeground());
			checkBox.setBackground(table.getSelectionBackground());
		}
		else
		{
			checkBox.setForeground((unselectedForeground != null) ? unselectedForeground : table.getForeground());
			checkBox.setBackground((unselectedBackground != null) ? unselectedBackground : table.getBackground());
		}
		if (format!=null) value=format.format(value);
		return super.getTableCellEditorComponent(table, value, isSelected, row, column);
	}

	public Object getCellEditorValue()
	{
		if (format!=null)
			return format.parse((Boolean)super.getCellEditorValue(), targetClass);
		else return super.getCellEditorValue();
	}
}
