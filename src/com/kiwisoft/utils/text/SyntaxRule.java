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

import com.kiwisoft.utils.xml.XMLContext;
import com.kiwisoft.utils.xml.XMLAdapter;

/**
 * A parser rule.
 *
 * @author Mike Dillon
 * @author Mark Soderquist
 * @author Stefan Stiller
 * @version $Revision: 1.3 $, $Date: 2006/03/24 18:02:55 $
 */
public class SyntaxRule extends XMLAdapter
{
	private char[] characters;
	private int[] sequenceLengths;
	private int type;
	private byte style;
	private SyntaxRule nextRule;

	public SyntaxRule(String[] strings, int type, byte styleId)
	{
		int[] lengths;
		char[] chars;

		if (strings==null)
		{
			lengths=new int[0];
			chars=new char[0];
		}
		else
		{
			lengths=new int[strings.length];
			int charCount=0;
			for (int i=0; i<strings.length; i++)
			{
				if (strings[i]!=null) lengths[i]=strings[i].length();
				charCount+=lengths[i];
			}

			chars=new char[charCount];
			int offset=0;
			for (int i=0; i<strings.length; i++)
			{
				if (strings[i]!=null)
				{
					System.arraycopy(strings[i].toCharArray(), 0, chars, offset, lengths[i]);
					offset+=lengths[i];
				}
			}
		}

		characters=chars;
		sequenceLengths=lengths;
		this.type=type;
		style=styleId;
	}

	// Getter and Setter

	public char[] getCharacters()
	{
		return characters;
	}

	public int getLength(int index)
	{
		return sequenceLengths[index];
	}

	public int getOffset(int index)
	{
		int offset=0;
		for (int i=0; i<index; i++) offset+=sequenceLengths[i];
		return offset;
	}

	public int getType()
	{
		return type;
	}

	public byte getStyle()
	{
		return style;
	}

	public SyntaxRule getNextRule()
	{
		return nextRule;
	}

	public void setNextRule(SyntaxRule rule)
	{
		nextRule=rule;
	}

	public boolean isNotDelegated()
	{
		return (type&SyntaxParser.DELEGATE)!=SyntaxParser.DELEGATE;
	}

	public boolean isWhiteSpace()
	{
		return (type&SyntaxParser.WHITESPACE)==SyntaxParser.WHITESPACE;
	}

	// XMLObject interface

	public SyntaxRule(XMLContext context, String name)
	{
		super(context, name);
	}

	public void setXMLAttribute(XMLContext context, String name, String value)
	{
		if ("noLineBreak".equalsIgnoreCase(name))
		{
			if (Boolean.valueOf(value).booleanValue()) type|=SyntaxParser.NO_LINE_BREAK;
		}
		else if ("noWordBreak".equalsIgnoreCase(name))
		{
			if (Boolean.valueOf(value).booleanValue()) type|=SyntaxParser.NO_WORD_BREAK;
		}
		else if ("lineStart".equalsIgnoreCase(name))
		{
			if (Boolean.valueOf(value).booleanValue()) type|=SyntaxParser.AT_LINE_START;
		}
		else if ("exclude".equalsIgnoreCase(name))
		{
			if (Boolean.valueOf(value).booleanValue()) type|=SyntaxParser.EXCLUDE_MATCH;
		}
	}

	public void setXMLContent(XMLContext context, String value)
	{
		if (value!=null)
		{
            setCharacters(value);
        }
	}

    public void setCharacters(String value)
    {
        characters=value.toCharArray();
        sequenceLengths=new int[]{value.length()};
    }

    public static SyntaxRule createWhitespace(String characters)
    {
        SyntaxRule rule=new SyntaxRule(null, SyntaxParser.WHITESPACE, Style.DEFAULT);
        rule.setCharacters(characters);
        return rule;
    }

    public static SyntaxRule createSequence(byte style, String characters)
    {
        SyntaxRule rule=new SyntaxRule(null, 0, style);
        rule.setCharacters(characters);
        return rule;
    }
}
