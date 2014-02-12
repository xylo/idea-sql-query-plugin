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

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.2 $, $Date: 2006/03/24 18:33:39 $
 */
public abstract class AbstractValidator implements DocumentListener, ActionListener, PropertyChangeListener
{
	public void insertUpdate(DocumentEvent e)
	{
		validate();
	}

	public void removeUpdate(DocumentEvent e)
	{
		validate();
	}

	public void changedUpdate(DocumentEvent e)
	{
		validate();
	}

	public void actionPerformed(ActionEvent e)
	{
		validate();
	}

	public void propertyChange(PropertyChangeEvent evt)
	{
		validate();
	}

	public abstract void validate();
}
