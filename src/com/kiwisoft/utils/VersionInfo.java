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

import java.util.ResourceBundle;
import java.util.Locale;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.ParseException;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.3 $, $Date: 2006/03/24 17:48:08 $
 */
public class VersionInfo
{
	private String version;
	private String copyright;
	private String buildNumber;
	private Date builtDate;

	public VersionInfo(String bundleName)
	{
		ResourceBundle bundle=ResourceBundle.getBundle(bundleName, Locale.getDefault(), VersionInfo.class.getClassLoader());
		version=bundle.getString("version");
		copyright=bundle.getString("copyright");
		buildNumber=bundle.getString("build.number");
		try
		{
			builtDate=new SimpleDateFormat("d-M-yyyy").parse(bundle.getString("built.date"));
		}
		catch (ParseException e)
		{
			builtDate=new Date(0);
		}
	}

	public String getVersion()
	{
		return version;
	}

	public String getCopyright()
	{
		return copyright;
	}

	public Date getBuiltDate()
	{
		return builtDate;
	}

	public String getBuildNumber()
	{
		return buildNumber;
	}
}
