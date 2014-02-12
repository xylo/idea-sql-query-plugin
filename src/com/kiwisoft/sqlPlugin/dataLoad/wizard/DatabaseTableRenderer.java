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
package com.kiwisoft.sqlPlugin.dataLoad.wizard;

import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JList;
import javax.swing.UIManager;

import com.kiwisoft.db.DatabaseTable;
import com.kiwisoft.sqlPlugin.Icons;
import com.kiwisoft.utils.gui.IconManager;

/**
 * @author Stefan Stiller
 * @version $Revision: $, $Date: $
 */
public class DatabaseTableRenderer extends DefaultListCellRenderer
{
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
	{
		Icon icon=null;
		if (value instanceof DatabaseTable)
		{
			DatabaseTable table=(DatabaseTable)value;
			value=table.getTableName();
			String type=table.getType();
			if (type!=null)
			{
				if ("SYNONYM".equals(type) || "ALIAS".equals(type)) icon=IconManager.getIcon(Icons.SYNONYM);
				if (type.indexOf("TEMPORARY")>=0) icon=IconManager.getIcon(Icons.TEMP_TABLE);
				if ("SYSTEM_TABLE".equals(type)) icon=IconManager.getIcon(Icons.SYSTEM_TABLE);
				if ("VIEW".equals(type)) icon=IconManager.getIcon(Icons.VIEW);
			}
			if (icon==null) icon=IconManager.getIcon(Icons.TABLE);
		}

		setComponentOrientation(list.getComponentOrientation());
		if (isSelected)
		{
			setBackground(list.getSelectionBackground());
			setForeground(list.getSelectionForeground());
		}
		else
		{
			setBackground(list.getBackground());
			setForeground(list.getForeground());
		}

		setIcon(icon);
		setText((value==null) ? "" : value.toString());
		setEnabled(list.isEnabled());
		setFont(list.getFont());
		setBorder((cellHasFocus) ? UIManager.getBorder("List.focusCellHighlightBorder") : noFocusBorder);
		return this;
	}

}
