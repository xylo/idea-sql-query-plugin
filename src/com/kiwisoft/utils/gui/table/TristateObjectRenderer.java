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

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import com.kiwisoft.utils.Tristate;
import com.kiwisoft.utils.gui.TristateBox;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.5 $, $Date: 2006/03/24 18:04:07 $
 */
public class TristateObjectRenderer extends TristateBox implements ObjectRenderer
{
	protected static Border noFocusBorder=new EmptyBorder(1, 1, 1, 1);

	private List decorators;
	private Color unselectedForeground;
	private Color rowBackground;
	private Color cellBackground;

	public TristateObjectRenderer()
	{
		setHorizontalAlignment(JLabel.CENTER);
	}

	public void setCellForeground(Color fg)
	{
		super.setForeground(fg);
		unselectedForeground=fg;
	}

	public void setRowBackground(Color background)
	{
		rowBackground=background;
	}

	public void setCellBackground(Color background)
	{
		cellBackground=background;
	}

	public void setRowForeground(Color foreground)
	{
	}

	public void setTextFont(Font font)
	{
	}

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
	{
        RendererFactory.initializeRowColor(table, this, row);
		if (isSelected)
		{
			super.setForeground(table.getSelectionForeground());
			super.setBackground(table.getSelectionBackground());
		}
		else
		{
			super.setForeground((unselectedForeground!=null) ? unselectedForeground : table.getForeground());
			if (cellBackground!=null) setBackground(cellBackground);
			else if (rowBackground!=null) setBackground(rowBackground);
			else setBackground(table.getBackground());
		}
		if (value instanceof Tristate)
			setState((Tristate)value);
		else if (value instanceof Boolean)
			setState(Tristate.getTristate((Boolean)value));
		else
			setState(Tristate.UNDEFINED);
		setFont(table.getFont());

		if (hasFocus)
		{
			setBorder(UIManager.getBorder("Table.focusCellHighlightBorder"));
			if (table.isCellEditable(row, column))
			{
				super.setForeground(UIManager.getColor("Table.focusCellForeground"));
				super.setBackground(UIManager.getColor("Table.focusCellBackground"));
			}
		}
		else
		{
			setBorder(noFocusBorder);
		}

		return this;
	}

	protected void paintComponent(Graphics g)
	{
		RendererFactory.paintBackground(g, this, cellBackground, rowBackground);
		RendererFactory.prepareComponent(g, this, decorators);
		super.paintComponent(g);
		RendererFactory.decorateComponent(g, this, decorators);
	}

	/**
	 * Notification from the <code>UIManager</code> that the look and feel
	 * [L&F] has changed.
	 * Replaces the current UI object with the latest version from the
	 * <code>UIManager</code>.
	 *
	 * @see javax.swing.JComponent#updateUI
	 */
	public void updateUI()
	{
		super.updateUI();
		setCellForeground(null);
		setBackground(null);
	}

	public void setDecorators(List decorators)
	{
		this.decorators=decorators;
	}

	public ObjectRenderer cloneRenderer()
	{
		return new TristateObjectRenderer();
	}
}
