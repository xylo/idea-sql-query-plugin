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
import java.io.IOException;
import java.io.PrintWriter;
import javax.swing.JTable;
import javax.swing.filechooser.FileFilter;

import com.kiwisoft.utils.RegularFileFilter;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.db.sql.SQLStatement;
import com.kiwisoft.sqlPlugin.config.ExportConfiguration;
import com.kiwisoft.sqlPlugin.config.CSVExportConfiguration;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.6 $, $Date: 2006/03/24 17:53:37 $
 */
public class CSVExporter extends AbstractExporter
{
    private static Exporter instance;
    private String delimiter=",";
    private String textQualifier="\"";
	private boolean forceQualifier;

	public static Exporter getInstance()
    {
        if (instance==null) instance=new CSVExporter();
        return instance;
    }

    private CSVExporter()
    {
    }

    public void exportTable(JTable table, SQLStatement statement, File file, ExportConfiguration configuration) throws IOException
    {
		CSVExportConfiguration csv=configuration.getCSV();
		delimiter=csv.getDelimiter();
        textQualifier=csv.getTextQualifier();
		forceQualifier=csv.isForceQualifier();
        super.exportTable(table, statement, file, configuration);
    }

    public String getName()
    {
        return "Text [CSV]";
    }

    public String getIcon()
    {
        return "textFile";
    }

    public FileFilter getFileFilter()
    {
        return new RegularFileFilter("*.csv", "CSV Files");
    }

    private String createExportString(Object value)
    {
        if (value!=null)
        {
            String text=String.valueOf(value);
            if (forceQualifier || text.indexOf(textQualifier)>=0)
            {
                text=StringUtils.replaceStrings(text, textQualifier, textQualifier+textQualifier);
                return textQualifier+text+textQualifier;
            }
            if (text.indexOf(delimiter)>=0)
            {
                return textQualifier+text+textQualifier;
            }
            return text;
        }
        return "";
    }

    protected void writeFileHeader(PrintWriter out)
    {
    }

    protected void writeFileFooter(PrintWriter out)
    {
    }

    protected void openTableHeader(PrintWriter out)
    {
    }

    protected void closeTableHeader(PrintWriter out)
    {
        out.println();
    }

    protected void writeTableHeader(PrintWriter out, String name)
    {
        out.print(createExportString(name)+delimiter);
    }

    protected void openTableRow(PrintWriter out)
    {
    }

    protected void closeTableRow(PrintWriter out)
    {
        out.println();
    }

    protected void writeTableCell(PrintWriter out, File file, String columnName, Object cellValue, int renderer, int alignment)
    {
        out.print(createExportString(cellValue)+delimiter);
    }
}
