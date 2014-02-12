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
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import javax.swing.*;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.3 $, $Date: 2006/03/24 18:39:09 $
 */
public class ImageDialog extends DialogWrapper
{
	private ImageIcon icon;
	private Action saveAction;
	private JPanel panel;

	public ImageDialog(Project project, String title, ImageIcon icon, Action saveAction)
	{
		super(project, true);
		setTitle(title);
		this.icon=icon;
		this.saveAction=saveAction;
		initializeComponents();
		init();
	}

	public JComponent createCenterPanel()
	{
		return panel;
	}

	public Action[] createActions()
	{
		if (saveAction!=null) return new Action[]{saveAction, new CloseAction()};
		else return new Action[]{new CloseAction()};
	}

	private void initializeComponents()
	{
		ImagePanel imagePanel=new ImagePanel(icon);
		Dimension imageSize=imagePanel.getPreferredSize();
		JScrollPane scrollPane=new JScrollPane(imagePanel);
		Dimension viewSize=new Dimension(Math.max(160, Math.min(600, imageSize.width+scrollPane.getVerticalScrollBar().getPreferredSize().width)),
										 Math.max(90, Math.min(600, imageSize.height+scrollPane.getHorizontalScrollBar().getPreferredSize().height)));
		scrollPane.setPreferredSize(viewSize);

		JLabel label=new JLabel("Width: "+icon.getIconWidth()+", Height: "+icon.getIconHeight());

		panel=new JPanel(new GridBagLayout());
		panel.add(scrollPane,
				  new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		panel.add(label,
				  new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 0, 0, 0), 0, 0));
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
