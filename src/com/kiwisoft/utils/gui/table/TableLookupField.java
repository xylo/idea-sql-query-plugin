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
package com.kiwisoft.utils.gui.table;

import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import com.kiwisoft.utils.gui.lookup.LookupField;
import com.kiwisoft.utils.gui.lookup.Lookup;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.2 $, $Date: 2006/03/24 18:04:07 $
 */
class TableLookupField extends LookupField
{
    private List buttons;

    public TableLookupField(Lookup lookup)
    {
        super(new TableTextField(), lookup, null);
    }

    protected String getLookupIcon()
    {
        return "/icons/lookup_table.gif";
    }

    public void focusLost(FocusEvent e)
    {
    }

    protected boolean processKeyBinding(KeyStroke ks, KeyEvent e, int condition, boolean pressed)
    {
        TableTextField tableTextField=(TableTextField)getTextField();
        if (!tableTextField.isFocusOwner())
        {
            tableTextField.requestFocus();
            return tableTextField.processKeyBinding(ks, e, condition, pressed);
        }
        return super.processKeyBinding(ks, e, condition, pressed);
    }

    protected void layoutButtons(JTextField textField, JButton btnEdit, JButton btnCreate, JButton btnLookup)
    {
        buttons=new ArrayList();
        setLayout(null);
        add(textField);
        if (btnEdit!=null)
        {
            add(btnEdit);
            buttons.add(btnEdit);
        }
        if (btnCreate!=null)
        {
            add(btnCreate);
            buttons.add(btnCreate);
        }
        if (btnLookup!=null)
        {
            add(btnLookup);
            buttons.add(btnLookup);
        }
    }

    public void setBounds(int x, int y, int width, int height)
    {
        super.setBounds(x, y, width, height);
        int buttonHeight=Math.min(20, height);
        int xPos=width-buttonHeight*buttons.size();
        getTextField().setBounds(0, 0, xPos, height);
        for (Iterator it=buttons.iterator(); it.hasNext();)
        {
            JButton button=(JButton)it.next();
            button.setBounds(xPos, 0, buttonHeight, buttonHeight);
            xPos+=buttonHeight;
        }
    }
}
