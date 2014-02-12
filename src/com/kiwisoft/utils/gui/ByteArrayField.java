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
package com.kiwisoft.utils.gui;

import java.awt.Color;
import javax.swing.JTextPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;
import javax.swing.text.*;

import com.kiwisoft.utils.StringUtils;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.5 $, $Date: 2006/03/24 18:39:09 $
 */
public class ByteArrayField extends JTextPane
{
	public ByteArrayField(byte[] data)
	{
		if (data==null) data=new byte[0];
		setEditable(false);

		StyledDocument document=new DefaultStyledDocument();
		Style style=document.addStyle("default", null);
		StyleConstants.setFontFamily(style, "monospaced");
		StyleConstants.setTabSet(style, new TabSet(new TabStop[]{
			new TabStop(80, TabStop.ALIGN_LEFT, TabStop.LEAD_NONE),
			new TabStop(450, TabStop.ALIGN_LEFT, TabStop.LEAD_NONE)}));
		document.setParagraphAttributes(0, 0, style, false);

		style=document.addStyle("bytes", null);
		StyleConstants.setForeground(style, Color.blue);

		try
		{
			int i=0;
			while (i<data.length)
			{
				int j=0;
				String offset=StringUtils.fillLeft(Integer.toHexString(i), '0', 8);
				document.insertString(document.getLength(), offset+"\t", null);
				while (i+j<data.length && j<16)
				{
					byte b=data[i+j];
					document.insertString(document.getLength(), StringUtils.toByteString(b)+" ", document.getStyle("bytes"));
					j++;
				}
				document.insertString(document.getLength(), "\t"+getString(data, i, j)+"\n", null);
				i+=16;
			}
		}
		catch (BadLocationException e)
		{
			e.printStackTrace();
		}

		setDocument(document);
	}

	private String getString(byte[] data, int pos, int length)
	{
		StringBuffer buffer=new StringBuffer(length);
		for (int i=0; i<length; i++)
		{
			byte b=data[pos+i];
			if (b<32) buffer.append((char)0);
			else buffer.append((char)b);
		}
		return buffer.toString();
	}

	public static void main(String[] args)
	{
		ByteArrayField field=new ByteArrayField(new byte[]{
			61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77,
			78, 79, 80, 81, 82, 83, 84, 85});

		JFrame frame=new JFrame();
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setContentPane(new JScrollPane(field));
		frame.setSize(700, 300);
		frame.show();

	}

}
