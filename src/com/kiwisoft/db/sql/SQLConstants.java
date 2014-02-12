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

import java.util.HashSet;
import java.util.Set;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.3 $, $Date: 2006/03/24 17:55:45 $
 */
public class SQLConstants
{
    private static Set operators=new HashSet();
    private static Set preOperators=new HashSet();

    static
    {
        operators.add(">=");
        operators.add(">");
        operators.add("=");
        operators.add("<=");
        operators.add("<");
        operators.add("||");
        operators.add("*");
		operators.add("/");
		operators.add("+");
		operators.add("-");
        preOperators.add("|");
    }

	private SQLConstants()
	{
	}

    public static boolean isOperator(String text)
    {
        return operators.contains(text);
    }

    public static boolean isOperatorStart(String text)
    {
        return operators.contains(text) || preOperators.contains(text);
    }

    private static Set separators=new HashSet();

    static
    {
        separators.add(",");
        separators.add(";");
        separators.add(".");
        separators.add("(");
        separators.add(")");
	    separators.add("/");
    }

    public static boolean isSeparator(String text)
    {
        return separators.contains(text);
    }

    private static Set reservedWords=new HashSet();

    static
    {
        reservedWords.add("select");
        reservedWords.add("from");
        reservedWords.add("where");
        reservedWords.add("create");
        reservedWords.add("alter");
        reservedWords.add("index");
        reservedWords.add("constraint");
        reservedWords.add("as");
        reservedWords.add("order");
        reservedWords.add("by");
        reservedWords.add("table");
        reservedWords.add("group");
        reservedWords.add("describe");
        reservedWords.add("schema");
    }

    public static boolean isReservedWord(String text)
    {
        return reservedWords.contains(text.toLowerCase());
    }
}
