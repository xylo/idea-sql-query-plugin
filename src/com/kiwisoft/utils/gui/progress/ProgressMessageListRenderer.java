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
package com.kiwisoft.utils.gui.progress;

import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.Icon;

import com.kiwisoft.utils.gui.IconManager;

/**
 * @author Stefan Stiller
 * @version $Revision: 1$, $Date: 08.04.04 11:41:51$
 */
public class ProgressMessageListRenderer extends DefaultListCellRenderer
{
	private final static Icon OK_ICON=IconManager.getIcon("/com/kiwisoft/utils/gui/progress/ok.gif");
	private final static Icon ERROR_ICON=IconManager.getIcon("/com/kiwisoft/utils/gui/progress/error.gif");
	private final static Icon WARNING_ICON=IconManager.getIcon("/com/kiwisoft/utils/gui/progress/warning.gif");

	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
	{
		super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		if (value instanceof ProgressMessage)
		{
			ProgressMessage message=(ProgressMessage)value;
			switch (message.getSeverity())
			{
				case ProgressAnimation.OK:
					this.setIcon(OK_ICON);
					break;
				case ProgressAnimation.WARNING:
					this.setIcon(WARNING_ICON);
					break;
				case ProgressAnimation.ERROR:
					this.setIcon(ERROR_ICON);
					break;
				default:
					this.setIcon(null);
					break;
			}
		}

		return this;
	}
}
