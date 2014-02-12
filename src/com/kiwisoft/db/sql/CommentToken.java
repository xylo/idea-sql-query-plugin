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

/**
 * @author Stefan Stiller
 * @version $Revision: 1.4 $, $Date: 2006/03/24 17:54:50 $
 */
public class CommentToken extends Token
{
	public boolean closed;

	public CommentToken(char ch, int offset)
	{
		super(ch, offset);
	}

	public String getNormalizedText()
	{
		return "";
	}

	protected boolean isValidCharacter(char ch)
	{
		String text=getText();
		if ("/".equals(text) && ch=='*') return true;
		if ("-".equals(text) && ch=='-') return true;
		if (text.startsWith("/*")) return !text.endsWith("*/");
		else if (text.startsWith("--")) return ch!='\n';
		return false;
	}
}
