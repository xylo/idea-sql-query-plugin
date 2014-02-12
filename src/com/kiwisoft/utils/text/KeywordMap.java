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
package com.kiwisoft.utils.text;

import javax.swing.text.Segment;

/**
 * A <code>KeywordMap</code> is similar to a hashtable in that it maps keys
 * to values. However, the keys are Swing segments. This allows lookups of
 * text substrings without the overhead of creating a new string object.
 *
 * @author Slava Pestov
 * @author Mike Dillon
 * @author Mark Soderquist
 * @author Stefan Stiller
 * @version $Revision: 1.3 $, $Date: 2006/03/24 18:50:49 $
 */
public class KeywordMap
{
	private final static int MAP_SIZE=52;

	private Keyword[] keywords;
	private boolean ignoreCase=true;

	/**
	 * Create a new <code>KeywordMap</code>.
	 */
	public KeywordMap()
	{
		keywords=new Keyword[MAP_SIZE];
	}

	/**
	 * Look up a key.
	 *
	 * @param segment The text segment.
	 * @param offset The offset of the substring within the text segment.
	 * @param length The length of the substring.
	 */
	public byte lookup(Segment segment, int offset, int length)
	{
		if (length==0) return Style.DEFAULT;
		Keyword keyword=keywords[getSegmentMapKey(segment, offset, length)];
		while (keyword!=null)
		{
			if (length!=keyword.chars.length)
			{
				keyword=keyword.next;
				continue;
			}
			if (regionMatches(ignoreCase, segment, offset, keyword.chars))
			{
				return keyword.style;
			}
			keyword=keyword.next;
		}
		return Style.DEFAULT;
	}

	/**
	 * Adds a key-value mapping.
	 *
	 * @param keyword The key
	 * @param style The value
	 */
	public void add(String keyword, byte style)
	{
		int key=getStringMapKey(keyword);
		keywords[key]=new Keyword(keyword, style, keywords[key]);
	}

	/**
	 * Sets if the keyword maKeywords should be case insensitive.
	 * @param newValue True if the keyword maKeywords should be case
	 * insensitive, false otherwise.
	 */
	public void setIgnoreCase(boolean newValue)
	{
		ignoreCase=newValue;
	}

	private int getStringMapKey(String string)
	{
		char firstChar=Character.toUpperCase(string.charAt(0));
		char lastChar=Character.toUpperCase(string.charAt(string.length()-1));
		return (firstChar+lastChar)%MAP_SIZE;
	}

	private int getSegmentMapKey(Segment segment, int offset, int length)
	{
		char firstChar=Character.toUpperCase(segment.array[offset]);
		char lastChar=Character.toUpperCase(segment.array[offset+length-1]);
		return (firstChar+lastChar)%MAP_SIZE;
	}

	/**
	 * Checks if a subregion of a <code>Segment</code>
	 * is equal to a character array.
	 *
	 * @param ignoreCase True if case should be ignored, false otherwise
	 * @param text The segment
	 * @param offset The offset into the segment
	 * @param match The character array to match
	 */
	private static boolean regionMatches(boolean ignoreCase, Segment text, int offset, char[] match)
	{
		int length=offset+match.length;
		char[] textArray=text.array;
		if (length>text.offset+text.count) return false;
		for (int i=offset, j=0; i<length; i++, j++)
		{
			char c1=textArray[i];
			char c2=match[j];
			if (ignoreCase)
			{
				c1=Character.toUpperCase(c1);
				c2=Character.toUpperCase(c2);
			}
			if (c1!=c2) return false;
		}
		return true;
	}

	private static class Keyword
	{
		public char[] chars;
		public byte style;
		public Keyword next;

		private Keyword(String keyword, byte style, Keyword next)
		{
			this.chars=keyword.toCharArray();
			this.style=style;
			this.next=next;
		}
	}

}
