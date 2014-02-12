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
package com.kiwisoft.sqlPlugin.dataLoad;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

import org.apache.log4j.*;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

import bsh.EvalError;
import bsh.Interpreter;
import com.kiwisoft.db.Database;
import com.kiwisoft.db.DatabaseUtils;
import com.kiwisoft.sqlPlugin.dataLoad.io.DataFileReader;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.utils.gui.progress.Job;
import com.kiwisoft.utils.gui.progress.ProgressListener;
import com.kiwisoft.utils.gui.progress.ProgressSupport;
import com.kiwisoft.utils.gui.progress.ProgressMessage;

/**
 * @author Stefan Stiller
 * @version $Revision: $, $Date: $
 */
public class DataLoadJob implements Job
{
	private Project project;
	private Database database;
	private VirtualFile file;
	private DataLoadDescriptor descriptor;
	private ProgressSupport progressSupport;
	private DataFileReader dataReader;
	private Logger logger;
	private int errorCount;
	private List lines;
	private PrintWriter rejectWriter;
	private int importedLines;
	private int rejectedLines;
	private Interpreter interpreter;
	private MyBeanShellAdapter adapter;

	public DataLoadJob(Project project, Database database, VirtualFile file, DataLoadDescriptor descriptor)
	{
		this.project=project;
		this.database=database;
		this.file=file;
		this.descriptor=descriptor;
	}

	public String getName()
	{
		return "Data Load";
	}

	public boolean run(ProgressListener progressListener) throws Exception
	{
		progressSupport=new ProgressSupport(this, progressListener);

		progressSupport.startStep("Initializing...");
		errorCount=0;
		rejectWriter=null;
		lines=new ArrayList();
		if (!initLogging()) return false;
		if (!initRejectFile()) return false;

		info("Maximal allowed error count: "+descriptor.getMaxErrorCount());
		info("Commit changes: "+descriptor.isCommitChanges());
		info("Use batch mode: "+descriptor.isUseBatchMode());
		info("Batch size: "+descriptor.getBatchSize());
		FileDescriptor fileDescriptor=descriptor.getFileDescriptor();
		List sourceColumns=new ArrayList(descriptor.getSourceColumns());
		String targetTable=descriptor.getTargetTable();
		List targetColumns=new ArrayList(descriptor.getTargetColumns());
		// todo: edited line
		dataReader=fileDescriptor.createReader(new InputStreamReader(file.getInputStream()));
		long fileLength=file.getLength();
		dataReader.start();
		String script=descriptor.getScript();
		if (StringUtils.isEmpty(script)) script=null;
		if (logger.isDebugEnabled()) logger.debug("Script: "+script);
		info("Data file '"+file.getPresentableName()+"' opened.");
		Connection connection=DatabaseUtils.connect(project, database, false);
		if (connection!=null) info("Connected to database.");
		else
		{
			error("Couldn't connect to database.");
			return false;
		}
		String sql=createSQLStatement(targetColumns, targetTable);
		if (logger.isDebugEnabled()) logger.debug("SQL Statement: "+sql);
		PreparedStatement statement=connection.prepareStatement(sql);
		info("Prepared SQL statement.");

		progressSupport.startStep("Loading...");
		progressSupport.initialize(true, 100, "{0}%");
		progressSupport.progress((int)(dataReader.getPosition()/fileLength), false);

		try
		{
			String[] rowData;
			DataRow sourceRow=new DataRow();
			while (!progressSupport.isStoppedByUser() && (rowData=dataReader.readRow())!=null)
			{
				lines.add(dataReader.getLine());
				sourceRow.clear();
				try
				{
					for (int i=0; i<rowData.length; i++)
					{
						String cellValue=rowData[i];
						SourceColumnDescriptor columnDescriptor=(SourceColumnDescriptor)sourceColumns.get(i);
						Object value=columnDescriptor.parse(cellValue);
						sourceRow.set(columnDescriptor.getName(), value);
					}
					if (logger.isDebugEnabled()) logger.debug("Row Data: "+sourceRow);
					if (applyScript(script, sourceRow)!=ProgressMessage.ERROR)
					{
						for (int i=0; i<targetColumns.size(); i++)
						{
							TargetColumnDescriptor columnDescriptor=(TargetColumnDescriptor)targetColumns.get(i);
							Object value=sourceRow.get(columnDescriptor.getSourceColumn());
							if (value==null) statement.setNull(i+1, columnDescriptor.getJdbcType());
							else statement.setObject(i+1, value, columnDescriptor.getJdbcType());
						}
						if (descriptor.isUseBatchMode())
						{
							if (logger.isDebugEnabled()) logger.debug("Add record to batch");
							statement.addBatch();
							if (lines.size()>=descriptor.getBatchSize())
							{
								if (logger.isDebugEnabled()) logger.debug("Execute batch");
								statement.executeBatch();
								commit(connection);
							}
						}
						else
						{
							if (logger.isDebugEnabled()) logger.debug("Execute update");
							statement.executeUpdate();
							commit(connection);
						}
					}
					else
					{
						if (descriptor.isLogRejects()) logRejects();
						if (errorCount>=descriptor.getMaxErrorCount())
						{
							info("Aborting job after too many errors.");
							return false;
						}
					}
				}
				catch (Exception e)
				{
					error(e);
					if (descriptor.isLogRejects()) logRejects();
					if (errorCount>=descriptor.getMaxErrorCount())
					{
						info("Aborting job after too many errors.");
						return false;
					}
				}
				progressSupport.progress((int)(100*dataReader.getPosition()/fileLength), false);
			}
			if (!lines.isEmpty() && descriptor.isUseBatchMode())
			{
				statement.executeBatch();
				commit(connection);
			}
		}
		catch (Exception e)
		{
			error(e);
			if (descriptor.isLogRejects()) logRejects();
			if (errorCount>=descriptor.getMaxErrorCount()) return false;
		}
		finally
		{
			info(importedLines+" line(s) imported; "+rejectedLines+" line(s) rejected");
		}
		progressSupport.progress(100, false);
		return !progressSupport.isStoppedByUser();
	}

