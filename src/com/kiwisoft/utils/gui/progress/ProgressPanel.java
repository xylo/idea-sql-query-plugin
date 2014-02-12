// Copyright (c) 2002-2005 by net-linx; All rights reserved 
package com.kiwisoft.utils.gui.progress;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.MessageFormat;
import javax.swing.*;

import com.kiwisoft.utils.StringUtils;

/**
 * Panel for displaying process progress.
 *
 * @author Stefan Stiller
 * @version $Revision: 24$, $Date: 18.05.05 13:13:18$
 */
public class ProgressPanel extends JPanel implements ProgressListener
{
	public static final int NOT_STARTED=0;
	public static final int STARTED=1;
	public static final int STOPPING=2;
	public static final int STOPPED=3;

	private Job job;
	private int jobState=NOT_STARTED;
	private int errors;
	private int warnings;

	private ProgressAnimation animation;
	private JLabel progressLabel;
	private JLabel messageLabel;
	private DefaultListModel messages;
	private JLabel errorLabel;
	private JLabel warningLabel;
	private JList messagesList;
	private String progressPattern;

	public ProgressPanel(Job runnable)
	{
		this.job=runnable;
		messages=new DefaultListModel();

		createComponents();
	}

	private void createComponents()
	{
		animation=new ProgressAnimation();
		progressLabel=new JLabel(" ");
		progressLabel.setHorizontalAlignment(SwingConstants.CENTER);
		messageLabel=new JLabel("Processing...");
		errorLabel=new JLabel(getErrorLabel(0));
		warningLabel=new JLabel(getWarningLabel(0));
		messagesList=new JList(messages);
		messagesList.setCellRenderer(new ProgressMessageListRenderer());

		setLayout(new GridBagLayout());
		add(messageLabel,
			new GridBagConstraints(0, 0, 2, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		add(animation,
			new GridBagConstraints(0, 1, 2, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 0, 0, 0), 0, 0));
		add(progressLabel,
			new GridBagConstraints(0, 2, 2, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 0, 0, 0), 0, 0));
		add(new JLabel("Messages:"),
			new GridBagConstraints(0, 3, 2, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 0, 0, 0), 0, 0));
		add(new JScrollPane(messagesList),
			new GridBagConstraints(0, 4, 2, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 0, 0, 0), 0, 0));
		add(errorLabel,
			new GridBagConstraints(0, 5, 1, 1, 0.5, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 0, 0, 12), 0, 0));
		add(warningLabel,
			new GridBagConstraints(1, 5, 1, 1, 0.5, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 12, 0, 0), 0, 0));
	}

	private String getProgressLabel(int current, int total)
	{
		return MessageFormat.format(progressPattern!=null ? progressPattern : "{0} of {1}",
									new Object[]{new Integer(current), new Integer(total)});
	}

	private static String getErrorLabel(int errorCount)
	{
		return "Errors: "+errorCount;
	}

	private static String getWarningLabel(int warningCount)
	{
		return "Warnings: "+warningCount;
	}

	public boolean isStoppedByUser(Job job)
	{
		return jobState==STOPPING || jobState==STOPPED;
	}

	public Thread startProgress()
	{
		Thread thread=new Thread()
		{
			public void run()
			{
				ProgressPanel.this.run();
			}
		};
		thread.start();
		return thread;
	}

	public void run()
	{
		setJobState(STARTED);
		long time=System.currentTimeMillis();
		try
		{
			boolean completed=job.run(this);
			time=System.currentTimeMillis()-time;
			if (completed) progressMessage(job, "Job completed after "+time+"ms", ProgressMessage.INFO);
			else progressMessage(job, "Job aborted after "+time+"ms", ProgressMessage.INFO);
		}
		catch (Throwable e)
		{
			e.printStackTrace();
			progressMessage(job, e.getClass().getName()+": "+e.getMessage(), ProgressMessage.ERROR);
		}
		finally
		{
			try
			{
				job.dispose();
			}
			catch (Throwable e)
			{
				e.printStackTrace();
				progressMessage(job, e.getClass().getName()+": "+e.getMessage(), ProgressMessage.ERROR);
			}
		}
		setJobState(STOPPED);
	}

	public void progressChanged(Job job, final int value, final boolean increment)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				if (increment)
				{
					animation.increaseProgress(value);
				}
				else
				{
					animation.setProgress(value);
				}

				String progressLabelText=getProgressLabel(animation.getProgress(), animation.getMaximum());
				progressLabel.setText(progressLabelText);
			}
		});
	}

	public void progressChanged(Job job)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				animation.updateProgress();
			}
		});
	}

	public void progressStepStarted(Job job, final String message)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				if (message!=null)
					messageLabel.setText(message);
			}
		});
	}

	public void progressMessage(Job job, final String message, final int severity)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				if (!StringUtils.isEmpty(message))
				{
					ProgressMessage listElement=new ProgressMessage(message.trim(), severity);
					messages.addElement(listElement);
					messagesList.setSelectedValue(listElement, true);
				}
				switch (severity)
				{
					case ProgressMessage.INFO:
						animation.setSeverity(ProgressAnimation.OK);
						break;
					case ProgressMessage.WARNING:
						animation.setSeverity(ProgressAnimation.WARNING);
						warnings++;
						warningLabel.setText(getWarningLabel(warnings));
						break;
					case ProgressMessage.ERROR:
						animation.setSeverity(ProgressAnimation.ERROR);
						errors++;
						errorLabel.setText(getErrorLabel(errors));
						break;
				}
			}
		});
	}

	public void progressInitialized(Job job, final boolean mode, final int maxValue, final String pattern)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				animation.setMode(mode ? ProgressAnimation.INCREASING : ProgressAnimation.CONTINUOSLY);
				animation.setMaximum(maxValue);
				progressPattern=pattern;
				progressLabel.setText(getProgressLabel(animation.getProgress(), animation.getMaximum()));
			}
		});
	}

	public void progressInitialized(Job job, final int maxValue)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				animation.setMode(ProgressAnimation.INCREASING);
				animation.setMaximum(maxValue);
				progressLabel.setText(getProgressLabel(animation.getProgress(), animation.getMaximum()));
			}
		});
	}

	public int getErrors()
	{
		return errors;
	}

	public int getWarnings()
	{
		return warnings;
	}

	public int getJobState()
	{
		return jobState;
	}

	private void setJobState(int newState)
	{
		int oldState=jobState;
		jobState=newState;
		firePropertyChange("jobState", oldState, newState);
	}

	public void setStopped()
	{
		if (jobState==STARTED) setJobState(STOPPING);
	}
}
