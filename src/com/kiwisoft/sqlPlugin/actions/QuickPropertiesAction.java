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

import java.awt.Component;

import com.intellij.openapi.actionSystem.*;

import com.kiwisoft.utils.gui.IconManager;
import com.kiwisoft.sqlPlugin.Icons;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.4 $, $Date: 2006/03/24 17:58:15 $
 */
public class QuickPropertiesAction extends AnAction
{
	public QuickPropertiesAction()
	{
		super("Quick Settings");
		getTemplatePresentation().setIcon(IconManager.getIcon(Icons.QUICK_PROPERTIES));
		getTemplatePresentation().setDescription("Quick access to some plugin settings.");
	}

	public void actionPerformed(AnActionEvent event)
	{
		DefaultActionGroup actionGroup=new DefaultActionGroup();
		actionGroup.add(new StopOnErrorAction());
		actionGroup.add(new RowLimitAction());
		actionGroup.add(new LoadLargeObjectsAction());
		actionGroup.add(new HighlightPrimaryKeysAction());
		actionGroup.add(new ShowGridAction());
		actionGroup.add(new AlternatingRowColorsAction());
		ActionPopupMenu popupMenu=ActionManager.getInstance().createActionPopupMenu("QuickPropertiesMenu", actionGroup);
		popupMenu.getComponent().show((Component)event.getInputEvent().getSource(), 20, 20);
	}
}
