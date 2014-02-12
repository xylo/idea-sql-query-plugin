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

import java.util.Hashtable;
import javax.swing.text.Segment;

/**
 * A token marker splits lines of text into tokens. Each token carries
 * a length field and an identification tag that can be mapped to a color
 * or font style for painting that token.
 *
 * @author Mike Dillon
 * @author Slava Pestov
 * @author Mark Soderquist
 * @author Stefan Stiller
 * @version $Revision: 1.3 $, $Date: 2006/03/24 18:02:55 $
 * @see com.kiwisoft.utils.text.Token
 */
public class SyntaxParser
{
	private static final int MAJOR_TYPES=0x000000FF;

	public static final int WHITESPACE=1;
	public static final int SPAN=2;
	public static final int MARK_PREVIOUS=4;
	public static final int MARK_FOLLOWING=8;
	public static final int EOL_SPAN=16;

	public static final int EXCLUDE_MATCH=1<<8;
	public static final int AT_LINE_START=1<<9;
	public static final int NO_LINE_BREAK=1<<10;
	public static final int NO_WORD_BREAK=1<<11;
	public static final int IS_ESCAPE=1<<12;
	public static final int DELEGATE=1<<13;

	private static final int SOFT_SPAN=MARK_FOLLOWING|NO_WORD_BREAK;

	private String rulePrefix;
	private Hashtable rulesMap=new Hashtable(64);
	private SyntaxRules defaultRules;

	private LineContext context;
	private Segment pattern=new Segment();
	private int lastOffset;
	private int lastKeyword;
	private int lineLength;
	private int position;
	private boolean escaped;

	public void addRules(String name, SyntaxRules rules)
	{
		if (rules==null) return;
		if (name==null) name="MAIN";
		this.rulesMap.put(rulePrefix.concat(name), rules);
		if ("MAIN".equals(name)) defaultRules=rules;
	}

	public SyntaxRules getDefaultSyntaxRules()
	{
		return defaultRules;
	}

	private SyntaxRules getSyntaxRules(String name)
	{
		SyntaxRules rules=(SyntaxRules)this.rulesMap.get(name);
		if (rules==null && !name.startsWith(rulePrefix))
		{
			int delimiter=name.indexOf("::");
			String defName=name.substring(0, delimiter);
			SyntaxDefinition definition=SyntaxDefinitionFactory.getInstance().getSyntaxDefinition(defName);
			if (definition==null)
			{
				System.err.println("Unknown syntax definition: "+defName);
				rules=null;
			}
			else
			{
				SyntaxParser parser=definition.getSyntaxParser();
				rules=parser.getSyntaxRules(name);
				this.rulesMap.put(name, rules);
			}
		}
		if (rules==null) System.err.println("Unresolved delegate target: "+name);
		return rules;
	}

	public void setName(String name)
	{
		if (name==null) throw new NullPointerException();
		rulePrefix=name.concat("::");
	}

