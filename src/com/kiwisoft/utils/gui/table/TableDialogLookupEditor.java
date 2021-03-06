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

import java.awt.Component;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.EventObject;
import javax.swing.AbstractCellEditor;
import javax.swing.JTable;

import com.kiwisoft.utils.gui.lookup.DialogLookup;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.4 $, $Date: 2006/03/24 18:04:07 $
 */
public class TableDialogLookupEditor extends AbstractCellEditor implements ObjectEditor
{
	protected TableDialogLookupField lookupField;

	public TableDialogLookupEditor(DialogLookup lookup)
	{
		lookupField=new TableDialogLookupField(lookup);
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
		return new TableDialogLookupEditor(lookupField.getLookup());
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
		return lookupField.getText();
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
		fireEditingStopped();
		return true;
	}

	public void cancelCellEditing()
	{
		fireEditingCanceled();
	}

	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column)
	{
		lookupField.setText((String)value);
		return lookupField;
	}
}
