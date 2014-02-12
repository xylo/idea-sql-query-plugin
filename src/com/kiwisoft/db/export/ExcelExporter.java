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
import java.io.FileOutputStream;
import java.util.Date;
import java.util.Vector;
import java.lang.reflect.Field;
import javax.swing.JTable;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.apache.poi.hssf.usermodel.*;

import com.kiwisoft.db.sql.SQLStatement;
import com.kiwisoft.sqlPlugin.ResultSetTableModel;
import com.kiwisoft.sqlPlugin.config.ExportConfiguration;
import com.kiwisoft.utils.RegularFileFilter;
import com.kiwisoft.utils.StringUtils;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.4 $, $Date: 2006/03/24 17:53:37 $
 */
public class ExcelExporter implements Exporter
{
    private static Exporter instance;

    public static Exporter getInstance()
    {
        if (instance==null) instance=new ExcelExporter();
        return instance;
    }

    private ExcelExporter()
    {
    }

	public boolean isEnabled()
	{
		try
		{
			Class.forName("org.apache.poi.hssf.usermodel.HSSFWorkbook");
			return true;
		}
		catch (ClassNotFoundException e)
		{
			return false;
		}
	}

    public void exportTable(JTable table, SQLStatement statement, File file, ExportConfiguration configuration) throws Exception
    {
		HSSFWorkbook workbook=new HSSFWorkbook();
		HSSFSheet sheet=workbook.createSheet("Query");
		TableColumnModel columnModel=table.getColumnModel();
		ResultSetTableModel tableModel=(ResultSetTableModel) table.getModel();
		HSSFFont headerFont=workbook.createFont();
		headerFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		HSSFCellStyle headerStyle=workbook.createCellStyle();
		headerStyle.setFont(headerFont);
		headerStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		int rowNum=0;
		HSSFRow row=sheet.createRow(rowNum++);
		sheet.createFreezePane(0, 1);
		int columnCount=columnModel.getColumnCount();
		for (short i=0; i<columnCount; i++)
		{
			TableColumn column=columnModel.getColumn(i);
			int columnIndex=column.getModelIndex();
			HSSFCell cell=row.createCell(i);
			cell.setCellStyle(headerStyle);
			String columnName=tableModel.getColumnName(columnIndex);
			cell.setCellValue(columnName);
		}
		for (int j=0; j<tableModel.getRowCount(); j++)
		{
			row=sheet.createRow(rowNum++);
			for (short i=0; i<columnCount; i++)
			{
				TableColumn column=columnModel.getColumn(i);
				int columnIndex=column.getModelIndex();
				Object cellValue=tableModel.getValueAt(j, columnIndex);
				HSSFCell cell=row.createCell(i);
				if (cellValue instanceof Number)  cell.setCellValue(((Number)cellValue).doubleValue());
				else if (cellValue instanceof Date)
				{

					HSSFCellStyle style=workbook.createCellStyle();
					style.setDataFormat((short)14);
					cell.setCellValue((Date)cellValue);
					cell.setCellStyle(style);
				}
				else if (cellValue instanceof Boolean) cell.setCellValue(((Boolean)cellValue).booleanValue());
				else if (cellValue!=null) cell.setCellValue(cellValue.toString());
			}
		}
		FileOutputStream out=new FileOutputStream(file);
		workbook.write(out);
		Field field=ClassLoader.class.getDeclaredField("classes");
		field.setAccessible(true);
		Vector classes=(Vector)field.get(HSSFWorkbook.class.getClassLoader());
		field.setAccessible(false);
		System.out.println("classes = "+StringUtils.enumerate(classes, "\n"));
		out.close();
	}

    public String getName()
    {
        return "Excel";
    }

    public String getIcon()
    {
        return "excel";
    }

    public FileFilter getFileFilter()
    {
        return new RegularFileFilter("*.xls", "Excel Files");
    }
}
