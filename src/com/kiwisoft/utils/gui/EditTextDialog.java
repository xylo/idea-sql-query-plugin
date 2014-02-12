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

import java.awt.HeadlessException;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import javax.swing.*;

import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.project.Project;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.2 $, $Date: 2006/03/24 18:58:20 $
 */
public class EditTextDialog extends DialogWrapper
{
	private JTextPane textPane;
	private JComponent pnlContent;
	private String text;
	private boolean returnValue;

	public EditTextDialog(Project project, String title, String text)
	{
		super(project, false);
		setTitle(title);
		textPane=new JTextPane();
		textPane.setText(text);

		pnlContent=new JScrollPane(textPane);
		pnlContent.setPreferredSize(new Dimension(400, 300));
		init();
	}

	public EditTextDialog(Project project, String text) throws HeadlessException
	{
		this(project, "Text Editor", text);
	}

	public String getText()
	{
		return text;
	}

	public boolean getReturnValue()
	{
		return returnValue;
	}

	public Action[] createActions()
	{
		return new Action[]{new ApplyAction(), new CancelAction()};
	}

	public JComponent createCenterPanel()
	{
		return pnlContent;
	}

	public JComponent getPreferredFocusedComponent()
	{
		return textPane;
	}

	private class ApplyAction extends AbstractAction
	{
		public ApplyAction()
		{
			super("Ok");
			putValue(DEFAULT_ACTION, Boolean.TRUE);
		}

		public void actionPerformed(ActionEvent e)
		{
			text=textPane.getText();
			returnValue=true;
			dispose();
		}
	}

	private class CancelAction extends AbstractAction
	{
		public CancelAction()
		{
			super("Cancel");
		}

		public void actionPerformed(ActionEvent e)
		{
			returnValue=false;
			dispose();
		}
	}
}
