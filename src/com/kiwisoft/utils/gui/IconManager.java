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

import java.util.Hashtable;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.Icon;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.4 $, $Date: 2006/03/24 18:52:26 $
 */
public class IconManager
{
	public static final Icon ICON_16X16=new ImageIcon(new BufferedImage(16, 16, BufferedImage.TYPE_4BYTE_ABGR));

	private static Hashtable icons;

	public static ImageIcon getIcon(String resource)
	{
		if (icons==null) icons=new Hashtable();
		ImageIcon icon=(ImageIcon)icons.get(resource);
		if (icon==null)
		{
			icon=new ImageIcon(IconManager.class.getResource(resource));
			icons.put(resource, icon);
		}
		return icon;
	}

	public static ImageIcon loadIcon(String resource)
	{
		return new ImageIcon(IconManager.class.getResource(resource));
	}

	private IconManager()
	{
	}
}
