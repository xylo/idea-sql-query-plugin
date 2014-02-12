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

import java.io.*;
import java.util.Stack;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.5 $, $Date: 2006/03/24 17:46:32 $
 */
public class XMLWriter
{
	private Writer writer;
	private Stack tags;
	private boolean tagOpen;
	private boolean started;
	private boolean empty;
	private boolean content;
	private String encoding;
	private boolean doubleEncoding;
	private Map temporaryIds;
	private long nextTemporaryId;

	public XMLWriter(OutputStream outputStream, String encoding)
	{
		writer=new OutputStreamWriter(outputStream);
		this.encoding=encoding;
		tags=new Stack();
	}

	public XMLWriter(Writer writer, String encoding)
	{
		this.writer=writer;
		this.encoding=encoding;
		tags=new Stack();
	}

	public void setDoubleEncoding(boolean doubleEncoding)
	{
		this.doubleEncoding=doubleEncoding;
	}

	private String encode(String text)
	{
		text=XMLUtils.toXMLString(text);
		if (doubleEncoding) text=XMLUtils.toXMLString(text);
		return text;
	}

	public void start() throws IOException
	{
		if (!started)
		{
			writer.write("<?xml version=\"1.0\"");
			if (encoding!=null) writer.write(" encoding=\""+encoding+"\"");
			writer.write(" ?>\n");
			started=true;
			temporaryIds=new WeakHashMap();
			nextTemporaryId=1;
		}
	}

	public void startElement(String name) throws IOException
	{
		if (!started) throw new RuntimeException("Writer not opened");
		if (!XMLUtils.isXMLName(name)) throw new RuntimeException("Invalid tag name: "+name);
		if (tagOpen) writer.write(">\n");
		printIndent();
		writer.write("<"+name);
		tags.push(name);
		tagOpen=true;
		empty=true;
		content=false;
	}

	public void setAttribute(String name, String value) throws IOException
	{
		if (!tagOpen) throw new RuntimeException("Tag already closed");
		if (value==null) return;
		if (!XMLUtils.isXMLName(name)) throw new RuntimeException("Invalid attribute name: "+name);
		writer.write(" "+name+"=\""+encode(value)+"\"");
	}

	public void setText(String text) throws IOException
	{
		if (!started) throw new RuntimeException("Writer not opened");
		if (!empty) throw new RuntimeException("Text not allowed here");
		if (tagOpen) writer.write(">");
		tagOpen=false;
		content=true;
		if (text!=null)
		{
			writer.write(encode(text));
		}
	}

	public void closeElement(String name) throws IOException
	{
		Object tag=tags.pop();
		if (!name.equals(tag)) throw new RuntimeException("Tag stack error");
		if (tagOpen)
			writer.write("/>\n");
		else
		{
			if (!content) printIndent();
			writer.write("</"+name+">\n");
		}
		tagOpen=false;
		empty=false;
		content=false;
	}

	public void close() throws IOException
	{
		if (!tags.isEmpty()) throw new RuntimeException("Not all elements have been closed");
		started=false;
		writer.flush();
		writer.close();
	}

	private void printIndent() throws IOException
	{
		for (int i=0; i<tags.size(); i++) writer.write("\t");
	}

	public void newLine() throws IOException
	{
		if (!started) throw new RuntimeException("Writer not opened");
		if (tagOpen) writer.write(">\n");
		tagOpen=false;
	}

	public void addComment(String comment) throws IOException
	{
		if (tagOpen)
		{
			writer.write(">\n");
			tagOpen=false;
		}
		content=true;
		printIndent();
		writer.write("<!-- ");
		writer.write(XMLUtils.toXMLString(comment));
		writer.write("-->\n");

	}

	public void addElement(String elementName, String content) throws IOException
	{
		if (content!=null)
		{
			startElement(elementName);
			setText(content);
			closeElement(elementName);
		}
	}

	public void setAttribute(String name, int value) throws IOException
	{
		setAttribute(name, Integer.toString(value));
	}

	public void setAttribute(String name, boolean value) throws IOException
	{
		setAttribute(name, Boolean.toString(value));
	}

	public long getTemporaryId(Object object)
	{
		Long id=(Long)temporaryIds.get(object);
		if (id==null)
		{
			id=new Long(nextTemporaryId++);
			temporaryIds.put(object, id);
		}
		return id.longValue();
	}

}
