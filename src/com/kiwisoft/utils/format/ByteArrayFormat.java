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

import com.kiwisoft.utils.StringUtils;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.3 $, $Date: 2006/03/24 18:05:38 $
 */
public class ByteArrayFormat extends DefaultObjectFormat
{
	public ByteArrayFormat(String name)
	{
		super(name);
	}

	public boolean canFormat(Class aClass)
	{
		return byte[].class==aClass;
	}

	public String format(Object value)
	{
		if (value instanceof byte[])
		{
			byte[] bytes=(byte[])value;
			StringBuffer buffer=new StringBuffer(bytes.length*3);
			for (int i=0; i<bytes.length; i++)
			{
				if (i>0) buffer.append(" ");
				buffer.append(StringUtils.toByteString(bytes[i]));
			}
			return buffer.toString();
		}
		return super.format(value);
	}
}
