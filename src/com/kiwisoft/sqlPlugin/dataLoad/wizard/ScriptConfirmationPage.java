package com.kiwisoft.sqlPlugin.dataLoad.wizard;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.ParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import bsh.EvalError;
import bsh.Interpreter;
import com.kiwisoft.sqlPlugin.dataLoad.BeanShellAdapter;
import com.kiwisoft.sqlPlugin.dataLoad.DataLoadDescriptor;
import com.kiwisoft.sqlPlugin.dataLoad.DataRow;
import com.kiwisoft.sqlPlugin.dataLoad.SourceColumnDescriptor;
import com.kiwisoft.utils.Utils;
import com.kiwisoft.utils.gui.IconManager;
import com.kiwisoft.utils.gui.progress.ProgressMessage;
import com.kiwisoft.utils.gui.progress.ProgressMessageListRenderer;
import com.kiwisoft.utils.gui.table.SortableTable;
import com.kiwisoft.utils.gui.table.SortableTableModel;
import com.kiwisoft.utils.gui.table.SortableTableRow;
import com.kiwisoft.wizard.WizardDialog;
import com.kiwisoft.wizard.WizardPane;

/**
 * @author Stefan Stiller
 * @version $Revision: $, $Date: $
 */
public class ScriptConfirmationPage extends WizardPane
{
	private SortableTable table;
	private JList messageField;
	private JLabel tableLabel;

	protected ScriptConfirmationPage(WizardDialog dialog)
	{
		super(dialog);
	}

	public String getTitle()
	{
		return "Script - Confirmation";
	}

