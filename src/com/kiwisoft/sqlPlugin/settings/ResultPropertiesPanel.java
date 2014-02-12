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
package com.kiwisoft.sqlPlugin.settings;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.Vector;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import com.intellij.ui.ColorChooser;

import com.kiwisoft.sqlPlugin.ResultSetTableModel;
import com.kiwisoft.sqlPlugin.config.SQLPluginAppConfig;
import com.kiwisoft.utils.ListenerSupport;
import com.kiwisoft.utils.SetMap;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.utils.format.ObjectFormat;
import com.kiwisoft.utils.gui.ColorBarDecorator;
import com.kiwisoft.utils.gui.ObjectStyle;
import com.kiwisoft.utils.gui.table.DefaultSortableTableRow;
import com.kiwisoft.utils.gui.table.RendererFactory;
import com.kiwisoft.utils.gui.table.SortableTable;
import com.kiwisoft.utils.gui.table.SortableTableModel;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.8 $, $Date: 2006/03/24 18:12:26 $
 */
public class ResultPropertiesPanel extends GlobalSettingsPanel
{
	private JCheckBox cbHighlightKeys;
	private JCheckBox cbResizeColumnsToHeader;
	private JCheckBox cbResizeColumnsToContent;
	private JCheckBox cbSaveTableConfiguration;
	private JTextField tfNullString;
	private JComboBox cbxDateFormats;
	private JComboBox cbxNumberFormats;
	private JComboBox cbxStringFormats;
	private JComboBox cbxBooleanFormats;
	private JCheckBox cbRowLimit;
	private JLabel lblMaxRows;
	private JTextField tfMaxRows;
	private JComboBox cbxPartialQuery;
	private JCheckBox cbSaveQueries;
	private JCheckBox cbProjectClassLoader;
	private JCheckBox cbLoadLOBs;
	private JCheckBox cbStopOnError;
	private JCheckBox cbUseAlternatingRowColors;
	private ColorButton btnAlternateBackground;
	private ColorButton btnAlternateForeground;
	private JLabel lblAlternateForeground;
	private JLabel lblAlternateBackground;
	private JCheckBox cbShowGrid;
	private JLabel lblPrimaryKeyColor;
	private ColorButton btnPrimaryKeyColor;
	private JLabel lblForeignKeyColor;
	private ColorButton btnForeignKeyColor;

	private SortableTable previewTable;
	private ListenerSupport listenerSupport=new ListenerSupport();

	public ResultPropertiesPanel()
	{
	}

	public String getTitle()
	{
		return "Result";
	}

	public String getHelpTopic()
	{
		return "KiwiSQL.resultPanel";
	}

	public void initializeData()
	{
		SQLPluginAppConfig configuration=SQLPluginAppConfig.getInstance();
		cbRowLimit.setSelected(configuration.isRowLimitEnabled());
		cbSaveQueries.setSelected(configuration.isSaveQueries());
		cbProjectClassLoader.setSelected(configuration.isIncludeProjectClasses());
		cbStopOnError.setSelected(configuration.isStopOnError());
		tfMaxRows.setEnabled(configuration.isRowLimitEnabled());
		lblMaxRows.setEnabled(configuration.isRowLimitEnabled());
		tfMaxRows.setText(Integer.toString(configuration.getRowLimit()));
		selectBoolean(cbxPartialQuery, configuration.getConfirmation(SQLPluginAppConfig.PARTIAL_EXECUTE));
		cbHighlightKeys.setSelected(configuration.isHighlightKeyColumns());
		btnPrimaryKeyColor.setColor(configuration.getPrimaryKeyColor());
		btnForeignKeyColor.setColor(configuration.getForeignKeyColor());
		cbLoadLOBs.setSelected(configuration.isLoadLargeObjects());
		cbResizeColumnsToContent.setSelected(configuration.isResizeColumnsToContent());
		cbResizeColumnsToHeader.setSelected(configuration.isResizeColumnsToHeader());
		cbSaveTableConfiguration.setSelected(configuration.isSaveResultTableConfiguration());
		tfNullString.setText(configuration.getNullString());
		String format=configuration.getDefaultFormat(Date.class);
		if (format!=null) cbxDateFormats.setSelectedItem(format);
		else cbxDateFormats.setSelectedIndex(0);
		format=configuration.getDefaultFormat(String.class);
		if (format!=null) cbxStringFormats.setSelectedItem(format);
		else cbxStringFormats.setSelectedIndex(0);
		format=configuration.getDefaultFormat(Number.class);
		if (format!=null) cbxNumberFormats.setSelectedItem(format);
		else cbxNumberFormats.setSelectedIndex(0);
		format=configuration.getDefaultFormat(Boolean.class);
		if (format!=null) cbxBooleanFormats.setSelectedItem(format);
		else cbxBooleanFormats.setSelectedIndex(0);
		cbUseAlternatingRowColors.setSelected(configuration.isUseAlternateRowColors());
		btnAlternateBackground.setColor(configuration.getAlternateRowBackground());
		btnAlternateForeground.setColor(configuration.getAlternateRowForeground());
		cbShowGrid.setSelected(configuration.isShowGrid());
	}

