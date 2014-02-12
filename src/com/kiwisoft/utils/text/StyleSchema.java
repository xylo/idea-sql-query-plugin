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
 * @author Stefan Stiller
 * @version $Revision: 1.3 $, $Date: 2006/03/24 18:02:54 $
 */
public class StyleSchema
{
	private Style[] styles=new Style[Style.STYLE_COUNT];

	public StyleSchema()
	{
		styles[Style.DEFAULT]=new Style(new Color(0, 0, 0));
		styles[Style.COMMENT1]=new Style(new Color(128, 128, 128));
		styles[Style.COMMENT2]=new Style(new Color(128, 128, 128));
		styles[Style.COMMENT3]=new Style(new Color(0, 0, 160));
		styles[Style.DIGIT]=new Style(new Color(0, 160, 0));
		styles[Style.LITERAL1]=new Style(new Color(64, 128, 255));
		styles[Style.LITERAL2]=new Style(Color.orange.darker(), Font.BOLD);
		styles[Style.LITERAL3]=new Style(Color.magenta.darker(), Font.BOLD);
		styles[Style.KEYWORD1]=new Style(Color.blue.darker(), Font.BOLD);
		styles[Style.KEYWORD2]=new Style(new Color(0, 160, 96));
		styles[Style.KEYWORD3]=new Style(new Color(0, 160, 192));
		styles[Style.KEYWORD4]=new Style(new Color(192, 160, 192));
		styles[Style.KEYWORD5]=new Style(new Color(0, 192, 192));
		styles[Style.FUNCTION]=new Style(new Color(128, 32, 255));
		styles[Style.OPERATOR]=new Style(new Color(0, 0, 0));
		styles[Style.MARKUP]=new Style(new Color(64, 64, 128));
		styles[Style.LABEL]=new Style(new Color(0, 96, 96));
		styles[Style.INVALID]=new Style(new Color(255, 0, 0));
	}

	public Style getStyle(int style)
	{
		return styles[style];
	}

}
