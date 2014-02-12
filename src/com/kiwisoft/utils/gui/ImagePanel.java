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

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Color;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.3 $, $Date: 2006/03/24 18:52:27 $
 */
public class ImagePanel extends JPanel
{
	private ImageIcon image;

	public ImagePanel(Dimension aSize)
	{
		setPreferredSize(aSize);
		setMinimumSize(aSize);
		setBackground(Color.WHITE);
		setOpaque(true);
	}

	public ImagePanel(ImageIcon anImage)
	{
		this(new Dimension(anImage.getIconWidth(), anImage.getIconHeight()));
		image=anImage;
	}

	public void setImage(ImageIcon anImage)
	{
		image=anImage;
		repaint();
	}

	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		if (image!=null)
		{
			Dimension size=getSize();
			int x=0;
			if (size.width>image.getIconWidth()) x=(size.width-image.getIconWidth())/2;
			int y=0;
			if (size.height>image.getIconHeight()) y=(size.height-image.getIconHeight())/2;
			image.paintIcon(this,g,x,y);
		}
	}



}
