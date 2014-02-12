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
public class CSVExportConfiguration implements JDOMExternalizable
{
	CSVExportConfiguration()
	{
	}

	private String delimiter=",";

	public String getDelimiter()
	{
		return delimiter;
	}

	public void setDelimiter(String delimiter)
	{
		this.delimiter=delimiter;
	}

	private String textQualifier="\"";

	public String getTextQualifier()
	{
		return textQualifier;
	}

	public void setTextQualifier(String textQualifier)
	{
		this.textQualifier=textQualifier;
	}

	private boolean forceQualifier;

	public boolean isForceQualifier()
	{
		return forceQualifier;
	}

	public void setForceQualifier(boolean forceQualifier)
	{
		this.forceQualifier=forceQualifier;
	}

	public void readExternal(Element element) throws InvalidDataException
	{
		setDelimiter(element.getAttributeValue("delimiter"));
		setTextQualifier(element.getAttributeValue("text"));
		setForceQualifier(PluginUtils.getBoolean(element, "forceQualifier", false));
	}

	public void writeExternal(Element element) throws WriteExternalException
	{
		element.setAttribute("delimiter", getDelimiter());
		element.setAttribute("text", getTextQualifier());
		element.setAttribute("forceQualifier", Boolean.toString(isForceQualifier()));
	}
}
