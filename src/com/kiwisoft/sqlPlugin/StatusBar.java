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

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.*;
import javax.swing.border.BevelBorder;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.4 $, $Date: 2006/03/24 18:15:18 $
 */
public class StatusBar extends JPanel
{
	private JLabel label;
	private JProgressBar progressBar;

	public StatusBar()
	{
		label=new JLabel();
		label.setBorder(new BevelBorder(BevelBorder.LOWERED));

		progressBar=new JProgressBar();
		progressBar.setMinimumSize(new Dimension(100, 20));
		progressBar.setPreferredSize(new Dimension(100, 20));
		progressBar.setBorder(new BevelBorder(BevelBorder.LOWERED));

		setLayout(new GridBagLayout());
		add(label,
		    new GridBagConstraints(0, 0, 1, 1, 0.9, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		add(progressBar,
		    new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
	}

	public void addComponent(JComponent component)
	{
		component.setFocusable(false);
		component.setPreferredSize(new Dimension(component.getPreferredSize().width, 20));
		add(component,
		    new GridBagConstraints(1, 0, 1, 1, 0.1, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
	}

	public void showMessage(String text)
	{
		label.setText(text);
	}

	public void appendMessage(String text)
	{
		StringBuffer buffer=new StringBuffer(label.getText());
		if (buffer.length()>0) buffer.append("   ");
		buffer.append(text);
		label.setText(buffer.toString());
	}

	public void startProgress()
	{
		progressBar.setIndeterminate(true);
	}

	public void stopProgress()
	{
		progressBar.setIndeterminate(false);
	}
}
