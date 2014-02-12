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

import java.awt.Component;
import java.awt.Color;
import java.awt.Font;
import javax.swing.table.TableCellRenderer;
import javax.swing.JTable;

import com.kiwisoft.utils.gui.ObjectStyle;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.5 $, $Date: 2006/03/24 18:50:48 $
 */
public class StyleRenderer implements TableCellRenderer
{
	private ObjectRenderer renderer;
	private ObjectStyle style;

	public StyleRenderer(ObjectRenderer renderer, ObjectStyle style)
	{
		this.renderer=renderer.cloneRenderer();
		this.style=style;
	}

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
	{
		Color foreground=style.getForeground();
		if (foreground!=null) renderer.setCellForeground(foreground);
		Color background=style.getBackground();
		if (background!=null) renderer.setCellBackground(background);
		Font font=style.getFont();
		if (font!=null) renderer.setTextFont(font);
		renderer.setDecorators(style.getDecorators());
		return renderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
	}
}
