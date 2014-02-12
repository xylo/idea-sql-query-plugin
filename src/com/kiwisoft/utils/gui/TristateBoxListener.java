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

import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.ActionMapUIResource;
import javax.swing.plaf.ComponentInputMapUIResource;

/**
 * Button Listener
 *
 * @author Jeff Dinkins
 * @author Arnaud Weber (keyboard UI support)
 * @author Stefan Stiller
 * @version $Revision: 1.3 $, $Date: 2006/03/24 18:50:51 $
 */
public class TristateBoxListener implements MouseListener, MouseMotionListener,
		FocusListener, ChangeListener, PropertyChangeListener
{
	/** Set to true when the WindowInputMap is installed. */
	private boolean createdWindowInputMap;

	private transient long lastPressedTimestamp=-1;
	private transient boolean shouldDiscardRelease;

	public TristateBoxListener()
	{
	}

	public void propertyChange(PropertyChangeEvent e)
	{
		String prop=e.getPropertyName();
		if (prop.equals(TristateBox.MNEMONIC_CHANGED_PROPERTY))
		{
			updateMnemonicBinding((TristateBox)e.getSource());
		}

		if (prop.equals(TristateBox.CONTENT_AREA_FILLED_CHANGED_PROPERTY))
		{
			checkOpacity((TristateBox)e.getSource());
		}

//		if (prop.equals(TristateBox.TEXT_CHANGED_PROPERTY) ||
//				"font".equals(prop) || "foreground".equals(prop))
//		{
//			TristateBox b=(TristateBox)e.getSource();
//			BasicHTML.updateRenderer(b, b.getText());
//		}
	}

	private void checkOpacity(TristateBox b)
	{
		b.setOpaque(b.isContentAreaFilled());
	}

	/**
	 * Register default key actions: pressing space to "click" a
	 * button and registring the keyboard mnemonic (if any).
	 */
	public void installKeyboardActions(JComponent c)
	{
		TristateBox b=(TristateBox)c;
		// Update the mnemonic binding.
		updateMnemonicBinding(b);

		// Reset the ActionMap.
		ActionMap map=getActionMap(b);

		SwingUtilities.replaceUIActionMap(c, map);

		InputMap km=getInputMap(JComponent.WHEN_FOCUSED, c);

		SwingUtilities.replaceUIInputMap(c, JComponent.WHEN_FOCUSED, km);
	}

	/**
	 * Unregister's default key actions
	 */
	public void uninstallKeyboardActions(JComponent c)
	{
		if (createdWindowInputMap)
		{
			SwingUtilities.replaceUIInputMap(c, JComponent.
					WHEN_IN_FOCUSED_WINDOW, null);
			createdWindowInputMap=false;
		}
		SwingUtilities.replaceUIInputMap(c, JComponent.WHEN_FOCUSED, null);
		SwingUtilities.replaceUIActionMap(c, null);
	}

	/**
	 * Returns the ActionMap to use for <code>b</code>. Called as part of
	 * <code>installKeyboardActions</code>.
	 */
	private ActionMap getActionMap(TristateBox b)
	{
		return createActionMap(b);
	}

	/**
	 * Returns the InputMap for condition <code>condition</code>. Called as
	 * part of <code>installKeyboardActions</code>.
	 */
	private InputMap getInputMap(int condition, JComponent c)
	{
		if (condition==JComponent.WHEN_FOCUSED)
		{
			TristateBoxUI ui=((TristateBox)c).getUI();
			if (ui!=null)
			{
				return (InputMap)UIManager.get(ui.getPropertyPrefix()+"focusInputMap");
			}
		}
		return null;
	}

	/**
	 * Creates and returns the ActionMap to use for the button.
	 */
	private ActionMap createActionMap(TristateBox c)
	{
		ActionMap retValue=new ActionMapUIResource();

		retValue.put("pressed", new PressedAction(c));
		retValue.put("released", new ReleasedAction(c));
		return retValue;
	}

	/**
	 * Resets the binding for the mnemonic in the WHEN_IN_FOCUSED_WINDOW
	 * UI InputMap.
	 */
	private void updateMnemonicBinding(TristateBox b)
	{
		int m=b.getMnemonic();
		if (m!=0)
		{
			InputMap map;
			if (!createdWindowInputMap)
			{
				map=new ComponentInputMapUIResource(b);
				SwingUtilities.replaceUIInputMap(b,
						JComponent.WHEN_IN_FOCUSED_WINDOW, map);
				createdWindowInputMap=true;
			}
			else
			{
				map=SwingUtilities.getUIInputMap(b, JComponent.
						WHEN_IN_FOCUSED_WINDOW);
			}
			if (map!=null)
			{
				map.clear();
				map.put(KeyStroke.getKeyStroke(m, ActionEvent.ALT_MASK, false),
						"pressed");
				map.put(KeyStroke.getKeyStroke(m, ActionEvent.ALT_MASK, true),
						"released");
				map.put(KeyStroke.getKeyStroke(m, 0, true), "released");
			}
		}
		else if (createdWindowInputMap)
		{
			InputMap map=SwingUtilities.getUIInputMap(b, JComponent.
					WHEN_IN_FOCUSED_WINDOW);
			if (map!=null)
			{
				map.clear();
			}
		}
	}

	public void stateChanged(ChangeEvent e)
	{
		TristateBox b=(TristateBox)e.getSource();
		b.repaint();
	}

	public void focusGained(FocusEvent e)
	{
		TristateBox b=(TristateBox)e.getSource();
		b.repaint();
	}

	public void focusLost(FocusEvent e)
	{
		TristateBox b=(TristateBox)e.getSource();
		b.getModel().setArmed(false);
		b.repaint();
	}

	public void mouseMoved(MouseEvent e)
	{
	}

	public void mouseDragged(MouseEvent e)
	{
	}

	public void mouseClicked(MouseEvent e)
	{
	}

	public void mousePressed(MouseEvent e)
	{
		if (SwingUtilities.isLeftMouseButton(e))
		{
			TristateBox b=(TristateBox)e.getSource();

			if (b.contains(e.getX(), e.getY()))
			{
				long multiClickThreshhold=b.getMultiClickThreshhold();
				long lastTime=lastPressedTimestamp;
				long currentTime=lastPressedTimestamp=e.getWhen();
				if (lastTime!=-1 && currentTime-lastTime<multiClickThreshhold)
				{
					shouldDiscardRelease=true;
					return;
				}

				TristateModel model=b.getModel();
				if (!model.isEnabled())
				{
					// Disabled buttons ignore all input...
					return;
				}
				if (!model.isArmed())
				{
					// button not armed, should be
					model.setArmed(true);
				}
				model.setPressed(true);
				if (!b.hasFocus() && b.isRequestFocusEnabled())
				{
					b.requestFocus();
				}
			}
		}
	}

	public void mouseReleased(MouseEvent e)
	{
		if (SwingUtilities.isLeftMouseButton(e))
		{
			// Support for multiClickThreshhold
			if (shouldDiscardRelease)
			{
				shouldDiscardRelease=false;
				return;
			}
			TristateBox b=(TristateBox)e.getSource();
			TristateModel model=b.getModel();
			model.setPressed(false);
			model.setArmed(false);
		}
	}

	public void mouseEntered(MouseEvent e)
	{
		TristateBox b=(TristateBox)e.getSource();
		TristateModel model=b.getModel();
		if (model.isPressed()) model.setArmed(true);
	}

	public void mouseExited(MouseEvent e)
	{
		TristateBox b=(TristateBox)e.getSource();
		TristateModel model=b.getModel();
		model.setArmed(false);
	}

	static class PressedAction extends AbstractAction
	{
		TristateBox b;

		PressedAction(TristateBox b)
		{
			this.b=b;
		}

		public void actionPerformed(ActionEvent e)
		{
			TristateModel model=b.getModel();
			model.setArmed(true);
			model.setPressed(true);
			if (!b.hasFocus())
			{
				b.requestFocus();
			}
		}

		public boolean isEnabled()
		{
			return b.getModel().isEnabled();
		}
	}

	static class ReleasedAction extends AbstractAction
	{
		TristateBox b;

		ReleasedAction(TristateBox b)
		{
			this.b=b;
		}

		public void actionPerformed(ActionEvent e)
		{
			TristateModel model=b.getModel();
			model.setPressed(false);
			model.setArmed(false);
		}

		public boolean isEnabled()
		{
			return b.getModel().isEnabled();
		}
	}

}

