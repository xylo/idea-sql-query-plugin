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

import java.awt.Container;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import javax.swing.UIManager;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.PlainView;
import javax.swing.text.Segment;
import javax.swing.text.TabExpander;

/**
 * @author Mark Soderquist
 * @author Stefan Stiller
 * @version $Revision: 1.3 $, $Date: 2006/03/24 18:50:50 $
 */
public class SourceView extends PlainView
{
	/**
	 * The font currently used by the container. This is cached for comparison later.
	 */
	private Font currentFont;

	/**
	 * The font metrics of the currently used font.
	 */
	private FontMetrics fontMetrics;

	/**
	 * The derived versions of the container font.  These are cached for performance.
	 * They must be updated when the container changes font.  Usually done in the
	 * <code>updateMetrics()</code> method.
	 */
	private Font[] fonts;

	private Segment segment;

	private int tabSpace;

	/**
	 * The selection syntax style.
	 */
	private Style style;

	/**
	 * Construct a new <code>SyntaxView</code> for painting the specified element.
	 *
	 * @param element The element.
	 */
	public SourceView(Element element)
	{
		super(element);
		fonts=new Font[4];
		segment=new Segment();
		style=new Style();
	}

	/**
	 * Draw the specified line.
	 *
	 * @param lineIndex The line index.
	 * @param graphics The graphics context.
	 * @param x The x coordinate where the line should be painted.
	 * @param baseLine The text baseline where the line should be painted.
	 */
	public void drawLine(int lineIndex, Graphics graphics, int x, int baseLine)
	{
		SourceTextPane textPane=(SourceTextPane)getContainer();
		SourceDocument document=(SourceDocument)getDocument();
		StyleSchema styleSchema=textPane.getStyleSchema();
		LineContext context=document.getLineContext(lineIndex);
		Token token=context.getTokenList().getFirstToken();

		int selectionStart=textPane.getSelectionStart();
		int selectionEnd=textPane.getSelectionEnd();
		boolean selection=selectionStart<selectionEnd && textPane.getCaret().isSelectionVisible();

		// Load the line text into the segment.
		int start=document.getStartOffsetForLineIndex(lineIndex);
		int end=document.getEndOffsetForLineIndex(lineIndex);
		try
		{
			document.getText(start, end-(start+1), segment);
		}
		catch (BadLocationException e)
		{
			e.printStackTrace();
			return;
		}

		byte styleId=token.getStyle();
		int offset=0;
		int tokenLength=token.getLength();

		int fontHeight=fontMetrics.getHeight();
		Rectangle bounds=new Rectangle(x, lineIndex*fontHeight+fontMetrics.getMaxDescent(), textPane.getSize().width-x, fontHeight);

		// Paint the tokens in the line.
		int tokenStart;
		int tokenEnd;
		while (styleId!=Style.END)
		{
			int segmentOffset=segment.offset;
			segment.count=tokenLength;
			Style style=styleSchema.getStyle(styleId);

			// Create the selection style based on the current style.
			if (selection)
			{
				this.style.setForeground(textPane.getSelectedTextColor());
				this.style.setFontStyle(style.getFontStyle());
			}

			// Calculate the token start and end offsets.
			tokenStart=start+offset;
			tokenEnd=tokenStart+tokenLength;

			// Paint the token.
			if (!selection || selectionStart>=tokenEnd || selectionEnd<=tokenStart)
			{
				bounds.x=drawText(segment, graphics, this, style, bounds, baseLine);
			}
			else
			{
				if (selectionStart<=tokenStart&selectionEnd>=tokenEnd)
				{
					bounds.x=drawText(segment, graphics, this, this.style, bounds, baseLine);
				}
				else if (selectionStart>tokenStart&selectionEnd<tokenEnd)
				{
					// Draw first part normal.
					segment.count=selectionStart-start-offset;
					bounds.x=drawText(segment, graphics, this, style, bounds, baseLine);
					// Draw second part selected.
					segment.offset+=segment.count;
					segment.count=selectionEnd-selectionStart;
					bounds.x=drawText(segment, graphics, this, this.style, bounds, baseLine);
					// Draw last part normal.
					segment.offset+=segment.count;
					segment.count=start+offset+tokenLength-selectionEnd;
					bounds.x=drawText(segment, graphics, this, style, bounds, baseLine);
					segment.offset=segmentOffset;
				}
				else if (selectionStart>tokenStart&selectionStart<tokenEnd)
				{
					// Draw first part normal.
					segment.count=selectionStart-start-offset;
					bounds.x=drawText(segment, graphics, this, style, bounds, baseLine);
					// Draw last part selected.
					segment.offset+=segment.count;
					segment.count=tokenLength-segment.count;
					bounds.x=drawText(segment, graphics, this, this.style, bounds, baseLine);
					segment.offset=segmentOffset;
				}
				else if (selectionEnd>tokenStart&selectionEnd<tokenEnd)
				{
					// Draw first part selected.
					segment.count=selectionEnd-start-offset;
					bounds.x=drawText(segment, graphics, this, this.style, bounds, baseLine);
					// Draw last part normal.
					segment.offset+=segment.count;
					segment.count=tokenLength-segment.count;
					bounds.x=drawText(segment, graphics, this, style, bounds, baseLine);
					segment.offset=segmentOffset;
				}
			}

			// Advance to the next token.
			offset+=tokenLength;
			segment.offset+=tokenLength;

			token=token.getNextToken();
			styleId=token.getStyle();
			tokenLength=token.getLength();
		}

		if (context.isNextLineRequested() && context.isNextLineUnpainted())
		{
			context.setNextLineRequested(false);
			context.setNextLineUnpainted(false);
			forceRepaint(lineIndex+1);
		}
	}

