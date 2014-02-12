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
package com.kiwisoft.sqlPlugin.actions;

import java.awt.Window;
import javax.swing.JPanel;
import javax.swing.ImageIcon;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

import com.kiwisoft.sqlPlugin.Icons;
import com.kiwisoft.sqlPlugin.SQLPluginVersionInfo;
import com.kiwisoft.utils.gui.IconManager;
import com.kiwisoft.utils.gui.AboutWindow;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.6 $, $Date: 2006/03/24 17:58:14 $
 */
public class AboutAction extends AnAction
{
	private JPanel panel;

	public AboutAction(JPanel panel)
	{
		super("About");
		getTemplatePresentation().setIcon(IconManager.getIcon(Icons.ABOUT));
		getTemplatePresentation().setDescription("Information about this plugin.");
		this.panel=panel;
	}

	public void actionPerformed(AnActionEvent e)
	{
		ImageIcon image=IconManager.loadIcon(Icons.LOGO);
		new AboutWindow((Window)panel.getTopLevelAncestor(), SQLPluginVersionInfo.getInstance(), image).show();
	}
}
