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
import java.util.Collection;
import java.util.Iterator;
import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;

import com.intellij.openapi.project.Project;

import com.kiwisoft.db.Database;
import com.kiwisoft.db.DatabaseSchema;
import com.kiwisoft.db.DatabaseTable;
import com.kiwisoft.utils.gui.ButtonDialog;
import com.kiwisoft.utils.gui.IconManager;
import com.kiwisoft.utils.gui.lookup.DialogLookup;

/**
 * @author Stefan Stiller
 * @version $Revision: $, $Date: $
 */
public class DatabaseTableLookup implements DialogLookup
{
	private Project project;
	private Database database;

	public DatabaseTableLookup(Project project, Database database)
	{
		this.project=project;
		this.database=database;
	}

	public void open(JTextField field)
	{
		Container window=field.getTopLevelAncestor();
		LookupDialog dialog;
		if (window instanceof Dialog) dialog=new LookupDialog((Dialog)window);
		else dialog=new LookupDialog((Frame)window);
		if (dialog.open())
		{
			field.setText(dialog.getTable());
		}
	}

	public Icon getIcon()
	{
		return IconManager.getIcon("/com/kiwisoft/utils/gui/table/lookup_zoom.png");
	}

	private class LookupDialog extends ButtonDialog implements ListSelectionListener
	{
		private String table;

		private JList lstTables;
		private Action okAction;

		private LookupDialog(Dialog dialog)
		{
			super(dialog, "Tables", true);
		}

		private LookupDialog(Frame frame)
		{
			super(frame, "Tables", true);
		}

		protected JComponent createContentPane()
		{
			lstTables=new JList();
			lstTables.setCellRenderer(new DatabaseTableRenderer());
			lstTables.addListSelectionListener(this);
			lstTables.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

			JScrollPane tablePane=new JScrollPane(lstTables);
			tablePane.setPreferredSize(new Dimension(500, 300));

			JPanel panel=new JPanel(new GridBagLayout());
			panel.add(tablePane,
					  new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(10, 5, 0, 0), 0, 0));

			return panel;
		}

		protected Action[] getActions()
		{
			okAction=new OkAction();
			okAction.putValue(DEFAULT_ACTION, Boolean.TRUE);
			return new Action[]
			{
				okAction,
				new CancelAction()
			};
		}

		public void initData()
		{
			try
			{
				DefaultListModel listModel=new DefaultListModel();
				DatabaseSchema schema=database.getDefaultSchema(project);
				Collection types=database.getTableTypes(project);
				for (Iterator it=types.iterator(); it.hasNext();)
				{
					String type=(String)it.next();
					Collection tablesOfType=database.getTables(project, schema, type);
					for (Iterator it2=tablesOfType.iterator(); it2.hasNext();) listModel.addElement(it2.next());
				}
				lstTables.setModel(listModel);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			pack();
		}

		private String getTable()
		{
			return table;
		}

		public void valueChanged(ListSelectionEvent e)
		{
			if (!e.getValueIsAdjusting())
			{
				DatabaseTable table=(DatabaseTable)lstTables.getSelectedValue();
				if (table!=null) this.table=table.getTableName();
				else this.table=null;
				okAction.setEnabled(this.table!=null);
			}
		}
	}

}
