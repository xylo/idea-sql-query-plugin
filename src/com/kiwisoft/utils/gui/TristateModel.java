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

import java.awt.AWTEvent;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.util.EventListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

import com.kiwisoft.utils.Tristate;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.4 $, $Date: 2006/03/24 18:50:51 $
 */
public class TristateModel
{
	protected int stateMask;

	private Tristate state=Tristate.UNDEFINED;

	protected String actionCommand;

	protected int mnemonic;

	/**
	 * Only one <code>ChangeEvent</code> is needed per button model
	 * instance since the
	 * event's only state is the source property.  The source of events
	 * generated is always "this".
	 */
	protected transient ChangeEvent changeEvent;
	protected EventListenerList listenerList=new EventListenerList();

	/**
	 * Constructs a default <code>JButtonModel</code>.
	 *
	 */
	public TristateModel()
	{
		stateMask=0;
		setEnabled(true);
	}

	/**
	 * Indicates partial commitment towards choosing the
	 * button.
	 */
	public final static int ARMED=1;

	/**
	 * Indicates that the button has been "pressed"
	 * (typically, when the mouse is released).
	 */
	public final static int PRESSED=1<<2;

	/**
	 * Indicates that the button can be selected by
	 * an input device (such as a mouse pointer).
	 */
	public final static int ENABLED=1<<3;

	/**
	 * Sets the <code>actionCommand</code> string that gets sent as
	 * part of the event when the button is pressed.
	 *
	 * @param actionCommand the <code>String</code> that identifies the generated event
	 */
	public void setActionCommand(String actionCommand)
	{
		this.actionCommand=actionCommand;
	}

	/**
	 * Returns the action command for this button.
	 *
	 * @return the <code>String</code> that identifies the generated event
	 * @see #setActionCommand
	 */
	public String getActionCommand()
	{
		return actionCommand;
	}

	/**
	 * Indicates partial commitment towards pressing the
	 * button.
	 *
	 * @return true if the button is armed, and ready to be pressed
	 * @see #setArmed
	 */
	public boolean isArmed()
	{
		return (stateMask&ARMED)!=0;
	}

	/**
	 * Indicates whether the button can be selected or pressed by
	 * an input device (such as a mouse pointer). (Checkbox-buttons
	 * are selected, regular buttons are "pressed".)
	 *
	 * @return true if the button is enabled, and therefore
	 *         selectable (or pressable)
	 */
	public boolean isEnabled()
	{
		return (stateMask&ENABLED)!=0;
	}

	/**
	 * Indicates whether button has been pressed.
	 *
	 * @return true if the button has been pressed
	 */
	public boolean isPressed()
	{
		return (stateMask&PRESSED)!=0;
	}

	/**
	 * Marks the button as "armed". If the mouse button is
	 * released while it is over this item, the button's action event
	 * fires. If the mouse button is released elsewhere, the
	 * event does not fire and the button is disarmed.
	 *
	 * @param b true to arm the button so it can be selected
	 */
	public void setArmed(boolean b)
	{
		if ((isArmed()==b) || !isEnabled())
		{
			return;
		}

		if (b)
		{
			stateMask|=ARMED;
		}
		else
		{
			stateMask&=~ARMED;
		}

		fireStateChanged();
	}

	/**
	 * Enables or disables the button.
	 *
	 * @param b true to enable the button
	 * @see #isEnabled
	 */
	public void setEnabled(boolean b)
	{
		if (isEnabled()==b)
		{
			return;
		}

		if (b)
		{
			stateMask|=ENABLED;
		}
		else
		{
			stateMask&=~ENABLED;
			// unarm and unpress, just in case
			stateMask&=~ARMED;
			stateMask&=~PRESSED;
		}

		fireStateChanged();
	}

//	/**
//	 * Selects or deselects the button.
//	 *
//	 * @param b true selects the button,
//	 *          false deselects the button
//	 */
//	public void setSelected(boolean b)
//	{
//		if (this.isSelected()==b)
//		{
//			return;
//		}
//
//		if (b)
//		{
//			stateMask|=SELECTED;
//		}
//		else
//		{
//			stateMask&=~SELECTED;
//		}
//		fireStateChanged();
//	}


	public Tristate getState()
	{
		return state;
	}

	public void setState(Tristate state)
	{
		if (state==null) state=Tristate.UNDEFINED;
		if (getState().equals(state)) return;

		this.state=state;
		fireStateChanged();
	}

	public void toggleState()
	{
		if (Tristate.TRUE.equals(getState())) setState(Tristate.UNDEFINED);
		else if (Tristate.UNDEFINED.equals(getState())) setState(Tristate.FALSE);
		else setState(Tristate.TRUE);
	}

	/**
	 * Sets the pressed state of the toggle button.
	 */
	public void setPressed(boolean b) {
		if ((isPressed() == b) || !isEnabled()) {
			return;
		}

		if (!b && isArmed()) toggleState();

		if (b) {
			stateMask |= PRESSED;
		} else {
			stateMask &= ~PRESSED;
		}

		fireStateChanged();

		if(!isPressed() && isArmed()) {
			int modifiers = 0;
			AWTEvent currentEvent = EventQueue.getCurrentEvent();
			if (currentEvent instanceof InputEvent) {
				modifiers = ((InputEvent)currentEvent).getModifiers();
			} else if (currentEvent instanceof ActionEvent) {
				modifiers = ((ActionEvent)currentEvent).getModifiers();
			}
			fireActionPerformed(
				new ActionEvent(this, ActionEvent.ACTION_PERFORMED,
								getActionCommand(),
								EventQueue.getMostRecentEventTime(),
								modifiers));
		}

	}

