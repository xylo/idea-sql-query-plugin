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
package com.kiwisoft.utils.gui.inspect;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.tree.DefaultTreeModel;

import com.kiwisoft.utils.gui.IconManager;
import com.kiwisoft.utils.gui.BaseIcons;
import com.kiwisoft.utils.gui.tree.DynamicTreeNode;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.5 $, $Date: 2006/03/24 18:52:27 $
 */
public class ObjectNode extends DynamicTreeNode
{
	private String name;
	private Class valueClass;
	private Object value;
	private boolean inArray;
	private ObjectHandler objectHandler;

	/**
	 * Creates a new tree node.
	 */
	public ObjectNode(String name, Class valueClass, Object value, boolean array, DefaultTreeModel aTreeModel)
	{
		super(null, aTreeModel);
		this.name=name;
		this.valueClass=valueClass;
		this.value=value;
		this.inArray=array;
		if (value!=null)
		{
			if (value.getClass().isArray())
			{
				Class componentType=value.getClass().getComponentType();
				if (!componentType.isPrimitive())
					objectHandler=new ObjectArrayHandler(value);
				else if (componentType==Character.TYPE)
					objectHandler=new CharArrayHandler(value);
				else if (componentType==Boolean.TYPE)
					objectHandler=new BooleanArrayHandler(value);
				else if (componentType==Byte.TYPE)
					objectHandler=new ByteArrayHandler(value);
				else if (componentType==Short.TYPE)
					objectHandler=new ShortArrayHandler(value);
				else if (componentType==Integer.TYPE)
					objectHandler=new IntArrayHandler(value);
				else if (componentType==Long.TYPE)
					objectHandler=new LongArrayHandler(value);
				else if (componentType==Float.TYPE)
					objectHandler=new FloatArrayHandler(value);
				else if (componentType==Double.TYPE) objectHandler=new DoubleArrayHandler(value);
			}
			else
			{
				if (valueClass.isPrimitive())
					objectHandler=new PrimitiveObjectHandler();
				else
					objectHandler=new DefaultObjectHandler();
			}
		}
	}

	/**
	 * Returns if this tree node has children.
	 */
	public boolean isLeaf()
	{
		return value==null || !objectHandler.hasChildren();
	}

	/**
	 * Method called to load all child nodes.
	 */
	protected void loadChildren()
	{
		if (value!=null) objectHandler.loadChildren();
		super.loadChildren();
	}

	/**
	 * Returns the name used in the user interface.
	 */
	public String getText()
	{
		StringBuffer buffer=new StringBuffer(name);
		if (!inArray)
		{
			buffer.append(": ");
			buffer.append(formatClass(valueClass));
		}
		buffer.append(" = ");
		buffer.append(formatValue(value));
		return buffer.toString();
	}

	public static String formatClass(Class objectClass)
	{
		if (objectClass.isArray()) return objectClass.getComponentType().getName()+"[]";
		return objectClass.getName();
	}

	private String formatValue(Object value)
	{
		if (value==null) return "null";
		if (value.getClass().isArray())
		{
			Class componentType=value.getClass().getComponentType();
			return componentType.getName()+"["+objectHandler.length()+"]";
		}
		if (value instanceof String) return "\""+value+'"';
		if (value instanceof Character) return "'"+value+"'";
		try
		{
			return String.valueOf(value);
		}
		catch (Exception e)
		{
			return value.getClass()+"@"+value.hashCode();
		}
	}

	/**
	 * Returns the icon used in the user interface.
	 */
	public ImageIcon getIcon()
	{
		String icon=BaseIcons.BEAN;
		if (value!=null)
		{
			if (value.getClass().isArray()) icon=BaseIcons.ARRAY_FIELD;
			else if (valueClass.isPrimitive()) icon=BaseIcons.PRIMITIVE_FIELD;
		}
		return IconManager.getIcon(icon);
	}

	private abstract static class ObjectHandler
	{
		public abstract int length();

		public abstract void loadChildren();

		public boolean hasChildren()
		{
			return length()>0;
		}
	}

	private class ObjectArrayHandler extends ObjectHandler
	{
		private Object[] array;

		public ObjectArrayHandler(Object value)
		{
			this.array=(Object[])value;
		}

		public int length()
		{
			return array.length;
		}

		public boolean hasChildren()
		{
			return true;
		}

		public void loadChildren()
		{
			boolean hasChild=false;
			Class componentType=value.getClass().getComponentType();
			for (int i=0; i<array.length; i++)
			{
				if (array[i]!=null)
				{
					insertNode(new ObjectNode("["+i+']', componentType, array[i], true, treeModel));
					hasChild=true;
				}
			}
			if (!hasChild)
			{
				insertNode(new InfoNode("All values in range are null", InfoNode.INFO, treeModel));
			}
		}
	}

