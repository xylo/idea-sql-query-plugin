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
package com.kiwisoft.db.export;

import java.awt.Component;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import com.kiwisoft.db.sql.SQLStatement;
import com.kiwisoft.sqlPlugin.ResultSetTableModel;
import com.kiwisoft.sqlPlugin.config.ExportConfiguration;
import com.kiwisoft.utils.RegularFileFilter;
import com.kiwisoft.utils.StringUtils;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.4 $, $Date: 2006/03/24 17:53:37 $
 */
public class FixedWidthExporter implements Exporter
{
    private static Exporter instance;

    public static Exporter getInstance()
    {
        if (instance==null) instance=new FixedWidthExporter();
        return instance;
    }

    private FixedWidthExporter()
    {
    }

    public void exportTable(JTable table, SQLStatement statement, File file, ExportConfiguration configuration) throws Exception
    {
		PrintWriter out=new PrintWriter(new FileWriter(file));
		TableColumnModel columnModel=table.getColumnModel();
		ResultSetTableModel tableModel=(ResultSetTableModel) table.getModel();
		int columnCount=columnModel.getColumnCount();
		int[] columnWidths=new int[columnCount];
		List rows=new ArrayList();
		List row=new ArrayList();
		for (int i=0; i<columnCount; i++)
		{
			TableColumn column=columnModel.getColumn(i);
			int columnIndex=column.getModelIndex();
			CellInfo cell=new CellInfo(tableModel.getColumnName(columnIndex));
			if (cell.content!=null && cell.content.length()>columnWidths[i]) columnWidths[i]=cell.content.length();
			row.add(cell);
		}
		rows.add(row);
		for (int j=0; j<tableModel.getRowCount(); j++)
		{
			row=new ArrayList();
			for (int i=0; i<columnCount; i++)
			{
				TableColumn column=columnModel.getColumn(i);
				int columnIndex=column.getModelIndex();
				Object cellValue=tableModel.getValueAt(j, columnIndex);
				TableCellRenderer cellRenderer=table.getCellRenderer(j, columnIndex);
				Component component=cellRenderer.getTableCellRendererComponent(table, cellValue, false, false, j, columnIndex);
				int alignment=SwingConstants.LEADING;
				if (component instanceof JLabel)
				{
					JLabel label=(JLabel)component;
					cellValue=label.getText();
					alignment=label.getHorizontalAlignment();
				}
				CellInfo cell=new CellInfo(cellValue!=null ? cellValue.toString() : null, alignment);
				if (cell.content!=null && cell.content.length()>columnWidths[i]) columnWidths[i]=cell.content.length();
				row.add(cell);
			}
			rows.add(row);
		}
		for (Iterator itRows=rows.iterator(); itRows.hasNext();)
		{
			row=(List)itRows.next();
			int i=0;
			for (Iterator itCols=row.iterator(); itCols.hasNext();i++)
			{
				CellInfo cell=(CellInfo)itCols.next();
				if (cell.content==null) cell.content="";
				if (cell.alignment==SwingConstants.TRAILING || cell.alignment==SwingConstants.RIGHT)
					cell.content=StringUtils.fillLeft(cell.content, ' ', columnWidths[i]);
				else
					cell.content=StringUtils.fillRight(cell.content, ' ', columnWidths[i]);
				out.print(cell.content);
				if (itCols.hasNext()) out.print(" ");
			}
			if (itRows.hasNext()) out.println();
		}
		out.close();
	}

	private static class CellInfo
	{
		public String content;
		public int alignment=SwingConstants.LEADING;

		public CellInfo(String content)
		{
			this.content=content;
		}

		public CellInfo(String content, int alignment)
		{
			this.content=content;
			this.alignment=alignment;
		}
	}

	public boolean isEnabled()
	{
		return true;
	}

	public String getName()
    {
        return "Text [Fixed Width]";
    }

    public String getIcon()
    {
        return "textFile";
    }

    public FileFilter getFileFilter()
    {
        return new RegularFileFilter("*.txt", "Text Files");
    }
}
