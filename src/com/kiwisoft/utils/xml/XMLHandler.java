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

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.StringTokenizer;

import org.apache.xerces.parsers.SAXParser;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Implementation of {@link org.xml.sax.ContentHandler} which creates
 * objects while parsing a XML document. Use the method addTagMapping
 * to map a tag to a class implementing the interface {@link XMLObject}.
 *
 * @author Stefan Stiller
 * @version $Revision: 1.4 $, $Date: 2006/03/24 17:48:08 $
 * @see org.apache.xerces.parsers.SAXParser
 * @todo Use context object for name spaces
 */
public class XMLHandler extends DefaultHandler
{
	/** The content between a start tag and an end tag. */
	private StringBuffer text;
	/** The mapping from tag names to classes. */
	private Map tagMap;
	private Map tagHandlers;
	/** The rootElement of the XML document. */
	private XMLObject rootElement;
	/** The current stack of xml tags. The current tag are at the top of the stack, the root tag at the bottom. */
	private Stack elementStack;

	private Map idAttributes;
	private Map refAttributes;
	private ObjectMap objectMap;
	private Set references;
	private XMLContext context;

	private boolean trimText=true;

	/**
	 * Creates a new XMLHandler with no mappings.
	 */
	public XMLHandler()
	{
		tagMap=new HashMap();
		elementStack=new Stack();
		idAttributes=new HashMap();
		refAttributes=new HashMap();
		objectMap=new ObjectMap();
		references=new HashSet();
		tagHandlers=new HashMap();
		context=new XMLContext(this);
	}

	public void reset()
	{
		elementStack.clear();
		objectMap.clear();
		references.clear();
		rootElement=null;
		text=null;
		context=new XMLContext(this);
	}

	public void setTrimText(boolean trimText)
	{
		this.trimText=trimText;
	}

	/**
	 * Maps the name of a tag to a class implementing the interface XMLObject.
	 *
	 * @param tagName The name of tag.
	 * @param aClass The class of the objects which should be created from this tag.
	 * @exception IllegalArgumentException If the class doesn't implement the
	 * interface XMLObject.
	 * @see XMLObject
	 */
	public void addTagMapping(String tagName, Class aClass)
	{
		if (!XMLObject.class.isAssignableFrom(aClass)) throw new IllegalArgumentException("The class '"+aClass.getName()+"' has to implement the interface XMLObject.");
		{
			tagMap.put(tagName, aClass);
		}
	}

	public void addTagMapping(String tagName, XMLObject object)
	{
		tagMap.put(tagName, object);
	}

	public void addTagMapping(String tagName, XMLObjectFactory factory)
	{
		tagMap.put(tagName, factory);
	}

	public void addTagHandler(String name, XMLTagHandler handler)
	{
		tagHandlers.put(name, handler);
	}

	public void addIdAttribute(String tag, String attr, String idMap)
	{
		idAttributes.put(tag+"."+attr, idMap);
	}

	public void removeIdAttribute(String tag, String attr)
	{
		idAttributes.remove(tag+"."+attr);
	}

	public void addRefAttribute(String tag, String attr, String idMap)
	{
		refAttributes.put(tag+"."+attr, idMap);
	}

	public void removeRefAttribute(String tag, String attr)
	{
		refAttributes.remove(tag+"."+attr);
	}

	/**
	 * Returns the root element of the XML document after the document
	 * was parsed.
	 *
	 * @return The root element.
	 */
	public XMLObject getRootElement()
	{
		return rootElement;
	}

