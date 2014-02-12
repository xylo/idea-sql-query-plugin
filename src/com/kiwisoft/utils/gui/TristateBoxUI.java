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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicGraphicsUtils;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.4 $, $Date: 2006/03/24 18:50:51 $
 */
public class TristateBoxUI extends ComponentUI
{
	private final static TristateBoxUI checkboxUI=new TristateBoxUI();

	public static final String PREFIX="TristateBox.";

	protected Color focusColor;
	protected Color disabledTextColor;
	protected Icon icon;

	private boolean defaults_initialized;

	// ********************************
	//         Create PlAF
	// ********************************

	/** @noinspection UNUSED_SYMBOL*/
	public static ComponentUI createUI(JComponent b)
	{
		return checkboxUI;
	}

	// ********************************
	//          Install PLAF
	// ********************************

	public void installUI(JComponent c)
	{
		installDefaults((TristateBox)c);
		installListeners((TristateBox)c);
		installKeyboardActions((TristateBox)c);
	}

	public void installDefaults(TristateBox b)
	{
		if (!defaults_initialized)
		{
			focusColor=UIManager.getColor(PREFIX+"focus");
			disabledTextColor=UIManager.getColor(PREFIX+"disabledText");
			icon=UIManager.getIcon(PREFIX+"icon");
			defaults_initialized=true;
		}
		b.setOpaque(true);

		if (b.getMargin()==null || (b.getMargin() instanceof UIResource))
		{
			b.setMargin(UIManager.getInsets(PREFIX+"margin"));
		}
		LookAndFeel.installColorsAndFont(b, PREFIX+"background", PREFIX+"foreground", PREFIX+"font");
		LookAndFeel.installBorder(b, PREFIX+"border");
	}

	protected void installListeners(TristateBox b)
	{
		TristateBoxListener listener=new TristateBoxListener();
		// put the listener in the button's client properties so that
		// we can get at it later
		b.putClientProperty(this, listener);

		b.addMouseListener(listener);
		b.addMouseMotionListener(listener);
		b.addFocusListener(listener);
		b.addPropertyChangeListener(listener);
		b.addChangeListener(listener);
	}

	protected void installKeyboardActions(TristateBox b)
	{
		TristateBoxListener listener=(TristateBoxListener)b.getClientProperty(this);
		if (listener!=null) listener.installKeyboardActions(b);
	}

	public void uninstallUI(JComponent c)
	{
		uninstallKeyboardActions((TristateBox)c);
		uninstallListeners((TristateBox)c);
		uninstallDefaults((TristateBox)c);
	}

	protected void uninstallKeyboardActions(TristateBox b)
	{
		TristateBoxListener listener=(TristateBoxListener)b.getClientProperty(this);
		if (listener!=null) listener.uninstallKeyboardActions(b);
	}

	protected void uninstallListeners(TristateBox b)
	{
		TristateBoxListener listener=(TristateBoxListener)b.getClientProperty(this);
		b.putClientProperty(this, null);
		if (listener!=null)
		{
			b.removeMouseListener(listener);
			b.removeMouseListener(listener);
			b.removeMouseMotionListener(listener);
			b.removeFocusListener(listener);
			b.removeChangeListener(listener);
			b.removePropertyChangeListener(listener);
		}
	}

	protected void uninstallDefaults(TristateBox b)
	{
		LookAndFeel.uninstallBorder(b);
		defaults_initialized=false;
	}

	// ********************************
	//         Default Accessors
	// ********************************

	protected Color getDisabledTextColor()
	{
		return disabledTextColor;
	}

	protected Color getFocusColor()
	{
		return focusColor;
	}

	public Icon getDefaultIcon()
	{
		return icon;
	}

	// ********************************
	//        Paint Methods
	// ********************************

