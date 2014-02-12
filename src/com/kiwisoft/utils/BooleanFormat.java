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
 * Format definition for Boolean objects.
 * <br/>
 * <br/><b>Pattern:</b>
 * <pre>
 * &lt;'false' text&gt;;&lt;'true' text&gt;[;&lt;'null' text&gt;]
 * </pre>
 * <p/>
 * <b>Example:</b>
 * <pre>
 * BooleanFormat format=new BooleanFormat("F;T;X");
 * format.format(Boolean.TRUE) -> "T"
 * format.format(Boolean.FALSE) -> "F"
 * format.format(null) -> "X"
 * <p/>
 * BooleanFormat format=new BooleanFormat("F;T");
 * format.format(Boolean.TRUE) -> "T"
 * format.format(Boolean.FALSE) -> "F"
 * format.format(null) -> "F"
 * </pre>
 *
 * @author Stefan Stiller
 * @version $Revision: 1.2 $, $Date: 2006/03/24 18:27:21 $
 */
public class BooleanFormat
{
    private String trueString;
    private String falseString;
    private String nullString;
    private boolean nullStringSet;

    public BooleanFormat(String pattern)
    {
        prepare(pattern);
    }

    private void prepare(String pattern)
    {
        String[] parts=pattern.split(";"); // NOI18N
        if (parts==null || parts.length<2 || parts.length>3)
        {
            throw new IllegalArgumentException("Invalid pattern '"+pattern+"'."); // NOI18N
        }
        falseString=parts[0];
        trueString=parts[1];
        if (parts.length>2)
        {
            nullString=parts[2];
            nullStringSet=true;
        }
    }

    public Boolean parse(String source)
    {
        if (trueString.equals(source))
            return Boolean.TRUE;
        else if (falseString.equals(source))
            return Boolean.FALSE;
        else
            return null;
    }

    public String format(Boolean obj)
    {
        if (obj==null)
        {
            if (nullStringSet)
            {
                return nullString;
            }
            else
            {
                return falseString;
            }
        }
        else
        {
            if (obj.booleanValue())
            {
                return trueString;
            }
            else
            {
                return falseString;
            }
        }
    }
}
