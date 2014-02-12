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

import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.UIResource;

import com.kiwisoft.utils.Tristate;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.4 $, $Date: 2006/03/24 18:50:51 $
 */
public class TristateBox extends JComponent implements SwingConstants
{
	static
	{
		// Copies the input map from TextArea to get the same action keys as in the editor window
		UIManager.put("EditorPane.focusInputMap", UIManager.get("TextArea.focusInputMap"));
		UIManager.put("EditorPane.font", UIManager.get("TextArea.font"));


		UIManager.put("TristateBoxUI", "com.kiwisoft.utils.gui.TristateBoxUI");
		UIManager.put("TristateBox.icon", new TristateBoxIcon());
		UIManager.put("TristateBox.focus", UIManager.getColor("CheckBox.focus"));
		UIManager.put("TristateBox.disabledText", UIManager.getColor("CheckBox.disabledText"));
		UIManager.put("TristateBox.focusInputMap", new UIDefaults.LazyInputMap(new Object[]{
			"SPACE", "pressed",
			"released SPACE", "released"
		}));

	}

	/** Identifies a change to the flat property. */
	public static final String BORDER_PAINTED_FLAT_CHANGED_PROPERTY="borderPaintedFlat";

	// *********************************
	// ******* Button properties *******
	// *********************************

	/** Identifies a change in the button model. */
	public static final String MODEL_CHANGED_PROPERTY="model";
	/** Identifies a change in the button's text. */
	public static final String TEXT_CHANGED_PROPERTY="text";
	/** Identifies a change to the button's mnemonic. */
	public static final String MNEMONIC_CHANGED_PROPERTY="mnemonic";

	// Text positioning and alignment
	/** Identifies a change in the button's margins. */
	public static final String MARGIN_CHANGED_PROPERTY="margin";
	/** Identifies a change in the button's vertical alignment. */
	public static final String VERTICAL_ALIGNMENT_CHANGED_PROPERTY="verticalAlignment";
	/** Identifies a change in the button's horizontal alignment. */
	public static final String HORIZONTAL_ALIGNMENT_CHANGED_PROPERTY="horizontalAlignment";

	/** Identifies a change in the button's vertical text position. */
	public static final String VERTICAL_TEXT_POSITION_CHANGED_PROPERTY="verticalTextPosition";
	/** Identifies a change in the button's horizontal text position. */
	public static final String HORIZONTAL_TEXT_POSITION_CHANGED_PROPERTY="horizontalTextPosition";

	// Paint options
	/**
	 * Identifies a change to having the border drawn,
	 * or having it not drawn.
	 */
	public static final String BORDER_PAINTED_CHANGED_PROPERTY="borderPainted";

	/**
	 * Identifies a change to having the border highlighted when focused,
	 * or not.
	 */
	public static final String FOCUS_PAINTED_CHANGED_PROPERTY="focusPainted";
	/** Identifies a change to having the button paint the content area. */
	public static final String CONTENT_AREA_FILLED_CHANGED_PROPERTY="contentAreaFilled";

	/** The data model that determines the button's state. */
	protected TristateModel model;

	private String text=""; // for BeanBox
	private Insets margin;
	private Insets defaultMargin;

	// Display properties
	private boolean paintBorder=true;
	private boolean paintFocus=true;
	private boolean contentAreaFilled=true;

	// Icon/Label Alignment
	private int verticalAlignment=CENTER;
	private int horizontalAlignment=CENTER;

	private int verticalTextPosition=CENTER;
	private int horizontalTextPosition=TRAILING;

	private int iconTextGap=4;

	private int mnemonic;
	private int mnemonicIndex=-1;

	private long multiClickThreshhold;

	/** The button model's <code>changeListener</code>. */
	protected ChangeListener changeListener;

	/** The button model's <code>ActionListener</code>. */
	protected ActionListener actionListener;

	/**
	 * Only one <code>ChangeEvent</code> is needed per button
	 * instance since the
	 * event's only state is the source property.  The source of events
	 * generated is always "this".
	 */
	protected transient ChangeEvent changeEvent;

	private boolean flat;

	/**
	 * @see #getUIClassID
	 * @see #readObject
	 */
	private static final String uiClassID="TristateBoxUI";

	/**
	 * Creates an initially unselected check box button with no text, no icon.
	 */
	public TristateBox()
	{
		this(null, Tristate.UNDEFINED);
	}

	/**
	 * Creates a check box with an icon and specifies whether
	 * or not it is initially selected.
	 */
	public TristateBox(Tristate state)
	{
		this(null, state);
	}

	/**
	 * Creates a check box where properties are taken from the
	 * Action supplied.
	 *
	 * @since 1.3
	 */
	public TristateBox(Action a)
	{
		this();
		setAction(a);
	}

	/**
	 * Creates an initially unselected check box with
	 * the specified text and icon.
	 *
	 * @param text the text of the check box.
	 */
	public TristateBox(String text)
	{
		this(text, Tristate.UNDEFINED);
	}

	/**
	 * Creates a check box with text and icon,
	 * and specifies whether or not it is initially selected.
	 *
	 * @param text the text of the check box.
	 */
	public TristateBox(String text, Tristate state)
	{
		// Create the model
		setModel(new TristateModel());

		model.setState(state);

		// initialize
		init(text);
		setBorderPainted(false);
		setHorizontalAlignment(LEADING);
	}

	/**
	 * Sets the <code>borderPaintedFlat</code> property,
	 * which gives a hint to the look and feel as to the
	 * appearance of the check box border.
	 * This is usually set to <code>true</code> when a
	 * <code>JCheckBox</code> instance is used as a
	 * renderer in a component such as a <code>JTable</code> or
	 * <code>JTree</code>.  The default value for the
	 * <code>borderPaintedFlat</code> property is <code>false</code>.
	 * This method fires a property changed event.
	 * Some look and feels might not implement flat borders;
	 * they will ignore this property.
	 *
	 * @param b <code>true</code> requests that the border be painted flat;
	 *          <code>false</code> requests normal borders
	 * @beaninfo bound: true
	 * attribute: visualUpdate true
	 * description: Whether the border is painted flat.
	 * @see #isBorderPaintedFlat
	 */
	public void setBorderPaintedFlat(boolean b)
	{
		boolean oldValue=flat;
		flat=b;
		firePropertyChange(BORDER_PAINTED_FLAT_CHANGED_PROPERTY, oldValue, flat);
		if (b!=oldValue)
		{
			revalidate();
			repaint();
		}
	}

