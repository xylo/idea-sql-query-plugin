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
package com.kiwisoft.sqlPlugin.dataLoad.wizard;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import javax.swing.*;
import javax.swing.table.TableColumn;
import javax.swing.event.DocumentEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;

import com.kiwisoft.sqlPlugin.dataLoad.DataType;
import com.kiwisoft.sqlPlugin.dataLoad.SourceColumnDescriptor;
import com.kiwisoft.sqlPlugin.dataLoad.DataLoadDescriptor;
import com.kiwisoft.utils.DocumentAdapter;
import com.kiwisoft.utils.ListenerSupport;
import com.kiwisoft.utils.Utils;
import com.kiwisoft.utils.gui.IconManager;
import com.kiwisoft.utils.gui.lookup.BooleanFormatLookup;
import com.kiwisoft.utils.gui.lookup.DateFormatLookup;
import com.kiwisoft.utils.gui.lookup.DialogLookupField;
import com.kiwisoft.utils.gui.lookup.NumberFormatLookup;
import com.kiwisoft.utils.gui.table.SortableTable;
import com.kiwisoft.utils.gui.table.SortableTableModel;
import com.kiwisoft.utils.gui.table.SortableTableRow;
import com.kiwisoft.wizard.WizardDialog;
import com.kiwisoft.wizard.WizardPane;

/**
 * @author Stefan Stiller
 * @version $Revision: $, $Date: $
 */
public class SourceColumnsPage extends WizardPane
{
	private SortableTable table;
	private TableModel tableModel;
	private JPanel columnFormatContainer;
	private ColumnFormatPanel columnFormatPanel;

	protected SourceColumnsPage(WizardDialog dialog)
	{
		super(dialog);
	}

	public String getTitle()
	{
		return "Data Source - Column Formats";
	}

	protected String getHelpTopic()
	{
		return "KiwiSQL.dataLoad.fileColumnFormats";
	}

