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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.Date;
import java.util.EventObject;
import javax.swing.AbstractCellEditor;
import javax.swing.JTable;

import com.kiwisoft.utils.format.TextFormat;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.4 $, $Date: 2006/03/24 18:04:06 $
 */
public class TableDateEditor extends AbstractCellEditor implements ObjectEditor
{
	protected TableDateField lookupField;
	private Class dateClass;
	private TextFormat format;

	public TableDateEditor(Class dateClass, TextFormat format)
	{
		this.dateClass=dateClass;
		this.format=format;
		lookupField=new TableDateField(dateClass, format);
		lookupField.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				stopCellEditing();
			}
		});
	}

	public ObjectEditor cloneEditor()
	{
		return new TableDateEditor(dateClass, format);
	}

	public void setForeground(Color foreground)
	{
		lookupField.setForeground(foreground);
	}

	public void setBackground(Color background)
	{
		lookupField.setBackground(background);
	}

	public void setQuickEditable(boolean editable)
	{
	}

	public Object getCellEditorValue()
	{
		return lookupField.getDate();
	}

	public boolean isCellEditable(EventObject anEvent)
	{
		if (anEvent instanceof MouseEvent)
		{
			return ((MouseEvent)anEvent).getClickCount()>=2;
		}
		return true;
	}

	public boolean shouldSelectCell(EventObject anEvent)
	{
		return true;
	}

	public boolean stopCellEditing()
	{
		if (lookupField.verify())
		{
			fireEditingStopped();
			return true;
		}
		return false;
	}

	public void cancelCellEditing()
	{
		fireEditingCanceled();
	}

	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column)
	{
		lookupField.setDate((Date)value);
		return lookupField;
	}
}
