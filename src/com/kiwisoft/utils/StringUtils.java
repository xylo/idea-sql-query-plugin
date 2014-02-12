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
package com.kiwisoft.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.10 $, $Date: 2006/03/24 18:02:05 $
 */
public class StringUtils
{
	private StringUtils()
	{
	}

	public static final int MIXED_CASE=0;
	public static final int UPPER_CASE=1;
	public static final int LOWER_CASE=2;

	public static boolean matchExpression(String text, String exp)
	{
		String exp2=exp;
		String text2=text;

		while (exp2.length()>0)
		{
			char ch;
			try
			{
				ch=exp2.charAt(0);
			}
			catch (StringIndexOutOfBoundsException e)
			{
				return true;
			}
			exp2=removeFirstChar(exp2);
			switch (ch)
			{
				case '*':
					if (exp2.length()==0) return true;
					boolean found=false;
					while (!found && text2.length()>0)
					{
						found=matchExpression(text2, exp2);
						if (!found) text2=removeFirstChar(text2);
					}
					if (text2.length()<=0) return false;
					if (!found) return false;
					break;
				case '?':
					if (text2.length()>0)
						text2=removeFirstChar(text2);
					else
						return false;
					break;
				default:
					try
					{
						if (ch==text2.charAt(0))
							text2=removeFirstChar(text2);
						else
							return false;
					}
					catch (StringIndexOutOfBoundsException e)
					{
						return false;
					}
			}
		}
		return (text2.length()==0);
	}

	public static String removeFirstChar(String string)
	{
		return string.substring(1, string.length());
	}

	public static String replaceStrings(String text, String s1, String s2)
	{
		int pos;
		if (text==null || s2==null) return text;
		int limit=0;
		while ((pos=text.indexOf(s1))>=limit)
		{
			text=text.substring(0, pos)+s2+text.substring(pos+s1.length(), text.length());
			limit=pos+s2.length();
		}
		return text;
	}

	public static String trim(String text)
	{
		if (text==null) return null;
		int start=0;
		while (start<text.length() && Character.isWhitespace(text.charAt(start)))
			start++;
		int end=text.length()-1;
		while (end>=0 && Character.isWhitespace(text.charAt(end)))
			end--;
		if (start>end)
			return "";
		else
			return text.substring(start, end+1);
	}

	public static boolean isEmpty(String value)
	{
		return (value==null || value.trim().length()<=0);
	}

	public static boolean equal(String s1, String s2)
	{
        //noinspection StringEquality
		if (s1==s2) return true;
		if (s1==null || s2==null) return false;
		return s1.equals(s2);
	}

	public static String toByteString(byte[] bytes, String seperator)
	{
		StringBuffer buffer=new StringBuffer(bytes.length*2);
		for (int i=0; i<bytes.length; i++)
		{
			if (i>0 && seperator!=null) buffer.append(seperator);
			buffer.append(toByteString(bytes[i]));
		}
		return buffer.toString();
	}

	public static String toByteString(byte aByte)
	{
		byte lowByte=(byte)(aByte&15);
		byte highByte=(byte)((aByte&240)/16);
		return Integer.toHexString(highByte)+Integer.toHexString(lowByte);
	}

	public static byte[] toByteArray(String hexString)
	{
		byte[] buffer=new byte[hexString.length()/2];
		for (int i=0; i<buffer.length; i++)
			buffer[i]=toByte(hexString.substring(2*i, 2*i+2));
		return buffer;
	}

	public static byte toByte(String hex)
	{
		char[] lowChar={'#', '0'};
		char[] highChar={'#', '0'};

		int start=0;
		if (hex.startsWith("#"))
			start=1;
		else if (hex.startsWith("0x")) start=2;

		if (hex.length()>start+1)
		{
			lowChar[1]=hex.charAt(start+1);
			highChar[1]=hex.charAt(start);
		}
		else if (hex.length()>start)
		{
			lowChar[1]=hex.charAt(start);
		}

		byte lowByte=Byte.decode(new String(lowChar)).byteValue();
		byte highByte=Byte.decode(new String(highChar)).byteValue();

		return (byte)(lowByte+highByte*16);
	}

