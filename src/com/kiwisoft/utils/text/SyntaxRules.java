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

import javax.swing.text.Segment;

import com.kiwisoft.utils.xml.DefaultXMLObject;
import com.kiwisoft.utils.xml.XMLContext;
import com.kiwisoft.utils.xml.XMLObject;
import com.kiwisoft.utils.xml.XMLAdapter;

/**
 * A set of parser rules.
 *
 * @author Mike Dillon
 * @author Stefan Stiller
 * @version $Revision: 1.3 $, $Date: 2006/03/24 18:02:55 $
 */
public class SyntaxRules extends XMLAdapter
{
	private static final int MAP_SIZE=32;

	private KeywordMap keywordMap;

	private SyntaxRule[] ruleMapFirst;
	private SyntaxRule[] ruleMapLast;

	private SyntaxRule escapeRule;
	private Segment escapePattern;
	private int terminateChar=-1;
	private boolean ignoreCase;
	private boolean highlightDigits;
	private byte defaultToken;

	private String name="MAIN";

	public SyntaxRules()
	{
		initArrays();
	}

	private void initArrays()
	{
		ruleMapFirst=new SyntaxRule[MAP_SIZE];
		ruleMapLast=new SyntaxRule[MAP_SIZE];
	}

	public void addRule(SyntaxRule rule)
	{
		int key=0;
		try
		{
			key=Character.toUpperCase(rule.getCharacters()[0])%MAP_SIZE;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		SyntaxRule lastRule=ruleMapLast[key];
		if (lastRule==null)
		{
			ruleMapFirst[key]=ruleMapLast[key]=rule;
		}
		else
		{
			lastRule.setNextRule(rule);
			ruleMapLast[key]=rule;
		}
	}

	public SyntaxRule getRules(char ch)
	{
		int key=Character.toUpperCase(ch)%MAP_SIZE;
		return ruleMapFirst[key];
	}

	public int getTerminateChar()
	{
		return terminateChar;
	}

	public void setTerminateChar(int ch)
	{
		terminateChar=ch>=0 ? ch : -1;
	}

	public boolean getIgnoreCase()
	{
		return ignoreCase;
	}

	public void setIgnoreCase(boolean value)
	{
		ignoreCase=value;
		getKeywords().setIgnoreCase(ignoreCase);
	}

	public KeywordMap getKeywords()
	{
		if (keywordMap==null)
		{
			keywordMap=new KeywordMap();
			keywordMap.setIgnoreCase(getIgnoreCase());
		}
		return keywordMap;
	}

	public boolean getHighlightDigits()
	{
		return highlightDigits;
	}

	public void setHighlightDigits(boolean value)
	{
		highlightDigits=value;
	}

	public SyntaxRule getEscapeRule()
	{
		return escapeRule;
	}

	public Segment getEscapePattern()
	{
		if (escapePattern==null && escapeRule!=null)
		{
			escapePattern=new Segment(escapeRule.getCharacters(), 0, escapeRule.getLength(0));
		}
		return escapePattern;
	}

	public void setEscape(String value)
	{
		if (value==null) escapeRule=null;
		else escapeRule=new SyntaxRule(new String[]{value}, SyntaxParser.IS_ESCAPE, Style.DEFAULT);
		escapePattern=null;
	}

	public byte getDefault()
	{
		return defaultToken;
	}

	public void setDefault(byte value)
	{
		defaultToken=value;
	}

	public String getName()
	{
		return name;
	}

	// XMLObject interface

	public SyntaxRules(XMLContext context, String name)
	{
		super(context, name);
		initArrays();
	}

	public void setXMLAttribute(XMLContext context, String name, String value)
	{
		if ("id".equalsIgnoreCase(name))
			this.name=value;
		else if ("ignoreCase".equalsIgnoreCase(name))
			setIgnoreCase(Boolean.valueOf(value).booleanValue());
		else if ("highlightDigits".equalsIgnoreCase(name))
			setHighlightDigits(Boolean.valueOf(value).booleanValue());
		else if ("terminateChar".equalsIgnoreCase(name))
			setTerminateChar(Integer.valueOf(value).intValue());
		else if ("escape".equalsIgnoreCase(name))
			setEscape(value);
	}

	public void addXMLElement(XMLContext context, XMLObject element)
	{
		if (element instanceof DefaultXMLObject)
		{
			DefaultXMLObject xmlObject=(DefaultXMLObject) element;
			if ("keyword".equalsIgnoreCase(xmlObject.getName()))
			{
                addKeyword(Style.getStyleId(xmlObject.getAttribute("id")), xmlObject.getContent());
            }
		}
		else if (element instanceof SyntaxRule)
		{
			addRule((SyntaxRule) element);
		}
	}

    public void addKeyword(byte styleId, String keyword)
    {
        getKeywords().add(keyword, styleId);
    }
}
