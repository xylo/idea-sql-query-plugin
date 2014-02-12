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
import java.text.DecimalFormat;
import javax.swing.*;
import javax.swing.event.DocumentEvent;

import com.kiwisoft.utils.gui.IconManager;
import com.kiwisoft.utils.gui.ButtonDialog;
import com.kiwisoft.utils.DocumentAdapter;
import com.kiwisoft.utils.StringUtils;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.2 $, $Date: 2006/03/24 18:27:20 $
 */
public class NumberFormatLookup implements DialogLookup
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

		private JTextField tfPattern;
		private JList lstExamples;
		private FormatChangeListener listener;

		private FormatDialog(Dialog dialog, String format)
		{
			super(dialog, "Number Format", true);
			this.format = format;
		}

		private FormatDialog(Frame frame, String format)
		{
			super(frame, "Number Format", true);
			this.format = format;
		}


		protected JComponent createContentPane()
		{
			tfPattern = new JTextField(10);
			lstExamples = new JList();
			lstExamples.setBackground(tfPattern.getBackground());
			lstExamples.setBorder(tfPattern.getBorder());
			lstExamples.setPreferredSize(new Dimension(200, 175));

			listener = new FormatChangeListener();
			tfPattern.getDocument().addDocumentListener(listener);

			JPanel panel = new JPanel(new GridBagLayout());
			int row = 0;
			panel.add(new JLabel("Pattern:"),
					  new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
			panel.add(tfPattern,
					  new GridBagConstraints(1, row, 4, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 0), 0, 0));

			row++;
			panel.add(createInsertButton("Digit", "0"),
					  new GridBagConstraints(1, row, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));
			panel.add(createInsertButton("Digit [no zeros]", "#"),
					  new GridBagConstraints(2, row, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));
			panel.add(createInsertButton("Decimal Separator", "."),
					  new GridBagConstraints(3, row, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));
			panel.add(createInsertButton("Grouping Separator", ","),
					  new GridBagConstraints(4, row, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

			row++;
			panel.add(createInsertButton("Exponent", "E0"),
					  new GridBagConstraints(1, row, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0));
			panel.add(createInsertButton("Percentage", "%"),
					  new GridBagConstraints(2, row, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0));
			panel.add(createInsertButton("Per mille", "\u2030"),
					  new GridBagConstraints(3, row, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0));
			panel.add(createInsertButton("Currency", "\u00A4"),
					  new GridBagConstraints(4, row, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0));

			row++;
			panel.add(new JLabel("Examples:"),
					  new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
			panel.add(lstExamples,
					  new GridBagConstraints(1, row, 4, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(10, 5, 0, 0), 0, 0));

			return panel;
		}

		private JButton createInsertButton(String name, String text)
		{
			JButton button = new JButton(new InsertAction(name, text));
			button.setMargin(new Insets(1, 1, 1, 1));
			button.setFocusable(false);
			return button;
		}

		public void initData()
		{
			format = StringUtils.null2empty(format);
			String[] formats = format.split(";");
			if (formats.length == 2)
			{
				tfPattern.setText(formats[0]);
			}
			else
			{
				tfPattern.setText(format);
			}
			if (StringUtils.isEmpty(format)) listener.changedUpdate(null);

			pack();
		}

		public boolean apply()
		{
			String positivePattern = tfPattern.getText();
			String negativePattern = tfPattern.getText();
			if (positivePattern.equals(negativePattern) || StringUtils.isEmpty(negativePattern))
				format = positivePattern;
			else format = positivePattern + ";" + negativePattern;
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
				DefaultListModel examples = new DefaultListModel();
				try
				{
					String pattern = tfPattern.getText();
					if (StringUtils.isEmpty(pattern)) pattern = null;

					addExample(examples, pattern, 12345.0);
					addExample(examples, pattern, 1.234);
					addExample(examples, pattern, 1.23E12);
					addExample(examples, pattern, 1.23E-12);
					addExample(examples, pattern, -12345.0);
					addExample(examples, pattern, -1.234);
					addExample(examples, pattern, -1.23E12);
					addExample(examples, pattern, -1.23E-12);
				}
				catch (Exception e1)
				{
					examples.addElement(e1.getMessage());
				}
				lstExamples.setModel(examples);
			}

			private void addExample(DefaultListModel listModel, String pattern, double number)
			{
				if (pattern != null)
					listModel.addElement(new DecimalFormat(pattern).format(number) + " [" + number + "]");
				else
					listModel.addElement(number + " [" + number + "]");
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
				StringBuffer format = new StringBuffer(tfPattern.getText());
				int position = Math.min(format.length(), tfPattern.getCaretPosition());
				String selectedText = tfPattern.getSelectedText();
				if (selectedText != null)
				{
					int selectionStart = tfPattern.getSelectionStart();
					int selectionEnd = tfPattern.getSelectionEnd();
					format.delete(selectionStart, selectionEnd);
					format.insert(selectionStart, text);
					tfPattern.setText(format.toString());
					tfPattern.setCaretPosition(selectionStart + text.length());
				}
				else
				{
					format.insert(position, text);
					tfPattern.setText(format.toString());
					tfPattern.setCaretPosition(position + text.length());
				}
			}
		}
	}
}