	/**
	 * Do not call this method directly. This class is not thread safe.
	 * Call SyntaxDocument.markTokens() instead.
	 * @param context The new <code>SyntaxLineContext</code> of the line to mark.
	 */
	public LineContext markTokens(Segment segment, LineContext context)
	{
		this.context=context;
		TokenList tokenList=new TokenList();

		lastOffset=lastKeyword=segment.offset;
		lineLength=segment.count+segment.offset;

		int terminateChar=this.context.getSyntaxRules().getTerminateChar();
		int searchLimit=terminateChar>=0 && terminateChar<segment.count ? segment.offset+terminateChar : lineLength;

		escaped=false;

		boolean keepGoing;
		boolean tempEscaped;
		Segment tempPattern;
		SyntaxRule rule;
		LineContext tempContext;

		for (position=segment.offset; position<searchLimit; position++)
		{
			// If we are not in the top level context, we are delegated
			if (this.context.getParentContext()!=null)
			{
				tempContext=this.context;
				this.context=this.context.getParentContext();

				pattern.array=this.context.getCurrentRule().getCharacters();
				pattern.count=this.context.getCurrentRule().getLength(1);
				pattern.offset=this.context.getCurrentRule().getOffset(1);

				keepGoing=handleRule(tokenList, segment, this.context.getCurrentRule());

				this.context=tempContext;

				if (!keepGoing)
				{
					if (escaped)
					{
						escaped=false;
					}
					else
					{
						if (position!=lastOffset)
						{
							if (this.context.getCurrentRule()==null)
							{
								markKeyword(tokenList, segment, lastKeyword, position);
								tokenList.addToken(position-lastOffset, this.context.getSyntaxRules().getDefault(), this.context.getSyntaxRules());
							}
							else if ((this.context.getCurrentRule().getType()&(NO_LINE_BREAK|NO_WORD_BREAK))==0)
							{
								tokenList.addToken(position-lastOffset, this.context.getCurrentRule().getStyle(), this.context.getSyntaxRules());
							}
							else
							{
								tokenList.addToken(position-lastOffset, Style.INVALID, this.context.getSyntaxRules());
							}
						}

						this.context=(LineContext)this.context.getParentContext().clone();

						if ((this.context.getCurrentRule().getType()&EXCLUDE_MATCH)==EXCLUDE_MATCH)
						{
							tokenList.addToken(pattern.count, this.context.getSyntaxRules().getDefault(), this.context.getSyntaxRules());
						}
						else
						{
							tokenList.addToken(pattern.count, this.context.getCurrentRule().getStyle(), this.context.getSyntaxRules());
						}

						this.context.setCurrentRule(null);

						lastKeyword=lastOffset=position+pattern.count;
					}

					// Move postion to last character of match sequence.
					position+=pattern.count-1;
					continue;
				}
			}

			// Check the escape rule for the current context.
			if ((rule=this.context.getSyntaxRules().getEscapeRule())!=null)
			{
				// Assign tempPattern to mutable "buffer" moPattern.
				tempPattern=pattern;

				// Swap in the escape moPattern.
				pattern=this.context.getSyntaxRules().getEscapePattern();

				tempEscaped=escaped;

				keepGoing=handleRule(tokenList, segment, rule);

				// Swap back the buffer moPattern.
				pattern=tempPattern;

				if (!keepGoing)
				{
					if (tempEscaped) escaped=false;
					continue;
				}
			}

			// If we are inside a span, check for its end sequence.
			rule=this.context.getCurrentRule();
			if (rule!=null && (rule.getType()&SPAN)==SPAN)
			{
				pattern.array=rule.getCharacters();
				pattern.count=rule.getLength(1);
				pattern.offset=rule.getOffset(1);

				// If we match the end of the span, or if this is a "hard" span,
				// continue to the next character; otherwise, check all
				// applicable rulesMap below.
				if (!handleRule(tokenList, segment, rule) || (rule.getType()&SOFT_SPAN)==0)
				{
					escaped=false;
					continue;
				}
			}

			// Now check every rule.
			rule=this.context.getSyntaxRules().getRules(segment.array[position]);
			while (rule!=null)
			{
				pattern.array=rule.getCharacters();

				if (this.context.getCurrentRule()==rule && (rule.getType()&SPAN)==SPAN)
				{
					pattern.count=rule.getLength(1);
					pattern.offset=rule.getOffset(1);
				}
				else
				{
					pattern.count=rule.getLength(0);
					pattern.offset=0;
				}

				// Stop checking rulesMap if there was a match and go to next position.
				if (!handleRule(tokenList, segment, rule)) break;

				rule=rule.getNextRule();
			}

			escaped=false;
		}
		// Done scanning line for tokens.

		// Check for keywords at the end of the line.
		if (this.context.getCurrentRule()==null) markKeyword(tokenList, segment, lastKeyword, lineLength);

		// Mark all remaining characters.
		if (lastOffset!=lineLength)
		{
			if (this.context.getCurrentRule()==null)
			{
				tokenList.addToken(
				    lineLength-lastOffset,
				    this.context.getSyntaxRules().getDefault(),
				    this.context.getSyntaxRules()
				);
			}
			else if ((this.context.getCurrentRule().getType()&SPAN)==SPAN &&
			    (this.context.getCurrentRule().getType()&(NO_LINE_BREAK|NO_WORD_BREAK))!=0)
			{
				tokenList.addToken(lineLength-lastOffset, Style.INVALID, this.context.getSyntaxRules());
				this.context.setCurrentRule(null);
			}
			else
			{
				tokenList.addToken(lineLength-lastOffset, this.context.getCurrentRule().getStyle(), this.context.getSyntaxRules());
				if ((this.context.getCurrentRule().getType()&MARK_FOLLOWING)==MARK_FOLLOWING)
				{
					this.context.setCurrentRule(null);
				}
			}
		}
		tokenList.addToken(0, Style.END, this.context.getSyntaxRules());
		this.context.setTokenList(tokenList);
		return this.context;
	}

