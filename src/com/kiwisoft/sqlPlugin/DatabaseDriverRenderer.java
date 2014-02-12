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
package com.kiwisoft.sqlPlugin;

import java.awt.*;
import javax.swing.*;

import com.kiwisoft.db.driver.DatabaseDriver;
import com.kiwisoft.utils.gui.IconManager;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.4 $, $Date: 2006/03/24 17:59:51 $
 */
public class DatabaseDriverRenderer extends DefaultListCellRenderer
{
    private ImageIcon okIcon;
    private ImageIcon errorIcon;
    private ImageIcon unknownIcon;

	public DatabaseDriverRenderer()
    {
        okIcon=IconManager.getIcon(Icons.DRIVER_OK);
        errorIcon=IconManager.getIcon(Icons.DRIVER_ERROR);
        unknownIcon=IconManager.getIcon(Icons.DRIVER_UNKOWN);
    }

    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
    {
        DatabaseDriver driver=(DatabaseDriver) value;
        switch (driver.getStatus())
        {
            case DatabaseDriver.LOADED:
                setIcon(okIcon);
                break;
            case DatabaseDriver.ERROR:
                setIcon(errorIcon);
                break;
            default:
                setIcon(unknownIcon);
        }
        setText(driver.toString());
        if (!isSelected)
        {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }
        else
        {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        }
        return this;
    }
}
