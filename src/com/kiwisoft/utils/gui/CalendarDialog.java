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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import javax.swing.*;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.5 $, $Date: 2006/03/24 18:58:19 $
 */
public class CalendarDialog extends JDialog implements ActionListener
{
	public final static int DAY=0;
	public final static int WEEK=1;

	private JLabel lblMonth;
	private SimpleDateFormat dateFormat;
	private CalendarView calView;
	private Calendar date;
	private Calendar[] selection;

	public Calendar[] execute()
	{
		show();
		return getSelection();
	}

	public CalendarDialog(Dialog parent, Date date)
	{
		super(parent,"Calendar",true);
		this.date=Calendar.getInstance();
		if (date!=null) this.date.setTime(date);
		initialize();
	}

	public CalendarDialog(Frame parent, Date date)
	{
		super(parent,"Calendar",true);
		this.date=Calendar.getInstance();
		if (date!=null) this.date.setTime(date);
		initialize();
	}

	private void initialize()
	{
		setSize(280,280);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		dateFormat=new SimpleDateFormat("MMMM yyyy");

		JPanel pnlContent=new JPanel();
		pnlContent.setLayout(new GridBagLayout());
		GridBagConstraints c;

		lblMonth=new JLabel(dateFormat.format(date.getTime()));
		lblMonth.setFont(new Font("dialog",Font.BOLD,12));
		c=new GridBagConstraints();
		c.gridx=2; c.gridy=0; c.weightx=0.5; c.insets=new Insets(2,2,2,2);
		pnlContent.add(lblMonth,c);

		JButton btnPrev2=new JButton("<<");
		btnPrev2.setMargin(new Insets(0,0,0,0));
		btnPrev2.setPreferredSize(new Dimension(35,19));
		btnPrev2.setActionCommand("previous year");
		btnPrev2.addActionListener(this);
		c=new GridBagConstraints();
		c.gridx=0; c.gridy=0; c.insets=new Insets(2,2,2,2);
		pnlContent.add(btnPrev2,c);

		JButton btnPrev=new JButton("<");
		btnPrev.setMargin(new Insets(0,0,0,0));
		btnPrev.setPreferredSize(new Dimension(35,19));
		btnPrev.setActionCommand("previous month");
		btnPrev.addActionListener(this);
		c=new GridBagConstraints();
		c.gridx=1; c.gridy=0; c.insets=new Insets(2,2,2,2);
		pnlContent.add(btnPrev,c);

		JButton btnNext=new JButton(">");
		btnNext.setMargin(new Insets(0,0,0,0));
		btnNext.setPreferredSize(new Dimension(35,19));
		btnNext.setActionCommand("next month");
		btnNext.addActionListener(this);
		c=new GridBagConstraints();
		c.gridx=3; c.gridy=0; c.insets=new Insets(2,2,2,2);
		pnlContent.add(btnNext,c);

		JButton btnNext2=new JButton(">>");
		btnNext2.setMargin(new Insets(0,0,0,0));
		btnNext2.setPreferredSize(new Dimension(35,19));
		btnNext2.setActionCommand("next year");
		btnNext2.addActionListener(this);
		c=new GridBagConstraints();
		c.gridx=4; c.gridy=0; c.insets=new Insets(2,2,2,2);
		pnlContent.add(btnNext2,c);

		calView=new CalendarView(date);
		c=new GridBagConstraints();
		c.gridx=0; c.gridy=1; c.fill=GridBagConstraints.BOTH; c.gridwidth=5; c.weightx=1.0; c.weighty=1.0; c.insets=new Insets(2,2,2,2);
		pnlContent.add(calView,c);

		JButton btnOk=new JButton("Ok");
		btnOk.setMnemonic('o');
		btnOk.setActionCommand("apply");
		btnOk.addActionListener(this);

		JButton btnCancel=new JButton("Cancel");
		btnCancel.setMnemonic('c');
		btnCancel.setActionCommand("cancel");
		btnCancel.addActionListener(this);

		JPanel pnlButtons=new JPanel();
		pnlButtons.setLayout(new FlowLayout());
		pnlButtons.add(btnOk);
		pnlButtons.add(btnCancel);
		c=new GridBagConstraints();
		c.gridx=0; c.gridy=2; c.fill=GridBagConstraints.BOTH; c.gridwidth=5; c.insets=new Insets(2,2,2,2);
		pnlContent.add(pnlButtons,c);

		pnlContent.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT,Event.CTRL_MASK),"next year");
		pnlContent.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT,Event.CTRL_MASK),"previous year");
		pnlContent.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT,Event.ALT_MASK),"next month");
		pnlContent.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT,Event.ALT_MASK),"previous month");
		pnlContent.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE,0),"cancel");
		pnlContent.getActionMap().put("previous month", new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				date.add(Calendar.MONTH,-1);
				calView.setDate(date);
				lblMonth.setText(dateFormat.format(date.getTime()));
			}
		});
		pnlContent.getActionMap().put("next month", new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				date.add(Calendar.MONTH,1);
				calView.setDate(date);
				lblMonth.setText(dateFormat.format(date.getTime()));
			}
		});
		pnlContent.getActionMap().put("next year", new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				date.add(Calendar.YEAR,1);
				calView.setDate(date);
				lblMonth.setText(dateFormat.format(date.getTime()));
			}
		});
		pnlContent.getActionMap().put("previous year", new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				date.add(Calendar.YEAR,-1);
				calView.setDate(date);
				lblMonth.setText(dateFormat.format(date.getTime()));
			}
		});
		pnlContent.getActionMap().put("cancel", new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				dispose();
			}
		});

		setContentPane(pnlContent);
		getRootPane().setDefaultButton(btnOk);
	}

