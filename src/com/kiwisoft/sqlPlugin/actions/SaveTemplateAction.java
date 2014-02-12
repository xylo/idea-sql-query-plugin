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

import javax.swing.text.JTextComponent;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;

import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.utils.idea.PluginUtils;
import com.kiwisoft.sqlPlugin.settings.EditTemplateDialog;
import com.kiwisoft.sqlPlugin.templates.TemplateManager;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.2 $, $Date: 2006/03/24 17:58:15 $
 */
public class SaveTemplateAction extends AnAction
{
	private Project project;
	private JTextComponent textField;

	public SaveTemplateAction(Project project, JTextComponent textField)
	{
		super("Save as Template...");
		this.project=project;
		this.textField=textField;
	}

	public void update(AnActionEvent event)
	{
		super.update(event);
		String text=textField.getSelectedText();
		if (text==null) text=textField.getText();
		event.getPresentation().setEnabled(!StringUtils.isEmpty(text));
	}

	public void actionPerformed(AnActionEvent e)
	{
		String text=textField.getSelectedText();
		if (text==null) text=textField.getText();
		if (!StringUtils.isEmpty(text))
		{
			EditTemplateDialog dialog=new EditTemplateDialog(project, TemplateManager.getInstance(), null, text);
			PluginUtils.showDialog(dialog, true, true);
		}
	}
}
