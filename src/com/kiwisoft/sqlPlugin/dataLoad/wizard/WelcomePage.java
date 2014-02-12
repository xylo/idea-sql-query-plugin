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

import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Iterator;
import javax.swing.*;

import com.kiwisoft.sqlPlugin.dataLoad.DataLoadDescriptor;
import com.kiwisoft.sqlPlugin.dataLoad.DataLoadManager;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.utils.idea.PluginUtils;
import com.kiwisoft.wizard.WizardDialog;
import com.kiwisoft.wizard.WizardPane;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.3 $, $Date: 2002/02/22 10:00:24 $
 */
public class WelcomePage extends WizardPane
{
	private JComboBox previousFormats;
	private JRadioButton previousButton;
	private JRadioButton newButton;
	private JTextField nameField;

	public WelcomePage(WizardDialog wizardDialog)
	{
		super(wizardDialog);
	}

	public String getTitle()
	{
		return "Data Load - Select Format";
	}

	protected String getHelpTopic()
	{
		return "KiwiSQL.dataLoad.formatSelection";
	}

	public JComponent createComponent()
	{
		previousButton=new JRadioButton();
		JLabel previousLabel=new JLabel("Use previous data load format");
		previousLabel.setFont(previousLabel.getFont().deriveFont(Font.BOLD));
		previousFormats=new JComboBox(DataLoadManager.getInstance().getDescriptors().toArray());
		previousFormats.setEditable(false);
		ConfigureAction configureAction=new ConfigureAction();
		JButton configureButton=new JButton(configureAction);
		configureButton.setMargin(new Insets(0, 0, 0, 0));
		getListenerSupport().installActionListener(previousFormats, getValidator());
		getListenerSupport().installComponentEnabler(previousButton, new Object[]{previousFormats, configureAction});
		getListenerSupport().installActionListener(previousButton, getValidator());

		newButton=new JRadioButton();
		JLabel newLabel=new JLabel("Create new data load format");
		newLabel.setFont(newLabel.getFont().deriveFont(Font.BOLD));
		getListenerSupport().installActionListener(newButton, getValidator());
		nameField=new JTextField();
		getListenerSupport().installDocumentListener(nameField, getValidator());
		getListenerSupport().installComponentEnabler(newButton, new Object[]{nameField});

		ButtonGroup buttonGroup=new ButtonGroup();
		buttonGroup.add(previousButton);
		buttonGroup.add(newButton);

		JPanel panel=new JPanel(new GridBagLayout());
		panel.add(previousButton,
				  new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		panel.add(previousLabel,
				  new GridBagConstraints(1, 0, 2, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		panel.add(previousFormats,
				  new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 0), 0, 0));
		panel.add(configureButton,
				  new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5, 0, 5, 5), 0, 0));
		panel.add(newButton,
				  new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(50, 5, 5, 5), 0, 0));
		panel.add(newLabel,
				  new GridBagConstraints(1, 2, 2, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(50, 5, 5, 5), 0, 0));
		panel.add(new JLabel("Name:"),
				  new GridBagConstraints(1, 3, 2, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		panel.add(nameField,
				  new GridBagConstraints(1, 4, 2, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 5, 5), 0, 0));
		return panel;
	}

	public void initData()
	{
		DataLoadWizard wizard=(DataLoadWizard)getDialog();
		DataLoadDescriptor descriptor=wizard.getDescriptor();
		if (descriptor==null)
		{
			String fileName=wizard.getFile().getName();
			nameField.setText(fileName);
			for (Iterator it=DataLoadManager.getInstance().getDescriptors().iterator(); it.hasNext();)
			{
				DataLoadDescriptor oldDescriptor=(DataLoadDescriptor)it.next();
				if (fileName.equals(oldDescriptor.getName()))
				{
					descriptor=oldDescriptor;
					break;
				}
			}
			if (descriptor!=null)
			{
				previousButton.setSelected(true);
				previousFormats.setSelectedItem(descriptor);
			}
			else
			{
				newButton.setSelected(true);
			}
		}
		super.initData();
	}

	protected boolean canGoForward()
	{
		return (newButton.isSelected() && !StringUtils.isEmpty(nameField.getText()))
			   || (previousButton.isSelected() && previousFormats.getSelectedItem() instanceof DataLoadDescriptor);
	}

	protected WizardPane getNextPane()
	{
		DataLoadDescriptor descriptor;
		if (newButton.isSelected())
		{
			descriptor=new DataLoadDescriptor(nameField.getText());
			DataLoadManager.getInstance().addDescriptor(descriptor);
		}
		else descriptor=(DataLoadDescriptor)previousFormats.getSelectedItem();
		DataLoadWizard wizard=(DataLoadWizard)getDialog();
		wizard.setDescriptor(descriptor);
		return new DataSourcePage(wizard);
	}

	private class ConfigureAction extends AbstractAction
	{
		public ConfigureAction()
		{
			super("Configure");
			setEnabled(false);
		}

		public void actionPerformed(ActionEvent e)
		{
			PluginUtils.showDialog(new DataLoadDescriptorsDialog(getDialog().getProject()), true, true);
			previousFormats.setModel(new DefaultComboBoxModel(DataLoadManager.getInstance().getDescriptors().toArray()));
			validateActions();
		}
	}
}