//	public boolean isResizable()
//	{
//		return false;
//	}

	public void actionPerformed(ActionEvent event)
	{
		String action=event.getActionCommand();
		if ("previous month".equals(action))
		{
			date.add(Calendar.MONTH,-1);
			calView.setDate(date);
			lblMonth.setText(dateFormat.format(date.getTime()));
			return;
		}
		if ("previous year".equals(action))
		{
			date.add(Calendar.YEAR,-1);
			calView.setDate(date);
			lblMonth.setText(dateFormat.format(date.getTime()));
			return;
		}
		if ("next month".equals(action))
		{
			date.add(Calendar.MONTH,1);
			calView.setDate(date);
			lblMonth.setText(dateFormat.format(date.getTime()));
			return;
		}
		if ("next year".equals(action))
		{
			date.add(Calendar.YEAR,1);
			calView.setDate(date);
			lblMonth.setText(dateFormat.format(date.getTime()));
			return;
		}
		if ("apply".equals(action))
		{
			selection=calView.getSelection();
			dispose();
			return;
		}
		if ("cancel".equals(action))
		{
			dispose();
			return;
		}
	}

	public Calendar[] getSelection()
	{
		return selection;
	}

	public void setDate(Calendar aDate)
	{
		date=(Calendar)aDate.clone();
		calView.setDate(date);
		lblMonth.setText(dateFormat.format(date.getTime()));
	}

	public void setSelectionModus(int modus)
	{
		calView.setSelectionModus(modus);
	}

	public void setSelection(Date start)
	{
		Calendar startCal=Calendar.getInstance();
		startCal.setTime(start);
		startCal.set(Calendar.HOUR_OF_DAY, 0);
		startCal.set(Calendar.MINUTE, 0);
		startCal.set(Calendar.SECOND, 0);
		startCal.set(Calendar.MILLISECOND, 0);
		calView.setSelection(startCal, startCal);
	}

	public void setSelection(Calendar start,Calendar end)
	{
		calView.setSelection(start,end);
	}

	public static void main(String[] args)
	{
		CalendarDialog dialog=new CalendarDialog((Frame)null, new Date())
		{
			public void dispose()
			{
				super.dispose();
				System.exit(0);
			}
		};
		dialog.show();
	}
}