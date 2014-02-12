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

import java.util.Iterator;

import com.kiwisoft.sqlPlugin.templates.TemplateManager;
import com.kiwisoft.sqlPlugin.templates.StatementTemplate;
import com.kiwisoft.utils.gui.table.SortableTableModel;
import com.kiwisoft.utils.gui.table.SortableTableRow;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.3 $, $Date: 2006/03/24 18:15:15 $
 */
public class TemplatesTableModel extends SortableTableModel
{
	private String[] names={"name"};

	public TemplatesTableModel()
	{
		super();
		createData();
	}

	private void createData()
	{
		TemplateManager templateManager=TemplateManager.getInstance();
		for (Iterator it=templateManager.getTemplates().iterator(); it.hasNext();)
		{
			StatementTemplate template=(StatementTemplate)it.next();
			addRow(new Row(new StatementTemplateModel(template)));
			sort();
		}
	}

	public int getColumnCount()
	{
		return names.length;
	}

	public String getColumnName(int col)
	{
		return names[col];
	}

	public void addTemplate(StatementTemplate template)
	{
		addRow(new Row(template));
		sort();
	}

	public StatementTemplate getTemplate(String name)
	{
		for (int i=0;i<getRowCount();i++)
		{
			StatementTemplate template=(StatementTemplate)getObject(i);
			if (template.getName().equals(name)) return template;
		}
		return null;
	}

	private class Row extends SortableTableRow
	{
		public Row(StatementTemplate template)
		{
			super(template);
		}

		public Comparable getSortValue(int col)
		{
			return ((StatementTemplate)getUserObject()).getName();
		}

		public Object getDisplayValue(int col)
		{
			return getUserObject();
		}
	}

}
