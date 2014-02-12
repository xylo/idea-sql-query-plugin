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
import java.io.FileOutputStream;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileFilter;

import com.intellij.openapi.util.io.FileUtil;

import com.kiwisoft.utils.RegularFileFilter;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.utils.xml.XMLUtils;
import com.kiwisoft.db.sql.SQLStatement;
import com.kiwisoft.sqlPlugin.config.ExportConfiguration;
import com.kiwisoft.sqlPlugin.config.HTMLExportConfiguration;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.6 $, $Date: 2006/03/24 17:54:49 $
 */
public class HTMLExporter extends AbstractExporter
{
    private static Exporter instance;
    private String tableStyle;
    private String cellStyle;
    private String headerStyle;
    private String rowStyle1;
    private String rowStyle2;
    private boolean alternateRows;
    private int changeRow;
    private boolean hasHeaderStyle;
    private boolean hasRowStyle1;
    private boolean hasRowStyle2;
    private int currentRow;
    private int currentStyle;
    private String statement;

    public static Exporter getInstance()
    {
        if (instance==null) instance=new HTMLExporter();
        return instance;
    }

    private HTMLExporter()
    {
    }

    public String getName()
    {
        return "HTML";
    }

    public String getIcon()
    {
        return "htmlFile";
    }

    public FileFilter getFileFilter()
    {
        return new RegularFileFilter("*.htm*", "HTML Files");
    }

    public void exportTable(JTable table, SQLStatement statement, File file, ExportConfiguration configuration) throws IOException
    {
		HTMLExportConfiguration config=configuration.getHTML();
		if (statement!=null && config.isIncludeQuery()) this.statement=statement.getText();
        tableStyle=config.getTableStyle();
        headerStyle=config.getHeaderStyle();
        cellStyle=config.getCellStyle();
        hasHeaderStyle=!StringUtils.isEmpty(headerStyle);
        rowStyle1=config.getRowStyle1();
        hasRowStyle1=!StringUtils.isEmpty(rowStyle1);
        rowStyle2=config.getRowStyle2();
        hasRowStyle2=!StringUtils.isEmpty(rowStyle2);
        alternateRows=config.isAlternateRows();
        changeRow=config.getAlternateInterval();
        currentRow=0;
        currentStyle=1;
        super.exportTable(table, statement, file, configuration);
    }

    protected void writeFileHeader(PrintWriter out)
    {
        out.println("<html>");
        out.println("<head>");
        out.println("<title>Table Export</title>");
        out.println("<style type=\"text/css\">");
        if (!StringUtils.isEmpty(tableStyle)) out.println("table {"+tableStyle+'}');
        if (!StringUtils.isEmpty(cellStyle)) out.println("td {"+cellStyle+'}');
        if (hasHeaderStyle) out.println(".header {"+headerStyle+'}');
        if (hasRowStyle1) out.println(".row1 {"+rowStyle1+'}');
        if (alternateRows && hasRowStyle2) out.println(".row2 {"+rowStyle2+'}');
        out.println("</style>");
        out.println("</head>");
        out.println("<body>");
		if (!StringUtils.isEmpty(statement)) out.println("<p>"+StringUtils.createHtml(statement)+"</p>");
        out.println("<table>");
    }

    protected void writeFileFooter(PrintWriter out)
    {
        out.println("</table>");
        out.println("</body>");
        out.println("</html>");
    }

    protected void openTableHeader(PrintWriter out)
    {
        if (hasHeaderStyle) out.print("<tr class=header>");
        else out.println("<tr>");
    }

    protected void closeTableHeader(PrintWriter out)
    {
        out.println("</tr>");
    }

    protected void writeTableHeader(PrintWriter out, String name)
    {
        if (name!=null) name=name.trim();
        if (StringUtils.isEmpty(name)) name="&nbsp;";
        else name=XMLUtils.toXMLString(name);
        out.print("<td>"+name+"</td>");
    }

    protected void openTableRow(PrintWriter out)
    {
        if (alternateRows)
        {
            if (currentRow>0 && currentRow%changeRow==0) currentStyle=3-currentStyle;
            if (currentStyle==1 && hasRowStyle1) out.print("<tr class=row1>");
            else if (currentStyle==2 && hasRowStyle2) out.print("<tr class=row2>");
            else out.println("<tr>");
        }
        else
        {
            if (hasRowStyle1) out.print("<tr class=row1>");
            else out.println("<tr>");
        }
    }

    protected void closeTableRow(PrintWriter out)
    {
        out.println("</tr>");
        currentRow++;
    }

    protected void writeTableCell(PrintWriter out, File file, String columnName, Object cellValue, int renderer, int alignment) throws IOException
	{
		out.print("<td");
		switch (alignment)
		{
			case SwingConstants.RIGHT:
			case SwingConstants.TRAILING:
				out.print(" align=\"right\"");
				break;
			case SwingConstants.CENTER:
				out.print(" align=\"center\"");
				break;
		}
		out.print(">");
		if (renderer==CHECKBOX)
		{
			String image=Boolean.TRUE.equals(cellValue) ? "checkbox1.png" : "checkbox2.png";
			copyIcon(file, image);
			out.print("<img src=\""+file.getName()+"-files/"+image+"\" width=\"16\" height=\"16\" alt=\""+cellValue+"\">");
		}
		else
		{
			String cellText=null;
			if (cellValue!=null) cellText=cellValue.toString().trim();
			if (StringUtils.isEmpty(cellText))
				cellText="&nbsp;";
			else
				cellText=XMLUtils.toXMLString(cellText);
			out.print(cellText);
		}
		out.print("</td>");
	}

	private static void copyIcon(File file, String image) throws IOException
	{
		File folder=new File(file.getParentFile(), file.getName()+"-files");
		File imageFile=new File(folder, image);
		if (!imageFile.exists())
		{
			folder.mkdirs();
			FileUtil.copy(HTMLExporter.class.getResourceAsStream("/icons/"+image), new FileOutputStream(imageFile));
		}
	}
}