	/**
	 * Gets the value of the <code>borderPaintedFlat</code> property.
	 *
	 * @return the value of the <code>borderPaintedFlat</code> property
	 * @see #setBorderPaintedFlat
	 */
	public boolean isBorderPaintedFlat()
	{
		return flat;
	}

	/**
	 * Resets the UI property to a value from the current look and feel.
	 *
	 * @see JComponent#updateUI
	 */
	public void updateUI()
	{
		setUI(UIManager.getUI(this));
	}

	/**
	 * Returns a string that specifies the name of the L&F class
	 * that renders this component.
	 *
	 * @return the string "CheckBoxUI"
	 * @beaninfo expert: true
	 * description: A string that specifies the name of the L&F class
	 * @see JComponent#getUIClassID
	 * @see UIDefaults#getUI
	 */
	public String getUIClassID()
	{
		return uiClassID;
	}

	/**
	 * Factory method which sets the <code>ActionEvent</code> source's
	 * properties according to values from the Action instance. The
	 * properties which are set may differ for subclasses.
	 * By default, the properties which get set are <code>Text, Mnemonic,
	 * Enabled, ActionCommand</code>, and <code>ToolTipText</code>.
	 *
	 * @param a the Action from which to get the properties, or null
	 * @see Action
	 * @see #setAction
	 * @since 1.3
	 */
	protected void configurePropertiesFromAction(Action a)
	{
		String[] types={Action.MNEMONIC_KEY, Action.NAME,
						Action.SHORT_DESCRIPTION,
						Action.ACTION_COMMAND_KEY, "enabled"};
		configurePropertiesFromAction(a, types);
	}

	/**
	 * Factory method which creates the PropertyChangeListener
	 * used to update the ActionEvent source as properties change on
	 * its Action instance.  Subclasses may override this in order
	 * to provide their own PropertyChangeListener if the set of
	 * properties which should be kept up to date differs from the
	 * default properties (Text, Icon, Enabled, ToolTipText).
	 * <p/>
	 * Note that PropertyChangeListeners should avoid holding
	 * strong references to the ActionEvent source, as this may hinder
	 * garbage collection of the ActionEvent source and all components
	 * in its containment hierarchy.
	 *
	 * @see Action
	 * @see #setAction
	 * @since 1.3
	 */
	protected PropertyChangeListener createActionPropertyChangeListener(Action a)
	{
		return new ActionPropertyChangeListener(this, a)
		{
			public void propertyChange(PropertyChangeEvent e)
			{
				String propertyName=e.getPropertyName();
				AbstractButton button=(AbstractButton)getTarget();
				if (button==null)
				{   //WeakRef GC'ed in 1.2
					Action action=(Action)e.getSource();
					action.removePropertyChangeListener(this);
				}
				else
				{
					if (propertyName.equals(Action.NAME))
					{
						String text=(String)e.getNewValue();
						button.setText(text);
						button.repaint();
					}
					else if (propertyName.equals(Action.SHORT_DESCRIPTION))
					{
						String text=(String)e.getNewValue();
						button.setToolTipText(text);
					}
					else if ("enabled".equals(propertyName))
					{
						Boolean enabledState=(Boolean)e.getNewValue();
						button.setEnabled(enabledState.booleanValue());
						button.repaint();
					}
					else if (propertyName.equals(Action.ACTION_COMMAND_KEY))
					{
						button.setActionCommand((String)e.getNewValue());
					}
				}
			}
		};
	}

	/**
	 * Returns the button's text.
	 *
	 * @return the buttons text
	 * @see #setText
	 */
	public String getText()
	{
		return text;
	}

	/**
	 * Sets the button's text.
	 *
	 * @param text the string used to set the text
	 * @beaninfo bound: true
	 * preferred: true
	 * attribute: visualUpdate true
	 * description: The button's text.
	 * @see #getText
	 */
	public void setText(String text)
	{
		String oldValue=this.text;
		this.text=text;
		firePropertyChange(TEXT_CHANGED_PROPERTY, oldValue, text);
		updateDisplayedMnemonicIndex(text, getMnemonic());

		if (text==null || oldValue==null || !text.equals(oldValue))
		{
			revalidate();
			repaint();
		}
	}

	/**
	 * Returns the state of the button. True if the
	 * toggle button is selected, false if it's not.
	 *
	 * @return true if the toggle button is selected, otherwise false
	 */
	public Tristate getState()
	{
		return model.getState();
	}

	/**
	 * Sets the state of the button. Note that this method does not
	 * trigger an <code>actionEvent</code>.
	 * Call <code>doClick</code> to perform a programatic action change.
	 */
	public void setState(Tristate state)
	{
		model.setState(state);
	}

	/**
	 * Sets space for margin between the button's border and
	 * the label. Setting to <code>null</code> will cause the button to
	 * use the default margin.  The button's default <code>Border</code>
	 * object will use this value to create the proper margin.
	 * However, if a non-default border is set on the button,
	 * it is that <code>Border</code> object's responsibility to create the
	 * appropriate margin space (else this property will
	 * effectively be ignored).
	 *
	 * @param m the space between the border and the label
	 * @beaninfo bound: true
	 * attribute: visualUpdate true
	 * description: The space between the button's border and the label.
	 */
	public void setMargin(Insets m)
	{
		// Cache the old margin if it comes from the UI
		if (m instanceof UIResource)
		{
			defaultMargin=m;
		}
		else if (margin instanceof UIResource)
		{
			defaultMargin=margin;
		}

		// If the client passes in a null insets, restore the margin
		// from the UI if possible
		if (m==null && defaultMargin!=null)
		{
			m=defaultMargin;
		}

		Insets old=margin;
		margin=m;
		firePropertyChange(MARGIN_CHANGED_PROPERTY, old, m);
		if (old==null || (m!=null && !m.equals(old)))
		{
			revalidate();
			repaint();
		}
	}

	/**
	 * Returns the margin between the button's border and
	 * the label.
	 *
	 * @return an <code>Insets</code> object specifying the margin
	 *         between the botton's border and the label
	 * @see #setMargin
	 */
	public Insets getMargin()
	{
		return (margin==null) ? null : (Insets)margin.clone();
	}

