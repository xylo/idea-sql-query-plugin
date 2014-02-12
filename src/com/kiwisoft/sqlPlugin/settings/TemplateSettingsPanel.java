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
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.intellij.openapi.project.Project;

import com.kiwisoft.sqlPlugin.DefaultTableConfiguration;
import com.kiwisoft.sqlPlugin.templates.StatementTemplate;
import com.kiwisoft.sqlPlugin.templates.TemplateManager;
import com.kiwisoft.utils.gui.table.SortableTable;
import com.kiwisoft.utils.idea.PluginUtils;
import com.kiwisoft.utils.text.SourceTextPane;
import com.kiwisoft.utils.text.SyntaxDefinitionFactory;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.5 $, $Date: 2006/03/24 18:12:26 $
 */
public class TemplateSettingsPanel extends GlobalSettingsPanel implements TemplateHandler
{
	private SortableTable tblTemplates;
	private SourceTextPane tfPreview;
	private TemplateSelectionListener templateSelectionListener;
	private TemplatesTableModel tmTemplates;
	private Project project;

	private EditAction editAction;
	private CopyAction copyAction;
	private RemoveAction removeAction;

	public TemplateSettingsPanel(Project project)
	{
		this.project=project;
	}

	public String getTitle()
	{
		return "Templates";
	}

	public String getHelpTopic()
	{
		return "KiwiSQL.templatesPanel";
	}

