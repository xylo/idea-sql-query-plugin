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

import javax.swing.event.DocumentEvent;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.PlainDocument;
import javax.swing.text.Segment;

/**
 * A document implementation that can be tokenized by the syntax highlighting
 * system.
 *
 * @author Mark Soderquist
 * @author Stefan Stiller
 * @version $Revision: 1.3 $, $Date: 2006/03/24 18:50:50 $
 */
public class SourceDocument extends PlainDocument
{
	private SyntaxDefinition syntaxDefinition;
	private LineContextManager contextManager;

	/**
	 * Create a new <code>SyntaxDocument</code>.
	 */
	public SourceDocument()
	{
		contextManager=new LineContextManager();
		syntaxDefinition=SyntaxDefinitionFactory.getInstance().getSyntaxDefinition("text");
	}

	/**
	 * Sets the syntax that is used to split lines
	 * of this document into tokens.
	 *
	 * @param definition The syntax.
	 */
	public void setSyntaxDefinition(SyntaxDefinition definition)
	{
		if (definition==null)
			syntaxDefinition=SyntaxDefinitionFactory.getInstance().getSyntaxDefinition("text");
		else
			syntaxDefinition=definition;
	}

	/**
	 * Gets the syntax that is used to split lines
	 * of this document into tokens.
	 *
	 * @return The syntax.
	 */
	public SyntaxDefinition getSyntaxDefinition()
	{
		return syntaxDefinition;
	}

	/**
	 * Get the number of lines in the document.
	 */
	public int getLineCount()
	{
		return getDefaultRootElement().getElementCount();
	}

//	/**
//	 * Get the line index for the specified offset.
//	 */
//	public int getLineIndexForOffset(int offset)
//	{
//		return getDefaultRootElement().getElementIndex(offset);
//	}

	/**
	 * Get the start offset for the specified line index.
	 */
	public int getStartOffsetForLineIndex(int index)
	{
		if (index<0 || index>=getDefaultRootElement().getElementCount()) return -1;
		Element element=getDefaultRootElement().getElement(index);
		return element==null ? -1 : element.getStartOffset();
	}

	/**
	 * Get the end offset for the specified line index.
	 */
	public int getEndOffsetForLineIndex(int index)
	{
		if (index<0 || index>=getDefaultRootElement().getElementCount()) return -1;
		Element element=getDefaultRootElement().getElement(index);
		return element==null ? -1 : element.getEndOffset();
	}

