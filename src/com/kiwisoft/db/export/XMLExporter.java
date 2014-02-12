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

import java.io.PrintWriter;
import java.io.File;
import javax.swing.filechooser.FileFilter;

import com.kiwisoft.utils.RegularFileFilter;
import com.kiwisoft.utils.xml.XMLUtils;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.4 $, $Date: 2006/03/24 17:54:49 $
 */
public class XMLExporter extends AbstractExporter
{
    private static Exporter instance;

    public static Exporter getInstance()
    {
        if (instance==null) instance=new XMLExporter();
        return instance;
    }

    private XMLExporter()
    {
    }

    public String getName()
    {
        return "XML";
    }

    public String getIcon()
    {
        return "xmlFile";
    }

    public FileFilter getFileFilter()
    {
        return new RegularFileFilter("*.xml", "XML Files");
    }

    protected void writeFileHeader(PrintWriter out)
    {
        out.println("<?xml version=\"1.0\"?>");
        out.println("<table>");
    }

    protected void writeFileFooter(PrintWriter out)
    {
        out.println("</table>");
    }

    protected void openTableHeader(PrintWriter out)
    {
    }

    protected void closeTableHeader(PrintWriter out)
    {
    }

    protected void writeTableHeader(PrintWriter out, String name)
    {
    }

    protected void openTableRow(PrintWriter out)
    {
        out.println("\t<row>");
    }

    protected void closeTableRow(PrintWriter out)
    {
        out.println("\t</row>");
    }

    protected void writeTableCell(PrintWriter out, File file, String columnName, Object cellValue, int renderer, int alignment)
    {
        String cellText=XMLUtils.toXMLString(String.valueOf(cellValue));
        out.println("\t\t<"+columnName+">"+cellText+"</"+columnName+">");
    }

}
