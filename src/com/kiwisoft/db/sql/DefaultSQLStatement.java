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

/**
 * @author Stefan Stiller
 * @version $Revision: 1.5 $, $Date: 2006/03/24 17:54:50 $
 */
public class DefaultSQLStatement extends AbstractSQLStatement
{
	private TokenList tokenList;
	private boolean simpleSelect;
	private List columnInfos;
	private NameInfo tableInfo;

	public DefaultSQLStatement(TokenList tokens)
	{
		this.tokenList=tokens;
		columnInfos=new LinkedList();
		try
		{
			parse();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			simpleSelect=false;
		}
	}

	private void parse()
	{
		List tokens=tokenList.trim();
		if (tokens.size()>0 && "select".equals(((Token)tokens.get(0)).getNormalizedText()))
		{
			simpleSelect=true;
			int indexFrom=findFromToken(tokens, 1);
			int indexFromEnd=findFromEndToken(tokens, indexFrom+1);
			if (indexFrom>=0 && indexFromEnd>=0)
			{
				List columnNameTokens=SQLParser.splitList(tokens.subList(1, indexFrom));
				List tableNameTokens=SQLParser.splitList(tokens.subList(indexFrom+1, indexFromEnd));
				if (tableNameTokens.size()!=1)
				{
					simpleSelect=false;
					return;
				}

				Iterator it=columnNameTokens.iterator();
				while (it.hasNext())
				{
					String[] names=analyzeName((List)it.next());
					if (names!=null)
					{
						if (names.length==1)
							columnInfos.add(new NameInfo(names[0], names[0]));
						else
							columnInfos.add(new NameInfo(names[0], names[1]));
					}
					else
					{
						simpleSelect=false;
						return;
					}
				}

				String[] names=analyzeName((List)tableNameTokens.get(0));
				if (names!=null)
				{
					if (names.length==1)
						tableInfo=new NameInfo(names[0], names[0]);
					else
						tableInfo=new NameInfo(names[0], names[1]);
				}
				else
					simpleSelect=false;
			}
			else
				simpleSelect=false;
		}
	}

	private static final Class[] PATTERN_1A=new Class[]{NameToken.class};
	private static final Class[] PATTERN_1B=new Class[]{SymbolToken.class};
	private static final Class[] PATTERN_3A=new Class[]{NameToken.class, SpaceToken.class, NameToken.class};
	private static final Class[] PATTERN_3B=new Class[]{NameToken.class, SpaceToken.class, StringToken.class};
	private static final Class[] PATTERN_5A=new Class[]{NameToken.class, SpaceToken.class, NameToken.class, SpaceToken.class, NameToken.class};
	private static final Class[] PATTERN_5B=new Class[]{NameToken.class, SpaceToken.class, NameToken.class, SpaceToken.class, StringToken.class};

	private String[] analyzeName(List tokens)
	{
		if (tokens.isEmpty()) return null;
		if (matches(tokens, PATTERN_1A))
		{
			String name=getName((Token)tokens.get(0));
			return new String[]{name};
		}
		if (matches(tokens, PATTERN_1B))
		{
			if ("*".equals(tokens.get(0).toString())) return new String[]{"*"};
			return null;
		}
		if (matches(tokens, PATTERN_3A) || matches(tokens, PATTERN_3B))
		{
			String name=getName((Token)tokens.get(0));
			String label=getName((Token)tokens.get(2));
			return new String[]{name, label};
		}
		if (matches(tokens, PATTERN_5A) || matches(tokens, PATTERN_5B))
		{
			if ("as".equals(((Token)tokens.get(2)).getNormalizedText()))
			{
				String name=getName((Token)tokens.get(0));
				String label=getName((Token)tokens.get(4));
				return new String[]{name, label};
			}
			return null;
		}
		return null;
	}

	private boolean matches(List tokens, Class[] classes)
	{
		if (tokens.size()!=classes.length) return false;
		for (int i=0; i<classes.length; i++)
		{
			Class aClass=classes[i];
			if (!aClass.isInstance(tokens.get(i))) return false;
		}
		return true;
	}