	/**
	 * Checks if the rule matches the line at the current position
	 * and handles the rule if so.
	 *
	 * @param tokenList List of tokens in line.
	 * @param segment Segment to check rule against
	 * @param rule ParserRule to check against line
	 * @return true: Keep checking other rulesMap. <br>false: Stop checking other rulesMap.
	 */
	private boolean handleRule(TokenList tokenList, Segment segment, SyntaxRule rule)
	{
		if (pattern.count==0) return true;

		if (lineLength-position<pattern.count) return true;

		for (int i=0; i<pattern.count; i++)
		{
			char ch1=pattern.array[pattern.offset+i];
			char ch2=segment.array[position+i];

			// Break out and check the next rule if there is a mismatch.
			if (!(ch1==ch2 || context.getSyntaxRules().getIgnoreCase()
			    && (Character.toLowerCase(ch1)==ch2 || ch1==Character.toLowerCase(ch2))))
				return true;
		}

		if (escaped)
		{
			position+=pattern.count-1;
			return false;
		}
		else if ((rule.getType()&IS_ESCAPE)==IS_ESCAPE)
		{
			escaped=true;
			position+=pattern.count-1;
			return false;
		}

		//{{{ handle soft spans
		if (context.getCurrentRule()!=null && context.getCurrentRule()!=rule && (context.getCurrentRule().getType()&SOFT_SPAN)!=0)
		{
			if ((context.getCurrentRule().getType()&NO_WORD_BREAK)==NO_WORD_BREAK)
			{
				tokenList.addToken(position-lastOffset, Style.INVALID, context.getSyntaxRules());
			}
			else
			{
				tokenList.addToken(position-lastOffset, context.getCurrentRule().getStyle(), context.getSyntaxRules());
			}
			lastOffset=lastKeyword=position;
			context.setCurrentRule(null);
		}

		// Not inside a rule.
		if (context.getCurrentRule()==null)
		{
			//Log.write( Log.CONFIG, "Not inside a rule..." );
			if ((rule.getType()&AT_LINE_START)==AT_LINE_START)
			{
				if (((rule.getType()&MARK_PREVIOUS)!=0 ? lastKeyword : position)!=segment.offset)
				{
					return true;
				}
			}

			markKeyword(tokenList, segment, lastKeyword, position);

			if ((rule.getType()&MARK_PREVIOUS)!=MARK_PREVIOUS)
			{
				lastKeyword=position+pattern.count;
				if (rule.isWhiteSpace()) return false;
				// Mark previous sequence as NULL (plain text).
				if (lastOffset<position) tokenList.addToken(position-lastOffset, context.getSyntaxRules().getDefault(), context.getSyntaxRules());
			}

			switch (rule.getType()&MAJOR_TYPES)
			{
				case 0:
					// This is a plain sequence rule.
					tokenList.addToken(pattern.count, rule.getStyle(), context.getSyntaxRules());
					lastOffset=position+pattern.count;
					break;
				case SPAN:
					markSpan(tokenList, rule);
					break;
				case EOL_SPAN:
					markEOLSpan(tokenList, rule);
					return false;
				case MARK_PREVIOUS:
					markPrevious(tokenList, rule);
					break;
				case MARK_FOLLOWING:
					markFollowing(tokenList, rule);
					break;
				default:
					throw new InternalError("Unhandled major action");
			}

			lastKeyword=lastOffset;

			// Move position to last character of match sequence.
			position+=pattern.count-1;

			// Break out of inner for loop to check next char.
			return false;
		}
		else if ((rule.getType()&SPAN)==SPAN)
		{
			if (rule.isNotDelegated())
			{
				context.setCurrentRule(null);
				//Log.write( Log.CONFIG, "Using delegate: " + new String( aoCheckRule.maSearchChars ) );
				if ((rule.getType()&EXCLUDE_MATCH)==EXCLUDE_MATCH)
				{
					tokenList.addToken(position-lastOffset, rule.getStyle(), context.getSyntaxRules());
					tokenList.addToken(pattern.count, context.getSyntaxRules().getDefault(), context.getSyntaxRules());
				}
				else
				{
					//Log.write( Log.CONFIG, "Adding token: " + aoCheckRule.myToken );
					tokenList.addToken(position+pattern.count-lastOffset, rule.getStyle(), context.getSyntaxRules());
				}

				lastKeyword=lastOffset=position+pattern.count;

				// Move position to last character of match sequence.
				position+=pattern.count-1;
			}

			// Break out of inner for loop to check next char.
			return false;
		}

		return true;
	}

