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

import java.io.IOException;
import java.io.StringWriter;

/**
 * Utilities which can be used while working with XML documents.
 *
 * @author Stefan Stiller
 * @version $Revision: 1.5 $, $Date: 2006/03/24 17:44:16 $
 */
public final class XMLUtils
{
	private XMLUtils()
	{
	}

	/**
	 * Converts a text into a XML string by converting special characters
	 * into where corresponding entities.
	 *
	 * @param text The string to convert.
	 * @return The converted string.
	 */
	public static String toXMLString(String text)
	{
		StringBuffer buffer=new StringBuffer();
		int len=text.length();
		for (int i=0; i<len; i++)
		{
			char ch=text.charAt(i);
			if ((ch<0x0020) || (ch>0x007e))
			{
				buffer.append("&#");
				buffer.append((int)ch);
				buffer.append(";");
			}
			else if (isSpecial(ch)) buffer.append(getSpecialEntity(ch));
			else buffer.append(ch);
		}
		return buffer.toString();
	}

	public static boolean isXMLName(String text)
	{
		if (text==null || text.length()<=0) return false;
		if (!Character.isLetter(text.charAt(0))) return false;
		for (int i=0; i<text.length(); i++)
		{
			char ch=text.charAt(i);
			if (Character.isLetter(ch)) continue;
			if (Character.isDigit(ch)) continue;
			if (ch=='.' || ch==':' || ch=='_' || ch=='-') continue;
			return false;
		}
		return true;
	}

	/**
	 * Checks if this character is a special character.
	 *
	 * @param ch The character.
	 * @return Returns <code>true</code> if the character is '&amp;', '&quot;', '&lt;' or '&gt;'
	 */
	public static boolean isSpecial(char ch)
	{
		if (ch=='&') return true;
		if (ch=='"') return true;
		if (ch=='<') return true;
		return ch=='>';
	}

	/**
	 * Returns the entity value for special characters.
	 *
	 * @param ch The character.
	 * @return The entity for this character.
	 */
	public static String getSpecialEntity(char ch)
	{
		if (ch=='&') return "&amp;";
		if (ch=='"') return "&quot;";
		if (ch=='<') return "&lt;";
		if (ch=='>') return "&gt;";
		return null;
	}

	public static String toXML(XMLWritable xmlWritable)
	{
		StringWriter stringWriter=new StringWriter();
		XMLWriter writer=new XMLWriter(stringWriter, null);
		try
		{
			writer.start();
			xmlWritable.writeXML(writer);
			writer.close();
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
		return stringWriter.getBuffer().toString();
	}

}