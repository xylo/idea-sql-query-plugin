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

import com.kiwisoft.utils.StringUtils;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.3 $, $Date: 2006/03/24 18:33:39 $
 */
public class NonDecimalNumberFormat extends DefaultNumberFormat
{
	private String prefix;
	private int radix;

	public NonDecimalNumberFormat(String name, int radix, String prefix)
	{
		super(name);
		this.radix=radix;
		this.prefix=prefix;
	}

	public String format(Object value)
	{
		if (value instanceof Number)
		{
			Number number=(Number)value;
			if (number instanceof BigDecimal)
				return prefix+((BigDecimal)value).toBigInteger().toString(radix);
			else if (number instanceof BigInteger)
				return prefix+((BigInteger)value).toString(radix);
			else
				return prefix+Long.toString(number.longValue(), radix);
		}
		return super.format(value);
	}

	public boolean canParse(Class aClass)
	{
		return Number.class.isAssignableFrom(aClass);
	}

	public Object parse(String value, Class targetClass)
	{
		if (StringUtils.isEmpty(value)) return null;
		value=value.trim();
		Number number;
		if (value.startsWith(prefix)) value=value.substring(prefix.length());
		number=new BigInteger(value, radix);
		if (targetClass.isInstance(number)) return number;
		if (targetClass==Double.class) return new Double(number.doubleValue());
		if (targetClass==Float.class) return new Float(number.floatValue());
		if (targetClass==Long.class) return new Long(number.longValue());
		if (targetClass==Integer.class) return new Integer(number.intValue());
		if (targetClass==Short.class) return new Short(number.shortValue());
		if (targetClass==Byte.class) return new Byte(number.byteValue());
		if (targetClass==BigDecimal.class) return new BigDecimal((BigInteger)number);
		if (targetClass==BigInteger.class) return number;
		return number;
	}
}
