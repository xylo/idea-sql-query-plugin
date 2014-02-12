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

import java.util.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import com.kiwisoft.utils.idea.PluginUtils;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.7 $, $Date: 2006/03/24 18:05:39 $
 */
public class FormatManager
{
	private static FormatManager instance;
	private Set formats;

	public static FormatManager getInstance()
	{
		if (instance==null) instance=new FormatManager();
		return instance;
	}

	private FormatManager()
	{
	}

	public Set getFormats()
	{
		if (formats==null)
		{
			formats=new HashSet();
			Set formatClasses=new HashSet();
			formatClasses.add(DefaultFormatProvider.class);
			try
			{
				Properties properties=new Properties();
				ClassLoader classLoader=FormatManager.class.getClassLoader();
				File file=new File(PluginUtils.getPluginBasePath(FormatManager.class), "formats.properties");
				System.out.println("Load formats from '"+file.getAbsolutePath()+"'...");
				properties.load(new FileInputStream(file));
				Enumeration providerNames=properties.propertyNames();
				while (providerNames.hasMoreElements())
				{
					String providerName=(String)providerNames.nextElement();
					try
					{
						formatClasses.add(Class.forName(providerName, true, classLoader));
					}
					catch (Throwable e)
					{
						e.printStackTrace();
					}
				}
			}
			catch (FileNotFoundException e)
			{
				System.err.println(e.getMessage());
			}
			catch (Throwable e)
			{
				e.printStackTrace();
			}

			for (Iterator it=formatClasses.iterator(); it.hasNext();)
			{
				Class formatClass=(Class)it.next();
				try
				{
					Object format=formatClass.newInstance();
					if (format instanceof FormatProvider)
					{
						ObjectFormat[] formats2=((FormatProvider)format).getFormats();
						for (int i=0; i<formats2.length; i++)
						{
							formats.add(formats2[i]);
							System.out.println("Loaded format '"+formats2[i].getName()+"'.");
						}
					}
					else if (format instanceof ObjectFormat)
					{
						formats.add(format);
						System.out.println("Loaded format '"+((ObjectFormat)format).getName()+"'.");
					}
				}
				catch (Throwable e)
				{
					e.printStackTrace();
				}
			}
		}
		return Collections.unmodifiableSet(formats);
	}

}
