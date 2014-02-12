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
package com.kiwisoft.db.sql;

import java.util.*;

import com.kiwisoft.utils.StringUtils;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.3 $, $Date: 2006/03/24 17:55:46 $
 */
public class TokenList
{
    private LinkedList tokens;
    private boolean closed;
    private Stack brackets;
    private int offset;
    private int length;

    public TokenList(Token token)
    {
        tokens=new LinkedList();
        brackets=new Stack();
        addToken(token);
    }

    public void addToken(Token token)
    {
        if (isClosed()) throw new RuntimeException("TokenList is already closed");

        if (tokens.isEmpty()) offset=token.getOffset();
        length+=token.getLength();
        tokens.add(token);

        if (token instanceof SeparatorToken)
        {
            SeparatorToken separator=(SeparatorToken) token;
            String sep=separator.getText();
            if ("(".equals(sep)) brackets.push(separator);
            else if (")".equals(sep))
            {
				if (!brackets.isEmpty())
				{
	                SeparatorToken separator2=(SeparatorToken)brackets.peek();
    	            if ("(".equals(separator2.getText())) brackets.pop();
				}
				else throw new RuntimeException("Missing left parenthis.");
            }
            else if (";".equals(sep) || "/".equals(sep))
            {
                if (brackets.size()==0)
                {
                    closed=true;
                    tokens.removeLast();
                    brackets=null;
                }
            }
        }
    }

    public List getTokens()
    {
        return tokens;
    }

    public int getOffset()
    {
        return offset;
    }

    public int getLength()
    {
        return length;
    }

    public boolean isClosed()
    {
        return closed;
    }

    public String getNormalizedText()
    {
        StringBuffer text=new StringBuffer();
        Iterator it=tokens.iterator();
		boolean space=false;
        while (it.hasNext())
        {
            Token token=(Token)it.next();
			if (token instanceof SpaceToken) space=true;
			else if (!(token instanceof CommentToken))
			{
				if (space) text.append(" ");
            	text.append(token.getNormalizedText());
				space=false;
			}
        }
        return StringUtils.trim(text.toString());
    }

    public String getText()
    {
        StringBuffer text=new StringBuffer();
        Iterator it=tokens.iterator();
        while (it.hasNext())
		{
			Token token=(Token)it.next();
			text.append(token.getText());
		}
        return text.toString();
    }

    public String toString()
    {
        return getText();
    }

	public static List trim(List tokens)
	{
		if (tokens.isEmpty()) return tokens;
		int start=0;
		while (start<tokens.size() && tokens.get(start) instanceof SpaceToken) start++;
		int end=tokens.size()-1;
		while (end>=0 && tokens.get(end) instanceof SpaceToken) end--;
		if (end>=start) return tokens.subList(start, end+1);
		return Collections.EMPTY_LIST;
	}

	public List trim()
	{
		return trim(tokens);
	}

	public Token getFirstToken()
	{
		for (Iterator it=tokens.iterator(); it.hasNext();)
		{
			Token token=(Token)it.next();
			if (token instanceof SpaceToken || token instanceof CommentToken) continue;
			return token;
		}
		return null;
	}

	public SQLStatement analyze()
	{
		Token token=getFirstToken();
		if (token!=null)
		{
			String tokenName=token.getNormalizedText();
			if ("describe".equals(tokenName)) return new DescribeStatement(this);
			else if ("@".equals(tokenName)) return new TemplateStatement(this);
		}
		return new DefaultSQLStatement(this);
	}
}
