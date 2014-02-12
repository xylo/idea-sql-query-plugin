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
package com.kiwisoft.utils.idea;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Date;
import javax.swing.SwingUtilities;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.OrderRootType;
import org.jdom.Attribute;
import org.jdom.Element;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.vfs.VirtualFile;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.13 $, $Date: 2006/03/24 18:50:51 $
 */
public class PluginUtils
{
	private PluginUtils()
	{
	}

	public static void showDialog(final DialogWrapper dialog, boolean wait, final boolean modal)
	{
		Runnable runnable=new Runnable()
		{
			public void run()
			{
				dialog.setModal(modal);
				dialog.show();
			}
		};
		if (SwingUtilities.isEventDispatchThread()) runnable.run();
		else if (wait)
		{
			try
			{
				SwingUtilities.invokeAndWait(runnable);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		else SwingUtilities.invokeLater(runnable);
	}

	/**
	 * Reads a boolean attribute from the specified Element
	 *
	 * @param child		 the <code>Element</code> containing the attribute
	 * @param attributeName the <code>String</code> name of the attribute
	 * @param defaultValue  the <code>boolean</code> default value, if the attribute does not exists
	 * @return the boolean value as stored as attribute value of the specified element
	 */
	public static boolean getBoolean(Element child, String attributeName, boolean defaultValue)
	{
		Attribute attribute=child.getAttribute(attributeName);
		if (attribute!=null)
		{
			String bool=child.getAttributeValue(attributeName);
			if (bool!=null) return Boolean.valueOf(bool).booleanValue();
			else return defaultValue;
		}
		else return defaultValue;
	}

	/**
	 * Reads a boolean attribute from the specified Element
	 *
	 * @param child		 the <code>Element</code> containing the attribute
	 * @param attributeName the <code>String</code> name of the attribute
	 * @param defaultValue  the <code>boolean</code> default value, if the attribute does not exists
	 * @return the boolean value as stored as attribute value of the specified element
	 */
	public static Boolean getBoolean(Element child, String attributeName, Boolean defaultValue)
	{
		Attribute attribute=child.getAttribute(attributeName);
		if (attribute!=null)
		{
			String bool=attribute.getValue();
			if (bool!=null) return Boolean.valueOf(bool);
			else return defaultValue;
		}
		else return defaultValue;
	}

	/**
	 * Reads a boolean attribute from the specified Element
	 *
	 * @param child		 the <code>Element</code> containing the attribute
	 * @param attributeName the <code>String</code> name of the attribute
	 * @param defaultValue  the <code>boolean</code> default value, if the attribute does not exists
	 * @return the boolean value as stored as attribute value of the specified element
	 */
	public static String getString(Element child, String attributeName, String defaultValue)
	{
		Attribute attribute=child.getAttribute(attributeName);
		if (attribute!=null)
		{
			String value=attribute.getValue();
			return value!=null ? value : defaultValue;
		}
		return defaultValue;
	}

	/**
	 * Reads an int attribute from the specified Element
	 *
	 * @param child		 the <code>Element</code> containing the attribute
	 * @param attributeName the <code>String</code> name of the attribute
	 * @param defaultValue  the <code>int</code> default value, if the attribute does not exists
	 * @return the Integer value as stored as attribute value of the specified element
	 */
	public static Integer getInteger(Element child, String attributeName, Integer defaultValue)
	{
		String value=child.getAttributeValue(attributeName);
		if (value!=null)
		{
			try
			{
				return Integer.valueOf(value);
			}
			catch (NumberFormatException e)
			{
				return defaultValue;
			}
		}
		else
			return defaultValue;
	}

	/**
	 * Reads a date attribute from the specified Element
	 *
	 * @param child		 the <code>Element</code> containing the attribute
	 * @param attributeName the <code>String</code> name of the attribute
	 * @return the Date value as stored as attribute value of the specified element
	 */
	public static Date getDate(Element child, String attributeName)
	{
		String value=child.getAttributeValue(attributeName);
		if (value!=null)
		{
			try
			{
				long millis=Long.parseLong(value);
				return new Date(millis);
			}
			catch (NumberFormatException e)
			{
				return null;
			}
		}
		else
			return null;
	}

	/**
	 * Writes a date attribute to the specified Element
	 *
	 * @param child		 the <code>Element</code> containing the attribute
	 * @param attributeName the <code>String</code> name of the attribute
	 */
	public static void setDate(Element child, String attributeName, Date date)
	{
		if (date!=null) child.setAttribute(attributeName, String.valueOf(date.getTime()));
	}

	public static boolean setValue(Element child, String name, Object content)
	{
		if (content!=null)
		{
			child.setAttribute(name, String.valueOf(content));
			return true;
		}
		return false;
	}

	public static void setValue(Element child, String name, boolean content)
	{
		child.setAttribute(name, Boolean.toString(content));
	}

	public static void setValue(Element child, String name, int content)
	{
		child.setAttribute(name, Integer.toString(content));
	}

	public static ClassLoader getProjectClassLoader(Project project)
	{
		if (project==null) return PluginUtils.class.getClassLoader();
		//noinspection deprecation
		// todo: edited lines
		Module[] modules = ModuleManager.getInstance(project).getModules();
		if (modules.length == 0) return PluginUtils.class.getClassLoader();
		ModuleRootManager mrm = ModuleRootManager.getInstance(modules[0]);
		VirtualFile[] roots = mrm.orderEntries().classes().getRoots();
		//VirtualFile[] roots=ProjectRootManager.getInstance(project).getFullClassPath();
		URL[] classpath=new URL[roots.length];
		for (int i=0; i<roots.length; i++)
		{
			try
			{
				String path=roots[i].getPath();
				if (path.endsWith("!/")) path=path.substring(0, path.length()-2);
				classpath[i]=new File(path).toURI().toURL();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		return new URLClassLoader(classpath, PluginUtils.class.getClassLoader());
	}

	public static File getPluginBasePath(Class aClass)
	{
		String className=aClass.getName();
		className=className.replace('.', '/')+".class";
		URL url=aClass.getClassLoader().getResource(className);
		if ("file".equals(url.getProtocol()))
		{
			String dir=url.getFile();
			dir=dir.substring(0, dir.length()-className.length());
			return new File(dir).getParentFile();
		}
		if ("jar".equals(url.getProtocol()))
		{
			String dir=url.getFile();
			if (dir.startsWith("file:/"))
			{
				String jarFile=dir.substring(6, dir.indexOf('!'));
				File file=new File(jarFile).getParentFile();
				return file.getParentFile();
			}
		}
		throw new RuntimeException("Couldn't locate base directory.");
	}
}
