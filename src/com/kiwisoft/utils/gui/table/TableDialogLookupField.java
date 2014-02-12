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

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.*;
import javax.swing.text.Document;

import com.kiwisoft.utils.gui.lookup.DialogLookup;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.4 $, $Date: 2006/03/24 18:04:07 $
 */
public class TableDialogLookupField extends JPanel
{
	private TableTextField textField;
	private JButton button;
	private DialogLookup lookup;

	public TableDialogLookupField(DialogLookup lookup)
	{
		this.lookup=lookup;
		textField=new TableTextField();
		textField.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, KeyEvent.ALT_MASK), "lookup");
		textField.getActionMap().put("lookup", new LookupAction());
		button=new JButton(new LookupAction());
		button.setMargin(new Insets(0, 0, 0, 0));
		button.setFocusable(false);

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

	public DialogLookup getLookup()
	{
		return lookup;
	}

	private class LookupAction extends AbstractAction
	{
		public LookupAction()
		{
			super(null, lookup.getIcon());
			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, KeyEvent.ALT_MASK));
		}

		public void actionPerformed(ActionEvent e)
		{
			lookup.open(textField);
		}
	}

	public void setBounds(int x, int y, int width, int height)
	{
		super.setBounds(x, y, width, height);
		int buttonHeight=Math.min(20, height);
		textField.setBounds(0, 0, width-buttonHeight, height);
		button.setBounds(width-buttonHeight, 0, buttonHeight, buttonHeight);
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
