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
package com.kiwisoft.sqlPlugin.config;

import org.jdom.Element;

import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.JDOMExternalizable;
import com.intellij.openapi.util.WriteExternalException;

import com.kiwisoft.utils.idea.PluginUtils;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.3 $, $Date: 2006/03/24 17:59:52 $
 */
public class HTMLExportConfiguration implements JDOMExternalizable
{
	private String tableStyle="border: 1px solid black; border-spacing:0px";
	private String headerStyle="background: darkgrey; font-weight:bold; text-align: center";
	private String rowStyle1="background: white";
	private String rowStyle2="background: lightgrey";
	private String cellStyle="border: 1px solid black";
	private boolean alternateRows=true;
	private int alternateInterval=1;
	private boolean includeQuery=true;

	HTMLExportConfiguration()
	{
	}

	public String getTableStyle()
	{
		return tableStyle;
	}

	public void setTableStyle(String tableStyle)
	{
		this.tableStyle=tableStyle;
	}

	public String getHeaderStyle()
	{
		return headerStyle;
	}

	public void setHeaderStyle(String headerStyle)
	{
		this.headerStyle=headerStyle;
	}

	public String getRowStyle1()
	{
		return rowStyle1;
	}

	public void setRowStyle1(String rowStyle1)
	{
		this.rowStyle1=rowStyle1;
	}

	public String getRowStyle2()
	{
		return rowStyle2;
	}

	public void setRowStyle2(String rowStyle2)
	{
		this.rowStyle2=rowStyle2;
	}

	public String getCellStyle()
	{
		return cellStyle;
	}

	public void setCellStyle(String cellStyle)
	{
		this.cellStyle=cellStyle;
	}

	public boolean isAlternateRows()
	{
		return alternateRows;
	}

	public void setAlternateRows(boolean alternateRows)
	{
		this.alternateRows=alternateRows;
	}

	public int getAlternateInterval()
	{
		return alternateInterval;
	}

	public void setAlternateInterval(int alternateInterval)
	{
		this.alternateInterval=alternateInterval;
	}

	public boolean isIncludeQuery()
	{
		return includeQuery;
	}

	public void setIncludeQuery(boolean includeQuery)
	{
		this.includeQuery=includeQuery;
	}


	public void readExternal(Element element) throws InvalidDataException
	{
		setTableStyle(element.getAttributeValue("table"));
		setCellStyle(element.getAttributeValue("cell"));
		setHeaderStyle(element.getAttributeValue("header"));
		setRowStyle1(element.getAttributeValue("row1"));
		setRowStyle2(element.getAttributeValue("row2"));
		String attributeValue=element.getAttributeValue("alternate");
		setAlternateRows("true".equals(attributeValue));
		setAlternateInterval(PluginUtils.getInteger(element, "interval", new Integer(1)).intValue());
		setIncludeQuery(PluginUtils.getBoolean(element, "includeQuery", true));
	}

	public void writeExternal(Element element) throws WriteExternalException
	{
		element.setAttribute("table", getTableStyle());
		element.setAttribute("cell", getCellStyle());
		element.setAttribute("header", getHeaderStyle());
		element.setAttribute("row1", getRowStyle1());
		element.setAttribute("row2", getRowStyle2());
		element.setAttribute("alternate", String.valueOf(isAlternateRows()));
		element.setAttribute("interval", String.valueOf(getAlternateInterval()));
		element.setAttribute("includeQuery", String.valueOf(isIncludeQuery()));
	}
}
