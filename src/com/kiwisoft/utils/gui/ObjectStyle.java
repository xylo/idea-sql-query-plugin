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
import java.awt.Font;
import java.util.List;
import java.util.Collections;
import java.util.ArrayList;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.6 $, $Date: 2006/03/24 18:52:27 $
 */
public class ObjectStyle
{
	private Color foreground;
	private Color background;
	private List decorators;
	private Font font;

	public ObjectStyle()
	{
	}

	public ObjectStyle(ComponentDecorator decorator)
	{
		if (decorator!=null) this.decorators=Collections.singletonList(decorator);
	}

	public ObjectStyle(Color foreground, Color background)
	{
		this.foreground=foreground;
		this.background=background;
	}

	public ObjectStyle(Color foreground, Color background, ComponentDecorator decorator)
	{
		this.foreground=foreground;
		this.background=background;
		if (decorator!=null) this.decorators=Collections.singletonList(decorator);
	}

	public ObjectStyle(Color foreground, Color background, Font font, List decorators)
	{
		this.foreground=foreground;
		this.background=background;
		this.font=font;
		this.decorators=decorators;
	}

	public ObjectStyle(Font font)
	{
		this.font=font;
	}

	public ObjectStyle(ObjectStyle style, Font font)
	{
		this.foreground=style.getForeground();
		this.background=style.getBackground();
		this.decorators=style.getDecorators();
		this.font=font;
	}

	public Color getForeground()
	{
		return foreground;
	}

	public Color getBackground()
	{
		return background;
	}

	public Font getFont()
	{
		return font;
	}

	public List getDecorators()
	{
		if (decorators==null) return Collections.EMPTY_LIST;
		return Collections.unmodifiableList(decorators);
	}

	public boolean equals(Object o)
	{
		if (this==o) return true;
		if (o==null || getClass()!=o.getClass()) return false;

		final ObjectStyle that=(ObjectStyle)o;

		if (background!=null ? !background.equals(that.background) : that.background!=null) return false;
		if (font!=null ? !font.equals(that.font) : that.font!=null) return false;
		if (foreground!=null ? !foreground.equals(that.foreground) : that.foreground!=null) return false;
		return !(decorators!=null ? !decorators.equals(that.decorators) : that.decorators!=null);
	}

	public int hashCode()
	{
		int result;
		result=(foreground!=null ? foreground.hashCode() : 0);
		result=29*result+(background!=null ? background.hashCode() : 0);
		result=29*result+(decorators!=null ? decorators.hashCode() : 0);
		result=29*result+(font!=null ? font.hashCode() : 0);
		return result;
	}

	public ObjectStyle combine(ObjectStyle style)
	{
		return new ObjectStyle(
				combineColors(getForeground(), style.getForeground()),
				combineColors(getBackground(), style.getBackground()),
				getFont()!=null ? getFont() : style.getFont(),
				combineDecorators(this, style));
	}

	public static Color combineColors(Color color1, Color color2)
	{
		if (color1==null) return color2;
		if (color2==null) return color1;
		return new Color(color1.getRed()+color2.getRed()-255, color1.getGreen()+color2.getGreen()-255, color1.getBlue()+color2.getBlue()-255);
	}

	private List combineDecorators(ObjectStyle style1, ObjectStyle style2)
	{
		List decorators=new ArrayList();
		decorators.addAll(style1.getDecorators());
		decorators.addAll(style2.getDecorators());
		return decorators;
	}
}
