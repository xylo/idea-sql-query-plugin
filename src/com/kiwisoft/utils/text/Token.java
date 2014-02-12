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

/**
 * Represent syntax tokens.
 *
 * @author Slava Pestov
 * @author Mark Soderquist
 * @author Stefan Stiller
 * @version $Revision: 1.3 $, $Date: 2006/03/24 18:02:55 $
 */
public class Token implements Cloneable
{
	/**
	 * The length of this token.
	 */
	private int length;

	/**
	 * The id of this token.
	 */
	private byte style;

	/**
	 * The rule set of this token.
	 */
	private SyntaxRules rules;

	/**
	 * The next token in the linked list.
	 */
	private Token nextToken;

	/**
	 * Creates a new token.
	 *
	 * @param length The length of the token
	 * @param id The id of the token
	 * @param rules The parser rule set that generated this token
	 */
	public Token(int length, byte id, SyntaxRules rules)
	{
		this.length=length;
		style=id;
		this.rules=rules;
	}

	/**
	 * Create a clone of this token.
	 */
	public Object clone()
	{
		return new Token(length, style, rules);
	}

	/**
	 * Returns a string representation of this token.
	 */
	public String toString()
	{
		return "[id="+style+",length="+length+"]";
	}

	public int getLength()
	{
		return length;
	}

	public void setLength(int length)
	{
		this.length=length;
	}

	public byte getStyle()
	{
		return style;
	}

	public void setStyle(byte style)
	{
		this.style=style;
	}

	public SyntaxRules getRules()
	{
		return rules;
	}

	public void setRules(SyntaxRules rules)
	{
		this.rules=rules;
	}

	public Token getNextToken()
	{
		return nextToken;
	}

	public void setNextToken(Token nextToken)
	{
		this.nextToken=nextToken;
	}

}
