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
package com.kiwisoft.utils.gui.lookup;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.*;
import javax.swing.event.DocumentEvent;

import com.kiwisoft.utils.gui.IconManager;
import com.kiwisoft.utils.gui.ButtonDialog;
import com.kiwisoft.utils.DocumentAdapter;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.2 $, $Date: 2006/03/24 18:27:19 $
 */
public class DateFormatLookup implements DialogLookup
{
	public void open(JTextField field)
	{
		String format = field.getText();
		Container window = field.getTopLevelAncestor();
		FormatDialog dialog;
		if (window instanceof Dialog) dialog = new FormatDialog((Dialog) window, format);
		else dialog = new FormatDialog((Frame) window, format);
		if (dialog.open())
		{
			field.setText(dialog.getFormat());
		}
	}

	public Icon getIcon()
	{
		return IconManager.getIcon("/com/kiwisoft/utils/gui/table/lookup_zoom.png");
	}

	private static class FormatDialog extends ButtonDialog
	{
		private String format;

		private JTextField tfFormat;
		private JTextField tfExample;

		private FormatDialog(Dialog dialog, String format)
		{
			super(dialog, "Date Format", true);
			this.format = format;
		}

		private FormatDialog(Frame frame, String format)
		{
			super(frame, "Date Format", true);
			this.format = format;
		}

		protected JComponent createContentPane()
		{
			tfFormat = new JTextField(10);
			tfExample = new JTextField(10);
			tfExample.setEditable(false);

			tfFormat.getDocument().addDocumentListener(new FormatChangeListener());

			JPanel panel = new JPanel(new GridBagLayout());
			int row = 0;
			panel.add(new JLabel("Pattern:"),
					  new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
			panel.add(tfFormat,
					  new GridBagConstraints(1, row, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 0), 0, 0));

			row++;
			panel.add(new JLabel("Example:"),
					  new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
			panel.add(tfExample,
					  new GridBagConstraints(1, row, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

			row++;
			JScrollPane buttons = new JScrollPane(createButtonsPanel());
			buttons.setBorder(null);
			buttons.setPreferredSize(new Dimension(400, 200));
			panel.add(buttons,
					  new GridBagConstraints(0, row, 2, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(20, 0, 0, 0), 0, 0));

			return panel;
		}

		private JPanel createButtonsPanel()
		{
			JPanel panel = new JPanel(new GridBagLayout());
			int row = 0;
			panel.add(new JLabel("Year:"),
					  new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
			panel.add(createInsertButton("2 digits", "yy"),
					  new GridBagConstraints(1, row, 1, 1, 0.1, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 0), 0, 0));
			panel.add(createInsertButton("4 digits", "yyyy"),
					  new GridBagConstraints(2, row, 1, 1, 0.1, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 0), 0, 0));

			row++;
			panel.add(new JLabel("Month:"),
					  new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 0, 0, 0), 0, 0));
			panel.add(createInsertButton("1-2 digits", "M"),
					  new GridBagConstraints(1, row, 1, 1, 0.1, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0));
			panel.add(createInsertButton("2 digits", "MM"),
					  new GridBagConstraints(2, row, 1, 1, 0.1, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0));
			panel.add(createInsertButton("3 letters", "MMM"),
					  new GridBagConstraints(3, row, 1, 1, 0.1, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0));
			panel.add(createInsertButton("Full", "MMMM"),
					  new GridBagConstraints(4, row, 1, 1, 0.1, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0, 10), 0, 0));