	/**
	 * Method called by the SAXParser if a new start tag was found.
	 * If a class for this tag is found in the tag mappings an instance of this
	 * class is created. If no mapping can be found an instance of
	 * {@link DefaultXMLObject} is created. After the creating the attributes
	 * of the tag are passed to the instance by using the
	 * method {@link XMLObject#setXMLAttribute(XMLContext, String, String)}.
	 *
	 * @param uri The URI of the namespace of this tag.
	 * @param localName The name of the tag without a prefix.
	 * @param rawName The name of the tag with the namespace prefix.
	 * @param attributes The attributes of this tag.
	 * @see org.xml.sax.ContentHandler
	 */
	public void startElement(String uri, String localName, String rawName, Attributes attributes) throws SAXException
	{
		XMLTagHandler tagHandler=(XMLTagHandler) tagHandlers.get(localName);
		if (tagHandler==null)
		{
			try
			{
				text=null;
				XMLObject element=createElement(localName, attributes);
				if (rootElement==null) rootElement=element;
				if (attributes!=null)
				{
					for (int i=0; i<attributes.getLength(); i++)
					{
						String attrName=attributes.getLocalName(i);
						String attrValue=attributes.getValue(i);
						String idAttrMap=(String)idAttributes.get(localName+"."+attrName);
						if (idAttrMap!=null) objectMap.put(idAttrMap, attrValue, element);
						String refAttrMap=(String)refAttributes.get(localName+"."+attrName);
						if (refAttrMap!=null) references.add(new XMLReference(element, refAttrMap, attrName, attrValue));
						element.setXMLAttribute(context, attrName, attrValue);
					}
				}
				elementStack.push(element);
			}
			catch (Exception e)
			{
				throw new XMLException(e);
			}
		}
		else
		{
			String replace=tagHandler.startTag(context, uri, localName, rawName, attributes);
			if (replace!=null) appendText(replace);
		}
	}

	/**
	 * Creates an instance of XMLObject for a tag with a given name.
	 * We first look for a class mapping of this tag. If one is found we use the
	 * constructor (XMLDummy,String) to create an instance of this class. If no
	 * mapping was found an instance of DefaultXMLObject is created.
	 *
	 * @param elementName The name of the tag.
	 * @return An instance of XMLObject if an object could be created or null.
	 */
	private XMLObject createElement(String elementName, Attributes attributes)
	{
		try
		{
			if (tagMap.containsKey(elementName))
				return createElementFromMapping(tagMap.get(elementName), elementName, attributes);
			if (tagMap.containsKey("*"))
				return createElementFromMapping(tagMap.get("*"), elementName, attributes);
			return new DefaultXMLObject(context, elementName);
		}
		catch (Exception e)
		{
			throw new XMLException(e);
		}
	}

	private XMLObject createElementFromMapping(Object mapping, String elementName, Attributes attributes) throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException
	{
		if (mapping instanceof Class)
		{
			Constructor constructor=((Class)mapping).getConstructor(new Class[]{XMLContext.class, String.class});
			return (XMLObject)constructor.newInstance(new Object[]{context, elementName});
		}
		else if (mapping instanceof XMLObjectFactory)
		{
			XMLObjectFactory factory=(XMLObjectFactory)mapping;
			return factory.createElement(context, elementName, attributes);
		}
		else
		{
			return (XMLObject)mapping;
		}
	}

	/**
	 * Method called by the SAXParser if an end tag was found.
	 * The content of the tag will passed to the object. If this element
	 * is not the root element the object associated with this tag will be added
	 * with the method {@link XMLObject#addXMLElement(XMLContext, XMLObject)} to its parent.
	 *
	 * @param uri The URI of the namespace of this tag.
	 * @param localName The name of the tag without a prefix.
	 * @param rawName The name of the tag with the namespace prefix.
	 * @see org.xml.sax.ContentHandler
	 */
	public void endElement(String uri, String localName, String rawName) throws SAXException
	{
		XMLTagHandler tagHandler=(XMLTagHandler) tagHandlers.get(localName);
		if (tagHandler==null)
		{
			try
			{
				XMLObject element=(XMLObject)elementStack.pop();
				element.setXMLContent(context, getText());
				if (!elementStack.empty())
				{
					XMLObject superElement=(XMLObject)elementStack.peek();
					superElement.addXMLElement(context, element);
				}
			}
			catch (Exception e)
			{
				throw new XMLException(e);
			}
			text=null;
		}
		else
		{
			String replace=tagHandler.endTag(context, uri, localName, rawName);
			if (replace!=null) appendText(replace);
		}
	}