	/**
	 * Custom implementation of the </code>drawTabbedText</code> method
	 * to account for differences in font style glyph widths.
	 */
	private int drawText(Segment segment, Graphics graphics, TabExpander tabExpander, Style style, Rectangle bounds, int baseLine)
	{
		int x=bounds.x;
		int nextX=x;
		char[] text=segment.array;
		int offset=segment.offset;
		int count=segment.offset+segment.count;
		int charWidth;
		for (int i=offset; i<count; i++)
		{
			switch (text[i])
			{
				case '\t':
					x=nextX;
					nextX=(int)tabExpander.nextTabStop(nextX, i-offset);
					charWidth=nextX-x;
					graphics.setColor(style.getBackground());
					graphics.fillRect(x, bounds.y, charWidth, bounds.height);
					break;
				case '\n':
					charWidth=0;
					nextX+=charWidth;
					break;
				default :
					charWidth=fontMetrics.charWidth(text[i]);
					// Draw the background.
					graphics.setColor(style.getBackground());
					graphics.fillRect(nextX, bounds.y, charWidth, bounds.height);
					// Draw the foreground.
					graphics.setColor(style.getForeground());
					graphics.setFont(fonts[style.getFontStyle()]);
					graphics.drawString(new String(text, i, 1), nextX, baseLine);
					nextX+=charWidth;
			}
		}
		return nextX;
	}

	/**
	 * Force a range of lines to be repainted.
	 */
	private void forceRepaint(int lineIndex)
	{
		int fontHeight=fontMetrics.getHeight();
		Container container=getContainer();
		container.repaint(0, 0, lineIndex*fontHeight, container.getSize().width, fontHeight);
	}

	/**
	 * Get the next tab stop.
	 */
	public float nextTabStop(float x, int offset)
	{
		int tabSize=((SourceTextPane)getContainer()).getTabSize();
		int tabWidth=tabSpace*tabSize;
		if (tabWidth==0) return x;
		int pad=tabWidth-((int)x+tabWidth)%tabWidth;
		return x+pad;
	}

	/**
	 * Override <code>updateMetrics()</code>.
	 */
	protected void updateMetrics()
	{
		super.updateMetrics();
		Font font=UIManager.getFont("EditorPane.font");
		if (currentFont!=font)
		{
			// Set the new font.
			currentFont=font;
			fontMetrics=metrics;
			tabSpace=fontMetrics.charWidth('m');

			// Derive new font styles.
			fonts[Font.PLAIN]=font.deriveFont(Font.PLAIN);
			fonts[Font.BOLD]=font.deriveFont(Font.BOLD);
			fonts[Font.ITALIC]=font.deriveFont(Font.ITALIC);
			fonts[Font.BOLD|Font.ITALIC]=font.deriveFont(Font.BOLD|Font.ITALIC);
		}
	}

}