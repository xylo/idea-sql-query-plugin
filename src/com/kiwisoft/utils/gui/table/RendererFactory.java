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
package com.kiwisoft.utils.gui.table;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.*;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import com.kiwisoft.utils.DoubleKeyMap;
import com.kiwisoft.utils.SetMap;
import com.kiwisoft.utils.Tristate;
import com.kiwisoft.utils.format.*;
import com.kiwisoft.utils.gui.ComponentDecorator;
import com.kiwisoft.utils.gui.ObjectStyle;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.5 $, $Date: 2006/03/24 18:58:21 $
 */
public class RendererFactory
{
	private static RendererFactory instance;

	public synchronized static RendererFactory getInstance()
	{
		if (instance==null) instance=new RendererFactory();
		return instance;
	}

	private Map defaultRenderers;
	private DoubleKeyMap renderers=new DoubleKeyMap();
	private DoubleKeyMap createdRenderers=new DoubleKeyMap();
	private DoubleKeyMap styleRenderers=new DoubleKeyMap();

	private RendererFactory()
	{
		defaultRenderers=new HashMap();
		defaultRenderers.put(Object.class, new DefaultObjectRenderer(new DefaultObjectFormat(ObjectFormat.DEFAULT)));
		defaultRenderers.put(Number.class, new DefaultObjectRenderer(new DefaultNumberFormat(ObjectFormat.DEFAULT)));
		defaultRenderers.put(Date.class, new DefaultObjectRenderer(new DefaultDateFormat(ObjectFormat.DEFAULT)));
		defaultRenderers.put(String.class, new DefaultObjectRenderer(new DefaultStringFormat(ObjectFormat.DEFAULT)));
		defaultRenderers.put(Boolean.class, new BooleanObjectRenderer());
		defaultRenderers.put(Tristate.class, new TristateObjectRenderer());

		renderers.put(Boolean.class, "Tristate", new TristateObjectRenderer());
	}

	public SetMap getVariants(Class objectClass)
	{
		SetMap variants=new SetMap();
		variants.add(null, ObjectFormat.DEFAULT);
		Class aClass=objectClass;
		while (aClass!=null)
		{
			for (Iterator it=renderers.getKeys(aClass).iterator(); it.hasNext();)
			{
				variants.add(null, it.next());
			}
			aClass=aClass.getSuperclass();
		}
		for (Iterator it=FormatManager.getInstance().getFormats().iterator(); it.hasNext();)
		{
			ObjectFormat format=(ObjectFormat)it.next();
			if (format.canFormat(objectClass)) variants.add(format.getGroup(), format.getName());
		}
		return variants;
	}

	public ObjectRenderer getRenderer(Class aClass, String variant)
	{
		if (variant==null || ObjectFormat.DEFAULT.equals(variant))
		{
			ObjectRenderer renderer=getDefaultRenderer(aClass);
			defaultRenderers.put(aClass, renderer);
			return renderer;
		}
		else
		{
			ObjectRenderer renderer=getVariantRenderer(aClass, variant);
			if (renderer==null) renderer=getRenderer(aClass, null);
			createdRenderers.put(aClass, variant, renderer);
			return renderer;
		}
	}

	private ObjectRenderer getVariantRenderer(Class aClass, String variant)
	{
		ObjectRenderer renderer=(ObjectRenderer)renderers.get(aClass, variant);
		if (renderer==null) renderer=(ObjectRenderer)createdRenderers.get(aClass, variant);
		if (renderer==null)
		{
			for (Iterator it=FormatManager.getInstance().getFormats().iterator(); it.hasNext();)
			{
				ObjectFormat format=(ObjectFormat)it.next();
				if (variant.equals(format.getName()) && format.canFormat(aClass))
				{
					if (format instanceof BooleanFormat)
						return new BooleanObjectRenderer((BooleanFormat)format);
					return new DefaultObjectRenderer((TextFormat)format);
				}
			}
			if (aClass.getSuperclass()!=null) renderer=getVariantRenderer(aClass.getSuperclass(), variant);
		}
		return renderer;
	}