	public void endDocument() throws SAXException
	{
		try
		{
			Iterator it=references.iterator();
			while (it.hasNext())
			{
				XMLReference ref=(XMLReference)it.next();
				StringTokenizer tokens=new StringTokenizer(ref.attrValue, ",");
				while (tokens.hasMoreTokens())
				{
					String token=tokens.nextToken().trim();
					XMLObject object=objectMap.get(ref.idMap, token);
					if (object!=null)
						ref.element.setXMLReference(context, ref.attrName, object);
					else
						throw new SAXException("No object with id="+token+" found.");
				}
			}
		}
		catch (SAXException e)
		{
			throw new XMLException(e);
		}
	}

	/**
	 * Returns the content of an element. Leading or trailing spaces while
	 * be removed.
	 *
	 * @return The content of the current element.
	 */
	private String getText()
	{
		if (text!=null)
		{
			if (trimText) return text.toString().trim();
			else return text.toString();
		}
		else
			return null;
	}

	/**
	 * Method called by the SAXParser if character data were found.
	 *
	 * @param ch - The characters.
	 * @param start - The start position in the character array.
	 * @param length - The number of characters to use from the character array.
	 * @see org.xml.sax.ContentHandler
	 */
	public void characters(char[] ch, int start, int length)
	{
		appendText(new String(ch, start, length));
	}

	private void appendText(String value)
	{
        if (text==null) text=new StringBuffer();
		text.append(value);
	}

	public XMLObject loadFile(File file)
	{
		context.setFileName(file.getAbsolutePath());
		try
		{
			InputStream is=new FileInputStream(file);
			SAXParser parser=new SAXParser();
			parser.setContentHandler(this);
			parser.parse(new InputSource(is));
			is.close();
			return getRootElement();
		}
		catch (Exception e)
		{
			throw new XMLException(e);
		}
	}

	public XMLObject loadFile(String fileName)
	{
		context.setFileName(fileName);
		try
		{
			InputStream is=new FileInputStream(fileName);
			SAXParser parser=new SAXParser();
			parser.setContentHandler(this);
			parser.parse(new InputSource(is));
			is.close();
			return getRootElement();
		}
		catch (Exception e)
		{
			throw new XMLException(e);
		}
	}

	public XMLObject loadStream(InputStream is)
	{
		try
		{
			SAXParser parser=new SAXParser();
			parser.setContentHandler(this);
			parser.parse(new InputSource(is));
			is.close();
			return getRootElement();
		}
		catch (Exception e)
		{
			throw new XMLException(e);
		}
	}

	public XMLObject loadStream(Reader reader)
	{
		try
		{
			SAXParser parser=new SAXParser();
			parser.setContentHandler(this);
			parser.parse(new InputSource(reader));
			reader.close();
			return getRootElement();
		}
		catch (Exception e)
		{
			throw new XMLException(e);
		}
	}

	private static class XMLReference
	{
		public XMLObject element;
		public String attrName;
		public String attrValue;
		public String idMap;

		public XMLReference(XMLObject anElement, String anIdMap, String anAttrName, String anAttrValue)
		{
			element=anElement;
			idMap=anIdMap;
			attrName=anAttrName;
			attrValue=anAttrValue;
		}
	}

	private static class ObjectMap extends HashMap
	{
		private XMLObject get(String idName, String idValue)
		{
			Map map=(Map)get(idName);
			if (map!=null)
				return (XMLObject)map.get(idValue);
			else
				return null;
		}

		private void put(String idName, String idValue, XMLObject value)
		{
			Map map=(Map)get(idName);
			if (map==null)
			{
				map=new HashMap();
				put(idName, map);
			}
			map.put(idValue, value);
		}
	}

	public XMLContext getContext()
	{
		return context;
	}
}
