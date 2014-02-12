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
package com.kiwisoft.utils.xml;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.3 $, $Date: 2006/03/24 17:48:08 $
 */
public class XMLAdapter implements XMLObject
{
	public XMLAdapter()
	{
	}

	/** @noinspection UNUSED_SYMBOL*/
	public XMLAdapter(XMLContext dummy,String aName)
	{
	}

	public void setXMLAttribute(XMLContext context, String name, String value)
	{
	}

	public void setXMLReference(XMLContext context, String name, Object value)
	{
	}

	public void setXMLContent(XMLContext context, String value)
	{
	}

	public void addXMLElement(XMLContext context, XMLObject element)
	{
	}
}
