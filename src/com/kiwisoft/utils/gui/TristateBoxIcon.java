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
import java.awt.Graphics;
import java.io.Serializable;
import javax.swing.Icon;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.metal.MetalLookAndFeel;

import com.kiwisoft.utils.Tristate;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.3 $, $Date: 2006/03/24 18:50:51 $
 */
public class TristateBoxIcon implements Icon, UIResource, Serializable
{

	private int getControlSize()
	{
		return 13;
	}

	public void paintIcon(Component c, Graphics g, int x, int y)
	{
		TristateBox cb=(TristateBox)c;
		TristateModel model=cb.getModel();
		int controlSize=getControlSize();

		if (model.isEnabled())
		{
			if (cb.isBorderPaintedFlat())
			{
				g.setColor(MetalLookAndFeel.getControlDarkShadow());
				g.drawRect(x+1, y, controlSize-1, controlSize-1);
			}
			if (model.isPressed() && model.isArmed())
			{
				if (cb.isBorderPaintedFlat())
				{
					g.setColor(MetalLookAndFeel.getControlShadow());
					g.fillRect(x+2, y+1, controlSize-2, controlSize-2);
				}
				else
				{
					g.setColor(MetalLookAndFeel.getControlShadow());
					g.fillRect(x, y, controlSize-1, controlSize-1);
					drawPressed3DBorder(g, x, y, controlSize, controlSize);
				}
			}
			else if (!cb.isBorderPaintedFlat())
			{
				drawFlush3DBorder(g, x, y, controlSize, controlSize);
			}
			g.setColor(MetalLookAndFeel.getControlInfo());
		}
		else
		{
			g.setColor(MetalLookAndFeel.getControlShadow());
			g.drawRect(x, y, controlSize-1, controlSize-1);
		}

		Tristate state=model.getState();
		if (state!=null && !state.isUndefinied())
		{
			if (cb.isBorderPaintedFlat()) x++;
			if (state.isTrue()) drawCheck(g, x, y);
			else drawCross(g, x, y);
		}
	}

	/**
	 * This draws a variant "Flush 3D Border"
	 * It is used for things like pressed buttons.
	 */
	private static void drawPressed3DBorder(Graphics g, int x, int y, int w, int h)
	{
		g.translate(x, y);

		drawFlush3DBorder(g, 0, 0, w, h);

		g.setColor(MetalLookAndFeel.getControlShadow());
		g.drawLine(1, 1, 1, h-2);
		g.drawLine(1, 1, w-2, 1);
		g.translate(-x, -y);
	}

	/**
	 * This draws the "Flush 3D Border" which is used throughout the Metal L&F
	 */
	private static void drawFlush3DBorder(Graphics g, int x, int y, int w, int h)
	{
		g.translate(x, y);
		g.setColor(MetalLookAndFeel.getControlDarkShadow());
		g.drawRect(0, 0, w-2, h-2);
		g.setColor(MetalLookAndFeel.getControlHighlight());
		g.drawRect(1, 1, w-2, h-2);
		g.setColor(MetalLookAndFeel.getControl());
		g.drawLine(0, h-1, 1, h-2);
		g.drawLine(w-1, 0, w-2, 1);
		g.translate(-x, -y);
	}

	private void drawCheck(Graphics g, int x, int y)
	{
		g.setColor(Color.black);
		int controlSize=getControlSize();
		g.fillRect(x+3, y+5, 2, controlSize-8);
		g.drawLine(x+(controlSize-4), y+3, x+5, y+(controlSize-6));
		g.drawLine(x+(controlSize-4), y+4, x+5, y+(controlSize-5));
	}

	private void drawCross(Graphics g, int x, int y)
	{
		g.setColor(Color.red);
		int controlSize=getControlSize();
		g.drawLine(x+3, y+3, x+controlSize-4, y+controlSize-4);
		g.drawLine(x+3, y+4, x+controlSize-5, y+controlSize-4);
		g.drawLine(x+4, y+3, x+controlSize-4, y+controlSize-5);

		g.drawLine(x+3, y+controlSize-4, x+controlSize-4, y+3);
		g.drawLine(x+3, y+controlSize-5, x+controlSize-5, y+3);
		g.drawLine(x+4, y+controlSize-4, x+controlSize-4, y+4);
	}

	public int getIconWidth()
	{
		return getControlSize();
	}

	public int getIconHeight()
	{
		return getControlSize();
	}
}