	public synchronized void paint(Graphics g, JComponent c)
	{

		TristateBox b=(TristateBox)c;
		TristateModel model=b.getModel();

		Dimension size=c.getSize();

		Font f=c.getFont();
		g.setFont(f);
		FontMetrics fm=g.getFontMetrics();

		Rectangle viewRect=new Rectangle(size);
		Rectangle iconRect=new Rectangle();
		Rectangle textRect=new Rectangle();

		Insets i=c.getInsets();
		viewRect.x+=i.left;
		viewRect.y+=i.top;
		viewRect.width-=(i.right+viewRect.x);
		viewRect.height-=(i.bottom+viewRect.y);

		String text=SwingUtilities.layoutCompoundLabel(
				c, fm, b.getText(), getDefaultIcon(),
				b.getVerticalAlignment(), b.getHorizontalAlignment(),
				b.getVerticalTextPosition(), b.getHorizontalTextPosition(),
				viewRect, iconRect, textRect, b.getIconTextGap());

		// fill background
		if (c.isOpaque())
		{
			g.setColor(b.getBackground());
			g.fillRect(0, 0, size.width, size.height);
		}

		getDefaultIcon().paintIcon(c, g, iconRect.x, iconRect.y);

		// Draw the Text
		if (text!=null)
		{
			int mnemIndex=b.getDisplayedMnemonicIndex();
			if (model.isEnabled())
			{
				// *** paint the text normally
				g.setColor(b.getForeground());
				BasicGraphicsUtils.drawStringUnderlineCharAt(g, text,
						mnemIndex, textRect.x, textRect.y+fm.getAscent());
			}
			else
			{
				// *** paint the text disabled
				g.setColor(getDisabledTextColor());
				BasicGraphicsUtils.drawStringUnderlineCharAt(g, text,
						mnemIndex, textRect.x, textRect.y+fm.getAscent());
			}
			if (b.hasFocus() && b.isFocusPainted() &&
					textRect.width>0 && textRect.height>0)
			{
				paintFocus(g, textRect);
			}
		}
		Border border=b.getBorder();
		if (border!=null) border.paintBorder(b, g, 0, 0, size.width, size.height);
	}

	protected void paintFocus(Graphics g, Rectangle t)
	{
		g.setColor(getFocusColor());
		g.drawRect(t.x-1, t.y-1, t.width+1, t.height+1);
	}

	/* These Insets/Rectangles are allocated once for all
	 * RadioButtonUI.getPreferredSize() calls.  Re-using rectangles
	 * rather than allocating them in each call substantially
	 * reduced the time it took getPreferredSize() to run.  Obviously,
	 * this method can't be re-entered.
	 */
	private static Rectangle prefViewRect=new Rectangle();
	private static Rectangle prefIconRect=new Rectangle();
	private static Rectangle prefTextRect=new Rectangle();
	private static Insets prefInsets=new Insets(0, 0, 0, 0);

	/**
	 * The preferred size of the radio button
	 */
	public Dimension getPreferredSize(JComponent c)
	{
		if (c.getComponentCount()>0)
		{
			return null;
		}

		TristateBox b=(TristateBox)c;
		String text=b.getText();
		Icon buttonIcon=getDefaultIcon();

		Font font=b.getFont();

		// XXX - getFontMetrics has been deprecated but there isn't a
		// suitable replacement
		//noinspection deprecation
		FontMetrics fm=b.getToolkit().getFontMetrics(font);

		prefViewRect.x=prefViewRect.y=0;
		prefViewRect.width=Short.MAX_VALUE;
		prefViewRect.height=Short.MAX_VALUE;
		prefIconRect.x=prefIconRect.y=prefIconRect.width=prefIconRect.height=0;
		prefTextRect.x=prefTextRect.y=prefTextRect.width=prefTextRect.height=0;

		SwingUtilities.layoutCompoundLabel(
				c, fm, text, buttonIcon,
				b.getVerticalAlignment(), b.getHorizontalAlignment(),
				b.getVerticalTextPosition(), b.getHorizontalTextPosition(),
				prefViewRect, prefIconRect, prefTextRect,
				text==null ? 0 : b.getIconTextGap());

		// find the union of the icon and text rects (from Rectangle.java)
		int x1=Math.min(prefIconRect.x, prefTextRect.x);
		int x2=Math.max(prefIconRect.x+prefIconRect.width,
				prefTextRect.x+prefTextRect.width);
		int y1=Math.min(prefIconRect.y, prefTextRect.y);
		int y2=Math.max(prefIconRect.y+prefIconRect.height,
				prefTextRect.y+prefTextRect.height);
		int width=x2-x1;
		int height=y2-y1;

		prefInsets=b.getInsets(prefInsets);
		width+=prefInsets.left+prefInsets.right;
		height+=prefInsets.top+prefInsets.bottom;
		return new Dimension(width, height);
	}

	public Dimension getMinimumSize(JComponent c)
	{
		return getPreferredSize(c);
	}

	public Dimension getMaximumSize(JComponent c)
	{
		return getPreferredSize(c);
	}

	public String getPropertyPrefix()
	{
		return PREFIX;
	}

}
