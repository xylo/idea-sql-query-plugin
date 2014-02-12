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
package com.kiwisoft.wizard;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Dimension;
import java.util.Stack;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.project.Project;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.3 $, $Date: 2002/02/22 10:00:24 $
 */
public abstract class WizardDialog extends DialogWrapper
{
	private JPanel pnlContent;
	private JPanel pnlButtons;
	private Stack panes=new Stack();
	private TitledBorder border;
	private Project project;

	protected WizardDialog(Project project, String title)
	{
		super(project, true);
		this.project=project;
		setTitle(title);
		setCrossClosesWindow(false);
	}

	public Project getProject()
	{
		return project;
	}

	public void show()
	{
		init();
		super.show();
	}

	public void dispose()
	{
		while (!panes.isEmpty()) ((WizardPane)panes.pop()).dispose();
		pnlContent=null;
		getContentPane().removeAll();
		super.dispose();
	}

	protected JComponent createCenterPanel()
	{
		if (pnlContent==null)
		{
			pnlContent=new JPanel(new BorderLayout());
			pnlContent.setPreferredSize(new Dimension(600, 400));
			border=new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED));
			border.setTitleFont(UIManager.getFont("Label.font").deriveFont(Font.BOLD));
			pnlContent.setBorder(new CompoundBorder(border, new EmptyBorder(10, 10, 10, 10)));

			pnlButtons=new JPanel(new FlowLayout(FlowLayout.TRAILING, 1, 1));
			pnlButtons.setBorder(new EmptyBorder(10, 0, 0, 0));

			setCurrentPane(getFirstPane());
		}
		return pnlContent;
	}

	protected JComponent createSouthPanel()
	{
		return pnlButtons;
	}

	public boolean canGoBack()
	{
		return panes.size()>1;
	}

	public void setLastPane()
	{
		WizardPane currentPane=(WizardPane)panes.pop();
		if (currentPane!=null)
		{
			currentPane.saveData();
			currentPane.dispose();
		}
		WizardPane lastPane=(WizardPane)panes.peek();

		border.setTitle(lastPane.getTitle());
		pnlContent.removeAll();
		pnlContent.add(lastPane.getComponent(), BorderLayout.CENTER);
		createButtonPanel(lastPane.getActions());
		lastPane.initData();
		pnlContent.updateUI();
	}

	public WizardPane getCurrentPane()
	{
		if (panes.isEmpty())
			return null;
		else
			return (WizardPane)panes.peek();
	}

	public void setCurrentPane(WizardPane pane)
	{
		WizardPane currentPane=getCurrentPane();
		if (currentPane!=null) currentPane.saveData();
		panes.push(pane);

		border.setTitle(pane.getTitle());
		pnlContent.removeAll();
		pnlContent.add(pane.getComponent(), BorderLayout.CENTER);
		createButtonPanel(pane.getActions());
		pane.initData();
		pnlContent.updateUI();
	}

	private void createButtonPanel(Action[] actions)
	{
		pnlButtons.removeAll();
		for (int i=0; i<actions.length; i++)
		{
			Action action=actions[i];
			if (action==null)
			{
				pnlButtons.add(Box.createHorizontalStrut(10));
			}
			else
				pnlButtons.add(new JButton(action));
		}
		pnlButtons.updateUI();
	}

	protected abstract WizardPane getFirstPane();

	public String getHelpTopic()
	{
		return null;
	}
}
