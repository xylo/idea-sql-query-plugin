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

import java.io.File;
import java.io.FileOutputStream;
import java.util.*;

import com.intellij.openapi.application.PathManager;
import com.kiwisoft.utils.xml.*;
import com.kiwisoft.sqlPlugin.settings.TemplateHandler;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.6 $, $Date: 2006/03/24 18:15:19 $
 */
public class TemplateManager extends XMLAdapter implements TemplateHandler
{
	private static final String PATH=System.getProperty("sqlPlugin.templates")==null
									 ? PathManager.getConfigPath()+File.separator+"sqlPlugin"+File.separator+"templates.xml"
									 : System.getProperty("sqlPlugin.templates");

	private static TemplateManager instance;

	public static TemplateManager getInstance()
	{
		if (instance==null) instance=new TemplateManager();
		return instance;
	}

	public static boolean hasInstance()
	{
		return instance!=null;
	}

	private Map templates=new HashMap();

	private TemplateManager()
	{
		loadTemplates();
	}

	public StatementTemplate getTemplate(String name)
	{
		return (StatementTemplate)templates.get(name);
	}

	private void loadTemplates()
	{
		File file=new File(PATH);
		if (file.exists())
		{
			XMLHandler handler=new XMLHandler();
			handler.addTagMapping("templates", this);
			handler.addTagMapping("template", StatementTemplateImpl.class);
			handler.loadFile(file);
		}
	}

	public void saveTemplates()
	{
		File file=new File(PATH);
		file.getParentFile().mkdirs();
		try
		{
			XMLWriter writer=new XMLWriter(new FileOutputStream(file), null);
			writer.start();
			writer.startElement("templates");
			for (Iterator it=getTemplates().iterator(); it.hasNext();)
			{
				StatementTemplate template=(StatementTemplate)it.next();
				writer.startElement("template");
				writer.setAttribute("name", template.getName());
				writer.setAttribute("value", template.getText());
				writer.closeElement("template");
			}
			writer.closeElement("templates");
			writer.close();
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	// XMLObject interface

	public void addXMLElement(XMLContext context, XMLObject element)
	{
		if (element instanceof StatementTemplate)
		{
			templates.put(((StatementTemplate)element).getName(), element);
		}
	}

	public Collection getTemplates()
	{
		return Collections.unmodifiableCollection(templates.values());
	}

	public void removeAllTemplates()
	{
		templates.clear();
	}

	public StatementTemplate createTemplate(String name, String text)
	{
		return new StatementTemplateImpl(name, text);
	}

	public void addTemplate(StatementTemplate template)
	{
		templates.put(template.getName(), template);
	}

	public void removeTemplate(StatementTemplate template)
	{
		templates.remove(template.getName());
	}

	public boolean isNameValid(String name, StatementTemplate template)
	{
		StatementTemplate tmp=getTemplate(name);
		return template==null ? tmp==null : tmp==null || tmp==template;
	}
}
