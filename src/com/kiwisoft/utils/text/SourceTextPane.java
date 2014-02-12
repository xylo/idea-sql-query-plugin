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

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.KeyEvent;
import javax.swing.*;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.EditorKit;
import javax.swing.text.JTextComponent;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

/**
 * <p>The <code>SourceTextPane</code> is a powerful set of classes for editing primarily
 * text.  It may be adapted to handle editing of binary files at a future date.</p>
 * <p/>
 * <p>The <code>SourceTextPane</code> was derived from two versions of the program
 * <a href="http://www.jedit.org">jEdit</a> written by Slava Pestov.</p>
 *
 * @author Slava Pestov
 * @author Mark Soderquist
 * @author Stefan Stiller
 * @author Thomas Saxtoft
 * @version $Revision: 1.4 $, $Date: 2006/03/24 18:50:50 $
 */
public class SourceTextPane extends JEditorPane
{
	/**
	 * The default editor kit for this text component.
	 */
	private static final EditorKit EDITOR_KIT=new SourceEditorKit();

	private static final Cursor TEXT_CURSOR=Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR);

	private boolean antiAliasing;
	private int tabSize=4;
	private StyleSchema styleSchema;

	/**
	 * Create a new <code>SyntaxTextPane</code> component without syntax highlighting by default.
	 */
	public SourceTextPane(SyntaxDefinition syntaxDefinition)
	{
		// Setup the cursor.
		setCursor(TEXT_CURSOR);
		addMouseMotionListener(new MouseMotionHandler());
		setSyntax(syntaxDefinition);
		initializeUndo();
	}

	/**
	 * Returns the default editor kit for this text component.
	 */
	public EditorKit createDefaultEditorKit()
	{
		return EDITOR_KIT;
	}

	/**
	 * Set the anti-aliasing flag.
	 */
	public void setAntiAliasing(boolean newValue)
	{
		antiAliasing=newValue;
		repaint();
	}

	/**
	 * Get the anit-aliasing flag.
	 */
	public boolean isAntiAliasing()
	{
		return antiAliasing;
	}

	/**
	 * Set the tab size for the editor.
	 */
	public void setTabSize(int newValue)
	{
		tabSize=newValue;
		revalidate();
		repaint();
	}

	/**
	 * Get the tab size for the editor.
	 */
	public int getTabSize()
	{
		return tabSize;
	}

	/**
	 * A unit is the height of one line of text.
	 */
	public int getScrollableUnitIncrement(Rectangle visibleArea, int orientation, int direction)
	{
		return getLineHeight();
	}

	/**
	 * Sets the document edited by this text area. This method
	 * makes sure that it implements the {@link SourceDocument} interface.
	 *
	 * @param document The document.
	 */
	public void setDocument(Document document)
	{
		if (!(document instanceof SourceDocument))
			throw new IllegalArgumentException("Document is not an instance of SourceDocument");
		invalidate();
		super.setDocument(document);
	}

	/**
	 * Returns the text area's document, typecast to a
	 * {@link SourceDocument}.
	 */
	private SourceDocument getSyntaxDocument()
	{
		return (SourceDocument)getDocument();
	}

	/**
	 * Sets the syntax that is to be used to split lines of
	 * this document up into tokens.
	 *
	 * @param definition The syntax.
	 */
	private void setSyntax(SyntaxDefinition definition)
	{
		getSyntaxDocument().setSyntaxDefinition(definition);
	}

	/**
	 * Returns the syntax that is to be used to split lines
	 * of this document up into tokens.
	 *
	 * @return The syntax.
	 */
	public SyntaxDefinition getSyntax()
	{
		return getSyntaxDocument().getSyntaxDefinition();
	}

	public void setStyleSchema(StyleSchema styleSchema)
	{
		this.styleSchema=styleSchema;
	}

	public StyleSchema getStyleSchema()
	{
		if (styleSchema==null) styleSchema=new StyleSchema();
		return styleSchema;
	}

	private int getLineHeight()
	{
		return getFontMetrics(getFont()).getHeight();
	}

	/**
	 * Paint the component.
	 */
	public void paint(Graphics graphics)
	{
		Graphics2D graphics2D=(Graphics2D)graphics;
		if (antiAliasing)
			graphics2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		super.paint(graphics);
	}

	/**
	 * Set the foreground color.
	 */
	public void setForeground(Color color)
	{
		super.setForeground(color);
		Style style=getStyleSchema().getStyle(Style.DEFAULT);
		if (style!=null) style.setForeground(color);
	}

	/**
	 * Set the background color.
	 */
	public void setBackground(Color color)
	{
		super.setBackground(color);
		Style style=getStyleSchema().getStyle(Style.DEFAULT);
		if (style!=null) style.setBackground(color);
	}

	/**
	 * Scroll to the specified offset.
	 */
	public void scrollToOffset(int offset)
	{
		if (offset<0 || offset>getDocument().getLength()) return;

		Container parent=getParent();
		while (parent!=null && !(parent instanceof JViewport)) parent=parent.getParent();
		if (parent==null) return;

		try
		{
			Rectangle viewArea=((JViewport)parent).getViewRect();
			Rectangle lineArea=modelToView(offset);
			int nettoHeight=viewArea.height-lineArea.height;
			int topHeight=(int)(nettoHeight*0.3);
			scrollRectToVisible(new Rectangle(0, lineArea.y-topHeight, viewArea.width, viewArea.height));
		}
		catch (BadLocationException e)
		{
			e.printStackTrace();
			return;
		}
	}

	private void initializeUndo()
	{
		UndoManager undo=new UndoManager();
		getDocument().addUndoableEditListener(new MyUndoableEditListener(undo));

		InputMap inputMap=getInputMap();
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, KeyEvent.CTRL_DOWN_MASK), "undo");
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, KeyEvent.CTRL_DOWN_MASK|KeyEvent.SHIFT_DOWN_MASK), "redo");

		ActionMap actionMap=getActionMap();
		actionMap.put("undo", new UndoAction(undo));
		actionMap.put("redo", new RedoAction(undo));
	}

	/**
	 * A mouse listener to set the cursor back to a text cursor.
	 */
	private static class MouseMotionHandler extends MouseMotionAdapter
	{
		public void mouseMoved(MouseEvent aoEvent)
		{
			JTextComponent component=(JTextComponent)aoEvent.getSource();
			if (component.getCursor()!=TEXT_CURSOR) component.setCursor(TEXT_CURSOR);
		}

	}

	private static class UndoAction extends AbstractAction
	{
		private final UndoManager undo;

		public UndoAction(UndoManager undo)
		{
			super("Undo");
			this.undo=undo;
		}

		public void actionPerformed(ActionEvent evt)
		{
			try
			{
				if (undo.canUndo())
				{
					undo.undo();
				}
			}
			catch (CannotUndoException e)
			{
			}
		}
	}

	private static class RedoAction extends AbstractAction
	{
		private final UndoManager undo;

		public RedoAction(UndoManager undo)
		{
			super("Redo");
			this.undo=undo;
		}

		public void actionPerformed(ActionEvent evt)
		{
			try
			{
				if (undo.canRedo())
				{
					undo.redo();
				}
			}
			catch (CannotRedoException e)
			{
			}
		}
	}

	private static class MyUndoableEditListener implements UndoableEditListener
	{
		private final UndoManager undo;

		public MyUndoableEditListener(UndoManager undo)
		{
			this.undo=undo;
		}

		public void undoableEditHappened(UndoableEditEvent evt)
		{
			undo.addEdit(evt.getEdit());
		}
	}
}

