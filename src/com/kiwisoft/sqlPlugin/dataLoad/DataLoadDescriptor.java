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

import java.util.*;
import java.io.IOException;

import org.apache.log4j.Level;

import com.kiwisoft.utils.xml.*;
import com.kiwisoft.utils.StringUtils;

/**
 * @author Stefan Stiller
 * @version $Revision: $, $Date: $
 */
public class DataLoadDescriptor extends XMLAdapter
{
	private FileDescriptor fileDescriptor;
	private List sourceColumns;
	private String targetTable;
	private List targetColumns;
	private String name;
	private Level logLevel;
	private boolean logToFile;
	private String logFile;
	private boolean logRejects;
	private String rejectFile;
	private boolean useBatchMode;
	private int batchSize;
	private int maxErrorCount;
	private boolean commitChanges;
	private String script;

	public DataLoadDescriptor(String name)
	{
		this.name=name;
	}

	public String toString()
	{
		return name;
	}

	public FileDescriptor getFileDescriptor()
	{
		return fileDescriptor;
	}

	public void setFileDescriptor(FileDescriptor fileDescriptor)
	{
		if (this.fileDescriptor!=null ? !this.fileDescriptor.equals(fileDescriptor) : fileDescriptor!=null)
		{
			this.fileDescriptor=fileDescriptor;
			sourceColumns=null;
		}
	}

	public List getSourceColumns()
	{
		return sourceColumns;
	}

	public List createSourceColumns(String[] titles)
	{
		sourceColumns=new ArrayList();
		for (int i=0; i<titles.length; i++)
		{
			sourceColumns.add(new SourceColumnDescriptor(titles[i]));
		}
		return sourceColumns;
	}

	public SourceColumnDescriptor getSourceColumn(String sourceColumnName)
	{
		if (StringUtils.isEmpty(sourceColumnName)) return null;
		for (Iterator it=sourceColumns.iterator(); it.hasNext();)
		{
			SourceColumnDescriptor descriptor=(SourceColumnDescriptor)it.next();
			if (sourceColumnName.equals(descriptor.getName())) return descriptor;
		}
		return null;
	}

	public List getTargetColumns()
	{
		return targetColumns;
	}

	public void setTargetColumns(List targetColumns)
	{
		this.targetColumns=new ArrayList(targetColumns);
	}

	public void setTargetTable(String targetTable)
	{
		this.targetTable=targetTable;
	}

	public String getTargetTable()
	{
		return targetTable;
	}

	public void writeXML(XMLWriter xml) throws IOException
	{
		xml.startElement("dataLoad");
		xml.setAttribute("name", name);
		xml.setAttribute("logLevel", logLevel!=null ? logLevel.toString() : null);
		xml.setAttribute("logToFile", logToFile);
		xml.setAttribute("logFile", logFile);
		xml.setAttribute("logRejects", logRejects);
		xml.setAttribute("rejectFile", rejectFile);
		xml.setAttribute("useBatchMode", useBatchMode);
		xml.setAttribute("batchSize", batchSize);
		xml.setAttribute("maxErrorCount", maxErrorCount);
		xml.setAttribute("commitChanges", commitChanges);
		if (fileDescriptor!=null) fileDescriptor.writeXML(xml);
		if (sourceColumns!=null)
		{
			for (Iterator it=sourceColumns.iterator(); it.hasNext();)
			{
				SourceColumnDescriptor descriptor=(SourceColumnDescriptor)it.next();
				descriptor.writeXML(xml);
			}
		}
		xml.addElement("script", script);
		xml.startElement("table");
		xml.setAttribute("name", targetTable);
		if (targetColumns!=null)
		{
			for (Iterator it=targetColumns.iterator(); it.hasNext();)
			{
				TargetColumnDescriptor descriptor=(TargetColumnDescriptor)it.next();
				descriptor.writeXML(xml);
			}
		}
		xml.closeElement("table");
		xml.closeElement("dataLoad");
	}

	public String getName()
	{
		return name;
	}

	public DataLoadDescriptor(XMLContext dummy, String aName)
	{
		super(dummy, aName);
	}

