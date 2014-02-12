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

/**
 * @author Stefan Stiller
 * @version $Revision: 1.3 $, $Date: 2006/03/24 18:02:05 $
 */
public class Tristate
{
	public static final Tristate TRUE=new Tristate(true);
	public static final Tristate FALSE=new Tristate(false);
	public static final Tristate UNDEFINED=new Tristate();

	private final static char TRUE_STATE=2;
	private final static char FALSE_STATE=1;
	private final static char UNDEFINED_STATE=0;

	private char ch=UNDEFINED_STATE;

	public Tristate(Boolean value)
	{
		if (value!=null) ch=value.booleanValue() ? TRUE_STATE : FALSE_STATE;
	}

	public Tristate(boolean value)
	{
		ch=value ? TRUE_STATE : FALSE_STATE;
	}

	public static Tristate getTristate(boolean value)
	{
		return value ? TRUE : FALSE;
	}

	public static Tristate getTristate(Boolean value)
	{
		if (value!=null) return value.booleanValue() ? TRUE : FALSE;
		return UNDEFINED;
	}

	public Tristate()
	{
	}

	public Boolean toBoolean()
	{
		if (isTrue()) return Boolean.TRUE;
		else if (isFalse()) return Boolean.FALSE;
		return null;
	}

	public boolean isTrue()
	{
		return ch==TRUE_STATE;
	}

	public void setTrue()
	{
		ch=TRUE_STATE;
	}

	public boolean isFalse()
	{
		return ch==FALSE_STATE;
	}

	public void setFalse()
	{
		ch=FALSE_STATE;
	}

	public boolean isUndefinied()
	{
		return ch==UNDEFINED_STATE;
	}

	public void setUndefined()
	{
		ch=UNDEFINED_STATE;
	}

	public boolean equals(Object o)
	{
		if (this==o) return true;
		if (o==null) return isUndefinied();
		if (!(o instanceof Tristate)) return false;

		final Tristate tristate=(Tristate)o;

		return ch==tristate.ch;
	}

	public int hashCode()
	{
		return (int)ch;
	}

	public String toString()
	{
		if (isTrue()) return "true";
		if (isFalse()) return "false";
		return "undefined";
	}
}
