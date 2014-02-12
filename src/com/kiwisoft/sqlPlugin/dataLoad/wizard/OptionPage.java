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
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.NumberFormatter;

import org.apache.log4j.Level;

import com.kiwisoft.sqlPlugin.dataLoad.DataLoadDescriptor;
import com.kiwisoft.sqlPlugin.dataLoad.DataLoadJob;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.utils.gui.lookup.DialogLookupField;
import com.kiwisoft.utils.gui.lookup.FileLookup;
import com.kiwisoft.wizard.WizardDialog;
import com.kiwisoft.wizard.WizardJobPane;
import com.kiwisoft.wizard.WizardPane;

/**
 * @author Stefan Stiller
 * @version $Revision: $, $Date: $
 */
public class OptionPage extends WizardPane
{
	private DialogLookupField logFileField;
	private JCheckBox rejectField;
	private DialogLookupField rejectFileField;
	private JCheckBox logToFileField;
	private JCheckBox commitField;
	private JCheckBox batchModeField;
	private JComboBox logLevelField;
	private JFormattedTextField batchSizeField;
	private JFormattedTextField errorsField;

	public OptionPage(WizardDialog dialog)
	{
		super(dialog);
	}

	public String getTitle()
	{
		return "Processing Options";
	}

	public JComponent createComponent()
	{
		JPanel panel=new JPanel(new GridBagLayout());
		panel.add(createLoggingPanel(),
				  new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		panel.add(createErrorPanel(),
				  new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 0, 0, 0), 0, 0));
		panel.add(createConnectionPanel(),
				  new GridBagConstraints(0, 2, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 0, 0, 0), 0, 0));
		return panel;
	}

	private JPanel createConnectionPanel()
	{
		commitField=new JCheckBox();
		batchModeField=new JCheckBox();
		JLabel batchSizeLabel=new JLabel("Batch size:");
		batchSizeField=createIntegerField(new Integer(1));

		getListenerSupport().installComponentEnabler(batchModeField, new Object[]{batchSizeLabel, batchSizeField});
		getListenerSupport().installActionListener(batchModeField, getValidator());

		JPanel panel=new JPanel(new GridBagLayout());
		panel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), "Database Handling"));
		panel.add(commitField,
				  new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		panel.add(new JLabel("Commit changes"),
				  new GridBagConstraints(1, 0, 2, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		panel.add(batchModeField,
				  new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		panel.add(new JLabel("Use batch mode for better performance"),
				  new GridBagConstraints(1, 1, 2, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		panel.add(batchSizeLabel,
				  new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		panel.add(batchSizeField,
				  new GridBagConstraints(2, 2, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		return panel;
	}

	private JPanel createErrorPanel()
	{
		rejectField=new JCheckBox();
		rejectFileField=new DialogLookupField(new FileLookup(JFileChooser.FILES_ONLY, false));
		errorsField=createIntegerField(new Integer(1));

		getListenerSupport().installActionListener(rejectField, getValidator());
		getListenerSupport().installComponentEnabler(rejectField, new Object[]{rejectFileField});
		rejectFileField.installDocumentListener(getListenerSupport(), getValidator());

		JPanel panel=new JPanel(new GridBagLayout());
		panel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), "Error Handling"));
		panel.add(new JLabel("Number of errors to stop:"),
				  new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		panel.add(errorsField,
				  new GridBagConstraints(2, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		panel.add(rejectField,
				  new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		panel.add(new JLabel("Copy invalid records to file:"),
				  new GridBagConstraints(1, 1, 2, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		panel.add(rejectFileField,
				  new GridBagConstraints(1, 2, 2, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 5, 5), 0, 0));
		return panel;
	}

	private JFormattedTextField createIntegerField(Integer minValue)
	{
		NumberFormatter formatter=new NumberFormatter();
		formatter.setValueClass(Integer.class);
		formatter.setMinimum(minValue);
		JFormattedTextField field=new JFormattedTextField(formatter);
		field.setColumns(5);
		field.setValue(minValue);
		field.setHorizontalAlignment(JTextField.TRAILING);
		return field;
	}

	private JPanel createLoggingPanel()
	{
		logToFileField=new JCheckBox();
		logFileField=new DialogLookupField(new FileLookup(JFileChooser.FILES_ONLY, false));
		logLevelField=new JComboBox(new Object[]{Level.OFF, Level.FATAL, Level.ERROR, Level.WARN, Level.INFO, Level.DEBUG});

		getListenerSupport().installActionListener(logLevelField, getValidator());
		getListenerSupport().installComponentEnabler(logToFileField, new Object[]{logFileField});
		getListenerSupport().installActionListener(logToFileField, getValidator());
		logFileField.installDocumentListener(getListenerSupport(), getValidator());

		JPanel panel=new JPanel(new GridBagLayout());
		panel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), "Logging"));
		panel.add(new JLabel("Log Level:"),
				  new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		panel.add(logLevelField,
				  new GridBagConstraints(2, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		panel.add(logToFileField,
				  new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		panel.add(new JLabel("Log to file:"),
				  new GridBagConstraints(1, 1, 2, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		panel.add(logFileField,
				  new GridBagConstraints(1, 2, 2, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 5, 5), 0, 0));
		return panel;
	}

	public void initData()
	{
		DataLoadDescriptor descriptor=((DataLoadWizard)getDialog()).getDescriptor();
		logLevelField.setSelectedItem(descriptor.getLogLevel());
		logToFileField.setSelected(descriptor.isLogToFile());
		logFileField.setText(descriptor.getLogFile());
		rejectField.setSelected(descriptor.isLogRejects());
		rejectFileField.setText(descriptor.getRejectFile());
		batchModeField.setSelected(descriptor.isUseBatchMode());
		batchSizeField.setValue(new Integer(descriptor.getBatchSize()));
		errorsField.setValue(new Integer(descriptor.getMaxErrorCount()));
		commitField.setSelected(descriptor.isCommitChanges());
		super.initData();
	}

	public void saveData()
	{
		DataLoadDescriptor descriptor=((DataLoadWizard)getDialog()).getDescriptor();
		descriptor.setLogLevel((Level)logLevelField.getSelectedItem());
		descriptor.setLogToFile(logToFileField.isSelected());
		descriptor.setLogFile(logFileField.getText());
		descriptor.setLogRejects(rejectField.isSelected());
		descriptor.setRejectFile(rejectFileField.getText());
		descriptor.setUseBatchMode(batchModeField.isSelected());
		Integer batchSize=(Integer)batchSizeField.getValue();
		descriptor.setBatchSize(batchSize!=null ? batchSize.intValue() : 0);
		Integer errorCount=(Integer)errorsField.getValue();
		descriptor.setMaxErrorCount(errorCount!=null ? errorCount.intValue() : 0);
		descriptor.setCommitChanges(commitField.isSelected());
		super.saveData();
	}

	protected boolean canGoForward()
	{
		return (!logToFileField.isSelected() || !StringUtils.isEmpty(logFileField.getText()))
			   && (!rejectField.isSelected() || !StringUtils.isEmpty(rejectFileField.getText()))
			   && logLevelField.getSelectedItem()!=null;
	}

	protected Action createNextAction()
	{
		return new NextAction("Start Import");
	}

	protected WizardPane getNextPane()
	{
		DataLoadWizard wizard=(DataLoadWizard)getDialog();
		DataLoadDescriptor descriptor=wizard.getDescriptor();
		DataLoadJob job=new DataLoadJob(getDialog().getProject(), wizard.getDatabase(), wizard.getFile(), descriptor);
		return new WizardJobPane(getDialog(), job);
	}

	protected String getHelpTopic()
	{
		return "KiwiSQL.dataLoad.options";
	}
}
