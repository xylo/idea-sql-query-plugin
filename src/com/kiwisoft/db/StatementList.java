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
package com.kiwisoft.db;

import java.util.*;

import com.kiwisoft.db.sql.SQLStatement;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.3 $, $Date: 2006/03/24 17:55:46 $
 */
public class StatementList
{
	private List statements;

	public StatementList(List parsedStatements)
	{
		this.statements=parsedStatements;
	}

	public StatementList(SQLStatement statement)
	{
		this.statements=Collections.singletonList(statement);
	}

	public List getStatements()
	{
		return statements;
	}

	public SQLStatement getStatement(int i)
	{
		return (SQLStatement)statements.get(i);
	}
}
