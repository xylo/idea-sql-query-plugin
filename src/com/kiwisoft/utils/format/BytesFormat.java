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
package com.kiwisoft.utils.format;

import java.io.UnsupportedEncodingException;

import com.kiwisoft.utils.StringUtils;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.3 $, $Date: 2006/03/24 18:05:39 $
 */
public class BytesFormat extends DefaultObjectFormat
{
	private String charsetName;
	private String name;

	public BytesFormat(String name, String charsetName)
	{
		super("Default");
		this.charsetName=charsetName;
		this.name=name;
	}

	public BytesFormat(String name)
	{
		super("Default");
		this.name=name;
	}

	public String getName()
	{
		return name;
	}

	public boolean canFormat(Class aClass)
	{
		return aClass!=null && String.class.isAssignableFrom(aClass);
	}

	public String format(Object value)
	{
		if (value instanceof String)
		{
			String string=(String)value;
			byte[] bytes;
			try
			{
				if (charsetName!=null) bytes=string.getBytes(charsetName);
				else bytes=string.getBytes();
			}
			catch (UnsupportedEncodingException e)
			{
				bytes=string.getBytes();
			}
			return StringUtils.toByteString(bytes, " ");
		}
		return super.format(value);
	}

	public boolean canParse(Class aClass)
	{
		return false;
	}
}