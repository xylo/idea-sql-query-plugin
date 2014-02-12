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

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.AbstractTableModel;

import com.intellij.openapi.vfs.VirtualFile;

import com.kiwisoft.sqlPlugin.dataLoad.*;
import com.kiwisoft.sqlPlugin.dataLoad.FileDescriptor;
import com.kiwisoft.sqlPlugin.dataLoad.io.CSVReader;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.utils.gui.table.TableUtils;
import com.kiwisoft.utils.gui.GuiUtils;
import com.kiwisoft.wizard.WizardDialog;
import com.kiwisoft.wizard.WizardPane;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.3 $, $Date: 2002/02/22 10:00:24 $
 */
public class DataSourcePage extends WizardPane
{
	private JComboBox formatField;
	private JPanel fileTypePanel;
	private DataSourcePage.CSVComponent csvComponent;
	private DataSourcePage.FixedWidthComponent fixedWidthComponent;

	public DataSourcePage(WizardDialog wizardDialog)
	{
		super(wizardDialog);
	}

	public String getTitle()
	{
		return "Data Source - File Format";
	}

	public JComponent createComponent()
	{
		fileTypePanel=new JPanel(new GridBagLayout());
		csvComponent=new CSVComponent();
		fixedWidthComponent=new FixedWidthComponent();
		formatField=new JComboBox(new Object[]{csvComponent, fixedWidthComponent});
		formatField.setEditable(false);
		FormatListener formatListener=new FormatListener();
		getListenerSupport().installActionListener(formatField, formatListener);
		formatListener.actionPerformed(null);

		JPanel panel=new JPanel(new GridBagLayout());
		int row=0;
		panel.add(new JLabel("File Format:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 5, 5), 0, 0));
		panel.add(formatField, new GridBagConstraints(1, row, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));
		row++;
		panel.add(fileTypePanel, new GridBagConstraints(0, row, 2, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 0, 0, 0), 0, 0));
		return panel;
	}

	protected boolean canGoForward()
	{
		FileTypeComponent fileTypeComponent=(FileTypeComponent)formatField.getSelectedItem();
		if (fileTypeComponent!=null)
		{
			FileDescriptor fileDescriptor=fileTypeComponent.getFormat();
			return fileDescriptor!=null;
		}
		return false;
	}

	protected WizardPane getNextPane()
	{
		try
		{
			DataLoadWizard wizard=(DataLoadWizard)getDialog();
			DataLoadDescriptor descriptor=wizard.getDescriptor();
			FileTypeComponent fileTypeComponent=(FileTypeComponent)formatField.getSelectedItem();
			if (fileTypeComponent!=null)
			{
				FileDescriptor fileDescriptor=fileTypeComponent.getFormat();
				if (fileDescriptor!=null)
				{
					descriptor.setFileDescriptor(fileDescriptor);
					wizard.loadSampleData();
					return new SourceColumnsPage(wizard);
				}
			}
		}
		catch (Exception e)
		{
			GuiUtils.handleThrowable(getComponent(), e);
		}
		return null;
	}

	public void initData()
	{
		DataLoadWizard wizard=(DataLoadWizard)getDialog();
		DataLoadDescriptor descriptor=wizard.getDescriptor();
		FileDescriptor fileDescriptor=descriptor.getFileDescriptor();
		if (fileDescriptor instanceof CSVFileDescriptor)
		{
			csvComponent.initData((CSVFileDescriptor)fileDescriptor);
			formatField.setSelectedItem(csvComponent);
		}
		else if (fileDescriptor instanceof FixedWidthFileDescriptor)
		{
			fixedWidthComponent.initData((FixedWidthFileDescriptor)fileDescriptor);
			formatField.setSelectedItem(fixedWidthComponent);
		}
		super.initData();
	}

	public void saveData()
	{
		FileTypeComponent fileTypeComponent=(FileTypeComponent)formatField.getSelectedItem();
		if (fileTypeComponent!=null)
		{
			FileDescriptor fileDescriptor=fileTypeComponent.getFormat();
			if (fileDescriptor!=null)
			{
				DataLoadWizard wizard=(DataLoadWizard)getDialog();
				DataLoadDescriptor descriptor=wizard.getDescriptor();
				descriptor.setFileDescriptor(fileDescriptor);
			}
		}
	}

	protected String getHelpTopic()
	{
		FileTypeComponent fileTypeComponent=(FileTypeComponent)formatField.getSelectedItem();
		if (fileTypeComponent!=null) return fileTypeComponent.getHelpTopic();
		return "KiwiSQL.dataLoad.fileFormat.csv";
	}

	private static abstract class FileTypeComponent implements ChangeListener, ActionListener
	{
		protected JSpinner rowsToSkipField;
		protected JCheckBox titleRowField;

		protected FileTypeComponent()
		{
			rowsToSkipField=new JSpinner(new SpinnerNumberModel(new Integer(0), new Integer(0), null, new Integer(1)));
			rowsToSkipField.getModel().addChangeListener(this);
			rowsToSkipField.setMinimumSize(new Dimension(80, rowsToSkipField.getPreferredSize().height));
			rowsToSkipField.setPreferredSize(new Dimension(80, rowsToSkipField.getPreferredSize().height));
			titleRowField=new JCheckBox("Title Row");
			titleRowField.addActionListener(this);
		}

		public void stateChanged(ChangeEvent e)
		{
			updateTable();
		}

		public void actionPerformed(ActionEvent e)
		{
			updateTable();
		}

		protected abstract void updateTable();

		protected abstract void showComponent(JPanel fileTypePanel) throws IOException;

		protected abstract FileDescriptor getFormat();

		public void initData(FileDescriptor fileDescriptor)
		{
			rowsToSkipField.setValue(new Integer(fileDescriptor.getRowsToSkip()));
			titleRowField.setSelected(fileDescriptor.hasTitleRow());
		}

		public abstract String getHelpTopic();
	}

	private class CSVComponent extends FileTypeComponent implements ActionListener
	{
		private JComboBox delimiterField;
		private JComboBox qualifierField;
		private JTable table;

		public CSVComponent()
		{
			delimiterField=new JComboBox(new Object[]{CharacterResource.COMMA, CharacterResource.SEMICOLON, CharacterResource.TABULATOR,
													  CharacterResource.SPACE, CharacterResource.PIPE});
			delimiterField.addActionListener(this);
			qualifierField=new JComboBox(new Object[]{CharacterResource.QUOTES, CharacterResource.SINGLE_QUOTES, CharacterResource.NONE});
			qualifierField.addActionListener(this);
		}

		public String toString()
		{
			return "CSV";
		}

		protected void showComponent(JPanel panel) throws IOException
		{
			table=new JTable();
			table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			updateTable();

			int row=0;
			panel.add(new JLabel("Delimiter:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 5, 5), 0, 0));
			panel.add(delimiterField, new GridBagConstraints(1, row, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 5), 0, 0));
			panel.add(new JLabel("Qualifier:"), new GridBagConstraints(2, row, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 5, 5, 5), 0, 0));
			panel.add(qualifierField, new GridBagConstraints(3, row, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

			row++;
			panel.add(new JLabel("# of Rows to Skip:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 0, 5, 5), 0, 0));
			panel.add(rowsToSkipField, new GridBagConstraints(1, row, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 0, 5, 5), 0, 0));
			panel.add(titleRowField, new GridBagConstraints(3, row, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 0, 5, 0), 0, 0));

			row++;
			panel.add(new JScrollPane(table), new GridBagConstraints(0, row, 4, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 0, 0, 0), 0, 0));
		}

		protected void updateTable()
		{
			try
			{
				CharacterResource resource=(CharacterResource)delimiterField.getSelectedItem();
				char delimiter=resource!=null ? resource.getCharacter() : 0;
				resource=(CharacterResource)qualifierField.getSelectedItem();
				char qualifier=resource!=null ? resource.getCharacter() : 0;
				int rowsToSkip=0;
				if (rowsToSkipField.getValue()!=null) rowsToSkip=((Integer)rowsToSkipField.getValue()).intValue();
				boolean titleRow=titleRowField.isSelected();

				VirtualFile file=((DataLoadWizard)getDialog()).getFile();

				// todo: edited line
				CSVReader reader=new CSVReader(new BufferedReader(new InputStreamReader(file.getInputStream())), delimiter, qualifier);
				String[] row;
				String[] titles=null;
				Vector data=new Vector();
				int columns=0;
				while (data.size()<50 && (row=reader.readRow())!=null)
				{
					if (rowsToSkip>0)
					{
						rowsToSkip--;
						continue;
					}
					if (titleRow)
					{
						titles=row;
						titleRow=false;
						continue;
					}
					if (row.length>columns) columns=row.length;
					data.add(new Vector(Arrays.asList(row)));
				}
				reader.close();
				table.setModel(new CSVPreviewModel(titles, columns, data));
				TableUtils.sizeColumnsToFit(table, true, true);
			}
			catch (Exception e1)
			{
				e1.printStackTrace();
			}
		}

		protected FileDescriptor getFormat()
		{
			CharacterResource resource=(CharacterResource)delimiterField.getSelectedItem();
			char delimiter=resource!=null ? resource.getCharacter() : 0;
			resource=(CharacterResource)qualifierField.getSelectedItem();
			char qualifier=resource!=null ? resource.getCharacter() : 0;
			int rowsToSkip=0;
			if (rowsToSkipField.getValue()!=null) rowsToSkip=((Integer)rowsToSkipField.getValue()).intValue();
			boolean titleRow=titleRowField.isSelected();

			CSVFileDescriptor csvFileFormat=new CSVFileDescriptor();
			csvFileFormat.setDelimiter(delimiter);
			csvFileFormat.setQualifier(qualifier);
			csvFileFormat.setRowsToSkip(rowsToSkip);
			csvFileFormat.setTitleRow(titleRow);
			return csvFileFormat;
		}

		public String getHelpTopic()
		{
			return "KiwiSQL.dataLoad.fileFormat.csv";
		}

		public void initData(CSVFileDescriptor fileDescriptor)
		{
			super.initData(fileDescriptor);
			delimiterField.setSelectedItem(CharacterResource.valueOf(fileDescriptor.getDelimiter()));
			qualifierField.setSelectedItem(CharacterResource.valueOf(fileDescriptor.getQualifier()));
		}

		private class CSVPreviewModel extends AbstractTableModel
		{
			private String[] titles;
			private final int columns;
			private final Vector data;

			public CSVPreviewModel(String[] titles, int columns, Vector data)
			{
				this.titles=titles;
				this.columns=columns;
				this.data=data;
			}

			public String getColumnName(int column)
			{
				String title=null;
				if (titles!=null && column<titles.length) title=titles[column];
				if (StringUtils.isEmpty(title)) return "Column #"+(column+1);
				return title;
			}

			public int getColumnCount()
			{
				return columns;
			}

			public int getRowCount()
			{
				return data.size();
			}

			public Object getValueAt(int rowIndex, int columnIndex)
			{
				if (rowIndex<data.size())
				{
					Vector row=(Vector)data.get(rowIndex);
					if (columnIndex<row.size())
					{
						return row.get(columnIndex);
					}
				}
				return null;
			}
		}
	}

	private class FixedWidthComponent extends FileTypeComponent
	{
		private ColumnInitializer columnInitializer;

		public FixedWidthComponent()
		{
			columnInitializer=new ColumnInitializer(null);
		}

		public String toString()
		{
			return "Fixed Width";
		}

		protected void showComponent(JPanel panel)
		{
			updateTable();

			JScrollPane scrollPane=new JScrollPane(columnInitializer);

			int row=0;
			panel.add(new JLabel("# of Rows to Skip:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 0, 5, 5), 0, 0));
			panel.add(rowsToSkipField, new GridBagConstraints(1, row, 1, 1, 0.5, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 0, 5, 5), 0, 0));
			panel.add(titleRowField, new GridBagConstraints(3, row, 1, 1, 0.5, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 0, 5, 0), 0, 0));

			row++;
			panel.add(scrollPane, new GridBagConstraints(0, row, 4, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 0, 0, 0), 0, 0));
		}

		protected void updateTable()
		{
			int rowsToSkip=0;
			if (rowsToSkipField.getValue()!=null) rowsToSkip=((Integer)rowsToSkipField.getValue()).intValue();
			boolean titleRow=titleRowField.isSelected();
			if (titleRow) rowsToSkip++;

			List lines=new ArrayList();
			try
			{
				VirtualFile file=((DataLoadWizard)getDialog()).getFile();
				// todo: edited line
				BufferedReader reader=new BufferedReader(new InputStreamReader(file.getInputStream()));
				String line;
				while (lines.size()<50 && (line=reader.readLine())!=null)
				{
					if (rowsToSkip>0) rowsToSkip--;
					else lines.add(line);
				}
				reader.close();
			}
			catch (IOException e)
			{
				throw new RuntimeException(e);
			}

			String[] lineArray=(String[])lines.toArray(new String[0]);
			columnInitializer.setLines(lineArray);
		}

		protected FileDescriptor getFormat()
		{
			int rowsToSkip=0;
			if (rowsToSkipField.getValue()!=null) rowsToSkip=((Integer)rowsToSkipField.getValue()).intValue();
			boolean titleRow=titleRowField.isSelected();

			List columns=columnInitializer.getColumns();
			FixedWidthFileDescriptor fileDescriptor=new FixedWidthFileDescriptor();
			fileDescriptor.setColumns(columns);
			fileDescriptor.setRowsToSkip(rowsToSkip);
			fileDescriptor.setTitleRow(titleRow);
			return fileDescriptor;
		}

		public String getHelpTopic()
		{
			return "KiwiSQL.dataLoad.fileFormat.fixed";
		}

		public void initData(FixedWidthFileDescriptor fileDescriptor)
		{
			super.initData(fileDescriptor);
			columnInitializer.setColumns(fileDescriptor.getColumns());
		}
	}

	private class FormatListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			FileTypeComponent fileTypeComponent=(FileTypeComponent)formatField.getSelectedItem();
			fileTypePanel.removeAll();
			try
			{
				if (fileTypeComponent!=null) fileTypeComponent.showComponent(fileTypePanel);
			}
			catch (IOException e1)
			{
				e1.printStackTrace();
			}
			fileTypePanel.updateUI();
			validateActions();
		}
	}
}