	/**
	 * Returns the vertical alignment of the text and icon.
	 *
	 * @return the <code>verticalAlignment</code> property, one of the
	 *         following values:
	 *         <ul>
	 *         <li>SwingConstants.CENTER (the default)
	 *         <li>SwingConstants.TOP
	 *         <li>SwingConstants.BOTTOM
	 *         </ul>
	 */
	public int getVerticalAlignment()
	{
		return verticalAlignment;
	}

	/**
	 * Sets the vertical alignment of the icon and text.
	 *
	 * @param alignment one of the following values:
	 *                  <ul>
	 *                  <li>SwingConstants.CENTER (the default)
	 *                  <li>SwingConstants.TOP
	 *                  <li>SwingConstants.BOTTOM
	 *                  </ul>
	 * @beaninfo bound: true
	 * enum: TOP    SwingConstants.TOP
	 * CENTER SwingConstants.CENTER
	 * BOTTOM  SwingConstants.BOTTOM
	 * attribute: visualUpdate true
	 * description: The vertical alignment of the icon and text.
	 */
	public void setVerticalAlignment(int alignment)
	{
		if (alignment==verticalAlignment) return;
		int oldValue=verticalAlignment;
		verticalAlignment=checkVerticalKey(alignment, "verticalAlignment");
		firePropertyChange(VERTICAL_ALIGNMENT_CHANGED_PROPERTY, oldValue, verticalAlignment);
		repaint();
	}

	/**
	 * Returns the horizontal alignment of the icon and text.
	 *
	 * @return the <code>horizontalAlignment</code> property,
	 *         one of the following values:
	 *         <ul>
	 *         <li>SwingConstants.RIGHT (the default)
	 *         <li>SwingConstants.LEFT
	 *         <li>SwingConstants.CENTER
	 *         <li>SwingConstants.LEADING
	 *         <li>SwingConstants.TRAILING
	 *         </ul>
	 */
	public int getHorizontalAlignment()
	{
		return horizontalAlignment;
	}

	/**
	 * Sets the horizontal alignment of the icon and text.
	 *
	 * @param alignment one of the following values:
	 *                  <ul>
	 *                  <li>SwingConstants.RIGHT (the default)
	 *                  <li>SwingConstants.LEFT
	 *                  <li>SwingConstants.CENTER
	 *                  <li>SwingConstants.LEADING
	 *                  <li>SwingConstants.TRAILING
	 *                  </ul>
	 * @beaninfo bound: true
	 * enum: LEFT     SwingConstants.LEFT
	 * CENTER   SwingConstants.CENTER
	 * RIGHT    SwingConstants.RIGHT
	 * LEADING  SwingConstants.LEADING
	 * TRAILING SwingConstants.TRAILING
	 * attribute: visualUpdate true
	 * description: The horizontal alignment of the icon and text.
	 */
	public void setHorizontalAlignment(int alignment)
	{
		if (alignment==horizontalAlignment) return;
		int oldValue=horizontalAlignment;
		horizontalAlignment=checkHorizontalKey(alignment,
				"horizontalAlignment");
		firePropertyChange(HORIZONTAL_ALIGNMENT_CHANGED_PROPERTY,
				oldValue, horizontalAlignment);
		repaint();
	}

	/**
	 * Returns the vertical position of the text relative to the icon.
	 *
	 * @return the <code>verticalTextPosition</code> property,
	 *         one of the following values:
	 *         <ul>
	 *         <li>SwingConstants.CENTER  (the default)
	 *         <li>SwingConstants.TOP
	 *         <li>SwingConstants.BOTTOM
	 *         </ul>
	 */
	public int getVerticalTextPosition()
	{
		return verticalTextPosition;
	}

	/**
	 * Sets the vertical position of the text relative to the icon.
	 *
	 * @param textPosition one of the following values:
	 *                     <ul>
	 *                     <li>SwingConstants.CENTER (the default)
	 *                     <li>SwingConstants.TOP
	 *                     <li>SwingConstants.BOTTOM
	 *                     </ul>
	 * @beaninfo bound: true
	 * enum: TOP    SwingConstants.TOP
	 * CENTER SwingConstants.CENTER
	 * BOTTOM SwingConstants.BOTTOM
	 * attribute: visualUpdate true
	 * description: The vertical position of the text relative to the icon.
	 */
	public void setVerticalTextPosition(int textPosition)
	{
		if (textPosition==verticalTextPosition) return;
		int oldValue=verticalTextPosition;
		verticalTextPosition=checkVerticalKey(textPosition, "verticalTextPosition");
		firePropertyChange(VERTICAL_TEXT_POSITION_CHANGED_PROPERTY, oldValue, verticalTextPosition);
		repaint();
	}

	/**
	 * Returns the horizontal position of the text relative to the icon.
	 *
	 * @return the <code>horizontalTextPosition</code> property,
	 *         one of the following values:
	 *         <ul>
	 *         <li>SwingConstants.RIGHT
	 *         <li>SwingConstants.LEFT
	 *         <li>SwingConstants.CENTER
	 *         <li>SwingConstants.LEADING
	 *         <li>SwingConstants.TRAILING (the default)
	 *         </ul>
	 */
	public int getHorizontalTextPosition()
	{
		return horizontalTextPosition;
	}

	/**
	 * Sets the horizontal position of the text relative to the icon.
	 *
	 * @param textPosition one of the following values:
	 *                     <ul>
	 *                     <li>SwingConstants.RIGHT
	 *                     <li>SwingConstants.LEFT
	 *                     <li>SwingConstants.CENTER
	 *                     <li>SwingConstants.LEADING
	 *                     <li>SwingConstants.TRAILING (the default)
	 *                     </ul>
	 * @throws IllegalArgumentException if <code>textPosition</code>
	 *                                  is not one of the legal values listed above
	 * @beaninfo bound: true
	 * enum: LEFT     SwingConstants.LEFT
	 * CENTER   SwingConstants.CENTER
	 * RIGHT    SwingConstants.RIGHT
	 * LEADING  SwingConstants.LEADING
	 * TRAILING SwingConstants.TRAILING
	 * attribute: visualUpdate true
	 * description: The horizontal position of the text relative to the icon.
	 */
	public void setHorizontalTextPosition(int textPosition)
	{
		if (textPosition==horizontalTextPosition) return;
		int oldValue=horizontalTextPosition;
		horizontalTextPosition=checkHorizontalKey(textPosition,
				"horizontalTextPosition");
		firePropertyChange(HORIZONTAL_TEXT_POSITION_CHANGED_PROPERTY,
				oldValue,
				horizontalTextPosition);
		repaint();
	}