	public static String convertJavaString(String string)
	{
		String text=string.trim();
		StringBuffer buffer=new StringBuffer(text.length());
		StringBuffer whiteSpace=new StringBuffer();
		int lineBreaks=0;
		boolean quoted=text.startsWith("\"");
		boolean inString=false;
		int varCount=1;
		for (int i=0; i<text.length(); i++)
		{
			char ch=text.charAt(i);
			String append=null;
			if (quoted)
			{
				switch (ch)
				{
					case '\"':
						inString=!inString;
						break;
					case '?':
						if (inString) append="&var"+(varCount++)+";";
						break;
					case ' ':
					case '\t':
						if (inString) whiteSpace.append(ch);
						break;
					case '\r':
						break;
					case '\n':
						lineBreaks++;
						whiteSpace.delete(0, whiteSpace.length());
						break;
					case '\\':
						if (inString && i+1<text.length())
						{
							char nextCh=text.charAt(i+1);
							if (nextCh=='n')
							{
								lineBreaks++;
								whiteSpace.delete(0, whiteSpace.length());
								i++;
								break;
							}
							else if (nextCh=='t')
							{
								whiteSpace.append('\t');
								i++;
								break;
							}
						}
						// continue as default
					default:
						if (inString) append=Character.toString(ch);
				}
			}
			else
			{
				switch (ch)
				{
					case '?':
						append="&var"+(varCount++)+';';
						break;
					case ' ':
					case '\t':
						whiteSpace.append(ch);
						break;
					case '\r':
						break;
					case '\n':
						lineBreaks++;
						whiteSpace.delete(0, whiteSpace.length());
						break;
					default:
						append=Character.toString(ch);
				}
			}
			if (append!=null)
			{
				append(buffer, '\n', lineBreaks);
				lineBreaks=0;
				buffer.append(whiteSpace).append(ch);
				whiteSpace.delete(0, whiteSpace.length());
			}
		}
		return buffer.toString();
	}

	public static void append(StringBuffer buffer, char ch, int count)
	{
		for (int i=0; i<count; i++)
			buffer.append(ch);
	}

	public static List tokenize(String list, String separator)
	{
		List functions=new LinkedList();
		for (StringTokenizer tokenizer=new StringTokenizer(list, separator); tokenizer.hasMoreTokens();)
		{
			functions.add(tokenizer.nextToken().trim());
		}
		return functions;
	}

	public static String encodeURL(String text)
	{
		try
		{
			return URLEncoder.encode(text, "UTF-8");
		}
		catch (UnsupportedEncodingException e)
		{
			//noinspection deprecation
			return URLEncoder.encode(text);
		}
	}

	public static String decodeURL(String text)
	{
		try
		{
			return URLDecoder.decode(text, "UTF-8");
		}
		catch (UnsupportedEncodingException e)
		{
            //noinspection deprecation
			return URLDecoder.decode(text);
		}
	}

	/**
	 * Convert a nibble to a hex character
	 *
	 * @param	nibble	the nibble to convert.
	 */
	public static char toHex(int nibble)
	{
		return HEX_DIGITS[(nibble&0xF)];
	}

	/**
	 * A table of hex digits
	 */
	private static final char[] HEX_DIGITS={'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

	public static Comparator getComparator()
	{
		return new Comparator()
		{
			public int compare(Object o1, Object o2)
			{
				return String.CASE_INSENSITIVE_ORDER.compare(String.valueOf(o1), String.valueOf(o2));
			}
		};
	}

	public static String createHTMLToolTip(String text)
	{
		return "<html>"+createHtml(text)+"</html>";
	}

	public static String createHtml(String text)
	{
		text=replaceStrings(text, "\n", "<br>");
		text=replaceStrings(text, "\t", "    ");
		text=replaceStrings(text, " ", "&nbsp;");
		return text;
	}

	public static String enumerate(Collection objects, String separator)
	{
		StringBuffer buffer=new StringBuffer();
		for (Iterator it=objects.iterator(); it.hasNext();)
		{
			buffer.append(it.next());
			if (it.hasNext()) buffer.append(separator);
		}
		return buffer.toString();
	}

	public static String enumerate(Object[] objects, String separator)
	{
		if (objects==null) return "";
		StringBuffer buffer=new StringBuffer();
		for (int i=0; i<objects.length; i++)
		{
			if (i>0) buffer.append(separator);
			buffer.append(objects[i]);
		}
		return buffer.toString();
	}

	public static String applyCharacterCase(String text, int charCase)
	{
		if (text==null) return null;
		switch (charCase)
		{
			case UPPER_CASE:
				return text.toUpperCase();
			case LOWER_CASE:
				return text.toLowerCase();
			default:
				return text;
		}
	}

	public static String fillLeft(String s, char c, int length)
	{
		while (s.length()<length)
			s=c+s;
		return s;
	}

	public static String fillRight(String s, char c, int length)
	{
		while (s.length()<length)
			s=s+c;
		return s;
	}

	public static String[] combineArrays(String[] array1, String[] array2)
	{
		String[] array=new String[array1.length+array2.length];
		System.arraycopy(array1, 0, array, 0, array1.length);
		System.arraycopy(array2, 0, array, array1.length, array2.length);
		return array;
	}

    public static String null2empty(String text)
    {
        return text==null ? "" : text;
	}
}
