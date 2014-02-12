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
package com.kiwisoft.wizard;

import java.awt.event.ActionEvent;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.Action;
import javax.swing.AbstractAction;

import com.intellij.openapi.help.HelpManager;

import com.kiwisoft.utils.Updateable;
import com.kiwisoft.utils.ListenerSupport;
import com.kiwisoft.utils.gui.AbstractValidator;

/**
 * @author Stefan Stiller
 * @version $Revision: 4$, $Date: 10.08.05 15:58:32$
 * @since Oct 13, 2004
 */
public abstract class WizardPane
{
	private WizardDialog dialog;
	private JComponent component;
	private ListenerSupport listenerSupport;
	private Validator validator;

	protected WizardPane(WizardDialog dialog)
	{
		this.dialog=dialog;
	}

	public final WizardDialog getDialog()
	{
		return dialog;
	}

	protected String getHelpTopic()
	{
		return getDialog().getHelpTopic();
	}

	public final ListenerSupport getListenerSupport()
	{
		if (listenerSupport==null) listenerSupport=new ListenerSupport();
		return listenerSupport;
	}

	public AbstractValidator getValidator()
	{
		if (validator==null) validator=new Validator();
		return validator;
	}

	protected final JComponent getComponent()
	{
		if (component==null) component=createComponent();
		if (component==null) component=new JLabel(getClass().getName()+".createComponent() is not implemented.");
		return component;
	}

	public abstract JComponent createComponent();

	public Action[] getActions()
	{
		return new Action[]{getHelpAction(), null, getBackAction(), getNextAction(), null, getCloseAction()};
	}

	public void saveData()
	{
	}

	public void initData()
	{
		validateActions();
	}

	protected void validateActions()
	{
		Action[] actions=getActions();
		for (int i=0; i<actions.length; i++)
		{
			Action action=actions[i];
			if (action instanceof Updateable) ((Updateable)action).update();
		}
	}

	protected boolean canGoForward()
	{
		return false;
	}

	protected boolean canGoBack()
	{
		return getDialog().canGoBack();
	}

	private Action nextAction;

	protected Action getNextAction()
	{
		if (nextAction==null) nextAction=createNextAction();
		return nextAction;
	}

	protected Action createNextAction()
	{
		return new NextAction("Next");
	}

	protected WizardPane getNextPane()
	{
		return null;
	}

	public void dispose()
	{
		if (listenerSupport!=null) listenerSupport.dispose();
	}

	public abstract String getTitle();

	protected class NextAction extends AbstractAction implements Updateable
	{
		public NextAction(String name)
		{
			super(name);
			update();
		}

		public void update()
		{
			setEnabled(canGoForward());
		}

		public void actionPerformed(ActionEvent e)
		{
			if (canGoForward())
			{
				WizardPane nextPane=getNextPane();
				if (nextPane!=null) dialog.setCurrentPane(nextPane);
			}
		}
	}

	private Action backAction;

	protected Action getBackAction()
	{
		if (backAction==null) backAction=new BackAction("Back");
		return backAction;
	}

	protected class BackAction extends AbstractAction implements Updateable
	{
		public BackAction(String name)
		{
			super(name);
			update();
		}

		public void update()
		{
			setEnabled(canGoBack());
		}

		public void actionPerformed(ActionEvent e)
		{
			if (canGoBack()) dialog.setLastPane();
		}
	}

	private Action closeAction;

	protected Action getCloseAction()
	{
		if (closeAction==null) closeAction=createCloseAction();
		return closeAction;
	}

	protected Action createCloseAction()
	{
		return new CloseAction("Cancel");
	}

	protected class CloseAction extends AbstractAction
	{
		public CloseAction(String name)
		{
			super(name);
		}

		public void actionPerformed(ActionEvent e)
		{
			dialog.dispose();
		}
	}

	private Action helpAction;

	protected Action getHelpAction()
	{
		if (helpAction==null) helpAction=new HelpAction();
		return helpAction;
	}

	protected class HelpAction extends AbstractAction implements Updateable
	{
		public HelpAction()
		{
			super("Help");
		}

		public void update()
		{
			setEnabled(getHelpTopic()!=null);
		}

		public void actionPerformed(ActionEvent e)
		{
			String helpTopic=getHelpTopic();
			if (helpTopic!=null)
			{
				HelpManager.getInstance().invokeHelp(helpTopic);
			}
		}
	}

	private class Validator extends AbstractValidator
	{
		public void validate()
		{
			validateActions();
		}
	}

}

/*-----------------------------------------------------------------------------
Modifications
--------------------
$Log: 
 4    nxStar_5.31.2.1.0     10.08.05 15:58:32    Stefan Stiller  Prepared
      actions for security
 3    nxStar_4.3_ServiceOrder1.2         11.02.05 12:07:49    Stefan Stiller 
      Made constructor protected
 2    nxStar_4.3_ServiceOrder1.1         05.01.05 11:53:16    Stefan Stiller 
      show error panel if createComponent() returns null
 1    nxStar_4.3_ServiceOrder1.0         19.10.04 13:08:22    Stefan Stiller  
$
-----------------------------------------------------------------------------*/