	/**
	 * Returns the amount of space between the text and the icon
	 * displayed in this button.
	 *
	 * @return an int equal to the number of pixels between the text
	 *         and the icon.
	 * @see #setIconTextGap
	 * @since 1.4
	 */
	public int getIconTextGap()
	{
		return iconTextGap;
	}

	/**
	 * If both the icon and text properties are set, this property
	 * defines the space between them.
	 * <p/>
	 * The default value of this property is 4 pixels.
	 * <p/>
	 * This is a JavaBeans bound property.
	 *
	 * @beaninfo bound: true
	 * attribute: visualUpdate true
	 * description: If both the icon and text properties are set, this
	 * property defines the space between them.
	 * @see #getIconTextGap
	 * @since 1.4
	 */
	public void setIconTextGap(int iconTextGap)
	{
		int oldValue=this.iconTextGap;
		this.iconTextGap=iconTextGap;
		firePropertyChange("iconTextGap", oldValue, iconTextGap);
		if (iconTextGap!=oldValue)
		{
			revalidate();
			repaint();
		}
	}

	/**
	 * Verify that key is a legal value for the
	 * <code>horizontalAlignment</code> properties.
	 *
	 * @param key       the property value to check, one of the following values:
	 *                  <ul>
	 *                  <li>SwingConstants.RIGHT (the default)
	 *                  <li>SwingConstants.LEFT
	 *                  <li>SwingConstants.CENTER
	 *                  <li>SwingConstants.LEADING
	 *                  <li>SwingConstants.TRAILING
	 *                  </ul>
	 * @param exception the <code>IllegalArgumentException</code>
	 *                  detail message
	 * @throws IllegalArgumentException if key is not one of the legal
	 *                                  values listed above
	 * @see #setHorizontalTextPosition
	 * @see #setHorizontalAlignment
	 */
	protected int checkHorizontalKey(int key, String exception)
	{
		if ((key==LEFT) ||
				(key==CENTER) ||
				(key==RIGHT) ||
				(key==LEADING) ||
				(key==TRAILING))
		{
			return key;
		}
		else
		{
			throw new IllegalArgumentException(exception);
		}
	}

	/**
	 * Ensures that the key is a valid. Throws an
	 * <code>IllegalArgumentException</code>
	 * exception otherwise.
	 *
	 * @param key       the value to check, one of the following values:
	 *                  <ul>
	 *                  <li>SwingConstants.CENTER (the default)
	 *                  <li>SwingConstants.TOP
	 *                  <li>SwingConstants.BOTTOM
	 *                  </ul>
	 * @param exception a string to be passed to the
	 *                  <code>IllegalArgumentException</code> call if key
	 *                  is not one of the valid values listed above
	 * @throws IllegalArgumentException if key is not one of the legal
	 *                                  values listed above
	 */
	protected int checkVerticalKey(int key, String exception)
	{
		if ((key==TOP) || (key==CENTER) || (key==BOTTOM))
		{
			return key;
		}
		else
		{
			throw new IllegalArgumentException(exception);
		}
	}

	/**
	 * Sets the action command for this button.
	 *
	 * @param actionCommand the action command for this button
	 */
	public void setActionCommand(String actionCommand)
	{
		getModel().setActionCommand(actionCommand);
	}

	/**
	 * Returns the action command for this button.
	 *
	 * @return the action command for this button
	 */
	public String getActionCommand()
	{
		String ac=getModel().getActionCommand();
		if (ac==null)
		{
			ac=getText();
		}
		return ac;
	}

	private Action action;
	private PropertyChangeListener actionPropertyChangeListener;

	/**
	 * Sets the <code>Action</code> for the <code>ActionEvent</code> source.
	 * The new <code>Action</code> replaces any previously set
	 * <code>Action</code> but does not affect <code>ActionListeners</code>
	 * independently added with <code>addActionListener</code>.
	 * If the <code>Action</code> is already a registered
	 * <code>ActionListener</code> for the button, it is not re-registered.
	 * <p/>
	 * A side-effect of setting the <code>Action</code> is that the
	 * <code>ActionEvent</code> source's properties  are immediately
	 * set from the values in the <code>Action</code> (performed by the
	 * method <code>configurePropertiesFromAction</code>) and
	 * subsequently updated as the <code>Action</code>'s properties change
	 * (via a <code>PropertyChangeListener</code> created by the method
	 * <code>createActionPropertyChangeListener</code>.
	 *
	 * @param a the <code>Action</code> for the <code>AbstractButton</code>,
	 *          or <code>null</code>
	 * @beaninfo bound: true
	 * attribute: visualUpdate true
	 * description: the Action instance connected with this ActionEvent source
	 * @see Action
	 * @see #getAction
	 * @see #configurePropertiesFromAction
	 * @see #createActionPropertyChangeListener
	 * @since 1.3
	 */
	public void setAction(Action a)
	{
		Action oldValue=getAction();
		if (action==null || !action.equals(a))
		{
			action=a;
			if (oldValue!=null)
			{
				removeActionListener(oldValue);
				oldValue.removePropertyChangeListener(actionPropertyChangeListener);
				actionPropertyChangeListener=null;
			}
			configurePropertiesFromAction(action);
			if (action!=null)
			{
				// Don't add if it is already a listener
				if (!isListener(ActionListener.class, action))
				{
					addActionListener(action);
				}
				// Reverse linkage:
				actionPropertyChangeListener=createActionPropertyChangeListener(action);
				action.addPropertyChangeListener(actionPropertyChangeListener);
			}
			firePropertyChange("action", oldValue, action);
			revalidate();
			repaint();
		}
	}

	private boolean isListener(Class c, ActionListener a)
	{
		boolean isListener=false;
		Object[] listeners=listenerList.getListenerList();
		for (int i=listeners.length-2; i>=0; i-=2)
		{
			if (listeners[i]==c && listeners[i+1]==a)
			{
				isListener=true;
			}
		}
		return isListener;
	}

