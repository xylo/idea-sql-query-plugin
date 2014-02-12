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
import java.awt.event.MouseEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.EventObject;
import javax.swing.AbstractCellEditor;
import javax.swing.JTable;

import com.kiwisoft.utils.Tristate;
import com.kiwisoft.utils.gui.TristateBox;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.6 $, $Date: 2006/03/24 18:04:07 $
 */
public class TristateObjectEditor extends AbstractCellEditor implements ObjectEditor
{
	protected TristateBox tristateBox;
	private boolean useBoolean;
	private int clickCount=1;

	public TristateObjectEditor(boolean useBoolean)
	{
		this.useBoolean=useBoolean;
		tristateBox=new TristateBox();
		tristateBox.setHorizontalAlignment(TristateBox.CENTER);
		tristateBox.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				stopCellEditing();
			}
		});
	}

	public Object getCellEditorValue()
	{
		if (useBoolean) return tristateBox.getState().toBoolean();
		else return tristateBox.getState();
	}

	public boolean isCellEditable(EventObject anEvent)
	{
		if (anEvent instanceof MouseEvent)
		{
			return ((MouseEvent)anEvent).getClickCount()>=clickCount;
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

	public ObjectEditor cloneEditor()
	{
		return new TristateObjectEditor(useBoolean);
	}

	public void setForeground(Color foreground)
	{
		tristateBox.setForeground(foreground);
	}

	public void setBackground(Color background)
	{
		tristateBox.setBackground(background);
	}

	public void setQuickEditable(boolean editable)
	{
		setClickCountToStart(editable ? 1 : 2);
	}

	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column)
	{
		if (value instanceof Tristate)
			tristateBox.setState((Tristate)value);
		else if (value instanceof Boolean)
			tristateBox.setState(Tristate.getTristate((Boolean)value));
		else
			tristateBox.setState(Tristate.UNDEFINED);
		return tristateBox;
	}

	public void setClickCountToStart(int clickCount)
	{
		this.clickCount=clickCount;
	}
}