	public JComponent createComponent()
	{
		tableLabel=new JLabel("Sample data after script:");
		table=new SortableTable(new TableModel());
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		messageField=new JList(new DefaultListModel());
		messageField.setCellRenderer(new ProgressMessageListRenderer());

		JPanel panel=new JPanel(new GridBagLayout());
		panel.add(tableLabel,
				  new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));
		panel.add(new JScrollPane(table),
				  new GridBagConstraints(0, 1, 1, 1, 1.0, 0.7, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));
		panel.add(new JLabel("Messages:"),
				  new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 0, 5, 0), 0, 0));
		panel.add(new JScrollPane(messageField),
				  new GridBagConstraints(0, 3, 1, 1, 1.0, 0.3, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));

		getListenerSupport().installSelectionListener(table, new RowSelectionListener());
		return panel;
	}

	public void initData()
	{
		DataLoadWizard wizard=((DataLoadWizard)getDialog());
		DataLoadDescriptor descriptor=wizard.getDescriptor();
		List sourceColumns=descriptor.getSourceColumns();
		List sampleData=wizard.getSampleData();
		Set columns=new LinkedHashSet();
		TableModel tableModel=new TableModel();
		int errors=0;
		int warnings=0;
		for (Iterator it=sampleData.iterator(); it.hasNext();)
		{
			DataRow row=new DataRow();
			MyBeanShellAdapter adapter=new MyBeanShellAdapter();
			String[] rowData=(String[])it.next();
			try
			{
				for (int i=0; i<rowData.length; i++)
				{
					String cellValue=rowData[i];
					SourceColumnDescriptor columnDescriptor=(SourceColumnDescriptor)sourceColumns.get(i);
					columns.add(columnDescriptor.getName());
					try
					{
						Object value=columnDescriptor.parse(cellValue);
						row.set(columnDescriptor.getName(), value);
					}
					catch (ParseException e)
					{
						adapter.error("Transformation error for '"+cellValue+"' in column '"+columnDescriptor.getName()+"': "+e.getMessage());
						row.set(columnDescriptor.getName(), e);
						throw e;
					}
				}
				try
				{
					Interpreter interpreter=new Interpreter();
					interpreter.set("bsh.system.shutdownOnExit", false);
					interpreter.set("row", row);
					interpreter.set("dataload", adapter);
					interpreter.eval(descriptor.getScript());
					columns.addAll(row.getFields());
				}
				catch (EvalError evalError)
				{
					String message=evalError.getMessage().trim();
					Matcher matcher=Pattern.compile("Sourced file: .*'' : (.*)").matcher(message);
					if (matcher.matches()) message=matcher.group(1);
					try
					{
						adapter.error("Error at line "+evalError.getErrorLineNumber()+": "+message);
					}
					catch (Exception e)
					{
						adapter.error(message);
					}
				}
			}
			catch (ParseException e)
			{
			}
			catch (Exception e)
			{
				e.printStackTrace();
				adapter.error(Utils.getShortClassName(e.getClass())+": "+e.getMessage());
			}
			errors+=adapter.getErrors();
			warnings+=adapter.getWarnings();
			tableModel.addRow(new TableRow(row, adapter.getSeverity(), adapter.getMessages()));
		}

		tableLabel.setText("Sample data after script: "+errors+" error(s); "+warnings+" warning(s)");

		((DataLoadWizard)getDialog()).setSourceColumns(columns);
		tableModel.setColumns(new ArrayList(columns));
		table.setModel(tableModel);
		table.sizeColumnsToFit(true, true);
		table.clearSelection();
		super.initData();
	}

	protected boolean canGoForward()
	{
		return true;
	}

	protected WizardPane getNextPane()
	{
		return new TargetColumnsPage(getDialog());
	}

	protected String getHelpTopic()
	{
		return "KiwiSQL.dataLoad.scriptConfirmation";
	}

	private static class TableModel extends SortableTableModel
	{
		private List columns=Collections.EMPTY_LIST;

		public TableModel()
		{
		}

		public void setColumns(List columns)
		{
			this.columns=columns;
		}

		public boolean isResortable()
		{
			return false;
		}

		public int getColumnCount()
		{
			return columns.size()+1;
		}

		public Object getValueAt(int rowIndex, int column)
		{
			TableRow tableRow=(TableRow)getRow(rowIndex);
			if (column==0) return tableRow.getDisplayValue(column);
			String columnName=(String)columns.get(column-1);
			return tableRow.getDisplayValue(columnName);
		}

		public String getColumnName(int column)
		{
			if (column==0) return "";
			return (String)columns.get(column-1);
		}
	}

	private static class TableRow extends SortableTableRow
	{
		private List messages;
		private Icon severityIcon;

		public TableRow(DataRow row, int severity, List messages)
		{
			super(row);
			this.messages=messages;
			switch (severity)
			{
				case ProgressMessage.ERROR:
					severityIcon=IconManager.getIcon("/com/kiwisoft/utils/gui/progress/error.gif");
					break;
				case ProgressMessage.WARNING:
					severityIcon=IconManager.getIcon("/com/kiwisoft/utils/gui/progress/warning.gif");
					break;
				default:
					severityIcon=IconManager.getIcon("/com/kiwisoft/utils/gui/progress/ok.gif");
			}
		}

		public List getMessages()
		{
			return messages;
		}

		public Class getCellClass(int col)
		{
			return null;
		}

		public Object getDisplayValue(int column)
		{
			return severityIcon;
		}

		public Object getDisplayValue(String column)
		{
			DataRow row=(DataRow)getUserObject();
			return row.get(column);
		}
	}

	private class MyBeanShellAdapter implements BeanShellAdapter
	{
		private List messages=new ArrayList();
		private int severity=ProgressMessage.INFO;
		private int errors;
		private int warnings;

		public void info(String message)
		{
			messages.add(new ProgressMessage(message, ProgressMessage.INFO));
		}

		public void warning(String message)
		{
			messages.add(new ProgressMessage(message, ProgressMessage.WARNING));
			warnings++;
			severity=Math.max(ProgressMessage.WARNING, severity);
		}

		public void error(String message)
		{
			messages.add(new ProgressMessage(message, ProgressMessage.ERROR));
			errors++;
			severity=ProgressMessage.ERROR;
		}

		public int getErrors()
		{
			return errors;
		}

		public int getWarnings()
		{
			return warnings;
		}

		public List getMessages()
		{
			return messages;
		}

		public int getSeverity()
		{
			return severity;
		}
	}

	private class RowSelectionListener implements ListSelectionListener
	{
		public void valueChanged(ListSelectionEvent e)
		{
			if (!e.getValueIsAdjusting())
			{
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						DefaultListModel messagesModel=(DefaultListModel)messageField.getModel();
						messagesModel.clear();
						int index=table.getSelectedRow();
						if (index>=0)
						{
							SortableTableModel model=(SortableTableModel)table.getModel();
							TableRow row=(TableRow)model.getRow(index);
							List messages=row.getMessages();
							for (Iterator it=messages.iterator(); it.hasNext();) messagesModel.addElement(it.next());
						}
					}
				});
			}
		}
	}
}