	public JComponent createComponent()
	{
		tableModel=new TableModel(Collections.EMPTY_LIST);
		table=new SortableTable(tableModel);
		table.setColumnSelectionAllowed(true);
		table.setRowSelectionAllowed(false);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		ListSelectionModel selectionModel=table.getColumnModel().getSelectionModel();
		selectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		getListenerSupport().installColumnSelectionListener(selectionModel, new SelectionListener());

		columnFormatContainer=new JPanel(new BorderLayout());
		columnFormatContainer.add(new JLabel("Select a column to change type and format."), BorderLayout.CENTER);

		JPanel panel=new JPanel(new GridBagLayout());
		panel.add(new JScrollPane(table),
				  new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
		panel.add(columnFormatContainer,
				  new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
		return panel;
	}

	public void initData()
	{
		DataLoadWizard wizard=((DataLoadWizard)getDialog());
		DataLoadDescriptor descriptor=wizard.getDescriptor();
		List sourceColumns=descriptor.getSourceColumns();
		if (sourceColumns==null) sourceColumns=descriptor.createSourceColumns(wizard.getSampleDataTitles());

		tableModel=new TableModel(sourceColumns);
		for (Iterator it=wizard.getSampleData().iterator(); it.hasNext();)
		{
			String[] row=(String[])it.next();
			tableModel.addRow(new TableRow(row));
		}
		table.setModel(tableModel);
		table.sizeColumnsToFit(true, true);
		table.clearSelection();
		super.initData();
	}

	protected boolean canGoForward()
	{
		DataLoadWizard wizard=((DataLoadWizard)getDialog());
		DataLoadDescriptor descriptor=wizard.getDescriptor();
		List sourceColumns=descriptor.getSourceColumns();
		if (sourceColumns!=null)
		{
			Set names=new HashSet();
			for (Iterator it=sourceColumns.iterator(); it.hasNext();)
			{
				SourceColumnDescriptor columnDescriptor=(SourceColumnDescriptor)it.next();
				if (!columnDescriptor.isValid() || names.contains(columnDescriptor.getName())) return false;
				names.add(columnDescriptor.getName());
			}
			return true;
		}
		return false;
	}

	protected WizardPane getNextPane()
	{
		return new ScriptPage(getDialog());
	}

	public void dispose()
	{
		if (columnFormatPanel!=null) columnFormatPanel.dispose();
		super.dispose();
	}

	private static class TableModel extends SortableTableModel
	{
		private List sourceColumns;

		public TableModel(List sourceColumns)
		{
			this.sourceColumns=sourceColumns;
		}

		public boolean isResortable()
		{
			return false;
		}

		public int getColumnCount()
		{
			return sourceColumns.size();
		}

		public Class getCellClass(int row, int col)
		{
			return getColumnDescriptor(col).getType().getType();
		}

		public Object getValueAt(int rowIndex, int columnIndex)
		{
			String value=(String)super.getValueAt(rowIndex, columnIndex);
			SourceColumnDescriptor descriptor=getColumnDescriptor(columnIndex);
			try
			{
				return descriptor.parse(value);
			}
			catch (Exception e)
			{
				return "<"+Utils.getShortClassName(e.getClass())+": "+e.getMessage()+">";
			}
		}

		public String getColumnName(int column)
		{
			return ((SourceColumnDescriptor)sourceColumns.get(column)).getName();
		}

		public SourceColumnDescriptor getColumnDescriptor(int column)
		{
			return (SourceColumnDescriptor)sourceColumns.get(column);
		}
	}

	private static class TableRow extends SortableTableRow
	{
		public TableRow(String[] data)
		{
			super(data);
		}

		public Object getDisplayValue(int column)
		{
			String[] data=(String[])getUserObject();
			if (column<data.length) return data[column];
			return null;
		}
	}

	private class SelectionListener implements ListSelectionListener
	{
		public void valueChanged(ListSelectionEvent e)
		{
			if (!e.getValueIsAdjusting())
			{
				ListSelectionModel selectionModel=table.getColumnModel().getSelectionModel();
				if (columnFormatPanel!=null)
				{
					columnFormatPanel.dispose();
					columnFormatPanel=null;
				}
				columnFormatContainer.removeAll();
				int column=selectionModel.getMinSelectionIndex();
				if (column>=0)
				{
					column=table.convertColumnIndexToModel(column);
					columnFormatPanel=new ColumnFormatPanel(column, tableModel.getColumnDescriptor(column));
					columnFormatContainer.add(columnFormatPanel, BorderLayout.CENTER);
				}
				else
				{
					columnFormatContainer.add(new JLabel("Select a column to change type and format."), BorderLayout.CENTER);
				}
				columnFormatContainer.updateUI();
			}
		}
	}

	private class ColumnFormatPanel extends JPanel
	{
		private JTextField titleField;
		private DialogLookupField formatField;
		private JComboBox typeField;
		private JCheckBox trimField;

		private int column;
		private SourceColumnDescriptor columnDescriptor;
		private ListenerSupport listenerSupport=new ListenerSupport();

		public ColumnFormatPanel(int col, SourceColumnDescriptor columnDescriptor)
		{
			this.column=col;
			this.columnDescriptor=columnDescriptor;

			initializeComponents();

			setLayout(new GridBagLayout());
			add(new JLabel("Name:"),
				new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
			add(titleField,
				new GridBagConstraints(1, 0, 3, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 0, 5, 5), 0, 0));

			add(trimField,
				new GridBagConstraints(1, 1, 3, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 0, 5, 5), 0, 0));

			add(new JLabel("Type:"),
				new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
			add(typeField,
				new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 0, 5, 5), 0, 0));
			add(new JLabel("Format:"),
				new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
			add(formatField,
				new GridBagConstraints(3, 2, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 0, 5, 5), 0, 0));

			initializeData();
			initializeListeners();
		}

		private void initializeData()
		{
			DataType type=columnDescriptor.getType();
			typeField.setSelectedItem(type);
			typeField.setEnabled(true);
			formatField.setText(columnDescriptor.getPattern());
			formatField.setEnabled(type!=null && type.usePattern());
			trimField.setSelected(columnDescriptor.isTrim());
			titleField.setText(columnDescriptor.getName());
		}

		private void initializeComponents()
		{
			typeField=new JComboBox(DataType.values());
			typeField.setEnabled(false);
			formatField=new DialogLookupField(IconManager.getIcon("/com/kiwisoft/utils/gui/table/lookup_zoom.png"));
			formatField.setEnabled(false);
			trimField=new JCheckBox("Remove leading and trailing whitespaces");
			titleField=new JTextField();
		}

		private void initializeListeners()
		{
			listenerSupport.installActionListener(typeField, new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					DataType type=(DataType)typeField.getSelectedItem();
					columnDescriptor.setType(type);
					tableModel.fireTableChanged(new TableModelEvent(tableModel, 0, tableModel.getRowCount(), column));
					if (DataType.DATE.equals(type) || DataType.TIME_PERIOD.equals(type))
						formatField.setLookup(new DateFormatLookup());
					else if (DataType.NUMBER.equals(type))
						formatField.setLookup(new NumberFormatLookup());
					else if (DataType.FLAG.equals(type))
						formatField.setLookup(new BooleanFormatLookup());
					else
						formatField.setLookup(null);
					formatField.setEnabled(type!=null && type.usePattern());
					validateActions();
				}
			});
			formatField.installDocumentListener(listenerSupport, new DocumentAdapter()
			{
				public void changedUpdate(DocumentEvent e)
				{
					columnDescriptor.setPattern(formatField.getText());
					tableModel.fireTableChanged(new TableModelEvent(tableModel, 0, tableModel.getRowCount(), column));
					validateActions();
				}
			});
			listenerSupport.installActionListener(trimField, new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					columnDescriptor.setTrim(trimField.isSelected());
					tableModel.fireTableChanged(new TableModelEvent(tableModel, 0, tableModel.getRowCount(), column));
					validateActions();
				}
			});
			listenerSupport.installDocumentListener(titleField, new DocumentAdapter()
			{
				public void changedUpdate(DocumentEvent e)
				{
					columnDescriptor.setName(titleField.getText());
					int viewColumn=table.convertColumnIndexToView(column);
					TableColumn tableColumn=table.getColumnModel().getColumn(viewColumn);
					tableColumn.setHeaderValue(titleField.getText());
					table.getTableHeader().repaint();
					validateActions();
				}
			});
		}

		public void dispose()
		{
			listenerSupport.dispose();
		}
	}
}
