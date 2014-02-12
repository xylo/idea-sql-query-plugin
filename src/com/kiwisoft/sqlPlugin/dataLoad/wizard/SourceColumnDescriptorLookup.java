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
package com.kiwisoft.sqlPlugin.dataLoad.wizard;

import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

import com.kiwisoft.utils.gui.lookup.ListLookup;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.sqlPlugin.dataLoad.SourceColumnDescriptor;

/**
 * @author Stefan Stiller
 * @version $Revision: $, $Date: $
 */
public class SourceColumnDescriptorLookup extends ListLookup
{
	private Reference wizardReference;

	public SourceColumnDescriptorLookup(DataLoadWizard wizard)
	{
		this.wizardReference=new WeakReference(wizard);
	}

	public Collection getValues(String text, Object currentValue)
	{
		DataLoadWizard wizard=(DataLoadWizard)wizardReference.get();
		if (wizard!=null)
		{
			List values=new ArrayList();
			Set columns=wizard.getSourceColumns();
			if (columns==null)
			{
				columns=new LinkedHashSet();
				for (Iterator it=wizard.getDescriptor().getSourceColumns().iterator(); it.hasNext();)
					columns.add(((SourceColumnDescriptor)it.next()).getName());
			}
			if (StringUtils.isEmpty(text))
			{
				return columns;
			}
			else
			{
				text=text+".*";
				Pattern pattern=Pattern.compile(text, Pattern.CASE_INSENSITIVE);
				for (Iterator it=columns.iterator(); it.hasNext();)
				{
					String column=(String)it.next();
					Matcher matcher=pattern.matcher(column);
					if (matcher.matches())
					{
						values.add(column);
					}
				}
			}
			return values;
		}
		return Collections.EMPTY_SET;
	}
}
