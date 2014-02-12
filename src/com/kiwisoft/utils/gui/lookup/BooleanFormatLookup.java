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

import com.kiwisoft.utils.gui.IconManager;
import com.kiwisoft.utils.gui.ButtonDialog;
import com.kiwisoft.utils.BooleanFormat;

import java.awt.*;
import javax.swing.*;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.2 $, $Date: 2006/03/24 18:27:19 $
 */
public class BooleanFormatLookup implements DialogLookup
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

        private JTextField tfTrue;
        private JTextField tfFalse;

        public FormatDialog(Dialog dialog, String format)
        {
            super(dialog, "Boolean Format", true);
            this.format = format;
            setSize(300, 200);
        }

        public FormatDialog(Frame frame, String format)
        {
            super(frame, "Boolean Format", true);
            this.format = format;
            setSize(300, 200);
        }

        protected JComponent createContentPane()
        {
            tfTrue = new JTextField(10);
            tfFalse = new JTextField(10);

            JPanel panel = new JPanel(new GridBagLayout());
            panel.add(new JLabel("True:"), new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
            panel.add(tfTrue, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 0), 0, 0));
            panel.add(new JLabel("False"), new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
            panel.add(tfFalse, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));
            return panel;
        }

        protected void initData()
        {
            try
            {
                BooleanFormat booleanFormat = new BooleanFormat(format);
                tfTrue.setText(booleanFormat.format(Boolean.TRUE));
                tfFalse.setText(booleanFormat.format(Boolean.FALSE));
            }
            catch (IllegalArgumentException e)
            {
            }
        }

        protected boolean apply()
        {
            String trueString = tfTrue.getText();
            String falseString = tfFalse.getText();
            format = falseString + ";" + trueString;
            return true;
        }

        public String getFormat()
        {
            return format;
        }
    }
}
