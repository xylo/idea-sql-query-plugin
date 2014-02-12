/*
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: Mar 24, 2003
 * Time: 9:05:01 PM
 */
package com.kiwisoft.utils.gui.progress;

public class ProgressMessage
{
	public static final int INFO = 0;
	public static final int WARNING = 1;
	public static final int ERROR = 2;

	private int severity;
	private String msgText;

	public ProgressMessage(String msgText, int severity)
	{
		this.msgText=msgText;
		this.severity=severity;
	}

	public String getMsgText()
	{
		return msgText;
	}

	public int getSeverity()
	{
		return severity;
	}

	public String toString()
	{
		return msgText;
	}
}
