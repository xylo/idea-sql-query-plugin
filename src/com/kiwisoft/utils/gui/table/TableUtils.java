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
package com.kiwisoft.utils.gui.table;

import java.awt.Component;
import java.awt.Dimension;
import javax.swing.table.TableColumn;
import javax.swing.table.TableCellRenderer;
import javax.swing.JTable;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.2 $, $Date: 2006/03/24 18:04:07 $
 */
public class TableUtils
{
	private TableUtils()
	{
	}

	public static void sizeColumnsToFit(JTable table, boolean useHeader, boolean useContent)
	{
		if (!useHeader && !useContent) return;
		for (int i=0; i<table.getColumnCount(); i++)
		{
			TableColumn column=table.getColumnModel().getColumn(i);
			int columnIndex=column.getModelIndex();
			int preferredWidth=0;
			if (useContent)
			{
				for (int row=0; row<table.getModel().getRowCount(); row++)
				{
					Object value=table.getModel().getValueAt(row, columnIndex);
					TableCellRenderer renderer=table.getCellRenderer(row, columnIndex);
					Component component=renderer.getTableCellRendererComponent(table, value, false, false, row, columnIndex);
					Dimension preferredSize=component.getPreferredSize();
					if (preferredSize.width>preferredWidth) preferredWidth=preferredSize.width;
				}
			}
			if (useHeader)
			{
				Object value=column.getHeaderValue();
				TableCellRenderer renderer=column.getHeaderRenderer();
				if (renderer==null) renderer=table.getTableHeader().getDefaultRenderer();
				Component component=renderer.getTableCellRendererComponent(table, value, false, false, 0, columnIndex);
				if (component.getPreferredSize().width>preferredWidth) preferredWidth=component.getPreferredSize().width;
			}
			if (preferredWidth>300) preferredWidth=300;
			if (preferredWidth>0)
			{
				column.setPreferredWidth(preferredWidth+20);
			}
		}
	}
}
