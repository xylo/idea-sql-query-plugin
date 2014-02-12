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

import java.awt.*;
import java.awt.event.ActionEvent;

import javax.swing.*;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.2 $, $Date: 2006/03/24 18:27:18 $
 */
public abstract class ButtonDialog extends JDialog
{
    public static final String DEFAULT_ACTION = "DefaultAction";

    /** @noinspection FieldCanBeLocal*/
    private boolean returnValue;

    protected ButtonDialog(Frame frame, String title, boolean modal)
    {
        super(frame, title, modal);
        createComponents();
    }

	protected ButtonDialog(Dialog dialog, String title, boolean modal)
	{
		super(dialog, title, modal);
		createComponents();
	}

    public boolean open()
    {
        returnValue = false;
        show();
        return returnValue;
    }

    private void createComponents()
	{
		JButton defaultButton=null;

		Action[] actions=getActions();

		JPanel pnlButtons=new JPanel(new FlowLayout(FlowLayout.TRAILING, 5, 0));
		for (int i=0; i<actions.length; i++)
		{
			Action action=actions[i];
			JButton button=new JButton(action);
			if (Boolean.TRUE.equals(action.getValue(DEFAULT_ACTION))) defaultButton=button;
			pnlButtons.add(button);
		}

		JPanel pnlContent=new JPanel(new GridBagLayout());
		pnlContent.add(createContentPane(), new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0,
		        GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(10, 10, 0, 10), 0, 0));
		pnlContent.add(pnlButtons, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0,
		        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 10, 10), 0, 0));
		setContentPane(pnlContent);

		if (defaultButton!=null) getRootPane().setDefaultButton(defaultButton);

		JComponent component=getPreferredFocusComponent();
		if (component!=null) component.requestFocus();
	}

	protected abstract JComponent createContentPane();

	protected void initData()
	{
	}

	public void show()
	{
		if (!isVisible()) // Because show() is also called after the focus comes back from a lookup window.
		{
			initData();
            pack();
            GuiUtils.arrangeWindow((Window) getParent(), this);
		}
		super.show();
	}

	protected Action[] getActions()
	{
		Action okAction =new OkAction();
		okAction.putValue(DEFAULT_ACTION, Boolean.TRUE);
		return new Action[]
		{
			okAction,
			new CancelAction()
		};
	}

    protected JComponent getPreferredFocusComponent()
	{
		return null;
	}

    protected boolean apply()
    {
        return true;
    }

    protected class OkAction extends AbstractAction
    {
        public OkAction()
        {
            super("Ok");
        }

        public void actionPerformed(ActionEvent event)
        {
            try
            {
                if (apply())
                {
                    returnValue=true;
                    dispose();
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
                String className = e.getClass().getName();
                className = className.substring(className.lastIndexOf(".") + 1);
                JOptionPane.showMessageDialog(ButtonDialog.this, className + ": " + e.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    protected class CancelAction extends AbstractAction
    {
        public CancelAction()
        {
            super("Cancel");
        }

        public void actionPerformed(ActionEvent e)
        {
            dispose();
        }
    }
}
