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
package com.kiwisoft.utils;

import java.awt.Component;
import java.awt.event.*;
import java.beans.PropertyChangeListener;
import java.util.Observable;
import java.util.Observer;
import java.util.Stack;
import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.JTextComponent;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.5 $, $Date: 2006/03/24 18:50:51 $
 */
public class ListenerSupport
{
	private Stack disposables=new Stack();

	public ListenerSupport()
	{
	}

	public void dispose()
	{
		while (!disposables.isEmpty())
		{
			Disposable disposable=(Disposable)disposables.pop();
			disposable.dispose();
		}
	}

	public void addDisposable(Disposable disposable)
	{
		disposables.push(disposable);
	}

	public void installObserver(final Observable observable, final Observer observer)
	{
		observable.addObserver(observer);
		disposables.push(new Disposable()
		{
			public void dispose()
			{
				observable.deleteObserver(observer);
			}
		});
	}

	public void installActionListener(final JComboBox comboBox, final ActionListener actionListener)
	{
		comboBox.addActionListener(actionListener);
		disposables.push(new Disposable()
		{
			public void dispose()
			{
				comboBox.removeActionListener(actionListener);
			}
		});
	}

	public void installActionListener(final AbstractButton button, final ActionListener actionListener)
	{
		button.addActionListener(actionListener);
		disposables.push(new Disposable()
		{
			public void dispose()
			{
				button.removeActionListener(actionListener);
			}
		});
	}

	public void installChangeListener(final JTabbedPane tabbedPane, final ChangeListener changeListener)
	{
		tabbedPane.addChangeListener(changeListener);
		disposables.push(new Disposable()
		{
			public void dispose()
			{
				tabbedPane.removeChangeListener(changeListener);
			}
		});
	}

	public void installMouseListener(final Component component, final MouseListener mouseListener)
	{
		component.addMouseListener(mouseListener);
		disposables.push(new Disposable()
		{
			public void dispose()
			{
				component.removeMouseListener(mouseListener);
			}
		});
	}

	public void installKeyListener(final Component component, final KeyListener keyListener)
	{
		component.addKeyListener(keyListener);
		disposables.push(new Disposable()
		{
			public void dispose()
			{
				component.removeKeyListener(keyListener);
			}
		});
	}

	public void installDocumentListener(final JTextComponent component, final DocumentListener listener)
	{
		component.getDocument().addDocumentListener(listener);
		disposables.push(new Disposable()
		{
			public void dispose()
			{
				component.getDocument().removeDocumentListener(listener);
			}
		});
	}

	public void installColumnSelectionListener(final ListSelectionModel selectionModel, final ListSelectionListener selectionListener)
	{
		selectionModel.addListSelectionListener(selectionListener);
		disposables.push(new Disposable()
		{
			public void dispose()
			{
				selectionModel.removeListSelectionListener(selectionListener);
			}
		});
	}

	public void installSelectionListener(final JTable table, final ListSelectionListener selectionListener)
	{
		table.getSelectionModel().addListSelectionListener(selectionListener);
		disposables.push(new Disposable()
		{
			public void dispose()
			{
				table.getSelectionModel().removeListSelectionListener(selectionListener);
			}
		});
	}

	public void installItemListener(final AbstractButton button, final ItemListener itemListener)
	{
		button.addItemListener(itemListener);
		disposables.push(new Disposable()
		{
			public void dispose()
			{
				button.removeItemListener(itemListener);
			}
		});
	}

	public void installPropertyChangeListener(final JComponent component, final PropertyChangeListener listener)
	{
		component.addPropertyChangeListener(listener);
		disposables.push(new Disposable()
		{
			public void dispose()
			{
				component.removePropertyChangeListener(listener);
			}
		});
	}

	public void installPropertyChangeListener(final JComponent component, final String property, final PropertyChangeListener listener)
	{
		component.addPropertyChangeListener(property, listener);
		disposables.push(new Disposable()
		{
			public void dispose()
			{
				component.removePropertyChangeListener(property, listener);
			}
		});
	}

	public void installComponentEnabler(final JToggleButton button, final Object[] components)
	{
		enableComponents(components, button.isSelected());
		installItemListener(button, new ItemListener()
		{
			public void itemStateChanged(ItemEvent e)
			{
				boolean selected=e.getStateChange()==ItemEvent.SELECTED;
				enableComponents(components, selected);
			}
		});
	}

	private void enableComponents(Object[] components, boolean enable)
	{
		for (int i=0; i<components.length; i++)
		{
			if (components[i] instanceof Component)
			{
				((Component)components[i]).setEnabled(enable);
			}
			else if (components[i] instanceof Action)
			{
				((Action)components[i]).setEnabled(enable);
			}
		}
	}
}
