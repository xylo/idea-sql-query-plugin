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

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.3 $, $Date: 2006/03/24 18:39:09 $
 */
public class ByteArrayDialog extends DialogWrapper
{
	private byte[] data;
	private Action saveAction;
	private JScrollPane scrlContent;

	public ByteArrayDialog(Project project, String title, byte[] data, Action saveAction)
	{
		super(project, true);
		setTitle(title);
		this.data=data;
		this.saveAction=saveAction;
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
		ByteArrayField tfContent=new ByteArrayField(data);
		tfContent.setEditable(false);
		scrlContent=new JScrollPane(tfContent);
		scrlContent.setPreferredSize(new Dimension(600, 400));

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