	private String getName(Token token)
	{
		if (token instanceof NameToken)
			return token.getNormalizedText();
		else if (token instanceof StringToken)
			return ((StringToken) token).getUnquotedText();
		return null;
	}

	private int findFromEndToken(List tokens, int start)
	{
		int index=start;
		while (index<tokens.size())
		{
			Token token=(Token)tokens.get(index);
			if (token instanceof NameToken)
			{
				String name=getName(token);
				if (SQLConstants.isReservedWord(name))
				{
					if ("where".equals(name)) return index;
					if ("order".equals(name)) return index;
					if (!"as".equals(name)) return -1;
				}
			}
			else if (token instanceof SeparatorToken)
			{
				if (",".equals(token.getText()))
					index++;
				else
					return -1;
			}
			else if (!(token instanceof SpaceToken))
			{
				return -1;
			}
			index++;
		}
		return index;
	}

	private int findFromToken(List tokens, int start)
	{
		int index=start;
		Stack brackets=new Stack();
		while (index<tokens.size())
		{
			Token token=(Token)tokens.get(index);
			if (token instanceof SeparatorToken)
			{
				SeparatorToken separator=(SeparatorToken)token;
				String sep=separator.getText();
				if ("(".equals(sep))
					brackets.push(separator);
				else if (")".equals(sep))
				{
					SeparatorToken separator2=(SeparatorToken)brackets.peek();
					if ("(".equals(separator2.getText())) brackets.pop();
				}
			}
			else if (token instanceof NameToken && brackets.isEmpty())
			{
				NameToken nameToken=(NameToken)token;
				if ("from".equals(nameToken.getNormalizedText())) return index;
			}
			index++;
		}
		return -1;
	}

	public String getSuccessMessage(int updateCount)
	{
		String text=getNormalizedText();
		if (text.startsWith("select")) return updateCount+" row(s) found.";
		if (text.startsWith("insert")) return updateCount+" row(s) inserted.";
		if (text.startsWith("update")) return updateCount+" row(s) updated.";
		if (text.startsWith("delete")) return updateCount+" row(s) deleted.";
		if (text.startsWith("commit")) return "Commit completed.";
		if (text.startsWith("rollback")) return "Rollback completed.";

		if (text.startsWith("alter table")) return "Tabled altered.";
		if (text.startsWith("create table")) return "Table created.";
		if (text.startsWith("create or replace table")) return "Table created.";
		if (text.startsWith("drop table")) return "Table dropped.";

		if (text.startsWith("alter view")) return "View altered.";
		if (text.startsWith("create view")) return "View created.";
		if (text.startsWith("create or replace view")) return "View created.";
		if (text.startsWith("drop view")) return "View dropped";

		if (text.startsWith("alter index")) return "Index altered.";
		if (text.startsWith("create index")) return "Index created.";
		if (text.startsWith("drop index")) return "Index dropped.";

		if (text.startsWith("create function")) return "Function created.";
		if (text.startsWith("create or replace function")) return "Function created.";
		if (text.startsWith("drop function")) return "Function dropped.";

		if (text.startsWith("create procedure")) return "Procedure created.";
		if (text.startsWith("create or replace procedure")) return "Procedure created.";
		if (text.startsWith("drop procedure")) return "Procedure dropped.";

		if (text.startsWith("create synonym")) return "Synonym created.";
		if (text.startsWith("drop synonym")) return "Synonym dropped.";

		if (text.startsWith("alter trigger")) return "Trigger altered.";
		if (text.startsWith("create trigger")) return "Trigger created.";
		if (text.startsWith("create or replace trigger")) return "Trigger created.";
		if (text.startsWith("drop trigger")) return "Trigger dropped.";

		if (text.startsWith("create database")) return "Database created.";

		return "Statement executed.";
	}

	public String getNormalizedText()
	{
		return tokenList.getNormalizedText();
	}

	public String getText()
	{
		return tokenList.getText();
	}

	public String toString()
	{
		return tokenList.getText();
	}

	public boolean isSimpleSelect()
	{
		return simpleSelect;
	}

	public String getTableName()
	{
		return getTable().getName();
}

	public List getColumnInfos()
	{
		return columnInfos;
	}

	public NameInfo getTable()
	{
		return tableInfo;
	}
}
