package com.kiwisoft.sqlPlugin;

import com.kiwisoft.utils.idea.PluginErrorHandler;
import com.kiwisoft.utils.VersionInfo;

/**
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: 12.11.2006
 * Time: 16:10:58
 * To change this template use File | Settings | File Templates.
 */
public class ErrorReporter extends PluginErrorHandler
{
	public ErrorReporter()
	{
	}

	public VersionInfo getVersionInfo()
	{
		return SQLPluginVersionInfo.getInstance();
	}
}
