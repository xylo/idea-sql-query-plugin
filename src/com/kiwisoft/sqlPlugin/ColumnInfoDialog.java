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
package com.kiwisoft.sqlPlugin;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Color;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.project.Project;

import com.kiwisoft.db.DatabaseUtils;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.3 $, $Date: 2006/03/24 17:59:51 $
 */
public class ColumnInfoDialog extends DialogWrapper
{
	private JTextField tfName;
	private JTextField tfJDBCType;
	private JTextField tfType;

	private JPanel pnlContent;

	public ColumnInfoDialog(Project project, String title, ColumnInfo columnInfo)
	{
		super(project, true);
		setTitle(title);
		initializeComponents();
		initializeData(columnInfo);
		init();
	}

	private void initializeData(ColumnInfo columnInfo)
	{
		tfName.setText(columnInfo.getName());
		int jdbcType=columnInfo.getJdbcType();
		tfJDBCType.setText(DatabaseUtils.getTypeString(jdbcType)+" ["+jdbcType+"]");
		if (columnInfo.getColumnClassName()!=null) tfType.setText(columnInfo.getColumnClassName());
	}

	public Action[] createActions()
	{
		return new Action[]{new CloseAction()};
	}

	public JComponent createCenterPanel()
	{
		return pnlContent;
	}

	private void initializeComponents()
	{
		tfName=createTextField();
		tfJDBCType=createTextField();
		tfType=createTextField();

		pnlContent=new JPanel(new GridBagLayout());

		int row=0;
		addField(pnlContent, row++, "Column Name:", tfName);
		addField(pnlContent, row++, "JDBC Type:", tfJDBCType);
		addField(pnlContent, row, "Class:", tfType);
	}

	private JTextField createTextField()
	{
		JTextField textField=new JTextField(20);
		textField.setEditable(false);
		textField.setBackground(Color.WHITE);
		return textField;
	}

	private void addField(JPanel pnlContent, int row, String name, JTextField textField)
	{
		pnlContent.add(new JLabel(name),
					   new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(3, 3, 3, 3), 0, 0));
		pnlContent.add(textField,
					   new GridBagConstraints(1, row, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(3, 3, 3, 3), 0, 0));
	}

	private class CloseAction extends AbstractAction
	{
		public CloseAction()
		{
			super("Close");
			putValue(DEFAULT_ACTION, Boolean.TRUE);
		}

		public void actionPerformed(ActionEvent e)
		{
			dispose();
		}
	}

}