	/**
	 * Returns the currently set <code>Action</code> for this
	 * <code>ActionEvent</code> source, or <code>null</code>
	 * if no <code>Action</code> is set.
	 *
	 * @return the <code>Action</code> for this <code>ActionEvent</code>
	 *         source, or <code>null</code>
	 * @see Action
	 * @see #setAction
	 * @since 1.3
	 */
	public Action getAction()
	{
		return action;
	}

	/**
	 * Configures the AbstractButton's properties according to values
	 * from the <code>Action</code> instance.  Which properties to set
	 * is determined by the <code>types</code> parameter.
	 * <code>types</code> may hold the following keys:
	 * <ul>
	 * <li><code>Action.NAME</code> - set the <code>Text</code> property
	 * from the <code>Action</code>,
	 * <li><code>Action.SHORT_DESCRIPTION</code> - set the
	 * <code>ToolTipText</code> property from the <code>Action</code>,
	 * <li><code>Action.SMALL_ICON</code> - set the <code>Icon</code> property
	 * from the <code>Action</code>,
	 * <li><code>Action.MNEMONIC</code> - set the <code>Mnemonic</code>
	 * property from the <code>Action</code>,
	 * <li><code>Action.ACTION_COMMAND_KEY</code> - set the
	 * <code>ActionCommand</code> property from the <code>Action</code>,
	 * <li><code>"enabled"</code> - set <code>Enabled</code> property
	 * from the <code>Action</code>
	 * </ul>
	 * <p/>
	 * If the <code>Action</code> passed in is <code>null</code>,
	 * the following things will occur:
	 * <ul>
	 * <li>the text is set to <code>null</code>,
	 * <li>the icon is set to <code>null</code>,
	 * <li>enabled is set to true,
	 * <li>the tooltip text is set to <code>null</code>
	 * <li>the mnemonic is set to <code>'\0'</code>
	 * </ul>
	 *
	 * @param a     the <code>Action</code> from which to get the properties,
	 *              or <code>null</code>
	 * @param types determines which properties to set from the
	 *              <code>Action</code>
	 * @see Action
	 * @see #setAction
	 * @see #configurePropertiesFromAction(javax.swing.Action)
	 * @since 1.4
	 */
	protected void configurePropertiesFromAction(Action a, String[] types)
	{
		if (types==null)
		{
			types=new String[]{Action.MNEMONIC_KEY, Action.NAME,
							   Action.SHORT_DESCRIPTION, Action.SMALL_ICON,
							   Action.ACTION_COMMAND_KEY, "enabled"};
		}
		for (int i=0; i<types.length; i++)
		{
			String type=types[i];
			if (type==null) continue;

			if (type.equals(Action.MNEMONIC_KEY))
			{
				Integer n=(a==null) ? null : (Integer)a.getValue(type);
				setMnemonic(n==null ? '\0' : n.intValue());
			}
			else if (type.equals(Action.NAME))
			{
				setText(a!=null ? (String)a.getValue(type) : null);
			}
			else if (type.equals(Action.SHORT_DESCRIPTION))
			{
				setToolTipText(a!=null ? (String)a.getValue(type) : null);
			}
			else if (type.equals(Action.ACTION_COMMAND_KEY))
			{
				setActionCommand(a!=null ? (String)a.getValue(type) : null);
			}
			else if ("enabled".equals(type))
			{
				setEnabled(a==null || a.isEnabled());
			}
		}
	}

	/**
	 * Gets the <code>borderPainted</code> property.
	 *
	 * @return the value of the <code>borderPainted</code> property
	 * @see #setBorderPainted
	 */
	public boolean isBorderPainted()
	{
		return paintBorder;
	}

	/**
	 * Sets the <code>borderPainted</code> property.
	 * If <code>true</code> and the button has a border,
	 * the border is painted. The default value for the
	 * <code>borderPainted</code> property is <code>true</code>.
	 *
	 * @param b if true and border property is not <code>null</code>,
	 *          the border is painted
	 * @beaninfo bound: true
	 * attribute: visualUpdate true
	 * description: Whether the border should be painted.
	 * @see #isBorderPainted
	 */
	public void setBorderPainted(boolean b)
	{
		boolean oldValue=paintBorder;
		paintBorder=b;
		firePropertyChange(BORDER_PAINTED_CHANGED_PROPERTY, oldValue, paintBorder);
		if (b!=oldValue)
		{
			revalidate();
			repaint();
		}
	}

	/**
	 * Paint the button's border if <code>BorderPainted</code>
	 * property is true and the button has a border.
	 *
	 * @param g the <code>Graphics</code> context in which to paint
	 * @see #paint
	 * @see #setBorder
	 */
	protected void paintBorder(Graphics g)
	{
		if (isBorderPainted())
		{
			super.paintBorder(g);
		}
	}

	/**
	 * Gets the <code>paintFocus</code> property.
	 *
	 * @return the <code>paintFocus</code> property
	 * @see #setFocusPainted
	 */
	public boolean isFocusPainted()
	{
		return paintFocus;
	}

	/**
	 * Sets the <code>paintFocus</code> property, which must
	 * be <code>true</code> for the focus state to be painted.
	 * The default value for the <code>paintFocus</code> property
	 * is <code>true</code>.
	 * Some look and feels might not paint focus state;
	 * they will ignore this property.
	 *
	 * @param b if <code>true</code>, the focus state should be painted
	 * @beaninfo bound: true
	 * attribute: visualUpdate true
	 * description: Whether focus should be painted
	 * @see #isFocusPainted
	 */
	public void setFocusPainted(boolean b)
	{
		boolean oldValue=paintFocus;
		paintFocus=b;
		firePropertyChange(FOCUS_PAINTED_CHANGED_PROPERTY, oldValue, paintFocus);
		if (b!=oldValue && isFocusOwner())
		{
			revalidate();
			repaint();
		}
	}

	/**
	 * Gets the <code>contentAreaFilled</code> property.
	 *
	 * @return the <code>contentAreaFilled</code> property
	 * @see #setContentAreaFilled
	 */
	public boolean isContentAreaFilled()
	{
		return contentAreaFilled;
	}

