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
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellEditor;

import com.intellij.openapi.actionSystem.*;

import com.kiwisoft.db.Database;
import com.kiwisoft.db.DatabaseManager;
import com.kiwisoft.db.driver.DatabaseDriver;
import com.kiwisoft.db.driver.DatabaseDriverManager;
import com.kiwisoft.sqlPlugin.DatabaseDriverRenderer;
import com.kiwisoft.sqlPlugin.DefaultTableConfiguration;
import com.kiwisoft.sqlPlugin.Icons;
import com.kiwisoft.utils.DocumentAdapter;
import com.kiwisoft.utils.NotifyObject;
import com.kiwisoft.utils.gui.IconManager;
import com.kiwisoft.utils.gui.table.SortableTable;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.8 $, $Date: 2006/03/24 18:12:24 $
 */
public class ConnectionPropertiesPanel extends GlobalSettingsPanel
{
	private JList lstDatabases;
	private DatabaseListModel lmDatabases;
	private JComboBox cbxDriver;
	private SortableTable tblDriverProperties;
	private DriverPropertiesTableModel tmDriverProperties;
	private SortableTable tblCustomProperties;
	private CustomPropertiesTableModel tmCustomProperties;
	private JTextField tfName;
	private JTextField tfUrl;
	private JTextField tfClass;
	private JComboBox cbxGroup;

	private DatabaseSelectionListener databaseListener;
	private NameChangeListener nameChangeListener;
	private DriverSelectionListener driverSelectionListener;

	private DatabaseModel selectedDatabase;

	public ConnectionPropertiesPanel()
	{
	}

	public String getTitle()
	{
		return "Connections";
	}

	public String getHelpTopic()
	{
		return "KiwiSQL.connectionsPanel";
	}

