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

import java.util.HashMap;
import java.util.Map;

/**
 * @author Stefan Stiller
 * @author Mark Soderquist
 * @version $Revision: 1.3 $, $Date: 2006/03/24 18:02:55 $
 */
public class SyntaxDefinitionFactory
{
	private static SyntaxDefinitionFactory instance;

	private Map definitions=new HashMap();

	private SyntaxDefinitionFactory()
	{
		addSyntaxDefinition(new SyntaxDefinition("text", "syntaxes/text.xml"));
		addSyntaxDefinition(new SyntaxDefinition("sql", "syntaxes/sql.xml"));
		addSyntaxDefinition(new SyntaxDefinition("java", "syntaxes/java.xml"));
		addSyntaxDefinition(new SyntaxDefinition("bsh", "syntaxes/bsh.xml"));
	}

	public static SyntaxDefinitionFactory getInstance()
	{
		if (instance==null) instance=new SyntaxDefinitionFactory();
		return instance;
	}

	public void addSyntaxDefinition(SyntaxDefinition aoSyntax)
	{
		definitions.put(aoSyntax.getName(), aoSyntax);
	}

	public SyntaxDefinition getSyntaxDefinition(String asName)
	{
		return (SyntaxDefinition) definitions.get(asName);
	}
}