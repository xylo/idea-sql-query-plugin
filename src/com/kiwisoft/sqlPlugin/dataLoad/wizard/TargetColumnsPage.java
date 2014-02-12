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
import java.awt.Color;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.DocumentEvent;
import javax.swing.table.TableCellEditor;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.Project;

import com.kiwisoft.db.Database;
import com.kiwisoft.db.DatabaseSchema;
import com.kiwisoft.db.DatabaseUtils;
import com.kiwisoft.sqlPlugin.DefaultTableConfiguration;
import com.kiwisoft.sqlPlugin.Icons;
import com.kiwisoft.sqlPlugin.dataLoad.*;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.utils.DocumentAdapter;
import com.kiwisoft.utils.gui.IconManager;
import com.kiwisoft.utils.gui.ObjectStyle;
import com.kiwisoft.utils.gui.lookup.DialogLookupField;
import com.kiwisoft.utils.gui.table.*;
import com.kiwisoft.wizard.WizardDialog;
import com.kiwisoft.wizard.WizardPane;

/**
 * @author Stefan Stiller
 * @version $Revision: $, $Date: $
 */
public class TargetColumnsPage extends WizardPane
{
	private static final ObjectStyle EXCLUDED_STYLE=new ObjectStyle(Color.LIGHT_GRAY, null);

	private DialogLookupField tableField;
	private SortableTable columnTable;
	private TableModel columnTableModel;

	protected TargetColumnsPage(WizardDialog dialog)
	{
		super(dialog);
	}

	public String getTitle()
	{
		return "Target Column Formats";
	}

	protected String getHelpTopic()
	{
		return "KiwiSQL.dataLoad.targetColumnFormats";
	}