	private void commit(Connection connection) throws SQLException
	{
		if (descriptor.isCommitChanges())
		{
			logger.debug("Committing data");
			connection.commit();
			logger.debug("Committed data");
			importedLines+=lines.size();
			lines.clear();
		}
	}

	private int applyScript(String script, DataRow sourceRow) throws EvalError
	{
		if (script!=null)
		{
			logger.debug("Apply BSH script");
			if (interpreter==null)
			{
				adapter=new MyBeanShellAdapter();
				interpreter=new Interpreter();
			}
			adapter.reset();
			interpreter.set("bsh.system.shutdownOnExit", false);
			interpreter.set("dataload", adapter);
			interpreter.set("row", sourceRow);
			interpreter.eval(script);
			if (logger.isDebugEnabled()) logger.debug("Row Data: "+sourceRow);
			return adapter.getSeverity();
		}
		return ProgressMessage.INFO;
	}

	private void logRejects() throws FileNotFoundException
	{
		if (!lines.isEmpty())
		{
			rejectedLines+=lines.size();
			List lines=new ArrayList(this.lines);
			this.lines.clear();
			if (rejectWriter==null)
			{
				File file=new File(descriptor.getRejectFile());
				if (logger.isDebugEnabled()) logger.debug("Writing invalid records to file.");
				file.getParentFile().mkdirs();
				rejectWriter=new PrintWriter(new FileOutputStream(file));
				String titleLine=dataReader.getTitleLine();
				if (titleLine!=null) rejectWriter.println(titleLine);
			}
			for (Iterator it=lines.iterator(); it.hasNext();)
			{
				rejectWriter.println((String)it.next());
			}
			rejectWriter.flush();
		}
	}