	/**
	 * Sets the <code>contentAreaFilled</code> property.
	 * If <code>true</code> the button will paint the content
	 * area.  If you wish to have a transparent button, such as
	 * an icon only button, for example, then you should set
	 * this to <code>false</code>. Do not call <code>setOpaque(false)</code>.
	 * The default value for the the <code>contentAreaFilled</code>
	 * property is <code>true</code>.
	 * <p/>
	 * This function may cause the component's opaque property to change.
	 * <p/>
	 * The exact behavior of calling this function varies on a
	 * component-by-component and L&F-by-L&F basis.
	 *
	 * @param b if true, the content should be filled; if false
	 *          the content area is not filled
	 * @beaninfo bound: true
	 * attribute: visualUpdate true
	 * description: Whether the button should paint the content area
	 * or leave it transparent.
	 * @see #isContentAreaFilled
	 * @see #setOpaque
	 */
	public void setContentAreaFilled(boolean b)
	{
		boolean oldValue=contentAreaFilled;
		contentAreaFilled=b;
		firePropertyChange(CONTENT_AREA_FILLED_CHANGED_PROPERTY, oldValue, contentAreaFilled);
		if (b!=oldValue)
		{
			repaint();
		}
	}

	/**
	 * Returns the keyboard mnemonic from the the current model.
	 *
	 * @return the keyboard mnemonic from the model
	 */
	public int getMnemonic()
	{
		return mnemonic;
	}

	/**
	 * Sets the keyboard mnemonic on the current model.
	 * The mnemonic is the key which when combined with the look and feel's
	 * mouseless modifier (usually Alt) will activate this button
	 * if focus is contained somewhere within this button's ancestor
	 * window.
	 * <p/>
	 * A mnemonic must correspond to a single key on the keyboard
	 * and should be specified using one of the <code>VK_XXX</code>
	 * keycodes defined in <code>java.awt.event.KeyEvent</code>.
	 * Mnemonics are case-insensitive, therefore a key event
	 * with the corresponding keycode would cause the button to be
	 * activated whether or not the Shift modifier was pressed.
	 * <p/>
	 * If the character defined by the mnemonic is found within
	 * the button's label string, the first occurrence of it
	 * will be underlined to indicate the mnemonic to the user.
	 *
	 * @param mnemonic the key code which represents the mnemonic
	 * @beaninfo bound: true
	 * attribute: visualUpdate true
	 * description: the keyboard character mnemonic
	 * @see java.awt.event.KeyEvent
	 * @see #setDisplayedMnemonicIndex
	 */
	public void setMnemonic(int mnemonic)
	{
		model.setMnemonic(mnemonic);
		updateMnemonicProperties();
	}

	/**
	 * This method is now obsolete, please use <code>setMnemonic(int)</code>
	 * to set the mnemonic for a button.  This method is only designed
	 * to handle character values which fall between 'a' and 'z' or
	 * 'A' and 'Z'.
	 *
	 * @param mnemonic a char specifying the mnemonic value
	 * @beaninfo bound: true
	 * attribute: visualUpdate true
	 * description: the keyboard character mnemonic
	 * @see #setMnemonic(int)
	 */
	public void setMnemonic(char mnemonic)
	{
		int vk=(int)mnemonic;
		if (vk>='a' && vk<='z')
			vk-=('a'-'A');
		setMnemonic(vk);
	}

	/**
	 * Provides a hint to the look and feel as to which character in the
	 * text should be decorated to represent the mnemonic. Not all look and
	 * feels may support this. A value of -1 indicates either there is no
	 * mnemonic, the mnemonic character is not contained in the string, or
	 * the developer does not wish the mnemonic to be displayed.
	 * <p/>
	 * The value of this is updated as the properties relating to the
	 * mnemonic change (such as the mnemonic itself, the text...).
	 * You should only ever have to call this if
	 * you do not wish the default character to be underlined. For example, if
	 * the text was 'Save As', with a mnemonic of 'a', and you wanted the 'A'
	 * to be decorated, as 'Save <u>A</u>s', you would have to invoke
	 * <code>setDisplayedMnemonicIndex(5)</code> after invoking
	 * <code>setMnemonic(KeyEvent.VK_A)</code>.
	 *
	 * @param index Index into the String to underline
	 * @throws IllegalArgumentException will be thrown if <code>index</code>
	 *                                  is &gt;= length of the text, or &lt; -1
	 * @beaninfo bound: true
	 * attribute: visualUpdate true
	 * description: the index into the String to draw the keyboard character
	 * mnemonic at
	 * @see #getDisplayedMnemonicIndex
	 * @since 1.4
	 */
	public void setDisplayedMnemonicIndex(int index)
			throws IllegalArgumentException
	{
		int oldValue=mnemonicIndex;
		if (index==-1)
		{
			mnemonicIndex=-1;
		}
		else
		{
			String text=getText();
			int textLength=(text==null) ? 0 : text.length();
			if (index<-1 || index>=textLength)
			{  // index out of range
				throw new IllegalArgumentException("index == "+index);
			}
		}
		mnemonicIndex=index;
		firePropertyChange("displayedMnemonicIndex", oldValue, index);
		if (index!=oldValue)
		{
			revalidate();
			repaint();
		}
	}

	/**
	 * Returns the character, as an index, that the look and feel should
	 * provide decoration for as representing the mnemonic character.
	 *
	 * @return index representing mnemonic character
	 * @see #setDisplayedMnemonicIndex
	 * @since 1.4
	 */
	public int getDisplayedMnemonicIndex()
	{
		return mnemonicIndex;
	}

	/**
	 * Update the displayedMnemonicIndex property. This method
	 * is called when either text or mnemonic changes. The new
	 * value of the displayedMnemonicIndex property is the index
	 * of the first occurrence of mnemonic in text.
	 */
	private void updateDisplayedMnemonicIndex(String text, int mnemonic)
	{
		setDisplayedMnemonicIndex(findDisplayedMnemonicIndex(text, mnemonic));
	}

	/**
	 * Brings the mnemonic property in accordance with model's mnemonic.
	 * This is called when model's mnemonic changes. Also updates the
	 * displayedMnemonicIndex property.
	 */
	private void updateMnemonicProperties()
	{
		int newMnemonic=model.getMnemonic();
		if (mnemonic!=newMnemonic)
		{
			int oldValue=mnemonic;
			mnemonic=newMnemonic;
			firePropertyChange(MNEMONIC_CHANGED_PROPERTY,
					oldValue, mnemonic);
			updateDisplayedMnemonicIndex(getText(), mnemonic);
			revalidate();
			repaint();
		}
	}

