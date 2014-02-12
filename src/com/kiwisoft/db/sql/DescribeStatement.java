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

import java.util.List;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.6 $, $Date: 2006/03/24 17:54:50 $
 */
public class DescribeStatement extends AbstractSQLStatement
{
	private TokenList tokenList;
	private String schema;
	private NameInfo tableInfo;
	private boolean valid;

	public DescribeStatement(TokenList tokenList)
	{
		this.tokenList=tokenList;
		try
		{
			parse();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			valid=false;
		}
	}

	public boolean isValid()
	{
		return valid;
	}

	private void parse()
	{
		valid=true;
		List tokens=tokenList.trim();
		// <'describe'><space><schemaName><'.'><tableName>
		if (tokens.size()==5)
		{
			assertType(tokens, 1, SpaceToken.class, null);
			assertType(tokens, 2, NameToken.class, null);
			assertType(tokens, 3, SeparatorToken.class, ".");
			assertType(tokens, 4, NameToken.class, null);
			schema=((Token)tokens.get(2)).getText();
			String tableName=((Token)tokens.get(4)).getText();
			tableInfo=new NameInfo(tableName);
			return;
		}
		// <'describe'><space><tableName>
		if (tokens.size()==3)
		{
			assertType(tokens, 1, SpaceToken.class, null);
			assertType(tokens, 2, NameToken.class, null);
			String tableName=((Token)tokens.get(2)).getText();
			tableInfo=new NameInfo(tableName);
			return;
		}
		valid=false;
	}

	private void assertType(List tokens, int offset, Class aClass, String text)
	{
		Token token=(Token)tokens.get(offset);
		if (!aClass.isInstance(token))
			throw new RuntimeException("Invalid statement");
		if (text!=null && !text.equals(token.getNormalizedText()))
			throw new RuntimeException("Invalid statement");
	}

	public String getText()
	{
		return tokenList.getText();
	}

	public String getNormalizedText()
	{
		return tokenList.getNormalizedText();
	}

	public String getSuccessMessage(int updateCount)
	{
		return "Statement executed.";
	}

	public boolean isSimpleSelect()
	{
		return false;
}

	public String getTableName()
	{
		return null;
}

	public String getSchema()
	{
		return schema;
	}

	public NameInfo getTable()
	{
		return tableInfo;
	}
}
