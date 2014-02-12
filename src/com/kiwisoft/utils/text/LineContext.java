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

/**
 * Stores information needed to render a line of syntax highlighted text.
 *
 * @author Slava Pestov
 * @author Mike Dillon
 * @author Mark Soderquist
 * @author Stefan Stiller
 * @version $Revision: 1.3 $, $Date: 2006/03/24 18:50:49 $
 */
public class LineContext implements Cloneable
{
	/**
	 * Used to flag the <code>SyntaxView</code> object that
	 * the next line may need to be painted due to the parse rules.
	 * An example is multi-line comments.
	 */
	private boolean nextLineRequested;

	/**
	 * Used to flag the <code>SyntaxView</code> object that
	 * the next line may need to be painted due to the parse rules.
	 * This is only checked if <code>mbNextLineRequested</code> is true.
	 */
	private boolean nextLineUnpainted;

	/**
	 * Used by the <code>TokenMarker</code> to store a reference to
	 * parent syntax rules when marking deleate tokens.  Do not use
	 * for any other purpose.
	 */
	private LineContext parentContext;

	/**
	 * Used by the <code>TokenMarker</code> to store a reference to
	 * the <code>ParserRule</code> that the marker is "inside". Do not
	 * use for any other purpose.
	 */
	private SyntaxRule currentRule;

	/**
	 Used by the <code>TokenMarker</code> to store a reference to
	 the <code>ParserRuleSet</code> for this context.  Do not use
	 for any other purpose.
	 */
	private SyntaxRules syntaxRules;

	/**
	 * Used by the <code>SyntaxDocument</code> to store the valid tokens
	 * for later use.  Do not use for any other purpose.
	 */
	private TokenList tokenList;

	public LineContext()
	{
		this(null);
	}

	public LineContext(SyntaxRules syntaxRules)
	{
		this(syntaxRules, (SyntaxRule) null);
	}

	private LineContext(SyntaxRules syntaxRules, SyntaxRule rule)
	{
		this.setCurrentRule(rule);
		this.setSyntaxRules(syntaxRules);
	}

	public LineContext(SyntaxRules syntaxRules, LineContext parentContext)
	{
		this.setSyntaxRules(syntaxRules);
		if (parentContext!=null) this.setParentContext((LineContext) parentContext.clone());
	}

	public int hashCode()
	{
		if (getCurrentRule()!=null)
			return getCurrentRule().hashCode();
		else if (getSyntaxRules()!=null)
			return getSyntaxRules().hashCode();
		else
			return 0;
	}

	public boolean equals(Object obj)
	{
		if (obj instanceof LineContext)
		{
			LineContext context=(LineContext) obj;
			if (context.getParentContext()==null)
			{
				if (getParentContext()!=null) return false;
			}
			else
			{
				if (getParentContext()==null)
					return false;
				else if (!context.getParentContext().equals(getParentContext())) return false;
			}
			return context.getCurrentRule()==getCurrentRule() && context.getSyntaxRules()==getSyntaxRules();
		}
		return false;
	}

	public Object clone()
	{
		LineContext clone=new LineContext();
		clone.setCurrentRule(getCurrentRule());
		clone.setSyntaxRules(getSyntaxRules());
		clone.setParentContext(getParentContext()==null ? null : (LineContext) getParentContext().clone());
		return clone;
	}

	public boolean isNextLineRequested()
	{
		return nextLineRequested;
	}

	public void setNextLineRequested(boolean nextLineRequested)
	{
		this.nextLineRequested=nextLineRequested;
	}

	public boolean isNextLineUnpainted()
	{
		return nextLineUnpainted;
	}

	public void setNextLineUnpainted(boolean nextLineUnpainted)
	{
		this.nextLineUnpainted=nextLineUnpainted;
	}

	public LineContext getParentContext()
	{
		return parentContext;
	}

	public void setParentContext(LineContext parentContext)
	{
		this.parentContext=parentContext;
	}

	public SyntaxRule getCurrentRule()
	{
		return currentRule;
	}

	public void setCurrentRule(SyntaxRule currentRule)
	{
		this.currentRule=currentRule;
	}

	public SyntaxRules getSyntaxRules()
	{
		return syntaxRules;
	}

	public void setSyntaxRules(SyntaxRules syntaxRules)
	{
		this.syntaxRules=syntaxRules;
	}

	public TokenList getTokenList()
	{
		return tokenList;
	}

	public void setTokenList(TokenList tokenList)
	{
		this.tokenList=tokenList;
	}

}

