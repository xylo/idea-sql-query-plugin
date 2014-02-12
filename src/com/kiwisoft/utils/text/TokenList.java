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
 * Encapsulates a token list.
 *
 * @author Stefan Stiller
 * @version $Revision: 1.3 $, $Date: 2006/03/24 18:02:55 $
 */
public class TokenList
{
	private Token firstToken;
	private Token lastToken;

	/**
	 * Returns the first syntax token.
	 */
	public Token getFirstToken()
	{
		return firstToken;
	}

	/**
	 * Add a token.
	 */
	void addToken(int length, byte id, SyntaxRules rules)
	{
		if (length==0 && id!=Style.END) return;

		if (firstToken==null)
		{
			firstToken=new Token(length, id, rules);
			lastToken=firstToken;
		}
		else if (lastToken==null)
		{
			lastToken=firstToken;
			firstToken.setLength(length);
			firstToken.setStyle(id);
			firstToken.setRules(rules);
		}
		else if (lastToken.getStyle()==id && lastToken.getRules()==rules)
		{
			lastToken.setLength(lastToken.getLength()+length);
		}
		else if (lastToken.getNextToken()==null)
		{
			lastToken.setNextToken(new Token(length, id, rules));
			lastToken=lastToken.getNextToken();
		}
		else
		{
			lastToken=lastToken.getNextToken();
			lastToken.setLength(length);
			lastToken.setStyle(id);
			lastToken.setRules(rules);
		}
	}
}