	public void initializeComponents()
	{
		editAction=new EditAction();
		copyAction=new CopyAction();
		removeAction=new RemoveAction();

		tmTemplates=new TemplatesTableModel();
		tblTemplates=new SortableTable(tmTemplates);
		tblTemplates.putClientProperty(SortableTable.AUTO_RESIZE_ROWS, Boolean.TRUE);
		DefaultTableConfiguration tableConfig=new DefaultTableConfiguration("templates");
		tableConfig.setSupportWidths(false);
		tblTemplates.initializeColumns(tableConfig);
		tmTemplates.addSortColumn(0, false);
		tblTemplates.setShowGrid(false);
		tfPreview=new SourceTextPane(SyntaxDefinitionFactory.getInstance().getSyntaxDefinition("sql"));
		tfPreview.setEditable(false);

		JScrollPane scrlTemplates=new JScrollPane(tblTemplates);
		scrlTemplates.setPreferredSize(new Dimension(300, 150));
		scrlTemplates.getViewport().setBackground(tblTemplates.getBackground());

		JPanel pnlTemplates=new JPanel();
		int row=0;
		pnlTemplates.setLayout(new GridBagLayout());
		pnlTemplates.setBorder(new TitledBorder(new EtchedBorder(), "Templates"));
		pnlTemplates.add(scrlTemplates,
						 new GridBagConstraints(0, 0, 1, 5, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(4, 4, 4, 4), 0, 0));
		pnlTemplates.add(new JButton(new AddAction()),
						 new GridBagConstraints(1, row++, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(4, 0, 4, 4), 0, 0));
		pnlTemplates.add(new JButton(copyAction),
						 new GridBagConstraints(1, row++, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 4, 4), 0, 0));
		pnlTemplates.add(new JButton(editAction),
						 new GridBagConstraints(1, row++, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 4, 4), 0, 0));
		pnlTemplates.add(new JButton(removeAction),
						 new GridBagConstraints(1, row++, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 4, 4), 0, 0));
		pnlTemplates.add(Box.createGlue(),
						 new GridBagConstraints(1, row, 1, 1, 0.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));

		JPanel pnlPreview=new JPanel();
		pnlPreview.setLayout(new GridBagLayout());
		pnlPreview.setBorder(new TitledBorder(new EtchedBorder(), "Preview"));
		pnlPreview.add(new JScrollPane(tfPreview),
					   new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(4, 4, 4, 4), 0, 0));

		setLayout(new GridBagLayout());
		row=0;
		add(pnlTemplates,
			new GridBagConstraints(0, row++, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(7, 7, 7, 7), 0, 0));
		add(pnlPreview,
			new GridBagConstraints(0, row, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 7, 7, 7), 0, 0));
	}

	public void installListeners()
	{
		templateSelectionListener=new TemplateSelectionListener();
		tblTemplates.getSelectionModel().addListSelectionListener(templateSelectionListener);
	}

	private void removeListeners()
	{
		tblTemplates.getSelectionModel().removeListSelectionListener(templateSelectionListener);
	}

	public void removeNotify()
	{
		removeListeners();
		super.removeNotify();
	}

	public StatementTemplate createTemplate(String name, String text)
	{
		return new StatementTemplateModel(name, text);
	}

	public void removeTemplate(StatementTemplate template)
	{
		tmTemplates.removeRow(template);
	}

	public void addTemplate(StatementTemplate template)
	{
		tmTemplates.addTemplate(template);
		int index=tmTemplates.indexOf(template);
		if (index>=0) tblTemplates.getSelectionModel().setSelectionInterval(index, index);
	}

	public boolean isNameValid(String name, StatementTemplate template)
	{
		StatementTemplate tmp=tmTemplates.getTemplate(name);
		return template==null ? tmp==null : tmp==null || tmp==template;
	}

	public boolean canApply()
	{
		return true;
	}

	public void apply()
	{
		TemplateManager manager=TemplateManager.getInstance();
		manager.removeAllTemplates();
		for (int i=0;i<tmTemplates.getRowCount();i++)
		{
			StatementTemplate template=(StatementTemplate)tmTemplates.getObject(i);
			manager.addTemplate(template);
		}
	}

	private class TemplateSelectionListener implements ListSelectionListener
	{
		public void valueChanged(ListSelectionEvent e)
		{
			if (!e.getValueIsAdjusting())
			{
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						int row=tblTemplates.getSelectedRow();
						if (row>=0)
						{
							StatementTemplate template=(StatementTemplate)tmTemplates.getObject(row);
							if (template!=null)
								tfPreview.setText(template.getText());
							else
								tfPreview.setText("");
						}
						else
							tfPreview.setText("");
						editAction.validate();
						copyAction.validate();
						removeAction.validate();
					}
				});
			}
		}
	}

	private class EditAction extends AbstractAction
	{
		public EditAction()
		{
			super("Edit...");
			setEnabled(false);
		}

		public void validate()
		{
			setEnabled(tblTemplates.getSelectedRow()>=0);
		}

		public void actionPerformed(ActionEvent e)
		{
			int row=tblTemplates.getSelectedRow();
			if (row>=0)
			{
				StatementTemplate template=(StatementTemplate)tmTemplates.getObject(row);
				if (template!=null)
				{
					PluginUtils.showDialog(new EditTemplateDialog(project, TemplateSettingsPanel.this, template), true, true);
				}
			}
		}
	}

	private class CopyAction extends AbstractAction
	{
		public CopyAction()
		{
			super("Copy...");
			setEnabled(false);
		}

		public void validate()
		{
			setEnabled(tblTemplates.getSelectedRow()>=0);
		}

		public void actionPerformed(ActionEvent e)
		{
			int row=tblTemplates.getSelectedRow();
			if (row>=0)
			{
				StatementTemplate template=(StatementTemplate)tmTemplates.getObject(row);
				if (template!=null)
				{
					EditTemplateDialog dialog=new EditTemplateDialog(project, TemplateSettingsPanel.this, template.getName(), template.getText());
					PluginUtils.showDialog(dialog, true, true);
				}
			}
		}
	}

	private class RemoveAction extends AbstractAction
	{
		public RemoveAction()
		{
			super("Remove");
			setEnabled(false);
		}

		public void validate()
		{
			setEnabled(tblTemplates.getSelectedRow()>=0);
		}

		public void actionPerformed(ActionEvent e)
		{
			int row=tblTemplates.getSelectedRow();
			if (row>=0)
			{
				StatementTemplate template=(StatementTemplate)tmTemplates.getObject(row);
				if (template!=null) removeTemplate(template);
			}
		}
	}

	private class AddAction extends AbstractAction
	{
		public AddAction()
		{
			super("Add...");
		}

		public void actionPerformed(ActionEvent e)
		{
			PluginUtils.showDialog(new EditTemplateDialog(project, TemplateSettingsPanel.this), true, true);
		}
	}
}