	/**
	 * Get line text.
	 */
	private void getLineSegment(int lineIndex, Segment segment)
	{
		try
		{
			Element element=getDefaultRootElement().getElement(lineIndex);
			if (element==null) return;
			int start=element.getStartOffset();
			int end=element.getEndOffset();
			getText(start, end-(start+1), segment);
		}
		catch (BadLocationException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Returns the syntax tokens for the specified line.
	 *
	 * @param index The line number.
	 */
	public LineContext getLineContext(int index)
	{
		if (index<0 || index>=contextManager.getLineCount()) throw new ArrayIndexOutOfBoundsException(index);
		// If cached tokens are valid return them.
		if (contextManager.isValid(index)) return contextManager.getLineContext(index);
		return markTokens(index);
	}

	/**
	 * Marks the tokens for the specified line.
	 *
	 * @param lineIndex The line number.
	 * @return The syntax context for the line.
	 */
	private LineContext markTokens(int lineIndex)
	{
		// Check for index out of bounds.
		if (lineIndex<0 || lineIndex>=contextManager.getLineCount()) throw new ArrayIndexOutOfBoundsException(lineIndex);

		// Find a previous line with a valid line context.
		int start=0;
		for (int i=lineIndex-1; i>0; i--)
		{
			if (contextManager.isValid(i))
			{
				start=i+1;
				break;
			}
		}

		Segment segment=new Segment();

		// Mark the tokens for each line that needs marking.
		for (int i=start; i<=lineIndex; i++)
		{
			// Store the old context.
			LineContext oldContext=contextManager.getLineContext(i);

			// Create a new context.
			LineContext context=new LineContext(syntaxDefinition.getSyntaxParser().getDefaultSyntaxRules());

			LineContext prevContext=i==0 ? null : contextManager.getLineContext(i-1);
			if (prevContext!=null)
			{
				context.setParentContext(prevContext.getParentContext());
				context.setCurrentRule(prevContext.getCurrentRule());
				context.setSyntaxRules(prevContext.getSyntaxRules());
			}

			getLineSegment(i, segment);

			LineContext newContext=syntaxDefinition.getSyntaxParser().markTokens(segment, context);
			contextManager.setLineContext(i, newContext);

			// Set the next line requested flag.
			SyntaxRule oldRule=oldContext.getCurrentRule();
			SyntaxRule newRule=newContext.getCurrentRule();
			if (oldRule!=newRule)
			{
				newContext.setNextLineRequested(true);
				newContext.setNextLineUnpainted(true);
			}
			else if (oldContext.getSyntaxRules()!=newContext.getSyntaxRules())
			{
				newContext.setNextLineRequested(true);
				newContext.setNextLineUnpainted(true);
			}

			// The last line should never have the next line unpainted.
			if (i>= contextManager.getLineCount()-1)
			{
				newContext.setNextLineRequested(false);
				newContext.setNextLineUnpainted(false);
			}
		}

		// Invalidate requested next lines.
		if (lineIndex<contextManager.getLineCount()-1)
		{
			contextManager.invalidateNextLinesRequested(lineIndex);
		}

		return contextManager.getLineContext(lineIndex);
	}

	/**
	 * We overwrite this method to update the token marker
	 * state immediately so that any event listeners get a
	 * consistent token marker.
	 */
	protected void fireInsertUpdate(DocumentEvent event)
	{
		if (syntaxDefinition!=null && syntaxDefinition.getSyntaxParser()!=null)
		{
			DocumentEvent.ElementChange change=event.getChange(getDefaultRootElement());
			if (change!=null)
			{
				int index=change.getIndex();
				int length=change.getChildrenAdded().length-change.getChildrenRemoved().length;
				contextManager.linesChanged(index, contextManager.getLineCount()-index);
				contextManager.insertLines(change.getIndex()+1, length);
			}
			else
			{
				int index=getDefaultRootElement().getElementIndex(event.getOffset());
				contextManager.linesChanged(index, 1);
			}
		}
		super.fireInsertUpdate(event);
	}

	/**
	 * We overwrite this method to update the token marker
	 * state immediately so that any event listeners get a
	 * consistent token marker.
	 */
	protected void fireRemoveUpdate(DocumentEvent event)
	{
		if (syntaxDefinition!=null && syntaxDefinition.getSyntaxParser()!=null)
		{
			DocumentEvent.ElementChange change=event.getChange(getDefaultRootElement());
			if (change!=null)
			{
				int index=change.getIndex();
				int length=change.getChildrenRemoved().length-change.getChildrenAdded().length;
				contextManager.linesChanged(index, contextManager.getLineCount()-index);
				contextManager.deleteLines(index+1, length);
			}
			else
			{
				int index=getDefaultRootElement().getElementIndex(event.getOffset());
				contextManager.linesChanged(index, 1);
			}
		}
		super.fireRemoveUpdate(event);
	}

	private class LineContextManager
	{
		private int lineCount;
		private LineContext[] contexts;

		/**
		 * Construct a LineContextManager.  Even documents that are just plain text
		 * will need a LineContextManager.  Empty documents need at least one line.
		 */
		public LineContextManager()
		{
			setLineCount(1);
			ensureCapacity(0);
			contexts[0]=new LineContext();
		}

		private void setLineContext(int index, LineContext context)
		{
			contexts[index]=context;
		}

		private LineContext getLineContext(int index)
		{
			return contexts[index];
		}

		private boolean isValid(int index)
		{
			return contexts[index].getTokenList()!=null;
		}

		public void invalidate(int start, int end)
		{
			for (int i=start; i<end; i++) contexts[i].setTokenList(null);
		}

		/**
		 * Invalidate the next lines if the next lines requested flag is set.  The
		 * last line in a document should never have the mbNextLineRequested flag
		 * set to true, there is no next line.
		 *
		 * @param index The line index to check first.
		 */
		private void invalidateNextLinesRequested(int index)
		{
			for (int i=index; contexts[i].isNextLineRequested(); i++)
			{
				contexts[i+1].setTokenList(null);
			}
		}

		/**
		 * Informs the token marker that lines have been inserted into
		 * the document. This inserts a gap in the <code>maContexts</code>
		 * array.
		 *
		 * @param index The first line number.
		 * @param count The number of lines.
		 */
		private void insertLines(int index, int count)
		{
			if (count<=0) return;
			lineCount=lineCount+count;
			ensureCapacity(lineCount);
			int length=index+count;
			System.arraycopy(contexts, index, contexts, length, contexts.length-length);
			SyntaxRules rules=syntaxDefinition.getSyntaxParser().getDefaultSyntaxRules();
			for (int i=index+count-1; i>=index; i--)
			{
				contexts[i]=new LineContext(rules);
			}
		}

		/**
		 * Informs the token marker that line have been deleted from
		 * the document. This removes the lines in question from the
		 * <code>maContexts</code> array.
		 *
		 * @param index The first line number.
		 * @param count The number of lines.
		 */
		private void deleteLines(int index, int count)
		{
			if (count<=0) return;
			int length=index+count;
			lineCount=lineCount-count;
			System.arraycopy(contexts, length, contexts, index, contexts.length-length);
		}

		/**
		 * Informs the token marker that lines have changed.
		 * This will invalidate any cached contexts.
		 *
		 * @param index The first line number.
		 * @param count The number of lines.
		 */
		private void linesChanged(int index, int count)
		{
			invalidate(index, index+count);
		}

		/**
		 * Effectively double the size of the context.  Because not all
		 * the elements in the array are used the <code>miLineCount</code>
		 * attribute is used to track the number of lines.
		 */
		private void ensureCapacity(int index)
		{
			if (contexts==null)
			{
				contexts=new LineContext[index+1];
			}
			else if (contexts.length<=index)
			{
				LineContext[] newContexts=new LineContext[(index+1)*2];
				System.arraycopy(contexts, 0, newContexts, 0, contexts.length);
				contexts=newContexts;
			}
		}

		public int getLineCount()
		{
			return lineCount;
		}

		public void setLineCount(int newValue)
		{
			this.lineCount=newValue;
		}
	}

}