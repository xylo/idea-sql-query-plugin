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
package com.kiwisoft.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Calendar;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.6 $, $Date: 2006/03/24 18:27:21 $
 */
public class DateUtils
{
	public final static long SECOND=1000;
	public final static long MINUTE=60*SECOND;
	public final static long HOUR=60*MINUTE;
	public final static long DAY=24*HOUR;
	public final static long WEEK=7*DAY;

	private static DateFormat[] dateFormats=
			{
				DateFormat.getDateInstance(DateFormat.FULL),
				DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL),
				DateFormat.getDateInstance(DateFormat.LONG),
				DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG),
				DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM),
				DateFormat.getDateInstance(DateFormat.MEDIUM),
				DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT),
				DateFormat.getDateInstance(DateFormat.SHORT),
			};

	private DateUtils()
	{
	}

	public static boolean isToday(Calendar date)
	{
		Calendar now=Calendar.getInstance();
		if (date.get(Calendar.DAY_OF_YEAR)!=now.get(Calendar.DAY_OF_YEAR)) return false;
		return date.get(Calendar.YEAR)==now.get(Calendar.YEAR);
	}

	public static boolean isWeekend(Calendar date)
	{
		int day=date.get(Calendar.DAY_OF_WEEK);
		return day==Calendar.SUNDAY || day==Calendar.SATURDAY;
	}

	public static Calendar getToday()
	{
		Calendar calendar=Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar;
	}

	public static Date parseDate(String value)
	{
		if (value!=null)
		{
			value=value.trim();
			if ("sysdate".equalsIgnoreCase(value) || "now".equalsIgnoreCase(value)) return new Date();
			if ("0".equalsIgnoreCase(value) || "today".equalsIgnoreCase(value)) return getToday().getTime();
			if ("yesterday".equalsIgnoreCase(value)) return getRelativeDate(-1);
			if ("tomorrow".equalsIgnoreCase(value)) return getRelativeDate(1);
			Matcher matcher=Pattern.compile("(\\+|-)(\\d+)(d|w|m|y)?", Pattern.CASE_INSENSITIVE).matcher(value);
			if (matcher.matches())
			{
				int count=Integer.parseInt(matcher.group(2));
				if ("-".equals(matcher.group(1))) count=-count;
				Calendar calendar=getToday();
				String unit=matcher.group(3);
				if ("d".equalsIgnoreCase(unit) || StringUtils.isEmpty(unit)) calendar.add(Calendar.DATE, count);
				if ("w".equalsIgnoreCase(unit)) calendar.add(Calendar.DATE, 7*count);
				if ("m".equalsIgnoreCase(unit)) calendar.add(Calendar.MONTH, count);
				if ("y".equalsIgnoreCase(unit)) calendar.add(Calendar.YEAR, count);
				return calendar.getTime();
			}
			for (int i=0; i<dateFormats.length; i++)
			{
				DateFormat dateFormat=dateFormats[i];
				try
				{
					return dateFormat.parse(value);
				}
				catch (ParseException e)
				{
				}
			}
		}
		return null;
	}

	private static Date getRelativeDate(int count)
	{
		Calendar calendar=getToday();
		calendar.add(Calendar.DATE, count);
		return calendar.getTime();
	}

	/**
	 * Sets the date to the begin of the week.
	 */
	public static void setStartOfWeek(Calendar date)
	{
		date.add(Calendar.DATE,-getDayOfWeek(date));
		date.set(Calendar.HOUR_OF_DAY, 0);
		date.set(Calendar.MINUTE, 0);
		date.set(Calendar.SECOND, 0);
		date.set(Calendar.MILLISECOND, 0);
	}

	/**
	 * Returns the day of week depending if the week starts on sunday or monday.
	 *
	 * @return 0 for the first day of the week and 6 for the last.
	 */
	public static int getDayOfWeek(Calendar date)
	{
		int day=date.get(Calendar.DAY_OF_WEEK);
		if (getFirstDayOfWeek()==Calendar.SUNDAY) return day-1;
		else
		{
			if (day>=Calendar.MONDAY) return day-Calendar.MONDAY;
			else return 6;
		}
	}

	private static int firstDayOfWeek=Calendar.MONDAY;

	public static int getFirstDayOfWeek()
	{
		return firstDayOfWeek;
	}

	/**
	 * Sets the date to the end of the week.
	 */
	public static void setEndOfWeek(Calendar date)
	{
		date.add(Calendar.DATE,7-getDayOfWeek(date));
		date.set(Calendar.HOUR_OF_DAY, 0);
		date.set(Calendar.MINUTE, 0);
		date.set(Calendar.SECOND, 0);
		date.set(Calendar.MILLISECOND, 0);
	}

	public static Date add(Date date, int field, int value)
	{
		Calendar calendar=Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(field, value);
		return calendar.getTime();
	}
}
