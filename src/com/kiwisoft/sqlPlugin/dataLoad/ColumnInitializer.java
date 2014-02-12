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
package com.kiwisoft.sqlPlugin.dataLoad;

import java.awt.event.MouseMotionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Iterator;
import javax.swing.JComponent;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.3 $, $Date: 2002/02/22 10:00:24 $
 */
public class ColumnInitializer extends JComponent implements MouseMotionListener, MouseListener
{
	private String[] lines;
	private int lineX;
	private int fontHeight;
	private int fontWidth;
	private List columns=new ArrayList();
	private Color alternateColor=new Color(235, 235, 255);
	private Font tickFont=new Font("dialog", Font.PLAIN, 8);

	public ColumnInitializer(String[] lines)
	{
		this.lines=lines!=null ? lines : new String[0];
		addMouseMotionListener(this);
		addMouseListener(this);
		setFont(new Font("monospaced", Font.PLAIN, 12));
		setBackground(Color.WHITE);
	}

	public void setFont(Font font)
	{
		super.setFont(font);
		calculatePreferredSize();
	}

	private void calculatePreferredSize()
	{
		FontMetrics fontMetrics=getFontMetrics(getFont());
		fontWidth=fontMetrics.getWidths()[0];
		fontHeight=fontMetrics.getHeight();

		int prefHeight=fontHeight*lines.length+25;
		int prefWidth=30;
		for (int i=0; i<lines.length; i++)
		{
			String line=lines[i];
			int length=line.length()*fontWidth+20;
			if (length>prefWidth) prefWidth=length;
		}
		setPreferredSize(new Dimension(prefWidth, prefHeight));
	}

	protected void paintComponent(Graphics g)
	{
		Dimension size=getSize();

		g.setColor(getBackground());
		g.fillRect(0, 0, size.width, 18);
		g.setColor(getForeground());
		g.setFont(tickFont);
		int length;
		FontMetrics fontMetrics=getFontMetrics(tickFont);
		for (int i=0;+i*fontWidth<size.width; i++)
		{
			int x=i*fontWidth+1;
			if (i%10==0)
			{
				int width=fontMetrics.stringWidth(String.valueOf(i));
				int height=fontMetrics.getHeight();
				g.drawString(String.valueOf(i), x-(width/2), 9+(height/2));
			}
			else
			{
				if (i%5==0) length=2;
				else length=1;
				g.drawLine(x, 10-length, x, 10+length);
			}
		}


		int lastX=0;
		Iterator it=columns.iterator();
		boolean alternate=false;
		while (it.hasNext())
		{
			Integer column=(Integer)it.next();
			int x=column.intValue()*fontWidth+1;
			g.setColor(alternate ? alternateColor : getBackground());
			g.fillRect(lastX, 20, x-lastX, size.height);
			lastX=x;
			alternate=!alternate;
		}
		g.setColor(alternate ? alternateColor : getBackground());
		g.fillRect(lastX, 20, size.width-lastX, size.height);

		g.setFont(getFont());
		int linePos=20+fontHeight;
		g.setColor(getForeground());
		for (int i=0; i<lines.length; i++)
		{
			String line=lines[i];
			g.drawString(line, 2, linePos);
			linePos+=fontHeight;
		}

		g.setColor(Color.BLUE);
		g.drawLine(lineX*fontWidth+1, 0, lineX*fontWidth+1, size.height);

		super.paintComponent(g);
	}

	public List getColumns()
	{
		return Collections.unmodifiableList(columns);
	}

	public void setColumns(List columns)
	{
		this.columns.clear();
		if (columns!=null) this.columns.addAll(columns);
		repaint();
	}

	public void mouseDragged(MouseEvent e)
	{
	}

	public void mouseMoved(MouseEvent e)
	{
		lineX=(e.getX()-2)/fontWidth;
		repaint();
	}

	public void mouseClicked(MouseEvent e)
	{
		if (lineX>0)
		{
			Integer column=new Integer(lineX);
			if (columns.contains(column)) columns.remove(column);
			else columns.add(column);
			Collections.sort(columns);
			repaint();
		}
	}

	public void mousePressed(MouseEvent e)
	{
	}

	public void mouseReleased(MouseEvent e)
	{
	}

	public void mouseEntered(MouseEvent e)
	{
	}

	public void mouseExited(MouseEvent e)
	{
		lineX=-1;
	}

	public void setLines(String[] lines)
	{
		this.lines=lines!=null ? lines : new String[0];
		calculatePreferredSize();
		repaint();
	}
}
