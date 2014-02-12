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

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.project.Project;

import com.kiwisoft.sqlPlugin.templates.StatementTemplate;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.utils.text.SourceTextPane;
import com.kiwisoft.utils.text.SyntaxDefinitionFactory;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.3 $, $Date: 2006/03/24 18:12:25 $
 */
public class EditTemplateDialog extends DialogWrapper
{
	private SourceTextPane textPane;
	private JPanel pnlContent;
	private JTextField tfName;
	private TemplateHandler handler;
	private StatementTemplate template;

	public EditTemplateDialog(Project project, TemplateHandler handler, StatementTemplate template)
	{
		super(project, false);
		this.handler=handler;
		this.template=template;
		initializeComponents();
		tfName.setText(template.getName());
		textPane.setText(template.getText());
		init();
	}

	public EditTemplateDialog(Project project, TemplateHandler handler)
	{
		super(project, false);
		this.handler=handler;
		initializeComponents();
		init();
	}

	public EditTemplateDialog(Project project, TemplateHandler handler, String name, String text)
	{
		super(project, false);
		this.handler=handler;
		initializeComponents();
		tfName.setText(name);
		textPane.setText(text);
		init();
	}

	private void initializeComponents()
	{
		setTitle("Edit Template");
		tfName=new JTextField();

		textPane=new SourceTextPane(SyntaxDefinitionFactory.getInstance().getSyntaxDefinition("sql"));

		JScrollPane templatePane=new JScrollPane(textPane);
		templatePane.setBorder(new TitledBorder(new EtchedBorder(), "Template Text"));

		pnlContent=new JPanel(new GridBagLayout());
		pnlContent.add(new JLabel("Name:"),
					   new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(4, 4, 4, 4), 0, 0));
		pnlContent.add(tfName,
					   new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(4, 0, 4, 4), 0, 0));
		pnlContent.add(templatePane,
					   new GridBagConstraints(0, 1, 2, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 4, 4, 4), 0, 0));
		pnlContent.setPreferredSize(new Dimension(400, 300));
	}

	public Action[] createActions()
	{
		return new Action[]{new ApplyAction(), new CancelAction()};
	}

	public JComponent createCenterPanel()
	{
		return pnlContent;
	}

	public JComponent getPreferredFocusedComponent()
	{
		return tfName;
	}

	private class ApplyAction extends AbstractAction
	{
		public ApplyAction()
		{
			super("Ok");
			putValue(DEFAULT_ACTION, Boolean.TRUE);
		}

		public void actionPerformed(ActionEvent e)
		{
			String name=tfName.getText();
			String text=textPane.getText();
			if (StringUtils.isEmpty(name))
			{
				tfName.requestFocus();
				JOptionPane.showMessageDialog(pnlContent, "Name must not be empty.", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			if (!handler.isNameValid(name, template))
			{
				tfName.requestFocus();
				JOptionPane.showMessageDialog(pnlContent, "Name is already used by another template.", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			if (template!=null)
			{
				handler.removeTemplate(template);
				template.setName(name);
				template.setText(text);
				handler.addTemplate(template);
			}
			else
			{
				template=handler.createTemplate(name, text);
				handler.addTemplate(template);
			}
			dispose();
		}
	}

	private class CancelAction extends AbstractAction
	{
		public CancelAction()
		{
			super("Cancel");
		}

		public void actionPerformed(ActionEvent e)
		{
			dispose();
		}
	}
}