	public void initializeComponents()
	{
		Vector drivers=new Vector(DatabaseDriverManager.getInstance().getAllDrivers());
		Collections.sort(drivers, new DriverComparator());

		tfName=new JTextField(20);
		cbxDriver=new JComboBox(drivers);
		cbxDriver.setRenderer(new DatabaseDriverRenderer());
		lmDatabases=new DatabaseListModel();
		lstDatabases=new JList(lmDatabases);
		tmDriverProperties=new DriverPropertiesTableModel(null, null);
		tblDriverProperties=new SortableTable(tmDriverProperties);
		tblDriverProperties.putClientProperty(SortableTable.AUTO_RESIZE_ROWS, Boolean.TRUE);
		tblDriverProperties.initializeColumns(new DefaultTableConfiguration("connectionProperties"));
		tblDriverProperties.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		tfUrl=new JTextField();
		tfUrl.setEditable(false);
		tfClass=new JTextField();
		tfClass.setEditable(false);
		cbxGroup=new JComboBox(DatabaseManager.getApplicationInstance().getGroups().toArray());
		cbxGroup.setPreferredSize(new Dimension(100, tfName.getPreferredSize().height));
		cbxGroup.setEditable(true);

		JScrollPane pnlDatabases=new JScrollPane(lstDatabases);
		pnlDatabases.setPreferredSize(new Dimension(100, 150));
		pnlDatabases.setMinimumSize(new Dimension(100, 100));

		JPanel pnlRight=new JPanel(new BorderLayout());
		pnlRight.add(createToolBar(), BorderLayout.NORTH);
		pnlRight.add(pnlDatabases, BorderLayout.CENTER);

		JTabbedPane propertiesTabs=new JTabbedPane();
		propertiesTabs.setTabPlacement(JTabbedPane.BOTTOM);
		propertiesTabs.setPreferredSize(new Dimension(300, 200));
		propertiesTabs.addTab("Standard", new JScrollPane(tblDriverProperties));
		propertiesTabs.addTab("Additional", createCustomPropertiesPanel());

		JPanel pnlProperties=new JPanel(new GridBagLayout());
		pnlProperties.setBorder(new EtchedBorder());
		pnlProperties.add(new JLabel("Driver:"),
						  new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(4, 4, 2, 4), 0, 0));
		pnlProperties.add(cbxDriver,
						  new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(2, 4, 2, 4), 0, 0));
		pnlProperties.add(new JLabel("Properties:"),
						  new GridBagConstraints(0, 2, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(7, 4, 2, 4), 0, 0));
		pnlProperties.add(propertiesTabs,
						  new GridBagConstraints(0, 3, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(2, 4, 2, 4), 0, 0));
		pnlProperties.add(new JLabel("Class:"),
						  new GridBagConstraints(0, 4, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(7, 4, 2, 4), 0, 0));
		pnlProperties.add(tfClass,
						  new GridBagConstraints(0, 5, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(2, 4, 2, 4), 0, 0));
		pnlProperties.add(new JLabel("URL:"),
						  new GridBagConstraints(0, 6, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(7, 4, 2, 4), 0, 0));
		pnlProperties.add(tfUrl,
						  new GridBagConstraints(0, 7, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(2, 4, 2, 4), 0, 0));

		JPanel pnlLeft=new JPanel(new GridBagLayout());
		int i=0;
		pnlLeft.add(new JLabel("Name:"),
					new GridBagConstraints(0, i, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
		pnlLeft.add(tfName,
					new GridBagConstraints(1, i, 1, 1, 0.5, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
		pnlLeft.add(new JLabel("Group:"),
					new GridBagConstraints(2, i, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
		pnlLeft.add(cbxGroup,
					new GridBagConstraints(3, i++, 1, 1, 0.5, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
		pnlLeft.add(pnlProperties,
					new GridBagConstraints(0, i, 4, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 0, 0));

		setLayout(new GridBagLayout());
		add(pnlRight,
			new GridBagConstraints(0, 0, 1, 1, 0.3, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(3, 3, 3, 3), 0, 0));
		add(pnlLeft,
			new GridBagConstraints(1, 0, 1, 1, 0.6, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(3, 3, 3, 3), 0, 0));
	}

	private JComponent createToolBar()
	{
		DefaultActionGroup actionGroup=new DefaultActionGroup();
		actionGroup.add(new AddDatabaseAction());
		actionGroup.add(new RemoveDatabaseAction());
		actionGroup.add(new CopyDatabaseAction());
		actionGroup.add(new MoveUpAction());
		actionGroup.add(new MoveDownAction());
		ActionToolbar toolbar=ActionManager.getInstance().createActionToolbar("ConnectionsPanel", actionGroup, true);
		return toolbar.getComponent();
	}

	private JPanel createCustomPropertiesPanel()
	{
		tmCustomProperties=new CustomPropertiesTableModel(null);
		tblCustomProperties=new SortableTable(tmCustomProperties);
		tblCustomProperties.putClientProperty(SortableTable.AUTO_RESIZE_ROWS, Boolean.TRUE);
		tblCustomProperties.initializeColumns(new DefaultTableConfiguration("customConnectionProperties"));
		tblCustomProperties.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);

		DefaultActionGroup actionGroup=new DefaultActionGroup();
		actionGroup.add(new AddCustomPropertiesAction());
		actionGroup.add(new RemoveCustomPropertiesAction());
		ActionToolbar toolbar=ActionManager.getInstance().createActionToolbar("AdditionalPropertiesPanel", actionGroup, true);

		JPanel panel=new JPanel(new BorderLayout());
		panel.add(toolbar.getComponent(), BorderLayout.PAGE_START);
		panel.add(new JScrollPane(tblCustomProperties), BorderLayout.CENTER);
		return panel;
	}

	public void installListeners()
	{
		databaseListener=new DatabaseSelectionListener();
		lstDatabases.addListSelectionListener(databaseListener);
		nameChangeListener=new NameChangeListener();
		tfName.getDocument().addDocumentListener(nameChangeListener);
		driverSelectionListener=new DriverSelectionListener();
		cbxDriver.addActionListener(driverSelectionListener);
	}

	private void removeListeners()
	{
		if (selectedDatabase!=null) selectedDatabase.deleteObservers();
		lstDatabases.removeListSelectionListener(databaseListener);
		tfName.getDocument().removeDocumentListener(nameChangeListener);
		cbxDriver.removeActionListener(driverSelectionListener);
	}

	public boolean canApply()
	{
		return true;
	}

	public void apply()
	{
		applyDatabase();
		List databases=new LinkedList();
		Enumeration elements=lmDatabases.elements();
		while (elements.hasMoreElements())
		{
			DatabaseModel databaseModel=(DatabaseModel)elements.nextElement();
			Database database=databaseModel.apply();
			databases.add(database);
		}
		DatabaseManager databaseManager=DatabaseManager.getApplicationInstance();
		databaseManager.setDatabases(databases);
		DatabaseModel currentDatabase=(DatabaseModel)lstDatabases.getSelectedValue();
		if (currentDatabase!=null && currentDatabase.getDatabase()!=null)
		{
			databaseManager.setCurrentDatabase(currentDatabase.getDatabase());
		}
	}

	public void initializeData()
	{
		setSelectedDatabase(null);
		DatabaseManager databaseManager=DatabaseManager.getApplicationInstance();
		Database currentDatabase=databaseManager.getCurrentDatabase();
		DatabaseModel currentModel=null;
		Iterator it=databaseManager.getDatabases().iterator();
		while (it.hasNext())
		{
			Database database=(Database)it.next();
			DatabaseModel databaseModel=new DatabaseModel(database);
			lmDatabases.addElement(databaseModel);
			if (database==currentDatabase) currentModel=databaseModel;
		}
		if (currentModel!=null)
			lstDatabases.setSelectedValue(currentModel, true);
		else if (lmDatabases.size()>0) lstDatabases.setSelectedIndex(0);
	}

	private void setSelectedDatabase(DatabaseModel database)
	{
		applyDatabase();
		if (selectedDatabase!=null) selectedDatabase.deleteObservers();
		selectedDatabase=database;
		if (selectedDatabase!=null) selectedDatabase.addObserver(new URLChangeListener());
		tmDriverProperties.setDatabase(selectedDatabase);
		tmCustomProperties.setDatabase(selectedDatabase);
		boolean databaseSelected=database!=null;
		tfName.setEnabled(databaseSelected);
		cbxGroup.setEnabled(databaseSelected);
		tblDriverProperties.setEnabled(databaseSelected);
		cbxDriver.setEnabled(databaseSelected);
		if (database!=null)
		{
			tfName.setText(database.getName());
			tfUrl.setText(database.buildURL());
			cbxGroup.setSelectedItem(database.getGroup());
			DatabaseDriver databaseDriver=DatabaseDriverManager.getInstance().getDriver(database.getDriver());
			if (databaseDriver!=null) cbxDriver.setSelectedItem(databaseDriver);
		}
		else
		{
			tfName.setText("");
			cbxGroup.setSelectedItem("");
			cbxDriver.setSelectedIndex(0);
		}
	}

	private void applyDatabase()
	{
		TableCellEditor cellEditor=tblDriverProperties.getCellEditor();
		if (cellEditor!=null)
		{
			if (!cellEditor.stopCellEditing()) cellEditor.cancelCellEditing();
		}
		if (selectedDatabase!=null)
		{
			selectedDatabase.setName(tfName.getText());
			selectedDatabase.setGroup((String)cbxGroup.getSelectedItem());
			DatabaseDriver databaseDriver=(DatabaseDriver)cbxDriver.getSelectedItem();
			if (databaseDriver!=null)
				selectedDatabase.setDriver(databaseDriver.getId());
			else
				selectedDatabase.setDriver(null);
		}
	}

	public void removeNotify()
	{
		removeListeners();
		super.removeNotify();
	}

	private class AddDatabaseAction extends AnAction
	{
		public AddDatabaseAction()
		{
			super("Create Connection");
			getTemplatePresentation().setIcon(IconManager.getIcon(Icons.ADD));
			getTemplatePresentation().setDescription("Create new database entry.");
		}

		public void actionPerformed(AnActionEvent e)
		{
			DatabaseModel newDatabase=new DatabaseModel();
			DatabaseDriver driver=(DatabaseDriver)cbxDriver.getSelectedItem();
			if (driver!=null) newDatabase.setDriver(driver.getId());
			lmDatabases.addElement(newDatabase);
			lstDatabases.setSelectedValue(newDatabase, true);
		}
	}

	private class RemoveDatabaseAction extends AnAction
	{
		public RemoveDatabaseAction()
		{
			super("Delete Connection");
			getTemplatePresentation().setIcon(IconManager.getIcon(Icons.REMOVE));
			getTemplatePresentation().setDescription("Delete selected database entry.");
		}

		public void update(AnActionEvent event)
		{
			super.update(event);
			event.getPresentation().setEnabled(selectedDatabase!=null);
		}

		public void actionPerformed(AnActionEvent e)
		{
			if (selectedDatabase!=null) lmDatabases.removeElement(selectedDatabase);
		}
	}

	private class MoveDownAction extends AnAction
	{
		public MoveDownAction()
		{
			super("Move Down");
			getTemplatePresentation().setIcon(IconManager.getIcon(Icons.MOVE_DOWN));
			getTemplatePresentation().setDescription("Move selected database down.");
		}

		public void update(AnActionEvent event)
		{
			super.update(event);
			event.getPresentation().setEnabled(selectedDatabase!=null);
		}

		public void actionPerformed(AnActionEvent e)
		{
			applyDatabase();
			if (selectedDatabase!=null)
			{
				DatabaseModel database=selectedDatabase;
				int index=lmDatabases.indexOf(database);
				if (index<lmDatabases.size()-1)
				{
					lmDatabases.removeElement(database);
					lmDatabases.insertElementAt(database, index+1);
					lstDatabases.setSelectedValue(database, true);
				}
			}
		}
	}

	private class MoveUpAction extends AnAction
	{
		public MoveUpAction()
		{
			super("Move Up");
			getTemplatePresentation().setIcon(IconManager.getIcon(Icons.MOVE_UP));
			getTemplatePresentation().setDescription("Move selected database up.");
		}

		public void update(AnActionEvent event)
		{
			super.update(event);
			event.getPresentation().setEnabled(selectedDatabase!=null);
		}

		public void actionPerformed(AnActionEvent e)
		{
			applyDatabase();
			if (selectedDatabase!=null)
			{
				DatabaseModel database=selectedDatabase;
				int index=lmDatabases.indexOf(database);
				if (index>0)
				{
					lmDatabases.removeElement(database);
					lmDatabases.insertElementAt(database, index-1);
					lstDatabases.setSelectedValue(database, true);
				}
			}
		}
	}

	private class CopyDatabaseAction extends AnAction
	{
		public CopyDatabaseAction()
		{
			super("Copy Connection");
			getTemplatePresentation().setIcon(IconManager.getIcon(Icons.COPY));
			getTemplatePresentation().setDescription("Create a new database based on the selected one.");
		}

		public void update(AnActionEvent event)
		{
			super.update(event);
			event.getPresentation().setEnabled(selectedDatabase!=null);
		}

		public void actionPerformed(AnActionEvent e)
		{
			applyDatabase();
			if (selectedDatabase!=null)
			{
				DatabaseModel newDatabase=new DatabaseModel(selectedDatabase);
				lmDatabases.addElement(newDatabase);
				lstDatabases.setSelectedValue(newDatabase, true);
			}
		}
	}

	private class DatabaseSelectionListener implements ListSelectionListener
	{
		public void valueChanged(ListSelectionEvent e)
		{
			if (!e.getValueIsAdjusting())
			{
				DatabaseModel database=(DatabaseModel)lstDatabases.getSelectedValue();
				setSelectedDatabase(database);
			}
		}
	}

	private class NameChangeListener extends DocumentAdapter
	{
		public void changedUpdate(DocumentEvent e)
		{
			if (selectedDatabase!=null)
			{
				selectedDatabase.setName(tfName.getText());
				lmDatabases.fireElementChanged(selectedDatabase);
			}
		}
	}

	private static class DatabaseListModel extends DefaultListModel
	{
		public void fireElementChanged(Object element)
		{
			int index=indexOf(element);
			super.fireContentsChanged(this, index, index);
		}
	}

	private class DriverSelectionListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			DatabaseDriver driver=(DatabaseDriver)cbxDriver.getSelectedItem();
			tmDriverProperties.setDriver(driver);
			if (selectedDatabase!=null)
			{
				if (driver!=null)
					selectedDatabase.setDriver(driver.getId());
				else
					selectedDatabase.setDriver(null);
				tfUrl.setText(selectedDatabase.buildURL());
				tfClass.setText(selectedDatabase.getClassNames());
			}
		}
	}

	private static class DriverComparator implements Comparator
	{
		public int compare(Object o1, Object o2)
		{
			DatabaseDriver driver1=(DatabaseDriver)o1;
			DatabaseDriver driver2=(DatabaseDriver)o2;
			return driver1.toString().compareToIgnoreCase(driver2.toString());
		}
	}

	private class URLChangeListener implements Observer
	{
		public void update(Observable o, Object arg)
		{
			NotifyObject note=(NotifyObject)arg;
			if ("url changed".equals(note.getArgument(0)))
			{
				String url=(String)note.getArgument(1);
				tfUrl.setText(url);
			}
			else if ("class changed".equals(note.getArgument(0)))
			{
				String classes=(String)note.getArgument(1);
				tfClass.setText(classes);
			}
		}
	}

	private class AddCustomPropertiesAction extends AnAction
	{
		public AddCustomPropertiesAction()
		{
			super("Create Property");
			getTemplatePresentation().setIcon(IconManager.getIcon(Icons.ADD));
			getTemplatePresentation().setDescription("Creates a new custom property.");
		}

		public void actionPerformed(AnActionEvent event)
		{
			tmCustomProperties.createRow();
		}
	}

	private class RemoveCustomPropertiesAction extends AnAction
	{
		public RemoveCustomPropertiesAction()
		{
			super("Delete Property");
			getTemplatePresentation().setIcon(IconManager.getIcon(Icons.REMOVE));
			getTemplatePresentation().setDescription("Deletes the selected custom properties.");
		}

		public void update(AnActionEvent event)
		{
			super.update(event);
			event.getPresentation().setEnabled(tblCustomProperties.getSelectedRowCount()>0);
		}

		public void actionPerformed(AnActionEvent e)
		{
			int[] rows=tblCustomProperties.getSelectedRows();
			if (rows!=null && rows.length>0)
			{
				final Set tableRows=new HashSet();
				for (int i=0; i<rows.length; i++)
				{
					tableRows.add(tmCustomProperties.getRow(rows[i]));
				}
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						for (Iterator it=tableRows.iterator(); it.hasNext();)
						{
							((CustomPropertiesTableModel.Row)it.next()).drop();
						}
					}
				});
			}
		}
	}

}
