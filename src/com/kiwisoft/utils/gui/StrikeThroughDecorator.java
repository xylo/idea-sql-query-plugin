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

import java.awt.Graphics;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Color;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.4 $, $Date: 2006/03/24 18:52:27 $
 */
public class StrikeThroughDecorator implements ComponentDecorator
{
	private Color color;

	public StrikeThroughDecorator()
	{
	}

	public StrikeThroughDecorator(Color color)
	{
		this.color=color;
	}

	public void prepareComponent(Graphics g, Component component)
	{
	}

	public void decorateComponent(Graphics g, Component component)
	{
		Dimension size=component.getSize();
		g.setColor(color!=null ? color : component.getForeground());
		g.drawLine(0, size.height/2, size.width, size.height/2);
	}
}
