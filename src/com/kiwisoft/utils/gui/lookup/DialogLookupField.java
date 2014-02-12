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
package com.kiwisoft.utils.gui.lookup;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.*;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;

import com.kiwisoft.utils.gui.GuiUtils;
import com.kiwisoft.utils.ListenerSupport;
import com.kiwisoft.utils.Disposable;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.6 $, $Date: 2006/03/24 18:52:28 $
 */
public class DialogLookupField extends JPanel
{
	private JTextField textField;
	private DialogLookup lookup;
	private LookupAction lookupAction;

	public DialogLookupField(Icon icon)
	{
		init(null, icon);
	}

	public DialogLookupField(DialogLookup lookup)
	{
		init(lookup, lookup.getIcon());
	}

	private void init(DialogLookup lookup, Icon icon)
	{
		this.lookup=lookup;
		textField=new JTextField(10);
		lookupAction=new LookupAction(lookup, icon);
		KeyStroke keyStroke=KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, KeyEvent.ALT_MASK);
		textField.getInputMap(JComponent.WHEN_FOCUSED).put(keyStroke, "lookup");
		textField.getActionMap().put("lookup", lookupAction);

		JButton button=GuiUtils.createButton(lookupAction);
		button.setBorderPainted(true);

		setLayout(new GridBagLayout());
		add(textField, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.BOTH,
											  new Insets(0, 0, 0, 0), 0, 0));
		add(button, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.BOTH,
										   new Insets(0, 0, 0, 0), 0, 0));
	}

	public void setWizard(DialogLookup lookup)
	{
		this.lookup=lookup;
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

	public void setLookup(DialogLookup lookup)
	{
		this.lookup=lookup;
		lookupAction.setLookup(lookup);
	}

	public void addDocumentListener(DocumentListener documentListener)
	{
		textField.getDocument().addDocumentListener(documentListener);
	}

	public void removeDocumentListener(DocumentListener documentListener)
	{
		textField.getDocument().removeDocumentListener(documentListener);
	}

	public void setEditable(boolean b)
	{
		textField.setEditable(b);
	}

	public void setEnabled(boolean value)
	{
		textField.setEnabled(value);
		lookupAction.setEnabled(lookup!=null && value);
		super.setEnabled(value);
	}

	private class LookupAction extends AbstractAction
	{
		public LookupAction(DialogLookup lookup, Icon icon)
		{
			super("Lookup", icon);
			putValue(SHORT_DESCRIPTION, "Zoom");
			setEnabled(lookup!=null);
		}

		public void actionPerformed(ActionEvent e)
		{
			if (lookup!=null)
			{
				lookup.open(textField);
			}
		}

		public void setLookup(DialogLookup lookup)
		{
			if (lookup!=null) putValue(SMALL_ICON, lookup.getIcon());
			setEnabled(lookup!=null);
		}
	}

	public void installDocumentListener(ListenerSupport listenerSupport, final DocumentListener listener)
	{
		addDocumentListener(listener);
		listenerSupport.addDisposable(new Disposable()
		{
			public void dispose()
			{
				removeDocumentListener(listener);
			}
		});
	}

}
