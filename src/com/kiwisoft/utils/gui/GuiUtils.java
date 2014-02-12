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

import java.awt.*;
import javax.swing.*;

import com.kiwisoft.utils.Utils;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.13 $, $Date: 2006/03/24 18:50:51 $
 */
public class GuiUtils
{
	private GuiUtils()
	{
	}

	public static void arrangeWindow(Window parent, Window child)
	{
		Point posParent;
		if (parent!=null)
			posParent=parent.getLocation();
		else
			posParent=new Point();
		Dimension sizeParent;
		if (parent!=null)
			sizeParent=parent.getSize();
		else
			sizeParent=Toolkit.getDefaultToolkit().getScreenSize();
		Dimension sizeChild=child.getSize();
		Point posChild=new Point();
		posChild.x=Math.max(0, posParent.x+(sizeParent.width-sizeChild.width)/2);
		posChild.y=Math.max(0, posParent.y+(sizeParent.height-sizeChild.height)/2);
		child.setLocation(posChild);
	}

	public static JButton createButton(Action action)
	{
		JButton button=new JButton(action);
		button.setMargin(new Insets(0, 0, 0, 0));
		button.setText(null);
		button.setFocusable(false);
		KeyStroke keyStroke=(KeyStroke)action.getValue(Action.ACCELERATOR_KEY);
		if (keyStroke!=null)
		{
			InputMap inputMap=button.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
			inputMap.put(keyStroke, Action.ACCELERATOR_KEY);
			button.getActionMap().put(Action.ACCELERATOR_KEY, action);
		}
		return button;
	}

	public static JToggleButton createToggleButton(Action action)
	{
		JToggleButton button=new JToggleButton(action);
		button.setMargin(new Insets(1, 1, 1, 1));
		button.setText(null);
		button.setBorderPainted(false);
		KeyStroke keyStroke=(KeyStroke)action.getValue(Action.ACCELERATOR_KEY);
		if (keyStroke!=null)
		{
			InputMap inputMap=button.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
			inputMap.put(keyStroke, Action.ACCELERATOR_KEY);
			button.getActionMap().put(Action.ACCELERATOR_KEY, action);
		}
		return button;
	}

	public static void handleThrowable(Component component, Throwable throwable)
	{
		throwable.printStackTrace();
		JOptionPane.showMessageDialog(component, Utils.getShortClassName(throwable.getClass()), throwable.getMessage(),
									  JOptionPane.ERROR_MESSAGE);
	}

}
