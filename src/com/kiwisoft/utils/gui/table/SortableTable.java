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
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.event.*;
import javax.swing.table.*;

import com.kiwisoft.utils.gui.IconManager;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.5 $, $Date: 2006/03/24 18:58:21 $
 */
public class SortableTable extends JTable implements MouseListener
{
	public static final String NULL_STRING="nullString";
	public static final String VERTICAL_CELL_ALIGNMENT="verticalAlignment";
	public static final String TOP="top";
	public static final String AUTO_RESIZE_ROWS="autoResizeRows";
	public static final String MAX_ROW_SIZE="maxRowSize";
	public static final String ALTERNATE_ROW_BACKGROUND="alternateRowBackground";
	public static final String ALTERNATE_ROW_FOREGROUND="alternateRowForeground";
	public static final String NO_QUICK_EDITING="noQuickEditing";

	private ColumnModelListener columnModelListener;
	public TableConfiguration configuration;

	public SortableTable(SortableTableModel aModel)
	{
		super(aModel);
		JTableHeader tableHeader=getTableHeader();
		tableHeader.addMouseListener(this);
		tableHeader.setDefaultRenderer(new DynamicTableCellRenderer());
	}

	public TableCellEditor getCellEditor(int row, int col)
	{
		TableCellEditor cellEditor=null;
		try
		{
			SortableTableModel tm=(SortableTableModel)getModel();
			TableColumn tableColumn=getColumnModel().getColumn(col);
			cellEditor=tm.getCellEditor(row, tableColumn.getModelIndex());
			if (cellEditor==null)
			{
				Class result;
				Object value=tm.getValueAt(row, tableColumn.getModelIndex());
				if (value!=null) result=value.getClass();
				else result=Object.class;
				cellEditor=getDefaultEditor(result);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		if (cellEditor==null) cellEditor=super.getCellEditor(row, col);
		if (Boolean.TRUE.equals(getClientProperty(NO_QUICK_EDITING)))
		{
			if (cellEditor instanceof EditorFactory.StyleEditor)
				((EditorFactory.StyleEditor)cellEditor).setQuickEditable(false);
			else if (cellEditor instanceof ObjectEditor)
				((ObjectEditor)cellEditor).setQuickEditable(false);
		}
		return cellEditor;
	}

	public TableCellRenderer getCellRenderer(int row, int col)
	{
		TableCellRenderer cellRenderer=null;
		try
		{
			SortableTableModel tm=(SortableTableModel)getModel();
			TableColumn tableColumn=getColumnModel().getColumn(col);

			cellRenderer=tm.getCellRenderer(row, tableColumn.getModelIndex());
			if (cellRenderer!=null) return cellRenderer;

			Class result;
			Object value=tm.getValueAt(row, tableColumn.getModelIndex());
			if (value!=null) result=value.getClass();
			else result=Object.class;
			Class cellClass=result;
			cellRenderer=getDefaultRenderer(cellClass);
		}
		catch (Exception e)
		{
		}
		if (cellRenderer==null) cellRenderer=super.getCellRenderer(row, col);
		return cellRenderer;
	}

	public void mouseClicked(MouseEvent event)
	{
		if ((event.getModifiers()&MouseEvent.BUTTON1_MASK)!=0)
		{
			if (getModel() instanceof SortableTableModel)
			{
				SortableTableModel model=(SortableTableModel)getModel();
				if (model.isResortable())
				{
					int column=getTableHeader().columnAtPoint(event.getPoint());
					if (column>=0)
					{
						column=convertColumnIndexToModel(column);
						boolean add=false;
						if ((event.getModifiers()&MouseEvent.CTRL_MASK)!=0) add=true;
						model.addSortColumn(column, add);
						getTableHeader().repaint();
						if (configuration!=null)
						{
							TableColumnModel columnModel=getColumnModel();
							int columnCount=columnModel.getColumnCount();
							for (int i=0; i<columnCount; i++)
							{
								TableColumn tableColumn=columnModel.getColumn(i);
								int modelIndex=tableColumn.getModelIndex();
								TableSortDescription sortDescription=model.getSortDescription(modelIndex);
								if (sortDescription!=null)
								{
									configuration.setSortDirection(tableColumn.getIdentifier(), sortDescription.getDirection());
									configuration.setSortIndex(tableColumn.getIdentifier(), model.getSortIndex(modelIndex));
								}
								else
									configuration.setSortIndex(tableColumn.getIdentifier(), -1);
							}
						}
					}
				}
				event.consume();
			}
		}
	}

	public void mousePressed(MouseEvent e)
	{
	}

	public void mouseReleased(MouseEvent e)
	{
	}

	public void mouseEntered(MouseEvent e)
	{
	}

	public void mouseExited(MouseEvent e)
	{
	}

	public void addNotify()
	{
		super.addNotify();
		if (columnModelListener!=null)
		{
			// to avoid double registration we remove the listener first
			getColumnModel().removeColumnModelListener(columnModelListener);
			getColumnModel().addColumnModelListener(columnModelListener);
		}
	}

	public void removeNotify()
	{
		if (columnModelListener!=null) getColumnModel().removeColumnModelListener(columnModelListener);
		super.removeNotify();
	}

	public void initializeColumns(TableConfiguration configuration)
	{
		if (configuration!=null)
		{
			Object lock=new Object();
			this.configuration=configuration;
			if (configuration.isSupportWidths()) setAutoResizeMode(AUTO_RESIZE_OFF);
			TableColumnModel columnModel=getColumnModel();
			try
			{
				if (columnModelListener!=null) columnModelListener.lock(lock);
				int columnCount=columnModel.getColumnCount();
				Object[] identifiers=new Object[columnCount];
				TableSortDescription[] sortDescriptions=new TableSortDescription[columnCount];
				for (int i=0; i<columnCount; i++)
				{
					TableColumn column=columnModel.getColumn(i);
					Object identifier=configuration.isColumnIndexIdentifier() ? new Integer(i) : column.getIdentifier();
					column.setIdentifier(identifier);
					identifiers[i]=identifier;
					if (configuration.isSupportTitles())
					{
						String title=configuration.getTitle(identifier);
						column.setHeaderValue(title);
					}
					if (configuration.isSupportWidths())
					{
						int width=configuration.getWidth(identifier);
						column.setPreferredWidth(width);
					}
					Integer sortDirection=configuration.getSortDirection(identifier);
					if (sortDirection!=null)
					{
						int sortIndex=configuration.getSortIndex(identifier);
						if (sortIndex>=0 && sortIndex<columnCount)
							sortDescriptions[sortIndex]=new TableSortDescription(column.getModelIndex(), sortDirection);
					}
				}
				TableModel tableModel=getModel();
				if (tableModel instanceof SortableTableModel)
				{
					SortableTableModel sortableTableModel=(SortableTableModel)tableModel;
					if (sortableTableModel.isResortable())
					{
						sortableTableModel.clearSort();
						for (int i=0; i<sortDescriptions.length; i++)
						{
							TableSortDescription sortDescription=sortDescriptions[i];
							if (sortDescription!=null) sortableTableModel.addSortColumn(sortDescription);
						}
						sortableTableModel.sort();
					}
				}

				hiddenColumns.clear();
				for (int i=0; i<columnCount; i++)
				{
					Object identifier=identifiers[i];
					int oldIndex=columnModel.getColumnIndex(identifier);
					int newIndex=configuration.getIndex(identifier);
					TableColumn column=columnModel.getColumn(oldIndex);
					if (newIndex>=0 && newIndex<columnCount) columnModel.moveColumn(oldIndex, newIndex);
					if (configuration.isHidden(identifier)) hiddenColumns.add(column);
				}
				for (Iterator it=hiddenColumns.iterator(); it.hasNext();)
				{
					columnModel.removeColumn((TableColumn)it.next());
				}
			}
			finally
			{
				if (columnModelListener==null)
				{
					columnModelListener=new ColumnModelListener();
					columnModel.addColumnModelListener(columnModelListener);
				}
				else
				{
					columnModelListener.unlock(lock);
				}
			}
		}
	}

	public void tableChanged(TableModelEvent e)
	{
		Object lock=new Object();
		if (columnModelListener!=null) columnModelListener.lock(lock);
		try
		{
			super.tableChanged(e);
			if (e.getFirstRow()==TableModelEvent.HEADER_ROW)
			{
				initializeColumns(configuration);
			}
			if (Boolean.TRUE.equals(getClientProperty(AUTO_RESIZE_ROWS)))
			{
				if (e.getFirstRow()!=TableModelEvent.HEADER_ROW)
				{
					if (e.getType()!=TableModelEvent.DELETE) sizeRowsToFit(e.getFirstRow(), e.getLastRow());
				}
				else
				{
					sizeRowsToFit();
				}
			}
		}
		finally
		{
			if (columnModelListener!=null) columnModelListener.unlock(lock);
		}
	}

	public void sizeRowsToFit(int firstRow, int lastRow)
	{
		int rowCount=getModel().getRowCount();
		for (int row=firstRow; row<=lastRow && row<rowCount; row++) sizeRowToFit(row);
	}

	public void sizeRowsToFit()
	{
		for (int row=0; row<getModel().getRowCount(); row++) sizeRowToFit(row);
	}

	public void sizeRowToFit(int row)
	{
		int preferredHeight=20;
		for (int i=0; i<getColumnCount(); i++)
		{
			TableColumn column=getColumnModel().getColumn(i);
			int columnIndex=column.getModelIndex();
			Object value=getModel().getValueAt(row, columnIndex);
			TableCellRenderer renderer=getCellRenderer(row, columnIndex);
			Component component=renderer.getTableCellRendererComponent(this, value, false, false, row, columnIndex);
			Dimension preferredSize=component.getPreferredSize();
			if (preferredSize.height>preferredHeight) preferredHeight=preferredSize.height;
		}
		Integer maxSize=(Integer)getClientProperty(MAX_ROW_SIZE);
		if (maxSize!=null) preferredHeight=Math.min(maxSize.intValue(), preferredHeight);
		setRowHeight(row, preferredHeight);
	}

	public void sizeColumnsToFit(boolean useHeader, boolean useContent)
	{
		TableUtils.sizeColumnsToFit(this, useHeader, useContent);
	}

	public String getToolTipText(MouseEvent event)
	{
		int columnIndex=columnAtPoint(event.getPoint());
		int rowIndex=rowAtPoint(event.getPoint());
		if (columnIndex>=0 && rowIndex>=0)
		{
			String toolTip=null;
			try
			{
				SortableTableModel tm=(SortableTableModel)getModel();
				TableColumn tableColumn=getColumnModel().getColumn(columnIndex);
				Object toolTipObject=tm.getToolTipText(rowIndex, tableColumn.getModelIndex());
				if (toolTipObject!=null)
				{
					TableCellRenderer renderer=getCellRenderer(rowIndex, columnIndex);
					Component component=renderer.getTableCellRendererComponent(this, toolTipObject, false, false, rowIndex, columnIndex);
					if (component instanceof JLabel)
						toolTip=((JLabel)component).getText();
					else
						toolTip=toolTipObject.toString();
				}
				String tableHeader=(String)tableColumn.getHeaderValue();
				if (toolTip==null) return tableHeader;
				else return toolTip;
			}
			catch (Exception e)
			{
			}
			if (toolTip!=null) return toolTip;
		}
		return super.getToolTipText(event);
	}

	private Set hiddenColumns=new HashSet();

	public void hideColumn(TableColumn column)
	{
		if (hiddenColumns.add(column))
		{
			removeColumn(column);
			if (configuration!=null) configuration.setHidden(column.getIdentifier(), true);
		}
	}

	public void showColumn(TableColumn column)
	{
		if (hiddenColumns.remove(column))
		{
			addColumn(column);
			if (configuration!=null) configuration.setHidden(column.getIdentifier(), false);
		}
	}

	public Set getHiddenColumns()
	{
		return Collections.unmodifiableSet(hiddenColumns);
	}

	private class ColumnModelListener implements TableColumnModelListener
	{
		private Object lock;

		private void lock(Object lock)
		{
			if (this.lock==null) this.lock=lock;
		}

		private void unlock(Object lock)
		{
			if (this.lock==lock) this.lock=null;
		}

		public void columnAdded(TableColumnModelEvent e)
		{
			columnMoved(e);
		}

		public void columnRemoved(TableColumnModelEvent e)
		{
			columnMoved(e);
		}

		public void columnMoved(TableColumnModelEvent e)
		{
			if (lock==null)
			{
				TableColumnModel columnModel=(TableColumnModel)e.getSource();
				for (int i=0; i<columnModel.getColumnCount(); i++)
				{
					TableColumn column=columnModel.getColumn(i);
					configuration.setIndex(column.getIdentifier(), i);
				}
			}
		}

		public void columnMarginChanged(ChangeEvent e)
		{
			if (lock==null)
			{
				if (configuration.isSupportWidths())
				{
					TableColumnModel columnModel=(TableColumnModel)e.getSource();
					for (int i=0; i<columnModel.getColumnCount(); i++)
					{
						TableColumn column=columnModel.getColumn(i);
						configuration.setWidth(column.getIdentifier(), column.getWidth());
					}
				}
			}
		}

		public void columnSelectionChanged(ListSelectionEvent e)
		{
		}
	}

	private class DynamicTableCellRenderer extends DefaultTableCellRenderer implements TableConstants
	{
		private ImageIcon iconAscend=IconManager.getIcon("/com/kiwisoft/utils/gui/table/ascend.gif");
		private ImageIcon iconAscend2=IconManager.getIcon("/com/kiwisoft/utils/gui/table/ascend2.gif");
		private ImageIcon iconDescend=IconManager.getIcon("/com/kiwisoft/utils/gui/table/descend.gif");
		private ImageIcon iconDescend2=IconManager.getIcon("/com/kiwisoft/utils/gui/table/descend2.gif");

		public DynamicTableCellRenderer()
		{
			setHorizontalAlignment(JLabel.CENTER);
		}

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
													   int row, int column)
		{
			if (table!=null)
			{
				TableModel model=table.getModel();
				if (model instanceof SortableTableModel)
				{
					SortableTableModel sortableTableModel=(SortableTableModel)model;
					JTableHeader header=table.getTableHeader();
					if (header!=null)
					{
						this.setForeground(header.getForeground());
						this.setBackground(header.getBackground());
						this.setFont(header.getFont());
						this.setIcon(null);
						int columnIndex=getColumnModel().getColumn(column).getModelIndex();
						TableSortDescription sortDescription=sortableTableModel.getSortDescription(columnIndex);
						if (sortDescription!=null)
						{
							int sortIndex=sortableTableModel.getSortIndex(columnIndex);
							if (sortIndex==0)
							{
								if (ASCEND.equals(sortDescription.getDirection())) this.setIcon(iconAscend);
								else this.setIcon(iconDescend);
							}
							else
							{
								if (ASCEND.equals(sortDescription.getDirection())) this.setIcon(iconAscend2);
								else this.setIcon(iconDescend2);
							}
						}
					}
				}
			}
			setText(value==null ? "" : value.toString());
			if (value!=null) setToolTipText(value.toString());
			this.setBorder(UIManager.getBorder("TableHeader.cellBorder"));
			return this;
		}
	}

	public void editingStopped(ChangeEvent e)
	{
		super.editingStopped(e);
		requestFocus();
	}

	public void editingCanceled(ChangeEvent e)
	{
		super.editingCanceled(e);
		requestFocus();
	}
}