	private String createSQLStatement(List targetColumns, String targetTable)
	{
		StringBuffer columnsSql=new StringBuffer();
		StringBuffer valuesSql=new StringBuffer();
		for (Iterator it=targetColumns.iterator(); it.hasNext();)
		{
			TargetColumnDescriptor columnDescriptor=(TargetColumnDescriptor)it.next();
			if (columnDescriptor.isIncluded())
			{
				if (columnsSql.length()>0) columnsSql.append(",");
				columnsSql.append(columnDescriptor.getName());
				if (valuesSql.length()>0) valuesSql.append(",");
				if (columnDescriptor.getSourceColumn()==null)
				{
					valuesSql.append(columnDescriptor.getSql());
					it.remove();
				}
				else
				{
					valuesSql.append("?");
				}
			}
			else it.remove();
		}
		StringBuffer sql=new StringBuffer("insert into ");
		sql.append(targetTable);
		sql.append(" (");
		sql.append(columnsSql);
		sql.append(") values (");
		sql.append(valuesSql);
		sql.append(")");
		return sql.toString();
	}

	public void dispose() throws IOException
	{
		interpreter=null;
		lines=null;
		progressSupport=null;
		if (dataReader!=null)
		{
			try
			{
				dataReader.close();
			}
			finally
			{
				dataReader=null;
			}
		}
		if (rejectWriter!=null)
		{
			try
			{
				rejectWriter.close();
			}
			finally
			{
				rejectWriter=null;
			}
		}
		if (logger!=null)
		{
			logger.removeAllAppenders();
			logger=null;
		}
	}

	private boolean initLogging()
	{
		Logger logger=Logger.getLogger(DataLoadJob.class);
		logger.setAdditivity(false);
		logger.setLevel(descriptor.getLogLevel());
		this.logger=logger;
		logger.addAppender(progressSupport.createAppender());
		if (descriptor.isLogToFile())
		{
			try
			{
				PatternLayout patternLayout=new PatternLayout("%d{ISO8601} %-5p [%c{1}]: %m%n");
				RollingFileAppender appender=new RollingFileAppender(patternLayout, descriptor.getLogFile());
				appender.setMaximumFileSize(1024*1024);
				appender.setMaxBackupIndex(10);
				appender.activateOptions();
				logger.addAppender(appender);
			}
			catch (Exception e)
			{
				fatal(e);
				return false;
			}
		}
		return true;
	}

	private boolean initRejectFile()
	{
		if (descriptor.isLogRejects())
		{
			try
			{
				if (StringUtils.isEmpty(descriptor.getRejectFile()))
				{
					fatal("No reject file specified.");
					return false;
				}
				File file=new File(descriptor.getRejectFile());
				if (!file.exists())
				{
					if (!file.createNewFile())
					{
						fatal("Can't create reject file '"+file.getAbsolutePath()+"'.");
						return false;
					}
				}
				if (!file.canWrite())
				{
					fatal("Can't write to reject file '"+file.getAbsolutePath()+"'.");
					return false;
				}
				info("Log invalid records to '"+file.getAbsolutePath()+"'.");
			}
			catch (IOException e)
			{
				fatal(e);
				return false;
			}
		}
		return true;
	}

	private void info(String message)
	{
		if (logger!=null) logger.info(message);
		else progressSupport.info(message);
	}

	private void warning(String message)
	{
		if (logger!=null) logger.warn(message);
		else progressSupport.warning(message);
	}

	private void error(String message)
	{
		errorCount++;
		if (logger!=null) logger.error(message);
		else progressSupport.error(message);
	}

	private void error(Throwable throwable)
	{
		errorCount++;
		if (logger!=null) logger.error(throwable.getMessage(), throwable);
		else progressSupport.error(throwable);
	}

	private void fatal(String message)
	{
		if (logger!=null) logger.fatal(message);
		else progressSupport.error(message);
	}

	private void fatal(Throwable throwable)
	{
		if (logger!=null) logger.fatal(throwable.getMessage(), throwable);
		else progressSupport.error(throwable);
	}

	private class MyBeanShellAdapter implements BeanShellAdapter
	{
		private int severity=ProgressMessage.INFO;

		public void reset()
		{
			severity=ProgressMessage.INFO;
		}

		public void info(String message)
		{
			DataLoadJob.this.info(message);
		}

		public void warning(String message)
		{
			severity=Math.max(ProgressMessage.WARNING, severity);
			DataLoadJob.this.warning(message);
		}

		public void error(String message)
		{
			severity=ProgressMessage.ERROR;
			DataLoadJob.this.error(message);
		}

		public int getSeverity()
		{
			return severity;
		}
	}

}