	private void markSpan(TokenList tokenList, SyntaxRule rule)
	{
		context.setCurrentRule(rule);

		// Non-delegated.
		if (rule.isNotDelegated())
		{
			if ((rule.getType()&EXCLUDE_MATCH)==EXCLUDE_MATCH)
			{
				tokenList.addToken(pattern.count, context.getSyntaxRules().getDefault(), context.getSyntaxRules());
				lastOffset=position+pattern.count;
			}
			else
			{
				lastOffset=position;
			}
		}
		else
		{
			String sName=new String(rule.getCharacters(), rule.getOffset(2), rule.getLength(2));

			SyntaxRules oDelegateSet=getSyntaxRules(sName);

			if (oDelegateSet!=null)
			{
				if ((rule.getType()&EXCLUDE_MATCH)==EXCLUDE_MATCH)
				{
					tokenList.addToken(pattern.count, context.getSyntaxRules().getDefault(), context.getSyntaxRules());
				}
				else
				{
					tokenList.addToken(pattern.count, rule.getStyle(), context.getSyntaxRules());
				}
				lastOffset=position+pattern.count;

				context=new LineContext(oDelegateSet, context);
			}
		}
	}

	private void markEOLSpan(TokenList tokenList, SyntaxRule rule)
	{
		if ((rule.getType()&EXCLUDE_MATCH)==EXCLUDE_MATCH)
		{
			tokenList.addToken(pattern.count, context.getSyntaxRules().getDefault(), context.getSyntaxRules());
			tokenList.addToken(lineLength-(position+pattern.count), rule.getStyle(), context.getSyntaxRules());
		}
		else
		{
			tokenList.addToken(lineLength-position, rule.getStyle(), context.getSyntaxRules());
		}
		lastOffset=lineLength;
		lastKeyword=lineLength;
		position=lineLength;
	}

	private void markFollowing(TokenList tokenList, SyntaxRule rule)
	{
		context.setCurrentRule(rule);
		if ((rule.getType()&EXCLUDE_MATCH)==EXCLUDE_MATCH)
		{
			tokenList.addToken(pattern.count, context.getSyntaxRules().getDefault(), context.getSyntaxRules());
			lastOffset=position+pattern.count;
		}
		else
		{
			lastOffset=position;
		}
	}

