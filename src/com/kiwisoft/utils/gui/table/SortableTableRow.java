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

import java.util.EventListener;
import javax.swing.event.EventListenerList;

import com.kiwisoft.utils.gui.ObjectStyle;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.5 $, $Date: 2006/03/24 18:58:22 $
 */
public abstract class SortableTableRow
{
	private Object userObject;
	protected final EventListenerList listeners=new EventListenerList();

	protected SortableTableRow(Object userObject)
	{
		this.userObject=userObject;
	}

	public Class getCellClass(int col)
	{
		Object value=getDisplayValue(col);
		if (value!=null) return value.getClass();
		return null;
	}

	public boolean isEditable(int column)
	{
		return false;
	}

	public Comparable getSortValue(int col)
	{
	    Object value=getDisplayValue(col);
	    if (value instanceof Comparable)
	        return (Comparable) value;
	    else if (value==null)
	        return "";
	    else
	        return String.valueOf(value);
	}

    public abstract Object getDisplayValue(int column);

	public int setValue(Object value, int col)
	{
		return SortableTableModel.NO_UPDATE;
	}

	public Object getUserObject()
	{
		return userObject;
	}

	public void setUserObject(Object userObject)
	{
		this.userObject=userObject;
	}

	public ObjectStyle getCellStyle(int col)
	{
		return null;
	}

	public String getCellFormat(int col)
	{
		return null;
	}

	public Object getToolTipText(int col)
	{
		return getDisplayValue(col);
	}

	public void installListener()
	{
	}

	public void removeListener()
	{
	}

	public void addRowListener(TableRowListener listener)
	{
		listeners.add(TableRowListener.class, listener);
	}

	public void removeRowListener(TableRowListener listener)
	{
		listeners.remove(TableRowListener.class, listener);
	}

	protected void fireRowUpdated()
	{
		EventListener[] rowListeners=listeners.getListeners(TableRowListener.class);
		for (int i=0; i<rowListeners.length; i++)
		{
			TableRowListener listener=(TableRowListener)rowListeners[i];
			listener.rowUpdated(this);
		}
	}

	protected void fireRowDeleted()
	{
		EventListener[] rowListeners=listeners.getListeners(TableRowListener.class);
		for (int i=0; i<rowListeners.length; i++)
		{
			TableRowListener listener=(TableRowListener)rowListeners[i];
			listener.rowDeleted(this);
		}
	}

	protected boolean keepAtEnd()
	{
		return false;
	}

	public ObjectEditor getObjectEditor(int col)
	{
		return null;
	}
}
