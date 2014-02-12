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
package com.kiwisoft.sqlPlugin.dataLoad;

import java.util.Map;
import java.util.HashMap;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.3 $, $Date: 2002/02/22 10:00:24 $
 */
public class CharacterResource
{
	private static Map values=new HashMap();

	public final static CharacterResource COMMA=new CharacterResource(',', "Comma");
	public final static CharacterResource SEMICOLON=new CharacterResource(';', "Semicolon");
	public final static CharacterResource TABULATOR=new CharacterResource('\t', "Tabulator");
	public final static CharacterResource SPACE=new CharacterResource(' ', "Space");
	public final static CharacterResource PIPE=new CharacterResource('|', "Pipe");
	public final static CharacterResource QUOTES=new CharacterResource('"', "Quote");
	public final static CharacterResource SINGLE_QUOTES=new CharacterResource('\'', "Single Quotes");
	public final static CharacterResource NONE=new CharacterResource((char)0, "None");

	private char character;
	private String name;

	private CharacterResource(char character, String name)
	{
		this.character=character;
		this.name=name;
		values.put(new Character(character), this);
	}

	public char getCharacter()
	{
		return character;
	}

	public String getName()
	{
		return name;
	}

	public String toString()
	{
		return getName();
	}

	public boolean equals(Object o)
	{
		if (this==o) return true;
		if (o==null || getClass()!=o.getClass()) return false;

		final CharacterResource that=(CharacterResource)o;

		return character==that.character;
	}

	public int hashCode()
	{
		return (int)character;
	}

	public static CharacterResource valueOf(char ch)
	{
		return (CharacterResource)values.get(new Character(ch));
	}
}
