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

import java.util.*;

import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.AbstractTableModel;

import com.kiwisoft.utils.gui.ObjectStyle;
import com.kiwisoft.utils.Utils;

/**
 * {@link javax.swing.table.TableModel} implementation which is based on sortable row
 * objects.
 *
 * @author Stefan Stiller
 * @version $Revision: 1.7 $, $Date: 2006/03/24 18:58:22 $
 */
public abstract class SortableTableModel extends AbstractTableModel implements TableRowListener, TableConstants
{
	/**
	 * List of sort column descriptions.
	 */
	private List sortColumns=new ArrayList(1);

	/**
	 * The list of all table rows.
	 */
	protected List rows=new ArrayList();

	/**
	 * The comparator to sort the table rows.
	 */
	protected Comparator comparator=new DefaultComparator();

	/**
	 * Adds a new row to the end of the table.
	 *
	 * @param row The new row.
	 */
	public void addRow(SortableTableRow row)
	{
		rows.add(row);
		row.addRowListener(this);
		row.installListener();
		int index=rows.size()-1;
		fireTableRowsInserted(index, index);
	}

	/**
	 * Adds a new row at the specified index of the table.
	 *
	 * @param row The new row.
	 * @param index The index at which the row should be inserted.
	 */
	public void addRow(SortableTableRow row, int index)
	{
		rows.add(index, row);
		row.addRowListener(this);
		row.installListener();
		index=rows.indexOf(row);
		fireTableRowsInserted(index, index);
	}

	/**
	 * Removes the specified row from the table.
	 *
	 * @param row The row to be removed.
	 */
	public void removeRow(SortableTableRow row)
	{
		int index=rows.indexOf(row);
		if (index>=0)
		{
			row.removeListener();
			row.removeRowListener(this);
			rows.remove(row);
			fireTableRowsDeleted(index, index);
		}
	}

	/**
	 * Removes the row at the specified index from the table.
	 *
	 * @param index The index to be removed.
	 */
	public void removeRow(int index)
	{
		SortableTableRow row=(SortableTableRow)rows.get(index);
		row.removeListener();
		row.removeRowListener(this);
		rows.remove(index);
		fireTableRowsDeleted(index, index);
	}

	/**
	 * Remove all rows there the user object is equal to the specified object.
	 */
	public void removeRow(Object userObject)
	{
		Iterator it=new LinkedList(rows).iterator();
		while (it.hasNext())
		{
			SortableTableRow row=(SortableTableRow)it.next();
			if (Utils.equals(row.getUserObject(), userObject)) removeRow(row);
		}
	}

	/**
	 * Checks if the table model contains a row which references the specified
	 * user object.
	 */
	public boolean containsObject(Object userObject)
	{
		Iterator it=new LinkedList(rows).iterator();
		while (it.hasNext())
		{
			SortableTableRow row=(SortableTableRow)it.next();
			if (Utils.equals(row.getUserObject(), userObject)) return true;
		}
		return false;
	}

	/**
	 * Returns the index of the first row which references the specified user
	 * object.
	 */
	public int indexOf(Object userObject)
	{
		for (int i=0; i<rows.size(); i++)
		{
			SortableTableRow row=(SortableTableRow)rows.get(i);
			if (Utils.equals(row.getUserObject(), userObject)) return i;
		}
		return -1;
	}

	public int indexOfRow(SortableTableRow row)
	{
		for (int i=0; i<rows.size(); i++)
		{
			if (row==rows.get(i)) return i;
		}
		return -1;
	}

	public void clear()
	{
		Iterator it=rows.iterator();
		while (it.hasNext())
		{
			SortableTableRow row=(SortableTableRow)it.next();
			row.removeListener();
			row.removeRowListener(this);
		}
		rows.clear();
		fireTableDataChanged();
	}

	public Object getObject(int row)
	{
		return getRow(row).getUserObject();
	}

	public SortableTableRow getRow(int row)
	{
		if (row>=0 && row<rows.size()) return (SortableTableRow)rows.get(row);
		else return null;
	}

	public int getRowCount()
	{
		return rows.size();
	}

	public Class getCellClass(int row, int col)
	{
		Class cellClass=getRow(row).getCellClass(col);
		if (cellClass!=null) return cellClass;
		else
		{
			Object value=getValueAt(row, col);
			if (value!=null) return value.getClass();
			else return Object.class;
		}
	}

	public TableCellRenderer getCellRenderer(int row, int col)
	{
		Class cellClass=getCellClass(row, col);
		if (cellClass==null) cellClass=Object.class;
		String cellFormat=getCellFormat(row, col);
		ObjectStyle cellStyle=getCellStyle(row, col);
		return RendererFactory.getInstance().getRenderer(cellClass, cellFormat, cellStyle);
	}

	public TableCellEditor getCellEditor(int row, int col)
	{
		ObjectEditor editor=getObjectEditor(row, col);
		ObjectStyle cellStyle=getCellStyle(row, col);
		if (editor!=null) return EditorFactory.getInstance().wrapEditor(editor, cellStyle, false);
		Class cellClass=getCellClass(row, col);
		if (cellClass==null) cellClass=Object.class;
		String cellFormat=getCellFormat(row, col);
		return EditorFactory.getInstance().getEditor(cellClass, cellFormat, cellStyle);
	}

