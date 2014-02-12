package com.kiwisoft.utils.gui.progress;

/**
 * Listener which can be used to view the progress of a black box process.
 *
 * @author Stefan Stiller
 * @version $Revision: 1.1.1.1 $, $Date: 2004/07/17 11:01:22 $
 */
public interface ProgressListener
{
    /**
     * @return Return <code>true</code> if underlying process should stop.
     */
    public boolean isStoppedByUser(Job job);

    /**
     * Callback method to indicate a progress change
     *
     * @param job The source of the event
     * @param value Current value for a progress view.
     */
    public void progressChanged(Job job, int value, boolean increment);

	public void progressChanged(Job job);

    public void progressStepStarted(Job job, String message);

    /**
     * Callback method for messages from the process
     *
     * @param job The source of the event
     * @param message The message id
     */
    public void progressMessage(Job job, String message, int severity);

    /**
     * Callback method to initialize a progress view.
     *
     * @param job The source of the event
	 * @param maxValue The maximum value for the progress bar
	 */
    public void progressInitialized(Job job, boolean mode, int maxValue, String pattern);
}