	private ObjectRenderer getDefaultRenderer(Class aClass)
	{
		if (aClass==null) return null;
		ObjectRenderer renderer=(ObjectRenderer)defaultRenderers.get(aClass);
		if (renderer==null)
		{
			for (Iterator it=FormatManager.getInstance().getFormats().iterator(); it.hasNext();)
			{
				ObjectFormat format=(ObjectFormat)it.next();
				if ((format.getName()==null || ObjectFormat.DEFAULT.equals(format.getName())) && format.canFormat(aClass))
				{
					if (format instanceof BooleanFormat)
						renderer=new BooleanObjectRenderer((BooleanFormat)format);
					else
						renderer=new DefaultObjectRenderer((TextFormat)format);
					break;
				}
			}
			if (renderer==null)
			{
				Class[] interfaces=aClass.getInterfaces();
				for (int i=0; i<interfaces.length && renderer==null; i++)
				{
					Class anInterface=interfaces[i];
					renderer=getDefaultRenderer(anInterface);
				}
			}
			if (renderer==null) renderer=getDefaultRenderer(aClass.getSuperclass());
		}
		return renderer;
	}

	public TableCellRenderer getRenderer(Class aClass, String variant, ObjectStyle style)
	{
		ObjectRenderer renderer=getRenderer(aClass, variant);
		if (style==null) return renderer;
		StyleRenderer styleRenderer=(StyleRenderer)styleRenderers.get(renderer, style);
		if (styleRenderer==null)
		{
			styleRenderer=new StyleRenderer(renderer, style);
			styleRenderers.put(renderer, style, styleRenderer);
		}
		return styleRenderer;
	}

	public void setRenderer(Class aClass, ObjectFormat format)
	{
		ObjectRenderer renderer;
		if (format instanceof BooleanFormat)
			renderer=new BooleanObjectRenderer((BooleanFormat)format);
		else
			renderer=new DefaultObjectRenderer((TextFormat)format);
		setRenderer(aClass, format.getName(), renderer);
	}

	public void setRenderer(Class aClass, String variant, ObjectRenderer renderer)
	{
		if (variant==null || ObjectFormat.DEFAULT.equals(variant))
		{
			defaultRenderers.put(aClass, renderer);
		}
		else
		{
			renderers.put(aClass, variant, renderer);
			createdRenderers.remove(aClass, variant);
		}
	}

	public static void initializeRowColor(JTable table, ObjectRenderer renderer, int row)
	{
		Color color=(Color)table.getClientProperty(SortableTable.ALTERNATE_ROW_BACKGROUND);
		if (color!=null && row%2==0) renderer.setRowBackground(null);
		else renderer.setRowBackground(color);
		color=(Color)table.getClientProperty(SortableTable.ALTERNATE_ROW_FOREGROUND);
		if (color!=null && row%2==0) renderer.setRowForeground(null);
		else renderer.setRowForeground(color);
	}

	public static void decorateComponent(Graphics g, Component component, List decorators)
	{
		if (decorators!=null)
		{
			for (Iterator it=decorators.iterator(); it.hasNext();)
			{
				ComponentDecorator componentDecorator=(ComponentDecorator)it.next();
				componentDecorator.decorateComponent(g, component);
			}
		}
	}

	public static void prepareComponent(Graphics g, Component component, List decorators)
	{
		if (decorators!=null)
		{
			for (Iterator it=decorators.iterator(); it.hasNext();)
			{
				ComponentDecorator componentDecorator=(ComponentDecorator)it.next();
				componentDecorator.prepareComponent(g, component);
			}
		}
	}

	public static void paintBackground(Graphics g, JComponent component, Color cellBackground, Color rowBackground)
	{
		component.setOpaque(false);
		Dimension size=component.getSize();
		if (cellBackground!=null && rowBackground!=null)
		{
			for (int i=0; i<size.width+size.height; i++)
			{
				if (i%4<2) g.setColor(cellBackground);
				else g.setColor(rowBackground);
				g.drawLine(i, 0, i-size.height, size.height);
			}
		}
		else
		{
			g.setColor(component.getBackground());
			g.fillRect(0, 0, size.width, size.height);
		};
	}
}
