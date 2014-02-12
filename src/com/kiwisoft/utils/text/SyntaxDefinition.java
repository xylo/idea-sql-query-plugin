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

import com.kiwisoft.utils.xml.XMLContext;
import com.kiwisoft.utils.xml.XMLHandler;
import com.kiwisoft.utils.xml.XMLObject;
import com.kiwisoft.utils.xml.XMLAdapter;

/**
 * An edit syntax defines specific settings for editing some type of file.
 * One instance of this class is created for each supported edit syntax.
 * In most cases, instances of this class can be created directly, however
 * if the edit syntax needs to define custom indentation behaviour,
 * subclassing is required.
 *
 * @author Slava Pestov
 * @author Stefan Stiller
 * @version $Revision: 1.3 $, $Date: 2006/03/24 18:02:55 $
 */
public class SyntaxDefinition extends XMLAdapter implements Comparable
{
	private String name;
	private String resource;

	private SyntaxParser syntaxParser;

	/**
	 * Creates a new edit syntax.
	 *
	 * @param name The name used in syntax listings and to query syntax properties.
	 */
	public SyntaxDefinition(String name, String resource)
	{
		if (name==null) throw new IllegalArgumentException("Syntax name can not be null.");
		this.name=name;
		this.resource=resource;
	}

	public SyntaxParser getSyntaxParser()
	{
		if (syntaxParser==null)
		{
			syntaxParser=new SyntaxParser();
			syntaxParser.setName(name);
			loadGrammar();
		}
		return syntaxParser;
	}

	/**
	 * Load the grammar for the specifed syntax.
	 */
	protected void loadGrammar()
	{
		XMLHandler handler=new XMLHandler();
		handler.setTrimText(false);

		handler.addTagMapping("syntaxDefinition", this);

		handler.addTagMapping("syntaxRules", SyntaxRules.class);

		SyntaxRuleFactory ruleFactory=new SyntaxRuleFactory();
		handler.addTagMapping("lineComment", ruleFactory);
		handler.addTagMapping("blockComment", ruleFactory);
		handler.addTagMapping("literal", ruleFactory);
		handler.addTagMapping("sequence", ruleFactory);
		handler.addTagMapping("markPrevious", ruleFactory);
		handler.addTagMapping("markFollowing", ruleFactory);

		handler.loadStream(SyntaxDefinition.class.getResourceAsStream(resource));
	}

	/**
	 * Returns the internal name of this edit syntax.
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Implementation of the <code>Comparable</code> interface.
	 */
	public int compareTo(Object oObject)
	{
		return getName().compareTo(((SyntaxDefinition)oObject).getName());
	}

	// Interface XMLObject

	public void addXMLElement(XMLContext context, XMLObject element)
	{
		if (element instanceof SyntaxRules)
		{
			SyntaxRules rules=(SyntaxRules) element;
			getSyntaxParser().addRules(rules.getName(), rules);
		}
	}
}
