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

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.swing.AbstractAction;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.UIManager;

import com.kiwisoft.utils.DateUtils;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.5 $, $Date: 2006/03/24 18:58:20 $
 */
public class CalendarView extends JPanel implements MouseListener
{
	private Calendar firstOfMonth;
	private int rows;
	private int cellwidth;
	private int cellheight;
	private Calendar selectionStart;
	private Calendar selectionEnd;
	private int selectionModus=CalendarDialog.DAY;

	private Color colNormal;
	private Color colSelected;
	private Color colCurrent;
	private Color colWeekend;
	private Color colNotInMonth;
	private Color colNotInMonthSelected;
	private Calendar firstDateVisible;
	private Calendar lastDateVisible;

	public CalendarView(Calendar aDate)
	{
		super();
		setFont(UIManager.getFont("Label.font"));
		setMinimumSize(new Dimension(100,100));

		colNormal=Color.white;
		colSelected=Color.blue;
		colCurrent=new Color(200,255,200);
		colWeekend=new Color(255,200,200);
		colNotInMonth=new Color(200,200,200);
		colNotInMonthSelected=new Color(100,100,222);

		addMouseListener(this);
		getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN,0),"down");
		getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_UP,0),"up");
		getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT,0),"left");
		getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT,0),"right");
		getActionMap().put("down",new AbstractAction()
		{
			public void actionPerformed(ActionEvent event)
			{
				if (selectionStart==null) setSelection(firstOfMonth,firstOfMonth);
				else
				{
					Calendar date=(Calendar)selectionStart.clone();
					date.add(Calendar.DATE,7);
					if (isVisible(date)) setSelection(date,date);
				}
			}
		});
		getActionMap().put("up",new AbstractAction()
		{
			public void actionPerformed(ActionEvent event)
			{
				if (selectionStart==null) setSelection(firstOfMonth,firstOfMonth);
				else
				{
					Calendar date=(Calendar)selectionStart.clone();
					date.add(Calendar.DATE,-7);
					if (isVisible(date)) setSelection(date,date);
				}
			}
		});
		getActionMap().put("left",new AbstractAction()
		{
			public void actionPerformed(ActionEvent event)
			{
				if (selectionStart==null) setSelection(firstOfMonth,firstOfMonth);
				else
				{
					Calendar date=(Calendar)selectionStart.clone();
					date.add(Calendar.DATE,-1);
					if (isVisible(date)) setSelection(date,date);
				}
			}
		});
		getActionMap().put("right",new AbstractAction()
		{
			public void actionPerformed(ActionEvent event)
			{
				if (selectionStart==null) setSelection(firstOfMonth,firstOfMonth);
				else
				{
					Calendar date=(Calendar)selectionStart.clone();
					date.add(Calendar.DATE,1);
					if (isVisible(date)) setSelection(date,date);
				}
			}
		});

		setDate(aDate);
	}

	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);

		firstDateVisible=getDateForCell(0,0);
		lastDateVisible=getDateForCell(6,rows-1);

		cellwidth=(getSize().width-20)/7;
		cellheight=(getSize().height-20)/6;

		int width=cellwidth*7+20;
		int height=cellheight*6+20;

		g.setColor(new Color(230,230,230));
		g.fillRect(0,0,width,height);

		int weekInYear=firstOfMonth.get(Calendar.WEEK_OF_YEAR);
		g.setColor(Color.black);
		for (int j=0;j<6;j++) g.drawString(String.valueOf(weekInYear+j),3,40+j*cellheight);

		SimpleDateFormat weekdays=new SimpleDateFormat("E");
		Calendar now=Calendar.getInstance();
		DateUtils.setStartOfWeek(now);
		for (int j=0;j<7;j++)
		{
			g.drawString(weekdays.format(now.getTime()),25+j*cellwidth,17);
			now.add(Calendar.DATE,1);
		}

		for (int j=0;j<6;j++)
		{
			for (int i=0;i<7;i++) renderCell(g,i,j);
		}

		g.setColor(Color.black);
		g.drawRect(0,0,width,height);
		g.drawRect(1,1,width-2,height-2);
	}

	public void renderCell(Graphics g,int x,int y)
	{
		Calendar date=getDateForCell(x,y);
		boolean selected=isDateSelected(date);
		if (isInMonth(date))
		{
			if (selected) g.setColor(colSelected);
			else
			{
				if (DateUtils.isToday(date)) g.setColor(colCurrent);
				else
					if (DateUtils.isWeekend(date)) g.setColor(colWeekend);
					else g.setColor(colNormal);
			}
		}
		else
		{
			if (selected) g.setColor(colNotInMonthSelected);
			else g.setColor(colNotInMonth);
		}
		g.fillRect(20+x*cellwidth,20+y*cellheight,cellwidth,cellheight);
		g.setColor(Color.black);
		g.drawRect(20+x*cellwidth,20+y*cellheight,cellwidth,cellheight);
		if (selected) g.setColor(Color.white);

		FontMetrics fontMetrics=getFontMetrics(getFont());
		String str=String.valueOf(date.get(Calendar.DAY_OF_MONTH));
		Rectangle2D strBounds=fontMetrics.getStringBounds(str, g);
		g.drawString(str,x*cellwidth+20+(int)((cellwidth-strBounds.getWidth())/2),
				y*cellheight+30+(int)((cellheight-strBounds.getHeight())/2));
	}

	public void setDate(Calendar aDate)
	{
		firstOfMonth=(Calendar)aDate.clone();
		firstOfMonth.set(Calendar.DAY_OF_MONTH,1);
		firstOfMonth.set(Calendar.HOUR_OF_DAY,0);
		firstOfMonth.set(Calendar.MINUTE,0);
		firstOfMonth.set(Calendar.SECOND,0);
		firstOfMonth.set(Calendar.MILLISECOND,0);
		rows=0;
		selectionStart=null;
		selectionEnd=null;
		repaint();
	}

	public boolean isDateSelected(Calendar date)
	{
		if (selectionStart==null || selectionEnd==null) return false;
		return !date.before(selectionStart) && date.before(selectionEnd);
	}

	public boolean isInMonth(Calendar date)
	{
		return date.get(Calendar.MONTH)==firstOfMonth.get(Calendar.MONTH);
	}

	public boolean isVisible(Calendar date)
	{
		if (date.before(firstDateVisible)) return false;
		return !date.after(lastDateVisible);
	}

	public Calendar getDateForCell(int x,int y)
	{
		int currentDay=7*y+x+1-DateUtils.getDayOfWeek(firstOfMonth);
		Calendar date=(Calendar)firstOfMonth.clone();
		date.set(Calendar.DAY_OF_MONTH,currentDay);
		return date;
	}

	public Calendar[] getSelection()
	{
		return new Calendar[]{selectionStart,selectionEnd};
	}

	public void setSelection(Calendar start,Calendar end)
	{
		if (start==null || end==null)
		{
			selectionStart=null;
			selectionEnd=null;
		}
		else
		{
			if (selectionModus==CalendarDialog.WEEK)
			{
				selectionStart=(Calendar)start.clone();
				if (selectionModus==CalendarDialog.WEEK) DateUtils.setStartOfWeek(selectionStart);
				selectionEnd=(Calendar)start.clone();
				if (selectionModus==CalendarDialog.WEEK) DateUtils.setEndOfWeek(selectionEnd);
			}
			else
			{
				selectionStart=(Calendar)start.clone();
				selectionEnd=(Calendar)start.clone();
				selectionEnd.add(Calendar.DATE,1);
			}
		}
		repaint();
	}

	public void setSelectionModus(int modus)
	{
		if (selectionModus!=modus)
		{
			selectionModus=modus;
			setSelection(selectionStart,selectionEnd);
		}
	}

	public void mouseClicked(MouseEvent event)
	{
		requestFocus();
		if ((event.getModifiers()&MouseEvent.BUTTON1_MASK)>0)
		{
			Point point=event.getPoint();
			Calendar date=getDateForCell((point.x-20)/cellwidth,(point.y-20)/cellheight);
			setSelection(date,date);
			repaint();
		}
	}

	public void mousePressed(MouseEvent event)
	{
	}

	public void mouseReleased(MouseEvent event)
	{
	}

	public void mouseEntered(MouseEvent event)
	{
	}

	public void mouseExited(MouseEvent event)
	{
	}

	public boolean isRequestFocusEnabled()
	{
		return true;
	}
}