	private class CharArrayHandler extends ObjectHandler
	{
		private char[] array;

		public CharArrayHandler(Object value)
		{
			this.array=(char[])value;
		}

		public int length()
		{
			return array.length;
		}

		public void loadChildren()
		{
			for (int i=0; i<array.length; i++)
			{
				insertNode(new ObjectNode("["+i+']', Character.TYPE, new Character(array[i]), true, treeModel));
			}
		}
	}

	private class BooleanArrayHandler extends ObjectHandler
	{
		private boolean[] array;

		public BooleanArrayHandler(Object value)
		{
			array=(boolean[])value;
		}

		public int length()
		{
			return array.length;
		}

		public void loadChildren()
		{
			for (int i=0; i<array.length; i++)
			{
				insertNode(new ObjectNode("["+i+']', Boolean.TYPE, Boolean.valueOf(array[i]), true, treeModel));
			}
		}
	}

	private class ByteArrayHandler extends ObjectHandler
	{
		private byte[] array;

		public ByteArrayHandler(Object value)
		{
			this.array=(byte[])value;
		}

		public int length()
		{
			return array.length;
		}

		public void loadChildren()
		{
			for (int i=0; i<array.length; i++)
			{
				insertNode(new ObjectNode("["+i+']', Byte.TYPE, new Byte(array[i]), true, treeModel));
			}
		}
	}

	private class ShortArrayHandler extends ObjectHandler
	{
		private short[] array;

		public ShortArrayHandler(Object value)
		{
			this.array=(short[])value;
		}

		public int length()
		{
			return array.length;
		}

		public void loadChildren()
		{
			for (int i=0; i<array.length; i++)
			{
				insertNode(new ObjectNode("["+i+']', Short.TYPE, new Short(array[i]), true, treeModel));
			}
		}
	}

	private class IntArrayHandler extends ObjectHandler
	{
		private int[] array;

		public IntArrayHandler(Object value)
		{
			this.array=(int[])value;
		}

		public int length()
		{
			return array.length;
		}

		public void loadChildren()
		{
			for (int i=0; i<array.length; i++)
			{
				insertNode(new ObjectNode("["+i+']', Integer.TYPE, new Integer(array[i]), true, treeModel));
			}
		}
	}

	private class LongArrayHandler extends ObjectHandler
	{
		private long[] array;

		public LongArrayHandler(Object value)
		{
			this.array=(long[])value;
		}

		public int length()
		{
			return array.length;
		}

		public void loadChildren()
		{
			for (int i=0; i<array.length; i++)
			{
				insertNode(new ObjectNode("["+i+']', Long.TYPE, new Long(array[i]), true, treeModel));
			}
		}
	}

	private class FloatArrayHandler extends ObjectHandler
	{
		private float[] array;

		public FloatArrayHandler(Object value)
		{
			this.array=(float[])value;
		}

		public int length()
		{
			return array.length;
		}

		public void loadChildren()
		{
			for (int i=0; i<array.length; i++)
			{
				insertNode(new ObjectNode("["+i+']', Float.TYPE, new Float(array[i]), true, treeModel));
			}
		}
	}

	private class DoubleArrayHandler extends ObjectHandler
	{
		private double[] array;

		public DoubleArrayHandler(Object value)
		{
			this.array=(double[])value;
		}

		public int length()
		{
			return array.length;
		}

		public void loadChildren()
		{
			for (int i=0; i<array.length; i++)
			{
				insertNode(new ObjectNode("["+i+']', Double.TYPE, new Double(array[i]), true, treeModel));
			}
		}
	}

	private static class PrimitiveObjectHandler extends ObjectHandler
	{
		public int length()
		{
			return 0;
		}

		public void loadChildren()
		{
		}
	}

	private class DefaultObjectHandler extends ObjectHandler
	{
		private List fields=new LinkedList();

		public DefaultObjectHandler()
		{
			getFields(value.getClass());
		}

		public int length()
		{
			return fields.size();
		}

		public void loadChildren()
		{
			Iterator it=fields.iterator();
			while (it.hasNext())
			{
				Field field=(Field)it.next();
				field.setAccessible(true);
				try
				{
					insertNode(new ObjectNode(field.getName(), field.getType(), field.get(value), false, treeModel));
				}
				catch (IllegalArgumentException e)
				{
					e.printStackTrace();
				}
				catch (IllegalAccessException e)
				{
					e.printStackTrace();
				}
				field.setAccessible(false);
			}
		}

		public void getFields(Class aClass)
		{
			Field[] fieldArray=aClass.getDeclaredFields();
			fields.addAll(Arrays.asList(fieldArray));
			Class superClass=aClass.getSuperclass();
			if (superClass!=null) getFields(superClass);
		}
	}
}