	public void setXMLAttribute(XMLContext context, String name, String value)
	{
		try
		{
			if ("name".equalsIgnoreCase(name)) this.name=value;
			else if ("logLevel".equalsIgnoreCase(name)) this.logLevel=Level.toLevel(value);
			else if ("logToFile".equalsIgnoreCase(name)) this.logToFile=Boolean.valueOf(value).booleanValue();
			else if ("logFile".equalsIgnoreCase(name)) this.logFile=value;
			else if ("logRejects".equalsIgnoreCase(name)) this.logRejects=Boolean.valueOf(value).booleanValue();
			else if ("rejectFile".equalsIgnoreCase(name)) this.rejectFile=value;
			else if ("useBatchMode".equalsIgnoreCase(name)) this.useBatchMode=Boolean.valueOf(value).booleanValue();
			else if ("batchSize".equalsIgnoreCase(name)) this.batchSize=Integer.parseInt(value);
			else if ("maxErrorCount".equalsIgnoreCase(name)) this.maxErrorCount=Integer.parseInt(value);
			else if ("commitChanges".equalsIgnoreCase(name)) this.commitChanges=Boolean.valueOf(value).booleanValue();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void addXMLElement(XMLContext context, XMLObject element)
	{
		if (element instanceof FileDescriptor)
		{
			fileDescriptor=(FileDescriptor)element;
		}
		else if (element instanceof SourceColumnDescriptor)
		{
			if (sourceColumns==null) sourceColumns=new ArrayList();
			sourceColumns.add(element);
		}
		else if (element instanceof TableXMLAdapter)
		{
			TableXMLAdapter xmlAdapter=(TableXMLAdapter)element;
			targetTable=xmlAdapter.getTableName();
			targetColumns=xmlAdapter.getColumns();
		}
		else if (element instanceof DefaultXMLObject)
		{
			DefaultXMLObject xmlObject=(DefaultXMLObject)element;
			if ("script".equalsIgnoreCase(xmlObject.getName())) script=xmlObject.getContent();
		}
	}

	public DataLoadDescriptor copy()
	{
		DataLoadDescriptor clone=new DataLoadDescriptor(getName()+" (2)");
		if (fileDescriptor!=null) clone.fileDescriptor=fileDescriptor.copy();
		Map sourceColumnsMap=new HashMap();
		if (sourceColumns!=null)
		{
			clone.sourceColumns=new ArrayList();
			for (Iterator it=sourceColumns.iterator(); it.hasNext();)
			{
				SourceColumnDescriptor descriptor=(SourceColumnDescriptor)it.next();
				SourceColumnDescriptor clonedDescriptor=descriptor.copy();
				clone.sourceColumns.add(clonedDescriptor);
				sourceColumnsMap.put(descriptor, clonedDescriptor);
			}
		}
		clone.targetTable=targetTable;
		if (targetColumns!=null)
		{
			clone.targetColumns=new ArrayList();
			for (Iterator it=targetColumns.iterator(); it.hasNext();)
			{
				clone.targetColumns.add(((TargetColumnDescriptor)it.next()).copy(sourceColumnsMap));
			}
		}
		clone.logLevel=logLevel;
		clone.logToFile=logToFile;
		clone.logFile=logFile;
		clone.logRejects=logRejects;
		clone.rejectFile=rejectFile;
		clone.useBatchMode=useBatchMode;
		clone.batchSize=batchSize;
		clone.maxErrorCount=maxErrorCount;
		clone.commitChanges=commitChanges;
		return clone;
	}

	public void setName(String name)
	{
		this.name=name;
	}

	public void setLogLevel(Level level)
	{
		this.logLevel=level;
	}

	public Level getLogLevel()
	{
		return logLevel!=null ? logLevel : Level.INFO;
	}

	public void setLogToFile(boolean logToFile)
	{
		this.logToFile=logToFile;
	}

	public void setLogFile(String logFile)
	{
		this.logFile=logFile;
	}

	public boolean isLogToFile()
	{
		return logToFile;
	}

	public String getLogFile()
	{
		return logFile;
	}

	public void setLogRejects(boolean logRejects)
	{
		this.logRejects=logRejects;
	}

	public void setRejectFile(String rejectFile)
	{
		this.rejectFile=rejectFile;
	}

	public void setUseBatchMode(boolean useBatchMode)
	{
		this.useBatchMode=useBatchMode;
	}

	public void setBatchSize(int batchSize)
	{
		this.batchSize=batchSize;
	}

	public void setMaxErrorCount(int maxErrorCount)
	{
		this.maxErrorCount=maxErrorCount;
	}

	public void setCommitChanges(boolean commitChanges)
	{
		this.commitChanges=commitChanges;
	}

	public boolean isLogRejects()
	{
		return logRejects;
	}

	public String getRejectFile()
	{
		return rejectFile;
	}

	public boolean isUseBatchMode()
	{
		return useBatchMode;
	}

	public int getBatchSize()
	{
		return Math.max(1, batchSize);
	}

	public int getMaxErrorCount()
	{
		return Math.max(1, maxErrorCount);
	}

	public boolean isCommitChanges()
	{
		return commitChanges;
	}

	public String getScript()
	{
		return script;
	}

	public void setScript(String script)
	{
		this.script=script;
	}

	public static class TableXMLAdapter extends XMLAdapter
	{
		private String tableName;
		private List columns=new ArrayList();

		public TableXMLAdapter(XMLContext dummy, String aName)
		{
			super(dummy, aName);
		}

		public void setXMLAttribute(XMLContext context, String name, String value)
		{
			if ("name".equalsIgnoreCase(name)) tableName=value;
		}

		public void addXMLElement(XMLContext context, XMLObject element)
		{
			if (element instanceof TargetColumnDescriptor) columns.add(element);
		}

		public String getTableName()
		{
			return tableName;
		}

		public List getColumns()
		{
			return columns;
		}
	}
}