	/**
	 * Sets the keyboard mnemonic (shortcut key or
	 * accelerator key) for this button.
	 *
	 * @param key an int specifying the accelerator key
	 */
	public void setMnemonic(int key)
	{
		mnemonic=key;
		fireStateChanged();
	}

	/**
	 * Gets the keyboard mnemonic for this model
	 *
	 * @return an int specifying the accelerator key
	 * @see #setMnemonic
	 */
	public int getMnemonic()
	{
		return mnemonic;
	}

	/**
	 * Adds a <code>ChangeListener</code> to the button.
	 *
	 * @param l the listener to add
	 */
	public void addChangeListener(ChangeListener l)
	{
		listenerList.add(ChangeListener.class, l);
	}

	/**
	 * Removes a <code>ChangeListener</code> from the button.
	 *
	 * @param l the listener to remove
	 */
	public void removeChangeListener(ChangeListener l)
	{
		listenerList.remove(ChangeListener.class, l);
	}

	/**
	 * Returns an array of all the change listeners
	 * registered on this <code>DefaultButtonModel</code>.
	 *
	 * @return all of this model's <code>ChangeListener</code>s
	 *         or an empty
	 *         array if no change listeners are currently registered
	 *
	 * @see #addChangeListener
	 * @see #removeChangeListener
	 *
	 * @since 1.4
	 */
	public ChangeListener[] getChangeListeners()
	{
		return (ChangeListener[])listenerList.getListeners(
				ChangeListener.class);
	}

	/**
	 * Notifies all listeners that have registered interest for
	 * notification on this event type.  The event instance
	 * is created lazily.
	 *
	 * @see javax.swing.event.EventListenerList
	 */
	protected void fireStateChanged()
	{
		// Guaranteed to return a non-null array
		Object[] listeners=listenerList.getListenerList();
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i=listeners.length-2; i>=0; i-=2)
		{
			if (listeners[i]==ChangeListener.class)
			{
				// Lazily create the event:
				if (changeEvent==null)
					changeEvent=new ChangeEvent(this);
				((ChangeListener)listeners[i+1]).stateChanged(changeEvent);
			}
		}
	}

	/**
	 * Adds an <code>ActionListener</code> to the button.
	 *
	 * @param l the listener to add
	 */
	public void addActionListener(ActionListener l)
	{
		listenerList.add(ActionListener.class, l);
	}

	/**
	 * Removes an <code>ActionListener</code> from the button.
	 *
	 * @param l the listener to remove
	 */
	public void removeActionListener(ActionListener l)
	{
		listenerList.remove(ActionListener.class, l);
	}

	/**
	 * Returns an array of all the action listeners
	 * registered on this <code>DefaultButtonModel</code>.
	 *
	 * @return all of this model's <code>ActionListener</code>s
	 *         or an empty
	 *         array if no action listeners are currently registered
	 *
	 * @see #addActionListener
	 * @see #removeActionListener
	 *
	 * @since 1.4
	 */
	public ActionListener[] getActionListeners()
	{
		return (ActionListener[])listenerList.getListeners(
				ActionListener.class);
	}

	/**
	 * Notifies all listeners that have registered interest for
	 * notification on this event type.
	 *
	 * @param e the <code>ActionEvent</code> to deliver to listeners
	 * @see javax.swing.event.EventListenerList
	 */
	protected void fireActionPerformed(ActionEvent e)
	{
		// Guaranteed to return a non-null array
		Object[] listeners=listenerList.getListenerList();
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i=listeners.length-2; i>=0; i-=2)
		{
			if (listeners[i]==ActionListener.class)
			{
				// Lazily create the event:
				// if (changeEvent == null)
				// changeEvent = new ChangeEvent(this);
				((ActionListener)listeners[i+1]).actionPerformed(e);
			}
		}
	}

	/**
	 * Returns an array of all the objects currently registered as
	 * <code><em>Foo</em>Listener</code>s
	 * upon this model.
	 * <code><em>Foo</em>Listener</code>s
	 * are registered using the <code>add<em>Foo</em>Listener</code> method.
	 * <p>
	 * You can specify the <code>listenerType</code> argument
	 * with a class literal, such as <code><em>Foo</em>Listener.class</code>.
	 * For example, you can query a <code>DefaultButtonModel</code>
	 * instance <code>m</code>
	 * for its action listeners
	 * with the following code:
	 *
	 * <pre>ActionListener[] als = (ActionListener[])(m.getListeners(ActionListener.class));</pre>
	 *
	 * If no such listeners exist,
	 * this method returns an empty array.
	 *
	 * @param listenerType  the type of listeners requested;
	 *          this parameter should specify an interface
	 *          that descends from <code>java.util.EventListener</code>
	 * @return an array of all objects registered as
	 *          <code><em>Foo</em>Listener</code>s
	 *          on this model,
	 *          or an empty array if no such
	 *          listeners have been added
	 * @exception ClassCastException if <code>listenerType</code> doesn't
	 *          specify a class or interface that implements
	 *          <code>java.util.EventListener</code>
	 *
	 * @see #getActionListeners
	 * @see #getChangeListeners
	 *
	 * @since 1.3
	 */
	public EventListener[] getListeners(Class listenerType)
	{
		return listenerList.getListeners(listenerType);
	}
}
