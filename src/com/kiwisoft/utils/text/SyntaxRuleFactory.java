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
package com.kiwisoft.utils.text;

import com.kiwisoft.utils.xml.DefaultXMLObject;
import com.kiwisoft.utils.xml.XMLContext;
import com.kiwisoft.utils.xml.XMLObject;
import com.kiwisoft.utils.xml.XMLObjectFactory;
import org.xml.sax.Attributes;

/**
 * Creates parser rules.
 *
 * @author Stefan Stiller
 * @version $Revision: 1.3 $, $Date: 2006/03/24 18:02:55 $
 */
public class SyntaxRuleFactory implements XMLObjectFactory
{
	public XMLObject createElement(XMLContext context, String name, Attributes attributes)
	{
		if ("sequence".equalsIgnoreCase(name))
		{
			String id=attributes.getValue("id");
			if ("whitespace".equalsIgnoreCase(id))
			{
				return new SyntaxRule(null, SyntaxParser.WHITESPACE, Style.DEFAULT);
			}
			else
			{
				byte styleId=Style.getStyleId(id);
				return new SyntaxRule(null, 0, styleId);
			}
		}
		else if ("lineComment".equalsIgnoreCase(name))
		{
			byte styleId=Style.getStyleId(attributes.getValue("id"));
			return new SyntaxRule(null, SyntaxParser.EOL_SPAN, styleId);
		}
		else if ("blockComment".equalsIgnoreCase(name) || "literal".equalsIgnoreCase(name))
		{
			byte token=Style.getStyleId(attributes.getValue("id"));
			String[] strings=new String[]{attributes.getValue("begin"), attributes.getValue("end")};
			return new SyntaxRule(strings, SyntaxParser.SPAN, token);
		}
		else if ("markFollowing".equalsIgnoreCase(name))
		{
			byte token=Style.getStyleId(attributes.getValue("id"));
			return new SyntaxRule(new String[]{attributes.getValue("begin")}, SyntaxParser.MARK_FOLLOWING, token);
		}
		else if ("markPrevious".equalsIgnoreCase(name))
		{
			byte token=Style.getStyleId(attributes.getValue("id"));
			return new SyntaxRule(new String[]{attributes.getValue("end")}, SyntaxParser.MARK_PREVIOUS, token);
		}
		return new DefaultXMLObject(context, name);
	}
}