	/**
	 * Checks if this table cell is editable. This implementation forwards the
	 * request to the table row.
	 *
	 * @param row The row being queried.
	 * @param column The column being queried.
	 * @return <code>true</code> if the cell is editable.
	 */
	public boolean isCellEditable(int row, int column)
	{
		return getRow(row).isEditable(column);
	}

	public String getCellFormat(int row, int col)
	{
		return getRow(row).getCellFormat(col);
	}

	public ObjectEditor getObjectEditor(int row, int col)
	{
		return getRow(row).getObjectEditor(col);
	}

	public ObjectStyle getCellStyle(int row, int col)
	{
		return getRow(row).getCellStyle(col);
	}

	public Object getToolTipText(int row, int col)
	{
		return getRow(row).getToolTipText(col);
	}

	public void setValueAt(Object aValue, int rowIndex, int columnIndex)
	{
		switch (getRow(rowIndex).setValue(aValue, columnIndex))
		{
			case CELL_UPDATE:
				fireTableCellUpdated(rowIndex, columnIndex);
				break;
			case ROW_UPDATE:
				fireTableRowsUpdated(rowIndex, rowIndex);
				break;
			case TABLE_UPDATE:
				fireTableDataChanged();
		}
	}

	public Object getValueAt(int rowIndex, int columnIndex)
	{
		return getRow(rowIndex).getDisplayValue(columnIndex);
	}

	public TableSortDescription getSortDescription(int columnIndex)
	{
		for (Iterator it=sortColumns.iterator(); it.hasNext();)
		{
			TableSortDescription sortDescription=(TableSortDescription)it.next();
			if (sortDescription.getColumn()==columnIndex) return sortDescription;
		}
		return null;
	}

	public int getSortIndex(int columnIndex)
	{
		for (int i=0; i<sortColumns.size(); i++)
		{
			TableSortDescription sortDescription=(TableSortDescription)sortColumns.get(i);
			if (sortDescription.getColumn()==columnIndex) return i;
		}
		return -1;
	}

	public void addSortColumn(TableSortDescription sortDescription)
	{
		sortColumns.add(sortDescription);
	}

	public void addSortColumn(int columnIndex, boolean add)
	{
		TableSortDescription sortDescription=getSortDescription(columnIndex);
		boolean singleSort=sortColumns.size()==1;
		if (!add) sortColumns.clear();
		if (sortDescription==null || (!add && !singleSort)) sortColumns.add(new TableSortDescription(columnIndex, ASCEND));
		else if (ASCEND.equals(sortDescription.getDirection()))
		{
			sortDescription.setDirection(DESCEND);
			sortColumns.remove(sortDescription);
			sortColumns.add(sortDescription);
		}
		else if (DESCEND.equals(sortDescription.getDirection())) sortColumns.remove(sortDescription);
		sort();
	}

	public void clearSort()
	{
		sortColumns.clear();
	}

	public void sort()
	{
		if (!sortColumns.isEmpty()) Collections.sort(rows, comparator);
		fireTableDataChanged();
	}

	public void rowUpdated(SortableTableRow row)
	{
		int index=rows.indexOf(row);
		if (index>=0) fireTableRowsUpdated(index, index);
	}

	public void rowDeleted(SortableTableRow row)
	{
		int index=rows.indexOf(row);
		if (index>=0) removeRow(index);
	}

	public void rowOrderChanged(SortableTableRow row)
	{
		sort();
	}

	public boolean isResortable()
	{
		return true;
	}

	private class DefaultComparator implements Comparator
	{
		public int compare(Object o1, Object o2)
		{
			SortableTableRow row1=(SortableTableRow)o1;
			SortableTableRow row2=(SortableTableRow)o2;
			for (Iterator it=sortColumns.iterator(); it.hasNext();)
			{
				TableSortDescription sortDescription=(TableSortDescription)it.next();
				int column=sortDescription.getColumn();
				Comparable value1=row1.getSortValue(column);
				Comparable value2=row2.getSortValue(column);
				int compareResult;
				int direction=sortDescription.getDirection().intValue();
				if (value1.getClass()==value2.getClass()) compareResult=direction*value1.compareTo(value2);
				else compareResult=direction*value1.getClass().getName().compareTo(value2.getClass().getName());
				if (compareResult!=0) return compareResult;
			}
			return 0;
		}
	}

	public void removeSelectedRows(JTable table)
	{
		int[] rows=table.getSelectedRows();
		if (rows!=null && rows.length>0)
		{
			final Set tableRows=new HashSet();
			for (int i=0; i<rows.length; i++)
			{
				tableRows.add(getRow(rows[i]));
			}
			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					for (Iterator it=tableRows.iterator(); it.hasNext();)
					{
						SortableTableRow row=(SortableTableRow)it.next();
						removeRow(row);
					}
				}
			});
		}

	}
}
