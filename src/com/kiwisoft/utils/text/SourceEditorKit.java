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

import javax.swing.text.DefaultEditorKit;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;

/**
 * An implementation of <code>EditorKit</code> used for syntax highlighting.
 * It implements a view factory that maps elements to syntax highlighting
 * views.<p>
 *
 * This editor kit can be plugged into text components to give them colorization features.
 *
 * @author Mark Soderquist
 * @author Stefan Stiller
 * @version $Revision: 1.3 $, $Date: 2006/03/24 18:50:50 $
 * @see SourceDocument
 * @see SourceView
 */
class SourceEditorKit extends DefaultEditorKit implements ViewFactory
{
	/**
	 * Returns an instance of a view factory that can be used for
	 * creating views from elements. This implementation returns
	 * the current instance, because this class already implements
	 * ViewFactory.
	 */
	public ViewFactory getViewFactory()
	{
		return this;
	}

	/**
	 * Creates a new instance of the default document for this
	 * editor kit. This returns a new instance of SyntaxDocument.
	 *
	 * @see SourceDocument
	 */
	public Document createDefaultDocument()
	{
		return new SourceDocument();
	}

	/**
	 * Creates a view from an element that can be used for painting that
	 * element. This implementation returns a new SyntaxView instance.
	 *
	 * @param element The element.
	 * @see SourceView
	 */
	public View create(Element element)
	{
		return new SourceView(element);
	}
}
