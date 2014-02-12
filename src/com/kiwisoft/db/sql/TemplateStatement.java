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
package com.kiwisoft.db.sql;

import java.util.*;
import java.io.StringWriter;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import com.kiwisoft.sqlPlugin.templates.TemplateManager;
import com.kiwisoft.sqlPlugin.templates.StatementTemplate;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.8 $, $Date: 2006/03/24 17:55:45 $
 */
public class TemplateStatement extends AbstractSQLStatement
{
	private TokenList tokenList;
	private String text;
	private List parameters;
	private boolean valid;
	private String templateName;

	public TemplateStatement(TokenList tokenList)
	{
		this.tokenList=tokenList;
		try
		{
			parse();
		}
		catch (Exception e)
		{
			valid=false;
			throw new RuntimeException(e);
//			e.printStackTrace();
//			valid=false;
		}
	}

	public boolean isValid()
	{
		return valid;
	}

	private void parse() throws Exception
	{
		valid=true;
		List tokens=tokenList.trim();
		assertType(tokens, 0, TemplateToken.class, null);
		assertType(tokens, 1, NameToken.class, null);
		templateName=((Token)tokens.get(1)).getText();
		parameters=new ArrayList();
		Boolean name=null;
		if (tokens.size()>2)
		{
			for (Iterator it=tokens.subList(2, tokens.size()).iterator(); it.hasNext();)
			{
				Token token=(Token)it.next();
				if (token instanceof SpaceToken) continue;
				else if (name==null || name.booleanValue())
				{
					if (token instanceof NameToken || token instanceof StringToken)
					{
						parameters.add(token.getText());
						name=Boolean.FALSE;
					}
					else throw new RuntimeException("Unexpected symbol: "+token.getText());
				}
				else
				{
					if (token instanceof SeparatorToken && ",".equals(token.getNormalizedText())) name=Boolean.TRUE;
					else throw new RuntimeException("Unexpected symbol: "+token.getText());
				}
			}
			if (name==Boolean.TRUE) throw new RuntimeException("Missing parameter.");
		}
		text=resolveTemplate();
	}

	private String resolveTemplate() throws Exception
	{
		StatementTemplate template=TemplateManager.getInstance().getTemplate(templateName);
		if (template==null) throw new RuntimeException("No statement with name '"+templateName+"' found.");
		StringWriter writer = new StringWriter();
		VelocityContext context=new VelocityContext();
		for (int i=0; i<parameters.size(); i++)
		{
			String parameter=(String)parameters.get(i);
			context.put("param"+(i+1), parameter);
		}

		VelocityEngine velocity = new VelocityEngine();
		velocity.init();
		velocity.evaluate(context, writer, getClass().getName(), template.getText());
		return writer.getBuffer().toString();
	}

	private void assertType(List tokens, int offset, Class aClass, String text)
	{
		Token token=(Token)tokens.get(offset);
		if (!aClass.isInstance(token))
			throw new RuntimeException("Invalid statement");
		if (text!=null && !text.equals(token.getNormalizedText()))
			throw new RuntimeException("Invalid statement");
	}

	public String getText()
	{
		return tokenList.getText();
	}

	public String getNormalizedText()
	{
		return tokenList.getNormalizedText();
	}

	public String getSuccessMessage(int updateCount)
	{
		return "Statement executed.";
	}

	public boolean isSimpleSelect()
	{
		return false;
	}

	public String getTableName()
	{
		return null;
}

	public String getResolvedText()
	{
		if (text!=null) return text;
		return "/* Couldn't resolve template */";
	}

	public String getTemplateName()
	{
		return templateName;
	}
}
