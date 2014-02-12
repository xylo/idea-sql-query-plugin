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
package com.kiwisoft.sqlPlugin;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import javax.swing.*;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;

import com.kiwisoft.utils.gui.table.SortableTable;
import com.kiwisoft.utils.gui.table.SortableTableModel;
import com.kiwisoft.utils.gui.table.SortableTableRow;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.2 $, $Date: 2006/03/24 17:46:32 $
 */
public class ManageColumnsDialog extends DialogWrapper
{
	private Map columns;

	private JPanel pnlContent;
	private SortableTable table;
	private ColumnTableModel tableModel;

	public ManageColumnsDialog(Project project, SortableTable table)
	{
		super(project, false);
		this.table=table;
		setTitle("Manage Columns");
		initializeComponents();
		init();
	}

	public Action[] createActions()
	{
		return new Action[]{new ApplyAction(), new CancelAction()};
	}

	public JComponent createCenterPanel()
	{
		return pnlContent;
	}

	private void initializeComponents()
	{
		columns=new TreeMap(new Comparator()
		{
			public int compare(Object o1, Object o2)
			{
				TableColumn column1=(TableColumn)o1;
				TableColumn column2=(TableColumn)o2;
				return column1.getModelIndex()-column2.getModelIndex();
			}
		});

		TableColumnModel columnModel=table.getColumnModel();
		for (int i=0; i<columnModel.getColumnCount(); i++)
		{
			TableColumn column=columnModel.getColumn(i);
			columns.put(column, Boolean.TRUE);
		}
		for (Iterator it=table.getHiddenColumns().iterator(); it.hasNext();)
		{
			TableColumn column=(TableColumn)it.next();
			columns.put(column, Boolean.FALSE);
		}

		tableModel=new ColumnTableModel();
		for (Iterator it=columns.keySet().iterator(); it.hasNext();)
		{
			tableModel.addRow(new ColumnRow((TableColumn)it.next()));
		}
		SortableTable columnTable=new SortableTable(tableModel);
		columnTable.initializeColumns(new DefaultTableConfiguration("columnsManager"));
		columnTable.sizeColumnsToFit(true, true);

		JScrollPane scrollPane=new JScrollPane(columnTable);
		scrollPane.setPreferredSize(new Dimension(400, 300));

		JPanel buttonPanel=new JPanel(new FlowLayout(FlowLayout.LEADING));
		buttonPanel.add(new JButton(new SelectAllAction(true)));
		buttonPanel.add(new JButton(new SelectAllAction(false)));

		pnlContent=new JPanel(new BorderLayout());
		pnlContent.add(scrollPane, BorderLayout.CENTER);
		pnlContent.add(buttonPanel, BorderLayout.SOUTH);
	}

	private static class ColumnTableModel extends SortableTableModel
	{
		private final static String[] COLUMNS={"visible", "columnName"};

		public int getColumnCount()
		{
			return COLUMNS.length;
		}

		public String getColumnName(int column)
		{
			return COLUMNS[column];
		}
	}

	private class ColumnRow extends SortableTableRow
	{
		public ColumnRow(TableColumn columnName)
		{
			super(columnName);
		}

		public Class getCellClass(int column)
		{
			switch (column)
			{
				case 0:
					return Boolean.class;
				case 1:
					return String.class;
			}
			return super.getCellClass(column);
		}

		public Object getDisplayValue(int column)
		{
			switch (column)
			{
				case 0:
					return columns.get(getUserObject());
				case 1:
					return String.valueOf(((TableColumn)getUserObject()).getHeaderValue());
			}
			return null;
		}

		public int setValue(Object value, int column)
		{
			if (column==0 && value instanceof Boolean)
			{
				columns.put(getUserObject(), value);
				return SortableTableModel.CELL_UPDATE;
			}
			return SortableTableModel.NO_UPDATE;
		}

		public boolean isEditable(int column)
		{
			return column==0;
		}
	}

	private class SelectAllAction extends AbstractAction
	{
		private boolean select;

		public SelectAllAction(boolean select)
		{
			super(select ? "Select All" : "Unselect All");
			this.select=select;
		}

		public void actionPerformed(ActionEvent e)
		{
			for (Iterator it=columns.keySet().iterator(); it.hasNext();)
			{
				columns.put(it.next(), Boolean.valueOf(select));
			}
			tableModel.fireTableDataChanged();
		}
	}

	private class ApplyAction extends AbstractAction
	{
		public ApplyAction()
		{
			super("Apply");
			putValue(DEFAULT_ACTION, Boolean.TRUE);
		}

		public void actionPerformed(ActionEvent e)
		{
			for (Iterator it=columns.entrySet().iterator(); it.hasNext();)
			{
				Map.Entry entry=(Map.Entry)it.next();
				TableColumn column=(TableColumn)entry.getKey();
				Boolean selected=(Boolean)entry.getValue();
				if (selected==null || selected.booleanValue())
				{
					if (table.getHiddenColumns().contains(column)) table.showColumn(column);
				}
				else
				{
					if (!table.getHiddenColumns().contains(column)) table.hideColumn(column);
				}
			}
			dispose();
		}
	}

	private class CancelAction extends AbstractAction
	{
		public CancelAction()
		{
			super("Cancel");
		}

		public void actionPerformed(ActionEvent e)
		{
			dispose();
		}
	}
}
