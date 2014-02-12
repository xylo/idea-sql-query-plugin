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
import java.io.UnsupportedEncodingException;
import java.io.StringWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLDecoder;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.13 $, $Date: 2006/03/24 18:50:51 $
 */
public class Utils
{
	private Utils()
	{
	}

	public static File getExistingPath(String exportPath)
	{
		File exportDir=null;
		if (exportPath!=null)
		{
			exportDir=new File(exportPath);
			if (!exportDir.exists() || !exportDir.isDirectory() || !exportDir.canWrite())
			{
				exportDir=null;
			}
		}
		if (exportDir==null) exportDir=new File(System.getProperty("user.home"));
		return exportDir;
	}

	public static File getClassBasePath(Class aClass)
	{
		String className=aClass.getName();
		className=className.replace('.', '/')+".class";
		URL url=aClass.getClassLoader().getResource(className);
		if ("file".equals(url.getProtocol()))
		{
			String dir=url.getFile();
			dir=dir.substring(0, dir.length()-className.length());
			try
			{
				dir=URLDecoder.decode(dir, "UTF-8");
			}
			catch (UnsupportedEncodingException e)
			{
			}
			return new File(dir);
		}
		if ("jar".equals(url.getProtocol()))
		{
			String dir=url.getFile();
			if (dir.startsWith("file:/"))
			{
				String jarFile=dir.substring(6, dir.indexOf('!'));
				try
				{
					jarFile=URLDecoder.decode(jarFile, "UTF-8");
				}
				catch (UnsupportedEncodingException e)
				{
				}
				return new File(jarFile);
			}
		}
		throw new RuntimeException("Couldn't locate base directory.");
	}

	public static boolean equals(Object o1, Object o2)
	{
		if (o1==o2) return true;
		else if (o1==null) return false; // o2 is not null because of the first test
		else return o1.equals(o2);
	}

	public static String getShortClassName(Class aClass)
	{
		String name=aClass.getName();
		return name.substring(name.lastIndexOf(".")+1);
	}

	public static String toString(Throwable exception)
	{
		StringWriter stringWriter=new StringWriter();
		exception.printStackTrace(new PrintWriter(stringWriter));
		return stringWriter.toString();
	}
}