	private void selectBoolean(JComboBox comboBox, Boolean value)
	{
		DefaultComboBoxModel comboBoxModel=(DefaultComboBoxModel)comboBox.getModel();
		NamedBoolean item=new NamedBoolean("", value);
		int index=comboBoxModel.getIndexOf(item);
		if (index<0)
		{
			if (value==null) comboBox.setSelectedIndex(0);
			else selectBoolean(comboBox, null);
		}
		comboBox.setSelectedIndex(index);
	}

	public void initializeComponents()
	{
		lblMaxRows=new JLabel("Number of Rows:");
		tfMaxRows=new JTextField(5);
		tfMaxRows.setHorizontalAlignment(SwingConstants.TRAILING);
		cbSaveQueries=new JCheckBox("Save into workspace");
		cbProjectClassLoader=new JCheckBox("Include project classes for BLOB's");
		cbStopOnError=new JCheckBox("Stop on Error");

		cbxPartialQuery=new JComboBox(new Object[]{
			new NamedBoolean("Ask", null),
			new NamedBoolean("Execute part of query", Boolean.TRUE),
			new NamedBoolean("Execute complete query", Boolean.FALSE)
		});

		cbRowLimit=new JCheckBox("Restrict number of rows loaded");
		cbRowLimit.setHorizontalTextPosition(SwingConstants.TRAILING);
		cbRowLimit.setEnabled(true);

		cbLoadLOBs=new JCheckBox("Load LOB's");
		cbLoadLOBs.setHorizontalTextPosition(SwingConstants.TRAILING);

		cbSaveTableConfiguration=new JCheckBox("Save last table settings into workspace");
		cbSaveTableConfiguration.setHorizontalTextPosition(SwingConstants.TRAILING);

		cbxDateFormats=new JComboBox(getFormats(Date.class));
		cbxNumberFormats=new JComboBox(getFormats(Number.class));
		cbxBooleanFormats=new JComboBox(getFormats(Boolean.class));
		cbxStringFormats=new JComboBox(getFormats(String.class));

		cbResizeColumnsToHeader=new JCheckBox("...to fit table header");
		cbResizeColumnsToContent=new JCheckBox("...to fit table content");

		tfNullString=new JTextField(10);

		lblAlternateBackground=new JLabel("Background Color:");
		lblAlternateBackground.setEnabled(false);
		btnAlternateBackground=new ColorButton("Alternate Row Background");
		lblAlternateForeground=new JLabel("Foreground Color:");
		lblAlternateForeground.setEnabled(false);
		btnAlternateForeground=new ColorButton("Alternate Row Foreground");
		cbUseAlternatingRowColors=new JCheckBox("Use Alternating Row Colors");
		cbShowGrid=new JCheckBox("Show Grid Lines");
		lblPrimaryKeyColor=new JLabel("Primary Keys:");
		lblPrimaryKeyColor.setEnabled(false);
		btnPrimaryKeyColor=new ColorButton("Primary Key Color");
		lblForeignKeyColor=new JLabel("Foreign Keys:");
		lblForeignKeyColor.setEnabled(false);
		btnForeignKeyColor=new ColorButton("Foreign Key Color");
		cbHighlightKeys=new JCheckBox("Highlight key columns");
		cbHighlightKeys.setHorizontalTextPosition(SwingConstants.TRAILING);

		previewTable=new SortableTable(new PreviewTableModel());
		previewTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		previewTable.sizeColumnsToFit(true, true);
		JScrollPane previewPane=new JScrollPane(previewTable);
		Dimension preferredSize=previewTable.getPreferredSize();
		previewPane.setPreferredSize(new Dimension(preferredSize.width+20, preferredSize.height+20));

		JPanel pnlQueries=new JPanel();
		int row=0;
		pnlQueries.setLayout(new GridBagLayout());
		pnlQueries.setBorder(new TitledBorder(new EtchedBorder(), "Queries"));
		pnlQueries.add(cbRowLimit,
					   new GridBagConstraints(0, row++, 2, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(4, 4, 2, 4), 0, 0));
		pnlQueries.add(lblMaxRows,
					   new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(4, 4, 4, 4), 0, 0));
		pnlQueries.add(tfMaxRows,
					   new GridBagConstraints(1, row++, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(4, 4, 4, 4), 0, 0));
		pnlQueries.add(new JLabel("Partial queries:"),
					   new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(4, 4, 4, 4), 0, 0));
		pnlQueries.add(cbxPartialQuery,
					   new GridBagConstraints(1, row++, 2, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(4, 4, 4, 4), 0, 0));
		pnlQueries.add(cbSaveQueries,
					   new GridBagConstraints(0, row, 2, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 4, 4, 4), 0, 0));
		pnlQueries.add(cbStopOnError,
					   new GridBagConstraints(2, row++, 2, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 4, 4, 4), 0, 0));
		pnlQueries.add(cbLoadLOBs,
					   new GridBagConstraints(0, row, 2, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 4, 4, 4), 0, 0));
		pnlQueries.add(cbProjectClassLoader,
					   new GridBagConstraints(2, row++, 2, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 4, 4, 4), 0, 0));
		pnlQueries.add(Box.createGlue(),
					   new GridBagConstraints(2, row, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));

		JPanel pnlResult=new JPanel();
		pnlResult.setLayout(new GridBagLayout());
		pnlResult.setBorder(new TitledBorder(new EtchedBorder(), "Result Table"));
		row=0;
		pnlResult.add(cbSaveTableConfiguration,
					  new GridBagConstraints(0, row, 2, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(4, 4, 4, 4), 0, 0));
		pnlResult.add(cbUseAlternatingRowColors,
					  new GridBagConstraints(2, row, 2, 1, 0.5, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(4, 4, 4, 4), 0, 0));
		pnlResult.add(cbHighlightKeys,
					  new GridBagConstraints(4, row, 2, 1, 0.5, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(4, 4, 4, 4), 0, 0));
		row++;
		pnlResult.add(new JLabel("Null String:"),
					  new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(4, 4, 4, 4), 0, 0));
		pnlResult.add(tfNullString,
					  new GridBagConstraints(1, row, 1, 1, 0.5, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(4, 4, 4, 4), 0, 0));
		pnlResult.add(lblAlternateBackground,
					  new GridBagConstraints(2, row, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(4, 24, 4, 4), 0, 0));
		pnlResult.add(btnAlternateBackground,
					  new GridBagConstraints(3, row, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(4, 4, 4, 4), 0, 0));
		pnlResult.add(lblPrimaryKeyColor,
					  new GridBagConstraints(4, row, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(4, 24, 4, 4), 0, 0));
		pnlResult.add(btnPrimaryKeyColor,
					  new GridBagConstraints(5, row, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(4, 4, 4, 4), 0, 0));
		row++;
		pnlResult.add(new JLabel("Automatically resize columns..."),
					  new GridBagConstraints(0, row, 2, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(4, 4, 4, 4), 0, 0));
		pnlResult.add(lblAlternateForeground,
					  new GridBagConstraints(2, row, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(4, 24, 4, 4), 0, 0));
		pnlResult.add(btnAlternateForeground,
					  new GridBagConstraints(3, row, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(4, 4, 4, 4), 0, 0));
		pnlResult.add(lblForeignKeyColor,
					  new GridBagConstraints(4, row, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(4, 24, 4, 4), 0, 0));
		pnlResult.add(btnForeignKeyColor,
					  new GridBagConstraints(5, row, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(4, 4, 4, 4), 0, 0));
		row++;
		pnlResult.add(cbResizeColumnsToHeader,
					  new GridBagConstraints(0, row, 2, 1, 0.5, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 24, 4, 4), 0, 0));
		pnlResult.add(cbShowGrid,
					  new GridBagConstraints(2, row, 2, 1, 0.5, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(4, 4, 4, 4), 0, 0));
		row++;
		pnlResult.add(cbResizeColumnsToContent,
					  new GridBagConstraints(0, row, 4, 1, 0.5, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 24, 4, 4), 0, 0));
		row++;
		pnlResult.add(new JLabel("Preview:"),
					  new GridBagConstraints(0, row, 6, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(4, 4, 4, 4), 0, 0));
		row++;
		pnlResult.add(previewPane,
					  new GridBagConstraints(0, row, 6, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 4, 4, 4), 0, 0));

		JPanel pnlFormats=new JPanel();
		pnlFormats.setLayout(new GridBagLayout());
		pnlFormats.setBorder(new TitledBorder(new EtchedBorder(), "Default Formats"));
		row=0;
		pnlFormats.add(new JLabel("Strings:"),
					   new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(4, 4, 4, 4), 0, 0));
		pnlFormats.add(cbxStringFormats,
					   new GridBagConstraints(1, row, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(4, 4, 4, 4), 0, 0));
		row++;
		pnlFormats.add(new JLabel("Numbers:"),
					   new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(4, 4, 4, 4), 0, 0));
		pnlFormats.add(cbxNumberFormats,
					   new GridBagConstraints(1, row, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(4, 4, 4, 4), 0, 0));
		row++;
		pnlFormats.add(new JLabel("Booleans:"),
					   new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(4, 4, 4, 4), 0, 0));
		pnlFormats.add(cbxBooleanFormats,
					   new GridBagConstraints(1, row, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(4, 4, 4, 4), 0, 0));
		row++;
		pnlFormats.add(new JLabel("Dates:"),
					   new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(4, 4, 4, 4), 0, 0));
		pnlFormats.add(cbxDateFormats,
					   new GridBagConstraints(1, row, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(4, 4, 4, 4), 0, 0));

		setLayout(new GridBagLayout());
		row=0;
		add(pnlQueries,
			new GridBagConstraints(0, row, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(7, 7, 7, 7), 0, 0));
		add(pnlFormats,
			new GridBagConstraints(1, row, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(7, 0, 7, 7), 0, 0));
		row++;
		add(pnlResult,
			new GridBagConstraints(0, row, 2, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 7, 7, 7), 0, 0));
	}

	private static Vector getFormats(Class aClass)
	{
		SetMap variants=RendererFactory.getInstance().getVariants(aClass);
		Vector list=new Vector();
		for (Iterator it=variants.keySet().iterator(); it.hasNext();)
		{
			list.addAll(variants.get(it.next()));
		}
		list.remove(ObjectFormat.DEFAULT);
		Collections.sort(list);
		list.add(0, ObjectFormat.DEFAULT);
		return list;
	}

	public void installListeners()
	{
		listenerSupport.installComponentEnabler(cbRowLimit, new Object[]{lblMaxRows, tfMaxRows});
		listenerSupport.installComponentEnabler(cbHighlightKeys, new Object[]{btnPrimaryKeyColor, lblPrimaryKeyColor,
																			  btnForeignKeyColor, lblForeignKeyColor});
		listenerSupport.installComponentEnabler(cbUseAlternatingRowColors, new Object[]{btnAlternateBackground, lblAlternateBackground,
																						btnAlternateForeground, lblAlternateForeground});
		PreviewTableListener previewTableListener=new PreviewTableListener();
		listenerSupport.installItemListener(cbHighlightKeys, previewTableListener);
		listenerSupport.installItemListener(cbUseAlternatingRowColors, previewTableListener);
		listenerSupport.installItemListener(cbShowGrid, previewTableListener);
		listenerSupport.installPropertyChangeListener(btnAlternateBackground, "background", previewTableListener);
		listenerSupport.installPropertyChangeListener(btnAlternateForeground, "background", previewTableListener);
		listenerSupport.installPropertyChangeListener(btnForeignKeyColor, "background", previewTableListener);
		listenerSupport.installPropertyChangeListener(btnPrimaryKeyColor, "background", previewTableListener);
	}

	private void removeListeners()
	{
		listenerSupport.dispose();
	}

	public void removeNotify()
	{
		removeListeners();
		super.removeNotify();
	}

	public boolean canApply()
	{
		boolean rowLimit=cbRowLimit.isSelected();
		if (rowLimit)
		{
			try
			{
				Integer.parseInt(tfMaxRows.getText());
			}
			catch (NumberFormatException e)
			{
				JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				tfMaxRows.requestFocus();
				return false;
			}
		}
		return true;
	}

	public void apply()
	{
		SQLPluginAppConfig configuration=SQLPluginAppConfig.getInstance();

		int maxRows=1000;
		boolean rowLimit=cbRowLimit.isSelected();
		Object selectedItem=cbxPartialQuery.getSelectedItem();
		Boolean partialExecute=null;
		if (selectedItem instanceof NamedBoolean) partialExecute=((NamedBoolean)selectedItem).getValue();
		if (rowLimit)
		{
			maxRows=Integer.parseInt(tfMaxRows.getText());
		}

		String nullString=tfNullString.getText();
		if (StringUtils.isEmpty("nullString")) nullString=null;
		configuration.setNullString(nullString);
		configuration.setHighlightKeyColumns(cbHighlightKeys.isSelected());
		configuration.setPrimaryKeyColor(btnPrimaryKeyColor.getColor());
		configuration.setForeignKeyColor(btnForeignKeyColor.getColor());
		configuration.setDefaultFormat(Date.class, (String)cbxDateFormats.getSelectedItem());
		configuration.setDefaultFormat(Number.class, (String)cbxNumberFormats.getSelectedItem());
		configuration.setDefaultFormat(String.class, (String)cbxStringFormats.getSelectedItem());
		configuration.setDefaultFormat(Boolean.class, (String)cbxBooleanFormats.getSelectedItem());
		configuration.setResizeColumnsToContent(cbResizeColumnsToContent.isSelected());
		configuration.setResizeColumnsToHeader(cbResizeColumnsToHeader.isSelected());
		configuration.setSaveResultTableConfiguration(cbSaveTableConfiguration.isSelected());
		configuration.setSaveQueries(cbSaveQueries.isSelected());
		configuration.setIncludeProjectClasses(cbProjectClassLoader.isSelected());
		configuration.setLoadLargeObjects(cbLoadLOBs.isSelected());
		configuration.setConfirmation(SQLPluginAppConfig.PARTIAL_EXECUTE, partialExecute);
		configuration.setStopOnError(cbStopOnError.isSelected());
		configuration.setRowLimitEnabled(rowLimit);
		if (rowLimit) configuration.setRowLimit(maxRows);
		configuration.setUseAlternateRowColors(cbUseAlternatingRowColors.isSelected());
		configuration.setAlternateRowBackground(btnAlternateBackground.getColor());
		configuration.setAlternateRowForeground(btnAlternateForeground.getColor());
		configuration.setShowGrid(cbShowGrid.isSelected());
	}

	private static class NamedBoolean
	{
		private String label;
		private Boolean value;

		public NamedBoolean(String label, Boolean value)
		{
			this.label=label;
			this.value=value;
		}

		public String toString()
		{
			return label;
		}

		public Boolean getValue()
		{
			return value;
		}

		public boolean equals(Object o)
		{
			if (this==o) return true;
			if (!(o instanceof NamedBoolean)) return false;

			final NamedBoolean namedBoolean=(NamedBoolean)o;

			return !(value!=null ? !value.equals(namedBoolean.value) : namedBoolean.value!=null);
		}

		public int hashCode()
		{
			return value!=null ? value.hashCode() : 0;
		}
	}

	private static class ColorButton extends JButton
	{
		private String title;

		public ColorButton(String title)
		{
			this.title=title;
			setPreferredSize(new Dimension(30, 20));
			setColor(Color.WHITE);
			setBorder(new LineBorder(Color.BLACK));
			setEnabled(false);
			addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					Color color=ColorChooser.chooseColor(ColorButton.this, ColorButton.this.title, getColor());
					if (color!=null) setColor(color);
				}
			});
		}

		public Color getColor()
		{
			return getBackground();
		}

		public void setColor(Color color)
		{
			setBackground(color);
		}
	}

	private class PreviewTableModel extends SortableTableModel
	{
		public final String[] COLUMNS={"id", "parent_id", "description", "flag"};
		private ObjectStyle keyStyle;
		private ObjectStyle referenceStyle;

		public PreviewTableModel()
		{
			keyStyle=new ObjectStyle(new ColorBarDecorator(null, 10)
			{
				public Color getColor()
				{
					return btnPrimaryKeyColor.getColor();
				}
			});
			referenceStyle=new ObjectStyle(new ColorBarDecorator(null, 10)
			{
				public Color getColor()
				{
					return btnForeignKeyColor.getColor();
				}
			});

			addRow(1, 1000, "Default Line", false, null);
			addRow(2, 1000, "Default Line", true, null);
			addRow(3, 1000, "Modified Line", false, ResultSetTableModel.MODIFIED_STYLE);
			addRow(4, 1000, "Modified Line", true, ResultSetTableModel.MODIFIED_STYLE);
			addRow(5, 1000, "Deleted Line", false, ResultSetTableModel.DELETED_STYLE);
			addRow(6, 1000, "Deleted Line", true, ResultSetTableModel.DELETED_STYLE);
			addRow(7, 1000, "Modified Deleted Line", false, ResultSetTableModel.DELETED_MODIFIED_STYLE);
			addRow(8, 1000, "Modified Deleted Line", true, ResultSetTableModel.DELETED_MODIFIED_STYLE);
		}

		private void addRow(long id, long parentId, String description, boolean flag, ObjectStyle style)
		{
			Vector data=new Vector();
			data.add(new Long(id));
			data.add(new Long(parentId));
			data.add(description);
			data.add(Boolean.valueOf(flag));
			addRow(new PreviewTableRow(data, style));
		}

		public ObjectStyle getCellStyle(int row, int col)
		{
			ObjectStyle style1=getColumnStyle(col);
			ObjectStyle style2=super.getCellStyle(row, col);
			if (style1==null) return style2;
			if (style2==null) return style1;
			return style1.combine(style2);
		}

		private ObjectStyle getColumnStyle(int col)
		{
			if (cbHighlightKeys.isSelected())
			{
				switch (col)
				{
					case 0:
						return keyStyle;
					case 1:
						return referenceStyle;
				}
			}
			return null;
		}

		public int getColumnCount()
		{
			return COLUMNS.length;
		}

		public String getColumnName(int column)
		{
			return COLUMNS[column];
		}
	}

	private static class PreviewTableRow extends DefaultSortableTableRow
	{
		private ObjectStyle style;

		public PreviewTableRow(Vector data, ObjectStyle style)
		{
			super(data);
			this.style=style;
		}

		public ObjectStyle getCellStyle(int col)
		{
			return style;
		}
	}

	private class PreviewTableListener implements PropertyChangeListener, ItemListener
	{
		public void updatePreview()
		{
			previewTable.setShowGrid(cbShowGrid.isSelected());
			if (cbUseAlternatingRowColors.isSelected())
			{
				previewTable.putClientProperty(SortableTable.ALTERNATE_ROW_BACKGROUND, btnAlternateBackground.getColor());
				previewTable.putClientProperty(SortableTable.ALTERNATE_ROW_FOREGROUND, btnAlternateForeground.getColor());
			}
			else
			{
				previewTable.putClientProperty(SortableTable.ALTERNATE_ROW_BACKGROUND, null);
				previewTable.putClientProperty(SortableTable.ALTERNATE_ROW_FOREGROUND, null);
			}
			previewTable.repaint();
		}

		public void propertyChange(PropertyChangeEvent evt)
		{
			updatePreview();
		}

		public void itemStateChanged(ItemEvent e)
		{
			updatePreview();
		}
	}
}