	/**
	 * Sets the amount of time (in milliseconds) required between
	 * mouse press events for the button to generate the corresponding
	 * action events.  After the initial mouse press occurs (and action
	 * event generated) any subsequent mouse press events which occur
	 * on intervals less than the threshhold will be ignored and no
	 * corresponding action event generated.  By default the threshhold is 0,
	 * which means that for each mouse press, an action event will be
	 * fired, no matter how quickly the mouse clicks occur.  In buttons
	 * where this behavior is not desirable (for example, the "OK" button
	 * in a dialog), this threshhold should be set to an appropriate
	 * positive value.
	 *
	 * @param threshhold the amount of time required between mouse
	 *                   press events to generate corresponding action events
	 * @throws IllegalArgumentException if threshhold < 0
	 * @see #getMultiClickThreshhold
	 * @since 1.4
	 */
	public void setMultiClickThreshhold(long threshhold)
	{
		if (threshhold<0)
		{
			throw new IllegalArgumentException("threshhold must be >= 0");
		}
		this.multiClickThreshhold=threshhold;
	}

	/**
	 * Gets the amount of time (in milliseconds) required between
	 * mouse press events for the button to generate the corresponding
	 * action events.
	 *
	 * @return the amount of time required between mouse press events
	 *         to generate corresponding action events
	 * @see #setMultiClickThreshhold
	 * @since 1.4
	 */
	public long getMultiClickThreshhold()
	{
		return multiClickThreshhold;
	}

	/**
	 * Returns the model that this button represents.
	 *
	 * @return the <code>model</code> property
	 * @see #setModel
	 */
	public TristateModel getModel()
	{
		return model;
	}

	/**
	 * Sets the model that this button represents.
	 *
	 * @param newModel the new <code>ButtonModel</code>
	 * @beaninfo bound: true
	 * description: Model that the Button uses.
	 * @see #getModel
	 */
	public void setModel(TristateModel newModel)
	{

		TristateModel oldModel=getModel();

		if (oldModel!=null)
		{
			oldModel.removeChangeListener(changeListener);
			oldModel.removeActionListener(actionListener);
			changeListener=null;
			actionListener=null;
		}

		model=newModel;

		if (newModel!=null)
		{
			changeListener=createChangeListener();
			actionListener=createActionListener();
			newModel.addChangeListener(changeListener);
			newModel.addActionListener(actionListener);

			mnemonic=newModel.getMnemonic();
		}
		else
		{
			mnemonic='\0';
		}

		updateDisplayedMnemonicIndex(getText(), mnemonic);

		firePropertyChange(MODEL_CHANGED_PROPERTY, oldModel, newModel);
		if (newModel!=oldModel)
		{
			revalidate();
			repaint();
		}
	}

	/**
	 * Returns the L&F object that renders this component.
	 *
	 * @return the ButtonUI object
	 * @see #setUI
	 */
	public TristateBoxUI getUI()
	{
		return (TristateBoxUI)ui;
	}

	/**
	 * Sets the L&F object that renders this component.
	 *
	 * @param ui the <code>ButtonUI</code> L&F object
	 * @beaninfo bound: true
	 * hidden: true
	 * attribute: visualUpdate true
	 * description: The UI object that implements the LookAndFeel.
	 * @see #getUI
	 */
	public void setUI(TristateBoxUI ui)
	{
		super.setUI(ui);
	}

	/**
	 * Adds a <code>ChangeListener</code> to the button.
	 *
	 * @param l the listener to be added
	 */
	public void addChangeListener(ChangeListener l)
	{
		listenerList.add(ChangeListener.class, l);
	}

	/**
	 * Removes a ChangeListener from the button.
	 *
	 * @param l the listener to be removed
	 */
	public void removeChangeListener(ChangeListener l)
	{
		listenerList.remove(ChangeListener.class, l);
	}

	/**
	 * Returns an array of all the <code>ChangeListener</code>s added
	 * to this AbstractButton with addChangeListener().
	 *
	 * @return all of the <code>ChangeListener</code>s added or an empty
	 *         array if no listeners have been added
	 * @since 1.4
	 */
	public ChangeListener[] getChangeListeners()
	{
		return (ChangeListener[])(listenerList.getListeners(
				ChangeListener.class));
	}

	/**
	 * Notifies all listeners that have registered interest for
	 * notification on this event type.  The event instance
	 * is lazily created.
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
	 * @param l the <code>ActionListener</code> to be added
	 */
	public void addActionListener(ActionListener l)
	{
		listenerList.add(ActionListener.class, l);
	}

	/**
	 * Removes an <code>ActionListener</code> from the button.
	 * If the listener is the currently set <code>Action</code>
	 * for the button, then the <code>Action</code>
	 * is set to <code>null</code>.
	 *
	 * @param l the listener to be removed
	 */
	public void removeActionListener(ActionListener l)
	{
		if ((l!=null) && (getAction()==l))
		{
			setAction(null);
		}
		else
		{
			listenerList.remove(ActionListener.class, l);
		}
	}

	/**
	 * Returns an array of all the <code>ActionListener</code>s added
	 * to this AbstractButton with addActionListener().
	 *
	 * @return all of the <code>ActionListener</code>s added or an empty
	 *         array if no listeners have been added
	 * @since 1.4
	 */
	public ActionListener[] getActionListeners()
	{
		return (ActionListener[])(listenerList.getListeners(
				ActionListener.class));
	}

	/**
	 * Subclasses that want to handle <code>ChangeEvents</code> differently
	 * can override this to return another <code>ChangeListener</code>
	 * implementation.
	 *
	 * @return the new <code>ButtonChangeListener</code>
	 */
	protected ChangeListener createChangeListener()
	{
		return new ButtonChangeListener();
	}

