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

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.swing.filechooser.FileFilter;

/**
 * @author Sven Krause
 * @version $Revision: 1.4 $, $Date: 2006/03/24 18:52:26 $
 */
public class ExtentionFileFilter extends FileFilter
{
    public static final String ALL="*";

    private String description;
    private Set extentions;

    private boolean showReadOnly=true;
    private boolean showDirectories=true;

    public ExtentionFileFilter(String description, String extention)
    {
        this(description, new String[]{extention});
    }

    public ExtentionFileFilter(String description, String[] extentions)
    {
        this.description=description;

        this.extentions=new HashSet(Arrays.asList(extentions));

        if (this.extentions.contains(ALL))
        {
            this.extentions=null;
        }
    }

    public boolean isShowReadOnly()
    {
        return showReadOnly;
    }

    public void setShowReadOnly(boolean showReadOnly)
    {
        this.showReadOnly=showReadOnly;
    }

    public boolean isShowDirectories()
    {
        return showDirectories;
    }

    public void setShowDirectories(boolean showDirectories)
    {
        this.showDirectories=showDirectories;
    }

    public boolean accept(File f)
    {
        return (f.isDirectory() && showDirectories) ||
            (!f.isDirectory() &&
            (f.canWrite() || showReadOnly) &&
            matchesExtention(f) &&
            f.canRead()
            );
    }

    private boolean matchesExtention(File f)
    {
        if (this.extentions==null)
        {
            return true;
        }

        String ex=getExtention(f);
        return extentions.contains(ex);
    }

    public String getDescription()
    {
        return description;
    }

    public String getExtention(File f)
    {
        String retValue=null;
        String name=f.getName();
        int index=name.lastIndexOf('.');
        if (index>0 && index<name.length()-1)
        {
            retValue=name.substring(index+1).toLowerCase();
        }
        return retValue;
    }
}