	public JComponent createComponent()
	{
		DataLoadWizard wizard=(DataLoadWizard)getDialog();
		tableField=new DialogLookupField(new DatabaseTableLookup(getDialog().getProject(), wizard.getDatabase()));

		columnTableModel=new TableModel((DataLoadWizard)getDialog());
		columnTable=new SortableTable(columnTableModel);
		columnTable.initializeColumns(new DefaultTableConfiguration("targetColumns"));

		JPanel panel=new JPanel(new GridBagLayout());
		panel.add(new JLabel("Target Table:"),
				  new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 5, 5), 0, 0));
		panel.add(tableField,
				  new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));
		panel.add(createToolBar(),
				  new GridBagConstraints(0, 1, 2, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 0, 5, 0), 0, 0));
		panel.add(new JScrollPane(columnTable),
				  new GridBagConstraints(0, 2, 2, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

		tableField.installDocumentListener(getListenerSupport(), new DocumentAdapter()
		{
			public void changedUpdate(DocumentEvent e)
			{
				DataLoadWizard wizard=((DataLoadWizard)getDialog());
				wizard.getDescriptor().setTargetTable(tableField.getText().trim());
				validateActions();
			}
		});
		return panel;
	}

	private JComponent createToolBar()
	{
		DefaultActionGroup actionGroup=new DefaultActionGroup();
		actionGroup.add(new LoadColumnsAction());
		ActionToolbar toolbar=ActionManager.getInstance().createActionToolbar("DataLoad.TargetColumns", actionGroup, true);
		return toolbar.getComponent();
	}

	public void initData()
	{
		DataLoadDescriptor descriptor=((DataLoadWizard)getDialog()).getDescriptor();
		tableField.setText(descriptor.getTargetTable());
		if (descriptor.getTargetColumns()!=null)
		{
			for (Iterator it=descriptor.getTargetColumns().iterator(); it.hasNext();)
			{
				TargetColumnDescriptor columnDescriptor=(TargetColumnDescriptor)it.next();
				columnTableModel.addRow(new TableRow(columnDescriptor));
			}
		}
		super.initData();
	}

	protected boolean canGoForward()
	{
		DataLoadWizard wizard=((DataLoadWizard)getDialog());
		DataLoadDescriptor descriptor=wizard.getDescriptor();
		if (StringUtils.isEmpty(descriptor.getTargetTable())) return false;
		List targetColumns=descriptor.getTargetColumns();
		if (targetColumns==null || targetColumns.isEmpty()) return false;
		for (Iterator it=targetColumns.iterator(); it.hasNext();)
		{
			TargetColumnDescriptor columnDescriptor=(TargetColumnDescriptor)it.next();
			if (!columnDescriptor.isValid()) return false;
		}
		return true;
	}

	protected WizardPane getNextPane()
	{
		return new OptionPage(getDialog());
	}

	private static class TableModel extends SortableTableModel
	{
		private static String[] COLUMNS={"include", "column", "type", "source", "sqlValue"};
		private TableLookupEditor sourceColumnEditor;

		public TableModel(DataLoadWizard wizard)
		{
			sourceColumnEditor=new TableLookupEditor(new SourceColumnDescriptorLookup(wizard));
			sourceColumnEditor.setFreeTextAllowed(true);
		}

		public int getColumnCount()
		{
			return COLUMNS.length;
		}

		public String getColumnName(int column)
		{
			return COLUMNS[column];
		}

		public TableCellEditor getCellEditor(int row, int col)
		{
			if (col==3) return sourceColumnEditor;
			return super.getCellEditor(row, col);
		}
	}

	private class LoadColumnsAction extends AnAction
	{
		public LoadColumnsAction()
		{
			super("Load Columns", "Load Columns from Database",
				  IconManager.getIcon(Icons.DATA_EXPORT));
		}

		public void update(AnActionEvent event)
		{
			event.getPresentation().setEnabled(!StringUtils.isEmpty(tableField.getText()));
		}

		public void actionPerformed(AnActionEvent event)
		{
			String table=tableField.getText();
			if (!StringUtils.isEmpty(table))
			{
				DataLoadWizard wizard=(DataLoadWizard)getDialog();
				Project project=wizard.getProject();
				Database database=wizard.getDatabase();
				Connection connection=DatabaseUtils.connect(project, database, true);
				List descriptors=new ArrayList();
				if (connection!=null)
				{
					try
					{
						DatabaseMetaData metaData=connection.getMetaData();
						DatabaseSchema schema=database.getDefaultSchema(project);
						ResultSet resultSet=metaData.getColumns(schema.getCatalog(), schema.getSchemaName(), table, null);
						try
						{
							while (resultSet.next())
							{
								String column=resultSet.getString("COLUMN_NAME");
								int type=resultSet.getInt("DATA_TYPE");
								descriptors.add(new TargetColumnDescriptor(column, type));
							}
						}
						finally
						{
							if (resultSet!=null) resultSet.close();
						}
						columnTable.sizeColumnsToFit(true, true);
					}
					catch (SQLException e)
					{
						e.printStackTrace();
					}
				}
				wizard.getDescriptor().setTargetColumns(descriptors);
				columnTableModel.clear();
				for (Iterator it=descriptors.iterator(); it.hasNext();)
				{
					TargetColumnDescriptor columnDescriptor=(TargetColumnDescriptor)it.next();
					columnTableModel.addRow(new TableRow(columnDescriptor));
				}
				validateActions();
			}
		}

		public boolean displayTextInToolbar()
		{
			return true;
		}
	}

	private class TableRow extends SortableTableRow
	{
		public TableRow(TargetColumnDescriptor descriptor)
		{
			super(descriptor);
		}

		public Class getCellClass(int column)
		{
			switch (column)
			{
				case 0:
					return Boolean.class;
				case 1:
				case 2:
				case 3:
				case 4:
					return String.class;
			}
			return super.getCellClass(column);
		}

		public ObjectStyle getCellStyle(int col)
		{
			if (getDescriptor().isIncluded()) return super.getCellStyle(col);
			return EXCLUDED_STYLE;
		}

		public Object getDisplayValue(int column)
		{
			switch (column)
			{
				case 0:
					return Boolean.valueOf(getDescriptor().isIncluded());
				case 1:
					return getDescriptor().getName();
				case 2:
					int type=getDescriptor().getJdbcType();
					return DatabaseUtils.getTypeString(type)+" ["+type+"]";
				case 3:
					return getDescriptor().getSourceColumn();
				case 4:
					return getDescriptor().getSql();
			}
			return null;
		}

		public boolean isEditable(int column)
		{
			return column==0 || column==3 || column==4;
		}

		public int setValue(Object value, int col)
		{
			switch (col)
			{
				case 0:
					getDescriptor().setIncluded(Boolean.TRUE.equals(value));
					validateActions();
					return SortableTableModel.ROW_UPDATE;
				case 3:
					getDescriptor().setSourceColumn((String)value);
					validateActions();
					return SortableTableModel.CELL_UPDATE;
				case 4:
					getDescriptor().setSql((String)value);
					validateActions();
					return SortableTableModel.CELL_UPDATE;
			}
			return SortableTableModel.NO_UPDATE;
		}

		public TargetColumnDescriptor getDescriptor()
		{
			return (TargetColumnDescriptor)getUserObject();
		}
	}
}
