package com.kiwisoft.utils.idea;

import java.awt.Component;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.*;

import com.intellij.openapi.application.ApplicationInfo;
import com.intellij.openapi.diagnostic.ErrorReportSubmitter;
import com.intellij.openapi.diagnostic.IdeaLoggingEvent;
import com.intellij.openapi.diagnostic.SubmittedReportInfo;
import com.intellij.openapi.ui.DialogWrapper;

import com.kiwisoft.utils.VersionInfo;
import com.kiwisoft.utils.WebUtils;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.utils.Utils;

/**
 * Error reporter for IntelliJ IDEA.
 */
public abstract class PluginErrorHandler extends ErrorReportSubmitter
{
	private static final String SUCCESS_PATTERN="New Issue: (\\d+)";

	protected PluginErrorHandler()
	{
	}

	public String getReportActionText()
	{
		return "Report";
	}

	public abstract VersionInfo getVersionInfo();

	protected String getURL()
	{
		return "http://java.sstiller.de/cgi/submitError.pl";
	}

	public SubmittedReportInfo submit(IdeaLoggingEvent[] events, Component parentComponent)
	{
		Map parameters=getParameters(events);
		if (getAdditionalParameters(parentComponent, parameters))
		{
			try
			{
				String output=WebUtils.postRequest(getURL(), parameters).trim();
				Matcher matcher=Pattern.compile(SUCCESS_PATTERN).matcher(output);
				if (matcher.matches())
				{
					return new SubmittedReportInfo(null, "issue #"+matcher.group(1), SubmittedReportInfo.SubmissionStatus.NEW_ISSUE);
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		return new SubmittedReportInfo(null, null, SubmittedReportInfo.SubmissionStatus.FAILED);
	}

	public boolean submit(Throwable throwable, Component parentComponent)
	{
		Map parameters=getParameters(throwable);
		if (getAdditionalParameters(parentComponent, parameters))
		{
			try
			{
				String output=WebUtils.postRequest(getURL(), parameters).trim();
				Matcher matcher=Pattern.compile(SUCCESS_PATTERN).matcher(output);
				if (matcher.matches()) return true;
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		return false;
	}

	protected boolean getAdditionalParameters(Component parentComponent, Map parameters)
	{
		MyDialog dialog=new MyDialog(parentComponent);
		PluginUtils.showDialog(dialog, true, true);
		if (dialog.isOK())
		{
			parameters.put("mail", dialog.getMail());
			parameters.put("comment", dialog.getComment());
			return true;
		}
		return false;
	}

	private Map getParameters(IdeaLoggingEvent[] events)
	{
		Map parameters=getEnvironmentParameters();
		parameters.put("message", events[0].getMessage());
		parameters.put("throwable", fixLineSeparators(events[0].getThrowableText()));
		return parameters;
	}

	private Map getParameters(Throwable throwable)
	{
		Map parameters=getEnvironmentParameters();
		parameters.put("throwable", fixLineSeparators(Utils.toString(throwable)));
		return parameters;
	}

	private Map getEnvironmentParameters()
	{
		Map parameters=new HashMap();
		parameters.put("os", getOSInfo());
		parameters.put("java.vm", getVMInfo());
		parameters.put("ide", getIDEInfo());
		parameters.put("plugin", getPluginInfo());
		return parameters;
	}

	protected String getPluginInfo()
	{
		StringBuffer info=new StringBuffer(getPluginDescriptor().getPluginId().getIdString());
		VersionInfo versionInfo=getVersionInfo();
		if (versionInfo!=null)
		{
			info.append(" ").append(versionInfo.getVersion());
			info.append(" (").append(versionInfo.getBuildNumber()).append(")");
		}
		return info.toString();
	}

	protected String getOSInfo()
	{
		StringBuffer osInfo=new StringBuffer(System.getProperty("os.name"));
		String osVersion=System.getProperty("os.version");
		if (!StringUtils.isEmpty(osVersion)) osInfo.append(" ").append(osVersion);
		String patchLevel=System.getProperty("sun.os.patch.level");
		if (!StringUtils.isEmpty(patchLevel)) osInfo.append(" ").append(patchLevel);
		return osInfo.toString();
	}

	protected String getVMInfo()
	{
		return System.getProperty("java.vm.name")+" "+System.getProperty("java.vm.version");
	}

	protected String getIDEInfo()
	{
		ApplicationInfo ideaInfo=ApplicationInfo.getInstance();
		return "IntelliJ IDEA "+ideaInfo.getMajorVersion()+"."+ideaInfo.getMinorVersion()+" ("+ideaInfo.getBuildNumber()+")";
	}

	private static class MyDialog extends DialogWrapper
	{
		private String mail;
		private String comment;

		private JTextField mailField;
		private JTextArea commentField;

		public MyDialog(Component parent)
		{
			super(parent, true);
			setTitle("Issue Details");
			init();
			pack();
		}

		protected JComponent createCenterPanel()
		{
			mailField=new JTextField(40);
			commentField=new JTextArea(5, 50);
			commentField.setLineWrap(true);
			commentField.setWrapStyleWord(true);

			JPanel panel=new JPanel(new GridBagLayout());
			int row=0;
			panel.add(new JLabel("E-Mail:"),
					  new GridBagConstraints(0, row++, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 0, 5), 0, 0));
			panel.add(mailField,
					  new GridBagConstraints(0, row++, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(2, 5, 5, 5), 0, 0));
			panel.add(new JLabel("Comment:"),
					  new GridBagConstraints(0, row++, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 0, 5), 0, 0));
			panel.add(new JScrollPane(commentField),
					  new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(2, 5, 5, 5), 0, 0));
			return panel;
		}

		protected void doOKAction()
		{
			comment=commentField.getText();
			mail=mailField.getText();
			super.doOKAction();
		}

		public String getMail()
		{
			return mail;
		}

		public String getComment()
		{
			return comment;
		}
	}

	private String fixLineSeparators(String text)
	{
		if (text!=null) return StringUtils.replaceStrings(text, "\r\n", "\n");
		return null;
	}

}
