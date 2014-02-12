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

import java.awt.AWTEvent;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.util.Collection;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.EventListenerList;

import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.utils.gui.IconManager;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.2 $, $Date: 2006/03/24 18:04:41 $
 */
public class LookupField extends JPanel implements FocusListener, DocumentListener
{
	private static final String LOOKUP="lookup";
	private static final String CANCEL="cancel";
	private static final String CREATE="create";
	private static final String EDIT="edit";

	private JTextField textField;
	private Lookup lookup;
	private LookupHandler objectHandler;
	private Object value;
	private Action lookupAction;
	private Action editAction;
	private Action createAction;
	private boolean valid=true;
	private EventListenerList listenerList=new EventListenerList();
	private boolean ignoreFocusLost;
	private boolean freeTextAllowed;

	public LookupField()
	{
		this(null);
	}

	public LookupField(Lookup lookup)
	{
		this(lookup, null);
	}

	public LookupField(Lookup lookup, LookupHandler objectHandler)
	{
		this(new JTextField(10), lookup, objectHandler);
	}

	protected LookupField(JTextField textField, Lookup lookup, LookupHandler objectHandler)
	{
		enableEvents(AWTEvent.KEY_EVENT_MASK | AWTEvent.INPUT_METHOD_EVENT_MASK);
		this.lookup=lookup;
		this.objectHandler=objectHandler;

		lookupAction=new LookupAction();
		if (objectHandler!=null)
		{
			if (objectHandler.isCreateAllowed()) createAction=new CreateAction();
			if (objectHandler.isEditAllowed())
			{
				editAction=new EditAction();
				editAction.setEnabled(false);
			}
		}

		this.textField=textField;
		InputMap inputMap=textField.getInputMap();
		ActionMap actionMap=textField.getActionMap();
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), LOOKUP);
		actionMap.put(LOOKUP, lookupAction);
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), CANCEL);
		actionMap.put(CANCEL, new CancelAction());
		if (createAction!=null)
		{
			inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_MASK), CREATE);
			actionMap.put(CREATE, createAction);
		}
		if (editAction!=null)
		{
			inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_E, KeyEvent.CTRL_MASK), EDIT);
			actionMap.put(EDIT, editAction);
		}
		textField.addFocusListener(this);
		textField.getDocument().addDocumentListener(this);

		JButton btnLookup=new JButton(lookupAction);
		btnLookup.setMargin(new Insets(0, 0, 0, 0));
		btnLookup.setFocusable(false);
		btnLookup.setEnabled(lookup!=null);

		JButton btnCreate=null;
		if (createAction!=null)
		{
			btnCreate=new JButton(createAction);
			btnCreate.setMargin(new Insets(0, 0, 0, 0));
			btnCreate.setFocusable(false);
		}

		JButton btnEdit=null;
		if (editAction!=null)
		{
			btnEdit=new JButton(editAction);
			btnEdit.setMargin(new Insets(0, 0, 0, 0));
			btnEdit.setFocusable(false);
		}

		layoutButtons(textField, btnEdit, btnCreate, btnLookup);
	}

	public boolean isFreeTextAllowed()
	{
		return freeTextAllowed;
	}

	public void setFreeTextAllowed(boolean freeTextAllowed)
	{
		this.freeTextAllowed=freeTextAllowed;
	}

	protected void layoutButtons(JTextField textField, JButton btnEdit, JButton btnCreate, JButton btnLookup)
	{
		setLayout(new GridBagLayout());
		int pos=0;
		add(textField, new GridBagConstraints(pos, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.BOTH,
		        new Insets(0, 0, 0, 0), 0, 0));
		if (btnEdit!=null)
		{
			pos++;
			add(btnEdit, new GridBagConstraints(pos, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.BOTH,
					new Insets(0, 0, 0, 0), 0, 0));
		}
		if (btnCreate!=null)
		{
			pos++;
			add(btnCreate, new GridBagConstraints(pos, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.BOTH,
					new Insets(0, 0, 0, 0), 0, 0));
		}
		pos++;
		add(btnLookup, new GridBagConstraints(pos, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.BOTH,
		        new Insets(0, 0, 0, 0), 0, 0));
	}

	public JTextField getTextField()
	{
		return textField;
	}

	public void setLookup(Lookup lookup)
	{
		this.lookup=lookup;
		if (lookup!=null && isEnabled())
			lookupAction.setEnabled(true);
		else
			lookupAction.setEnabled(false);
	}

	public void setHorizontalAlignment(int alignment)
	{
		textField.setHorizontalAlignment(alignment);
	}

	public void setValue(Object value)
	{
		if (value!=null)
		{
			textField.setText(value.toString());
			if (editAction!=null) editAction.setEnabled(true);
		}
		else
		{
			textField.setText("");
			if (editAction!=null) editAction.setEnabled(false);
		}
		this.value=value;
		valid=true;
		fireSelectionEvent();
	}

	public Object getValue()
	{
		return value;
	}

	public String getText()
	{
		return textField.getText();
	}

	public void addSelectionListener(LookupSelectionListener listener)
	{
		listenerList.add(LookupSelectionListener.class, listener);
	}

	public void removeSelectionListener(LookupSelectionListener listener)
	{
		listenerList.remove(LookupSelectionListener.class, listener);
	}

	protected void fireSelectionEvent()
	{
		Object[] listeners=listenerList.getListenerList();
		LookupEvent event=null;
		for (int i=listeners.length-2; i>=0; i-=2)
		{
			if (listeners[i]==LookupSelectionListener.class)
			{
				if (event==null) event=new LookupEvent(this);
				((LookupSelectionListener)listeners[i+1]).selectionChanged(event);
			}
		}
	}

	public void requestFocus()
	{
		textField.requestFocus();
	}

	public void focusGained(FocusEvent e)
	{
	}

	public void focusLost(FocusEvent e)
	{
		canTraverseFocus();
	}

    protected void cancel()
    {
    }

	public boolean canTraverseFocus()
	{
		if (ignoreFocusLost) return true;
		if (!valid && lookup!=null)
		{
			String text=getText();
			if (StringUtils.isEmpty(text)) setValue(null);
			else
			{
				if (freeTextAllowed)
				{
					setValue(text);
					return true;
				}
				else
				{
					final Collection values=lookup.getValues(text, value);
					if (values!=null && values.size()==1)
					{
						setValue(values.iterator().next());
						return true;
					}
					else
					{
						SwingUtilities.invokeLater(new Runnable()
						{
							public void run()
							{
								lookup.open(LookupField.this, values);
							}
						});
						return false;
					}
				}
			}
		}
		return true;
	}

	public void insertUpdate(DocumentEvent e)
	{
		changedUpdate(e);
	}

	public void removeUpdate(DocumentEvent e)
	{
		changedUpdate(e);
	}

	public void changedUpdate(DocumentEvent e)
	{
		valid=false;
		if (editAction!=null) editAction.setEnabled(false);
	}

	public void addActionListener(ActionListener actionListener)
	{
		textField.addActionListener(actionListener);
	}

	public void removeActionListener(ActionListener actionListener)
	{
		textField.removeActionListener(actionListener);
	}

	protected String getLookupIcon()
	{
		return "/com/kiwisoft/utils/icons/lookup.gif";
	}

	public Lookup getLookup()
	{
		return lookup;
	}

	private class LookupAction extends AbstractAction
	{
		public LookupAction()
		{
			super(null, IconManager.getIcon(getLookupIcon()));
		}

		public void actionPerformed(ActionEvent e)
		{
			if (lookup!=null)
			{
				lookup.open(LookupField.this, lookup.getValues(getText(), value));
			}
		}
	}

	private class CreateAction extends AbstractAction
	{
		public CreateAction()
		{
			super(null, IconManager.getIcon("com/kiwisoft/utils/icons/lookup_create.gif"));
		}

		public void actionPerformed(ActionEvent e)
		{
			ignoreFocusLost=true;
			try
			{
				Object newValue=objectHandler.createObject(LookupField.this);
				if (newValue!=null) setValue(newValue);
			}
			finally
			{
				ignoreFocusLost=false;
			}
		}
	}

	private class EditAction extends AbstractAction
	{
		public EditAction()
		{
			super(null, IconManager.getIcon("com/kiwisoft/utils/icons/lookup_edit.gif"));
		}

		public void actionPerformed(ActionEvent e)
		{
			ignoreFocusLost=true;
			try
			{
				Object value=LookupField.this.getValue();
				if (value!=null && valid)
				{
					objectHandler.editObject(value);
					setValue(value);
				}
			}
			finally
			{
				ignoreFocusLost=false;
			}
		}
	}

	public class CancelAction extends AbstractAction
	{
		public void actionPerformed(ActionEvent e)
		{
			setValue(value);
            cancel();
        }
    }
}
