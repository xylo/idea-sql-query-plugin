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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.JCheckBox;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableCellRenderer;

import com.kiwisoft.sqlPlugin.ResultSetTableModel;
import com.kiwisoft.sqlPlugin.config.ExportConfiguration;
import com.kiwisoft.db.sql.SQLStatement;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.7 $, $Date: 2006/03/24 17:53:37 $
 */
public abstract class AbstractExporter implements Exporter
{
	public static final int LABEL=0;
	public static final int CHECKBOX=1;

	public boolean isEnabled()
	{
		return true;
	}

	public void exportTable(JTable table, SQLStatement statement, File file, ExportConfiguration configuration) throws IOException
    {
        PrintWriter out=new PrintWriter(new FileWriter(file));
        writeFileHeader(out);
        TableColumnModel columnModel=table.getColumnModel();
        openTableHeader(out);
        ResultSetTableModel tableModel=(ResultSetTableModel) table.getModel();
        for (int i=0; i<columnModel.getColumnCount(); i++)
        {
            TableColumn column=columnModel.getColumn(i);
            int columnIndex=column.getModelIndex();
            writeTableHeader(out, tableModel.getColumnName(columnIndex));
        }
        closeTableHeader(out);
        for (int row=0; row<tableModel.getRowCount(); row++)
        {
            openTableRow(out);
            for (int col=0; col<columnModel.getColumnCount(); col++)
            {
                TableColumn column=columnModel.getColumn(col);
                int modelCol=column.getModelIndex();
                Object cellValue=tableModel.getValueAt(row, modelCol);
				TableCellRenderer cellRenderer=table.getCellRenderer(row, col);
				Component component=cellRenderer.getTableCellRendererComponent(table, cellValue, false, false, row, modelCol);
				int alignment=SwingConstants.LEFT;
				int renderer=LABEL;
				if (component instanceof JLabel)
				{
					JLabel label=(JLabel)component;
					alignment=label.getHorizontalAlignment();
					cellValue=label.getText();
				}
				else if (component instanceof JCheckBox)
				{
					JCheckBox checkBox=(JCheckBox)component;
					cellValue=Boolean.valueOf(checkBox.isSelected());
					alignment=checkBox.getHorizontalAlignment();
					renderer=CHECKBOX;
				}
				writeTableCell(out, file, tableModel.getColumnName(modelCol), cellValue, renderer, alignment);
            }
            closeTableRow(out);
        }
        writeFileFooter(out);
        out.close();
    }

    protected abstract void writeFileHeader(PrintWriter out);

    protected abstract void writeFileFooter(PrintWriter out);

    protected abstract void openTableHeader(PrintWriter out);

    protected abstract void closeTableHeader(PrintWriter out);

    protected abstract void writeTableHeader(PrintWriter out, String name);

    protected abstract void openTableRow(PrintWriter out);

    protected abstract void closeTableRow(PrintWriter out);

    protected abstract void writeTableCell(PrintWriter out, File file, String columnName, Object renderedValue, int renderer, int alignment) throws IOException;
}
