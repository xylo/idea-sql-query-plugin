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
package com.kiwisoft.utils.text;

import java.awt.Color;
import java.awt.Font;

/**
 * Encapsulates the styles used to paint text.
 *
 * @author Stefan Stiller
 * @version $Revision: 1.3 $, $Date: 2006/03/24 18:02:54 $
 */
public class Style
{
	public static final byte DEFAULT=0;
	public static final byte COMMENT1=1;
	public static final byte COMMENT2=2;
	public static final byte COMMENT3=3;
	public static final byte DIGIT=4;
	public static final byte LITERAL1=5;
	public static final byte LITERAL2=6;
	public static final byte KEYWORD1=7;
	public static final byte KEYWORD2=8;
	public static final byte KEYWORD3=9;
	public static final byte KEYWORD4=10;
	public static final byte KEYWORD5=11;
	public static final byte FUNCTION=12;
	public static final byte OPERATOR=13;
	public static final byte MARKUP=14;
	public static final byte LABEL=15;
	public static final byte INVALID=16;
	public static final byte LITERAL3=17;

	public static final byte END=127;

	public static final byte STYLE_COUNT=18;

	/**
	 * The foreground color.
	 */
	private Color foreground;

	/**
	 * The background color.
	 */
	private Color background;

	/**
	 * The font style.
	 */
	private int fontStyle;

	/**
	 * Construct a <code>SyntaxStyle</code>.
	 */
	public Style()
	{
		this(Color.BLACK);
	}

	/**
	 * Construct a <code>SyntaxStyle</code>.
	 */
	public Style(Color foreGround)
	{
		this(foreGround, Font.PLAIN);
	}

	/**
	 * Construct a <code>SyntaxStyle</code>.
	 */
	public Style(Color foreground, int fontStyle)
	{
		this(foreground, new Color(255, 255, 255, 0), fontStyle);
	}

	/**
	 * Construct a <code>SyntaxStyle</code>.
	 */
	private Style(Color foreground, Color background, int fontStyle)
	{
		this.foreground=foreground;
		this.background=background;
		this.fontStyle=fontStyle;
	}

	/**
	 * Set the foreground color.
	 */
	public void setForeground(Color foreground)
	{
		this.foreground=foreground;
	}

	/**
	 * Get the foreground color.
	 */
	public Color getForeground()
	{
		return foreground;
	}

	/**
	 * Set the background color.
	 */
	public void setBackground(Color background)
	{
		this.background=background;
	}

	/**
	 * Get the background color.
	 */
	public Color getBackground()
	{
		return background;
	}

	/**
	 * Set the font style.
	 */
	public void setFontStyle(int fontStyle)
	{
		this.fontStyle=fontStyle;
	}

	/**
	 * Get the font style.
	 */
	public int getFontStyle()
	{
		return fontStyle;
	}

	public static byte getStyleId(String name)
	{
		if ("keyword1".equalsIgnoreCase(name)) return KEYWORD1;
		else if ("keyword2".equalsIgnoreCase(name)) return KEYWORD2;
		else if ("keyword3".equalsIgnoreCase(name)) return KEYWORD3;
		else if ("keyword4".equalsIgnoreCase(name)) return KEYWORD4;
		else if ("keyword5".equalsIgnoreCase(name)) return KEYWORD5;
		else if ("comment1".equalsIgnoreCase(name)) return COMMENT1;
		else if ("comment2".equalsIgnoreCase(name)) return COMMENT2;
		else if ("comment3".equalsIgnoreCase(name)) return COMMENT3;
		else if ("operator".equalsIgnoreCase(name)) return OPERATOR;
		else if ("void".equalsIgnoreCase(name)) return DEFAULT;
		else if ("literal1".equalsIgnoreCase(name)) return LITERAL1;
		else if ("literal2".equalsIgnoreCase(name)) return LITERAL2;
		else if ("literal3".equalsIgnoreCase(name)) return LITERAL3;
		else if ("label".equalsIgnoreCase(name)) return LABEL;
		else if ("function".equalsIgnoreCase(name)) return FUNCTION;
		else if ("invalid".equalsIgnoreCase(name)) return INVALID;
		throw new IllegalArgumentException(name);
	}

}