	private void markPrevious(TokenList tokenList, SyntaxRule rule)
	{
		if (lastKeyword>lastOffset)
		{
			tokenList.addToken(lastKeyword-lastOffset, context.getSyntaxRules().getDefault(), context.getSyntaxRules());
			lastOffset=lastKeyword;
		}

		if ((rule.getType()&EXCLUDE_MATCH)==EXCLUDE_MATCH)
		{
			tokenList.addToken(position-lastOffset, rule.getStyle(), context.getSyntaxRules());
			tokenList.addToken(pattern.count, context.getSyntaxRules().getDefault(), context.getSyntaxRules());
		}
		else
		{
			tokenList.addToken(position-lastOffset+pattern.count, rule.getStyle(), context.getSyntaxRules());
		}

		lastOffset=position+pattern.count;
	}

	private void markKeyword(TokenList tokenList, Segment segment, int start, int end)
	{
		KeywordMap keywordMap=context.getSyntaxRules().getKeywords();

		// Do digits.

		// Right now, this is hardcoded to handle these cases:
		//   1234
		//   0x1234abcf
		//   1234l
		//   12.34f
		//   12.34d
		// In the future, we need some sort of regexp mechanism.

		int length=end-start;
		if (context.getSyntaxRules().getHighlightDigits())
		{
			char[] chars=segment.array;
			boolean digit=true;
			boolean octal=false;
			boolean hex=false;
			boolean seenSomeDigits=false;

			for (int i=0; i<length; i++)
			{
				switch (chars[start+i])
				{
					case '0':
						if (i==0) octal=true;
						seenSomeDigits=true;
						continue;
					case '1':
					case '2':
					case '3':
					case '4':
					case '5':
					case '6':
					case '7':
					case '8':
					case '9':
						seenSomeDigits=true;
						continue;
					case 'x':
					case 'X':
						if (octal && i==1)
						{
							hex=true;
							continue;
						}
						break;
					case 'd':
					case 'D':
					case 'f':
					case 'F':
						if (hex)
							continue;
						else if (i==length-1 && seenSomeDigits) continue;
						break;
					case 'l':
					case 'L':
						if (i==length-1 && seenSomeDigits)
							continue;
						else
							break;
					case 'e':
					case 'E':
						if (seenSomeDigits) continue;
						break;
					case 'a':
					case 'A':
					case 'b':
					case 'B':
					case 'c':
					case 'C':
						if (hex) continue;
						break;
					case '.':
					case '-':
						// Normally, this shouldn't be necessary, because most modes
						// define '.' and '-' SEQs. However, in props mode, we can't
						// define such a SEQ because it would break the AT_LINE_START
						// MARK_PREVIOUS rule.
						continue;
					default:
						break;
				}

				// if we ended up here, then we have found a
				// non-digit character.
				digit=false;
				break;
			}

			// If we got this far with digit = true, then the keyword
			// consists of all digits. Add it as such.
			if (digit && seenSomeDigits)
			{
				if (start!=lastOffset)
				{
					tokenList.addToken(start-lastOffset, context.getSyntaxRules().getDefault(), context.getSyntaxRules());
				}
				tokenList.addToken(length, Style.DIGIT, context.getSyntaxRules());
				lastKeyword=lastOffset=end;

				return;
			}
		}

		if (keywordMap!=null)
		{
			byte styleId=keywordMap.lookup(segment, start, length);

			if (styleId!=Style.DEFAULT)
			{
				if (start!=lastOffset)
				{
					tokenList.addToken(start-lastOffset, context.getSyntaxRules().getDefault(), context.getSyntaxRules());
				}
				tokenList.addToken(length, styleId, context.getSyntaxRules());
				lastKeyword=lastOffset=end;
			}
		}
	}

}
