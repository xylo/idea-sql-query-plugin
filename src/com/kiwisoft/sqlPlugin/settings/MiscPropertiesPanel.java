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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.Enumeration;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;
import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.vfs.VirtualFile;

import com.kiwisoft.sqlPlugin.JdbcLibrary;
import com.kiwisoft.sqlPlugin.config.SQLPluginAppConfig;
import com.kiwisoft.sqlPlugin.config.SQLPluginConstants;
import com.kiwisoft.utils.Encoder;
import com.kiwisoft.sqlPlugin.JdbcLibraryRenderer;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.5 $, $Date: 2006/03/24 18:12:25 $
 */
public class MiscPropertiesPanel extends GlobalSettingsPanel
{
	private Project project;

	private JRadioButton rbSavePasswordsNot;
	private JRadioButton rbSavePasswordsUnencoded;
	private JRadioButton rbSavePasswordsEncoded;
	private JCheckBox cbConfirmCommit;
	private JCheckBox cbConfirmRollback;
	private JCheckBox cbConfirmDisconnect;
	private JCheckBox cbConnections;
	private JList lstDrivers;
	private DefaultListModel lmDrivers;
	private RemoveDriverAction removeDriverAction;

	public MiscPropertiesPanel(Project project)
	{
		this.project=project;
	}

	public String getTitle()
	{
		return "Miscellaneous";
	}

	public String getHelpTopic()
	{
		return "KiwiSQL.miscPanel";
	}

	public void initializeData()
	{
		SQLPluginAppConfig appConfig=SQLPluginAppConfig.getInstance();
		cbConfirmCommit.setSelected(!Boolean.TRUE.equals(appConfig.getConfirmation(SQLPluginConstants.CONFIRM_COMMIT)));
		cbConfirmRollback.setSelected(!Boolean.TRUE.equals(appConfig.getConfirmation(SQLPluginConstants.CONFIRM_ROLLBACK)));
		cbConfirmDisconnect.setSelected(!Boolean.TRUE.equals(appConfig.getConfirmation(SQLPluginConstants.CONFIRM_DISCONNECT)));
		if (appConfig.isSavePasswords())
		{
			if (appConfig.isEncodePasswords()) rbSavePasswordsEncoded.setSelected(true);
			else rbSavePasswordsUnencoded.setSelected(true);
		}
		else rbSavePasswordsNot.setSelected(true);
		rbSavePasswordsEncoded.setEnabled(Encoder.isAvailable());

		for (Iterator it=appConfig.getJdbcLibraries().iterator(); it.hasNext();)
		{
			lmDrivers.addElement(it.next());
		}
		if (lmDrivers.getSize()>0) lstDrivers.setSelectedIndex(0);
		cbConnections.setSelected(appConfig.isKeepConnectionOpen());
	}

