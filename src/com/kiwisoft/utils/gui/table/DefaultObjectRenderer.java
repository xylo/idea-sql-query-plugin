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

import java.util.List;
import java.awt.*;
import javax.swing.Icon;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

import com.kiwisoft.utils.format.TextFormat;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.5 $, $Date: 2006/03/24 18:58:20 $
 */
public class DefaultObjectRenderer extends DefaultTableCellRenderer implements ObjectRenderer
{
	private TextFormat format;
	private Font font;
	private List decorators;
	private Color rowBackground;
	private Color cellBackground;
	private Color cellForeground;
	private Color rowForeground;

	public DefaultObjectRenderer(TextFormat format)
	{
		this.format=format;
	}

	public void setCellForeground(Color foreground)
	{
		cellForeground=foreground;
	}

	public void setRowForeground(Color foreground)
	{
		rowForeground=foreground;
	}

	public void setCellBackground(Color background)
	{
		cellBackground=background;
	}

	public void setRowBackground(Color background)
	{
		rowBackground=background;
	}

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
	{
		RendererFactory.initializeRowColor(table, this, row);
		if (SortableTable.TOP.equals(table.getClientProperty(SortableTable.VERTICAL_CELL_ALIGNMENT)))
			setVerticalAlignment(SwingConstants.TOP);
		setHorizontalAlignment(format.getHorizontalAlignment(value));
		String text=format.format(value);
		if (text==null) text=(String)table.getClientProperty(SortableTable.NULL_STRING);
		Icon icon=format.getIcon(value);
		if (icon!=null) setIcon(icon);
		else setIcon(null);
		if (cellBackground!=null) setBackground(cellBackground);
		else if (rowBackground!=null) setBackground(rowBackground);
		else setBackground(table.getBackground());
		if (cellForeground!=null) setForeground(cellForeground);
		else if (rowForeground!=null) setForeground(rowForeground);
		else setForeground(table.getForeground());
		Component rendererComponent=super.getTableCellRendererComponent(table, text, isSelected, hasFocus, row, column);
		if (font!=null) rendererComponent.setFont(font);
		return rendererComponent;
	}

	protected void paintComponent(Graphics g)
	{
		RendererFactory.paintBackground(g, this, cellBackground, rowBackground);
		RendererFactory.prepareComponent(g, this, decorators);
		super.paintComponent(g);
		RendererFactory.decorateComponent(g, this, decorators);
	}

	public void setDecorators(List decorators)
	{
		this.decorators=decorators;
	}

	public void setTextFont(Font font)
	{
		this.font=font;
	}

	public ObjectRenderer cloneRenderer()
	{
		return new DefaultObjectRenderer(format);
	}

	public String toString()
	{
		return "DefaultObjectRenderer[format="+format+"]";
	}
}
