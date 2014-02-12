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
package com.kiwisoft.utils.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.2 $, $Date: 2006/03/24 18:58:20 $
 */
public class ColorBarDecorator implements ComponentDecorator
{
	private Color color;
	private int width;

	public ColorBarDecorator(Color color, int width)
	{
		this.color=color;
		this.width=width;
	}

	public Color getColor()
	{
		return color;
	}

	public int getWidth()
	{
		return width;
	}

	public void prepareComponent(Graphics g, Component component)
	{
		Dimension size=component.getSize();
		Color color=getColor();
		Color color1=color!=null ? color : component.getForeground();
		Color color2=component.getBackground();
		int width=getWidth();
		double diffRed=((double)(color2.getRed()-color1.getRed()))/width;
		double diffGreen=((double)(color2.getGreen()-color1.getGreen()))/width;
		double diffBlue=((double)(color2.getBlue()-color1.getBlue()))/width;
		for (int i=0; i<width; i++)
		{
			int red=color1.getRed()+(int)(diffRed*i);
			int green=color1.getGreen()+(int)(diffGreen*i);
			int blue=color1.getBlue()+(int)(diffBlue*i);
			g.setColor(new Color(red, green, blue));
			g.drawLine(i, 0, i, size.height);
		}
	}

	public void decorateComponent(Graphics g, Component component)
	{
	}
}