	public void initializeComponents()
	{
		ButtonGroup buttonGroup=new ButtonGroup();
		buttonGroup.add(rbSavePasswordsNot=new JRadioButton("Don't save"));
		buttonGroup.add(rbSavePasswordsEncoded=new JRadioButton("Save encrypted"));
		buttonGroup.add(rbSavePasswordsUnencoded=new JRadioButton("Save as free text"));

		cbConfirmCommit=new JCheckBox("Confirm 'Commit'");
		cbConfirmRollback=new JCheckBox("Confirm 'Rollback'");
		cbConfirmDisconnect=new JCheckBox("Confirm 'Close Connections'");

		lmDrivers=new DefaultListModel();
		lstDrivers=new JList(lmDrivers);
		lstDrivers.setCellRenderer(new JdbcLibraryRenderer());
		cbConnections=new JCheckBox("Don't close with tool window");

		JPanel pnlSecurity=new JPanel();
		int row=0;
		pnlSecurity.setLayout(new GridBagLayout());
		pnlSecurity.setBorder(new TitledBorder(new EtchedBorder(), "Security"));
		pnlSecurity.add(new JLabel("Save passwords in workspace:"),
						new GridBagConstraints(0, row++, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(4, 4, 4, 4), 0, 0));
		pnlSecurity.add(rbSavePasswordsNot,
						new GridBagConstraints(0, row++, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 24, 4, 4), 0, 0));
		pnlSecurity.add(rbSavePasswordsEncoded,
						new GridBagConstraints(0, row++, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 24, 4, 4), 0, 0));
		pnlSecurity.add(rbSavePasswordsUnencoded,
						new GridBagConstraints(0, row++, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 24, 4, 4), 0, 0));
		pnlSecurity.add(Box.createGlue(),
						new GridBagConstraints(1, row, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));

		JPanel pnlDrivers=new JPanel();
		row=0;
		pnlDrivers.setLayout(new GridBagLayout());
		pnlDrivers.setBorder(new TitledBorder(new EtchedBorder(), "Drivers"));
		pnlDrivers.add(new JLabel("JDBC Drivers:"),
					   new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(4, 4, 4, 4), 0, 0));
		row++;
		pnlDrivers.add(new JScrollPane(lstDrivers),
					   new GridBagConstraints(0, row, 1, 2, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 4, 4, 4), 0, 0));
		pnlDrivers.add(new JButton(new AddDriverAction()),
					   new GridBagConstraints(1, row, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 4, 4, 4), 0, 0));
		row++;
		removeDriverAction=new RemoveDriverAction();
		pnlDrivers.add(new JButton(removeDriverAction),
					   new GridBagConstraints(1, row, 1, 1, 0.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 4, 4, 4), 0, 0));

		JPanel pnlConnections=new JPanel();
		row=0;
		pnlConnections.setLayout(new GridBagLayout());
		pnlConnections.setBorder(new TitledBorder(new EtchedBorder(), "Connections"));
		pnlConnections.add(cbConnections,
						   new GridBagConstraints(0, row, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(4, 4, 4, 4), 0, 0));

		JPanel pnlConfirmations=new JPanel();
		row=0;
		pnlConfirmations.setLayout(new GridBagLayout());
		pnlConfirmations.setBorder(new TitledBorder(new EtchedBorder(), "Confirmations"));
		pnlConfirmations.add(cbConfirmCommit,
							 new GridBagConstraints(0, row, 1, 1, 0.5, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(4, 4, 4, 4), 0, 0));
		pnlConfirmations.add(cbConfirmDisconnect,
							 new GridBagConstraints(1, row++, 1, 1, 0.5, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(4, 4, 4, 4), 0, 0));
		pnlConfirmations.add(cbConfirmRollback,
							 new GridBagConstraints(0, row, 1, 1, 0.5, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 4, 4, 4), 0, 0));

		setLayout(new GridBagLayout());
		row=0;
		add(pnlDrivers,
			new GridBagConstraints(0, row++, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(7, 7, 7, 7), 0, 0));
		add(pnlConnections,
			new GridBagConstraints(0, row++, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 7, 7, 7), 0, 0));
		add(pnlSecurity,
			new GridBagConstraints(0, row++, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 7, 7, 7), 0, 0));
		add(pnlConfirmations,
			new GridBagConstraints(0, row, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 7, 7, 7), 0, 0));
	}

	public void installListeners()
	{
		lstDrivers.addListSelectionListener(new ListSelectionListener()
		{
			public void valueChanged(ListSelectionEvent e)
			{
				removeDriverAction.update();
			}
		});
	}

	private void removeListeners()
	{
	}

	public void removeNotify()
	{
		removeListeners();
		super.removeNotify();
	}

	public boolean canApply()
	{
		return true;
	}

	public void apply()
	{
		SQLPluginAppConfig appConfig=SQLPluginAppConfig.getInstance();
		appConfig.setSavePasswords(!rbSavePasswordsNot.isSelected());
		appConfig.setEncodePasswords(rbSavePasswordsEncoded.isSelected());
		appConfig.setConfirmation(SQLPluginConstants.CONFIRM_COMMIT, Boolean.valueOf(!cbConfirmCommit.isSelected()));
		appConfig.setConfirmation(SQLPluginConstants.CONFIRM_ROLLBACK, Boolean.valueOf(!cbConfirmRollback.isSelected()));
		appConfig.setConfirmation(SQLPluginConstants.CONFIRM_DISCONNECT, Boolean.valueOf(!cbConfirmDisconnect.isSelected()));

		Enumeration elements=lmDrivers.elements();
		Set urls=new HashSet();
		while (elements.hasMoreElements())
		{
			urls.add(elements.nextElement());
		}

		appConfig.setJdbcLibraries(urls);
		appConfig.setKeepConnectionOpen(cbConnections.isSelected());
	}

	private class AddDriverAction extends AbstractAction
	{
		public AddDriverAction()
		{
			super("Add Jar/Directory...");
		}

		public void actionPerformed(ActionEvent e)
		{
			// todo: edited line
			VirtualFile[] files=FileChooser.chooseFiles(new FileChooserDescriptor(false, true, true, true, false, true), project, null);
			for (int i=0; i<files.length; i++)
			{
				VirtualFile file=files[i];
				lmDrivers.addElement(new JdbcLibrary(file));
			}
		}
	}

	private class RemoveDriverAction extends AbstractAction
	{
		public RemoveDriverAction()
		{
			super("Remove");
		}

		public void actionPerformed(ActionEvent e)
		{
			Object[] selectedValues=lstDrivers.getSelectedValues();
			for (int i=0; i<selectedValues.length; i++)
			{
				Object selectedValue=selectedValues[i];
				lmDrivers.removeElement(selectedValue);
			}
		}

		public void update()
		{
			setEnabled(lstDrivers.getSelectedValue()!=null);
		}
	}
}
