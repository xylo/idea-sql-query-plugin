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

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import javax.swing.*;

import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.project.Project;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.3 $, $Date: 2006/03/24 18:02:56 $
 */
public class TextDialog extends DialogWrapper
{
	private String content;
	private Action saveAction;
	private JScrollPane scrlContent;

	public TextDialog(Project project, String title, String content, Action saveAction)
	{
		super(project, true);
		setTitle(title);
		this.content=content;
		initializeComponents();
		this.saveAction=saveAction;
		init();
	}

	public TextDialog(Project project, String title, String content)
	{
		super(project, true);
		setTitle(title);
		this.content=content;
		initializeComponents();
		init();
	}

	public JComponent createCenterPanel()
	{
		return scrlContent;
	}

	public Action[] createActions()
	{
		if (saveAction!=null) return new Action[]{saveAction, new CloseAction()};
		else return new Action[]{new CloseAction()};
	}

	private void initializeComponents()
	{
		JTextPane tfContent=new JTextPane();
		tfContent.setEditable(false);
		scrlContent=new JScrollPane(tfContent);
		scrlContent.setPreferredSize(new Dimension(600, 400));

		tfContent.setText(content);
		tfContent.setCaretPosition(0);
	}

	private class CloseAction extends AbstractAction
	{
		public CloseAction()
		{
			super("Close");
			putValue(DEFAULT_ACTION, Boolean.TRUE);
		}

		public void actionPerformed(ActionEvent e)
		{
			dispose();
		}
	}
}
