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
package com.kiwisoft.sqlPlugin;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.2 $, $Date: 2006/03/24 17:59:51 $
 */
public class JdbcLibrary
{
	private String vfsUrl;

	public JdbcLibrary(String vfsUrl)
	{
		this.vfsUrl=vfsUrl;
	}

	public JdbcLibrary(VirtualFile virtualFile)
	{
		this.vfsUrl=virtualFile.getUrl();
	}

	public String getVfsUrl()
	{
		return vfsUrl;
	}

	public VirtualFile getVirtualFile()
	{
		return VirtualFileManager.getInstance().findFileByUrl(vfsUrl);
	}

	public boolean equals(Object o)
	{
		if (this==o) return true;
		if (o==null || getClass()!=o.getClass()) return false;

		final JdbcLibrary that=(JdbcLibrary)o;

		return !(vfsUrl!=null ? !vfsUrl.equals(that.vfsUrl) : that.vfsUrl!=null);
	}

	public int hashCode()
	{
		return (vfsUrl!=null ? vfsUrl.hashCode() : 0);
	}
}
