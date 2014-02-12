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
import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;

import com.kiwisoft.utils.format.TextFormat;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.6 $, $Date: 2006/03/24 18:58:20 $
 */
public class DefaultObjectEditor extends DefaultCellEditor implements ObjectEditor
{
	private TextFormat format;
	private Class targetClass;

	public DefaultObjectEditor(Class targetClass, TextFormat format)
	{
		super(new JTextField());
		this.targetClass=targetClass;
		this.format=format;
	}

	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column)
	{
		JTextField textField=(JTextField)getComponent();
		textField.setHorizontalAlignment(format.getHorizontalAlignment(value));
		String text=format.format(value);
		return super.getTableCellEditorComponent(table, text, isSelected, row, column);
	}

	public Object getCellEditorValue()
	{
		return format.parse((String)super.getCellEditorValue(), targetClass);
	}

	public void setForeground(Color foreground)
	{
		getComponent().setForeground(foreground);
	}

	public void setBackground(Color background)
	{
		getComponent().setBackground(background);
	}

	public void setQuickEditable(boolean editable)
	{
	}

	public ObjectEditor cloneEditor()
	{
		return new DefaultObjectEditor(targetClass, format);
	}
}
