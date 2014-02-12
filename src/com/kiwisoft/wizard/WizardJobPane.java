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
import javax.swing.Action;
import javax.swing.AbstractAction;

import com.intellij.openapi.application.ApplicationManager;

import com.kiwisoft.utils.gui.progress.Job;
import com.kiwisoft.utils.gui.progress.ProgressPanel;
import com.kiwisoft.utils.Updateable;

/**
 * @author Stefan Stiller
 * @version $Revision: 2$, $Date: 10.08.05 16:07:05$
 */
public class WizardJobPane extends WizardPane
{
	private Job job;
	private ProgressPanel progressPanel;

	public WizardJobPane(WizardDialog dialog, Job job)
	{
		super(dialog);
		this.job=job;
	}

	public Job getJob()
	{
		return job;
	}

	public String getTitle()
	{
		return "Processing";
	}

	public JComponent createComponent()
	{
		progressPanel=new ProgressPanel(job)
		{
			public Thread startProgress()
			{
				Thread thread=new Thread()
				{
					public void run()
					{
						ApplicationManager.getApplication().runReadAction(new Runnable()
						{
							public void run()
							{
								progressPanel.run();
							}
						});
					}
				};
				thread.start();
				return thread;
			}
		};
		getListenerSupport().installPropertyChangeListener(progressPanel, "jobState", getValidator());
		return progressPanel;
	}

	public Action[] getActions()
	{
		return new Action[]{getStopCloseAction()};
	}

	public void initData()
	{
		super.initData();
		progressPanel.startProgress();
	}

	private Action stopCloseAction;

	private Action getStopCloseAction()
	{
		if (stopCloseAction==null) stopCloseAction=new StopCloseAction();
		return stopCloseAction;
	}

	private class StopCloseAction extends AbstractAction implements Updateable
	{
		public StopCloseAction()
		{
			super("Stop");
		}

		public void actionPerformed(ActionEvent e)
		{
			switch (progressPanel.getJobState())
			{
				case ProgressPanel.STOPPED:
					getDialog().dispose();
					return;
				case ProgressPanel.STARTED:
					progressPanel.setStopped();
			}
		}

		public void update()
		{
			switch (progressPanel.getJobState())
			{
				case ProgressPanel.NOT_STARTED:
					putValue(NAME, "Stop");
					setEnabled(false);
					return;
				case ProgressPanel.STARTED:
					putValue(NAME, "Stop");
					setEnabled(true);
					return;
				case ProgressPanel.STOPPING:
					putValue(NAME, "Close");
					setEnabled(false);
					return;
				case ProgressPanel.STOPPED:
					putValue(NAME, "Close");
					setEnabled(true);
			}
		}
	}
}
