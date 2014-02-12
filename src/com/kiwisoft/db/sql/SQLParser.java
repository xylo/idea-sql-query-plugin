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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.kiwisoft.db.StatementList;
import com.kiwisoft.utils.StringIterator;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.3 $, $Date: 2006/03/24 17:55:45 $
 */
public class SQLParser
{
	private String statement;
	private List statements;

	public static StatementList parse(String statement)
	{
		List analyzed=new LinkedList();
		analyze(new SQLParser(statement).getStatements(), analyzed);
		return new StatementList(analyzed);
	}

	private static void analyze(List statements, List analyzed)
	{
		if (statements==null) return;
		for (Iterator iterator=statements.iterator(); iterator.hasNext();)
		{
			TokenList tokenList=(TokenList)iterator.next();
			SQLStatement sqlStatement=tokenList.analyze();
			if (sqlStatement instanceof TemplateStatement)
			{
				TemplateStatement template=(TemplateStatement)sqlStatement;
				analyze(new SQLParser(template.getResolvedText()).getStatements(), analyzed);
			}
			else
				analyzed.add(sqlStatement);
		}
	}

	private SQLParser(String statement)
	{
		this.statement=statement;
	}

	private List getStatements()
	{
		if (statements==null)
		{
			List tokens=createTokens(statement);
			statements=createSingleStatements(tokens);
		}
		return statements;
	}

	private List createSingleStatements(List tokens)
	{
		List statements=new LinkedList();
		Iterator it=tokens.iterator();
		TokenList statement=null;
		while (it.hasNext())
		{
			Token token=(Token)it.next();
			if (statement==null || statement.isClosed())
			{
//				if (!(token instanceof SpaceToken))
				{
					statement=new TokenList(token);
					statements.add(statement);
				}
			}
			else
				statement.addToken(token);
		}
		return statements;
	}

	private List createTokens(String statement)
	{
		List tokens=new LinkedList();
		Token token=null;
		StringIterator charIterator=new StringIterator(statement);
		while (charIterator.hasNext())
		{
			int position=charIterator.getPosition();
			char ch=charIterator.next();
			if (token==null || !token.append(ch)) token=startNewToken(token, ch, position, charIterator, tokens);
		}
		return tokens;
	}

	private Token startNewToken(Token previousToken, char ch, int position, StringIterator charIterator, List tokens)
	{
		Token token;
		if (Character.isWhitespace(ch))
			token=new SpaceToken(ch, position);
		else if (Character.isLetterOrDigit(ch) || ch=='_' || ch=='$')
			token=new NameToken(ch, position);
		else if (ch=='\'' || ch=='"')
			token=new StringToken(ch, position);
		else if (ch=='/')
		{
			char next=charIterator.hasNext() ? charIterator.next() : 0;
			if (next=='*')
			{
                token=new CommentToken(ch, position);
				token.append(next);
			}
			else
			{
				if (previousToken instanceof SpaceToken && previousToken.getText().endsWith("\n"))
				{
					token=new SeparatorToken(ch, position);
				}
				else
				{
					token=new SymbolToken(ch, position);
				}
				if (next!=0)
				{
					tokens.add(token);
					token=startNewToken(token, next, position+1, charIterator, tokens);
					return token;
				}
			}
		}
		else if (ch=='@')
			token=new TemplateToken(ch, position);
		else if (ch=='-')
		{
			char next=charIterator.hasNext() ? charIterator.next() : 0;
			if (next=='-')
			{
				token=new CommentToken(ch, position);
				token.append(next);
			}
			else
			{
				token=new SymbolToken(ch, position);
				if (next!=0)
				{
					tokens.add(token);
					token=startNewToken(token, next, position+1, charIterator, tokens);
					return token;
				}
			}
		}
		else if (SQLConstants.isOperatorStart(String.valueOf(ch)))
			token=new SymbolToken(ch, position);
		else if (SQLConstants.isSeparator(String.valueOf(ch)))
			token=new SeparatorToken(ch, position);
		else
			token=new UnknownToken(ch, position);
		tokens.add(token);
		return token;
	}

	static void normalizeList(List tokens)
	{
		if (tokens.isEmpty()) return;
		if (tokens.get(0) instanceof SpaceToken) tokens.remove(0);
		if (tokens.isEmpty()) return;
		int last=tokens.size()-1;
		if (tokens.get(last) instanceof SpaceToken) tokens.remove(last);
	}

	static List splitList(List tokens)
	{
		List list=new LinkedList();
		int start=0;
		int index=0;
		while (index<tokens.size())
		{
			if (tokens.get(index) instanceof SeparatorToken)
			{
				SeparatorToken sep=(SeparatorToken)tokens.get(index);
				if (",".equals(sep.getNormalizedText()))
				{
					List subList=new LinkedList(tokens.subList(start, index));
					normalizeList(subList);
					list.add(subList);
					start=index+1;
				}
			}
			index++;
		}
		if (start<tokens.size())
		{
			List subList=new LinkedList(tokens.subList(start, tokens.size()));
			normalizeList(subList);
			list.add(subList);
		}
		return list;
	}

}
