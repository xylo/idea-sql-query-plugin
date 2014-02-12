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
package com.kiwisoft.db;

import java.io.File;
import java.util.Observable;

import com.kiwisoft.utils.NotifyObject;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.db.sql.SystemStatement;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.4 $, $Date: 2006/03/24 17:54:50 $
 */
public class Query extends Observable
{
	public static final String QUERY="query";
	public static final String FILE="file";
	public static final String NAME="name";
	public static final String CHANGED="changed";
	public static final String STATEMENT="statement";

    private String statement;
    private String name;
    private File file;
    private boolean changed;
	private SystemStatement systemStatement;

	public Query(String name, SystemStatement systemStatement)
	{
		this.name=name;
		this.systemStatement=systemStatement;
}

    public Query(String name)
    {
        this.name=name;
    }

    public Query(File file, String statement)
    {
        setFile(file);
        this.statement=StringUtils.trim(statement);
    }

    public Query(String name, Query query)
    {
        this.name=name;
        this.statement=StringUtils.trim(query.getStatement());
        this.changed=query.isChanged();
    }

	public Query(String name, String query)
	{
		this.name=name;
		this.statement=query;
		this.changed=true;
	}

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name=name;
        setChanged();
        notifyObservers(new NotifyObject("name changed"));
    }

    public File getFile()
    {
        return file;
    }

    public void setFile(File file)
    {
        this.file=file;
        if (file!=null) setName(file.getName());
    }

    public String getStatement()
    {
		if (systemStatement!=null) return systemStatement.getText();
        return statement;
    }

    public void setStatement(String statement)
    {
        String oldStatement=this.statement;
        this.statement=statement;
        if (!StringUtils.equal(oldStatement, statement)) setChanged(true);
    }

    public void setChanged(boolean value)
    {
		if (changed!=value)
		{
			changed=value;
			setChanged();
			notifyObservers(new NotifyObject(changed ? "changed" : "saved"));
		}
	}

    public boolean isChanged()
    {
        return changed;
    }

    public boolean isSaveable()
    {
        return isChanged() && !StringUtils.isEmpty(statement);
    }

	public boolean isSystemQuery()
	{
		return systemStatement!=null;
	}

	public SystemStatement getSystemStatement()
	{
		return systemStatement;
	}
}
