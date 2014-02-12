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

import java.awt.Color;
import javax.swing.JTextPane;
import javax.swing.text.*;
import javax.swing.text.Style;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTML;

import com.kiwisoft.utils.StringUtils;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.2 $, $Date: 2006/03/24 18:50:50 $
 */
public class MessagePane extends JTextPane
{
	public static final String DEFAULT_STYLE="default";
	public static final String INFO_STYLE="info";
	public static final String WARNING_STYLE="warning";
	public static final String ERROR_STYLE="error";
	public static final String LINK_STYLE="link";

	public MessagePane()
	{
		setContentType("text/html");
		clear();
		setEditable(false);
	}

	public void clear()
	{
		setText("<html><body></body></html>");
	}

	public void createBaseStyles()
	{
		javax.swing.text.Style warningStyle=createStyle(WARNING_STYLE, null);
		warningStyle.addAttribute(StyleConstants.FontConstants.Foreground, Color.ORANGE);

		Style errorStyle=createStyle(ERROR_STYLE, null);
		errorStyle.addAttribute(StyleConstants.FontConstants.Foreground, Color.RED);

		Style infoStyle=createStyle(INFO_STYLE, null);
		infoStyle.addAttribute(StyleConstants.FontConstants.Foreground, Color.GREEN.darker());

		Style linkStyle=createStyle(LINK_STYLE, null);
		linkStyle.addAttribute(StyleConstants.FontConstants.Foreground, Color.BLUE);
	}

	private HTMLDocument getHTMLDocument()
	{
		return (HTMLDocument)getDocument();
	}

	public Style createStyle(String name)
	{
		return createStyle(name, null);
	}

	public Style createStyle(String name, String baseName)
	{
		HTMLDocument document=getHTMLDocument();
		return document.addStyle(name, getStyle(document, baseName));
	}

	private static Style getStyle(HTMLDocument document, String styleName)
	{
		return document.getStyle(styleName!=null ? styleName : DEFAULT_STYLE);
	}

	public void appendText(String text)
	{
		appendText(text, null);
	}

	public void appendText(String text, String style)
	{
		HTMLDocument document=getHTMLDocument();
		insertText(document, text, getStyle(document, style));
	}

	public void appendLink(String text, String ref)
	{
		appendLink(text, ref, null);
	}

	public void appendLink(String text, String ref, String styleName)
	{
		HTMLDocument document=getHTMLDocument();

		SimpleAttributeSet href=new SimpleAttributeSet();
		href.addAttribute(HTML.Attribute.HREF, ref);

		SimpleAttributeSet style=new SimpleAttributeSet(getStyle(document, styleName));
		style.addAttribute(HTML.Tag.A, href);
		style.addAttribute(StyleConstants.Underline, Boolean.TRUE);

		insertText(document, text, style);
	}

	public void appendLineBreak()
	{
		appendText("\n");
	}

	public boolean isEmpty()
	{
		try
		{
			return StringUtils.isEmpty(getDocument().getText(0, getDocument().getLength()));
		}
		catch (BadLocationException e)
		{
			e.printStackTrace();
			return false;
		}
	}

	private void insertText(Document document, String text, AttributeSet style)
	{
		try
		{
			document.insertString(document.getLength(), text, style);
		}
		catch (BadLocationException e)
		{
			e.printStackTrace();
		}
	}
}
