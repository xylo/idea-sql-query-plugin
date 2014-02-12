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

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import java.math.BigDecimal;

import com.kiwisoft.utils.StringUtils;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.3 $, $Date: 2006/03/24 18:33:38 $
 */
public class JavaNumberFormat extends DefaultNumberFormat
{
	private NumberFormat numberFormat;

	public JavaNumberFormat(String name)
	{
		super(name);
		numberFormat=NumberFormat.getInstance(Locale.US);
		numberFormat.setMinimumFractionDigits(0);
		numberFormat.setMaximumFractionDigits(8);
		numberFormat.setMinimumIntegerDigits(1);
		numberFormat.setMaximumIntegerDigits(8);
		numberFormat.setGroupingUsed(false);
		numberFormat.setParseIntegerOnly(false);
	}

	public String format(Object value)
	{
		if (value instanceof Number) return numberFormat.format(value);
		return super.format(value);
	}

	public Object parse(String value, Class targetClass)
	{
		if (StringUtils.isEmpty(value)) return null;
		value=value.trim();
		try
		{
			Number number=numberFormat.parse(value);
			if (number==null || targetClass.isInstance(number)) return number;
			if (targetClass==Double.class) return new Double(number.doubleValue());
			if (targetClass==Float.class) return new Float(number.floatValue());
			if (targetClass==BigDecimal.class) return new BigDecimal(number.doubleValue());
			return number;
		}
		catch (ParseException e)
		{
			throw new RuntimeException(e);

		}
	}
}
