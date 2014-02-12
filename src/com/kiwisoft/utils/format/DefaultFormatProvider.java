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

import java.text.DateFormat;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.5 $, $Date: 2006/03/24 18:05:39 $
 */
public class DefaultFormatProvider implements FormatProvider
{
	public ObjectFormat[] getFormats()
	{
		return new ObjectFormat[]
		{
			new NonDecimalNumberFormat("Dual", 2, "b"),
			new NonDecimalNumberFormat("Octal", 8, "o"),
			new NonDecimalNumberFormat("Hexadecimal", 16, "h"),
			new JavaNumberFormat("Java"),
			new MoneyFormat("Money"),
			new BytesFormat("Bytes"),
			new BytesFormat("Bytes (UTF-8)", "UTF-8"),
			new BytesFormat("Bytes (UTF-16)", "UTF-16"),
			new BytesFormat("Bytes (ISO-8859-1)", "ISO-8859-1"),
			new DefaultDateFormat("Date only", DateFormat.getDateInstance()),
			new DefaultDateFormat("Time only", DateFormat.getTimeInstance()),
			new DefaultDateFormat("Date and Time", DateFormat.getDateTimeInstance()),
			new TimeMillisFormat("Millis"),
			new FlagFormat("Flag (0 or 1)")
		};
	}

	public String getComponentName()
	{
		return "SQLPluginFormats";
	}
}
