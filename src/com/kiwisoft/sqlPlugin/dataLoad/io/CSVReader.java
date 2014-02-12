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
package com.kiwisoft.sqlPlugin.dataLoad.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Reader;
import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;

/**
 * Specialized BasicCSVImport that reades the lines to tokenize from a buffered reader or file
 *
 * @author Stefan Stiller
 * @version $Revision: 1.3 $, $Date: 2002/02/22 10:00:24 $
 */
public class CSVReader extends DataFileReader
{
	private char delimiter;
	private char textQualifier;
	private List tokens;
	private String textQualifierString;
	private String doubleQualifier;

	public CSVReader(File file, char delimiter, char textQualifier) throws FileNotFoundException
	{
		super(file);
		init(delimiter, textQualifier);
	}

	public CSVReader(Reader reader, char delimiter, char textQualifier)
	{
		super(reader);
		init(delimiter, textQualifier);
	}

	private void init(char delimiter, char textQualifier)
	{
		this.delimiter=delimiter;
		this.textQualifier=textQualifier;
		textQualifierString = String.valueOf(textQualifier);
		doubleQualifier = textQualifierString + textQualifierString;
		tokens = new LinkedList();
	}

	/**
	 * gets the delimiter used to tokenize
	 * @return  the delimiter character
	 */
	public char getDelimiter()
	{
		return delimiter;
	}

	/**
	 * gets the text qualifier to identify strings
	 *
	 * @return  the text qualifier character
	 */
	public char getTextQualifier()
	{
		return textQualifier;
	}

	/**
	 * tokenizes the specified line according to the declared delimiter and removes the text quotations
	 * @param line  the <code>String</code> to tokenize
	 * @return  the lines token array
	 */
	public String[] parseLine(String line)
	{
		tokens.clear();
		StringBuffer buffer = new StringBuffer();
		boolean text = false;
		for(int i = 0; i < line.length(); i++)
		{
			char ch = line.charAt(i);
			if( !text )
			{
				if( ch == delimiter )
				{
					tokens.add(buffer.toString());
					buffer = new StringBuffer();
				}
				else if( ch == textQualifier && textQualifier != 0 )
				{
					buffer.append(ch);
					text = true;
				}
				else
					buffer.append(ch);
			}
			else
			{
				if( ch == textQualifier && textQualifier != 0 )
					text = false;
				buffer.append(ch);
			}
		}
		tokens.add(buffer.toString());

		Iterator it = tokens.iterator();
		String[] tokenArray = new String[tokens.size()];
		int i = 0;
		while( it.hasNext() )
		{
			String token = (String) it.next();
			token = token.trim();
			if( textQualifier != 0 )
			{
				if( token.startsWith(textQualifierString) && token.endsWith(textQualifierString) && token.length() >= 2 )
					token = token.substring(1, token.length() - 1);
				int pos = 0;
				while( true )
				{
					pos = token.indexOf(doubleQualifier, pos);
					if( pos < 0 )
						break;
					token = token.substring(0, pos) + textQualifier + token.substring(pos + 2);
					pos++;
				}
			}
			tokenArray[i++] = token;
		}
		return tokenArray;
	}
}
