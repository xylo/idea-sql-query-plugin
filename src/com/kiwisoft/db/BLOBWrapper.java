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

import java.io.*;
import java.sql.Blob;

import com.kiwisoft.utils.IOUtils;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.6 $, $Date: 2006/03/24 17:51:18 $
 */
public class BLOBWrapper
{
	private Object object;
	private Throwable throwable;
	private byte[] data;
	private boolean loaded;
	private String mimetype;

	public BLOBWrapper(Blob blob, ClassLoader classLoader, boolean load)
	{
		if (load)
		{
			try
			{
				long length=Math.min(blob.length(), Integer.MAX_VALUE);
				data=new byte[(int)length];
				blob.getBinaryStream().read(data);
				loaded=true;
				mimetype=IOUtils.getMimeType(data);
				if (IOUtils.APPLICATION_JAVA_SERIALIZED_OBJECT.equals(mimetype))
				{
					ObjectInputStream ois=new AdvancedObjectInputStream(new ByteArrayInputStream(data), classLoader);
					object=ois.readObject();
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
				throwable=e;
			}
		}
	}

	public Object getObject()
	{
		return object;
	}

	public byte[] getData()
	{
		return data;
	}

	public String getMimeType()
	{
		return mimetype;
	}

	public String toString()
	{
		if (loaded)
		{
			StringBuffer buffer=new StringBuffer("BLOB [");
			buffer.append(mimetype!=null ? mimetype : "unknown type");
			buffer.append("; ");
			buffer.append(data!=null ? data.length : 0);
			buffer.append(" bytes");
			if (object!=null)
			{
				buffer.append("; ");
				buffer.append(getClassName(object.getClass()));
			}
			buffer.append("]");
			return buffer.toString();
		}
		else return "BLOB [Not loaded]";
	}

	private static String getClassName(Class aClass)
	{
		if (aClass.isArray())
		{
			Class componentClass=aClass.getComponentType();
			return componentClass.getName()+"[]";
		}
		return aClass.getName();
	}

	public boolean isLoaded()
	{
		return loaded;
	}

	public Throwable getThrowable()
	{
		return throwable;
	}

	private static class AdvancedObjectInputStream extends ObjectInputStream
	{
		private ClassLoader classLoader;

		private AdvancedObjectInputStream(InputStream in, ClassLoader classLoader) throws IOException
		{
			super(in);
			this.classLoader=classLoader;
		}

		protected Class resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException
		{
			try
			{
				return super.resolveClass(desc);
			}
			catch (ClassNotFoundException e)
			{
				return classLoader.loadClass(desc.getName());
			}
		}
	}
}
