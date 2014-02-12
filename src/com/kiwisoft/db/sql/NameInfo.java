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
package com.kiwisoft.db.sql;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.3 $, $Date: 2006/03/24 17:54:50 $
 */
public class NameInfo
{
    private String name;
    private String label;

    public NameInfo(String name)
    {
        this.name=name;
    }

    public NameInfo(String name, String label)
    {
        this.name=name;
        this.label=label;
    }

    public String getName()
    {
        return name;
    }

    public String getLabel()
    {
        return label;
    }

    public String toString()
    {
        return name+" as '"+label+"'";
    }
}