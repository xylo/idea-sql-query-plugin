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
package com.kiwisoft.utils.gui.table;

import java.awt.Container;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Calendar;
import java.util.Date;
import javax.swing.*;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;

import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.utils.format.TextFormat;
import com.kiwisoft.utils.gui.CalendarDialog;
import com.kiwisoft.utils.gui.IconManager;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.5 $, $Date: 2006/03/24 18:04:07 $
 */
public class TableDateField extends JPanel
{
	private TableTextField textField;
	private JButton button;
	private Date date;
	private Class dateClass;
	private TextFormat format;

	public TableDateField(Class dateClass, TextFormat format)
	{
		this.dateClass=dateClass;
		this.format=format;
		textField=new TableTextField();
		textField.setInputVerifier(new Verifier());
		textField.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, KeyEvent.ALT_MASK), "lookup");
		textField.getActionMap().put("lookup", new LookupAction());
		button=new JButton(new LookupAction());
		button.setMargin(new Insets(0, 0, 0, 0));
		button.setFocusable(true);

		setLayout(null);
		add(textField);
		add(button);
	}

	public void setHorizontalAlignment(int alignment)
	{
		textField.setHorizontalAlignment(alignment);
	}

	public void setText(String text)
	{
		textField.setText(text);
	}

	public String getText()
	{
		return textField.getText();
	}

	public Document getDocument()
	{
		return textField.getDocument();
	}

	public void addActionListener(ActionListener actionListener)
	{
		textField.addActionListener(actionListener);
	}

	public void removeActionListener(ActionListener actionListener)
	{
		textField.removeActionListener(actionListener);
	}

	public void setDate(Date date)
	{
		this.date=date;
		if (date!=null)
			textField.setText(format.format(date));
		else
			textField.setText("");
	}

	public Date getDate()
	{
		return date;
	}

	private class LookupAction extends AbstractAction
	{
		public LookupAction()
		{
			super(null, IconManager.getIcon("/com/kiwisoft/utils/gui/table/lookup_date.png"));
			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, KeyEvent.ALT_MASK));
		}

		public void actionPerformed(ActionEvent e)
		{
			Container parent=getTopLevelAncestor();
			CalendarDialog dialog;
			if (parent instanceof Frame) dialog=new CalendarDialog((Frame)parent, getDate());
			else dialog=new CalendarDialog((Dialog)parent, getDate());
			dialog.setSelectionModus(CalendarDialog.DAY);
			if (date!=null) dialog.setSelection(date);
			dialog.setVisible(true);
			Calendar[] selection=dialog.getSelection();
			if (selection!=null && selection.length>0) setDate(selection[0].getTime());
		}
	}

	public void setBounds(int x, int y, int width, int height)
	{
		super.setBounds(x, y, width, height);
		int buttonHeight=Math.min(20, height);
		textField.setBounds(0, 0, width-buttonHeight, height);
		button.setBounds(width-buttonHeight, 0, buttonHeight, buttonHeight);
	}

	public boolean verify()
	{
		InputVerifier inputVerifier=textField.getInputVerifier();
		if (inputVerifier!=null) return inputVerifier.verify(textField);
		return true;
	}

	private class Verifier extends InputVerifier
	{
		public boolean verify(JComponent input)
		{
			String text=((JTextComponent)input).getText();
			if (!StringUtils.isEmpty(text))
			{
				try
				{
					Date date=(Date)format.parse(getText(), dateClass);
					if (date!=null)
					{
						setDate(date);
						return true;
					}
				}
				catch (Exception e)
				{
				}
			}
			else
			{
				setDate(null);
				return true;
			}
			return false;
		}
	}

	protected boolean processKeyBinding(KeyStroke ks, KeyEvent e, int condition, boolean pressed)
	{
		if (!textField.isFocusOwner())
		{
			textField.requestFocus();
			return textField.processKeyBinding(ks, e, condition, pressed);
		}
		return super.processKeyBinding(ks, e, condition, pressed);
	}
}