	/**
	 * Notifies all listeners that have registered interest for
	 * notification on this event type.  The event instance
	 * is lazily created using the <code>event</code>
	 * parameter.
	 *
	 * @param event the <code>ActionEvent</code> object
	 * @see javax.swing.event.EventListenerList
	 */
	protected void fireActionPerformed(ActionEvent event)
	{
		// Guaranteed to return a non-null array
		Object[] listeners=listenerList.getListenerList();
		ActionEvent e=null;
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i=listeners.length-2; i>=0; i-=2)
		{
			if (listeners[i]==ActionListener.class)
			{
				// Lazily create the event:
				if (e==null)
				{
					String actionCommand=event.getActionCommand();
					if (actionCommand==null)
					{
						actionCommand=getActionCommand();
					}
					e=new ActionEvent(TristateBox.this,
							ActionEvent.ACTION_PERFORMED,
							actionCommand,
							event.getWhen(),
							event.getModifiers());
				}
				((ActionListener)listeners[i+1]).actionPerformed(e);
			}
		}
	}

	private class ForwardActionEvents implements ActionListener, Serializable
	{
		public void actionPerformed(ActionEvent event)
		{
			fireActionPerformed(event);
		}
	}

	protected ActionListener createActionListener()
	{
		return new ForwardActionEvents();
	}

	/**
	 * Enables (or disables) the button.
	 *
	 * @param b true to enable the button, otherwise false
	 */
	public void setEnabled(boolean b)
	{
		super.setEnabled(b);
		model.setEnabled(b);
	}

	// *** Deprecated java.awt.Button APIs below *** //

	/**
	 * Returns the label text.
	 *
	 * @return a <code>String</code> containing the label
	 * @deprecated - Replaced by <code>getText</code>
	 */
	public String getLabel()
	{
		return getText();
	}

	/**
	 * Sets the label text.
	 *
	 * @param label a <code>String</code> containing the text
	 * @beaninfo bound: true
	 * description: Replace by setText(text)
	 * @deprecated - Replaced by <code>setText(text)</code>
	 */
	public void setLabel(String label)
	{
		setText(label);
	}

	protected void init(String text)
	{
		setLayout(new OverlayLayout(this));

		if (text!=null)
		{
			setText(text);
		}

		// Set the UI
		updateUI();

		setAlignmentX(LEFT_ALIGNMENT);
		setAlignmentY(CENTER_ALIGNMENT);
	}

	/**
	 * Returns index of the first occurrence of <code>mnemonic</code>
	 * within string <code>text</code>. Matching algorithm is not
	 * case-sensitive.
	 *
	 * @param text     The text to search through, may be null
	 * @param mnemonic The mnemonic to find the character for.
	 * @return index into the string if exists, otherwise -1
	 */
	private static int findDisplayedMnemonicIndex(String text, int mnemonic)
	{
		if (text==null || mnemonic=='\0') return -1;

		char uc=Character.toUpperCase((char)mnemonic);
		char lc=Character.toLowerCase((char)mnemonic);

		int uci=text.indexOf(uc);
		int lci=text.indexOf(lc);

		if (uci==-1)
			return lci;
		else if (lci==-1)
			return uci;
		else
			return (lci<uci) ? lci : uci;
	}

	/**
	 * Extends <code>ChangeListener</code> to be serializable.
	 * <p/>
	 * <strong>Warning:</strong>
	 * Serialized objects of this class will not be compatible with
	 * future Swing releases. The current serialization support is
	 * appropriate for short term storage or RMI between applications running
	 * the same version of Swing.  As of 1.4, support for long term storage
	 * of all JavaBeans<sup><font size="-2">TM</font></sup>
	 * has been added to the <code>java.beans</code> package.
	 * Please see {@link java.beans.XMLEncoder}.
	 */
	protected class ButtonChangeListener implements ChangeListener, Serializable
	{
		ButtonChangeListener()
		{
		}

		public void stateChanged(ChangeEvent e)
		{
			updateMnemonicProperties();
			fireStateChanged();
			repaint();
		}
	}

	private static class ActionPropertyChangeListener implements PropertyChangeListener
	{
		private static ReferenceQueue queue;
		private WeakReference target;
		private Action action;

		public ActionPropertyChangeListener(JComponent c, Action a)
		{
			setTarget(c);
			this.action=a;
		}

		public void setTarget(JComponent c)
		{
			if (queue==null)
			{
				queue=new ReferenceQueue();
			}
			// Check to see whether any old buttons have
			// been enqueued for GC.  If so, look up their
			// PCL instance and remove it from its Action.
			ActionPropertyChangeListener.OwnedWeakReference r;
			while ((r=(ActionPropertyChangeListener.OwnedWeakReference)queue.poll())!=null)
			{
				ActionPropertyChangeListener oldPCL=
						(ActionPropertyChangeListener)r.getOwner();
				Action oldAction=oldPCL.getAction();
				if (oldAction!=null)
				{
					oldAction.removePropertyChangeListener(oldPCL);
				}
			}
			this.target=new ActionPropertyChangeListener.OwnedWeakReference(c, queue, this);
		}

		public JComponent getTarget()
		{
			return (JComponent)this.target.get();
		}

		public Action getAction()
		{
			return action;
		}

		public void propertyChange(PropertyChangeEvent e)
		{
			String propertyName=e.getPropertyName();
			AbstractButton button=(AbstractButton)getTarget();
			if (button==null)
			{   //WeakRef GC'ed in 1.2
				Action action=(Action)e.getSource();
				action.removePropertyChangeListener(this);
			}
			else
			{
				if (e.getPropertyName().equals(Action.NAME))
				{
					Boolean hide=(Boolean)button.getClientProperty("hideActionText");
					if (hide==null || hide==Boolean.FALSE)
					{
						String text=(String)e.getNewValue();
						button.setText(text);
						button.repaint();
					}
				}
				else if (e.getPropertyName().equals(Action.SHORT_DESCRIPTION))
				{
					String text=(String)e.getNewValue();
					button.setToolTipText(text);
				}
				else if ("enabled".equals(propertyName))
				{
					Boolean enabledState=(Boolean)e.getNewValue();
					button.setEnabled(enabledState.booleanValue());
					button.repaint();
				}
				else if (e.getPropertyName().equals(Action.SMALL_ICON))
				{
					Icon icon=(Icon)e.getNewValue();
					button.setIcon(icon);
					button.invalidate();
					button.repaint();
				}
				else if (e.getPropertyName().equals(Action.MNEMONIC_KEY))
				{
					Integer mn=(Integer)e.getNewValue();
					button.setMnemonic(mn.intValue());
					button.invalidate();
					button.repaint();
				}
				else if (e.getPropertyName().equals(Action.ACTION_COMMAND_KEY))
				{
					button.setActionCommand((String)e.getNewValue());
				}
			}
		}

		private static class OwnedWeakReference extends WeakReference
		{
			private Object owner;

			OwnedWeakReference(Object target, ReferenceQueue queue, Object owner)
			{
				super(target, queue);
				this.owner=owner;
			}

			public Object getOwner()
			{
				return owner;
			}
		}
	}
}



