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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.text.DateFormat;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.border.LineBorder;

import com.kiwisoft.utils.VersionInfo;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.3 $, $Date: 2006/03/24 18:33:39 $
 */
public class AboutWindow extends JWindow implements WindowFocusListener, MouseListener
{
	private VersionInfo versionInfo;

	public AboutWindow(Window owner, VersionInfo versionInfo, ImageIcon image)
	{
		super(owner);
		this.versionInfo=versionInfo;
		initialize(image);
		addWindowFocusListener(this);
	}

	private void initialize(ImageIcon image)
	{
		Dimension screenSize=Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((screenSize.width-image.getIconWidth())/2,
		    (screenSize.height-image.getIconHeight())/2);

		int pos=36;
		JLabel lblVersion=new JLabel("Version "+versionInfo.getVersion());
		lblVersion.setBounds(20, pos+=18, 200, 20);
		JLabel lblBuilt=new JLabel("Build #"+versionInfo.getBuildNumber());
		lblBuilt.setBounds(20, pos+=18, 200, 20);
		JLabel lblBuiltDate=new JLabel("Built on "+DateFormat.getDateInstance().format(versionInfo.getBuiltDate()));
		lblBuiltDate.setBounds(20, pos+=18, 200, 20);
		JLabel lblCopyright=new JLabel(versionInfo.getCopyright());
		lblCopyright.setBounds(20, pos+=18, 200, 20);

		JPanel pnlImage=new ImagePanel(image);
		pnlImage.setBorder(new LineBorder(Color.BLACK));
		pnlImage.setFocusable(true);
		pnlImage.add(lblVersion);
		pnlImage.addMouseListener(this);

		setContentPane(pnlImage);
		getLayeredPane().add(lblVersion);
		getLayeredPane().add(lblBuilt);
		getLayeredPane().add(lblBuiltDate);
		getLayeredPane().add(lblCopyright);

		pack();
	}

	public void windowLostFocus(WindowEvent e)
	{
		dispose();
	}

	public void windowGainedFocus(WindowEvent e)
	{
	}

	public void mouseClicked(MouseEvent e)
	{
		dispose();
	}

	public void mouseEntered(MouseEvent e)
	{
	}

	public void mouseExited(MouseEvent e)
	{
	}

	public void mousePressed(MouseEvent e)
	{
	}

	public void mouseReleased(MouseEvent e)
	{
	}
}


