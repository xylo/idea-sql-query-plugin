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

import java.util.*;
import java.io.IOException;

/**
 * Default implementation of the interface <code>{@link XMLObject}</code>.
 *
 * @author Stefan Stiller
 * @version $Revision: 1.5 $, $Date: 2006/03/24 17:48:08 $
 *
 * @see XMLHandler
 */
public class DefaultXMLObject extends XMLAdapter implements XMLWritable
{
	/** A map containing all attributes of this tag and their values. */
	private HashMap attributes;
	/** A list containing references to all included elements of this tag. */
	private LinkedList elements;
	/** The name of the tag. */
	private String name;
	/** The content of the tag. */
	private String content;

	/**
	 * Constructor used by the XMLHandler.
	 *
	 * @param dummy Auxilary argument to help the XMLHandler identifying the correct constructor.
	 * @param aName The name of the tag.
	 */
	public DefaultXMLObject(XMLContext dummy,String aName)
	{
		super(dummy, aName);
		name=aName;
		attributes=new HashMap();
		elements=new LinkedList();
	}

	/**
	 * Method called by the XMLHandler to set an attribute.
	 *
	 * @param name The name of the attribute.
	 * @param value The value of the attribute.
	 */
	public void setXMLAttribute(XMLContext context, String name,String value)
	{
		attributes.put(name,value);
	}

	public void setXMLReference(XMLContext context, String name,Object value)
	{
		attributes.put(name,value);
	}

	/**
	 * Method called by the XMLHandler to set the content of this element.
	 *
	 * @param value The content of the element.
	 */
	public void setXMLContent(XMLContext context, String value)
	{
		content=value;
	}

	/**
	 * Method called by the XMLHandler to add an included element.
	 *
	 * @param element The included element.
	 */
	public void addXMLElement(XMLContext context, XMLObject element)
	{
		elements.add(element);
	}

	/**
	 * Returns the name of the element.
	 *
	 * @return The name of the element.
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Returns the content of the element.
	 *
	 * @return The content of the element.
	 */
	public String getContent()
	{
		return content;
	}

	/**
	 * Returns the value of the attribute with a given name.
	 *
	 * @param aName The name of the attribute.
	 * @return The value of the attribute or null if the attribute could not be found.
	 */
	public String getAttribute(String aName)
	{
		return (String)attributes.get(aName);
	}

	/**
	 * Returns a list containing all sub-elements .
	 *
	 * @return The list with the elements. If no elements are found an empty list will be returned.
	 */
	public List getElements()
	{
		return elements;
	}

	public void writeXML(XMLWriter writer) throws IOException
	{
		writer.startElement(name);
		for (Iterator it=attributes.entrySet().iterator(); it.hasNext();)
		{
			Map.Entry entry=(Map.Entry)it.next();
			writer.setAttribute(String.valueOf(entry.getKey()), String.valueOf(entry.getValue()));
		}
		for (Iterator it=elements.iterator(); it.hasNext();)
		{
			XMLObject element=(XMLObject)it.next();
			if (element instanceof XMLWritable)
				((XMLWritable)element).writeXML(writer);
			else
			{
				writer.startElement(element.getClass().getName());
				writer.closeElement(element.getClass().getName());
			}
		}
		writer.closeElement(name);
	}

	public String toString()
	{
		return XMLUtils.toXML(this);
	}
}
