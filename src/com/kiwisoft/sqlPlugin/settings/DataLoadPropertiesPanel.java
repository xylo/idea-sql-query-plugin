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

import java.awt.GridBagLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.Iterator;
import java.util.Set;
import java.util.HashSet;
import java.util.Enumeration;
import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;

import com.intellij.openapi.project.Project;

import com.kiwisoft.sqlPlugin.dataLoad.DataLoadManager;
import com.kiwisoft.sqlPlugin.dataLoad.DataLoadDescriptor;
import com.kiwisoft.sqlPlugin.RenameDialog;
import com.kiwisoft.utils.idea.PluginUtils;

/**
 * @author Stefan Stiller
 */
public class DataLoadPropertiesPanel extends GlobalSettingsPanel
{
	private JList list;
	private DefaultListModel listModel;

	private Action copyAction;
	private Action removeAction;
	private Action renameAction;
	private Project project;

	public DataLoadPropertiesPanel(Project project)
	{
		super(new GridBagLayout());
		this.project=project;

		copyAction=new CopyAction();
		removeAction=new RemoveAction();
		renameAction=new RenameAction();

		list=new JList();

		setPreferredSize(new Dimension(400, 200));
		add(new JScrollPane(list),
			new GridBagConstraints(0, 0, 1, 4, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
		add(new JButton(copyAction),
			new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 0, 5, 5), 0, 0));
		add(new JButton(removeAction),
			new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 5), 0, 0));
		add(new JButton(renameAction),
			new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 5), 0, 0));
		add(Box.createGlue(),
			new GridBagConstraints(1, 3, 1, 1, 0.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
	}

	public String getTitle()
	{
		return "Data Load";
	}

	public void initializeComponents()
	{
		listModel=new DefaultListModel();
		for (Iterator it=DataLoadManager.getInstance().getDescriptors().iterator(); it.hasNext();)
		{
			listModel.addElement(new DescriptorModel((DataLoadDescriptor)it.next()));
		}
		list.setModel(listModel);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.addListSelectionListener(new ListSelectionListener()
		{
			public void valueChanged(final ListSelectionEvent e)
			{
				if (!e.getValueIsAdjusting())
				{
					Object selectedValue=list.getSelectedValue();
					copyAction.setEnabled(selectedValue!=null);
					removeAction.setEnabled(selectedValue!=null);
					renameAction.setEnabled(selectedValue!=null);
				}
			}
		});
	}

	public JComponent getPreferredFocusedComponent()
	{
		return list;
	}

	public void apply()
	{
		Set descriptors=new HashSet();
		Enumeration elements=listModel.elements();
		while (elements.hasMoreElements())
		{
			DescriptorModel descriptorModel=(DescriptorModel)elements.nextElement();
			DataLoadDescriptor descriptor=descriptorModel.getDescriptor();
			descriptor.setName(descriptorModel.getName());
			descriptors.add(descriptor);
		}
		DataLoadManager.getInstance().setDescriptors(descriptors);
	}

	public boolean canApply()
	{
		return true;
	}

	private class CopyAction extends AbstractAction
	{
		public CopyAction()
		{
			super("Copy");
			setEnabled(false);
		}

		public void actionPerformed(ActionEvent e)
		{
			Object selectedValue=list.getSelectedValue();
			if (selectedValue!=null)
			{
				selectedValue=((DescriptorModel)selectedValue).copy();
				listModel.addElement(selectedValue);
				list.setSelectedValue(selectedValue, true);
			}
		}
	}

	private class RemoveAction extends AbstractAction
	{
		public RemoveAction()
		{
			super("Delete");
			setEnabled(false);
		}

		public void actionPerformed(ActionEvent e)
		{
			Object selectedValue=list.getSelectedValue();
			if (selectedValue!=null) listModel.removeElement(selectedValue);
		}
	}

	private class RenameAction extends AbstractAction
	{
		public RenameAction()
		{
			super("Rename");
			setEnabled(false);
		}

		public void actionPerformed(ActionEvent e)
		{
			DescriptorModel selectedValue=(DescriptorModel)list.getSelectedValue();
			if (selectedValue!=null)
			{
				RenameDialog renameDialog=new RenameDialog(project, "Rename Format", selectedValue.getName());
				PluginUtils.showDialog(renameDialog, true, true);
				String newName=renameDialog.getReturnValue();
				if (newName!=null)
				{
					Enumeration elements=listModel.elements();
					while (elements.hasMoreElements())
					{
						DescriptorModel model=(DescriptorModel)elements.nextElement();
						if (model!=selectedValue && newName.equals(model.getName()))
						{
							JOptionPane.showMessageDialog(list, "Configuration with this name already exists.", "Error", JOptionPane.ERROR_MESSAGE);
							return;
						}
					}
					selectedValue.setName(newName);
				}
				list.repaint();
			}
		}
	}

	private static class DescriptorModel
	{
		private DataLoadDescriptor descriptor;
		private String name;

		public DescriptorModel(DataLoadDescriptor descriptor)
		{
			this.descriptor=descriptor;
			this.name=descriptor.getName();
		}

		public DataLoadDescriptor getDescriptor()
		{
			return descriptor;
		}

		public String getName()
		{
			return name;
		}

		public void setName(String name)
		{
			this.name=name;
		}

		public String toString()
		{
			return name;
		}

		public DescriptorModel copy()
		{
			return new DescriptorModel(descriptor.copy());
		}
	}

}
