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

import java.math.BigDecimal;
import java.math.BigInteger;
import javax.swing.SwingConstants;

import com.kiwisoft.utils.StringUtils;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.3 $, $Date: 2006/03/24 18:05:39 $
 */
public class DefaultNumberFormat extends DefaultObjectFormat
{
	public DefaultNumberFormat(String name)
	{
		super(name);
	}

	public boolean canFormat(Class aClass)
	{
		return aClass!=null && Number.class.isAssignableFrom(aClass);
	}

	public boolean canParse(Class aClass)
	{
		return aClass!=null && Number.class.isAssignableFrom(aClass);
	}

	public Object parse(String value, Class targetClass)
	{
		if (StringUtils.isEmpty(value)) return null;
		value=value.trim();
		Number number=new BigDecimal(value);
		if (targetClass.isInstance(number)) return number;
		if (targetClass==Double.class) return new Double(number.doubleValue());
		if (targetClass==Float.class) return new Float(number.floatValue());
		if (targetClass==Long.class) return new Long(number.longValue());
		if (targetClass==Integer.class) return new Integer(number.intValue());
		if (targetClass==Short.class) return new Short(number.shortValue());
		if (targetClass==Byte.class) return new Byte(number.byteValue());
		if (targetClass==BigDecimal.class) return number;
		if (targetClass==BigInteger.class) return ((BigDecimal)number).toBigInteger();
		return number;
	}

	public int getHorizontalAlignment(Object value)
	{
		return SwingConstants.TRAILING;
	}
}