			row++;
			panel.add(new JLabel("Day in Month:"),
					  new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 0, 0, 0), 0, 0));
			panel.add(createInsertButton("1-2 digits", "d"),
					  new GridBagConstraints(1, row, 1, 1, 0.1, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0));
			panel.add(createInsertButton("2 digits", "dd"),
					  new GridBagConstraints(2, row, 1, 1, 0.1, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0));

			row++;
			panel.add(new JLabel("Hour in Day [0-23]:"),
					  new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 0, 0, 0), 0, 0));
			panel.add(createInsertButton("1-2 digits", "H"),
					  new GridBagConstraints(1, row, 1, 1, 0.1, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0));
			panel.add(createInsertButton("2 digits", "HH"),
					  new GridBagConstraints(2, row, 1, 1, 0.1, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0));

			row++;
			panel.add(new JLabel("Hour in Day [1-12]:"),
					  new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 0, 0, 0), 0, 0));
			panel.add(createInsertButton("1-2 digits", "h"),
					  new GridBagConstraints(1, row, 1, 1, 0.1, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0));
			panel.add(createInsertButton("2 digits", "hh"),
					  new GridBagConstraints(2, row, 1, 1, 0.1, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0));

			row++;
			panel.add(new JLabel("Hour in Day [1-24]:"),
					  new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 0, 0, 0), 0, 0));
			panel.add(createInsertButton("1-2 digits", "k"),
					  new GridBagConstraints(1, row, 1, 1, 0.1, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0));
			panel.add(createInsertButton("2 digits", "kk"),
					  new GridBagConstraints(2, row, 1, 1, 0.1, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0));

			row++;
			panel.add(new JLabel("Hour in Day [0-11]:"),
					  new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 0, 0, 0), 0, 0));
			panel.add(createInsertButton("1-2 digits", "K"),
					  new GridBagConstraints(1, row, 1, 1, 0.1, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0));
			panel.add(createInsertButton("2 digits", "KK"),
					  new GridBagConstraints(2, row, 1, 1, 0.1, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0));

			row++;
			panel.add(new JLabel("Minute:"),
					  new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 0, 0, 0), 0, 0));
			panel.add(createInsertButton("1-2 digits", "m"),
					  new GridBagConstraints(1, row, 1, 1, 0.1, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0));
			panel.add(createInsertButton("2 digits", "mm"),
					  new GridBagConstraints(2, row, 1, 1, 0.1, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0));

			row++;
			panel.add(new JLabel("Second:"),
					  new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 0, 0, 0), 0, 0));
			panel.add(createInsertButton("1-2 digits", "s"),
					  new GridBagConstraints(1, row, 1, 1, 0.1, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0));
			panel.add(createInsertButton("2 digits", "ss"),
					  new GridBagConstraints(2, row, 1, 1, 0.1, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0));

			row++;
			panel.add(new JLabel("Millisecond:"),
					  new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 0, 0, 0), 0, 0));
			panel.add(createInsertButton("3 digits", "S"),
					  new GridBagConstraints(1, row, 1, 1, 0.1, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0));

			row++;
			panel.add(new JLabel("Day in Week:"),
					  new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 0, 0, 0), 0, 0));
			panel.add(createInsertButton("3 letters", "E"),
					  new GridBagConstraints(1, row, 1, 1, 0.1, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0));
			panel.add(createInsertButton("Full", "EEEE"),
					  new GridBagConstraints(2, row, 1, 1, 0.1, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0));

			row++;
			panel.add(new JLabel("Day in Year:"),
					  new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 0, 0, 0), 0, 0));
			panel.add(createInsertButton("1-3 digits", "D"),
					  new GridBagConstraints(1, row, 1, 1, 0.1, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0));
			panel.add(createInsertButton("3 digits", "DDD"),
					  new GridBagConstraints(1, row, 1, 1, 0.1, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0));

			row++;
			panel.add(new JLabel("AM/PM:"),
					  new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 0, 0, 0), 0, 0));
			panel.add(createInsertButton("2 letters", "a"),
					  new GridBagConstraints(1, row, 1, 1, 0.1, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0));

			row++;
			panel.add(new JLabel("Week in Year:"),
					  new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 0, 0, 0), 0, 0));
			panel.add(createInsertButton("1-2 digits", "w"),
					  new GridBagConstraints(1, row, 1, 1, 0.1, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0));
			panel.add(createInsertButton("2 digits", "ww"),
					  new GridBagConstraints(2, row, 1, 1, 0.1, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0));

			row++;
			panel.add(new JLabel("Week in Month:"),
					  new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 0, 0, 0), 0, 0));
			panel.add(createInsertButton("1 digit", "W"),
					  new GridBagConstraints(1, row, 1, 1, 0.1, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0));

			row++;
			panel.add(new JLabel("Timezone"),
					  new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 0, 0, 0), 0, 0));
			panel.add(createInsertButton("3 letters", "z"),
					  new GridBagConstraints(1, row, 1, 1, 0.1, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0));
			panel.add(createInsertButton("Full", "zzzz"),
					  new GridBagConstraints(2, row, 1, 1, 0.1, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0));

			row++;
			panel.add(new JLabel("Era:"),
					  new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 0, 0, 0), 0, 0));
			panel.add(createInsertButton("2 letters", "G"),
					  new GridBagConstraints(1, row, 1, 1, 0.1, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0));

			return panel;
		}

		private JButton createInsertButton(String label, String text)
		{
			JButton button = new JButton(new InsertAction(label, text));
			button.setMargin(new Insets(1, 1, 1, 1));
			button.setFocusable(false);
			return button;
		}


		public void initData()
		{
			tfFormat.setText(format);
		}

		protected boolean apply()
		{
			format = tfFormat.getText();
			return true;
		}

		private String getFormat()
		{
			return format;
		}

		private class FormatChangeListener extends DocumentAdapter
		{
			public void changedUpdate(DocumentEvent e)
			{
				try
				{
					SimpleDateFormat dateFormat = new SimpleDateFormat(tfFormat.getText());
					tfExample.setText(dateFormat.format(new Date()));
				}
				catch (Exception e1)
				{
					tfExample.setText(e1.getMessage());
				}
			}
		}

		private class InsertAction extends AbstractAction
		{
			private String text;

			/**
			 * Defines an <code>Action</code> object with a default
			 * description string and default icon.
			 */
			public InsertAction(String name, String text)
			{
				super(name);
				putValue(SHORT_DESCRIPTION, text);
				this.text = text;
			}

			public void actionPerformed(ActionEvent e)
			{
				StringBuffer format = new StringBuffer(tfFormat.getText());
				int position = Math.min(format.length(), tfFormat.getCaretPosition());
				String selectedText = tfFormat.getSelectedText();
				if (selectedText != null)
				{
					int selectionStart = tfFormat.getSelectionStart();
					int selectionEnd = tfFormat.getSelectionEnd();
					format.delete(selectionStart, selectionEnd);
					format.insert(selectionStart, text);
					tfFormat.setText(format.toString());
					tfFormat.setCaretPosition(selectionStart + text.length());
				}
				else
				{
					format.insert(position, text);
					tfFormat.setText(format.toString());
					tfFormat.setCaretPosition(position + text.length());
				}
			}
		}
	}
}
