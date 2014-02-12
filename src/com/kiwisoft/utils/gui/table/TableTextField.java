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

import java.awt.event.KeyEvent;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.text.Document;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.4 $, $Date: 2006/03/24 18:04:07 $
 */
public class TableTextField extends JTextField
{
	public void setDocument(Document doc)
	{
		super.setDocument(doc);
		if (doc!=null) doc.putProperty("filterNewlines", Boolean.FALSE);
	}

	public boolean processKeyBinding(KeyStroke ks, KeyEvent e, int condition, boolean pressed)
	{
		return super.processKeyBinding(ks, e, condition, pressed);
	}
}