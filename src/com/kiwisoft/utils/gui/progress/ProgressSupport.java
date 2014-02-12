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
package com.kiwisoft.utils.gui.progress;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;

import com.kiwisoft.utils.Utils;

/**
 * Convenient class to handle the <code>ProgressListener</code> access.
 *
 * @author Stefan Stiller
 * @version $Revision: 1.1.1.1 $, $Date: 2004/07/17 11:01:22 $
 */
public class ProgressSupport
{
	private Job job;
	private ProgressListener listener;

	/**
	 * constructs a <code>ProgressSupport</code> for the given listener
	 *
	 * @param listener the <code>ProgressListener</code> to be hosted
	 */
	public ProgressSupport(Job job, ProgressListener listener)
	{
		this.job=job;
		this.listener=listener;
	}

	/**
	 * <code>progressInitialized()</code> access to the listener
	 *
	 * @param maxValue the maxValue of the progress element potentially being processed
	 * @see ProgressListener#progressInitialized(Job,boolean,int,String)
	 */
	public void initialize(boolean mode, int maxValue, String pattern)
	{
		if (listener!=null) listener.progressInitialized(job, mode, maxValue, pattern);
	}

	/**
	 * records the specified message as error
	 *
	 * @param message the error message <code>String</code>
	 * @see ProgressListener#progressMessage(Job, String, int)
	 */
	public void error(String message)
	{
		if (listener!=null) listener.progressMessage(job, message, ProgressMessage.ERROR);
	}

	public void error(Throwable throwable)
	{
		if (listener!=null)
			listener.progressMessage(job, Utils.getShortClassName(throwable.getClass())+": "+throwable.getMessage(),
									 ProgressMessage.ERROR);
	}

	/**
	 * records the specified message as warning
	 *
	 * @param message the warning message <code>String</code>
	 * @see ProgressListener#progressMessage(Job, String, int)
	 */
	public void warning(String message)
	{
		if (listener!=null) listener.progressMessage(job, message, ProgressMessage.WARNING);
	}

	/**
	 * records the specified message as process info
	 *
	 * @param message the message <code>String</code>
	 * @see ProgressListener#progressMessage(Job, String, int)
	 */
	public void info(String message)
	{
		if (listener!=null) listener.progressMessage(job, message, ProgressMessage.INFO);
	}

	/**
	 * notifies for a process step
	 *
	 * @param value	 the <code>int</code> new value on the process elements being processed
	 * @param increment <code>true</code>, if <code>value</code> is a diff; <code>false</code>, if value is absolute
	 * @see ProgressListener#progressChanged(Job, int, boolean)
	 */
	public void progress(int value, boolean increment)
	{
		if (listener!=null) listener.progressChanged(job, value, increment);
	}

	public void progress()
	{
		if (listener!=null) listener.progressChanged(job);
	}

	/**
	 * defines the progress indicator message
	 *
	 * @param message the <code>String</code> to display
	 * @see ProgressListener#progressStepStarted(Job, String)
	 */
	public void startStep(String message)
	{
		if (listener!=null) listener.progressStepStarted(job, message);
	}

	/**
	 * determines, if the process is stopped
	 *
	 * @return <code>true</code>, if the process is stopped
	 * @see ProgressListener#isStoppedByUser(Job)
	 */
	public boolean isStoppedByUser()
	{
		return listener!=null && listener.isStoppedByUser(job);
	}

	public Appender createAppender()
	{
		return new Appender();
	}
	
	private class Appender extends AppenderSkeleton
	{
		protected void append(LoggingEvent loggingEvent)
		{
			String message=String.valueOf(loggingEvent.getMessage());
			if (Level.ERROR.equals(loggingEvent.level) || Level.FATAL.equals(loggingEvent.level))
				error(message);
			else if (Level.WARN.equals(loggingEvent.level))
				warning(message);
			else if (Level.INFO.equals(loggingEvent.level))
				info(message);
		}

		public boolean requiresLayout()
		{
			return false;
		}

		public void close()
		{
		}
	}
}
