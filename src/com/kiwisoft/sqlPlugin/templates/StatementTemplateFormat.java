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
package com.kiwisoft.sqlPlugin.templates;

import javax.swing.*;

import com.kiwisoft.utils.format.DefaultObjectFormat;
import com.kiwisoft.utils.gui.IconManager;
import com.kiwisoft.sqlPlugin.Icons;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.4 $, $Date: 2006/03/24 18:15:18 $
 */
public class StatementTemplateFormat extends DefaultObjectFormat
{
	public StatementTemplateFormat(String name)
	{
		super(name);
	}

	public boolean canFormat(Class aClass)
	{
		return StatementTemplateImpl.class==aClass;
	}

	public String format(Object value)
	{
		if (value instanceof StatementTemplate)
		{
			StatementTemplate template=(StatementTemplate)value;
			return template.getName();
		}
		return super.format(value);
	}

	public Icon getIcon(Object value)
	{
		if (value instanceof StatementTemplate)
		{
			return IconManager.getIcon(Icons.TEMPLATE);
		}
		return super.getIcon(value);
	}
}
